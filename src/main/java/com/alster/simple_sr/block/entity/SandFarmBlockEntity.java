package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.SandFarmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SandFarmBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int INPUT_SIZE = 12;
    public static final int OUTPUT_START = INPUT_SIZE;
    public static final int OUTPUT_SIZE = 72;
    public static final int TOTAL_SIZE = OUTPUT_START + OUTPUT_SIZE;
    private static final int DROP_INTERVAL = 20;
    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SIZE, ItemStack.EMPTY);
    private boolean enabled;
    private int ticksUntilDrop = DROP_INTERVAL;

    public SandFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SAND_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.sand_farm"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new SandFarmMenu(id, inventory, this); }
    @Override public int getContainerSize() { return TOTAL_SIZE; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) { if (slot < INPUT_SIZE) enabled = hasInput(); setChanged(); }
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(items, slot);
        if (slot < INPUT_SIZE && !result.isEmpty()) enabled = hasInput();
        return result;
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot < INPUT_SIZE) {
            if (!stack.isEmpty() && !isGravityBlock(stack)) return;
            stack = stack.copy();
            stack.setCount(Math.min(1, stack.getCount()));
            if (stack.isEmpty()) enabled = false;
        }
        items.set(slot, stack);
        setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot < INPUT_SIZE ? isGravityBlock(stack) : true; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); enabled = false; setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    public boolean isGravityBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        return blockItem.getBlock() instanceof FallingBlock;
    }
    public boolean hasInput() {
        for (int i = 0; i < INPUT_SIZE; i++) if (!items.get(i).isEmpty()) return true;
        return false;
    }
    public boolean isEnabled() { return enabled && hasInput(); }
    public void setEnabled(boolean enabled) { this.enabled = enabled && hasInput(); setChanged(); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putBoolean("enabled", enabled);
        tag.putInt("ticks_until_drop", ticksUntilDrop);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        enabled = tag.getBoolean("enabled") && hasInput();
        ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getInt("ticks_until_drop") : DROP_INTERVAL;
    }

    @Override public int[] getSlotsForFace(Direction side) {
        int[] slots = new int[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) slots[i] = OUTPUT_START + i;
        return slots;
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot >= OUTPUT_START; }

    public static void tick(Level level, BlockPos pos, BlockState state, SandFarmBlockEntity farm) {
        if (level.isClientSide || level.dimension() != Level.END || !farm.isEnabled()) return;
        if (--farm.ticksUntilDrop <= 0) {
            farm.ticksUntilDrop = DROP_INTERVAL;
            farm.copyInputs();
        }
    }

    private void copyInputs() {
        NonNullList<ItemStack> simulated = NonNullList.withSize(OUTPUT_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < OUTPUT_SIZE; i++) simulated.set(i, items.get(OUTPUT_START + i).copy());
        for (int i = 0; i < INPUT_SIZE; i++) {
            ItemStack input = items.get(i);
            if (!input.isEmpty() && !insertOutput(simulated, input.copy())) return;
        }
        for (int i = 0; i < OUTPUT_SIZE; i++) items.set(OUTPUT_START + i, simulated.get(i));
        setChanged();
    }

    private boolean insertOutput(NonNullList<ItemStack> output, ItemStack stack) {
        for (int i = 0; i < output.size() && !stack.isEmpty(); i++) {
            ItemStack stored = output.get(i);
            if (stored.isEmpty()) {
                output.set(i, stack.copy());
                stack.setCount(0);
            } else if (ItemStack.isSameItemSameTags(stored, stack) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(stack.getCount(), stored.getMaxStackSize() - stored.getCount());
                stored.grow(amount);
                stack.shrink(amount);
            }
        }
        return stack.isEmpty();
    }
}
