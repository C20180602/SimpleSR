package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.block.PackerBlock;
import com.alster.simple_sr.menu.PackerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PackerBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int SLOT_COUNT = 27;
    private static final int SHULKER_SLOT_COUNT = 27;
    private static final int TRANSFER_INTERVAL = 8;
    private static final int ITEMS_PER_TRANSFER = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int transferTimer;

    public PackerBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.PACKER_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.packer"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new PackerMenu(id, inventory, this); }
    @Override public int getContainerSize() { return SLOT_COUNT; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(items, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); stack.setCount(Math.min(stack.getCount(), getMaxStackSize())); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.replaceAll(stack -> ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }

    public static void tick(Level level, BlockPos pos, BlockState state, PackerBlockEntity packer) {
        if (level.isClientSide) return;
        if (++packer.transferTimer >= TRANSFER_INTERVAL) {
            packer.transferTimer = 0;
            packer.packItems();
        }
    }

    public boolean materialsShortage() {
        return backCountItem(Items.SHULKER_SHELL) < 2 || backCountItem(Items.CHEST) < 1;
    }

    public boolean hasExtractableTopItem() {
        Container source = getTopContainer();
        if (source == null) return false;
        if (source instanceof WorldlyContainer sided) {
            for (int slot : sided.getSlotsForFace(Direction.DOWN)) {
                ItemStack stack = source.getItem(slot);
                if (!stack.isEmpty() && sided.canTakeItemThroughFace(slot, stack, Direction.DOWN)) return true;
            }
            return false;
        }
        for (int slot = 0; slot < source.getContainerSize(); slot++) if (!source.getItem(slot).isEmpty()) return true;
        return false;
    }

    private void packItems() {
        if (materialsShortage()) return;
        if (!hasExtractableTopItem()) return;
        int boxSlot = findNonFullShulkerBox();
        if (boxSlot < 0) {
            boxSlot = findEmptySlot();
            if (boxSlot < 0 || !consumeMaterials()) return;
            items.set(boxSlot, new ItemStack(Items.SHULKER_BOX));
        }

        for (int i = 0; i < ITEMS_PER_TRANSFER; i++) {
            int sourceSlot = findExtractableSlotThatFits(boxSlot);
            if (sourceSlot < 0) break;
            Container source = getTopContainer();
            if (source == null) break;
            ItemStack extracted = source.removeItem(sourceSlot, 1);
            if (extracted.isEmpty()) break;
            if (insertIntoShulker(boxSlot, extracted)) source.setChanged();
            else break;
        }
        setChanged();
    }

    private int findExtractableSlotThatFits(int boxSlot) {
        Container source = getTopContainer();
        if (source == null) return -1;
        if (source instanceof WorldlyContainer sided) {
            for (int slot : sided.getSlotsForFace(Direction.DOWN)) {
                ItemStack stack = source.getItem(slot);
                if (!stack.isEmpty() && sided.canTakeItemThroughFace(slot, stack, Direction.DOWN) && canInsertIntoShulker(boxSlot, stack)) return slot;
            }
            return -1;
        }
        for (int slot = 0; slot < source.getContainerSize(); slot++) if (canInsertIntoShulker(boxSlot, source.getItem(slot))) return slot;
        return -1;
    }

    @Nullable private Container getTopContainer() {
        if (level == null) return null;
        return HopperBlockEntity.getContainerAt(level, worldPosition.above());
    }

    private int backCountItem(net.minecraft.world.item.Item item) {
        Container source = getBackContainer();
        if (source == null) return 0;
        int count = 0;
        for (int slot = 0; slot < source.getContainerSize(); slot++) if (source.getItem(slot).is(item)) count += source.getItem(slot).getCount();
        return count;
    }

    @Nullable private Container getBackContainer() {
        if (level == null) return null;
        Direction facing = getBlockState().getValue(PackerBlock.FACING);
        return HopperBlockEntity.getContainerAt(level, worldPosition.relative(facing.getOpposite()));
    }

    private boolean consumeMaterials() {
        Container source = getBackContainer();
        if (source == null || backCountItem(Items.SHULKER_SHELL) < 2 || backCountItem(Items.CHEST) < 1) return false;
        removeItemFromContainer(source, Items.SHULKER_SHELL, 2);
        removeItemFromContainer(source, Items.CHEST, 1);
        source.setChanged();
        return true;
    }

    private static void removeItemFromContainer(Container source, net.minecraft.world.item.Item item, int amount) {
        for (int slot = 0; slot < source.getContainerSize() && amount > 0; slot++) if (source.getItem(slot).is(item)) {
            int removed = Math.min(amount, source.getItem(slot).getCount());
            source.removeItem(slot, removed);
            amount -= removed;
        }
    }

    private int findNonFullShulkerBox() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) if (isShulkerBox(items.get(slot)) && !isShulkerFull(items.get(slot))) return slot;
        return -1;
    }
    private int findEmptySlot() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) if (items.get(slot).isEmpty()) return slot;
        return -1;
    }
    private static boolean isShulkerBox(ItemStack stack) { return stack.is(Items.SHULKER_BOX); }

    private static boolean isShulkerFull(ItemStack box) {
        NonNullList<ItemStack> contents = readShulkerContents(box);
        for (ItemStack stack : contents) if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) return false;
        return true;
    }

    private boolean canInsertIntoShulker(int boxSlot, ItemStack source) {
        return canInsertIntoShulkerContents(readShulkerContents(items.get(boxSlot)), source);
    }

    private boolean insertIntoShulker(int boxSlot, ItemStack source) {
        ItemStack box = items.get(boxSlot);
        NonNullList<ItemStack> contents = readShulkerContents(box);
        if (!canInsertIntoShulkerContents(contents, source)) return false;
        insertIntoShulkerContents(contents, source);
        writeShulkerContents(box, contents);
        return true;
    }

    private static boolean canInsertIntoShulkerContents(NonNullList<ItemStack> contents, ItemStack source) {
        for (ItemStack stored : contents) if (!stored.isEmpty() && ItemStack.isSameItemSameTags(stored, source) && stored.getCount() < stored.getMaxStackSize()) return true;
        for (ItemStack stored : contents) if (stored.isEmpty()) return true;
        return false;
    }

    private static void insertIntoShulkerContents(NonNullList<ItemStack> contents, ItemStack source) {
        ItemStack remainder = source.copy();
        for (ItemStack stored : contents) if (!stored.isEmpty() && ItemStack.isSameItemSameTags(stored, remainder) && stored.getCount() < stored.getMaxStackSize()) {
            int amount = Math.min(remainder.getCount(), stored.getMaxStackSize() - stored.getCount());
            stored.grow(amount); remainder.shrink(amount);
        }
        for (int i = 0; i < contents.size() && !remainder.isEmpty(); i++) if (contents.get(i).isEmpty()) {
            contents.set(i, remainder.copy()); remainder.setCount(0);
        }
    }

    private static NonNullList<ItemStack> readShulkerContents(ItemStack box) {
        NonNullList<ItemStack> contents = NonNullList.withSize(SHULKER_SLOT_COUNT, ItemStack.EMPTY);
        CompoundTag blockEntityTag = box.getTagElement("BlockEntityTag");
        if (blockEntityTag == null || !blockEntityTag.contains("Items", 9)) return contents;
        ListTag list = blockEntityTag.getList("Items", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < contents.size()) contents.set(slot, ItemStack.of(itemTag));
        }
        return contents;
    }

    private static void writeShulkerContents(ItemStack box, NonNullList<ItemStack> contents) {
        CompoundTag blockEntityTag = box.getOrCreateTagElement("BlockEntityTag");
        ListTag list = new ListTag();
        for (int i = 0; i < contents.size(); i++) if (!contents.get(i).isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putByte("Slot", (byte)i);
            contents.get(i).save(itemTag);
            list.add(itemTag);
        }
        blockEntityTag.put("Items", list);
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, items); tag.putInt("transfer_timer", transferTimer);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag); ContainerHelper.loadAllItems(tag, items); transferTimer = tag.getInt("transfer_timer");
    }
}
