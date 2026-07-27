package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.RailFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RailFarmMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_START = 1;
    private static final int OUTPUT_END = 55;
    private static final int PLAYER_START = 55;
    private final RailFarmBlockEntity farm;
    private int enabledValue;

    public RailFarmMenu(int id, Inventory inventory, RailFarmBlockEntity farm) {
        super(ModMenuTypes.RAIL_FARM_MENU.get(), id);
        this.farm = farm;
        addSlot(new InputSlot(farm, 0, 57, 9));
        for (int row = 0; row < 6; row++) for (int col = 0; col < 9; col++) addSlot(new OutputSlot(farm, 1 + col + row * 9, 14 + col * 18, 36 + row * 18));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, 9 + col + row * 9, 14 + col * 18, 158 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 14 + col * 18, 216));
        addDataSlot(new DataSlot() { @Override public int get() { return enabledValue; } @Override public void set(int value) { enabledValue = value; } });
        enabledValue = farm.isEnabled() ? 1 : 0;
    }

    public static RailFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof RailFarmBlockEntity farm) return new RailFarmMenu(id, inventory, farm);
        throw new IllegalStateException("Rail farm block entity is missing");
    }
    public boolean isEnabled() { return enabledValue != 0; }
    public boolean hasInput() { return !farm.getItem(0).isEmpty(); }
    @Override public boolean clickMenuButton(Player player, int id) {
        if (id != 0 || !hasInput()) return false;
        farm.setEnabled(!farm.isEnabled());
        enabledValue = farm.isEnabled() ? 1 : 0;
        broadcastChanges();
        return true;
    }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.RAIL_FARM.get()); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem(); ItemStack copy = original.copy();
        if (index == INPUT_SLOT) {
            if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY;
        } else if (index >= OUTPUT_START && index < OUTPUT_END) {
            if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY;
        } else if (index >= PLAYER_START) {
            if (!moveItemStackTo(original, INPUT_SLOT, OUTPUT_START, false)) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
    private static class InputSlot extends Slot {
        InputSlot(RailFarmBlockEntity farm, int index, int x, int y) { super(farm, index, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return stack.is(net.minecraft.tags.ItemTags.RAILS); }
        @Override public int getMaxStackSize() { return 1; }
        @Override public int getMaxStackSize(ItemStack stack) { return 1; }
    }
    private static class OutputSlot extends Slot {
        OutputSlot(RailFarmBlockEntity farm, int index, int x, int y) { super(farm, index, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return false; }
    }
}
