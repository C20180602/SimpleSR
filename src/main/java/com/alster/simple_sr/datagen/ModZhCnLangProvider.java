package com.alster.simple_sr.datagen;

import com.alster.simple_sr.SimpleSR;
import com.alster.simple_sr.block.ModBlocks;
import com.alster.simple_sr.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output) {
        super(output, SimpleSR.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ModItems.SURVIVAL_REDSTONE_CORE.get(), "\u751f\u7535\u6838\u5fc3");
        add(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(), "\u751f\u7535\u5de5\u4f5c\u53f0");
        add(ModBlocks.IRON_FARM.get(), "\u5237\u94c1\u673a");
        add(ModBlocks.SLIME_TOWER.get(), "\u53f2\u83b1\u59c6\u5237\u602a\u5854");
        add(ModBlocks.COBBLESTONE_FARM.get(), "\u5237\u77f3\u673a");
        add(ModBlocks.FURNACE_ARRAY.get(), "50\u500d\u901f\u7194\u7089\u7ec4");
        add(ModBlocks.RAIL_FARM.get(), "\u5237\u94c1\u8f68\u673a");
        add(ModBlocks.SAND_FARM.get(), "\u5237\u6c99\u673a");
        add(ModBlocks.SOLIDIFIER.get(), "\u56fa\u5316\u673a");
        add(ModBlocks.SHULKER_FARM.get(), "\u6f5c\u5f71\u8d1d\u519c\u573a");
        add(ModBlocks.BAMBOO_FARM.get(), "\u7518\u8517\u7af9\u5b50\u673a");
        add(ModBlocks.PACKER.get(), "\u6253\u5305\u673a");
        add(ModBlocks.SWAMP_MOB_TOWER.get(), "\u6cbc\u6cfd\u5237\u602a\u5854");
        add(ModBlocks.HONEY_FARM.get(), "\u871c\u8702\u519c\u573a");
        add(ModBlocks.VILLAGER_BREEDER.get(), "\u6751\u6c11\u7e41\u6b96\u673a");
        add(ModBlocks.VILLAGER_TRADING_HALL.get(), "\u6751\u6c11\u4ea4\u6613\u6240");
        add("itemGroup.simple_sr_tab", "\u7b80\u6613\u751f\u7535");
        add("container.simple_sr.iron_farm", "\u5237\u94c1\u673a");
        add("container.simple_sr.slime_tower", "\u53f2\u83b1\u59c6\u5237\u602a\u5854");
        add("container.simple_sr.cobblestone_farm", "\u5237\u77f3\u673a");
        add("container.simple_sr.furnace_array", "50\u500d\u901f\u7194\u7089\u7ec4");
        add("container.simple_sr.rail_farm", "\u5237\u94c1\u8f68\u673a");
        add("container.simple_sr.sand_farm", "\u5237\u6c99\u673a");
        add("container.simple_sr.solidifier", "\u56fa\u5316\u673a");
        add("container.simple_sr.shulker_farm", "\u6f5c\u5f71\u8d1d\u519c\u573a");
        add("container.simple_sr.bamboo_farm", "\u7518\u8517\u7af9\u5b50\u673a");
        add("container.simple_sr.packer", "\u6253\u5305\u673a");
        add("container.simple_sr.swamp_mob_tower", "\u6cbc\u6cfd\u5237\u602a\u5854");
        add("container.simple_sr.honey_farm", "\u871c\u8702\u519c\u573a");
        add("status.simple_sr.slime_tower.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.slime_tower.not_running", "\u975e\u53f2\u83b1\u59c6\u533a\u5757\uff0c\u4e0d\u8fd0\u884c");
        add("container.simple_sr.villager_breeder", "\u6751\u6c11\u7e41\u6b96\u673a");
        add("container.simple_sr.villager_trading_hall", "\u6751\u6c11\u4ea4\u6613\u6240");
        add("status.simple_sr.swamp_mob_tower.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.swamp_mob_tower.not_running", "\u975e\u6c90\u6cfd\u7fa4\u7cfb\uff0c\u4e0d\u8fd0\u884c");
        add("button.simple_sr.submit", "\u63d0\u4ea4");
        add("button.simple_sr.refresh", "\u5237\u65b0\u4ea4\u6613");
        add("button.simple_sr.sort_number", "\u7f16\u53f7");
        add("button.simple_sr.sort_profession", "\u804c\u4e1a");
        add("screen.simple_sr.trades", "\u4ea4\u6613");
        add("recipe.simple_sr.villager_trading_hall", "\u6751\u6c11\u4ea4\u6613\u6240");
        add("recipe.simple_sr.iron_farm", "\u5237\u94c1\u673a");
        add("recipe.simple_sr.villager_breeder", "\u6751\u6c11\u7e41\u6b96\u673a");
        add("recipe.simple_sr.slime_tower", "\u53f2\u83b1\u59c6\u5237\u602a\u5854");
        add("recipe.simple_sr.cobblestone_farm", "\u5237\u77f3\u673a");
        add("recipe.simple_sr.furnace_array", "50\u500d\u901f\u7194\u7089\u7ec4");
        add("recipe.simple_sr.rail_farm", "\u5237\u94c1\u8f68\u673a");
        add("recipe.simple_sr.sand_farm", "\u5237\u6c99\u673a");
        add("recipe.simple_sr.solidifier", "\u56fa\u5316\u673a");
        add("recipe.simple_sr.shulker_farm", "\u6f5c\u5f71\u8d1d\u519c\u573a");
        add("recipe.simple_sr.bamboo_farm", "\u7518\u8517\u7af9\u5b50\u673a");
        add("recipe.simple_sr.packer", "\u6253\u5305\u673a");
        add("screen.simple_sr.packer.inventory", "\u6253\u5305\u673a\u7269\u54c1\u680f");
        add("screen.simple_sr.packer.backpack", "\u80cc\u5305");
        add("status.simple_sr.packer.material_shortage", "\u5236\u4f5c\u6f5c\u5f71\u76d2\u6750\u6599\u4e0d\u8db3");
        add("status.simple_sr.packer.top_empty", "\u4e0a\u65b9\u5bb9\u5668\u65e0\u7269\u54c1\u53ef\u4ee5\u53d6\u51fa");
        add("button.simple_sr.bamboo_farm.sugar_cane", "\u7518\u8517");
        add("button.simple_sr.bamboo_farm.bamboo", "\u7af9\u5b50");
        add("status.simple_sr.shulker_farm.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.shulker_farm.not_running", "\u975e\u672b\u5730\u7ef4\u5ea6\uff0c\u4e0d\u8fd0\u884c");
        add("screen.simple_sr.solidifier.input", "\u8f93\u5165\u7bb1");
        add("screen.simple_sr.solidifier.output", "\u4ea7\u7269\u7bb1");
        add("status.simple_sr.sand_farm.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.sand_farm.not_running", "\u975e\u672b\u5730\u7ef4\u5ea6\u9644\u8fd1\uff0c\u4e0d\u8fd0\u884c");
        add("status.simple_sr.solidifier.running", "\u6b63\u5e38\u8fd0\u884c");
        add("status.simple_sr.solidifier.not_running", "\u975e\u672b\u5730\u7ef4\u5ea6\u9644\u8fd1\uff0c\u4e0d\u8fd0\u884c");
        add("screen.simple_sr.sand_farm.input", "\u8f93\u5165\u69fd");
        add("screen.simple_sr.sand_farm.output", "\u4ea7\u7269\u7bb1");
        add("button.simple_sr.sand_farm.on", "\u5f00");
        add("button.simple_sr.sand_farm.off", "\u5173");
        add("recipe.simple_sr.swamp_mob_tower", "\u6cbc\u6cfd\u5237\u602a\u5854");
        add("recipe.simple_sr.honey_farm", "\u871c\u8702\u519c\u573a");
        add("screen.simple_sr.honey_farm.input", "\u8f93\u5165\u7bb1");
        add("screen.simple_sr.honey_farm.output", "\u8f93\u51fa\u7bb1");
        add("screen.simple_sr.furnace_array.input", "\u8f93\u5165\u7bb1");
        add("screen.simple_sr.furnace_array.output", "\u8f93\u51fa\u7bb1");
        add("screen.simple_sr.rail_farm.output", "\u4ea7\u7269\u7bb1");
        add("button.simple_sr.rail_farm.on", "\u5f00");
        add("button.simple_sr.rail_farm.off", "\u5173");
    }
}
