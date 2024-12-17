package com.github.tartaricacid.netmusic.client.init;

import com.github.tartaricacid.netmusic.inventory.AdvancedPlayerMenu;
import com.github.tartaricacid.netmusic.client.gui.AdvancedPlayerScreen;
import com.github.tartaricacid.netmusic.client.gui.CDBurnerMenuScreen;
import com.github.tartaricacid.netmusic.client.gui.ComputerMenuScreen;
import com.github.tartaricacid.netmusic.compat.tlm.init.CompatRegistry;
import com.github.tartaricacid.netmusic.inventory.CDBurnerMenu;
import com.github.tartaricacid.netmusic.inventory.ComputerMenu;
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
        event.register(AdvancedPlayerMenu.TYPE, AdvancedPlayerScreen::new);
        CompatRegistry.initContainerScreen(event);
    }
}
