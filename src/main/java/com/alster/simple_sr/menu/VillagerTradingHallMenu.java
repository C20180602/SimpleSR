package com.alster.simple_sr.menu;

import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.block.entity.VillagerTradingHallBlockEntity;
import com.alster.simple_sr.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillagerTradingHallMenu extends AbstractContainerMenu {
    private final VillagerTradingHallBlockEntity hall;
    private final SimpleContainer tradeInputs = new SimpleContainer(2) {
        @Override public void setChanged() { super.setChanged(); updateResult(); }
    };
    private final SimpleContainer result = new SimpleContainer(1);
    private int selectedVillager = -1;
    private int selectedOffer = -1;
    /* The screen must not read the client block entity directly: a later block update can
       replace it with an empty instance while the menu is already open. */
    private final List<VillagerTradingHallBlockEntity.VillagerRecord> displayedVillagers = new ArrayList<>();

    public VillagerTradingHallMenu(int id, Inventory inv, FriendlyByteBuf data) {
        this(id, inv, getHall(inv, data));
        net.minecraft.nbt.CompoundTag tag = data.readNbt();
        if (tag != null) hall.load(tag);
        copyVillagersForDisplay();
    }
    public VillagerTradingHallMenu(int id, Inventory inv, VillagerTradingHallBlockEntity hall) {
        super(ModMenuTypes.VILLAGER_TRADING_HALL_MENU.get(), id);
        this.hall = hall;
        copyVillagersForDisplay();
        addSlot(new Slot(hall, 0, 8, 18) { @Override public boolean mayPlace(ItemStack s) { return s.is(net.minecraft.world.item.Items.VILLAGER_SPAWN_EGG); } });
        addSlot(new Slot(hall, 1, 26, 18) { @Override public boolean mayPlace(ItemStack s) { return VillagerTradingHallBlockEntity.isWorkstation(s); } });
        addSlot(new Slot(tradeInputs, 0, 218, 50));
        addSlot(new Slot(tradeInputs, 1, 244, 50));
        addSlot(new ResultSlot(result, 0, 304, 50));
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inv, 9 + col + row * 9, 190 + col * 18, 120 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inv, col, 190 + col * 18, 178));
    }
    private static VillagerTradingHallBlockEntity getHall(Inventory inv, FriendlyByteBuf data) {
        if (inv.player.level().getBlockEntity(data.readBlockPos()) instanceof VillagerTradingHallBlockEntity hall) return hall;
        throw new IllegalStateException("Villager trading hall is missing");
    }
    public VillagerTradingHallBlockEntity getHall() { return hall; }
    public void rename(int index, String name) { hall.rename(index, name); }
    public void applyHallSync(net.minecraft.nbt.CompoundTag tag) { hall.load(tag); copyVillagersForDisplay(); if (selectedVillager >= displayedVillagers.size()) selectedVillager = -1; updateResult(); }
    public void submitFromNetwork(Player player) { if (hall.submit()) { copyVillagersForDisplay(); broadcastChanges(); } }
    public int getSelectedVillager() { return selectedVillager; }
    public int getSelectedOffer() { return selectedOffer; }
    public List<VillagerTradingHallBlockEntity.VillagerRecord> getDisplayedVillagers() { return displayedVillagers; }
    public VillagerTradingHallBlockEntity.VillagerRecord getSelectedVillagerRecord() {
        if (selectedVillager < 0) return null;
        if (hall.getLevel() != null && !hall.getLevel().isClientSide && selectedVillager < hall.getVillagers().size()) {
            return hall.getVillagers().get(selectedVillager);
        }
        return selectedVillager < displayedVillagers.size() ? displayedVillagers.get(selectedVillager) : null;
    }
    public MerchantOffers getSelectedOffers() {
        if (selectedVillager < 0) return new MerchantOffers();
        if (hall.getLevel() != null && !hall.getLevel().isClientSide && selectedVillager < hall.getVillagers().size()) {
            return hall.getVillagers().get(selectedVillager).offers;
        }
        return selectedVillager < displayedVillagers.size() ? displayedVillagers.get(selectedVillager).offers : new MerchantOffers();
    }
    public void selectVillager(int index) { if (index >= 0 && index < displayedVillagers.size()) { selectedVillager = index; selectedOffer = -1; updateResult(); } }
    public void selectOffer(int index) { if (index >= 0 && index < getSelectedOffers().size()) { selectedOffer = index; updateResult(); } }
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 100 && id < 200) { selectVillager(id - 100); return true; }
        if (id >= 200 && id < 300) { selectOffer(id - 200); return true; }
        if (id == 300) { hall.sort(false); copyVillagersForDisplay(); if (player instanceof ServerPlayer p) Network.syncTradingHall(p, hall); return true; }
        if (id == 301) { hall.sort(true); copyVillagersForDisplay(); if (player instanceof ServerPlayer p) Network.syncTradingHall(p, hall); return true; }
        if (id == 302 && selectedVillager >= 0 && selectedVillager < hall.getVillagers().size()
                && hall.getVillagers().get(selectedVillager).xp == 0) {
            hall.refresh(selectedVillager); copyVillagersForDisplay(); selectedOffer = -1; updateResult();
            if (player instanceof ServerPlayer p) {
                p.connection.send(net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(hall));
                Network.syncTradingHall(p, hall);
            }
            return true;
        }
        if (id == 303) {
            boolean submitted = hall.submit();
            if (submitted) copyVillagersForDisplay();
            if (submitted && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.connection.send(net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(hall));
                Network.syncTradingHall(serverPlayer, hall);
            }
            return submitted;
        }
        if (id >= 400 && id < 450) {
            int index = id - 400;
            if (hall.removeVillager(index)) {
                    copyVillagersForDisplay();
                    selectedVillager = -1;
                    selectedOffer = -1;
                    updateResult();
                    if (player instanceof ServerPlayer serverPlayer) Network.syncTradingHall(serverPlayer, hall);
            }
            return true;
        }
        return false;
    }
    public void updateResult() {
        ItemStack output = ItemStack.EMPTY;
        if (selectedOffer >= 0 && selectedOffer < getSelectedOffers().size()) {
            MerchantOffer offer = getSelectedOffers().get(selectedOffer);
            if (offer.satisfiedBy(tradeInputs.getItem(0), tradeInputs.getItem(1))) output = offer.getResult().copy();
        }
        result.setItem(0, output);
    }
    private void copyVillagersForDisplay() {
        displayedVillagers.clear();
        for (VillagerTradingHallBlockEntity.VillagerRecord source : hall.getVillagers()) {
            displayedVillagers.add(new VillagerTradingHallBlockEntity.VillagerRecord(
                    source.name, source.profession, new MerchantOffers(source.offers.createTag()), source.xp, source.level));
        }
    }
    private void completeTrade(Player player) {
        if (selectedVillager < 0 || selectedOffer < 0 || selectedOffer >= getSelectedOffers().size()) return;
        MerchantOffer offer = getSelectedOffers().get(selectedOffer);
        ItemStack first = tradeInputs.getItem(0), second = tradeInputs.getItem(1);
        if (!offer.satisfiedBy(first, second)) return;
        offer.take(first, second);
        tradeInputs.setItem(0, first); tradeInputs.setItem(1, second);
        hall.trade(selectedVillager, offer, player);
        updateResult();
        broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) Network.syncTradingHall(serverPlayer, hall);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index); if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack copy = slot.getItem().copy();
        if (index == 4) { if (!moveItemStackTo(slot.getItem(), 5, slots.size(), true)) return ItemStack.EMPTY; slot.onTake(player, slot.getItem()); }
        else if (index >= 5) { if (!moveItemStackTo(slot.getItem(), 2, 4, false)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(slot.getItem(), 5, slots.size(), true)) return ItemStack.EMPTY;
        if (slot.getItem().isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
    @Override public boolean stillValid(Player player) { return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(player.level(), hall.getBlockPos()), player, ModBlocks.VILLAGER_TRADING_HALL.get()); }
    private class ResultSlot extends Slot {
        ResultSlot(Container c, int index, int x, int y) { super(c, index, x, y); }
        @Override public boolean mayPlace(ItemStack stack) { return false; }
        @Override public void onTake(Player player, ItemStack stack) { completeTrade(player); }
    }
}
