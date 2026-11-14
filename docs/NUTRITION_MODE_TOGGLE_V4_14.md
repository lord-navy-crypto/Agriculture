# Nutrition Mode Toggle v4.14 / 营养模式开关 v4.14

## English

### Player-facing behavior

The N-key Nutrition Dashboard now has a compact **N: ON / N: OFF** switch.

- New and existing players default to **ON**. A missing saved setting is interpreted as enabled, so upgrading an existing world does not silently change its rules.
- The setting is saved per player on the server and survives death/respawn.
- When OFF, the 25 nutrient-reserve channels are paused: food no longer adds nutrient points, reserve depletion stops, the daily intake ledger is cleared, and nutrient feedback effects are no longer refreshed.
- Existing reserve values and shortfall history are preserved. Re-enabling resumes from the saved state without calculating missed days.
- Hydration and sodium remain independent and continue to be governed by WaterSaltV37. The switch does not disable the water/salt survival system.
- The setting is server-authoritative. The client requests a toggle; the server changes the saved preference and returns a fresh dashboard snapshot.

### Implementation contract

1. `gugugaga_nutrition_mode_enabled` is a per-player persistent boolean. Missing means `true`.
2. `NutritionV34.onEat` ignores nutrient contributions while the mode is OFF.
3. `NutritionV34.onTick` calls `NutritionPhysiologyV39.pause` while OFF and skips reserve advancement and nutrient feedback.
4. `pause` synchronizes the processed Overworld day and clears daily nutrient intake counters, but does not rewrite nutrient reserves or shortfall-day counters.
5. Network payload protocol is version `5`; the state packet carries the enabled flag.
6. The N-key panel displays the current mode and sends a server toggle request.

### Verification

```sh
python3 qa/validate_nutrition_mode_toggle_v4_14.py
gradle --no-daemon build
```

The validator can also inspect a built JAR:

```sh
python3 qa/validate_nutrition_mode_toggle_v4_14.py build/libs/gugugaga-4.0.0-alpha.1.jar
```

## 中文

### 玩家界面行为

N 键营养面板现在提供一个紧凑的 **N: ON / N: OFF** 开关。

- 新玩家和已有玩家默认**开启**。如果旧存档没有该设置，就按开启处理，升级不会悄悄改变原有规则。
- 设置保存在服务器端的玩家数据中，死亡和重生后仍然保留。
- 关闭时暂停 25 项营养储备：食物不再增加营养点数、储备停止消耗、清空每日摄入账本，并停止刷新营养反馈效果。
- 已有储备值和持续不足历史会保留。重新开启后从保存状态继续，不会补算关闭期间的天数。
- 饮水和钠盐仍然独立运行，由 WaterSaltV37 管理。这个开关不会关闭水盐生存系统。
- 设置由服务器权威管理。客户端发送切换请求，服务器保存设置并返回新的面板快照。

### 实现约定

1. `gugugaga_nutrition_mode_enabled` 是逐玩家持久化布尔值；旧数据缺少该键时默认为 `true`。
2. 关闭时，`NutritionV34.onEat` 不再计入营养贡献。
3. 关闭时，`NutritionV34.onTick` 调用 `NutritionPhysiologyV39.pause`，跳过储备推进和营养反馈。
4. `pause` 会同步已处理的主世界游戏日并清空每日营养摄入计数，但不会改写储备值或持续不足天数。
5. 网络协议版本升级为 `5`；状态包携带营养模式开关状态。
6. N 键面板显示当前模式，并向服务器发送切换请求。

### 验证

运行上面的 Python 验证脚本和 Gradle 构建即可。
