package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.block.PiglinTradingTowerBlock;
import com.alster.simple_sr.menu.PiglinTradingTowerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PiglinTradingTowerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int CHEST_SIZE = 54;
    public static final int OUTPUT_START = CHEST_SIZE;
    private static final int TOTAL_SIZE = CHEST_SIZE * 2;
    private static final int TRADE_INTERVAL_TICKS = 24;
    private static final ResourceLocation PIGLIN_BARTERING_LOOT_TABLE = new ResourceLocation("minecraft", "gameplay/piglin_bartering");
    private final NonNullList<ItemStack> inputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private final NonNullList<ItemStack> outputItems = NonNullList.withSize(CHEST_SIZE, ItemStack.EMPTY);
    private int tradeTicks = TRADE_INTERVAL_TICKS;

    public PiglinTradingTowerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.PIGLIN_TRADING_TOWER_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.piglin_trading_tower"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new PiglinTradingTowerMenu(id, inventory, this); }
    public boolean isInNetherDimension() { return level != null && level.dimension() == Level.NETHER; }

    @Override public int getContainerSize() { return TOTAL_SIZE; }
    @Override public boolean isEmpty() { return inputItems.stream().allMatch(ItemStack::isEmpty) && outputItems.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slot < OUTPUT_START ? inputItems.get(slot) : outputItems.get(slot - OUTPUT_START); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = slot < OUTPUT_START ? ContainerHelper.removeItem(inputItems, slot, amount) : ContainerHelper.removeItem(outputItems, slot - OUTPUT_START, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return slot < OUTPUT_START ? ContainerHelper.takeItem(inputItems, slot) : ContainerHelper.takeItem(outputItems, slot - OUTPUT_START); }
    @Override public void setItem(int slot, ItemStack stack) { if (slot < OUTPUT_START) inputItems.set(slot, stack); else outputItems.set(slot - OUTPUT_START, stack); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot < OUTPUT_START && stack.is(Items.GOLD_INGOT); }
    @Override public boolean canTakeItem(Container target, int slot, ItemStack stack) { return slot >= OUTPUT_START; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inputItems.replaceAll(stack -> ItemStack.EMPTY); outputItems.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }
    @Override public int[] getSlotsForFace(Direction side) { int[] slots = new int[TOTAL_SIZE]; for (int i = 0; i < TOTAL_SIZE; i++) slots[i] = i; return slots; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) { return slot < OUTPUT_START && stack.is(Items.GOLD_INGOT); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) { return slot >= OUTPUT_START; }

    private void trade() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        int inputSlot = findGoldSlot();
        if (inputSlot < 0) return;

        Piglin piglin = EntityType.PIGLIN.create(serverLevel);
        if (piglin == null) return;
        piglin.setPos(Vec3.atCenterOf(worldPosition));
        // Resolve the live loot table at trade time so datapacks and other mods can inject entries and weights.
        LootTable table = serverLevel.getServer().getLootData().getLootTable(PIGLIN_BARTERING_LOOT_TABLE);
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.GOLD_INGOT))
                .withParameter(LootContextParams.THIS_ENTITY, piglin)
                .withParameter(LootContextParams.BLOCK_ENTITY, this)
                .create(LootContextParamSets.PIGLIN_BARTER);

        NonNullList<ItemStack> backup = copyContents(outputItems);
        for (ItemStack drop : table.getRandomItems(params)) {
            if (!insertOutput(drop.copy()).isEmpty()) {
                restore(outputItems, backup);
                return;
            }
        }
        inputItems.get(inputSlot).shrink(1);
        setChanged();
    }

    private int findGoldSlot() {
        for (int slot = 0; slot < CHEST_SIZE; slot++) if (inputItems.get(slot).is(Items.GOLD_INGOT) && !inputItems.get(slot).isEmpty()) return slot;
        return -1;
    }

    private ItemStack insertOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < CHEST_SIZE && !remainder.isEmpty(); slot++) {
            ItemStack stored = outputItems.get(slot);
            if (stored.isEmpty()) { outputItems.set(slot, remainder.copy()); remainder.setCount(0); }
            else if (ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
                int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount());
                stored.grow(amount); remainder.shrink(amount);
            }
        }
        return remainder;
    }

    private static NonNullList<ItemStack> copyContents(NonNullList<ItemStack> source) { NonNullList<ItemStack> copy = NonNullList.withSize(source.size(), ItemStack.EMPTY); for (int i = 0; i < source.size(); i++) copy.set(i, source.get(i).copy()); return copy; }
    private static void restore(NonNullList<ItemStack> target, NonNullList<ItemStack> source) { for (int i = 0; i < target.size(); i++) target.set(i, source.get(i)); }

    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, inputItems); CompoundTag output = new CompoundTag(); ContainerHelper.saveAllItems(output, outputItems); tag.put("output", output); tag.putInt("trade_ticks", tradeTicks); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, inputItems); ContainerHelper.loadAllItems(tag.getCompound("output"), outputItems); tradeTicks = tag.contains("trade_ticks") ? tag.getInt("trade_ticks") : TRADE_INTERVAL_TICKS; }
    public static void tick(Level level, BlockPos pos, BlockState state, PiglinTradingTowerBlockEntity tower) { if (level.isClientSide || !tower.isInNetherDimension()) return; if (--tower.tradeTicks <= 0) { tower.tradeTicks = TRADE_INTERVAL_TICKS; tower.trade(); } }
}
