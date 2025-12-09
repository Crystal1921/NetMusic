package com.github.tartaricacid.netmusic.init;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class InitDataComponent {
    @SuppressWarnings("removal")
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(NetMusic.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemMusicCD.SongInfo>> SONG_INFO = DATA_COMPONENTS
            .register("song_info", () -> DataComponentType.<ItemMusicCD.SongInfo>builder().persistent(ItemMusicCD.SongInfo.CODEC).networkSynchronized(ItemMusicCD.SongInfo.STREAM_CODEC).build());

}
