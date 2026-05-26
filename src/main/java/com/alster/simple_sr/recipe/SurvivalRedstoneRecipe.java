package com.alster.simple_sr.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record SurvivalRedstoneRecipe(String id, String nameKey, int level, List<IngredientCount> ingredients, ItemStack result) {
    public boolean isValid() {
        return !ingredients.isEmpty() && !result.isEmpty();
    }

    public boolean needs(Item item) {
        return ingredients.stream().anyMatch(ingredient -> ingredient.item() == item);
    }

    public int requiredCount(Item item) {
        return ingredients.stream()
                .filter(ingredient -> ingredient.item() == item)
                .mapToInt(IngredientCount::count)
                .sum();
    }

    public ItemStack icon() {
        return result.copy();
    }

    public record IngredientCount(Item item, int count) {
        public ItemStack stack() {
            return new ItemStack(item, Math.min(count, new ItemStack(item).getMaxStackSize()));
        }

        public ResourceLocation itemId() {
            return ForgeRegistries.ITEMS.getKey(item);
        }
    }
}
