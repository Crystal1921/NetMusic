package com.github.tartaricacid.netmusic.network.message;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.tileentity.TileEntityAdvancedPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetPlayModeMessage(byte mode, boolean isPlay, BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetPlayModeMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NetMusic.MOD_ID, "set_play_message"));
    public static final StreamCodec<ByteBuf, SetPlayModeMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE,
            SetPlayModeMessage::mode,
            ByteBufCodecs.BOOL,
            SetPlayModeMessage::isPlay,
            net.minecraft.core.BlockPos.STREAM_CODEC,
            SetPlayModeMessage::blockPos,
            SetPlayModeMessage::new
    );


    public static void handle(SetPlayModeMessage message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level level = player.level();
            BlockEntity blockEntity = level.getBlockEntity(message.blockPos);
            if (blockEntity instanceof TileEntityAdvancedPlayer advancedPlayer) {
                advancedPlayer.setPlayMode(message.mode);
                advancedPlayer.setPlaying(message.isPlay);
            }
        });
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
