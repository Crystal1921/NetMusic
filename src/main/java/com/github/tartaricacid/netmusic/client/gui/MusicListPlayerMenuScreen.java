package com.github.tartaricacid.netmusic.client.gui;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.inventory.MusicListPlayerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.anti_ad.mc.ipn.api.IPNIgnore;

@IPNIgnore
public class MusicListPlayerMenuScreen extends AbstractContainerScreen<MusicListPlayerMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(NetMusic.MOD_ID, "textures/gui/music_list_player.png");

    public MusicListPlayerMenuScreen(MusicListPlayerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(BG, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
