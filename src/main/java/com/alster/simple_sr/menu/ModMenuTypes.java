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

    public static final RegistryObject<MenuType<EndermanTowerMenu>> ENDERMAN_TOWER_MENU =
            registerMenuType("enderman_tower", EndermanTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<ZombifiedPiglinTowerMenu>> ZOMBIFIED_PIGLIN_TOWER_MENU =
            registerMenuType("zombified_piglin_tower", ZombifiedPiglinTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<PiglinTradingTowerMenu>> PIGLIN_TRADING_TOWER_MENU =
            registerMenuType("piglin_trading_tower", PiglinTradingTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<HoglinFarmMenu>> HOGLIN_FARM_MENU =
            registerMenuType("hoglin_farm", HoglinFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<RaidTowerMenu>> RAID_TOWER_MENU =
            registerMenuType("raid_tower", RaidTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<GuardianFarmMenu>> GUARDIAN_FARM_MENU =
            registerMenuType("guardian_farm", GuardianFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<WitherSkeletonFarmMenu>> WITHER_SKELETON_FARM_MENU =
            registerMenuType("wither_skeleton_farm", WitherSkeletonFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<FurnaceArrayMenu>> FURNACE_ARRAY_MENU =
            registerMenuType("furnace_array", FurnaceArrayMenu::fromNetwork);

    public static final RegistryObject<MenuType<TreeFarmMenu>> TREE_FARM_MENU =
            registerMenuType("tree_farm", TreeFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<RailFarmMenu>> RAIL_FARM_MENU =
            registerMenuType("rail_farm", RailFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<SandFarmMenu>> SAND_FARM_MENU =
            registerMenuType("sand_farm", SandFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<SolidifierMenu>> SOLIDIFIER_MENU =
            registerMenuType("solidifier", SolidifierMenu::fromNetwork);

    public static final RegistryObject<MenuType<ShulkerFarmMenu>> SHULKER_FARM_MENU =
            registerMenuType("shulker_farm", ShulkerFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<BambooFarmMenu>> BAMBOO_FARM_MENU =
            registerMenuType("bamboo_farm", BambooFarmMenu::fromNetwork);

    public static final RegistryObject<MenuType<PackerMenu>> PACKER_MENU =
            registerMenuType("packer", PackerMenu::fromNetwork);

    public static final RegistryObject<MenuType<SwampMobTowerMenu>> SWAMP_MOB_TOWER_MENU =
            registerMenuType("swamp_mob_tower", SwampMobTowerMenu::fromNetwork);

    public static final RegistryObject<MenuType<HoneyFarmMenu>> HONEY_FARM_MENU =
            registerMenuType("honey_farm", HoneyFarmMenu::fromNetwork);

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
