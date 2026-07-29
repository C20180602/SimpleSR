package com.alster.simple_sr.block;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.item.ModItems;
import com.alster.simple_sr.item.VillagerTradingHallBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, SimpleSR.MODID);

    public static final RegistryObject<Block> SURVIVAL_REDSTONE_CRAFTING_TABLE
            = registerBlock("survival_redstone_crafting_table",
            () -> new SurvivalRedstoneCraftingTableBlock(machineProperties()));

    public static final RegistryObject<Block> IRON_FARM
            = registerBlock("iron_farm",
            () -> new IronFarmBlock(machineProperties()));

    public static final RegistryObject<Block> SLIME_TOWER
            = registerBlock("slime_tower",
            () -> new SlimeTowerBlock(machineProperties()));

    public static final RegistryObject<Block> COBBLESTONE_FARM
            = registerBlock("cobblestone_farm",
            () -> new CobblestoneFarmBlock(machineProperties()));

    public static final RegistryObject<Block> FURNACE_ARRAY
            = registerBlock("furnace_array",
            () -> new FurnaceArrayBlock(machineProperties()));

    public static final RegistryObject<Block> RAIL_FARM
            = registerBlock("rail_farm",
            () -> new RailFarmBlock(machineProperties()));

    public static final RegistryObject<Block> SWAMP_MOB_TOWER
            = registerBlock("swamp_mob_tower",
            () -> new SwampMobTowerBlock(machineProperties()));

    public static final RegistryObject<Block> HONEY_FARM
            = registerBlock("honey_farm",
            () -> new HoneyFarmBlock(machineProperties()));

    public static final RegistryObject<Block> VILLAGER_BREEDER
            = registerBlock("villager_breeder",
            () -> new VillagerBreederBlock(machineProperties()));

    public static final RegistryObject<Block> VILLAGER_TRADING_HALL
            = registerBlock("villager_trading_hall",
            () -> new VillagerTradingHallBlock(machineProperties()));

    public static BlockBehaviour.Properties machineProperties() {
        return BlockBehaviour.Properties.of()
                .strength(2.5f, 3.0f)
                .requiresCorrectToolForDrops();
    }

    private static <T extends Block> void registerBlockItems(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> block == VILLAGER_TRADING_HALL
                ? new VillagerTradingHallBlockItem(block.get(), new Item.Properties())
                : new BlockItem(block.get(), new Item.Properties()));
    }
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> blocks = BLOCKS.register(name, block);
        registerBlockItems(name, blocks);
        return blocks;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
