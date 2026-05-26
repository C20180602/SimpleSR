package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.SurvivalRedstoneCraftingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SurvivalRedstoneCraftingTableBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOTS = 54;

    private final ItemStackHandler itemHandler = new ItemStackHandler(INPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 16384;
        }
    };
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();
    private int selectedRecipe = 0;

    public SurvivalRedstoneCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SURVIVAL_REDSTONE_CRAFTING_TABLE_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simple_sr.survival_redstone_crafting_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SurvivalRedstoneCraftingTableMenu(containerId, inventory, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public int getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(int selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        setChanged();
    }

    public boolean inputsEmpty() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void clearInputs() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("selected_recipe", selectedRecipe);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        selectedRecipe = tag.getInt("selected_recipe");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SurvivalRedstoneCraftingTableBlockEntity blockEntity) {
    }
}
