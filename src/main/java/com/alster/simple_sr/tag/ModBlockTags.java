package com.alster.simple_sr.tag;

import com.alster.simple_sr.SimpleSR;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> MACHINES = create("machines");

    private static TagKey<Block> create(String pName) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SimpleSR.MODID,pName));
    }
    private static TagKey<Block> createForge(String pName) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge",pName));
    }
}
