package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.Config;
import com.alster.simple_sr.menu.SurvivalRedstoneCraftingTableMenu;
import com.alster.simple_sr.recipe.SurvivalRedstoneRecipe;
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
            return Config.MAX_MACHINE_INPUT_STACK_SIZE;
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return getInputSlotStackLimit(slot, stack);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return canPlaceInInputSlot(slot, stack);
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

    public boolean canPlaceInInputSlot(int slot, ItemStack stack) {
        SurvivalRedstoneRecipe recipe = getSelectedRecipeDefinition();
        if (recipe == null || stack.isEmpty() || slot < 0 || slot >= recipe.ingredients().size()) {
            return false;
        }
        SurvivalRedstoneRecipe.IngredientCount ingredient = recipe.ingredients().get(slot);
        ItemStack current = itemHandler.getStackInSlot(slot);
        return stack.is(ingredient.item())
                && (current.isEmpty() || ItemStack.isSameItemSameTags(current, stack))
                && current.getCount() < getInputSlotStackLimit(slot, stack);
    }

    public int getInputSlotStackLimit(int slot, ItemStack stack) {
        SurvivalRedstoneRecipe recipe = getSelectedRecipeDefinition();
        if (recipe == null || stack.isEmpty() || slot < 0 || slot >= recipe.ingredients().size()) {
            return 0;
        }
        SurvivalRedstoneRecipe.IngredientCount ingredient = recipe.ingredients().get(slot);
        if (!stack.is(ingredient.item())) {
            return 0;
        }

        ItemStack current = itemHandler.getStackInSlot(slot);
        if (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, stack)) {
            return 0;
        }
        return Math.max(0, Math.min(Config.MAX_MACHINE_INPUT_STACK_SIZE, ingredient.count()));
    }

    public int getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(int selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
        setChanged();
    }

    private SurvivalRedstoneRecipe getSelectedRecipeDefinition() {
        if (selectedRecipe < 0 || selectedRecipe >= Config.survivalRedstoneRecipes.size()) {
            return null;
        }
        return Config.survivalRedstoneRecipes.get(selectedRecipe);
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
