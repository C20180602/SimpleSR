package com.alster.simple_sr.block.entity;

import com.alster.simple_sr.menu.VillagerTradingHallMenu;
import com.alster.simple_sr.block.VillagerTradingHallBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VillagerTradingHallBlockEntity extends BlockEntity implements Container, MenuProvider {
    private final NonNullList<ItemStack> inputs = NonNullList.withSize(2, ItemStack.EMPTY);
    private final List<VillagerRecord> villagers = new ArrayList<>();

    public VillagerTradingHallBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.VILLAGER_TRADING_HALL_BE.get(), pos, state); }
    @Override public Component getDisplayName() { return Component.translatable("container.simple_sr.villager_trading_hall"); }
    @Nullable @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) { return new VillagerTradingHallMenu(id, inv, this); }
    @Override public int getContainerSize() { return 2; }
    @Override public boolean isEmpty() { return inputs.get(0).isEmpty() && inputs.get(1).isEmpty(); }
    @Override public ItemStack getItem(int slot) { return inputs.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(inputs, slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { ItemStack result = ContainerHelper.takeItem(inputs, slot); setChanged(); return result; }
    @Override public void setItem(int slot, ItemStack stack) {
        ItemStack stored = stack.copy();
        stored.setCount(Math.min(stored.getMaxStackSize(), stored.getCount()));
        inputs.set(slot, stored);
        setChanged();
    }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { inputs.set(0, ItemStack.EMPTY); inputs.set(1, ItemStack.EMPTY); setChanged(); }
    public void dropContents(Level level, BlockPos pos) { Containers.dropContents(level, pos, this); }
    public List<VillagerRecord> getVillagers() { return villagers; }
    public boolean removeVillager(int index) { if (index < 0 || index >= villagers.size()) return false; villagers.remove(index); setChanged(); syncClient(); return true; }
    public CompoundTag saveForItem() { CompoundTag tag = new CompoundTag(); saveAdditional(tag); return tag; }
    public static boolean isWorkstation(ItemStack stack) { return professionFor(BuiltInRegistries.BLOCK.getKey(Block.byItem(stack.getItem()))) != null; }
    public void refresh(int index) {
        if (index < 0 || index >= villagers.size() || level == null) return;
        VillagerRecord old = villagers.get(index);
        MerchantOffers refreshed = new MerchantOffers();
        for (int level = 1; level <= old.level; level++) refreshed.addAll(generateOffers(old.profession, level));
        old.offers.clear(); old.offers.addAll(refreshed); old.xp = 0; setChanged(); syncClient();
    }
    public boolean submit() {
        if (villagers.size() >= 50) return false;
        if (!inputs.get(0).is(Items.VILLAGER_SPAWN_EGG) || inputs.get(1).isEmpty()) return false;
        VillagerProfession profession = professionFor(BuiltInRegistries.BLOCK.getKey(Block.byItem(inputs.get(1).getItem())));
        if (profession == null || profession == VillagerProfession.NONE) return false;
        MerchantOffers offers = generateOffers(profession, 1);
        // Store the required Chinese default name independently of console/source encoding.
        String defaultName = "\u6751\u6c11" + (villagers.size() + 1);
        villagers.add(new VillagerRecord("村民" + (villagers.size() + 1), profession, offers, 0));
        villagers.get(villagers.size() - 1).name = defaultName;
        inputs.get(0).shrink(1); inputs.get(1).shrink(1); setChanged(); syncClient(); return true;
    }
    private void syncClient() {
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    public int recordTrade(MerchantOffer offer) {
        for (VillagerRecord record : villagers) {
            if (record.offers.stream().anyMatch(existing -> existing == offer)) {
                record.xp += offer.getXp();
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    net.minecraft.core.Direction front = getBlockState().getValue(VillagerTradingHallBlock.FACING);
                    net.minecraft.world.phys.Vec3 center = net.minecraft.world.phys.Vec3.atCenterOf(worldPosition)
                            .add(front.getStepX() * 0.55D, 0.0D, front.getStepZ() * 0.55D);
                    ExperienceOrb.award(serverLevel, center, offer.getXp());
                }
                syncClient();
                return record.xp;
            }
        }
        return 0;
    }
    public void trade(int index, MerchantOffer offer, Player player) {
        if (index < 0 || index >= villagers.size()) return;
        VillagerRecord record = villagers.get(index);
        if (!record.offers.contains(offer)) return;
        offer.increaseUses();
        record.xp += offer.getXp();
        int previousLevel = record.level;
        while (record.level < 5 && record.xp >= levelThreshold(record.level + 1)) record.level++;
        for (int level = previousLevel + 1; level <= record.level; level++) appendOffers(record, level);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.minecraft.core.Direction front = getBlockState().getValue(VillagerTradingHallBlock.FACING);
            ExperienceOrb.award(serverLevel, net.minecraft.world.phys.Vec3.atCenterOf(worldPosition).add(front.getStepX() * .55D, 0, front.getStepZ() * .55D), offer.getXp());
        }
        setChanged();
    }
    private int levelThreshold(int level) { return switch (level) { case 2 -> 10; case 3 -> 70; case 4 -> 150; default -> 250; }; }
    private void appendOffers(VillagerRecord record, int level) { record.offers.addAll(generateOffers(record.profession, level)); }
    private MerchantOffers generateOffers(VillagerProfession profession, int level) {
        StoredVillager villager = new StoredVillager(this.level);
        villager.setVillagerData(new VillagerData(VillagerType.PLAINS, profession, level));
        villager.regenerate();
        return new MerchantOffers(villager.getOffers().createTag());
    }
    private static VillagerProfession professionFor(ResourceLocation id) {
        if (id == null) return null;
        return switch (id.getPath()) {
            case "lectern" -> VillagerProfession.LIBRARIAN; case "composter" -> VillagerProfession.FARMER;
            case "blast_furnace" -> VillagerProfession.ARMORER; case "smoker" -> VillagerProfession.BUTCHER;
            case "cartography_table" -> VillagerProfession.CARTOGRAPHER; case "brewing_stand" -> VillagerProfession.CLERIC;
            case "barrel" -> VillagerProfession.FISHERMAN; case "fletching_table" -> VillagerProfession.FLETCHER;
            case "cauldron" -> VillagerProfession.LEATHERWORKER; case "stonecutter" -> VillagerProfession.MASON;
            case "smithing_table" -> VillagerProfession.TOOLSMITH; case "grindstone" -> VillagerProfession.WEAPONSMITH;
            default -> null;
        };
    }
    public void sort(boolean profession) { villagers.sort(profession ? Comparator.comparing(v -> BuiltInRegistries.VILLAGER_PROFESSION.getKey(v.profession).getPath()) : Comparator.comparing(v -> v.name)); setChanged(); syncClient(); }
    public void rename(int index, String name) { if (index >= 0 && index < villagers.size()) { villagers.get(index).name = name.substring(0, Math.min(10, name.length())); setChanged(); syncClient(); } }
    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); ContainerHelper.saveAllItems(tag, inputs); ListTag list = new ListTag(); for (VillagerRecord v : villagers) { CompoundTag t = new CompoundTag(); t.putString("Name", v.name); t.putString("Profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey(v.profession).toString()); t.put("Offers", v.offers.createTag()); t.putInt("Xp", v.xp); t.putInt("Level", v.level); list.add(t); } tag.put("Villagers", list); }
    @Override public void load(CompoundTag tag) { super.load(tag); ContainerHelper.loadAllItems(tag, inputs); villagers.clear(); ListTag list = tag.getList("Villagers", 10); for (int i=0;i<list.size();i++) { CompoundTag t=list.getCompound(i); VillagerProfession p=BuiltInRegistries.VILLAGER_PROFESSION.get(new ResourceLocation(t.getString("Profession"))); if (p != null) { int level = t.contains("Level") ? Math.max(1, Math.min(5, t.getInt("Level"))) : 1; MerchantOffers offers = new MerchantOffers(t.getCompound("Offers")); while (offers.size() > level * 2) offers.remove(offers.size() - 1); villagers.add(new VillagerRecord(t.getString("Name"),p,offers,t.getInt("Xp"),level)); } } }
    public static class VillagerRecord { public String name; public final VillagerProfession profession; public final MerchantOffers offers; public int xp; public int level; public VillagerRecord(String n,VillagerProfession p,MerchantOffers o,int x){this(n,p,o,x,1);} public VillagerRecord(String n,VillagerProfession p,MerchantOffers o,int x,int l){name=n;profession=p;offers=o;xp=x;level=l;} }
    private static class StoredVillager extends Villager { StoredVillager(Level level) { super(EntityType.VILLAGER, level); } void regenerate() { getOffers().clear(); updateTrades(); } }
}
