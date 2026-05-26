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
        add(ModItems.SURVIVAL_REDSTONE_CORE.get(), "生电核心");

        add(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(), "生电工作台");

        add("itemGroup.simple_sr_tab", "简易生电");
    }
}
