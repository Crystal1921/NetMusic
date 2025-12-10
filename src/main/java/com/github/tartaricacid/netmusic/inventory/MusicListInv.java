package com.github.tartaricacid.netmusic.inventory;

import com.github.tartaricacid.netmusic.init.InitItems;
import com.github.tartaricacid.netmusic.tileentity.TileEntityMusicListPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class MusicListInv extends ItemStackHandler {

    public MusicListInv(TileEntityMusicListPlayer te) {
        super(27);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() == InitItems.MUSIC_CD.get();
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return 1;
    }
}
