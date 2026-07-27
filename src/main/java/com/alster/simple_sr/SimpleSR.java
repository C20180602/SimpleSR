package com.alster.simple_sr;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.ModBlockEntities;
import com.alster.simple_sr.item.ModCreativeModeTabs;
import com.alster.simple_sr.item.ModItems;
import com.alster.simple_sr.menu.ModMenuTypes;
import com.alster.simple_sr.screen.SurvivalRedstoneCraftingTableScreen;
import com.alster.simple_sr.screen.VillagerBreederScreen;
import com.alster.simple_sr.screen.VillagerTradingHallScreen;
import com.alster.simple_sr.screen.SlimeTowerScreen;
import com.alster.simple_sr.screen.FurnaceArrayScreen;
import com.alster.simple_sr.screen.RailFarmScreen;
import com.alster.simple_sr.screen.SwampMobTowerScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SimpleSR.MODID)
public class SimpleSR
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "simple_sr";
    public static net.minecraft.resources.ResourceLocation id(String path) { return new net.minecraft.resources.ResourceLocation(MODID, path); }
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public SimpleSR(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        Network.register();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.SURVIVAL_REDSTONE_CRAFTING_TABLE_MENU.get(), SurvivalRedstoneCraftingTableScreen::new);
                MenuScreens.register(ModMenuTypes.VILLAGER_BREEDER_MENU.get(), VillagerBreederScreen::new);
                MenuScreens.register(ModMenuTypes.VILLAGER_TRADING_HALL_MENU.get(), VillagerTradingHallScreen::new);
                MenuScreens.register(ModMenuTypes.SLIME_TOWER_MENU.get(), SlimeTowerScreen::new);
                MenuScreens.register(ModMenuTypes.FURNACE_ARRAY_MENU.get(), FurnaceArrayScreen::new);
                MenuScreens.register(ModMenuTypes.RAIL_FARM_MENU.get(), RailFarmScreen::new);
                MenuScreens.register(ModMenuTypes.SWAMP_MOB_TOWER_MENU.get(), SwampMobTowerScreen::new);
            });
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
