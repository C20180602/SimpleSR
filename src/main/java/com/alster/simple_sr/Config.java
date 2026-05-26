package com.alster.simple_sr;

import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
            .comment("Machine trade recipes as JSON strings. Fields: id, name_key, level, ingredients[{item,count}], result{item,count}.")
            .defineListAllowEmpty("survivalRedstoneCraftingTable.recipes", List.of("""
                    {"id":"iron_farm","name_key":"recipe.simple_sr.iron_farm","level":1,"ingredients":[{"item":"minecraft:iron_shovel","count":3},{"item":"minecraft:lava_bucket","count":1},{"item":"minecraft:water_bucket","count":2},{"item":"minecraft:oak_planks","count":128}],"result":{"item":"minecraft:dirt","count":1}}
                    """), entry -> entry instanceof String);

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

        survivalRedstoneRecipes = SURVIVAL_REDSTONE_RECIPES.get().stream()
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
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ingredientObject.get("item").getAsString()));
                int count = ingredientObject.get("count").getAsInt();
                if (item != null && count > 0) {
                    ingredients.add(new SurvivalRedstoneRecipe.IngredientCount(item, count));
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
