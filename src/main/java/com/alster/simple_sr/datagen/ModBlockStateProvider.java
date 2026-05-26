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
    }

}
