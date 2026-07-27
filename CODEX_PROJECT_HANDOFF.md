# SimpleSR 项目交接文档

更新时间：2026-07-25

这份文档用于帮助后续 Codex 快速理解 SimpleSR 项目的整体架构、机器实现方式以及修改时容易踩坑的地方。

## 1. 项目基础信息

- Minecraft：1.20.1
- Forge：47.4.0
- Java：17
- 映射：Parchment `2023.09.03-1.20.1`
- Mod ID：`simple_sr`
- 主包：`com.alster.simple_sr`
- 项目目标：为生存红石玩法提供简化、压缩化的机器。

主要源码目录：

```text
src/main/java/com/alster/simple_sr/
├─ block/                 方块与方块注册
├─ block/entity/          方块实体与机器运行逻辑
├─ menu/                  服务端容器菜单、槽位与快速移动
├─ screen/                客户端 GUI
├─ item/                  物品、方块物品、创造标签页
├─ datagen/               模型、语言、标签、战利品表数据生成
├─ recipe/                生电工作台配方数据结构
├─ level/                 生电等级解锁逻辑
├─ tag/                   自定义方块/物品标签
├─ Network.java           自定义网络包
├─ Config.java            Forge 配置与生电工作台机器配方
└─ SimpleSR.java          Mod 主入口与客户端菜单注册
```

资源目录：

- `src/main/resources/assets/simple_sr/`：手写纹理、固定资源。
- `src/generated/resources/`：由 `runData` 生成的模型、方块状态、语言、标签和战利品表。

## 2. Mod 启动与注册链路

主入口是 `SimpleSR.java`，构造函数中注册：

1. `ModBlocks`
2. `ModBlockEntities`
3. `ModItems`
4. `ModMenuTypes`
5. `ModCreativeModeTabs`
6. `Network`

客户端菜单屏幕必须在 `SimpleSR.ClientModEvents.onClientSetup` 中注册：

```java
MenuScreens.register(ModMenuTypes.X_MENU.get(), XScreen::new);
```

新增一个带 GUI 的机器时，通常必须同时修改：

- `ModBlocks`
- `ModBlockEntities`
- `ModMenuTypes`
- `SimpleSR.ClientModEvents`
- `ModCreativeModeTabs`
- `ModBlockStateProvider`
- `ModItemModelsProvider`
- `ModBlockLootTablesProvider`
- `ModBlockTagsProvider`
- 中英文语言 Provider
- `Config` 中的生电工作台配方

## 3. 已实现机器清单

### 3.1 刷铁机 `iron_farm`

文件：

- `block/IronFarmBlock.java`
- `block/entity/IronFarmBlockEntity.java`

行为：

- 54 格大型箱子容器。
- 每 60 秒读取原版铁傀儡战利品表。
- 使用临时铁傀儡对象作为战利品上下文，不生成实体。
- 使用 `minecraft:entities/iron_golem` 战利品表。

### 3.2 史莱姆刷怪塔 `slime_tower`

文件：

- `block/SlimeTowerBlock.java`
- `block/entity/SlimeTowerBlockEntity.java`
- `menu/SlimeTowerMenu.java`
- `screen/SlimeTowerScreen.java`

行为：

- 54 格大型箱子容器。
- 每 20 tick 模拟一次史莱姆死亡掉落。
- 临时史莱姆必须设置为尺寸 1，因为原版史莱姆战利品表只对尺寸 1 给出黏液球。
- 不能调用 `addFresh` 或其他生成实体的方法。
- 采用原版 `minecraft:entities/slime` 战利品表，兼容其他 Mod 修改该战利品表。
- 检测史莱姆区块并在 UI 右上角显示运行状态。
- 因当前映射中没有直接可用的 `Slime.isSlimeChunk`，代码使用原版史莱姆区块种子公式。

掉落处理是事务式的：先复制箱子快照，所有掉落都能放入才提交；任何一个掉落无法放入就恢复快照。

### 3.3 刷石机 `cobblestone_farm`

文件：

- `block/CobblestoneFarmBlock.java`
- `block/entity/CobblestoneFarmBlockEntity.java`

行为：

- 54 格大型箱子容器。
- 每 20 tick 尝试加入 12 个原石。
- 箱子无法完整放入 12 个原石时，取消本次产出。

### 3.4 50 倍速熔炉组 `furnace_array`

文件：

- `block/FurnaceArrayBlock.java`
- `block/entity/FurnaceArrayBlockEntity.java`
- `menu/FurnaceArrayMenu.java`
- `screen/FurnaceArrayScreen.java`

容器布局：

- 槽位 `0..53`：左侧输入箱。
- 槽位 `54..107`：右侧输出箱。
- 玩家槽位从菜单槽位 `108` 开始。

行为：

- 每 4 tick 处理一个烧炼物品，相当于 50 个普通熔炉并行。
- 通过运行时 `RecipeManager` 查询：

```java
level.getRecipeManager().getRecipeFor(
    RecipeType.SMELTING,
    recipeInput,
    level
);
```

- 不硬编码烧炼配方，因此可以兼容其他 Mod 注册到标准 `RecipeType.SMELTING` 的配方。
- 不消耗燃料；机器本身抽象掉燃料过程。
- 输出箱无法放入结果时，不消耗输入。
- 实现了 `WorldlyContainer`：
  - 漏斗可以向输入箱输入。
  - 漏斗无法从输入箱抽取。
  - 漏斗可以从输出箱抽取。
- 玩家可以手动取出输入物品。
- 输出箱支持 Shift+点击快速取出。
- 双箱物品在破坏机器时都会掉落。

注意：菜单槽位必须保持“先添加全部输入槽，再添加全部输出槽”的顺序。输入/输出槽交错加入会导致 Shift+点击的索引范围失效。

### 3.5 刷铁轨机 `rail_farm`

文件：

- `block/RailFarmBlock.java`
- `block/entity/RailFarmBlockEntity.java`
- `menu/RailFarmMenu.java`
- `screen/RailFarmScreen.java`

容器布局：

- 槽位 `0`：单个铁轨输入槽。
- 槽位 `1..54`：54 格产物箱。

行为：

- 输入槽只接受 `minecraft:rails` 标签中的铁轨。
- 支持普通铁轨、动力铁轨、探测铁轨、激活铁轨。
- 输入槽最大堆叠数为 1。
- 有输入时可以点击开关；输入为空时自动关闭。
- 开启后每 10 tick 复制 2 个相同铁轨，即每秒 2 个。
- 输出箱无法放入完整的 2 个铁轨时，取消本次复制。
- UI 已压缩为约 190 像素宽：输入槽和开关在顶部中央，产物箱位于中间，玩家背包位于下方中央。

### 3.6 沼泽刷怪塔 `swamp_mob_tower`

文件：

- `block/SwampMobTowerBlock.java`
- `block/entity/SwampMobTowerBlockEntity.java`
- `menu/SwampMobTowerMenu.java`
- `screen/SwampMobTowerScreen.java`

行为：

- 54 格大型箱子容器。
- 仅在 `Biomes.SWAMP` 沼泽群系运行，不包含其他群系。
- 每 20 tick 随机模拟 5 次怪物死亡掉落。
- 每次随机选择：僵尸、骷髅、蜘蛛、苦力怕、女巫、小型史莱姆。
- 使用临时实体作为战利品表上下文，不生成真实实体。
- 小型史莱姆设置尺寸 1，以满足原版史莱姆战利品表条件。
- 根据实体类型动态寻找 `entities/<entity_id>` 战利品表，兼容其他 Mod 对原版实体战利品表的修改。
- 每只怪物的掉落独立事务处理：某只掉落无法完整放入，只取消该只，不影响同一秒其他模拟。
- UI 右上角显示“正常运行”或“非沼泽群系，不运行”。

## 4. 生电工作台与配方

生电工作台不是原版工作台配方系统，而是项目自己的配置驱动系统。

关键文件：

- `Config.java`
- `recipe/SurvivalRedstoneRecipe.java`
- `menu/SurvivalRedstoneCraftingTableMenu.java`
- `screen/SurvivalRedstoneCraftingTableScreen.java`

### 配方添加位置

机器配方应添加到 `Config.java` 的 `SURVIVAL_REDSTONE_RECIPES` 默认列表中，而不是添加到 `ModRecipesProvider`。

每个配方是 JSON 字符串，包含：

- `id`
- `name_key`
- `level`
- `ingredients`
- `result`

数量换算：

- 一组物品：`64`
- 例如 12 组平滑石头：`768`
- 例如 4 组红石：`256`

### 配置迁移

Forge 配置文件会保留旧版本内容。仅修改默认值不能让已有的 `simple_sr-common.toml` 自动出现新配方。

因此 `Config.onLoad` 中维护了按配方 ID 判断的自动迁移：如果旧配置缺少某个机器配方，就追加该配方并保存配置。新增机器时必须同时：

1. 添加默认配方常量。
2. 添加到默认 `List.of(...)`。
3. 在 `onLoad` 中增加缺失配方迁移。
4. 添加 `recipe.simple_sr.<id>` 中文和英文翻译。

木制材料应优先使用标签，例如：

- `minecraft:fence_gates`
- `minecraft:wooden_pressure_plates`
- `minecraft:wooden_trapdoors`

原版不存在“含水树叶”独立物品，之前刷石机按 `minecraft:leaves` 标签处理，这是物品配方中的合理近似。

## 5. 生电工作台机器列表滚动

文件：

- `screen/SurvivalRedstoneCraftingTableScreen.java`

左侧机器列表只显示 6 个项目，当前实现包含：

- `recipeScroll` 偏移量。
- 鼠标滚轮处理。
- 滚动条绘制。
- 点击时将可视行转换为真实配方索引。

如果继续增加机器，必须保持以下逻辑同步：

- 可视数量 `VISIBLE_RECIPE_COUNT`。
- 绘制按钮的实际索引。
- 名称和图标绘制的实际索引。
- 鼠标点击的实际索引。
- 滚动条滑块的位置。

## 6. GUI 开发注意事项

### Z 层级

村民交易所改名弹窗曾被中间栏、右侧栏物品图标遮挡。

原因是调用顺序靠后不代表 Z 层级更高。现在 `VillagerTradingHallScreen.renderDialog` 使用 Z=400 绘制整个弹窗。

如果新增弹窗、确认框或覆盖层，应使用：

```java
graphics.pose().pushPose();
graphics.pose().translate(0.0F, 0.0F, 400.0F);
// draw overlay and widgets
graphics.pose().popPose();
```

### 菜单槽位索引

自定义菜单中的槽位索引必须与 `quickMoveStack` 的范围一致。新增左右箱子时推荐：

1. 先连续添加输入槽。
2. 再连续添加输出槽。
3. 最后添加玩家背包槽。
4. 明确写出 `INPUT_START`、`OUTPUT_START`、`PLAYER_START` 常量。

### 客户端类与服务端类

通用方块实体不要直接引用只存在客户端的类，例如 `ClientLevel`。否则专用服务器可能在类加载阶段崩溃。

客户端专属逻辑应放在 `screen/` 或客户端事件中；服务端逻辑应放在方块实体 tick 中。

### 临时实体战利品模拟

使用原版战利品表时可以创建临时实体，但只能用于上下文：

- 设置必要属性，例如史莱姆尺寸。
- 设置位置和伤害来源参数。
- 不要将实体加入世界。
- 不要调用生成、加入世界或实体 tick 方法。

这样可以读取原版以及其他 Mod 修改的战利品表。

## 7. 资源和数据生成

新增机器通常需要：

- 正面纹理：`src/main/resources/assets/simple_sr/textures/block/<id>_front.png`
- 侧面纹理：`src/main/resources/assets/simple_sr/textures/block/<id>_side.png`
- `ModBlockStateProvider` 中的水平朝向模型。
- `ModItemModelsProvider` 中的方块物品模型。
- `ModBlockLootTablesProvider` 中的 `dropSelf`。
- `ModBlockTagsProvider` 中的机器标签。
- 中英文语言 Provider。

运行：

```powershell
.\gradlew.bat runData --no-daemon
```

不要手工编辑 `src/generated/resources` 中由数据生成器负责的文件；应修改 Provider 后重新运行 `runData`。

手写纹理必须实际存在于 `src/main/resources`，否则 `runData` 会在模型生成阶段报错：

```text
Texture simple_sr:block/<id> does not exist in any known resource pack
```

## 8. 推荐验证流程

每次新增或修改机器后：

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat runData --no-daemon
.\gradlew.bat build --no-daemon
```

游戏内至少验证：

1. 创造标签页和生电工作台能看到机器。
2. 新旧配置都能加载配方。
3. 输入槽拒绝错误物品。
4. 输出箱满时不会消耗输入或产生部分掉落。
5. 机器破坏时所有库存均掉落。
6. 机器运行条件不满足时 UI 状态正确。
7. 玩家手动取出、Shift+点击、漏斗输入/输出符合设计。
8. 其他 Mod 配方或战利品修改仍能被运行时查询到。

## 9. 当前已知设计约定

- 机器的生产逻辑通常在服务端方块实体 `tick` 中执行。
- 机器时间使用 tick：20 tick = 1 秒。
- 容器类负责保存、加载、掉落和产出；菜单负责槽位限制、交互和快速移动；屏幕只负责客户端绘制。
- 生电工作台配方是配置驱动的，不是普通 JSON 合成配方。
- 机器方块统一使用 `ModBlocks.machineProperties()`。
- 机器统一加入自定义机器标签并要求正确工具挖掘。
- 当前生成资源与日志目录可能在运行 Gradle 后产生工作区变更，提交时应区分源码资源与运行时日志。
