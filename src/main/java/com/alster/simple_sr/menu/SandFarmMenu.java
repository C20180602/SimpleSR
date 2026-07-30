package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.SandFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

public class SandFarmMenu extends AbstractContainerMenu {
    private static final int SLOT_STEP = 17;
    private static final int INPUT_START = 0;
    private static final int INPUT_END = 12;
    private static final int OUTPUT_START = 12;
    private static final int OUTPUT_END = 84;
    private static final int PLAYER_START = 84;
    private final SandFarmBlockEntity farm;
    private int enabledValue;

    public SandFarmMenu(int id, Inventory inventory, SandFarmBlockEntity farm) {
        super(ModMenuTypes.SAND_FARM_MENU.get(), id);
        this.farm = farm;
        for (int col = 0; col < SandFarmBlockEntity.INPUT_SIZE; col++) {
            addSlot(new InputSlot(farm, col, 10 + col * SLOT_STEP, 9));
        }
        for (int row = 0; row < 6; row++) for (int col = 0; col < 12; col++) {
            addSlot(new Slot(farm, OUTPUT_START + col + row * 12, 10 + col * SLOT_STEP, 45 + row * SLOT_STEP));
        }
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, 9 + col + row * 9, 32 + col * SLOT_STEP, 178 + row * SLOT_STEP));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 32 + col * SLOT_STEP, 229));
        addDataSlot(new DataSlot() {
            @Override public int get() { return enabledValue; }
            @Override public void set(int value) { enabledValue = value; }
        });
        enabledValue = farm.isEnabled() ? 1 : 0;
    }

    public static SandFarmMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf data) {
        BlockEntity blockEntity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof SandFarmBlockEntity farm) return new SandFarmMenu(id, inventory, farm);
        throw new IllegalStateException("Sand farm block entity is missing");
    }

    public boolean isEnabled() { return enabledValue != 0; }
    public boolean hasInput() { return farm.hasInput(); }
    public boolean isEndDimension() { return farm.getLevel() != null && farm.getLevel().dimension() == Level.END; }

    @Override public boolean clickMenuButton(Player player, int id) {
        if (id != 0 || !hasInput()) return false;
        farm.setEnabled(!farm.isEnabled());
        enabledValue = farm.isEnabled() ? 1 : 0;
        broadcastChanges();
        return true;
    }

    @Override public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(farm.getLevel(), farm.getBlockPos()), player, ModBlocks.SAND_FARM.get());
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (index >= INPUT_START && index < INPUT_END) {
            if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY;
        } else if (index >= OUTPUT_START && index < OUTPUT_END) {
            if (!moveItemStackTo(original, PLAYER_START, slots.size(), true)) return ItemStack.EMPTY;
        } else if (index >= PLAYER_START) {
            if (!moveItemStackTo(original, INPUT_START, INPUT_END, false) && !moveItemStackTo(original, OUTPUT_START, OUTPUT_END, false)) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    private static class InputSlot extends Slot {
        private final SandFarmBlockEntity farm;
        InputSlot(SandFarmBlockEntity farm, int index, int x, int y) { super(farm, index, x, y); this.farm = farm; }
        @Override public boolean mayPlace(ItemStack stack) { return farm.isGravityBlock(stack); }
        @Override public int getMaxStackSize() { return 1; }
        @Override public int getMaxStackSize(ItemStack stack) { return 1; }
    }
}
