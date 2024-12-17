package com.github.tartaricacid.netmusic.inventory;

import com.github.tartaricacid.netmusic.init.InitBlocks;
import com.github.tartaricacid.netmusic.init.InitItems;
import com.github.tartaricacid.netmusic.tileentity.TileEntityAdvancedPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.jetbrains.annotations.NotNull;

public class AdvancedPlayerMenu extends AbstractContainerMenu {
    private static final int INV_SIZE = 36;
    private final ContainerLevelAccess access;
    private final TileEntityAdvancedPlayer advancedPlayer;
    public static final MenuType<AdvancedPlayerMenu> TYPE = IMenuTypeExtension.create(AdvancedPlayerMenu::new);

    public AdvancedPlayerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, new SimpleContainer(27), (TileEntityAdvancedPlayer)inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public AdvancedPlayerMenu(int id, Inventory inventory, Container container, TileEntityAdvancedPlayer advancedPlayer) {
        super(TYPE, id);
        this.access = ContainerLevelAccess.NULL;
        this.advancedPlayer = advancedPlayer;
        int containerRows = 3;
        int i = (containerRows - 4) * 18;

        for (int j = 0; j < containerRows; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(container, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public int getMaxStackSize(@NotNull ItemStack pStack) {
                        return 1;
                    }

                    @Override
                    public boolean mayPlace(@NotNull ItemStack pStack) {
                        return pStack.is(InitItems.MUSIC_CD);
                    }
                });
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(inventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        int INV_START = 27;

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (index < INV_START) {
                if (!this.moveItemStackTo(slotStack, INV_START, INV_SIZE + INV_START, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemStack);
            } else {
                if (index < INV_SIZE + INV_START && !this.moveItemStackTo(slotStack, 0, INV_START, false))
                    return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, slotStack);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, InitBlocks.ADVANCED_MUSIC_PLAYER.get());
    }

    public TileEntityAdvancedPlayer getAdvancedPlayer() {
        return advancedPlayer;
    }
}
