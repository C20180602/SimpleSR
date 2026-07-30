package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.ShulkerFarmMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShulkerFarmBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 54;
    private static final int DROP_INTERVAL = 100;
    private static final int LOCATION_CHECK_VERSION = 1;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private long ticksUntilDrop = DROP_INTERVAL;
    private boolean nearEndCity;
    private boolean locationChecked;

    public ShulkerFarmBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SHULKER_FARM_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.shulker_farm"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ShulkerFarmMenu(id, inventory, this); }
    public boolean isNearEndCity() { return level != null && level.dimension() == Level.END; }
    public void checkEndCity(ServerLevel level) {
        if (locationChecked) return;
        locationChecked = true;
        nearEndCity = false;
        if (level.dimension() == Level.END) {
            Holder<Structure> endCity = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.END_CITY);
            Pair<BlockPos, Holder<Structure>> nearest = level.getChunkSource().getGenerator().findNearestMapStructure(level, HolderSet.direct(endCity), worldPosition, 7, false);
            if (nearest != null) {
                BlockPos structurePos = nearest.getFirst();
                double dx = structurePos.getX() - worldPosition.getX();
                double dz = structurePos.getZ() - worldPosition.getZ();
                nearEndCity = dx * dx + dz * dz <= 100.0D * 100.0D;
            }
        }
        setChanged();
    }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putLong("ticks_until_drop", ticksUntilDrop); tag.putBoolean("near_end_city", nearEndCity); tag.putBoolean("location_checked", locationChecked); tag.putInt("location_check_version", LOCATION_CHECK_VERSION); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, items); ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getLong("ticks_until_drop") : DROP_INTERVAL; nearEndCity = tag.getBoolean("near_end_city"); locationChecked = tag.getInt("location_check_version") == LOCATION_CHECK_VERSION && tag.getBoolean("location_checked"); }
    public static void tick(Level level, BlockPos pos, BlockState state, ShulkerFarmBlockEntity farm) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || level.dimension() != Level.END) return;
        if (--farm.ticksUntilDrop <= 0) { farm.ticksUntilDrop = DROP_INTERVAL; farm.generateDrops(serverLevel); }
    }
    private void generateDrops(ServerLevel level) {
        Shulker shulker = EntityType.SHULKER.create(level);
        if (shulker == null) return;
        shulker.setPos(Vec3.atCenterOf(worldPosition));
        ResourceLocation entityId = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(EntityType.SHULKER);
        if (entityId == null) return;
        LootTable table = level.getServer().getLootData().getLootTable(new ResourceLocation(entityId.getNamespace(), "entities/" + entityId.getPath()));
        LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, shulker).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition)).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic()).create(LootContextParamSets.ENTITY);
        java.util.List<ItemStack> drops = table.getRandomItems(params);
        NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < SLOT_COUNT; i++) backup.set(i, items.get(i).copy());
        for (ItemStack drop : drops) if (!insertStack(drop.copy()).isEmpty()) { for (int i = 0; i < SLOT_COUNT; i++) items.set(i, backup.get(i)); return; }
        setChanged();
    }
    private ItemStack insertStack(ItemStack stack) { ItemStack remainder = stack.copy(); for (int slot = 0; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) { ItemStack stored = items.get(slot); if (stored.isEmpty()) { items.set(slot, remainder.copy()); remainder.setCount(0); } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) { int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount); } } return remainder; }
}
