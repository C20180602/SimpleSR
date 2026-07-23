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
        add(ModBlocks.VILLAGER_BREEDER.get(), "\u6751\u6c11\u7e41\u6b96\u673a");
        add(ModBlocks.VILLAGER_TRADING_HALL.get(), "\u6751\u6c11\u4ea4\u6613\u6240");
        add("itemGroup.simple_sr_tab", "\u7b80\u6613\u751f\u7535");
        add("container.simple_sr.iron_farm", "\u5237\u94c1\u673a");
        add("container.simple_sr.villager_breeder", "\u6751\u6c11\u7e41\u6b96\u673a");
        add("container.simple_sr.villager_trading_hall", "\u6751\u6c11\u4ea4\u6613\u6240");
        add("button.simple_sr.submit", "\u63d0\u4ea4");
        add("button.simple_sr.refresh", "\u5237\u65b0\u4ea4\u6613");
        add("button.simple_sr.sort_number", "\u7f16\u53f7");
        add("button.simple_sr.sort_profession", "\u804c\u4e1a");
        add("screen.simple_sr.trades", "\u4ea4\u6613");
        add("recipe.simple_sr.villager_trading_hall", "\u6751\u6c11\u4ea4\u6613\u6240");
    }
}
