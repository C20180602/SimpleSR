package com.alster.simple_sr.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record SurvivalRedstoneRecipe(String id, String nameKey, int level, List<IngredientCount> ingredients, ItemStack result) {
    public boolean isValid() {
        return !ingredients.isEmpty() && !result.isEmpty();
    }

    public boolean needs(Item item) {
        return ingredients.stream().anyMatch(ingredient -> ingredient.ingredient().test(new ItemStack(item)));
    }

    public int requiredCount(Item item) {
        return ingredients.stream()
                .filter(ingredient -> ingredient.ingredient().test(new ItemStack(item)))
                .mapToInt(IngredientCount::count)
                .sum();
    }

    public ItemStack icon() {
        return result.copy();
    }

    public record IngredientCount(Ingredient ingredient, int count) {
        public ItemStack stack() {
            return stackAt(0);
        }

        public ItemStack stackAt(long index) {
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length == 0) {
                return ItemStack.EMPTY;
            }
            ItemStack displayStack = matchingStacks[(int) Math.floorMod(index, matchingStacks.length)].copy();
            displayStack.setCount(Math.min(count, displayStack.getMaxStackSize()));
            return displayStack;
        }

        public ResourceLocation itemId() {
            ItemStack[] matchingStacks = ingredient.getItems();
            return matchingStacks.length == 0 ? null : ForgeRegistries.ITEMS.getKey(matchingStacks[0].getItem());
        }

        public boolean matches(ItemStack stack) {
            return ingredient.test(stack);
        }
    }
}
