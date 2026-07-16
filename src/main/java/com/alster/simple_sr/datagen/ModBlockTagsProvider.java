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
                .add(ModBlocks.VILLAGER_BREEDER.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(ModBlockTags.MACHINES);
        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(ModBlockTags.MACHINES);
    }
}
