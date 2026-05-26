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
                })
                .build()
            );
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
