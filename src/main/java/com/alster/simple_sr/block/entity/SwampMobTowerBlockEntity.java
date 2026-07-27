package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.SwampMobTowerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SwampMobTowerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 54;
    private static final int DROP_INTERVAL = 20;
    private static final List<EntityType<? extends Entity>> MOB_TYPES = List.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
            EntityType.CREEPER, EntityType.WITCH, EntityType.SLIME);
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int ticksUntilDrop = DROP_INTERVAL;

    public SwampMobTowerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SWAMP_MOB_TOWER_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.swamp_mob_tower"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new SwampMobTowerMenu(id, inventory, this); }
    public boolean isSwampBiome() { return level != null && level.getBiome(worldPosition).is(Biomes.SWAMP); }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putInt("ticks_until_drop", ticksUntilDrop); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, items); ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getInt("ticks_until_drop") : DROP_INTERVAL; }

    public static void tick(Level level, BlockPos pos, BlockState state, SwampMobTowerBlockEntity tower) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || !tower.isSwampBiome()) return;
        if (--tower.ticksUntilDrop <= 0) { tower.ticksUntilDrop = DROP_INTERVAL; tower.generateDrops(serverLevel); }
    }

    private void generateDrops(ServerLevel level) {
        for (int i = 0; i < 5; i++) {
            EntityType<? extends Entity> type = MOB_TYPES.get(level.random.nextInt(MOB_TYPES.size()));
            Entity entity = type.create(level);
            if (entity == null) continue;
            if (entity instanceof Slime slime) slime.setSize(1, true);
            entity.setPos(Vec3.atCenterOf(worldPosition));
            ResourceLocation entityId = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (entityId == null) continue;
            LootTable table = level.getServer().getLootData().getLootTable(new ResourceLocation(entityId.getNamespace(), "entities/" + entityId.getPath()));
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, entity)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                    .create(LootContextParamSets.ENTITY);
            NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
            for (int slot = 0; slot < SLOT_COUNT; slot++) backup.set(slot, items.get(slot).copy());
            for (ItemStack drop : table.getRandomItems(params)) {
                if (!insertStack(drop.copy()).isEmpty()) {
                    for (int slot = 0; slot < SLOT_COUNT; slot++) items.set(slot, backup.get(slot));
                    break;
                }
            }
        }
        setChanged();
    }
    private ItemStack insertStack(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) {
            ItemStack stored = items.get(slot);
            if (stored.isEmpty()) { items.set(slot, remainder.copy()); remainder.setCount(0); }
            else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount);
            }
        }
        return remainder;
    }
}
