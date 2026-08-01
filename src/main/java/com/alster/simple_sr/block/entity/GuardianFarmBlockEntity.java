package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.GuardianFarmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuardianFarmBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int SLOT_COUNT = 54;
    private static final int DROP_INTERVAL = 20;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private long ticksUntilDrop = DROP_INTERVAL;

    public GuardianFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.GUARDIAN_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.guardian_farm"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new GuardianFarmMenu(id, inventory, this); }
    public boolean isInsideOceanMonument() { return level instanceof ServerLevel serverLevel && serverLevel.structureManager().getStructureWithPieceAt(worldPosition, BuiltinStructures.OCEAN_MONUMENT).isValid(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    public static void tick(Level level, BlockPos pos, BlockState state, GuardianFarmBlockEntity farm) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;
        if (--farm.ticksUntilDrop <= 0) { farm.ticksUntilDrop = DROP_INTERVAL; if (farm.isInsideOceanMonument()) farm.generateDrops(serverLevel); }
    }

    private void generateDrops(ServerLevel level) {
        NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < SLOT_COUNT; i++) backup.set(i, items.get(i).copy());
        LootTable table = level.getServer().getLootData().getLootTable(new ResourceLocation("minecraft", "entities/guardian"));
        for (int i = 0; i < 5; i++) {
            Guardian guardian = EntityType.GUARDIAN.create(level);
            if (guardian == null) { restore(backup); return; }
            guardian.setPos(Vec3.atCenterOf(worldPosition));
            LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, guardian)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                    .create(LootContextParamSets.ENTITY);
            for (ItemStack drop : table.getRandomItems(params)) if (!insertStack(drop.copy()).isEmpty()) { restore(backup); return; }
        }
        setChanged();
    }

    private void restore(NonNullList<ItemStack> backup) { for (int i = 0; i < SLOT_COUNT; i++) items.set(i, backup.get(i)); }
    private ItemStack insertStack(ItemStack stack) { ItemStack remainder = stack.copy(); for (int slot = 0; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) { ItemStack stored = items.get(slot); if (stored.isEmpty()) { items.set(slot, remainder.copy()); remainder.setCount(0); } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) { int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount); } } return remainder; }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = net.minecraft.world.ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return net.minecraft.world.ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { for (int i = 0; i < SLOT_COUNT; i++) items.set(i, ItemStack.EMPTY); setChanged(); }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); net.minecraft.world.ContainerHelper.saveAllItems(tag, items); tag.putLong("ticks_until_drop", ticksUntilDrop); }
    @Override public void load(CompoundTag tag) { super.load(tag); net.minecraft.world.ContainerHelper.loadAllItems(tag, items); ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getLong("ticks_until_drop") : DROP_INTERVAL; }
}
