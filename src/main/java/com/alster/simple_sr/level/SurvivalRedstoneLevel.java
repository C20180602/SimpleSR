package com.alster.simple_sr.level;

import com.alster.simple_sr.Config;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class SurvivalRedstoneLevel {
    private SurvivalRedstoneLevel() {
    }

    public static int get(ServerPlayer player) {
        int level = 1;
        for (Config.LevelUnlock unlock : Config.levelUnlocks) {
            if (unlock.level() <= 1 || hasAnyAdvancement(player, unlock.advancements())) {
                level = Math.max(level, unlock.level());
            }
        }
        return level;
    }

    private static boolean hasAnyAdvancement(ServerPlayer player, List<ResourceLocation> advancements) {
        if (advancements.isEmpty()) {
            return false;
        }
        for (ResourceLocation id : advancements) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(id);
            if (advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                return true;
            }
        }
        return false;
    }
}
