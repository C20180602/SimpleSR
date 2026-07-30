package com.alster.simple_sr.datagen;

import com.alster.simple_sr.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTablesProvider extends BlockLootSubProvider {
    public ModBlockLootTablesProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get());
        dropSelf(ModBlocks.IRON_FARM.get());
        dropSelf(ModBlocks.SLIME_TOWER.get());
        dropSelf(ModBlocks.COBBLESTONE_FARM.get());
        dropSelf(ModBlocks.FURNACE_ARRAY.get());
        dropSelf(ModBlocks.RAIL_FARM.get());
        dropSelf(ModBlocks.SAND_FARM.get());
        dropSelf(ModBlocks.SOLIDIFIER.get());
        dropSelf(ModBlocks.SHULKER_FARM.get());
        dropSelf(ModBlocks.BAMBOO_FARM.get());
        dropSelf(ModBlocks.PACKER.get());
        dropSelf(ModBlocks.SWAMP_MOB_TOWER.get());
        dropSelf(ModBlocks.HONEY_FARM.get());
        dropSelf(ModBlocks.VILLAGER_BREEDER.get());
        dropSelf(ModBlocks.VILLAGER_TRADING_HALL.get());
    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

}
