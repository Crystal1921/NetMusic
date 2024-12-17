package com.github.tartaricacid.netmusic.client.gui;

import com.github.tartaricacid.netmusic.inventory.AdvancedPlayerMenu;
import com.github.tartaricacid.netmusic.network.message.SetPlayModeMessage;
import com.github.tartaricacid.netmusic.tileentity.TileEntityAdvancedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class AdvancedPlayerScreen extends AbstractContainerScreen<AdvancedPlayerMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final TileEntityAdvancedPlayer advancedPlayer;
    private CycleButton<PlayMode> modeButton;
    private CycleButton<Boolean> playButton;
    private PlayMode playMode;
    private boolean isPlaying;

    public AdvancedPlayerScreen(AdvancedPlayerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.advancedPlayer = pMenu.getAdvancedPlayer();
        this.playMode = advancedPlayer.getPlayMode();
        this.isPlaying = advancedPlayer.getIsPlaying();
    }

    @Override
    protected void init() {
        super.init();
        this.modeButton = this.addRenderableWidget(
                CycleButton.<PlayMode>builder(mode -> switch (mode) {
                            case SINGLE_RECYCLE ->
                                    Component.translatable("gui.netmusic.advanced_player." + PlayMode.SINGLE_RECYCLE.name().toLowerCase());
                            case LIST_RECYCLE ->
                                    Component.translatable("gui.netmusic.advanced_player." + PlayMode.LIST_RECYCLE.name().toLowerCase());
                            case RANDOM ->
                                    Component.translatable("gui.netmusic.advanced_player." + PlayMode.RANDOM.name().toLowerCase());
                        })
                        .withValues(PlayMode.values())
                        .displayOnlyValue()
                        .withInitialValue(this.playMode)
                        .create(this.width / 2 - 50 - 100 - 4, 165, 50, 20, Component.translatable("gui.netmusic.advanced_player.mode"), (cycleButton, playMode) -> this.playMode = playMode)
        );
        this.playButton = this.addRenderableWidget(
                CycleButton.booleanBuilder(Component.translatable("gui.netmusic.advanced_player.on"), Component.translatable("gui.netmusic.advanced_player.off"))
                        .displayOnlyValue()
                        .withInitialValue(this.isPlaying)
                        .create(this.width / 2 - 50 - 100 - 4, 195, 50, 20, Component.translatable("gui.netmusic.advanced_player.is_playing"), (cycleButton, aBoolean) -> this.isPlaying = aBoolean)
        );
        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.advanced_player.confirm"), (button) ->
                this.save()).bounds(this.width / 2 - 50 - 100 - 4, 225, 50, 15).build());
    }

    private void save() {
        advancedPlayer.setPlayMode((byte) playMode.ordinal());
        advancedPlayer.setPlaying(isPlaying);
        PacketDistributor.sendToServer(new SetPlayModeMessage((byte) playMode.ordinal(),isPlaying,advancedPlayer.getBlockPos()));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int containerRows = 3;
        pGuiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, containerRows * 18 + 17);
        pGuiGraphics.blit(CONTAINER_BACKGROUND, i, j + containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
    }

    public enum PlayMode {
        SINGLE_RECYCLE,
        LIST_RECYCLE,
        RANDOM
    }
}
