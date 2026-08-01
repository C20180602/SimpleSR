package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.EndermanTowerMenu;
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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.EnderMan;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EndermanTowerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 54;
    private static final int ENDermen_PER_ATTACK = 3;
    private static final int EXPERIENCE_PER_ENDERMAN = 5;
    private static final long ATTACK_COOLDOWN_TICKS = 20L;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private long lastSuccessfulAttackTick = Long.MIN_VALUE;

    public EndermanTowerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.ENDERMAN_TOWER_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.enderman_tower"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new EndermanTowerMenu(id, inventory, this); }
    public boolean isInEndDimension() { return level != null && level.dimension() == Level.END; }

    public void generateDrops(Player player) {
        if (!(level instanceof ServerLevel serverLevel) || !isInEndDimension()) return;
        long gameTime = serverLevel.getGameTime();
        if (lastSuccessfulAttackTick != Long.MIN_VALUE
                && gameTime - lastSuccessfulAttackTick <= ATTACK_COOLDOWN_TICKS) return;
        lastSuccessfulAttackTick = gameTime;
        NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int slot = 0; slot < SLOT_COUNT; slot++) backup.set(slot, items.get(slot).copy());

        ResourceLocation lootId = new ResourceLocation("minecraft", "entities/enderman");
        LootTable table = serverLevel.getServer().getLootData().getLootTable(lootId);
        for (int i = 0; i < ENDermen_PER_ATTACK; i++) {
            EnderMan enderman = EntityType.ENDERMAN.create(serverLevel);
            if (enderman == null) return;
            enderman.setPos(Vec3.atCenterOf(worldPosition));
            enderman.setLastHurtByPlayer(player);
            // Mirror vanilla Player.attack: one machine hit represents one attack,
            // even though it produces the drops of three endermen.
            if (i == 0 && !player.getAbilities().instabuild) {
                player.getMainHandItem().hurtEnemy(enderman, player);
            }
            LootParams params = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, enderman)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                    .withParameter(LootContextParams.DAMAGE_SOURCE, serverLevel.damageSources().playerAttack(player))
                    .withParameter(LootContextParams.KILLER_ENTITY, player)
                    .withParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                    .withParameter(LootContextParams.BLOCK_STATE, getBlockState())
                    .withParameter(LootContextParams.BLOCK_ENTITY, this)
                    .withParameter(LootContextParams.TOOL, player.getMainHandItem().copy())
                    .withParameter(LootContextParams.EXPLOSION_RADIUS, 0.0F)
                    .create(LootContextParamSets.ALL_PARAMS);
            for (ItemStack drop : table.getRandomItems(params)) {
                if (!insertStack(drop.copy()).isEmpty()) {
                    restore(backup);
                    return;
                }
            }
        }
        setChanged();
        ExperienceOrb.award(serverLevel, frontCenter(), ENDermen_PER_ATTACK * EXPERIENCE_PER_ENDERMAN);
    }

    private Vec3 frontCenter() {
        net.minecraft.core.Direction front = getBlockState().getValue(com.alster.simple_sr.block.EndermanTowerBlock.FACING);
        return Vec3.atCenterOf(worldPosition).add(front.getStepX() * 0.56D, 0.0D, front.getStepZ() * 0.56D);
    }

    private void restore(NonNullList<ItemStack> backup) {
        for (int slot = 0; slot < SLOT_COUNT; slot++) items.set(slot, backup.get(slot));
    }

    private ItemStack insertStack(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < SLOT_COUNT && !remainder.isEmpty(); slot++) {
            ItemStack stored = items.get(slot);
            if (stored.isEmpty()) { items.set(slot, remainder.copy()); remainder.setCount(0); }
            else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount());
                stored.grow(amount);
                remainder.shrink(amount);
            }
        }
        return remainder;
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
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putLong("last_successful_attack_tick", lastSuccessfulAttackTick); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, items); lastSuccessfulAttackTick = tag.contains("last_successful_attack_tick") ? tag.getLong("last_successful_attack_tick") : Long.MIN_VALUE; }
    public static void tick(Level level, BlockPos pos, BlockState state, EndermanTowerBlockEntity tower) { }
}
