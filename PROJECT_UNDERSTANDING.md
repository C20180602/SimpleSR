# SimpleSR 项目理解文档

更新时间：2026-08-02

## 1. 项目定位

SimpleSR 是一个 Minecraft Forge Mod，目标是为生存模式提供更紧凑、更方便的红石辅助机器。

- Minecraft：1.20.1
- Forge：47.4.0
- Java：17
- 映射：Parchment `2023.09.03-1.20.1`
- Mod ID：`simple_sr`
- 主包：`com.alster.simple_sr`
- 构建工具：Gradle

项目不是单纯的物品/方块模板，而是已经包含多个带容器、方块实体和客户端 GUI 的机器，以及一个保存模拟村民数据的交易所。

## 2. 目录结构

```text
src/main/java/com/alster/simple_sr/
├─ block/                 方块定义、交互入口、方块注册
│  └─ entity/             方块实体、库存、服务端机器逻辑
├─ menu/                  菜单、槽位、输入输出限制、快速移动
├─ screen/                客户端 GUI 绘制与鼠标交互
├─ item/                  物品、方块物品、创造模式标签页
├─ datagen/               方块状态、模型、语言、配方、战利品表、标签生成
├─ recipe/                生存红石工作台的自定义配方类型
├─ level/                 生存红石等级相关逻辑
├─ tag/                   自定义方块/物品标签
├─ Config.java            Forge 配置及机器配方配置
├─ Network.java           自定义网络消息
├─ ModDataGenerator.java  数据生成入口
└─ SimpleSR.java          Mod 主入口
```

资源目录：

- `src/main/resources/assets/simple_sr/`：手写纹理和固定资源
- `src/generated/resources/`：通过 `runData` 生成的模型、方块状态、语言、配方、战利品表和标签

不要直接手工修改 `src/generated/resources` 中由数据生成器负责的文件；应修改 Provider 后重新运行 `runData`。

## 3. Mod 启动与注册链路

主入口是 `SimpleSR.java`。构造函数中主要完成：

1. 注册网络消息
2. 注册方块 `ModBlocks`
3. 注册方块实体 `ModBlockEntities`
4. 注册物品 `ModItems`
5. 注册菜单 `ModMenuTypes`
6. 注册创造模式标签页 `ModCreativeModeTabs`
7. 注册 Forge common config

客户端初始化时，`SimpleSR.ClientModEvents` 将菜单类型绑定到对应的 Screen。新增带 GUI 的机器时，通常需要同步修改 Block、BlockEntity、Menu、Screen、注册类、数据生成器、配置和语言文件。

## 4. 当前方块和机器

当前注册的主要方块及对应方块实体为：

- `survival_redstone_crafting_table`：生存红石工作台
- `iron_farm`：铁农场
- `slime_tower`：史莱姆塔
- `cobblestone_farm`：刷石机
- `furnace_array`：熔炉阵列
- `rail_farm`：铁轨机
- `swamp_mob_tower`：沼泽刷怪塔
- `honey_farm`：蜂蜜农场
- `villager_breeder`：村民繁殖机
- `villager_trading_hall`：村民交易所
- `sand_farm`：刷沙机，仅在末地运行
- `solidifier`：固化机，仅在末地运行
- `shulker_farm`：潜影贝农场，仅在末地运行
- `bamboo_farm`：甘蔗竹子机，默认甘蔗模式，可切换为竹子
- `packer`：打包机，读取背面材料和顶部容器，将物品装入潜影盒
- `enderman_tower`：小黑塔，支持剑槽、玩家绑定和经验存储
- `zombified_piglin_tower`：猪人塔，仅在下界运行，支持剑槽和经验存储
- `piglin_trading_tower`：猪灵交易塔，仅在下界运行，动态读取猪灵交易战利品表
- `hoglin_farm`：疣猪兽农场，仅在绯红森林群系运行
- `raid_tower`：袭击塔，在海洋群系中模拟袭击波次
- `guardian_farm`：守卫者农场，仅在海底神庙结构区域运行
- `wither_skeleton_farm`：凋零骷髅农场，仅在下界要塞结构区域运行，支持剑槽和经验存储

机器方块通常继承 `BaseEntityBlock`，负责创建方块实体、响应玩家右键和提供服务端 ticker。真正的库存、计时器、产出和 NBT 保存逻辑位于 `block/entity/*BlockEntity.java`。

## 5. 机器逻辑约定

机器逻辑主要在服务端方块实体的 `tick` 中运行，时间单位是 Minecraft tick，20 tick 约等于 1 秒。

- 容器负责库存保存、加载、输入输出和破坏时掉落。
- 机器应在输出无法完整放入时取消本次产出，避免消耗输入或产生部分结果。
- 需要原版行为时，优先查询原版 RecipeManager 或战利品表，而不是复制硬编码数据。
- 临时实体只用于构造战利品表上下文，不能加入世界、生成实体或执行实体 tick。
- 服务端逻辑不能依赖客户端专属类，例如 `ClientLevel`。

主要机器行为：

- 铁农场：定时读取原版铁傀儡战利品表，不生成真实铁傀儡。
- 史莱姆塔：模拟尺寸为 1 的史莱姆死亡，并读取 `minecraft:entities/slime` 战利品表。
- 刷石机：定时产出原石，库存不能完整容纳时取消本次产出。
- 熔炉阵列：54 个输入槽和 54 个输出槽，使用标准 `RecipeType.SMELTING` 配方，约每 4 tick 处理一个物品，不消耗燃料。
- 铁轨机：只接受 `minecraft:rails` 标签中的铁轨，开启后定时复制铁轨。
- 沼泽刷怪塔：只在沼泽生物群系运行，动态读取多种实体的原版战利品表。
- 蜂蜜农场：定时将玻璃瓶转为蜂蜜瓶，并周期性生成蜜脾；输入和输出两侧独立存储。
- 村民繁殖机：保存输入物品并提供对应容器界面，具体繁殖逻辑位于其方块实体。
- 刷沙机：12 个重力方块输入槽和 72 个输出槽，每秒复制输入方块，仅在末地运行。
- 固化机：54 个输入槽和 54 个输出槽，每秒将最多 20 个混凝土粉末转为对应颜色混凝土，仅在末地运行。
- 潜影贝农场：54 槽大型箱子，每 5 秒读取潜影贝战利品表，仅在末地维度运行。
- 甘蔗竹子机：54 槽大型箱子，默认每 30 秒产出 1 个甘蔗，顶部按钮可切换为竹子；输出空间不足时取消本次产出。
- 打包机：27 个物品槽，背面读取 2 个潜影壳和 1 个箱子，顶面按漏斗规则取物，并以 4 倍漏斗速度装入潜影盒；每 8 tick 最多取出 4 个物品。

补充机器行为：

- 小黑塔、猪人塔和凋零骷髅农场都提供 54 格产物箱与剑槽；放入剑时记录玩家 UUID 和名称，自动运行前确认该玩家仍在线。玩家位于其他维度时也可运行，只要方块实体所在区块已加载。
- 三座剑槽机器的自动运行间隔均为 12 tick。战利品上下文使用真实绑定玩家，并临时将插槽中的剑置于其主手，以使原版战利品表正确读取抢夺等剑上附魔；原主手物品会立即恢复。
- 三座剑槽机器将经验存储在方块实体中。GUI 右侧显示绑定玩家、原版等级经验条和蓝色 `Lv.xxx`；按住提取按钮时服务端每 tick 最多提取 100 点经验，松开或关闭界面即停止。
- 猪灵交易塔每 24 tick 消耗左侧大型箱子中的一个金锭，并在交易时动态解析 `minecraft:gameplay/piglin_bartering` 战利品表及其权重，兼容数据包和其他 Mod 注入的新交易条目。
- 疣猪兽农场每 5 秒模拟一只着火疣猪兽死亡，仅在绯红森林群系运行；守卫者农场每秒模拟 5 只守卫者死亡，仅在海底神庙结构区域运行；凋零骷髅农场仅在下界要塞结构区域运行。
- 袭击塔按不祥之兆等级和难度模拟 8 波袭击，波次开始时输出各怪物及数量，完成一轮后进入 200 tick 冷却；冷却期间交互提示“冷却中”。
- 猪灵交易塔、守卫者农场、凋零骷髅农场的条件状态通过服务端容器数据同步到 GUI，避免客户端状态显示与实际运行条件不一致。

## 6. 生存红石工作台

生存红石工作台不是原版工作台的扩展，而是一个配置驱动的自定义机器配方系统。

核心文件：

- `Config.java`
- `recipe/SurvivalRedstoneRecipe.java`
- `menu/SurvivalRedstoneCraftingTableMenu.java`
- `screen/SurvivalRedstoneCraftingTableScreen.java`

机器配方应添加到 `Config.SURVIVAL_REDSTONE_RECIPES`。每个配方是包含以下字段的 JSON 字符串：

- `id`
- `name_key`
- `level`
- `ingredients`
- `result`

新增机器时除了增加默认配方，还要考虑旧版配置迁移，以及中英文配方名称翻译。

## 7. 村民交易所

村民交易所不保存真实村民实体，而是保存最多 50 个模拟村民记录。每条记录包含：

- 名称
- 职业
- `MerchantOffers`
- 经验
- 交易等级

交易生成使用临时 Villager 调用原版 `updateTrades()`，从而尽量兼容原版和其他 Mod 对村民交易表的修改。

数据链路分为三部分：

1. 服务端方块实体保存真实村民数据并执行交易。
2. 打开菜单时向客户端传递数据快照。
3. 客户端 GUI 使用菜单快照显示列表，改名、提交、同步等操作通过 `Network.java` 发回服务端。

服务端交易逻辑不能使用客户端快照；客户端按钮的可用状态也必须在服务端再次校验。

成功交易后，服务端在交易所方块顶面中心播放村民交易成功音效 `VILLAGER_YES`。

放置带数据的交易所物品时，`VillagerTradingHallBlock.setPlacedBy` 必须恢复 `BlockEntityTag`，否则物品提示有数据但新方块实体可能为空。

## 8. 菜单和 GUI 约定

菜单负责槽位布局和物品移动，Screen 负责绘制。新增输入输出容器时，槽位应保持以下顺序：

1. 全部输入槽
2. 全部输出槽
3. 玩家背包槽

同时明确 `INPUT_START`、`OUTPUT_START`、`PLAYER_START` 等常量，并保证它们与 `quickMoveStack` 的范围一致。

GUI 覆盖层、确认框和弹窗应使用较高的 Z 层级绘制，例如先 push pose，再平移到较高 Z，最后恢复 pose。已经由 `super.render` 绘制的 widget 不应在 `render` 中再次手动绘制。

剑槽机器的 GUI 需要同时绘制剑槽、绑定玩家名称、经验提取按钮、经验条和累计等级；物品槽应保留原版物品名称 tooltip，背包物品 tooltip 只显示 Mod 名称，不追加机器分类等内部信息。

## 9. 数据生成和资源

数据生成入口是 `ModDataGenerator.gatherData`，当前包含配方、战利品表、方块标签、物品标签、方块状态、方块模型、物品模型和中英文语言 Provider。

新增机器通常需要：

- 正面和侧面纹理：`assets/simple_sr/textures/block/`
- BlockState Provider
- Block 模型和 BlockItem 模型
- `dropSelf` 战利品表
- 方块标签，尤其是挖掘工具和挖掘等级
- 中英文翻译
- 配置配方

近期纹理美化资源包括 `bamboo_farm_front.png`、`sand_farm_front.png` 和 `solidifier_front.png`，分别绘制甘蔗/竹子、沙子/铁砧元素和多色混凝土，同时保留机器框架；潜影贝农场、打包机、袭击塔、守卫者农场和凋零骷髅农场也使用保留框架的专用正面图标。

运行：

```powershell
.\gradlew.bat runData --no-daemon
```

如果模型引用的纹理不存在，数据生成阶段会报资源缺失错误。

## 10. 修改后的推荐验证流程

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat runData --no-daemon
.\gradlew.bat build --no-daemon
```

游戏内至少验证：

1. 创造模式标签页和生存红石工作台能看到新增机器。
2. 新旧配置都能正常加载。
3. 输入槽能拒绝错误物品。
4. 输出空间不足时不会错误消耗输入。
5. 破坏机器时所有库存都能掉落。
6. 机器运行条件和 GUI 状态一致。
7. 手动取出、Shift 点击、漏斗输入输出符合设计。
8. 原版或其他 Mod 修改的配方、战利品表仍能被查询到。

## 11. 当前文档状态说明

旧版理解文档记录过早期的单方块模板状态和 `ModBlockStateProvider` 占位符编译错误。当前源码已经包含完整的多机器架构和生成资源，因此旧版内容不能作为当前实现状态依据。本文件以当前源码和交接文档为准；具体功能细节继续参考：

- `CODEX_PROJECT_HANDOFF.md`
- `VILLAGER_TRADING_HALL_HANDOFF.md`
