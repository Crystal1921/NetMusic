package com.github.tartaricacid.netmusic.client.gui;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.client.config.MusicListManage;
import com.github.tartaricacid.netmusic.inventory.CDBurnerMenu;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import com.github.tartaricacid.netmusic.network.NetworkHandler;
import com.github.tartaricacid.netmusic.network.message.SetMusicIDMessage;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CDBurnerMenuScreen extends AbstractContainerScreen<CDBurnerMenu> {
    private MODE mode = MODE.NET;
    private static final ImmutableList<MODE> MODE_IMMUTABLE_LIST = ImmutableList.copyOf(MODE.values());
    private static final ResourceLocation BG = new ResourceLocation(NetMusic.MOD_ID, "textures/gui/cd_burner.png");
    private static final Pattern ID_REG = Pattern.compile("^\\d{4,}$");
    private static final Pattern URL_1_REG = Pattern.compile("^https://music\\.163\\.com/song\\?id=(\\d+).*$");
    private static final Pattern URL_2_REG = Pattern.compile("^https://music\\.163\\.com/#/song\\?id=(\\d+).*$");
    private EditBox textField;
    private EditBox timeField;
    private Component tips = Component.empty();

    public CDBurnerMenuScreen(CDBurnerMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();

        String perText = "";
        boolean focus = false;
        if (textField != null) {
            perText = textField.getValue();
            focus = textField.isFocused();
        }
        textField = new EditBox(getMinecraft().font, leftPos + 12, topPos + 18, 132, 16, Component.literal("Music ID Box")) {
            @Override
            public void insertText(String text) {
                Matcher matcher1 = URL_1_REG.matcher(text);
                if (matcher1.find()) {
                    String group = matcher1.group(1);
                    super.insertText(group);
                    return;
                }

                Matcher matcher2 = URL_2_REG.matcher(text);
                if (matcher2.find()) {
                    String group = matcher2.group(1);
                    super.insertText(group);
                    return;
                }

                super.insertText(text);
            }
        };
        textField.setValue(perText);
        textField.setBordered(false);
        textField.setMaxLength(19);
        textField.setTextColor(0xF3EFE0);
        textField.setFocused(focus);
        textField.moveCursorToEnd();
        this.addWidget(this.textField);

        timeField = new EditBox(getMinecraft().font, leftPos + 12, topPos + 56, 50, 16, Component.literal("Music ID Box"));
        timeField.setTextColor(0xF3EFE0);
        timeField.setVisible(false);
        this.addRenderableWidget(this.timeField);


        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.cd_burner.craft"), (b) -> handleCraftButton())
                .pos(leftPos + 7, topPos + 33).size(135, 18).build());
        this.addRenderableWidget(CycleButton.<MODE>builder((serializedName) -> Component.translatable("gui.netmusic.cd_burner." + serializedName.getSerializedName()))
                .withValues(MODE_IMMUTABLE_LIST)
                .displayOnlyValue()
                .withInitialValue(this.mode)
                .create(leftPos + 90, topPos + 55, 50, 20, Component.literal("MODE"), (colorModeCycleButton, updatedMode) -> this.updateMode(updatedMode)));
    }

    private void updateMode(MODE mode) {
        this.mode = mode;
<<<<<<< HEAD
        this.textField.setValue("");
        switch (mode){
            case LOCAL -> {
                this.timeField.setVisible(true);
                this.textField.setMaxLength(65536);
            }
            case NET -> {
                this.timeField.setVisible(false);
                this.textField.setMaxLength(19);
            }
=======
        if (mode == MODE.LOCAL) {
            this.textField.setMaxLength(65536);
        }else {
            this.textField.setMaxLength(19);
>>>>>>> 9a52d9a4cc48f9f11166f85833af81f3e2d35676
        }
    }

    private void handleCraftButton() {
        switch (mode){
            case NET -> {
                {
                    if (Util.isBlank(textField.getValue())) {
                        this.tips = Component.translatable("gui.netmusic.cd_burner.no_music_id");
                        return;
                    }
                    if (ID_REG.matcher(textField.getValue()).matches()) {
                        long id = Long.parseLong(textField.getValue());
                        try {
                            ItemMusicCD.SongInfo song = MusicListManage.get163Song(id);
                            NetworkHandler.CHANNEL.sendToServer(new SetMusicIDMessage(song));
                        } catch (Exception e) {
                            this.tips = Component.translatable("gui.netmusic.cd_burner.get_info_error");
                            e.printStackTrace();
                        }
                    } else {
                        this.tips = Component.translatable("gui.netmusic.cd_burner.music_id_error");
                    }
                }
            }
            case LOCAL -> {
<<<<<<< HEAD
                String string = textField.getValue();
                int time;
                try{
                    time = Integer.parseInt(timeField.getValue());
                }catch (NumberFormatException e) {
                    time = 583;
                }
                try {
                    Path path = Paths.get(string);
                    if (path.isAbsolute()) {
                        ItemMusicCD.SongInfo songInfo = new ItemMusicCD.SongInfo(string,time);
                        NetworkHandler.CHANNEL.sendToServer(new SetMusicIDMessage(songInfo));
                        this.tips = Component.empty();
                    } else {
                        this.tips = Component.translatable("gui.netmusic.cd_burner.file_error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
=======
                String string = textField.getValue().toString();
                ItemMusicCD.SongInfo songInfo = new ItemMusicCD.SongInfo(string);
                NetworkHandler.CHANNEL.sendToServer(new SetMusicIDMessage(songInfo));
>>>>>>> 9a52d9a4cc48f9f11166f85833af81f3e2d35676
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        renderBackground(graphics);
        int posX = this.leftPos;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(BG, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        super.render(graphics, x, y, partialTicks);
        textField.render(graphics, x, y, partialTicks);
        if (Util.isBlank(textField.getValue()) && !textField.isFocused()) {
            graphics.drawString(font, Component.translatable("gui.netmusic.cd_burner.id.tips").withStyle(ChatFormatting.ITALIC), this.leftPos + 12, this.topPos + 18, ChatFormatting.GRAY.getColor(), false);
        }
        graphics.drawWordWrap(font, tips, this.leftPos + 8, this.topPos + 55, 135, 0xCF0000);
        renderTooltip(graphics, x, y);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String value = this.textField.getValue();
        super.resize(minecraft, width, height);
        this.textField.setValue(value);
    }

    @Override
    protected void containerTick() {
        this.textField.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.textField.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.textField);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void insertText(String text, boolean overwrite) {
        if (overwrite) {
            this.textField.setValue(text);
        } else {
            this.textField.insertText(text);
        }
    }

    public enum MODE implements StringRepresentable {
        NET("net"),
        LOCAL("local");

        private final String name;
        private final Component displayName;
        private MODE(String pName) {
            this.name = pName;
            this.displayName = Component.translatable("gui.netmusic.cd_burner." + pName);
        }
        @Override
        public String getSerializedName() {
            return this.name;
        }
        public Component getDisplayName() {
            return this.displayName;
        }
    }
}
