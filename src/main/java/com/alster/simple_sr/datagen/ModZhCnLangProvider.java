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
        add("itemGroup.simple_sr_tab", "\u7b80\u6613\u751f\u7535");
        add("container.simple_sr.iron_farm", "\u5237\u94c1\u673a");
        add("container.simple_sr.villager_breeder", "\u6751\u6c11\u7e41\u6b96\u673a");
    }
}
