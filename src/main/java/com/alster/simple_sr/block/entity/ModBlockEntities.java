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

    public static final RegistryObject<BlockEntityType<IronFarmBlockEntity>> IRON_FARM_BE =
            BLOCK_ENTITIES.register("iron_farm",
                    () -> BlockEntityType.Builder.of(IronFarmBlockEntity::new,
                            ModBlocks.IRON_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<SlimeTowerBlockEntity>> SLIME_TOWER_BE =
            BLOCK_ENTITIES.register("slime_tower",
                    () -> BlockEntityType.Builder.of(SlimeTowerBlockEntity::new,
                            ModBlocks.SLIME_TOWER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CobblestoneFarmBlockEntity>> COBBLESTONE_FARM_BE =
            BLOCK_ENTITIES.register("cobblestone_farm",
                    () -> BlockEntityType.Builder.of(CobblestoneFarmBlockEntity::new,
                            ModBlocks.COBBLESTONE_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<FurnaceArrayBlockEntity>> FURNACE_ARRAY_BE =
            BLOCK_ENTITIES.register("furnace_array",
                    () -> BlockEntityType.Builder.of(FurnaceArrayBlockEntity::new,
                            ModBlocks.FURNACE_ARRAY.get()).build(null));

    public static final RegistryObject<BlockEntityType<RailFarmBlockEntity>> RAIL_FARM_BE =
            BLOCK_ENTITIES.register("rail_farm",
                    () -> BlockEntityType.Builder.of(RailFarmBlockEntity::new,
                            ModBlocks.RAIL_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<SwampMobTowerBlockEntity>> SWAMP_MOB_TOWER_BE =
            BLOCK_ENTITIES.register("swamp_mob_tower",
                    () -> BlockEntityType.Builder.of(SwampMobTowerBlockEntity::new,
                            ModBlocks.SWAMP_MOB_TOWER.get()).build(null));

    public static final RegistryObject<BlockEntityType<HoneyFarmBlockEntity>> HONEY_FARM_BE =
            BLOCK_ENTITIES.register("honey_farm",
                    () -> BlockEntityType.Builder.of(HoneyFarmBlockEntity::new,
                            ModBlocks.HONEY_FARM.get()).build(null));

    public static final RegistryObject<BlockEntityType<VillagerBreederBlockEntity>> VILLAGER_BREEDER_BE =
            BLOCK_ENTITIES.register("villager_breeder",
                    () -> BlockEntityType.Builder.of(VillagerBreederBlockEntity::new,
                            ModBlocks.VILLAGER_BREEDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<VillagerTradingHallBlockEntity>> VILLAGER_TRADING_HALL_BE =
            BLOCK_ENTITIES.register("villager_trading_hall",
                    () -> BlockEntityType.Builder.of(VillagerTradingHallBlockEntity::new,
                            ModBlocks.VILLAGER_TRADING_HALL.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
