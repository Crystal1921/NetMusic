package com.github.tartaricacid.netmusic.inventory;

import com.github.tartaricacid.netmusic.init.InitItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MusicListPlayerMenu extends AbstractContainerMenu {
    public static final MenuType<MusicListPlayerMenu> TYPE = IMenuTypeExtension.create((windowId, inv, data) -> {
        IItemHandler handler = new net.neoforged.neoforge.items.ItemStackHandler(27);
        return new MusicListPlayerMenu(windowId, inv, handler, ContainerLevelAccess.NULL);
    });
    
    private final IItemHandler itemHandler;
    private final ContainerLevelAccess access;

    public MusicListPlayerMenu(int id, Inventory playerInventory, IItemHandler itemHandler, ContainerLevelAccess access) {
        super(TYPE, id);
        this.itemHandler = itemHandler;
        this.access = access;

        // Add 27 slots for music CDs (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(itemHandler, col + row * 9, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.getItem() == InitItems.MUSIC_CD.get();
                    }
                });
            }
        }

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar (1 row of 9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            
            // If clicking on container slots (0-26)
            if (index < 27) {
                // Try to move to player inventory
                if (!this.moveItemStackTo(slotItem, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } 
            // If clicking on player inventory
            else {
                // Try to move to container slots
                if (slotItem.getItem() == InitItems.MUSIC_CD.get()) {
                    if (!this.moveItemStackTo(slotItem, 0, 27, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((level, pos) -> {
            return level.getBlockState(pos).getBlock() instanceof com.github.tartaricacid.netmusic.block.BlockMusicListPlayer
                    && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }, true);
    }
}
