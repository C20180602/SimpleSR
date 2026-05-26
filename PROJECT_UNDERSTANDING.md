# SimpleSR 项目理解与完整性检查

## 项目概况

该项目是一个使用 IntelliJ IDEA/Gradle 开发的 Minecraft Forge 模组项目。

- Minecraft 版本：`1.20.1`
- Forge 版本：`47.4.0`
- Java 版本：`17`
- 映射：Parchment `2023.09.03-1.20.1`
- 主包名：`com.alster.simple_sr`
- 运行配置：`client`、`server`、`gameTestServer`、`data`

从 `gradle.properties` 的描述看，模组目标是 **Simple Survival Redstone**：添加一些简单机器，让生存模式红石搭建更容易、更紧凑。

## 当前已实现内容

### 主模组入口

`SimpleSR` 是主入口类，`@Mod(SimpleSR.MODID)` 使用的模组 ID 是 `simple_sr`。

当前完成的职责：

- 注册方块：`ModBlocks.register(modEventBus)`
- 注册物品：`ModItems.register(modEventBus)`
- 注册创造模式标签页：`ModCreativeModeTabs.register(modEventBus)`
- 注册 Forge common config：`Config.SPEC`
- 注册服务端启动事件日志
- 注册客户端 setup 日志

`addCreative` 方法目前为空。由于项目已经使用自定义 `CreativeModeTab` 的 `displayItems` 添加物品和方块，这个空方法暂时不影响当前创造标签页内容，但它属于 Forge 模板残留，可以删除或补齐用途。

### 物品

`ModItems` 当前注册了 1 个物品：

- `survival_redstone_core`

该物品使用普通 `Item.Properties()`，没有特殊行为。

### 方块

`ModBlocks` 当前注册了 1 个方块：

- `survival_redstone_crafting_table`

该方块目前是普通 `Block`，属性为：

- 强度：`1.0f`
- 爆炸抗性：`1.0f`

同时 `registerBlock` 会自动注册同名 `BlockItem`，因此该方块可以作为物品出现在背包和创造标签页中。

当前还没有实现：

- 自定义方块类
- GUI/菜单
- 方块实体
- 红石逻辑
- 合成台或机器交互逻辑

因此 `survival_redstone_crafting_table` 现在只是一个普通可放置方块，不具备工作台或机器功能。

### 创造模式标签页

`ModCreativeModeTabs` 注册了一个自定义标签页：

- 注册名：`simple_sr_tab`
- 图标：`survival_redstone_core`
- 显示内容：
  - `survival_redstone_core`
  - `survival_redstone_crafting_table`

该部分逻辑基本完整。

### 配置

`Config` 基本保留 Forge 模板示例：

- `logDirtBlock`
- `magicNumber`
- `magicNumberIntroduction`
- `items`

这些配置只用于启动时输出日志，与当前模组目标没有直接关系。它可以作为配置系统示例保留，但正式功能开发时建议清理或替换为真实配置项。

### 数据生成器

`ModDataGenerator` 已接入以下 provider：

- 配方生成
- 战利品表生成
- 方块标签生成
- 物品标签生成
- 方块状态/模型生成
- 物品模型生成
- 英文语言文件生成
- 简体中文语言文件生成

其中部分 provider 已能表达当前内容，部分还不完整。

## 函数完整性检查

### 完整或基本可用

- `ModBlocks.register`
- `ModBlocks.registerBlock`
- `ModBlocks.registerBlockItems`
- `ModItems.register`
- `ModCreativeModeTabs.register`
- `ModDataGenerator.gatherData`
- `ModRecipesProvider.buildRecipes`
- `ModLootTableProvider.create`
- `ModBlockLootTablesProvider.generate`
- `ModBlockLootTablesProvider.getKnownBlocks`
- `ModItemModelsProvider.registerModels`
- `ModEnUsLangProvider.addTranslations`

### 空实现但当前不一定阻断

- `SimpleSR.addCreative`
  - 当前为空。
  - 因为已有自定义创造标签页，这个方法不是当前功能必需。

- `ModBlockTagsProvider.addTags`
  - 当前为空。
  - 如果方块需要挖掘工具、挖掘等级、通用标签等，需要在这里补齐。

- `ModItemTagsProvider.addTags`
  - 当前为空。
  - 如果物品需要参与标签配方或 Forge 通用标签，需要在这里补齐。

- `ModBlockTags.create` / `ModBlockTags.createForge`
  - 私有工具方法已写好，但没有公开 tag 常量，也没有被使用。

- `ModItemTags.bind`
  - 私有工具方法已写好，但没有公开 tag 常量，也没有被使用。

### 明确未完成或会导致构建失败

- `ModBlockStateProvider.registerStatesAndModels`
  - 当前包含占位代码：
    - `???(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(),???);`
  - 这是 Java 非法语法，当前项目无法编译。
  - 如果该方块是普通立方体，应改为类似 `simpleBlockWithItem(...)` 的实现，并补充方块纹理。

## 配方与资源状态

### 当前配方目标

`ModRecipesProvider` 生成两个合成配方：

1. `survival_redstone_core`
   - 石头 + 红石合成核心。

2. `survival_redstone_crafting_table`
   - 石头 + 任意台阶标签 + `survival_redstone_core` 合成。

### 当前资源情况

已有资源：

- `assets/simple_sr/textures/item/survival_redstone_core.png`

缺少或未生成的关键资源：

- `survival_redstone_crafting_table` 的方块纹理
- 方块状态 JSON
- 方块模型 JSON
- 方块物品模型 JSON
- 语言 JSON
- 配方 JSON
- 战利品表 JSON

这些 JSON 预期由数据生成器生成，但数据生成器当前无法运行，因为项目不能编译。

## 构建与运行确认

我已执行：

```powershell
.\gradlew.bat build
```

结果：构建失败。

当前首个阻断错误：

```text
src/main/java/com/alster/simple_sr/datagen/ModBlockStateProvider.java:16
错误: 非法的表达式开始
???(ModBlocks.SURVIVAL_REDSTONE_CRAFTING_TABLE.get(),???);
```

因此当前状态下：

- 不能完成 `build`
- 不能确认 `runClient` 可正常启动
- 不能运行数据生成任务生成资源

在修复编译错误后，建议依次验证：

```powershell
.\gradlew.bat build
.\gradlew.bat runData
.\gradlew.bat runClient
```

## 对模组任务内容的理解

该模组当前处于早期骨架阶段。它想要围绕“生存红石”做一套更方便、更紧凑的红石辅助系统。

目前已经落地的第一步是：

- 定义一个基础材料：`Survival Redstone Core`
- 定义一个核心工作方块：`Survival Redstone Crafting Table`
- 将它们加入自定义创造标签页
- 为它们准备配方、语言、模型、战利品表等数据生成框架

下一阶段合理任务应包括：

- 修复当前编译错误
- 补齐方块状态、模型和纹理
- 修复中文语言文件编码和内容
- 统一模组 ID 元数据
- 明确 `survival_redstone_crafting_table` 的实际玩法职责
- 如果它是特殊工作台，需要实现菜单、容器、屏幕、交互逻辑
- 如果它是红石机器，需要实现方块状态、红石输入/输出、tick 或邻居更新逻辑

## 建议优先级

1. 修复 `ModBlockStateProvider` 的占位代码。
2. 修复 `ModZhCnLangProvider` 的字符串和编码。
3. 将 `gradle.properties` 的 `mod_id` 改为 `simple_sr`。
4. 补充 `survival_redstone_crafting_table` 的方块纹理。
5. 运行 `build` 和 `runData`，生成并检查资源。
6. 再开始实现真正的机器/红石/GUI 功能。

