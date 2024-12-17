package com.github.tartaricacid.netmusic.tileentity;

import com.github.tartaricacid.netmusic.client.gui.AdvancedPlayerScreen;
import com.github.tartaricacid.netmusic.init.InitBlocks;
import com.github.tartaricacid.netmusic.inventory.AdvancedPlayerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEntityAdvancedPlayer extends RandomizableContainerBlockEntity {
    public static final BlockEntityType<TileEntityAdvancedPlayer> TYPE = BlockEntityType.Builder.of(TileEntityAdvancedPlayer::new, InitBlocks.ADVANCED_MUSIC_PLAYER.get()).build(null);
    NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    public final AdvancedPlayerScreen.PlayMode[] playModes = AdvancedPlayerScreen.PlayMode.values();
    private final String PLAY_MODE = "play_mode";
    private final String IS_PLAYING = "is_playing";
    private byte playMode;
    private boolean isPlaying;

    public TileEntityAdvancedPlayer(BlockPos pPos, BlockState pBlockState) {
        super(TYPE, pPos, pBlockState);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries);
        this.playMode = getPersistentData().getByte(PLAY_MODE);
        this.isPlaying = getPersistentData().getBoolean(IS_PLAYING);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        ContainerHelper.saveAllItems(pTag, this.items, pRegistries);
        getPersistentData().putByte(PLAY_MODE, this.playMode);
        getPersistentData().putBoolean(IS_PLAYING, this.isPlaying);
        super.saveAdditional(pTag, pRegistries);
    }

    public AdvancedPlayerScreen.PlayMode getPlayMode() {
        return playModes[playMode];
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setPlayMode(byte pPlayMode) {
        this.playMode = pPlayMode;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.netmusic.advanced_player");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItems) {

    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new AdvancedPlayerMenu(pContainerId,pInventory,this,this);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

}
