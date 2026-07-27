package com.alster.simple_sr.screen;

import com.alster.simple_sr.Network;
import com.alster.simple_sr.menu.VillagerTradingHallMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VillagerTradingHallScreen extends AbstractContainerScreen<VillagerTradingHallMenu> {
    private EditBox renameBox;
    private long lastClick;
    private int villagerScroll;
    private Button[] offerButtons = new Button[6];
    private Button[] deleteButtons = new Button[5];
    private int renameIndex = -1;
    private Button refreshButton, renameConfirmButton, renameCancelButton, deleteConfirmButton, deleteCancelButton;
    private int deleteIndex = -1;
    private boolean renameDialog, deleteDialog;

    public VillagerTradingHallScreen(VillagerTradingHallMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = 370;
        imageHeight = 220;
    }
    @Override protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("button.simple_sr.submit"), b -> click(303)).bounds(leftPos + 46, topPos + 18, 45, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("button.simple_sr.sort_number"), b -> click(300)).bounds(leftPos + 8, topPos + 42, 38, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("button.simple_sr.sort_profession"), b -> click(301)).bounds(leftPos + 49, topPos + 42, 38, 18).build());
        refreshButton = addRenderableWidget(Button.builder(Component.translatable("button.simple_sr.refresh"), b -> click(302)).bounds(leftPos + 278, topPos + 16, 84, 18).build());
        for (int i = 0; i < offerButtons.length; i++) {
            final int index = i;
            offerButtons[i] = addRenderableWidget(Button.builder(Component.empty(), b -> click(200 + index)).bounds(leftPos + 104, topPos + 18 + i * 28, 78, 25).build());
        }
        for (int i = 0; i < deleteButtons.length; i++) {
            final int index = i;
            deleteButtons[i] = addRenderableWidget(Button.builder(Component.literal("×"), b -> click(400 + index)).bounds(leftPos + 70, topPos + 64 + i * 28, 18, 20).build());
        }
        renameBox = new EditBox(font, leftPos + 115, topPos + 86, 140, 18, Component.empty());
        renameBox.setMaxLength(10); renameBox.visible = false; addRenderableWidget(renameBox);
        renameConfirmButton = addRenderableWidget(Button.builder(Component.literal("\u786e\u8ba4\u6539\u540d"), b -> commitRename()).bounds(leftPos + 115, topPos + 108, 68, 18).build());
        renameCancelButton = addRenderableWidget(Button.builder(Component.literal("\u53d6\u6d88"), b -> closeRenameDialog()).bounds(leftPos + 187, topPos + 108, 68, 18).build());
        deleteConfirmButton = addRenderableWidget(Button.builder(Component.literal("\u662f"), b -> confirmDelete()).bounds(leftPos + 145, topPos + 108, 50, 18).build());
        deleteCancelButton = addRenderableWidget(Button.builder(Component.literal("\u5426"), b -> closeDeleteDialog()).bounds(leftPos + 200, topPos + 108, 50, 18).build());
        closeRenameDialog(); closeDeleteDialog();
    }
    private void click(int id) { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id); }
    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderVillagerListOnTop(g, mouseX, mouseY, partialTick);
        renderOfferTooltip(g, mouseX, mouseY);
        renderDialog(g, mouseX, mouseY, partialTick);
    }
    @Override protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xffc6c6c6);
        panel(g, leftPos + 4, topPos + 4, 92, 212);
        panel(g, leftPos + 100, topPos + 4, 84, 212);
        panel(g, leftPos + 188, topPos + 4, 174, 212);
        slots(g, leftPos + 8, topPos + 18, 2, 1);
        slots(g, leftPos + 218, topPos + 50, 2, 1);
        slots(g, leftPos + 304, topPos + 50, 1, 1);
        slots(g, leftPos + 190, topPos + 120, 9, 3);
        slots(g, leftPos + 190, topPos + 178, 9, 1);
    }
    @Override protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, title, 8, 6, 0x404040, false);
        g.drawString(font, Component.literal("\u4ea4\u6613\u9009\u62e9\u680f"), 104, 6, 0x404040, false);
        g.drawString(font, Component.literal("\u4ea4\u6613\u6267\u884c\u680f"), 192, 6, 0x404040, false);
        g.drawString(font, playerInventoryTitle, 190, 109, 0x404040, false);
        var selected = menu.getSelectedVillagerRecord();
        refreshButton.active = selected != null && selected.xp == 0;
        if (selected != null) {
            int next = nextLevelXp(selected.level);
            int previous = previousLevelXp(selected.level);
            int progress = Math.max(0, selected.xp - previous);
            int required = Math.max(1, next - previous);
            int barWidth = Math.min(68, progress * 68 / required);
            g.drawString(font, Component.literal(levelName(selected.level) + "  " + selected.xp + "/" + next), 192, 27, 0x404040, false);
            g.fill(192, 39, 260, 43, 0xff373737);
            g.fill(192, 39, 192 + barWidth, 43, 0xff55aa55);
        }
        var offers = menu.getSelectedOffers();
        for (int i = 0; i < offerButtons.length; i++) {
            offerButtons[i].visible = i < offers.size();
            if (i < offers.size()) {
                var o = offers.get(i); int y = 23 + i * 28;
                renderStack(g, o.getCostA(), 108, y); if (!o.getCostB().isEmpty()) renderStack(g, o.getCostB(), 126, y);
                renderStack(g, o.getResult(), 154, y);
                if (i == menu.getSelectedOffer()) g.fill(103, y - 4, 181, y + 23, 0x6699aaff);
            }
        }
        int visibleVillagers = Math.min(deleteButtons.length, Math.max(0, menu.getDisplayedVillagers().size() - villagerScroll));
        for (int i = visibleVillagers; i < deleteButtons.length; i++) deleteButtons[i].visible = false;
    }
    private void renderVillagerListOnTop(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int first = villagerScroll;
        g.pose().pushPose();
        g.pose().translate(leftPos, topPos, 300.0F);
        for (int row = 0; row < 5; row++) {
            int index = first + row;
            if (index >= menu.getDisplayedVillagers().size()) break;
            int y = 68 + row * 28;
            var villager = menu.getDisplayedVillagers().get(index);
            deleteButtons[row].visible = true;
            deleteButtons[row].setPosition(leftPos + 70, topPos + 64 + row * 28);
            if (index == menu.getSelectedVillager()) g.fill(8, y - 2, 68, y + 25, 0xffa0a0ff);
            g.renderItem(icon(villager.profession), 10, y + 4);
            g.drawString(font, villager.name, 30, y + 4, 0x202020, false);
            g.drawString(font, villager.profession.name(), 30, y + 15, 0x404040, false);
        }
        g.pose().popPose();
    }
    private int previousLevelXp(int level) { return switch (level) { case 2 -> 10; case 3 -> 70; case 4 -> 150; case 5 -> 250; default -> 0; }; }
    private int nextLevelXp(int level) { return switch (level) { case 1 -> 10; case 2 -> 70; case 3 -> 150; case 4 -> 250; default -> 250; }; }
    private String levelName(int level) { return switch (level) { case 1 -> "\u65b0\u624b"; case 2 -> "\u5b66\u5f92"; case 3 -> "\u8001\u624b"; case 4 -> "\u4e13\u5bb6"; case 5 -> "\u5927\u5e08"; default -> "\u65b0\u624b"; }; }
    private void renderStack(GuiGraphics g, ItemStack stack, int x, int y) {
        g.renderItem(stack, x, y);
        g.renderItemDecorations(font, stack, x, y);
    }
    private void renderOfferTooltip(GuiGraphics g, int mouseX, int mouseY) {
        var offers = menu.getSelectedOffers();
        for (int i = 0; i < Math.min(offers.size(), 6); i++) {
            int y = topPos + 23 + i * 28;
            ItemStack[] stacks = {offers.get(i).getCostA(), offers.get(i).getCostB(), offers.get(i).getResult()};
            int[] xs = {leftPos + 108, leftPos + 126, leftPos + 154};
            for (int j = 0; j < stacks.length; j++) {
                if (!stacks[j].isEmpty() && mouseX >= xs[j] && mouseX < xs[j] + 16 && mouseY >= y && mouseY < y + 16) {
                    g.renderTooltip(font, stacks[j], mouseX, mouseY);
                    return;
                }
            }
        }
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if (renameDialog || deleteDialog) return super.mouseClicked(x, y, button);
        if (x >= leftPos + 70 && x < leftPos + 90 && y >= topPos + 64 && y < topPos + 204) {
            int row = (int)((y - topPos - 64) / 28);
            int index = villagerScroll + row;
            if (row < 5 && index < menu.getDisplayedVillagers().size()) openDeleteDialog(index);
            return true;
        }
        if (x >= leftPos + 8 && x < leftPos + 70 && y >= topPos + 64 && y < topPos + 204) {
            int visible = (int)((y - topPos - 64) / 28); int index = villagerScroll + visible;
            if (visible < 5 && index < menu.getDisplayedVillagers().size()) {
                menu.selectVillager(index); click(100 + index);
                if (System.currentTimeMillis() - lastClick < 400) openRenameDialog(index);
                lastClick = System.currentTimeMillis();
                return true;
            }
        }
        return super.mouseClicked(x, y, button);
    }
    @Override public boolean mouseScrolled(double x, double y, double delta) {
        if (x >= leftPos + 8 && x < leftPos + 92 && y >= topPos + 64 && y < topPos + 212) {
            villagerScroll = Math.max(0, Math.min(Math.max(0, menu.getDisplayedVillagers().size() - 5), villagerScroll - (int)Math.signum(delta))); return true;
        }
        return super.mouseScrolled(x, y, delta);
    }
    @Override public boolean keyPressed(int key, int scan, int mods) {
        if (renameDialog && key == 257) { commitRename(); return true; }
        return super.keyPressed(key, scan, mods);
    }
    private boolean insideRenameBox(double x, double y) { return renameBox.visible && x >= renameBox.getX() && x < renameBox.getX() + renameBox.getWidth() && y >= renameBox.getY() && y < renameBox.getY() + renameBox.getHeight(); }
    private void commitRename() {
        if (!renameDialog) return;
        if (renameIndex >= 0 && renameIndex < menu.getDisplayedVillagers().size()) {
            String value = renameBox.getValue();
            Network.rename(renameIndex, value);
            menu.getDisplayedVillagers().get(renameIndex).name = value.substring(0, Math.min(10, value.length()));
        }
        closeRenameDialog();
    }
    private void openRenameDialog(int index) { closeDeleteDialog(); renameIndex = index; renameDialog = true; renameBox.setValue(menu.getDisplayedVillagers().get(index).name); renameBox.visible = true; renameBox.setFocused(true); renameConfirmButton.visible = true; renameCancelButton.visible = true; }
    private void closeRenameDialog() { renameDialog = false; renameIndex = -1; if (renameBox != null) { renameBox.visible = false; renameBox.setFocused(false); } if (renameConfirmButton != null) renameConfirmButton.visible = false; if (renameCancelButton != null) renameCancelButton.visible = false; }
    private void openDeleteDialog(int index) { closeRenameDialog(); deleteIndex = index; deleteDialog = true; deleteConfirmButton.visible = true; deleteCancelButton.visible = true; }
    private void closeDeleteDialog() { deleteDialog = false; deleteIndex = -1; if (deleteConfirmButton != null) deleteConfirmButton.visible = false; if (deleteCancelButton != null) deleteCancelButton.visible = false; }
    private void confirmDelete() { if (deleteIndex >= 0) click(400 + deleteIndex); closeDeleteDialog(); }
    private void renderDialog(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (!renameDialog && !deleteDialog) return;
        g.pose().pushPose();
        g.pose().translate(0.0F, 0.0F, 400.0F);
        g.fill(leftPos + 96, topPos + 58, leftPos + 274, topPos + 134, 0xff303030);
        g.fill(leftPos + 98, topPos + 60, leftPos + 272, topPos + 132, 0xffc6c6c6);
        if (renameDialog) {
            g.drawString(font, Component.literal("\u4fee\u6539\u6751\u6c11\u540d\u79f0"), leftPos + 115, topPos + 68, 0x202020, false);
            renameBox.render(g, mouseX, mouseY, partialTick); renameConfirmButton.render(g, mouseX, mouseY, partialTick); renameCancelButton.render(g, mouseX, mouseY, partialTick);
        } else if (deleteIndex >= 0 && deleteIndex < menu.getDisplayedVillagers().size()) {
            g.drawString(font, Component.literal("\u786e\u5b9a\u5220\u9664\u6751\u6c11" + menu.getDisplayedVillagers().get(deleteIndex).name + "\u5417"), leftPos + 105, topPos + 76, 0x202020, false);
            deleteConfirmButton.render(g, mouseX, mouseY, partialTick); deleteCancelButton.render(g, mouseX, mouseY, partialTick);
        }
        g.pose().popPose();
    }
    private void panel(GuiGraphics g, int x, int y, int w, int h) { g.fill(x,y,x+w,y+h,0xff8b8b8b); g.fill(x,y,x+w-1,y+1,0xffffffff); g.fill(x,y,x+1,y+h-1,0xffffffff); }
    private void slots(GuiGraphics g, int x, int y, int c, int r) { for(int yy=0;yy<r;yy++) for(int xx=0;xx<c;xx++){int sx=x+xx*18,sy=y+yy*18;g.fill(sx-1,sy-1,sx+17,sy+17,0xff373737);g.fill(sx,sy,sx+16,sy+16,0xff8b8b8b);}}
    private ItemStack icon(VillagerProfession p) { if(p==VillagerProfession.FARMER)return new ItemStack(Items.COMPOSTER); if(p==VillagerProfession.LIBRARIAN)return new ItemStack(Items.LECTERN); if(p==VillagerProfession.ARMORER)return new ItemStack(Items.BLAST_FURNACE); if(p==VillagerProfession.BUTCHER)return new ItemStack(Items.SMOKER); if(p==VillagerProfession.CLERIC)return new ItemStack(Items.BREWING_STAND); if(p==VillagerProfession.FISHERMAN)return new ItemStack(Items.BARREL); if(p==VillagerProfession.FLETCHER)return new ItemStack(Items.FLETCHING_TABLE); if(p==VillagerProfession.CARTOGRAPHER)return new ItemStack(Items.CARTOGRAPHY_TABLE); if(p==VillagerProfession.LEATHERWORKER)return new ItemStack(Items.CAULDRON); if(p==VillagerProfession.MASON)return new ItemStack(Items.STONECUTTER); if(p==VillagerProfession.TOOLSMITH)return new ItemStack(Items.SMITHING_TABLE); if(p==VillagerProfession.WEAPONSMITH)return new ItemStack(Items.GRINDSTONE); return new ItemStack(Items.VILLAGER_SPAWN_EGG); }
}
