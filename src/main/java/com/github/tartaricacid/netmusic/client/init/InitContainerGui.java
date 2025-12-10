package com.github.tartaricacid.netmusic.client.init;

import com.github.tartaricacid.netmusic.client.gui.screen.CDBurnerMenuScreen;
import com.github.tartaricacid.netmusic.client.gui.screen.ComputerMenuScreen;
import com.github.tartaricacid.netmusic.client.gui.screen.MusicListPlayerMenuScreen;
import com.github.tartaricacid.netmusic.compat.tlm.init.CompatRegistry;
import com.github.tartaricacid.netmusic.client.gui.menu.CDBurnerMenu;
import com.github.tartaricacid.netmusic.client.gui.menu.ComputerMenu;
import com.github.tartaricacid.netmusic.client.gui.menu.MusicListPlayerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class InitContainerGui {
    @SubscribeEvent
    public static void clientSetup(RegisterMenuScreensEvent event) {
        event.register(CDBurnerMenu.TYPE, CDBurnerMenuScreen::new);
        event.register(ComputerMenu.TYPE, ComputerMenuScreen::new);
        event.register(MusicListPlayerMenu.TYPE, MusicListPlayerMenuScreen::new);
        CompatRegistry.initContainerScreen(event);
    }
}
