package com.alster.simple_sr.item;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS
            = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimpleSR.MODID);

    public static final RegistryObject<CreativeModeTab> SSR_TAB
            = CREATIVE_MODE_TABS.register("simple_sr_tab", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.SURVIVAL_REDSTONE_CORE.get()))
                .title(Component.translatable("itemGroup.simple_sr_tab"))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.SURVIVAL_REDSTONE_CORE.get());
                    output.accept(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get());
                    output.accept(ModBlocks.IRON_FARM.get());
                    output.accept(ModBlocks.SLIME_TOWER.get());
                    output.accept(ModBlocks.ENDERMAN_TOWER.get());
                    output.accept(ModBlocks.ZOMBIFIED_PIGLIN_TOWER.get());
                    output.accept(ModBlocks.PIGLIN_TRADING_TOWER.get());
                    output.accept(ModBlocks.HOGLIN_FARM.get());
                    output.accept(ModBlocks.RAID_TOWER.get());
                    output.accept(ModBlocks.GUARDIAN_FARM.get());
                    output.accept(ModBlocks.WITHER_SKELETON_FARM.get());
                    output.accept(ModBlocks.COBBLESTONE_FARM.get());
                    output.accept(ModBlocks.FURNACE_ARRAY.get());
                    output.accept(ModBlocks.TREE_FARM.get());
                    output.accept(ModBlocks.RAIL_FARM.get());
                    output.accept(ModBlocks.SAND_FARM.get());
                    output.accept(ModBlocks.SOLIDIFIER.get());
                    output.accept(ModBlocks.SHULKER_FARM.get());
                    output.accept(ModBlocks.BAMBOO_FARM.get());
                    output.accept(ModBlocks.PACKER.get());
                    output.accept(ModBlocks.SWAMP_MOB_TOWER.get());
                    output.accept(ModBlocks.HONEY_FARM.get());
                    output.accept(ModBlocks.VILLAGER_BREEDER.get());
                    output.accept(ModBlocks.VILLAGER_TRADING_HALL.get());
                })
                .build()
            );
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
