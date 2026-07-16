package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.VillagerBreederMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VillagerBreederBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int GENERATION_INTERVAL = 20 * 60 * 30;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private long ticksUntilGeneration = GENERATION_INTERVAL;
    private int progressTicks;

    public VillagerBreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VILLAGER_BREEDER_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simple_sr.villager_breeder");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VillagerBreederMenu(containerId, inventory, this);
    }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return items.get(0).isEmpty(); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack result = net.minecraft.world.ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return net.minecraft.world.ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.setCount(Math.min(stack.getCount(), 64));
        setChanged();
    }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.set(0, ItemStack.EMPTY); setChanged(); }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this);
    }

    public int getProgressTicks() {
        return progressTicks;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items);
        tag.putLong("ticks_until_generation", ticksUntilGeneration);
        tag.putInt("progress_ticks", progressTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        net.minecraft.world.ContainerHelper.loadAllItems(tag, items);
        ticksUntilGeneration = tag.contains("ticks_until_generation")
                ? tag.getLong("ticks_until_generation") : GENERATION_INTERVAL;
        progressTicks = tag.getInt("progress_ticks");
        if (!tag.contains("progress_ticks")) {
            progressTicks = (int) Math.max(0, Math.min(GENERATION_INTERVAL, GENERATION_INTERVAL - ticksUntilGeneration));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VillagerBreederBlockEntity breeder) {
        if (level.isClientSide) return;
        breeder.ticksUntilGeneration--;
        breeder.progressTicks = Math.min(GENERATION_INTERVAL, breeder.progressTicks + 1);
        breeder.setChanged();
        if (breeder.ticksUntilGeneration <= 0) {
            breeder.ticksUntilGeneration = GENERATION_INTERVAL;
            breeder.progressTicks = 0;
            ItemStack output = breeder.getItem(0);
            if (output.isEmpty()) {
                breeder.setItem(0, new ItemStack(Items.VILLAGER_SPAWN_EGG));
            } else if (output.is(Items.VILLAGER_SPAWN_EGG) && output.getCount() < 64) {
                output.grow(1);
                breeder.setChanged();
            }
        }
    }
}
