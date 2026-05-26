package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {

    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SimpleSR.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SURVIVAL_REDSTONE_CORE.get());
        withExistingParent(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.getId().getPath(),
                modLoc("block/survival_redstone_crafting_table"));
    }
}
