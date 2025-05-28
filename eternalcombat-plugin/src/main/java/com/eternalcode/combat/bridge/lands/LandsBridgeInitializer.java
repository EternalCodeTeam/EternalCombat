package com.eternalcode.combat.bridge.lands;

import com.eternalcode.combat.bridge.BridgeInitializer;
import com.eternalcode.combat.event.EventManager;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.region.RegionProvider;
import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.plugin.Plugin;

public class LandsBridgeInitializer implements BridgeInitializer {

    private final Plugin plugin;
    private final EventManager eventManager;
    private final FightManager fightManager;
    private final NoticeService noticeService;

    private RegionProvider regionProvider;

    public LandsBridgeInitializer(
        Plugin plugin,
        EventManager eventManager,
        FightManager fightManager,
        NoticeService noticeService
    ) {
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.fightManager = fightManager;
        this.noticeService = noticeService;
    }

    @Override
    public void initialize() {
        LandsIntegration lands = LandsIntegration.of(this.plugin);
        this.regionProvider = new LandsRegionProvider(lands);
        this.eventManager.subscribe(new LandsRegionController(
            this.fightManager,
            this.noticeService,
            lands
        ));
    }

    public RegionProvider getRegionProvider() {
        return this.regionProvider;
    }
}
