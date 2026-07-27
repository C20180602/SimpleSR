package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.SlimeTowerMenu;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SlimeTowerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 54;
    private static final int DROP_INTERVAL = 20;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private long ticksUntilDrop = DROP_INTERVAL;

    public SlimeTowerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SLIME_TOWER_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.slime_tower"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new SlimeTowerMenu(id, inventory, this); }
    public boolean isSlimeChunk() {
        if (level == null) return false;
        ChunkPos chunk = new ChunkPos(worldPosition);
        if (!(level instanceof ServerLevel serverLevel)) return false;
        long seed = serverLevel.getSeed() + (long) (chunk.x * chunk.x * 4987142)
                + (long) (chunk.x * 5947611) + (long) (chunk.z * chunk.z) * 4392871L
                + (long) (chunk.z * 389711) ^ 987234911L;
        return new LegacyRandomSource(seed).nextInt(10) == 0;
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

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putLong("ticks_until_drop", ticksUntilDrop);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); ContainerHelper.loadAllItems(tag, items); ticksUntilDrop = tag.contains("ticks_until_drop") ? tag.getLong("ticks_until_drop") : DROP_INTERVAL;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SlimeTowerBlockEntity tower) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || !tower.isSlimeChunk()) return;
        if (--tower.ticksUntilDrop <= 0) { tower.ticksUntilDrop = DROP_INTERVAL; tower.generateSlimeDrops(serverLevel); }
    }

    private void generateSlimeDrops(ServerLevel level) {
        // This entity is only a loot-table context; it is never added to the level.
        Slime slime = EntityType.SLIME.create(level);
        if (slime == null) return;
        // The vanilla slime loot table only drops slime balls for size 1.
        slime.setSize(1, true);
        slime.setPos(Vec3.atCenterOf(worldPosition));
        LootTable table = level.getServer().getLootData().getLootTable(new ResourceLocation("minecraft", "entities/slime"));
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, slime)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        java.util.List<ItemStack> drops = table.getRandomItems(params);
        NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < SLOT_COUNT; i++) backup.set(i, items.get(i).copy());
        for (ItemStack drop : drops) {
            if (!insertStack(drop.copy()).isEmpty()) {
                for (int i = 0; i < SLOT_COUNT; i++) items.set(i, backup.get(i));
                return;
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
