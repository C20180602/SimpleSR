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

    public static final RegistryObject<MenuType<SlimeTowerMenu>> SLIME_TOWER_MENU =
            registerMenuType("slime_tower", SlimeTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<FurnaceArrayMenu>> FURNACE_ARRAY_MENU =
            registerMenuType("furnace_array", FurnaceArrayMenu::fromNetwork);

    public static final RegistryObject<MenuType<RailFarmMenu>> RAIL_FARM_MENU =
            registerMenuType("rail_farm", RailFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<SwampMobTowerMenu>> SWAMP_MOB_TOWER_MENU =
            registerMenuType("swamp_mob_tower", SwampMobTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<VillagerTradingHallMenu>> VILLAGER_TRADING_HALL_MENU =
            registerMenuType("villager_trading_hall", VillagerTradingHallMenu::new);

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(
            String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
