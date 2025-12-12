package com.github.tartaricacid.netmusic.tileentity;

import com.github.tartaricacid.netmusic.init.InitBlocks;
import com.github.tartaricacid.netmusic.inventory.MusicListInv;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import com.github.tartaricacid.netmusic.network.NetworkHandler;
import com.github.tartaricacid.netmusic.network.message.MusicToClientMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileEntityMusicListPlayer extends AbstractMusicPlayer {
    private static final String CD_ITEM_TAG = "ItemStackCD";
    private static final String IS_PLAY_TAG = "IsPlay";
    private static final String CURRENT_TIME_TAG = "CurrentTime";
    private static final String SIGNAL_TAG = "RedStoneSignal";    public static final BlockEntityType<TileEntityMusicListPlayer> TYPE = BlockEntityType.Builder.of(TileEntityMusicListPlayer::new, InitBlocks.MUSIC_LIST_PLAYER.get()).build(null);
    private static final String CURRENT_SLOT_TAG = "CurrentSlot";
    private static final String CYCLE_MODE_TAG = "CycleMode";
    public final RandomSource randomSource = RandomSource.create();
    private final ItemStackHandler playerInv = new MusicListInv(this);
    private final CycleMode[] cycleModes = CycleMode.values();
    private boolean isPlay = false;
    private int currentTime;
    private boolean hasSignal = false;
    private int currentSlot = 0;
    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return TileEntityMusicListPlayer.this.currentSlot;
        }

        @Override
        public void set(int index, int value) {
            TileEntityMusicListPlayer.this.currentSlot = value;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    private CycleMode cycleMode = CycleMode.SINGLE;
    public TileEntityMusicListPlayer(BlockPos blockPos, BlockState blockState) {
        super(TYPE, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TileEntityMusicListPlayer te) {
        te.tickTime();
        handleCycle(te);
    }

    private static void handleCycle(TileEntityMusicListPlayer te) {
        // only run selection at specific ticks as in the original snippet
        if (0 < te.getCurrentTime() && te.getCurrentTime() < 16 && te.getCurrentTime() % 5 == 0) {
            switch (te.cycleMode) {
                case LIST -> {
                    // Find next non-empty slot
                    int startSlot = te.getCurrentSlot();
                    int slot = (startSlot + 1) % 27;
                    do {
                        if (playAvailableSong(te, slot)) return;
                        slot = (slot + 1) % 27;
                    } while (slot != (startSlot + 1) % 27);

                    // If we get here, no valid songs found
                    te.setPlay(false);
                    te.markDirty();
                }

                case RANDOM -> {
                    // Collect all valid song slots, then pick one at random.
                    List<Integer> validSlots = new ArrayList<>();
                    for (int i = 0; i < 27; i++) {
                        ItemStack stack = te.getPlayerInv().getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            ItemMusicCD.SongInfo info = ItemMusicCD.getSongInfo(stack);
                            if (info != null) validSlots.add(i);
                        }
                    }

                    if (validSlots.isEmpty()) {
                        // nothing to play
                        te.setPlay(false);
                        te.markDirty();
                        return;
                    }

                    // Choose random slot. If there are multiple slots, try to avoid repeating the same slot immediately.
                    int chosenSlot = getChosenSlot(te, validSlots);
                    ItemMusicCD.SongInfo chosenSong = ItemMusicCD.getSongInfo(te.getPlayerInv().getStackInSlot(chosenSlot));
                    if (chosenSong != null) {
                        te.dataAccess.set(0, chosenSlot);
                        te.setCurrentSlot(chosenSlot);
                        te.setPlayToClient(chosenSong);
                    } else {
                        // Extremely unlikely, but fallback to stopping if something changed
                        te.setPlay(false);
                        te.markDirty();
                    }
                }

                case SINGLE -> {
                    // In SINGLE mode we attempt to play the current slot repeatedly.
                    int current = te.getCurrentSlot();
                    if (current >= 0 && current < 27) {
                        ItemStack stack = te.getPlayerInv().getStackInSlot(current);
                        if (!stack.isEmpty()) {
                            ItemMusicCD.SongInfo songInfo = ItemMusicCD.getSongInfo(stack);
                            if (songInfo != null) {
                                // keep the same slot; send song to client (restarts/continues playback)
                                te.dataAccess.set(0, current);
                                te.setPlayToClient(songInfo);
                                return;
                            }
                        }
                    }

                    // If current slot is invalid, try to find any valid slot (first found) and use it as the single slot.
                    for (int i = 0; i < 27; i++) {
                        if (playAvailableSong(te, i)) return;
                    }

                    // No valid song found -> stop
                    te.setPlay(false);
                    te.markDirty();
                }

                case DISABLE -> {
                    // Playback disabled: ensure stopped and persist the change
                    te.setPlay(false);
                    te.markDirty();
                }

                default -> {
                    // Unknown mode: be safe and stop playback
                    te.setPlay(false);
                    te.markDirty();
                }
            }
        }
    }

    private static boolean playAvailableSong(TileEntityMusicListPlayer te, int i) {
        ItemStack stack = te.getPlayerInv().getStackInSlot(i);
        if (!stack.isEmpty()) {
            ItemMusicCD.SongInfo info = ItemMusicCD.getSongInfo(stack);
            if (info != null) {
                te.dataAccess.set(0, i);
                te.setCurrentSlot(i);
                te.setPlayToClient(info);
                return true;
            }
        }
        return false;
    }

    private static int getChosenSlot(TileEntityMusicListPlayer te, List<Integer> validSlots) {
        RandomSource rnd = te.randomSource;
        int chosenIndex = rnd.nextInt(validSlots.size());
        if (validSlots.size() > 1) {
            int current = te.getCurrentSlot();
            // if randomly chosen equals current, try one more time to pick a different index (best-effort)
            if (validSlots.get(chosenIndex) == current) {
                int alt = rnd.nextInt(validSlots.size() - 1);
                // map alt to the list skipping the position of current
                if (alt >= chosenIndex) alt++;
                chosenIndex = alt;
            }
        }

        return validSlots.get(chosenIndex);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        getPersistentData().put(CD_ITEM_TAG, playerInv.serializeNBT(provider));
        getPersistentData().putBoolean(IS_PLAY_TAG, isPlay);
        getPersistentData().putInt(CURRENT_TIME_TAG, currentTime);
        getPersistentData().putBoolean(SIGNAL_TAG, hasSignal);
        getPersistentData().putInt(CURRENT_SLOT_TAG, currentSlot);
        getPersistentData().putByte(CYCLE_MODE_TAG, (byte) cycleMode.ordinal());
        super.saveAdditional(compound, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        playerInv.deserializeNBT(provider, getPersistentData().getCompound(CD_ITEM_TAG));
        isPlay = getPersistentData().getBoolean(IS_PLAY_TAG);
        currentTime = getPersistentData().getInt(CURRENT_TIME_TAG);
        hasSignal = getPersistentData().getBoolean(SIGNAL_TAG);
        currentSlot = getPersistentData().getInt(CURRENT_SLOT_TAG);
        cycleMode = cycleModes[getPersistentData().getByte(CYCLE_MODE_TAG)];
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStackHandler getPlayerInv() {
        return playerInv;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public CycleMode getCycleMode() {
        return cycleMode;
    }

    public void setCycleMode(CycleMode cycleMode) {
        this.cycleMode = cycleMode;
    }

    public void setPlayToClient(ItemMusicCD.SongInfo info) {
        this.setCurrentTime(info.songTime * 20 + 64);
        this.isPlay = true;
        if (level != null && !level.isClientSide) {
            MusicToClientMessage msg = new MusicToClientMessage(worldPosition, info.songUrl, info.songTime, info.songName);
            NetworkHandler.sendToNearby(level, worldPosition, msg);
        }
    }

    public void markDirty() {
        this.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(int slot) {
        this.currentSlot = slot;
    }

    public void tickTime() {
        if (currentTime > 0) {
            currentTime--;
        }
    }

    public enum CycleMode {
        SINGLE,
        LIST,
        RANDOM,
        DISABLE
    }




}
