package com.alster.simple_sr.menu;

import com.alster.simple_sr.Config;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SurvivalRedstoneInputSlot extends SlotItemHandler {
    private final SurvivalRedstoneCraftingTableMenu menu;
    private final int inputSlot;

    public SurvivalRedstoneInputSlot(SurvivalRedstoneCraftingTableMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
        this.inputSlot = index;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return menu.canPlaceInInputSlot(inputSlot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return Config.MAX_MACHINE_INPUT_STACK_SIZE;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return menu.getInputSlotStackLimit(inputSlot, stack);
    }
}
