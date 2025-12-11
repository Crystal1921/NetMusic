package com.github.tartaricacid.netmusic.client.gui.screen;

import com.github.tartaricacid.netmusic.client.gui.menu.MusicListPlayerMenu;
import com.github.tartaricacid.netmusic.tileentity.TileEntityMusicListPlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.anti_ad.mc.ipn.api.IPNIgnore;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@IPNIgnore
public class MusicListPlayerMenuScreen extends AbstractContainerScreen<MusicListPlayerMenu> {
    /**
     * The ResourceLocation containing the chest GUI texture.
     */
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int BUTTON_WIDTH = 15;
    private static final int BUTTON_HEIGHT = 20;

    public MusicListPlayerMenuScreen(MusicListPlayerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int startX = this.width / 2 - 90;
        int startY = j - 20;
        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> clickButton(0))
                .size(15, 20).pos(startX , startY)
                .tooltip(Tooltip.create(Component.translatable("gui.netmusic.maid.music_player_backpack.previous")))
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> clickButton(1))
                .size(15, 20).pos(startX  + BUTTON_WIDTH, startY)
                .tooltip(Tooltip.create(Component.translatable("gui.netmusic.maid.music_player_backpack.next")))
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.maid.music_player_backpack.stop"), b -> this.clickButton(2))
                .size(BUTTON_WIDTH * 2, 20).pos(startX  + BUTTON_WIDTH * 2, startY).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.maid.music_player_backpack.play"), b -> this.clickButton(3))
                .size(BUTTON_WIDTH * 2, 20).pos(startX  + BUTTON_WIDTH * 4, startY).build());

        this.addRenderableWidget(CycleButton.<TileEntityMusicListPlayer.CycleMode>builder((mode) ->Component.translatable("gui.netmusic.list_mode." + mode.name().toLowerCase()))
                .withValues(TileEntityMusicListPlayer.CycleMode.values())
                .withInitialValue(this.menu.getTileEntity().getCycleMode())
                .create(startX  + BUTTON_WIDTH * 6, startY, 80, 20, Component.translatable("gui.netmusic.list_mode")
                , (button, mode) -> {
                            this.clickButton(4 + mode.ordinal());
                        }));
    }

    private void clickButton(int id) {
        if (this.getMinecraft().gameMode != null) {
            this.getMinecraft().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int containerRows = 3;
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, containerRows * 18 + 17);
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j + containerRows * 18 + 17, 0, 126, this.imageWidth, 96);

        int index = this.menu.getData().get(0);
        drawSlotBorder(guiGraphics, index, this.leftPos, this.topPos, Color.RED.getRGB(), 2);

    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    /**
     * 为指定的音乐 CD 槽（按 index，从 0 开始）画边框。
     *
     * @param guiGraphics GuiGraphics 实例（在 render 方法中可用）
     * @param index       要标记的格子索引（0..26）
     * @param leftPos     GUI 左上角的 x（通常是 this.leftPos 或 gui 左偏移）
     * @param topPos      GUI 左上角的 y（通常是 this.topPos 或 gui 上偏移）
     * @param color       ARGB 整数颜色，例如 0xFFFF0000 为不透明红色
     * @param thickness   边框厚度（像素），建议 1 或 2
     */
    public static void drawSlotBorder(GuiGraphics guiGraphics, int index, int leftPos, int topPos, int color, int thickness) {
        if (index < 0 || index >= 27) return; // 防御性判断（你有 27 个格子）
        if (thickness <= 0) thickness = 1;

        final int columns = 9;
        final int slotSize = 18; // 每格宽高（含间隔）
        final int slotOffsetX = 7;  // 添加槽时的 x 偏移
        final int slotOffsetY = 17; // 添加槽时的 y 偏移

        int col = index % columns;
        int row = index / columns;

        // 格子实际左上角像素坐标（相对于屏幕）
        int x = leftPos + slotOffsetX + col * slotSize;
        int y = topPos + slotOffsetY + row * slotSize;

        // 四条边：上、下、左、右（每条用 fill 画一个矩形）
        // 上边
        guiGraphics.fill(x, y, x + slotSize, y + thickness, color);
        // 下边
        guiGraphics.fill(x, y + slotSize - thickness, x + slotSize, y + slotSize, color);
        // 左边
        guiGraphics.fill(x, y, x + thickness, y + slotSize, color);
        // 右边
        guiGraphics.fill(x + slotSize - thickness, y, x + slotSize, y + slotSize, color);
    }
}
