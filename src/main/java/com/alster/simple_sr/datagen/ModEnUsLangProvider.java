package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output) {
        super(output, SimpleSR.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.SURVIVAL_REDSTONE_CORE.get(), "Survival Redstone Core");

        add(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(), "Survival Redstone Crafting Table");

        add("itemGroup.simple_sr_tab", "Simple Survival Redstone");
        add("container.simple_sr.survival_redstone_crafting_table", "Survival Redstone Crafting Table");
        add("recipe.simple_sr.iron_farm", "Iron Farm");
    }
}
