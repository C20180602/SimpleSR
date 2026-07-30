package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output) {
        super(output, SimpleSR.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.SURVIVAL_REDSTONE_CORE.get(), "Survival Redstone Core");

        add(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(), "Survival Redstone Crafting Table");
        add(ModBlocks.IRON_FARM.get(), "Iron Farm");
        add(ModBlocks.SLIME_TOWER.get(), "Slime Tower");
        add(ModBlocks.COBBLESTONE_FARM.get(), "Cobblestone Farm");
        add(ModBlocks.FURNACE_ARRAY.get(), "50x Furnace Array");
        add(ModBlocks.RAIL_FARM.get(), "Rail Farm");
        add(ModBlocks.SAND_FARM.get(), "Sand Farm");
        add(ModBlocks.SOLIDIFIER.get(), "Solidifier");
        add(ModBlocks.SHULKER_FARM.get(), "Shulker Farm");
        add(ModBlocks.BAMBOO_FARM.get(), "Sugar Cane/Bamboo Farm");
        add(ModBlocks.PACKER.get(), "Packer");
        add(ModBlocks.SWAMP_MOB_TOWER.get(), "Swamp Mob Tower");
        add(ModBlocks.HONEY_FARM.get(), "Honey Farm");
        add(ModBlocks.VILLAGER_BREEDER.get(), "Villager Breeder");
        add(ModBlocks.VILLAGER_TRADING_HALL.get(), "Villager Trading Hall");

        add("itemGroup.simple_sr_tab", "Simple Survival Redstone");
        add("container.simple_sr.survival_redstone_crafting_table", "Survival Redstone Crafting Table");
        add("container.simple_sr.iron_farm", "Iron Farm");
        add("container.simple_sr.slime_tower", "Slime Tower");
        add("container.simple_sr.cobblestone_farm", "Cobblestone Farm");
        add("container.simple_sr.furnace_array", "50x Furnace Array");
        add("container.simple_sr.rail_farm", "Rail Farm");
        add("container.simple_sr.sand_farm", "Sand Farm");
        add("container.simple_sr.solidifier", "Solidifier");
        add("container.simple_sr.shulker_farm", "Shulker Farm");
        add("container.simple_sr.bamboo_farm", "Sugar Cane/Bamboo Farm");
        add("container.simple_sr.packer", "Packer");
        add("container.simple_sr.swamp_mob_tower", "Swamp Mob Tower");
        add("container.simple_sr.honey_farm", "Honey Farm");
        add("status.simple_sr.slime_tower.running", "Running");
        add("status.simple_sr.slime_tower.not_running", "Not a slime chunk");
        add("container.simple_sr.villager_breeder", "Villager Breeder");
        add("container.simple_sr.villager_trading_hall", "Villager Trading Hall");
        add("status.simple_sr.swamp_mob_tower.running", "Running");
        add("status.simple_sr.swamp_mob_tower.not_running", "Not a swamp biome");
        add("button.simple_sr.submit", "Submit");
        add("button.simple_sr.refresh", "Refresh Trades");
        add("button.simple_sr.sort_number", "No.");
        add("button.simple_sr.sort_profession", "Job");
        add("screen.simple_sr.trades", "Trades");
        add("recipe.simple_sr.villager_trading_hall", "Villager Trading Hall");
        add("recipe.simple_sr.iron_farm", "Iron Farm");
        add("recipe.simple_sr.slime_tower", "Slime Tower");
        add("recipe.simple_sr.cobblestone_farm", "Cobblestone Farm");
        add("recipe.simple_sr.furnace_array", "50x Furnace Array");
        add("recipe.simple_sr.rail_farm", "Rail Farm");
        add("recipe.simple_sr.sand_farm", "Sand Farm");
        add("recipe.simple_sr.solidifier", "Solidifier");
        add("recipe.simple_sr.shulker_farm", "Shulker Farm");
        add("recipe.simple_sr.bamboo_farm", "Sugar Cane/Bamboo Farm");
        add("recipe.simple_sr.packer", "Packer");
        add("screen.simple_sr.packer.inventory", "Packer Inventory");
        add("screen.simple_sr.packer.backpack", "Backpack");
        add("status.simple_sr.packer.material_shortage", "Insufficient shulker box materials");
        add("status.simple_sr.packer.top_empty", "No extractable items above");
        add("button.simple_sr.bamboo_farm.sugar_cane", "Sugar Cane");
        add("button.simple_sr.bamboo_farm.bamboo", "Bamboo");
        add("status.simple_sr.shulker_farm.running", "Running");
        add("status.simple_sr.shulker_farm.not_running", "Not in the End dimension");
        add("screen.simple_sr.solidifier.input", "Input Chest");
        add("screen.simple_sr.solidifier.output", "Output Chest");
        add("status.simple_sr.sand_farm.running", "Running");
        add("status.simple_sr.sand_farm.not_running", "Not in the End dimension");
        add("status.simple_sr.solidifier.running", "Running");
        add("status.simple_sr.solidifier.not_running", "Not in the End dimension");
        add("screen.simple_sr.sand_farm.input", "Input Slots");
        add("screen.simple_sr.sand_farm.output", "Output Chest");
        add("button.simple_sr.sand_farm.on", "On");
        add("button.simple_sr.sand_farm.off", "Off");
        add("recipe.simple_sr.swamp_mob_tower", "Swamp Mob Tower");
        add("recipe.simple_sr.honey_farm", "Honey Farm");
        add("screen.simple_sr.honey_farm.input", "Input Chest");
        add("screen.simple_sr.honey_farm.output", "Output Chest");
        add("screen.simple_sr.furnace_array.input", "Input Chest");
        add("screen.simple_sr.furnace_array.output", "Output Chest");
        add("screen.simple_sr.rail_farm.output", "Output Chest");
        add("button.simple_sr.rail_farm.on", "On");
        add("button.simple_sr.rail_farm.off", "Off");
    }
}
