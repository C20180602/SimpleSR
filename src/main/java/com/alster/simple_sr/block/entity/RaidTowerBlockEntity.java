package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.block.RaidTowerBlock;
import com.alster.simple_sr.menu.RaidTowerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RaidTowerBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int STORAGE_SLOTS = 54;
    private static final int WAVE_COUNT_LIMIT = 8;
    private final NonNullList<ItemStack> items = NonNullList.withSize(STORAGE_SLOTS, ItemStack.EMPTY);
    private final Deque<RaidUnit> queue = new ArrayDeque<>();
    @Nullable private UUID raidPlayer;
    private int currentWave;
    private int totalWaves;
    private int waveTotalRaiders;
    private int badOmenLevel;
    private int cooldownTicks;

    private enum RaiderKind {
        PILLAGER(EntityType.PILLAGER, "pillager", 5), VINDICATOR(EntityType.VINDICATOR, "vindicator", 5),
        EVOKER(EntityType.EVOKER, "evoker", 10), WITCH(EntityType.WITCH, "witch", 5), RAVAGER(EntityType.RAVAGER, "ravager", 20);
        final EntityType<? extends LivingEntity> type; final String lootName; final int experience;
        RaiderKind(EntityType<? extends LivingEntity> type, String lootName, int experience) { this.type = type; this.lootName = lootName; this.experience = experience; }
    }

    private record RaidUnit(RaiderKind rider, @Nullable RaiderKind mount) {
        String saveName() { return rider.name() + (mount == null ? "" : "+" + mount.name()); }
        static RaidUnit load(String value) {
            String[] parts = value.split("\\+", 2);
            try { return new RaidUnit(RaiderKind.valueOf(parts[0]), parts.length == 1 ? null : RaiderKind.valueOf(parts[1])); }
            catch (IllegalArgumentException exception) { return null; }
        }
    }

    public RaidTowerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.RAID_TOWER_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.raid_tower"); }
    @Nullable @Override public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new RaidTowerMenu(id, inventory, this); }
    public boolean isOceanBiome() { return level != null && level.getBiome(worldPosition).is(BiomeTags.IS_OCEAN); }
    public boolean isRaidActive() { return !queue.isEmpty(); }
    public int getCurrentWave() { return currentWave; }
    public int getTotalWaves() { return totalWaves; }
    public int getRemainingRaiders() { return queue.size(); }
    public int getWaveSize() { return queue.size(); }
    public int getWaveTotalRaiders() { return waveTotalRaiders; }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    public void handleSwordUse(Player player) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (cooldownTicks > 0) { player.sendSystemMessage(Component.literal("\u51b7\u5374\u4e2d")); return; }
        if (!isOceanBiome()) return;
        if (queue.isEmpty()) {
            MobEffectInstance omen = player.getEffect(MobEffects.BAD_OMEN);
            if (omen == null) { player.sendSystemMessage(Component.literal("不具备不祥之兆buff，机器无法运行")); return; }
            badOmenLevel = Math.min(Raid.DEFAULT_MAX_BAD_OMEN_LEVEL, omen.getAmplifier() + 1);
            raidPlayer = player.getUUID();
            player.removeEffect(MobEffects.BAD_OMEN);
            totalWaves = Math.min(WAVE_COUNT_LIMIT, baseWaves(serverLevel.getDifficulty()) + Math.max(0, badOmenLevel - 1));
            currentWave = 1;
            fillWave(serverLevel.getRandom(), currentWave, player);
            player.sendSystemMessage(Component.literal("劫掠开始"));
            setChanged();
            return;
        }
        attackFirstThree(serverLevel, player);
    }

    private int baseWaves(Difficulty difficulty) { return difficulty == Difficulty.EASY ? 3 : difficulty == Difficulty.NORMAL ? 5 : 7; }

    private void fillWave(RandomSource random, int wave, Player player) {
        Map<String, Integer> composition = new LinkedHashMap<>();
        addRange(random, RaiderKind.PILLAGER, wave, new int[][]{{4,5},{3,4},{3,4},{4,5},{4,5},{4,5},{2,3},{2,3}}, composition, "掠夺者");
        addRange(random, RaiderKind.VINDICATOR, wave, new int[][]{{0,1},{2,3},{0,1},{1,2},{4,5},{2,3},{5,6},{5,6}}, composition, "卫道士");
        addRange(random, RaiderKind.RAVAGER, wave, new int[][]{{0,0},{0,0},{1,1},{0,0},{0,0},{0,0},{0,0},{0,0}}, composition, "劫掠兽");
        addRange(random, RaiderKind.WITCH, wave, new int[][]{{0,0},{0,0},{0,1},{3,3},{0,1},{0,1},{1,2},{1,2}}, composition, "女巫");
        addRange(random, RaiderKind.EVOKER, wave, new int[][]{{0,0},{0,0},{0,0},{0,0},{1,1},{1,1},{2,2},{2,3}}, composition, "唤魔者");
        addMounted(random, RaiderKind.PILLAGER, wave, new int[][]{{0,0},{0,0},{0,0},{0,0},{1,1},{0,0},{0,0},{1,1}}, composition, "掠夺者骑着劫掠兽");
        addMounted(random, RaiderKind.VINDICATOR, wave, new int[][]{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{1,1},{1,1}}, composition, "卫道士骑着劫掠兽");
        addMounted(random, RaiderKind.EVOKER, wave, new int[][]{{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{1,1},{1,1}}, composition, "唤魔者骑着劫掠兽");
        waveTotalRaiders = queue.size();
        StringBuilder message = new StringBuilder("当前波次").append(wave).append("：");
        if (composition.isEmpty()) message.append("无怪物"); else composition.forEach((name, count) -> message.append(" ").append(name).append("×").append(count));
        player.sendSystemMessage(Component.literal(message.toString()));
    }

    private void addRange(RandomSource random, RaiderKind kind, int wave, int[][] ranges, Map<String, Integer> composition, String label) {
        int[] range = ranges[Math.min(wave, 8) - 1];
        int count = range[0] == range[1] ? range[0] : random.nextInt(range[1] - range[0] + 1) + range[0];
        for (int i = 0; i < count; i++) queue.addLast(new RaidUnit(kind, null));
        if (count > 0) composition.put(label, count);
    }

    private void addMounted(RandomSource random, RaiderKind rider, int wave, int[][] ranges, Map<String, Integer> composition, String label) {
        int[] range = ranges[Math.min(wave, 8) - 1];
        int count = range[0] == range[1] ? range[0] : random.nextInt(range[1] - range[0] + 1) + range[0];
        for (int i = 0; i < count; i++) queue.addLast(new RaidUnit(rider, RaiderKind.RAVAGER));
        if (count > 0) composition.put(label, count);
    }

    private void attackFirstThree(ServerLevel level, Player player) {
        ItemStack sword = player.getMainHandItem();
        if (!(sword.getItem() instanceof SwordItem)) return;
        NonNullList<ItemStack> backup = NonNullList.withSize(STORAGE_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < STORAGE_SLOTS; i++) backup.set(i, items.get(i).copy());
        List<RaidUnit> defeated = new ArrayList<>();
        int count = Math.min(3, queue.size());
        for (int i = 0; i < count; i++) defeated.add(queue.peekFirst());
        int experience = 0;
        for (RaidUnit unit : defeated) {
            List<RaiderKind> members = unit.mount() == null ? List.of(unit.rider()) : List.of(unit.rider(), unit.mount());
            for (RaiderKind kind : members) {
                LivingEntity raider = kind.type.create(level);
                if (raider == null) { restore(backup); return; }
                raider.setPos(Vec3.atCenterOf(worldPosition));
                if (raider instanceof Raider raidMob) raidMob.setLastHurtByPlayer(player);
                ResourceLocation lootId = new ResourceLocation("minecraft", "entities/" + kind.lootName);
                LootTable table = level.getServer().getLootData().getLootTable(lootId);
                LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, raider)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                        .withParameter(LootContextParams.DAMAGE_SOURCE, kind == RaiderKind.RAVAGER ? level.damageSources().lava() : level.damageSources().playerAttack(player))
                        .withParameter(LootContextParams.BLOCK_STATE, getBlockState()).withParameter(LootContextParams.BLOCK_ENTITY, this)
                        .withParameter(LootContextParams.TOOL, sword.copy()).withParameter(LootContextParams.EXPLOSION_RADIUS, 0.0F)
                        .withParameter(LootContextParams.KILLER_ENTITY, player).withParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                        .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).create(LootContextParamSets.ALL_PARAMS);
                for (ItemStack drop : table.getRandomItems(params)) if (!insertStack(drop.copy()).isEmpty()) { restore(backup); return; }
                experience += kind.experience;
            }
            queue.removeFirst();
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, sword) == 0) sword.hurt(1, level.getRandom(), null);
        ExperienceOrb.award(level, frontCenter(), experience);
        if (queue.isEmpty()) {
            player.sendSystemMessage(Component.literal("当前波次" + currentWave));
            if (currentWave < totalWaves) { currentWave++; fillWave(level.getRandom(), currentWave, player); }
            else finishRaid(level, player);
        }
        setChanged();
    }

    private void finishRaid(ServerLevel level, Player attacker) {
        Player target = raidPlayer == null ? attacker : level.getServer().getPlayerList().getPlayer(raidPlayer);
        if (target == null) target = attacker;
        target.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, 0));
        target.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 12000, Math.min(Raid.DEFAULT_MAX_BAD_OMEN_LEVEL - 1, badOmenLevel)));
        raidPlayer = null; currentWave = 0; totalWaves = 0; badOmenLevel = 0;
        cooldownTicks = 200;
        setChanged();
    }

    private Vec3 frontCenter() { net.minecraft.core.Direction front = getBlockState().getValue(RaidTowerBlock.FACING); return Vec3.atCenterOf(worldPosition).add(front.getStepX() * 0.56D, 0.0D, front.getStepZ() * 0.56D); }
    private void restore(NonNullList<ItemStack> backup) { for (int i = 0; i < STORAGE_SLOTS; i++) items.set(i, backup.get(i)); }
    private ItemStack insertStack(ItemStack stack) { ItemStack remainder = stack.copy(); for (int slot = 0; slot < STORAGE_SLOTS && !remainder.isEmpty(); slot++) { ItemStack stored = items.get(slot); if (stored.isEmpty()) { items.set(slot, remainder.copy()); remainder.setCount(0); } else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) { int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount()); stored.grow(amount); remainder.shrink(amount); } } return remainder; }

    @Override public int getContainerSize() { return STORAGE_SLOTS; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = net.minecraft.world.ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return net.minecraft.world.ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { for (int i = 0; i < STORAGE_SLOTS; i++) items.set(i, ItemStack.EMPTY); setChanged(); }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); net.minecraft.world.ContainerHelper.saveAllItems(tag, items); tag.putInt("current_wave", currentWave); tag.putInt("total_waves", totalWaves); tag.putInt("wave_total_raiders", waveTotalRaiders); tag.putInt("bad_omen_level", badOmenLevel); tag.putInt("cooldown_ticks", cooldownTicks);
        ListTag storedQueue = new ListTag(); for (RaidUnit unit : queue) storedQueue.add(StringTag.valueOf(unit.saveName())); tag.put("queue", storedQueue); if (raidPlayer != null) tag.putUUID("raid_player", raidPlayer);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); net.minecraft.world.ContainerHelper.loadAllItems(tag, items); currentWave = tag.getInt("current_wave"); totalWaves = tag.getInt("total_waves"); waveTotalRaiders = tag.getInt("wave_total_raiders"); badOmenLevel = tag.getInt("bad_omen_level"); cooldownTicks = tag.getInt("cooldown_ticks"); queue.clear(); ListTag storedQueue = tag.getList("queue", 8); for (int i = 0; i < storedQueue.size(); i++) { RaidUnit unit = RaidUnit.load(storedQueue.getString(i)); if (unit != null) queue.add(unit); } raidPlayer = tag.hasUUID("raid_player") ? tag.getUUID("raid_player") : null;
    }
    public static void tick(Level level, BlockPos pos, BlockState state, RaidTowerBlockEntity tower) { if (!level.isClientSide && tower.cooldownTicks > 0) { tower.cooldownTicks--; tower.setChanged(); } }
}
