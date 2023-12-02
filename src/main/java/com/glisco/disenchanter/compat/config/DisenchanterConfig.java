package com.glisco.disenchanter.compat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@Config(name = "disenchanter")
public class DisenchanterConfig implements ConfigData {

    public Map<String, CatalystConfig> catalysts = new HashMap<>();
    public boolean allowDisenchantingWithoutCatalyst = true;

    public DisenchanterConfig() {
        catalysts.put("minecraft:emerald", new CatalystConfig());
        catalysts.put("minecraft:diamond", new CatalystConfig());
        catalysts.put("minecraft:ender_pearl", new CatalystConfig());
        catalysts.put("minecraft:heart_of_the_sea", new CatalystConfig());
        catalysts.put("minecraft:amethyst_shard", new CatalystConfig());
        catalysts.put("minecraft:nether_star", new CatalystConfig());
        catalysts.put("minecraft:experience_bottle", new CatalystConfig());
    }

    public static class CatalystConfig {
        public boolean enabled = true;
        public int required_item_count = 1;
    }

}
