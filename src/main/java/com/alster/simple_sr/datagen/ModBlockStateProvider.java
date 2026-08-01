package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SimpleSR.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(), models().cube(
                "survival_redstone_crafting_table",
                modLoc("block/survival_redstone_crafting_table_bottom"),
                modLoc("block/survival_redstone_crafting_table_top"),
                modLoc("block/survival_redstone_crafting_table_front"),
                modLoc("block/survival_redstone_crafting_table_side"),
                modLoc("block/survival_redstone_crafting_table_side"),
                modLoc("block/survival_redstone_crafting_table_side")
        ).texture("particle", modLoc("block/survival_redstone_crafting_table_side")));

        simpleBlockItem(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(),
                models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/survival_redstone_crafting_table")));

        horizontalBlock(ModBlocks.IRON_FARM.get(), models().cube(
                "iron_farm",
                modLoc("block/iron_farm_side"),
                modLoc("block/iron_farm_side"),
                modLoc("block/iron_farm_front"),
                modLoc("block/iron_farm_side"),
                modLoc("block/iron_farm_side"),
                modLoc("block/iron_farm_side")
        ).texture("particle", modLoc("block/iron_farm_side")));

        simpleBlockItem(ModBlocks.IRON_FARM.get(),
                models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/iron_farm")));

        horizontalBlock(ModBlocks.SLIME_TOWER.get(), models().cube(
                "slime_tower",
                modLoc("block/slime_tower_side"), modLoc("block/slime_tower_side"),
                modLoc("block/slime_tower_front"), modLoc("block/slime_tower_side"),
                modLoc("block/slime_tower_side"), modLoc("block/slime_tower_side")
        ).texture("particle", modLoc("block/slime_tower_side")));
        simpleBlockItem(ModBlocks.SLIME_TOWER.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/slime_tower")));

        horizontalBlock(ModBlocks.ENDERMAN_TOWER.get(), models().cube(
                "enderman_tower",
                modLoc("block/enderman_tower_side"), modLoc("block/enderman_tower_side"),
                modLoc("block/enderman_tower_front"), modLoc("block/enderman_tower_side"),
                modLoc("block/enderman_tower_side"), modLoc("block/enderman_tower_side")
        ).texture("particle", modLoc("block/enderman_tower_side")));
        simpleBlockItem(ModBlocks.ENDERMAN_TOWER.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/enderman_tower")));

        horizontalBlock(ModBlocks.COBBLESTONE_FARM.get(), models().cube(
                "cobblestone_farm",
                modLoc("block/cobblestone_farm_side"), modLoc("block/cobblestone_farm_side"),
                modLoc("block/cobblestone_farm_front"), modLoc("block/cobblestone_farm_side"),
                modLoc("block/cobblestone_farm_side"), modLoc("block/cobblestone_farm_side")
        ).texture("particle", modLoc("block/cobblestone_farm_side")));
        simpleBlockItem(ModBlocks.COBBLESTONE_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/cobblestone_farm")));

        horizontalBlock(ModBlocks.FURNACE_ARRAY.get(), models().cube(
                "furnace_array",
                modLoc("block/furnace_array_side"), modLoc("block/furnace_array_side"),
                modLoc("block/furnace_array_front"), modLoc("block/furnace_array_side"),
                modLoc("block/furnace_array_side"), modLoc("block/furnace_array_side")
        ).texture("particle", modLoc("block/furnace_array_side")));
        simpleBlockItem(ModBlocks.FURNACE_ARRAY.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/furnace_array")));

        horizontalBlock(ModBlocks.TREE_FARM.get(), models().cube(
                "tree_farm",
                modLoc("block/tree_farm_side"), modLoc("block/tree_farm_side"),
                modLoc("block/tree_farm_front"), modLoc("block/tree_farm_side"),
                modLoc("block/tree_farm_side"), modLoc("block/tree_farm_side")
        ).texture("particle", modLoc("block/tree_farm_side")));
        simpleBlockItem(ModBlocks.TREE_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/tree_farm")));

        horizontalBlock(ModBlocks.RAIL_FARM.get(), models().cube(
                "rail_farm",
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side"),
                modLoc("block/rail_farm_front"), modLoc("block/rail_farm_side"),
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side")
        ).texture("particle", modLoc("block/rail_farm_side")));
        simpleBlockItem(ModBlocks.RAIL_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/rail_farm")));

        horizontalBlock(ModBlocks.SAND_FARM.get(), models().cube(
                "sand_farm",
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side"),
                new ResourceLocation("minecraft", "block/sand"), modLoc("block/rail_farm_side"),
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side")
        ).texture("particle", modLoc("block/rail_farm_side")));
        simpleBlockItem(ModBlocks.SAND_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/sand_farm")));

        horizontalBlock(ModBlocks.SOLIDIFIER.get(), models().cube(
                "solidifier",
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side"),
                new ResourceLocation("minecraft", "block/sand"), modLoc("block/rail_farm_side"),
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side")
        ).texture("particle", modLoc("block/rail_farm_side")));
        simpleBlockItem(ModBlocks.SOLIDIFIER.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/solidifier")));

        horizontalBlock(ModBlocks.SHULKER_FARM.get(), models().cube(
                "shulker_farm",
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side"),
                new ResourceLocation("minecraft", "block/shulker_box"), modLoc("block/rail_farm_side"),
                modLoc("block/rail_farm_side"), modLoc("block/rail_farm_side")
        ).texture("particle", modLoc("block/rail_farm_side")));
        simpleBlockItem(ModBlocks.SHULKER_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/shulker_farm")));

        horizontalBlock(ModBlocks.BAMBOO_FARM.get(), models().cube(
                "bamboo_farm",
                modLoc("block/cobblestone_farm_side"), modLoc("block/cobblestone_farm_side"),
                new ResourceLocation("minecraft", "block/sugar_cane"), modLoc("block/cobblestone_farm_side"),
                modLoc("block/cobblestone_farm_side"), modLoc("block/cobblestone_farm_side")
        ).texture("particle", modLoc("block/cobblestone_farm_side")));
        simpleBlockItem(ModBlocks.BAMBOO_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/bamboo_farm")));

        horizontalBlock(ModBlocks.PACKER.get(), models().cube(
                "packer",
                modLoc("block/cobblestone_farm_side"), modLoc("block/cobblestone_farm_side"),
                new ResourceLocation("minecraft", "block/shulker_box"), modLoc("block/cobblestone_farm_side"),
                modLoc("block/cobblestone_farm_side"), modLoc("block/cobblestone_farm_side")
        ).texture("particle", modLoc("block/cobblestone_farm_side")));
        simpleBlockItem(ModBlocks.PACKER.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/packer")));

        horizontalBlock(ModBlocks.SWAMP_MOB_TOWER.get(), models().cube(
                "swamp_mob_tower",
                modLoc("block/swamp_mob_tower_side"), modLoc("block/swamp_mob_tower_side"),
                modLoc("block/swamp_mob_tower_front"), modLoc("block/swamp_mob_tower_side"),
                modLoc("block/swamp_mob_tower_side"), modLoc("block/swamp_mob_tower_side")
        ).texture("particle", modLoc("block/swamp_mob_tower_side")));
        simpleBlockItem(ModBlocks.SWAMP_MOB_TOWER.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/swamp_mob_tower")));

        horizontalBlock(ModBlocks.HONEY_FARM.get(), models().cube(
                "honey_farm",
                modLoc("block/honey_farm_side"), modLoc("block/honey_farm_side"),
                modLoc("block/honey_farm_front"), modLoc("block/honey_farm_side"),
                modLoc("block/honey_farm_side"), modLoc("block/honey_farm_side")
        ).texture("particle", modLoc("block/honey_farm_side")));
        simpleBlockItem(ModBlocks.HONEY_FARM.get(), models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/honey_farm")));

        horizontalBlock(ModBlocks.VILLAGER_BREEDER.get(), models().cube(
                "villager_breeder",
                modLoc("block/villager_breeder_side"),
                modLoc("block/villager_breeder_side"),
                modLoc("block/villager_breeder_front"),
                modLoc("block/villager_breeder_side"),
                modLoc("block/villager_breeder_side"),
                modLoc("block/villager_breeder_side")
        ).texture("particle", modLoc("block/villager_breeder_side")));

        simpleBlockItem(ModBlocks.VILLAGER_BREEDER.get(),
                models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/villager_breeder")));

        horizontalBlock(ModBlocks.VILLAGER_TRADING_HALL.get(), models().cube(
                "villager_trading_hall",
                modLoc("block/villager_breeder_side"), modLoc("block/villager_breeder_side"),
                modLoc("block/villager_breeder_front"), modLoc("block/villager_breeder_side"),
                modLoc("block/villager_breeder_side"), modLoc("block/villager_breeder_side")
        ).texture("particle", modLoc("block/villager_breeder_side")));
        simpleBlockItem(ModBlocks.VILLAGER_TRADING_HALL.get(),
                models().getExistingFile(new ResourceLocation(SimpleSR.MODID, "block/villager_trading_hall")));
    }

}
