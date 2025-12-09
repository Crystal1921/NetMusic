package com.github.tartaricacid.netmusic.tileentity;

import com.github.tartaricacid.netmusic.api.lyric.LyricRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMusicPlayer extends BlockEntity {
    /**
     * 仅客户端使用，记录当前音乐的歌词信息，用于渲染歌词
     */
    public LyricRecord lyricRecord = null;

    public AbstractMusicPlayer(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract boolean isPlay();
}
