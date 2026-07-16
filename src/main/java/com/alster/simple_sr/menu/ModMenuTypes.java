package com.alster.simple_sr.menu;

import com.alster.simple_sr.SimpleSR;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SimpleSR.MODID);

    public static final RegistryObject<MenuType<SurvivalRedstoneCraftingTableMenu>> SURVIVAL_REDSTONE_CRAFTING_TABLE_MENU =
            registerMenuType("survival_redstone_crafting_table", SurvivalRedstoneCraftingTableMenu::new);

    public static final RegistryObject<MenuType<VillagerBreederMenu>> VILLAGER_BREEDER_MENU =
            registerMenuType("villager_breeder", VillagerBreederMenu::new);

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(
            String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
