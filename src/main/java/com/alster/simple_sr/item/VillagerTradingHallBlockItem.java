package com.alster.simple_sr.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VillagerTradingHallBlockItem extends BlockItem {
    public VillagerTradingHallBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(64));
    }

    private static boolean hasSavedData(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("BlockEntityTag", 10);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        return hasSavedData(stack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return hasSavedData(stack) || hasSavedData(other);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains("BlockEntityTag", 10)) return;
        ListTag villagers = root.getCompound("BlockEntityTag").getList("Villagers", 10);
        int count = Math.min(2, villagers.size());
        for (int i = 0; i < count; i++) {
            CompoundTag villager = villagers.getCompound(i);
            String name = villager.getString("Name");
            String profession = villager.getString("Profession");
            int separator = profession.indexOf(':');
            if (separator >= 0) profession = profession.substring(separator + 1);
            tooltip.add(Component.literal(name + " - " + profession));
        }
        if (villagers.size() > 2) {
            tooltip.add(Component.literal("+" + (villagers.size() - 2) + " more villagers"));
        }
    }
}
