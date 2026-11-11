# Backroom Agriculture — v4.7 Food Feedback and Nutrition Profile Audit

## Simple feedback / 简明进食反馈
After completing a real eating action, the bottom of the screen displays the **top three actual reserve gains**:
- English: `Ate Lettuce: Vitamin C +5, Fiber +4, Vitamin K +3`
- 中文：`吃下生菜：维生素 C +5、膳食纤维 +4、维生素 K +3`

The values are examples of abstract Minecraft gameplay gains, **not** real nutrition quantities.
If the reserve is full or the per-day gain cap is reached, the player sees an explicit no-gain message.

Press **N** to open the nutrition screen. It now defaults to **Food Feedback / 进食反馈**, showing:
1. Most recent completed food / 最近吃下什么
2. Three strongest actual gains / 实际增加最多的三项
3. Today's target coverage (x/25) / 今日达标项数
4. Water and salt reserves (0–100) / 水盐储备
5. Arrow navigation to all 27 detailed indicators / 左右切换详细营养数据

The original eight detailed pages remain available. Snapshot values come from server-owned NBT and are sent in the revised v4 network payload. Reopening N requests fresh data.

## Food profile fixes / 数据修复
- All 404 food assignments and templates are checked for valid IDs, category references, nonnegative 0–10 scores and override validity.
- Added/expanded item-specific game balance for 24 selected raw crops to reduce identical scores between unrelated vegetables. No clinical nutrient accuracy is claimed.
- Fixed black pepper being treated like a full serving of vegetables.
- Fixed onion vegetable pie and stuffed pepper recipe categories to include grain ingredients.
- Removed false fermented-food category from non-fermented tomato sauce.
- Preserved the earlier eggplant/eggplant curry correction.
- Items without curated overrides still use category templates. This is a **game abstraction**, not a measured food database.

## Compatibility / 兼容性
Existing nutrition save keys, 27 indicator order, water/salt ownership, and all food registry IDs remain unchanged. The mod's internal network protocol has been bumped to 4 for the expanded packet; client and server must both use the same updated mod version.

## Remaining tests / 尚需测试
CI checks compilation, static catalog integrity, network structure and server boot. Client-side visual screenshots, complete single-player consumption tests, multiplayer version matching, and balance over many Minecraft days must still be performed before merging or labeling a full nutrition release.
