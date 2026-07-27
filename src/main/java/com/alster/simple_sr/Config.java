package com.alster.simple_sr;

import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = SimpleSR.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final String SLIME_TOWER_RECIPE = "{\"id\":\"slime_tower\",\"name_key\":\"recipe.simple_sr.slime_tower\",\"level\":1,\"ingredients\":[{\"item\":\"minecraft:iron_pickaxe\",\"count\":8},{\"item\":\"minecraft:torch\",\"count\":192},{\"item\":\"minecraft:water_bucket\",\"count\":2},{\"item\":\"minecraft:kelp\",\"count\":64},{\"item\":\"minecraft:cobblestone\",\"count\":576},{\"item\":\"minecraft:iron_block\",\"count\":16},{\"item\":\"minecraft:carved_pumpkin\",\"count\":4},{\"item\":\"minecraft:chest\",\"count\":2},{\"item\":\"minecraft:magma_block\",\"count\":28},{\"tag\":\"minecraft:fence_gates\",\"count\":64}],\"result\":{\"item\":\"simple_sr:slime_tower\",\"count\":1}}";
    private static final String COBBLESTONE_FARM_RECIPE = "{\"id\":\"cobblestone_farm\",\"name_key\":\"recipe.simple_sr.cobblestone_farm\",\"level\":1,\"ingredients\":[{\"tag\":\"minecraft:trapdoors\",\"count\":28},{\"item\":\"minecraft:cobblestone\",\"count\":128},{\"item\":\"minecraft:iron_trapdoor\",\"count\":1},{\"item\":\"minecraft:obsidian\",\"count\":1},{\"tag\":\"minecraft:leaves\",\"count\":48},{\"item\":\"minecraft:redstone_torch\",\"count\":1},{\"item\":\"minecraft:note_block\",\"count\":4},{\"item\":\"minecraft:chest\",\"count\":8},{\"item\":\"minecraft:redstone\",\"count\":2},{\"item\":\"minecraft:repeater\",\"count\":6},{\"item\":\"minecraft:lever\",\"count\":1},{\"item\":\"minecraft:piston\",\"count\":2},{\"item\":\"minecraft:hopper\",\"count\":20},{\"item\":\"minecraft:observer\",\"count\":4},{\"item\":\"minecraft:tnt\",\"count\":1},{\"item\":\"minecraft:water_bucket\",\"count\":2},{\"item\":\"minecraft:lava_bucket\",\"count\":4}],\"result\":{\"item\":\"simple_sr:cobblestone_farm\",\"count\":1}}";
    private static final String FURNACE_ARRAY_RECIPE = "{\"id\":\"furnace_array\",\"name_key\":\"recipe.simple_sr.furnace_array\",\"level\":1,\"ingredients\":[{\"item\":\"minecraft:cobblestone\",\"count\":192},{\"item\":\"minecraft:water_bucket\",\"count\":2},{\"item\":\"minecraft:redstone\",\"count\":32},{\"item\":\"minecraft:redstone_torch\",\"count\":10},{\"item\":\"minecraft:hopper\",\"count\":160},{\"item\":\"minecraft:furnace\",\"count\":50},{\"item\":\"minecraft:powered_rail\",\"count\":192},{\"item\":\"minecraft:rail\",\"count\":64},{\"item\":\"minecraft:lever\",\"count\":10},{\"item\":\"minecraft:chest\",\"count\":12},{\"item\":\"minecraft:oak_sign\",\"count\":10},{\"item\":\"minecraft:slime_block\",\"count\":8},{\"item\":\"minecraft:carpet\",\"count\":6},{\"item\":\"minecraft:observer\",\"count\":2},{\"item\":\"minecraft:chest_minecart\",\"count\":2},{\"item\":\"minecraft:sticky_piston\",\"count\":3},{\"item\":\"minecraft:netherrack\",\"count\":2},{\"item\":\"minecraft:flint_and_steel\",\"count\":1},{\"tag\":\"minecraft:fence_gates\",\"count\":64}],\"result\":{\"item\":\"simple_sr:furnace_array\",\"count\":1}}";
    private static final String RAIL_FARM_RECIPE = "{\"id\":\"rail_farm\",\"name_key\":\"recipe.simple_sr.rail_farm\",\"level\":1,\"ingredients\":[{\"item\":\"minecraft:observer\",\"count\":4},{\"item\":\"minecraft:piston\",\"count\":2},{\"item\":\"minecraft:dirt\",\"count\":2},{\"item\":\"minecraft:hopper\",\"count\":1},{\"item\":\"minecraft:chest\",\"count\":2}],\"result\":{\"item\":\"simple_sr:rail_farm\",\"count\":1}}";
    private static final String SWAMP_MOB_TOWER_RECIPE = "{\"id\":\"swamp_mob_tower\",\"name_key\":\"recipe.simple_sr.swamp_mob_tower\",\"level\":1,\"ingredients\":[{\"item\":\"minecraft:iron_pickaxe\",\"count\":3},{\"item\":\"minecraft:smooth_stone\",\"count\":768},{\"item\":\"minecraft:magma_block\",\"count\":48},{\"tag\":\"minecraft:fence_gates\",\"count\":32},{\"item\":\"minecraft:water_bucket\",\"count\":2},{\"item\":\"minecraft:redstone\",\"count\":256},{\"item\":\"minecraft:repeater\",\"count\":20},{\"item\":\"minecraft:redstone_torch\",\"count\":8},{\"item\":\"minecraft:armor_stand\",\"count\":1},{\"item\":\"minecraft:soul_campfire\",\"count\":22},{\"item\":\"minecraft:chest_minecart\",\"count\":2},{\"item\":\"minecraft:minecart\",\"count\":46},{\"tag\":\"minecraft:wooden_pressure_plates\",\"count\":14},{\"tag\":\"minecraft:wooden_trapdoors\",\"count\":256},{\"item\":\"minecraft:hopper\",\"count\":38},{\"item\":\"minecraft:observer\",\"count\":10},{\"item\":\"minecraft:powered_rail\",\"count\":6},{\"item\":\"minecraft:dispenser\",\"count\":3}],\"result\":{\"item\":\"simple_sr:swamp_mob_tower\",\"count\":1}}";
    public static final int MAX_MACHINE_INPUT_STACK_SIZE = 16384;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SURVIVAL_REDSTONE_RECIPES = BUILDER
            .comment("Machine trade recipes as JSON strings. Ingredients support item or tag: ingredients[{item,count}] or ingredients[{tag,count}].")
            .defineListAllowEmpty("survivalRedstoneCraftingTable.recipes", List.of("""
                    {"id":"iron_farm","name_key":"recipe.simple_sr.iron_farm","level":1,"ingredients":[{"item":"minecraft:cobblestone","count":64},{"item":"minecraft:stone_shovel","count":3},{"item":"minecraft:lava_bucket","count":1},{"item":"minecraft:water_bucket","count":2},{"item":"minecraft:barrel","count":3},{"item":"minecraft:chest","count":2},{"tag":"minecraft:beds","count":3},{"item":"minecraft:emerald","count":3},{"tag":"minecraft:signs","count":9}],"result":{"item":"simple_sr:iron_farm","count":1}}
                    """, """
                    {"id":"villager_breeder","name_key":"recipe.simple_sr.villager_breeder","level":1,"ingredients":[{"tag":"minecraft:planks","count":128},{"item":"minecraft:stone_hoe","count":1},{"item":"minecraft:water_bucket","count":2},{"item":"minecraft:composter","count":2},{"tag":"minecraft:trapdoors","count":13},{"tag":"minecraft:beds","count":5},{"item":"minecraft:torch","count":8},{"item":"minecraft:emerald","count":2},{"item":"minecraft:carrot","count":20}],"result":{"item":"simple_sr:villager_breeder","count":1}}
                    """, """
                    {"id":"villager_trading_hall","name_key":"recipe.simple_sr.villager_trading_hall","level":1,"ingredients":[{"item":"minecraft:magma_block","count":50},{"item":"minecraft:stone","count":192},{"item":"minecraft:powered_rail","count":64},{"item":"minecraft:rail","count":64},{"tag":"minecraft:trapdoors","count":50},{"item":"minecraft:minecart","count":1},{"item":"minecraft:oak_boat","count":1}],"result":{"item":"simple_sr:villager_trading_hall","count":1}}
                    """, """
                    {"id":"slime_tower","name_key":"recipe.simple_sr.slime_tower","level":1,"ingredients":[{"item":"minecraft:iron_pickaxe","count":8},{"item":"minecraft:torch","count":192},{"item":"minecraft:water_bucket","count":2},{"item":"minecraft:kelp","count":64},{"item":"minecraft:cobblestone","count":576},{"item":"minecraft:iron_block","count":16},{"item":"minecraft:carved_pumpkin","count":4},{"item":"minecraft:chest","count":2},{"item":"minecraft:magma_block","count":28},{"tag":"minecraft:fence_gates","count":64}],"result":{"item":"simple_sr:slime_tower","count":1}}
                    """, SLIME_TOWER_RECIPE, COBBLESTONE_FARM_RECIPE, FURNACE_ARRAY_RECIPE, RAIL_FARM_RECIPE, SWAMP_MOB_TOWER_RECIPE
                    ), entry -> entry instanceof String);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SURVIVAL_REDSTONE_LEVELS = BUILDER
            .comment("Survival redstone level unlocks as JSON strings. Level 1 is always available. Higher levels are true when any listed advancement is completed.")
            .defineListAllowEmpty("survivalRedstoneCraftingTable.levelUnlocks", List.of(
                    "{\"level\":1,\"advancements\":[]}",
                    "{\"level\":2,\"advancements\":[\"minecraft:story/mine_stone\"]}"
            ), entry -> entry instanceof String);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;
    public static List<SurvivalRedstoneRecipe> survivalRedstoneRecipes = List.of();
    public static List<LevelUnlock> levelUnlocks = List.of();

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                .collect(Collectors.toSet());

        List<String> recipeStrings = new ArrayList<>(SURVIVAL_REDSTONE_RECIPES.get());
        // Existing worlds keep their old TOML value; append new machine recipes once during config load.
        if (recipeStrings.stream().noneMatch(recipe -> recipe.contains("\"id\":\"slime_tower\""))) {
            recipeStrings.add(SLIME_TOWER_RECIPE);
            SURVIVAL_REDSTONE_RECIPES.set(recipeStrings);
        }
        if (recipeStrings.stream().noneMatch(recipe -> recipe.contains("\"id\":\"cobblestone_farm\""))) {
            recipeStrings.add(COBBLESTONE_FARM_RECIPE);
            SURVIVAL_REDSTONE_RECIPES.set(recipeStrings);
        }
        if (recipeStrings.stream().noneMatch(recipe -> recipe.contains("\"id\":\"furnace_array\""))) {
            recipeStrings.add(FURNACE_ARRAY_RECIPE);
            SURVIVAL_REDSTONE_RECIPES.set(recipeStrings);
        }
        if (recipeStrings.stream().noneMatch(recipe -> recipe.contains("\"id\":\"rail_farm\""))) {
            recipeStrings.add(RAIL_FARM_RECIPE);
            SURVIVAL_REDSTONE_RECIPES.set(recipeStrings);
        }
        if (recipeStrings.stream().noneMatch(recipe -> recipe.contains("\"id\":\"swamp_mob_tower\""))) {
            recipeStrings.add(SWAMP_MOB_TOWER_RECIPE);
            SURVIVAL_REDSTONE_RECIPES.set(recipeStrings);
        }
        survivalRedstoneRecipes = recipeStrings.stream()
                .map(Config::parseRecipe)
                .filter(SurvivalRedstoneRecipe::isValid)
                .toList();
        levelUnlocks = SURVIVAL_REDSTONE_LEVELS.get().stream()
                .map(Config::parseLevelUnlock)
                .toList();
    }

    private static SurvivalRedstoneRecipe parseRecipe(String value) {
        try {
            JsonObject object = JsonParser.parseString(value).getAsJsonObject();
            String id = object.get("id").getAsString();
            String nameKey = object.has("name_key") ? object.get("name_key").getAsString() : "recipe.simple_sr." + id;
            int level = object.has("level") ? object.get("level").getAsInt() : 1;

            List<SurvivalRedstoneRecipe.IngredientCount> ingredients = new ArrayList<>();
            JsonArray ingredientArray = object.getAsJsonArray("ingredients");
            for (int i = 0; i < ingredientArray.size(); i++) {
                JsonObject ingredientObject = ingredientArray.get(i).getAsJsonObject();
                int count = ingredientObject.get("count").getAsInt();
                Ingredient ingredient;
                if (ingredientObject.has("item")) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ingredientObject.get("item").getAsString()));
                    if (item == null) {
                        continue;
                    }
                    ingredient = Ingredient.of(item);
                } else if (ingredientObject.has("tag")) {
                    ingredient = Ingredient.of(ItemTags.create(new ResourceLocation(ingredientObject.get("tag").getAsString())));
                } else {
                    continue;
                }
                if (count > 0) {
                    ingredients.add(new SurvivalRedstoneRecipe.IngredientCount(ingredient, count));
                }
            }

            JsonObject resultObject = object.getAsJsonObject("result");
            Item resultItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultObject.get("item").getAsString()));
            int resultCount = resultObject.has("count") ? resultObject.get("count").getAsInt() : 1;
            ItemStack result = resultItem == null || resultCount <= 0 ? ItemStack.EMPTY : new ItemStack(resultItem, resultCount);
            return new SurvivalRedstoneRecipe(id, nameKey, level, ingredients, result);
        } catch (RuntimeException exception) {
            return new SurvivalRedstoneRecipe("invalid", "recipe.simple_sr.invalid", 1, List.of(), ItemStack.EMPTY);
        }
    }

    private static LevelUnlock parseLevelUnlock(String value) {
        try {
            JsonObject object = JsonParser.parseString(value).getAsJsonObject();
            int level = object.get("level").getAsInt();
            List<ResourceLocation> advancements = new ArrayList<>();
            JsonArray advancementArray = object.getAsJsonArray("advancements");
            for (int i = 0; i < advancementArray.size(); i++) {
                advancements.add(new ResourceLocation(advancementArray.get(i).getAsString()));
            }
            return new LevelUnlock(level, advancements);
        } catch (RuntimeException exception) {
            return new LevelUnlock(1, List.of());
        }
    }

    public record LevelUnlock(int level, List<ResourceLocation> advancements) {
    }
}
