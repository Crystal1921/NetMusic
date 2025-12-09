package com.github.tartaricacid.netmusic.tileentity;

import com.github.tartaricacid.netmusic.block.BlockMusicListPlayer;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

import static com.github.tartaricacid.netmusic.block.BlockMusicListPlayer.CYCLE_DISABLE;

public class TileEntityMusicListPlayer extends AbstractMusicPlayer {
    private static final String CD_ITEM_TAG = "ItemStackCD";    public static final BlockEntityType<TileEntityMusicListPlayer> TYPE = BlockEntityType.Builder.of(TileEntityMusicListPlayer::new, InitBlocks.MUSIC_LIST_PLAYER.get()).build(null);
    private static final String IS_PLAY_TAG = "IsPlay";
    private static final String CURRENT_TIME_TAG = "CurrentTime";
    private static final String SIGNAL_TAG = "RedStoneSignal";
    private static final String CURRENT_SLOT_TAG = "CurrentSlot";
    private final ItemStackHandler playerInv = new MusicListInv(this);
    private boolean isPlay = false;
    private int currentTime;
    private boolean hasSignal = false;
    private int currentSlot = 0;
    public TileEntityMusicListPlayer(BlockPos blockPos, BlockState blockState) {
        super(TYPE, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TileEntityMusicListPlayer te) {
        te.tickTime();
        if (0 < te.getCurrentTime() && te.getCurrentTime() < 16 && te.getCurrentTime() % 5 == 0) {
            if (blockState.getValue(CYCLE_DISABLE)) {
                te.setPlay(false);
                te.markDirty();
            } else {
                // Find next non-empty slot
                int startSlot = te.getCurrentSlot();
                int slot = (startSlot + 1) % 27;
                do {
                    ItemStack stackInSlot = te.getPlayerInv().getStackInSlot(slot);
                    if (!stackInSlot.isEmpty()) {
                        ItemMusicCD.SongInfo songInfo = ItemMusicCD.getSongInfo(stackInSlot);
                        if (songInfo != null) {
                            te.setCurrentSlot(slot);
                            te.setPlayToClient(songInfo);
                            return;
                        }
                    }
                    slot = (slot + 1) % 27;
                } while (slot != (startSlot + 1) % 27);

                // If we get here, no valid songs found
                te.setPlay(false);
                te.markDirty();
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        getPersistentData().put(CD_ITEM_TAG, playerInv.serializeNBT(provider));
        getPersistentData().putBoolean(IS_PLAY_TAG, isPlay);
        getPersistentData().putInt(CURRENT_TIME_TAG, currentTime);
        getPersistentData().putBoolean(SIGNAL_TAG, hasSignal);
        getPersistentData().putInt(CURRENT_SLOT_TAG, currentSlot);
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

    public IItemHandler createHandler() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof BlockMusicListPlayer) {
            return this.playerInv;
        }
        return null;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
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

    public boolean hasSignal() {
        return hasSignal;
    }

    public void setSignal(boolean signal) {
        this.hasSignal = signal;
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


}
