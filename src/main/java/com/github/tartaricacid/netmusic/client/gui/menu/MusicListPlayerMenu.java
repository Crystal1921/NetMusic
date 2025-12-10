package com.github.tartaricacid.netmusic.client.gui.menu;

import com.github.tartaricacid.netmusic.init.InitItems;
import com.github.tartaricacid.netmusic.tileentity.TileEntityMusicListPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MusicListPlayerMenu extends AbstractContainerMenu {
    public static final MenuType<MusicListPlayerMenu> TYPE = IMenuTypeExtension.create(MusicListPlayerMenu::new);
    
    private final IItemHandler itemHandler;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public MusicListPlayerMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        super(TYPE, id);
        TileEntityMusicListPlayer musicListPlayer = (TileEntityMusicListPlayer) playerInventory.player.level().getBlockEntity(buf.readBlockPos());
        this.itemHandler = musicListPlayer.getPlayerInv();
        this.access = ContainerLevelAccess.NULL;
        this.data = musicListPlayer.dataAccess;
        this.addDataSlots(this.data);

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

    public MusicListPlayerMenu(int id, Inventory playerInventory, IItemHandler itemHandler, BlockPos blockPos, ContainerData data) {
        super(TYPE, id);
        this.itemHandler = itemHandler;
        this.access = ContainerLevelAccess.NULL;
        this.data = data;
        this.addDataSlots(this.data);

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
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case 0 -> {
                int currentSlot = this.data.get(0);
                currentSlot = (currentSlot - 1 + 27) % 27;
                this.data.set(0, currentSlot);
            }

            case 1 -> {
                int currentSlot = this.data.get(0);
                currentSlot = (currentSlot + 1) % 27;
                this.data.set(0, currentSlot);
            }
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot.hasItem()) {
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
        return this.access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof com.github.tartaricacid.netmusic.block.BlockMusicListPlayer
                && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    public ContainerData getData() {
        return data;
    }
}
