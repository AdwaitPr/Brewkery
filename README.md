# Brewkery — Artisanal Coffee & Fresh Oven Bakes (Android App)

Brewkery is a modern, production-grade Android application built with **Jetpack Compose**, **Material 3**, and **Clean Architecture**. It faithfully replicates the interactive prototype and JSON specification defined for the Brewkery artisanal coffee house, providing a fluid menu browsing experience, item customization, in-memory cart with live tax/delivery calculations, and real-time active order tracking.

---

## 📱 Features & Highlights

1. **Screen 1 — Home / Menu (`MenuScreen`):**
   - Brand header with `BK` emblem, subtitle, and live Cart badge counter.
   - Dynamic store announcement banner:
     - Defaults to **Store Info** (`Delivery in 20 - 30 mins`, `$2.50 flat fee`, `Open`).
     - Automatically transitions to an **Active Order** banner (`#BK-XXXXX`, `PREPARING`, wait time, with one-tap navigation to the Order Status screen) once an order is placed.
   - Live search bar filtering items by title, tagline, and description.
   - Horizontal category filter row (`All Menu`, `☕ Hot Coffee`, `🧊 Cold Brews`, `🥐 Artisan Bakery`).
   - Rich menu cards featuring Coil image caching, bestseller badges, star ratings, review counts, and base prices.
   - Sticky bottom Cart bar when items are in the cart showing total item count, subtotal, and checkout CTA.

2. **Screen 2 — Item Detail / Customizer (`ItemDetailScreen`):**
   - Independent data fetching from `api/items/{id}.json` with loading spinner and error retry states.
   - Hero product image with status badge, key ingredients chips, and metadata pills (calories, prep time, rating).
   - Dynamic customization sections:
     - **Size Selection:** Tall (8 oz), Grande (12 oz), Venti (16 oz), Single Danish, Nitro Glass, etc., with real-time price modifiers.
     - **Milk / Spreads / Foam Options:** Barista blend oat milk, sweet cream foam, honey butter, artisan raspberry jam, etc.
     - **Sweetness / Serving Preference:** Sugar percentages (0%, 50%, 100%) or warmth levels (Warm & Crisp, Room Temperature).
   - Live recalculation of unit price and line item total: `(basePrice + sizeModifier + milkModifier) * quantity`.
   - Quantity stepper (`-` and `+`) and interactive favorite toggle button.

3. **Screen 3 — Cart (`CartScreen`):**
   - Itemized list of customized orders with size, milk, and sugar summaries.
   - Interactive quantity stepper that automatically removes line items when decremented below 1.
   - "Clear Cart" action and empty cart placeholder.
   - Transparent bill breakdown card:
     - `Subtotal`
     - `Delivery Fee: $2.50` (flat rate when cart is non-empty)
     - `Est. Tax (8.0%)` calculated via `BigDecimal(HALF_UP, 2 decimal places)`
     - `Total Payable`
   - "Place Order Now" CTA generating an order ticket.

4. **Screen 4 — Order Status (`OrderStatusScreen`):**
   - Animated coffee cup icon and order dispatched banner.
   - Generated ticket card `#BK-XXXXX` with an animated pulsing `PREPARING` badge.
   - Estimated wait time (`20 - 30 minutes`), total item count, and barista confirmation message.
   - "Back to Menu" action resetting navigation stack back to the Home screen.

---

## 🏛️ Architecture & Tech Stack

The application follows Google's recommended Modern Android Architecture:

```
app/
├── data/
│   ├── local/          # BrewkeryLocalStore (SharedPreferences + Gson persistence)
│   ├── remote/         # Retrofit API Service & DTOs
│   └── repository/     # BrewkeryRepository (Single Source of Truth)
├── domain/
│   ├── model/          # Pure Domain Entities (MenuItem, CartItem, OrderTicket, etc.)
│   └── CartCalculator  # Pure Kotlin financial calculator (BigDecimal HALF_UP)
├── di/
│   └── AppContainer    # Clean Manual Dependency Injection container
└── ui/
    ├── navigation/     # Jetpack Navigation Compose graph
    ├── screens/
    │   ├── menu/       # Screen 1: MenuScreen & MenuViewModel
    │   ├── detail/     # Screen 2: ItemDetailScreen & ItemDetailViewModel
    │   ├── cart/       # Screen 3: CartScreen & CartViewModel
    │   └── order/      # Screen 4: OrderStatusScreen
    └── theme/          # Color.kt, Theme.kt, Type.kt matching prototype styling
```

### 💾 Local Persistence (Bonus Requirement)
To prevent KSP and Kapt compiler/version conflicts with modern Kotlin and AGP releases, state persistence is handled by **`BrewkeryLocalStore`**:
- Implements a clean, robust, Gson-backed `SharedPreferences` storage layer.
- Automatically serializes and deserializes `cartItems` and `activeOrder` (`OrderTicket`).
- Ensures that when a user quits and restarts the application, their cart contents, quantities, selected customizations, and active order ticket remain completely intact.

---

## 🧪 Unit Tests

Unit tests for business logic and financial calculations are located in:
`app/src/test/java/com/clickretina/brewkery/domain/CartCalculatorTest.kt`

Key test cases verified:
1. **Prototype Exact Match Test:**
   - Caramel Macchiato (Grande 12 oz + Oat Milk + 50% Mild) = `$5.50`
   - Normandy Croissant (Single Piece + Classic No Spread + Warm & Crisp) = `$3.90`
   - `Subtotal`: `$9.40`
   - `Delivery Fee`: `$2.50`
   - `Est. Tax (8.0%)`: `9.40 * 0.08 = 0.752` rounded to `$0.75`
   - `Total Payable`: `9.40 + 2.50 + 0.75 = $12.65`
2. **Dynamic Unit Price Calculation:**
   - Verifies base price addition with size and milk modifiers.
3. **Composite Key Collision Prevention:**
   - Ensures customized items with different options produce unique composite keys.
   - Ensures identical customized items increment quantity instead of duplicating cart rows.
4. **Item Removal and Empty Cart Handling:**
   - Decrementing quantity below 1 removes the item.
   - Empty cart produces zero subtotal, delivery fee, tax, and total.

---

## 🛠️ Build & Verification Instructions

### 1. Prerequisites
- Java 17 or Java 21+ (`JAVA_HOME`)
- Android SDK installed (`sdk.dir` in `local.properties`)

### 2. Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

### 3. Assemble Debug APK
```bash
./gradlew assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🤖 How I Worked with AI

### Tools & Setup
- **Agent:** Google DeepMind Antigravity AI Coding Assistant
- **Harness / Plugins:** Sandboxed shell execution, filesystem inspection, static schema analysis
- **Core Strategy:** Test-Driven Development (TDD) and prototype reverse engineering

### 3 Prompts Used During Development
1. **Initial Schema & UI Extraction:**
   > *"Read `_spec/data.json` and ALL 6 item files (`_spec/item_1.json` through `_spec/item_6.json`). Map every exact JSON property name, category structure, and customization modifiers. Extract the exact CSS color hex codes, card radii, and JavaScript calculation logic from `_spec/prototype.html`."*
2. **Pure Domain Financial Logic:**
   > *"Build a pure Kotlin `CartCalculator` using `BigDecimal` (`RoundingMode.HALF_UP`, scale 2): generate deterministic composite keys per cart item, calculate dynamic unit prices, and calculate cart summary ($9.40 subtotal -> $2.50 delivery + $0.75 tax = $12.65 total)."*
3. **Architecture & Persistence Integration:**
   > *"Implement repository and manual DI exposing reactive StateFlows for cart items, bill summary, and active order ticket, persisted locally via `BrewkeryLocalStore` so state survives app restarts without KSP/Kapt conflicts."*

### 1 Thing AI Got Right
The AI extracted the exact CSS design tokens, card styles, and DOM hierarchy from `_spec/prototype.html` (including the dual-state Store Info vs. Active Order banner, the amber status badges, the terracotta gradient buttons, and the coffee mug icon dispatch layout) and accurately translated them into reusable Jetpack Compose components with zero stylistic deviation.

### 1 Bug Caught & Fixed
- **Composite Cart Key Collisions & Tax Rounding:**
  - *The Bug:* A naive cart implementation identified cart items solely by `itemId`. If a customer added a "Caramel Macchiato (Tall, Unsweetened)" and later added a "Caramel Macchiato (Venti, Extra Sweet)", the second item would simply increment the quantity of the first item, destroying the customer's distinct customizations. Additionally, floating-point arithmetic (`Double`) for the 8% tax calculation (`9.40 * 0.08 = 0.752`) caused IEEE 754 floating point inaccuracies that failed the prototype's `$12.65` total check.
  - *The Fix:* Created `CartCalculator.generateCompositeKey(itemId, sizeId, milkId, sugarLevel)` to guarantee uniqueness across custom combinations, and enforced `BigDecimal.setScale(2, RoundingMode.HALF_UP)` across all subtotals, tax rates, and delivery charges, ensuring mathematical precision down to the exact cent (`$9.40` subtotal + `$2.50` delivery + `$0.75` tax = `$12.65` total).
