package com.alster.simple_sr.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Shared container tooltip formatting for the mod's machine screens. */
public abstract class TooltipContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected TooltipContainerScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        return super.getTooltipFromContainerItem(stack).stream()
                .filter(line -> !line.getString().startsWith("#") && !line.getString().startsWith("NBT"))
                .toList();
    }
}
