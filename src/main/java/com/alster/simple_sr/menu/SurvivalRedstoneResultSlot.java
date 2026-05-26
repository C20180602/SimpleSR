package com.alster.simple_sr.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SurvivalRedstoneResultSlot extends Slot {
    private final SurvivalRedstoneCraftingTableMenu menu;

    public SurvivalRedstoneResultSlot(SurvivalRedstoneCraftingTableMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return menu.hasCompleteInputs();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        menu.consumeInputs();
        super.onTake(player, stack);
    }
}
