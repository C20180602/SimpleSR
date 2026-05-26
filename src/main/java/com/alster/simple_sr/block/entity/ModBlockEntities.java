package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SimpleSR.MODID);

    public static final RegistryObject<BlockEntityType<SurvivalRedstoneCraftingTableBlockEntity>> SURVIVAL_REDSTONE_CRAFTING_TABLE_BE =
            BLOCK_ENTITIES.register("survival_redstone_crafting_table",
                    () -> BlockEntityType.Builder.of(SurvivalRedstoneCraftingTableBlockEntity::new,
                            ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
