package com.alster.simple_sr.menu;

import com.alster.simple_sr.Config;
import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SurvivalRedstoneInputSlot extends SlotItemHandler {
    private final SurvivalRedstoneCraftingTableMenu menu;

    public SurvivalRedstoneInputSlot(SurvivalRedstoneCraftingTableMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        SurvivalRedstoneRecipe recipe = menu.getSelectedRecipe();
        if (recipe == null || !recipe.needs(stack.getItem())) {
            return false;
        }
        int required = recipe.requiredCount(stack.getItem());
        int stored = menu.countStored(stack.getItem());
        return stored < required;
    }

    @Override
    public int getMaxStackSize() {
        return Config.MAX_MACHINE_INPUT_STACK_SIZE;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        SurvivalRedstoneRecipe recipe = menu.getSelectedRecipe();
        if (recipe == null) {
            return 0;
        }
        int remaining = recipe.requiredCount(stack.getItem()) - menu.countStored(stack.getItem());
        ItemStack current = getItem();
        if (!current.isEmpty() && ItemStack.isSameItemSameTags(current, stack)) {
            remaining += current.getCount();
        }
        return Math.max(0, Math.min(Config.MAX_MACHINE_INPUT_STACK_SIZE, remaining));
    }
}
