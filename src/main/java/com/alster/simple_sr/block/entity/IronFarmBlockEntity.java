package com.alster.simple_sr.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IronFarmBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 54;
    private static final int DROP_INTERVAL = 20 * 60;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final List<ItemStack> pendingDrops = new ArrayList<>();
    private long ticksUntilDrop = DROP_INTERVAL;

    public IronFarmBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IRON_FARM_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simple_sr.iron_farm");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return ChestMenu.sixRows(containerId, inventory, this);
    }

    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = net.minecraft.world.ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return net.minecraft.world.ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public void setChanged() { super.setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { for (int i = 0; i < items.size(); i++) items.set(i, ItemStack.EMPTY); setChanged(); }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this);
        for (ItemStack stack : pendingDrops) {
            if (!stack.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items);
        tag.putLong("ticks_until_drop", ticksUntilDrop);
        CompoundTag pending = new CompoundTag();
        for (int i = 0; i < pendingDrops.size(); i++) {
            CompoundTag stackTag = new CompoundTag();
            pendingDrops.get(i).save(stackTag);
            pending.put("stack_" + i, stackTag);
        }
        pending.putInt("size", pendingDrops.size());
        tag.put("pending_drops", pending);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        net.minecraft.world.ContainerHelper.loadAllItems(tag, items);
        ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getLong("ticks_until_drop") : DROP_INTERVAL;
        pendingDrops.clear();
        CompoundTag pending = tag.getCompound("pending_drops");
        for (int i = 0; i < pending.getInt("size"); i++) {
            pendingDrops.add(ItemStack.of(pending.getCompound("stack_" + i)));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IronFarmBlockEntity farm) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;
        farm.ticksUntilDrop--;
        if (farm.ticksUntilDrop <= 0) {
            farm.ticksUntilDrop = DROP_INTERVAL;
            farm.generateGolemDrops(serverLevel);
        }
        farm.flushPendingDrops();
    }

    private void generateGolemDrops(ServerLevel level) {
        IronGolem golem = EntityType.IRON_GOLEM.create(level);
        if (golem == null) return;
        golem.setPos(Vec3.atCenterOf(worldPosition));
        LootTable table = level.getServer().getLootData().getLootTable(new net.minecraft.resources.ResourceLocation("minecraft", "entities/iron_golem"));
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, golem)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().lava())
                .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.ENTITY);
        table.getRandomItems(params, pendingDrops::add);
        setChanged();
    }

    private void flushPendingDrops() {
        for (int i = 0; i < pendingDrops.size(); i++) {
            ItemStack pending = pendingDrops.get(i);
            if (pending.isEmpty()) continue;
            ItemStack entering = pending.copy();
            entering.setCount(Math.min(16, entering.getCount()));
            ItemStack remainder = insertStack(entering);
            pending.shrink(entering.getCount() - remainder.getCount());
            if (!remainder.isEmpty()) {
                pendingDrops.clear();
                setChanged();
                return;
            }
        }
        pendingDrops.removeIf(ItemStack::isEmpty);
        if (!pendingDrops.isEmpty()) setChanged();
    }

    private ItemStack insertStack(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < getContainerSize() && !remainder.isEmpty(); slot++) {
            ItemStack stored = getItem(slot);
            if (stored.isEmpty()) {
                setItem(slot, remainder.copy());
                remainder.setCount(0);
            } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount());
                stored.grow(amount);
                remainder.shrink(amount);
                setChanged();
            }
        }
        return remainder;
    }
}
