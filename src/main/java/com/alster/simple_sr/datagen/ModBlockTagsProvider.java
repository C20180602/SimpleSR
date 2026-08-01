package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.tag.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper exFileHelper) {
        super(output, lookupProvider, SimpleSR.MODID, exFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModBlockTags.MACHINES)
                .add(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get())
                .add(ModBlocks.IRON_FARM.get())
                .add(ModBlocks.SLIME_TOWER.get())
                .add(ModBlocks.ENDERMAN_TOWER.get())
                .add(ModBlocks.ZOMBIFIED_PIGLIN_TOWER.get())
                .add(ModBlocks.PIGLIN_TRADING_TOWER.get())
                .add(ModBlocks.HOGLIN_FARM.get())
                .add(ModBlocks.RAID_TOWER.get())
                .add(ModBlocks.GUARDIAN_FARM.get())
                .add(ModBlocks.WITHER_SKELETON_FARM.get())
                .add(ModBlocks.COBBLESTONE_FARM.get())
                .add(ModBlocks.FURNACE_ARRAY.get())
                .add(ModBlocks.TREE_FARM.get())
                .add(ModBlocks.RAIL_FARM.get())
                .add(ModBlocks.SAND_FARM.get())
                .add(ModBlocks.SOLIDIFIER.get())
                .add(ModBlocks.SHULKER_FARM.get())
                .add(ModBlocks.BAMBOO_FARM.get())
                .add(ModBlocks.PACKER.get())
                .add(ModBlocks.SWAMP_MOB_TOWER.get())
                .add(ModBlocks.HONEY_FARM.get())
                .add(ModBlocks.VILLAGER_BREEDER.get())
                .add(ModBlocks.VILLAGER_TRADING_HALL.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(ModBlockTags.MACHINES);
        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(ModBlockTags.MACHINES);
    }
}
