package com.github.tartaricacid.netmusic.client.gui;

import com.github.tartaricacid.netmusic.inventory.MusicListPlayerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.anti_ad.mc.ipn.api.IPNIgnore;
import org.jetbrains.annotations.NotNull;

@IPNIgnore
public class MusicListPlayerMenuScreen extends AbstractContainerScreen<MusicListPlayerMenu> {
    /**
     * The ResourceLocation containing the chest GUI texture.
     */
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public MusicListPlayerMenuScreen(MusicListPlayerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int containerRows = 3;
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, containerRows * 18 + 17);
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j + containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
