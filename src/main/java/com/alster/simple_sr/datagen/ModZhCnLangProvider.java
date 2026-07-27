package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output) {
        super(output, SimpleSR.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.SURVIVAL_REDSTONE_CORE.get(), "\u751f\u7535\u6838\u5fc3");
        add(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(), "\u751f\u7535\u5de5\u4f5c\u53f0");
        add(ModBlocks.IRON_FARM.get(), "\u5237\u94c1\u673a");
        add(ModBlocks.SLIME_TOWER.get(), "\u53f2\u83b1\u59c6\u5237\u602a\u5854");
        add(ModBlocks.COBBLESTONE_FARM.get(), "\u5237\u77f3\u673a");
        add(ModBlocks.FURNACE_ARRAY.get(), "50\u500d\u901f\u7194\u7089\u7ec4");
        add(ModBlocks.RAIL_FARM.get(), "\u5237\u94c1\u8f68\u673a");
        add(ModBlocks.SWAMP_MOB_TOWER.get(), "\u6cbc\u6cfd\u5237\u602a\u5854");
        add(ModBlocks.VILLAGER_BREEDER.get(), "\u6751\u6c11\u7e41\u6b96\u673a");
        add(ModBlocks.VILLAGER_TRADING_HALL.get(), "\u6751\u6c11\u4ea4\u6613\u6240");
        add("itemGroup.simple_sr_tab", "\u7b80\u6613\u751f\u7535");
        add("container.simple_sr.iron_farm", "\u5237\u94c1\u673a");
        add("container.simple_sr.slime_tower", "\u53f2\u83b1\u59c6\u5237\u602a\u5854");
        add("container.simple_sr.cobblestone_farm", "\u5237\u77f3\u673a");
        add("container.simple_sr.furnace_array", "50\u500d\u901f\u7194\u7089\u7ec4");
        add("container.simple_sr.rail_farm", "\u5237\u94c1\u8f68\u673a");
        add("container.simple_sr.swamp_mob_tower", "\u6cbc\u6cfd\u5237\u602a\u5854");
        add("status.simple_sr.slime_tower.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.slime_tower.not_running", "\u975e\u53f2\u83b1\u59c6\u533a\u5757\uff0c\u4e0d\u8fd0\u884c");
        add("container.simple_sr.villager_breeder", "\u6751\u6c11\u7e41\u6b96\u673a");
        add("container.simple_sr.villager_trading_hall", "\u6751\u6c11\u4ea4\u6613\u6240");
        add("status.simple_sr.swamp_mob_tower.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.swamp_mob_tower.not_running", "\u975e\u6c90\u6cfd\u7fa4\u7cfb\uff0c\u4e0d\u8fd0\u884c");
        add("button.simple_sr.submit", "\u63d0\u4ea4");
        add("button.simple_sr.refresh", "\u5237\u65b0\u4ea4\u6613");
        add("button.simple_sr.sort_number", "\u7f16\u53f7");
        add("button.simple_sr.sort_profession", "\u804c\u4e1a");
        add("screen.simple_sr.trades", "\u4ea4\u6613");
        add("recipe.simple_sr.villager_trading_hall", "\u6751\u6c11\u4ea4\u6613\u6240");
        add("recipe.simple_sr.iron_farm", "\u5237\u94c1\u673a");
        add("recipe.simple_sr.villager_breeder", "\u6751\u6c11\u7e41\u6b96\u673a");
        add("recipe.simple_sr.slime_tower", "\u53f2\u83b1\u59c6\u5237\u602a\u5854");
        add("recipe.simple_sr.cobblestone_farm", "\u5237\u77f3\u673a");
        add("recipe.simple_sr.furnace_array", "50\u500d\u901f\u7194\u7089\u7ec4");
        add("recipe.simple_sr.rail_farm", "\u5237\u94c1\u8f68\u673a");
        add("recipe.simple_sr.swamp_mob_tower", "\u6cbc\u6cfd\u5237\u602a\u5854");
        add("screen.simple_sr.furnace_array.input", "\u8f93\u5165\u7bb1");
        add("screen.simple_sr.furnace_array.output", "\u8f93\u51fa\u7bb1");
        add("screen.simple_sr.rail_farm.output", "\u4ea7\u7269\u7bb1");
        add("button.simple_sr.rail_farm.on", "\u5f00");
        add("button.simple_sr.rail_farm.off", "\u5173");
    }
}
