package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.block.ZombifiedPiglinTowerBlock;
import com.alster.simple_sr.menu.ZombifiedPiglinTowerMenu;
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
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.List;

public class ZombifiedPiglinTowerBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int STORAGE_SLOTS = 54;
    public static final int SWORD_SLOT = 54;
    private static final int SLOT_COUNT = 55;
    private static final int EXPERIENCE_PER_PIGLIN = 5;
    private static final long ATTACK_INTERVAL_TICKS = 12L;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private long lastAttackTick = Long.MIN_VALUE;
    @Nullable private UUID swordOwner;
    private String swordOwnerName = "";
    private int storedExperience;
    @Nullable private UUID experienceExtractor;

    public ZombifiedPiglinTowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZOMBIFIED_PIGLIN_TOWER_BE.get(), pos, state);
    }

    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.zombified_piglin_tower"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ZombifiedPiglinTowerMenu(id, inventory, this); }
    public boolean isInNetherDimension() { return level != null && level.dimension() == Level.NETHER; }
    public boolean hasSword() { return getItem(SWORD_SLOT).getItem() instanceof SwordItem; }

    public void setSwordOwner(Player player) { if (level != null && level.isClientSide) return; swordOwner = player.getUUID(); swordOwnerName = player.getGameProfile().getName(); setChanged(); }
    public void clearSwordOwner() { if (level != null && level.isClientSide) return; swordOwner = null; swordOwnerName = ""; setChanged(); }
    public String getSwordOwnerName() { return swordOwnerName; }
    public void setClientSwordOwnerName(String name) { if (level != null && level.isClientSide) swordOwnerName = name; }
    @Nullable public Player getSwordOwner(ServerLevel serverLevel) {
        return swordOwner == null ? null : serverLevel.getServer().getPlayerList().getPlayer(swordOwner);
    }

    public void generateDrops(Player player) { generateDrops(player, true); }

    private void generateDrops(Player player, boolean useHeldSword) {
        if (!(level instanceof ServerLevel serverLevel) || !isInNetherDimension() || player == null) return;
        ItemStack sword = useHeldSword && player.getMainHandItem().getItem() instanceof SwordItem
                ? player.getMainHandItem()
                : getItem(SWORD_SLOT);
        if (!(sword.getItem() instanceof SwordItem)) return;
        long gameTime = serverLevel.getGameTime();
        if (lastAttackTick != Long.MIN_VALUE && gameTime - lastAttackTick < ATTACK_INTERVAL_TICKS) return;
        lastAttackTick = gameTime;

        NonNullList<ItemStack> backup = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int slot = 0; slot < SLOT_COUNT; slot++) backup.set(slot, items.get(slot).copy());

        int piglinCount = 1 + serverLevel.getRandom().nextInt(2);
        ResourceLocation lootId = new ResourceLocation("minecraft", "entities/zombified_piglin");
        LootTable table = serverLevel.getServer().getLootData().getLootTable(lootId);
        for (int i = 0; i < piglinCount; i++) {
            ZombifiedPiglin piglin = EntityType.ZOMBIFIED_PIGLIN.create(serverLevel);
            if (piglin == null) {
                restore(backup);
                return;
            }
            piglin.setPos(Vec3.atCenterOf(worldPosition));
            piglin.setLastHurtByPlayer(player);

            LootParams.Builder paramsBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, piglin)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                    .withParameter(LootContextParams.DAMAGE_SOURCE, serverLevel.damageSources().playerAttack(player))
                    .withParameter(LootContextParams.BLOCK_STATE, getBlockState())
                    .withParameter(LootContextParams.BLOCK_ENTITY, this)
                    .withParameter(LootContextParams.TOOL, sword.copy())
                    .withParameter(LootContextParams.EXPLOSION_RADIUS, 0.0F);
            paramsBuilder.withParameter(LootContextParams.KILLER_ENTITY, player)
                    .withParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                    .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
            LootParams params = paramsBuilder.create(LootContextParamSets.ALL_PARAMS);
            for (ItemStack drop : getRandomItemsWithSword(table, params, player, sword)) {
                if (!insertStack(drop.copy()).isEmpty()) {
                    restore(backup);
                    return;
                }
            }
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, sword) == 0) {
            sword.hurt(1, serverLevel.getRandom(), null);
        }
        setChanged();
        storedExperience += piglinCount * EXPERIENCE_PER_PIGLIN;
    }

    private List<ItemStack> getRandomItemsWithSword(LootTable table, LootParams params, Player player, ItemStack sword) {
        if (player.getMainHandItem() == sword) return table.getRandomItems(params);
        ItemStack original = player.getMainHandItem().copy();
        player.setItemInHand(InteractionHand.MAIN_HAND, sword);
        try { return table.getRandomItems(params); }
        finally { player.setItemInHand(InteractionHand.MAIN_HAND, original); }
    }

    public int getStoredExperience() { return storedExperience; }
    public void startExperienceExtraction(Player player) { experienceExtractor = player.getUUID(); }
    public void stopExperienceExtraction(Player player) { if (player.getUUID().equals(experienceExtractor)) experienceExtractor = null; }
    private void extractExperience(ServerLevel level) {
        if (experienceExtractor == null || storedExperience <= 0) return;
        Player player = level.getServer().getPlayerList().getPlayer(experienceExtractor);
        if (player == null) { experienceExtractor = null; return; }
        int amount = Math.min(100, storedExperience);
        storedExperience -= amount;
        player.giveExperiencePoints(amount);
        setChanged();
    }

    private Vec3 frontCenter() {
        net.minecraft.core.Direction front = getBlockState().getValue(ZombifiedPiglinTowerBlock.FACING);
        return Vec3.atCenterOf(worldPosition).add(front.getStepX() * 0.56D, 0.0D, front.getStepZ() * 0.56D);
    }

    private void restore(NonNullList<ItemStack> backup) { for (int slot = 0; slot < SLOT_COUNT; slot++) items.set(slot, backup.get(slot)); }

    private ItemStack insertStack(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < STORAGE_SLOTS && !remainder.isEmpty(); slot++) {
            ItemStack stored = items.get(slot);
            if (stored.isEmpty()) {
                items.set(slot, remainder.copy());
                remainder.setCount(0);
            } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
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
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == SWORD_SLOT ? stack.getItem() instanceof SwordItem : slot >= 0 && slot < STORAGE_SLOTS; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putLong("last_attack_tick", lastAttackTick);
        tag.putInt("stored_experience", storedExperience);
        if (swordOwner != null) tag.putUUID("sword_owner", swordOwner);
        if (!swordOwnerName.isEmpty()) tag.putString("sword_owner_name", swordOwnerName);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        lastAttackTick = tag.contains("last_attack_tick") ? tag.getLong("last_attack_tick") : Long.MIN_VALUE;
        swordOwner = tag.hasUUID("sword_owner") ? tag.getUUID("sword_owner") : null;
        swordOwnerName = tag.getString("sword_owner_name");
        storedExperience = tag.getInt("stored_experience");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ZombifiedPiglinTowerBlockEntity tower) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            tower.extractExperience(serverLevel);
            if (level.getGameTime() % ATTACK_INTERVAL_TICKS != 0 || !tower.hasSword()) return;
            Player owner = tower.getSwordOwner(serverLevel);
            if (owner != null) tower.generateDrops(owner, false);
        }
    }
}
