# SimpleSurvivalRedstone

SimpleSR 是一个面向 Minecraft 1.20.1 Forge 生存玩法的红石辅助机器 Mod，提供可通过生电工作台制造的压缩化自动化机器。

## 当前机器

- 刷铁机、史莱姆刷怪塔、刷石机、50 倍速熔炉组、刷铁轨机
- 沼泽刷怪塔、蜂蜜农场、村民繁殖机、村民交易所
- 刷沙机、固化机、潜影贝农场、甘蔗竹子机、打包机
- 小黑塔、猪人塔、猪灵交易塔、疣猪兽农场、袭击塔、守卫者农场、凋零骷髅农场

机器普遍拥有独立方块实体、容器、服务端运行逻辑和客户端 GUI。需要原版配方或战利品时，项目会优先通过运行时 RecipeManager 或战利品表查询，以兼容其他 Mod 的扩展。

小黑塔、猪人塔和凋零骷髅农场支持剑槽、剑的所属玩家校验、原版战利品表和抢夺附魔；经验会先存储在机器内，并可通过 GUI 按住按钮逐 tick 提取。猪灵交易塔运行时读取原版猪灵交易战利品表，袭击塔使用模拟波次并带有完成后的冷却时间。守卫者农场、凋零骷髅农场、袭击塔和其他条件型机器会在 GUI 中显示维度、群系或结构区域状态。

## 环境

- Minecraft：1.20.1
- Forge：47.4.0
- Java：17

## 常用命令

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat runData --no-daemon
.\gradlew.bat build --no-daemon
```

机器配方由 `Config.java` 中的生电工作台配置驱动。数据生成资源位于 `src/generated/resources/`，不应直接手工修改；修改对应 Provider 后重新运行 `runData`。
