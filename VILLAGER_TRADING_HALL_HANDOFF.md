# 村民交易所项目总结与接手说明

## 1. 项目环境

- Minecraft：1.20.1
- Forge：47.4.0
- Java：17
- 映射：Parchment 2023.09.03-1.20.1
- Mod ID：`simple_sr`
- 主包：`com.alster.simple_sr`

本文件记录本轮村民交易所功能的最终实现和开发过程中遇到的问题。后续修改前应先阅读本文件，再阅读以下核心文件：

- `block/VillagerTradingHallBlock.java`
- `block/entity/VillagerTradingHallBlockEntity.java`
- `menu/VillagerTradingHallMenu.java`
- `screen/VillagerTradingHallScreen.java`
- `item/VillagerTradingHallBlockItem.java`
- `Network.java`

## 2. 当前功能状态

村民交易所是一个保存模拟村民数据的方块，不生成并长期保存真实村民实体。每个方块最多保存 50 个模拟村民。

每个模拟村民保存以下数据：

- 名称，最多 10 个字符
- 职业
- 交易列表
- 当前经验
- 当前交易等级
- 交易使用次数等 `MerchantOffer` 数据

界面分为三栏：

- 左栏：村民输入槽、提交/排序按钮、村民列表
- 中栏：交易选择栏
- 右栏：交易执行栏、刷新按钮、玩家背包

村民等级使用原版名称：新手、学徒、老手、专家、大师。升级时保留旧交易，并追加新等级交易。

交易生成复用了原版 `Villager.updateTrades()` 和 `VillagerTrades.TRADES`，因此 Forge/其他 Mod 注册的村民交易项目仍有兼容机会。1.20.1 原版每次等级生成 2 个交易项目。

## 3. 数据保存与同步链路

### 方块实体保存

`VillagerTradingHallBlockEntity` 的 `saveAdditional/load` 保存和读取：

- 两个输入槽
- `Villagers` 列表
- `Name`
- `Profession`
- `Offers`
- `Xp`
- `Level`

挖掘时通过 `getDrops` 把完整数据写入物品的 `BlockEntityTag`。有村民或输入内容的交易所物品强制堆叠数为 1；空交易所物品仍可堆叠。

放置带数据的交易所时，`VillagerTradingHallBlock.setPlacedBy` 会显式将 `BlockEntityTag` 恢复到新的方块实体中。这一步不能省略，否则物品悬浮提示有村民数据，但放置后的方块实体可能为空。

### 打开菜单

`VillagerTradingHallBlock.use` 使用 `NetworkHooks.openScreen`，传递：

1. 方块坐标
2. `hall.saveForItem()` 完整 NBT

客户端菜单构造器读取这份 NBT，并复制出 `displayedVillagers` 快照供 UI 使用。

### 客户端快照

左侧列表和中间交易选择栏不能直接依赖客户端方块实体列表。客户端方块实体可能被后续空的更新包覆盖，导致 UI 短暂显示后消失。

因此：

- UI 使用 `VillagerTradingHallMenu.displayedVillagers`
- 服务端交易逻辑使用方块实体中的真实村民列表
- `Network.SyncTradingHall` 到达客户端后重新刷新菜单快照
- 打开界面时不要额外发送可能为空的冗余同步包

## 4. 交易生成与升级规则

原版 `Villager.updateTrades()` 的核心行为是从对应职业和等级的 `VillagerTrades.TRADES` 中随机抽取 2 个项目。

当前项目使用 `StoredVillager` 作为临时生成器：

```java
villager.setVillagerData(new VillagerData(VillagerType.PLAINS, profession, level));
villager.getOffers().clear();
villager.updateTrades();
```

必须先清空临时村民的交易列表，再调用一次 `updateTrades()`。否则如果未来修改构造流程或重复调用生成方法，可能出现新手村民有 4 个交易项目的问题。

升级时只追加新等级生成的 2 个交易，不清空旧交易。

经验阈值：

- 新手 → 学徒：10
- 学徒 → 老手：70
- 老手 → 专家：150
- 专家 → 大师：250

刷新交易只允许 `xp == 0` 的新手村民使用。按钮在客户端显示为灰色，服务端菜单也会再次校验，不能只依赖客户端按钮状态。

旧数据加载时会将交易数量限制为 `等级 × 2`，用于修复之前生成出的重复交易数据。

## 5. UI 交互注意事项

### 左侧村民列表

左侧信息框的区域必须拆开：

- `x=8..70`：选择村民/双击改名
- `x=70..90`：删除按钮

不能让整个信息框都响应选择，否则点击叉会同时触发选择和删除。

村民列表在 `render` 的后层绘制，以避免被容器内容覆盖；但删除按钮、确认按钮和输入框不能重复手动绘制，否则会出现按钮显示两个的问题。

### 改名

改名使用独立弹窗：

- 双击村民打开
- 输入框
- 确认改名
- 取消

点击确认后通过 `Network.rename` 发往服务端。服务端修改方块实体并发送完整同步包。

不要再把改名输入框放在村民列表内部，否则列表置顶绘制时会覆盖输入框，并且点击列表外保存逻辑容易失效。

### 删除

删除流程是：

1. 点击一次叉
2. 弹出“确定删除村民 xxx 吗”
3. 点击“是”才发送删除菜单按钮
4. 点击“否”关闭弹窗

当前菜单删除分支已经是一次删除，不应恢复旧的“三次点击删除”计数逻辑。

### 交易物品显示

中间栏物品是手动绘制的，不是标准 `Slot`，因此必须主动调用：

- `renderItem`
- `renderItemDecorations` 显示数量
- `renderTooltip`/`GuiGraphics.renderTooltip` 显示悬浮提示

否则物品虽然显示，但数量和名称提示不会出现。

## 6. 本轮主要踩坑记录

### 坑 1：继承原版 MerchantMenu/MerchantScreen

村民交易所不是村民实体，直接继承 `MerchantMenu` 或 `MerchantScreen` 会让原版绑定逻辑寻找真实村民实体，导致交易界面和方块数据不匹配。

最终方案是从 `AbstractContainerMenu` 和 `AbstractContainerScreen` 重写。

### 坑 2：把临时村民当成真实村民保存

项目需求只需要复用原版交易生成代码，不需要生成实体。真实实体会带来实体检测、生命周期、工作站绑定和同步问题。

最终只保存职业、交易、经验和等级。

### 坑 3：物品 NBT 有数据，但方块 UI 是空的

原因是物品悬浮提示读取的是物品 `BlockEntityTag`，而 UI 读取的是客户端方块实体。放置时没有可靠恢复 NBT，或客户端同步包覆盖了数据，就会出现“提示有数据、界面为空”。

解决方式是：

- `setPlacedBy` 显式恢复 NBT
- 打开菜单时传递完整 NBT
- 菜单保存客户端村民快照
- 同步包只刷新快照

### 坑 4：列表曾经短暂显示后消失

原因是客户端方块实体同步和菜单数据同步顺序不稳定，空方块更新会清掉界面正在使用的数据。

不能只调整绘制层级，必须隔离 UI 数据和方块实体数据。

### 坑 5：删除按钮重复显示

按钮已经由 Screen 的 widget 系统绘制后，又在 `render` 中手动调用了一次 `button.render`，导致重复绘制。

后续如果需要提升按钮层级，应先确认它是否已经由 `super.render` 绘制，避免再次手动画同一个按钮。

### 坑 6：交易生成数量翻倍

原版 `updateTrades()` 每次等级只追加 2 个项目。若生成函数重复调用或未清空临时村民已有的 `MerchantOffers`，就可能出现新手有 4 个交易。

当前生成器已经在调用原版方法前清空交易列表，加载旧数据时也会按等级裁剪异常数量。

### 坑 7：升级时清空旧交易

旧实现升级后调用 `record.offers.clear()`，导致低等级交易消失。正确行为是保留旧列表，再将新等级的 2 个交易追加进去。

### 坑 8：客户端快照被错误用于服务端交易

菜单快照是给客户端 UI 用的，服务端完成交易时必须读取方块实体中的真实 `MerchantOffer`。否则保存列表和 UI 快照中的交易对象可能不是同一个对象，导致交易、使用次数和经验更新失效。

## 7. 修改后的验证流程

建议每次修改后执行：

```powershell
.\gradlew.bat compileJava --no-daemon
```

进入游戏后至少验证：

1. 新交易所放入村民刷怪蛋和有效工作站，提交后出现 2 个新手交易。
2. 新手村民经验为 0 时刷新按钮可用。
3. 发生一次交易后刷新按钮变灰。
4. 交易达到阈值后等级升级，旧交易仍存在，并新增 2 个交易。
5. 改名弹窗确认/取消均正常。
6. 删除弹窗确认/取消均正常。
7. 破坏方块、重新放置后村民列表和交易完整保留。
8. 带 NBT 的交易所物品不能与其他交易所堆叠。

## 8. 后续修改原则

- 先区分服务端真实数据、客户端方块实体数据和客户端 UI 快照。
- 不要为了修 UI 显示问题直接修改交易生成逻辑。
- 不要重新继承 `MerchantMenu`/`MerchantScreen`。
- 交易生成优先调用原版 `Villager.updateTrades()`，不要手写职业交易表。
- 所有客户端按钮限制都必须在服务端菜单再次校验。
- 修改屏幕绘制顺序时，确认 widget 是否已经由 `super.render` 绘制，避免重复绘制。
- 修改 NBT 结构时必须同时检查方块实体保存、物品掉落、放置恢复、菜单打开和网络同步五条链路。
