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
        withExistingParent(ModBlocks.IRON_FARM.getId().getPath(),
                modLoc("block/iron_farm"));
        withExistingParent(ModBlocks.SLIME_TOWER.getId().getPath(),
                modLoc("block/slime_tower"));
        withExistingParent(ModBlocks.COBBLESTONE_FARM.getId().getPath(),
                modLoc("block/cobblestone_farm"));
        withExistingParent(ModBlocks.FURNACE_ARRAY.getId().getPath(),
                modLoc("block/furnace_array"));
        withExistingParent(ModBlocks.RAIL_FARM.getId().getPath(),
                modLoc("block/rail_farm"));
        withExistingParent(ModBlocks.SWAMP_MOB_TOWER.getId().getPath(),
                modLoc("block/swamp_mob_tower"));
        withExistingParent(ModBlocks.HONEY_FARM.getId().getPath(),
                modLoc("block/honey_farm"));
        withExistingParent(ModBlocks.VILLAGER_BREEDER.getId().getPath(),
                modLoc("block/villager_breeder"));
        withExistingParent(ModBlocks.VILLAGER_TRADING_HALL.getId().getPath(),
                modLoc("block/villager_trading_hall"));
    }
}
