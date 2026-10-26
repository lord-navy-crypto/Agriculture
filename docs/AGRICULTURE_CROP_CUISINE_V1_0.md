# Agriculture Crop Cuisine v1.0

## Goal

Connect the existing 100-crop agriculture catalog to a usable cooking loop without misclassifying fiber crops as food. The catalog currently registers 88 crop produce items as edible foods and 12 non-food botanical/material outputs.

## Recipes

### Everyday crop meals
- Garden Salad — leafy greens, tomato, cucumber
- Farmhouse Vegetable Stew — an agriculture vegetable, potato, carrot
- Roasted Root Vegetables — a root crop, carrot, canola oil
- Garden Stir-Fry — an agriculture vegetable, garlic, canola oil
- Farmer's Skillet — any registered edible crop produce, potato, carrot
- Rice and Vegetable Bowl — rice, an agriculture vegetable, carrot
- Barley Vegetable Soup — barley, carrot, onion
- Mixed-Grain Porridge — a grain, sugar, milk
- Hearty Bean Stew — a legume, onion, carrot

### Crop-specific dishes
- Rice-Stuffed Pepper, Eggplant Curry, Pumpkin Soup, Cabbage Rolls
- Mixed-Berry Pie, Fresh Fruit Salad, Quinoa Salad
- Cornbread, Sweet-Corn Fritters, Buckwheat Pancakes
- Honey-Roasted Sweet Potato, Cassava Flatbread, Taro Dumplings, Yam Cakes
- Chickpea Hummus, Peanut Sauce, Roasted Seed Mix
- Tomato Sauce, Herb Bread, Honey Mustard Dressing

### Botanical drinks and processed ingredients
- Garden Herbal Tea, Honey-Sweetened Tea, Farmhouse Coffee
- Canola Oil and Safflower Oil are processed ingredients used in crop cooking.

## Coverage rules

- The `gugugaga:cooking/any_crop_food` item tag includes all 88 existing agriculture crop foods. The Farmer's Skillet recipe gives every crop food at least one cooking use.
- Focused tags group leafy greens, vegetables, roots, grains, legumes, berries, fruits, and culinary herbs for more specialized recipes.
- Cassava, taro, and yam remain processing ingredients instead of being directly edible.
- Cotton and flax remain fiber materials and are intentionally excluded from food tags.
- Existing marine and fungal dishes remain separate, while this set fills the missing general-agriculture cuisine layer.

## Validation

`qa/validate_crop_cuisine_v1_0.py` checks catalog IDs, food registration, all 88 crop-food tag entries, recipe outputs, bilingual names, item models, and intended material handling. `tools/crop-cuisine-textures.gradle` generates distinct 16x16 pixel-art icons and `build/reports/agriculture/crop-cuisine-item-contact-sheet.png`; the generated-art validator checks dimensions, alpha, uniqueness, and contact-sheet output. The real-server smoke test also requires the crop-cuisine runtime initialization marker.
