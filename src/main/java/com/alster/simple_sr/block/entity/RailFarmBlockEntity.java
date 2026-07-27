package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.RailFarmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RailFarmBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_START = 1;
    public static final int OUTPUT_SIZE = 54;
    private static final int TOTAL_SIZE = OUTPUT_START + OUTPUT_SIZE;
    private static final int DROP_INTERVAL = 10;
    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SIZE, ItemStack.EMPTY);
    private boolean enabled;
    private int ticksUntilDrop = DROP_INTERVAL;

    public RailFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.RAIL_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.rail_farm"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new RailFarmMenu(id, inventory, this); }
    public boolean isEnabled() { return enabled && !items.get(INPUT_SLOT).isEmpty(); }
    public void setEnabled(boolean enabled) { this.enabled = enabled && !items.get(INPUT_SLOT).isEmpty(); setChanged(); }
    public boolean isRail(ItemStack stack) { return stack.is(ItemTags.RAILS); }
    @Override public int getContainerSize() { return TOTAL_SIZE; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) { if (slot == INPUT_SLOT) enabled = false; setChanged(); } return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { ItemStack result = ContainerHelper.takeItem(items, slot); if (slot == INPUT_SLOT && !result.isEmpty()) enabled = false; return result; }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) {
            if (!stack.isEmpty() && !isRail(stack)) return;
            stack = stack.copy(); stack.setCount(Math.min(1, stack.getCount()));
            if (stack.isEmpty()) enabled = false;
        }
        items.set(slot, stack); setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == INPUT_SLOT ? isRail(stack) : false; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); enabled = false; setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putBoolean("enabled", enabled); tag.putInt("ticks_until_drop", ticksUntilDrop); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, items); enabled = tag.getBoolean("enabled") && !items.get(INPUT_SLOT).isEmpty(); ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getInt("ticks_until_drop") : DROP_INTERVAL; }

    public static void tick(Level level, BlockPos pos, BlockState state, RailFarmBlockEntity farm) {
        if (level.isClientSide || !farm.isEnabled()) return;
        if (--farm.ticksUntilDrop <= 0) { farm.ticksUntilDrop = DROP_INTERVAL; farm.copyRail(); }
    }
    private void copyRail() {
        ItemStack input = items.get(INPUT_SLOT);
        if (input.isEmpty()) { enabled = false; return; }
        ItemStack result = new ItemStack(input.getItem(), 2);
        ItemStack remainder = insertOutput(result);
        if (!remainder.isEmpty()) return;
        setChanged();
    }
    private ItemStack insertOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = OUTPUT_START; i < TOTAL_SIZE && !remainder.isEmpty(); i++) {
            ItemStack stored = items.get(i);
            if (stored.isEmpty()) { items.set(i, remainder.copy()); remainder.setCount(0); }
            else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount);
            }
        }
        return remainder;
    }
}
