# Combat stats

Use `/teststats` in game to view your current equipped totals. Percentages use **1 = 1%** throughout the code and commands.

## Player base values

`ModStats.java` defines the defaults: Damage **1**, Critical Chance **0%**, Defence **0**, Resistance **0%**, Evasion **0%**. Item bonuses add to these values. Base values and permanent modifiers are saved with the player and preserved through respawn; equipment bonuses are recalculated.

Damage uses `minecraft:attack_damage` and Defence uses `minecraft:armor`. The other attributes are `testmod:critical_chance`, `testmod:resistance`, and `testmod:evasion`. This preserves equipment slots, attack cooldown, ordinary tooltips, and the armour bar. Damage and Defence retain vanilla attribute limits (2048 and 30); percentage stats cap at 100%.

With cheats/operator permissions, change an individual player's base stats with:

```mcfunction
attribute @s minecraft:attack_damage base set 1
attribute @s minecraft:armor base set 0
attribute @s testmod:critical_chance base set 1
attribute @s testmod:resistance base set 5
attribute @s testmod:evasion base set 2
```

Changing a default constant affects newly created base attributes. Use `/attribute` to change an existing player's saved base value.

## Item values

Edit `ModItemStats.modifyItemDefaults()`. The same helpers accept vanilla items and your `DeferredItem` registrations; existing ordinary item stacks also receive the new item defaults after restarting.

```java
// Total damage with default player base 1; critical chance in percent.
weapon(event, Items.WOODEN_SWORD, 5.0D, 1.0D);
weapon(event, ModItems.TEST_SWORD, 7.0D, 1.0D);

// Bonus damage per arrow, critical chance in percent.
rangedWeapon(event, ModItems.TEST_CUSTOMBOW_ITEM, 0.0D, 1.0D);

// Defence, Resistance %, Evasion %; uses the item's own armour slot.
armour(event, ModItems.TEST_HELMET, 5.0D, 2.0D, 1.0D);
```

`weapon(..., 5, ...)` installs an item bonus of 4. The normal base player deals **1 + 4 = 5** damage. A player whose base Damage is 3 deals **3 + 4 = 7** with that sword. Attack cooldown and enchantments still affect melee damage.

Unlisted items keep their existing attack damage and armour values and contribute no new percentage stats. To change vanilla armour, for example, call `armour(event, Items.IRON_CHESTPLATE, 6, 2, 1)`. Holding armour in a hand does not grant its armour bonuses.

The example custom armour keeps its existing Defence values (helmet 5, chestplate 9, leggings 7, boots 5) and gives each piece 2% Resistance and 1% Evasion. The complete set totals **26 Defence, 8% Resistance, 4% Evasion**. Other vanilla armour starts with 0% Resistance/Evasion; its usual armour points supply Defence.

## Combat rules

- **Critical Chance:** fully charged standard melee attacks roll the equipped chance for **1.5×** damage, replacing guaranteed jumping criticals. Spear jabs/charges roll through their own damage path. Arrow hits roll the snapshotted chance and also deal 1.5× damage on success. Bow charge no longer grants a separate vanilla critical bonus.
- **Defence:** subtracts a flat amount, down to zero, in place of vanilla's armour/toughness reduction. Armour durability and the armour bar still work. Armour-bypassing damage continues to bypass Defence.
- **Resistance:** reduces the remaining damage by its percentage, after Defence and the normal Resistance potion/Protection enchantment reductions, before absorption hearts. Sources tagged to bypass resistance or invulnerability bypass this custom reduction too.
- **Evasion:** rolls once per incoming combat hit to cancel it entirely. It works against attacks from entities and projectiles, including arrows with no owner. It does not evade ordinary fall, hunger, fire tick, or void damage. Normal damage immunity frames still apply.

For example, a **10** damage hit against **3 Defence** and **20% Resistance** deals **(10 - 3) × 0.8 = 5.6** damage before absorption. Defence can fully absorb weaker hits; tune item values accordingly.

Arrow shots store their firing stats when spawned, including base/permanent bonuses and the actual firing weapon. Switching weapons afterwards cannot increase a shot's damage or critical chance. A bow held in the off-hand uses that bow's stats, rather than a sword in the main hand. Ranged Damage is a bonus to the arrow's existing speed-dependent damage, not a replacement for its physics.

## Damage numbers

Player attacks show floating damage numbers above the victim: **white for normal hits**, **yellow for critical hits**. Numbers rise and fade over 25 ticks (about 1.25 seconds). They use final health/absorption damage after reductions, cap overkill to the victim's remaining health, and do not appear for dodges or fully blocked hits. The display supports regular melee, spear attacks, and arrows. Whole numbers omit decimals; fractional damage uses one decimal, and positive damage below 0.1 displays as `<0.1`.

The appearance lives in `DamageNumberStyle.java` and `client/DamageNumberRenderer.java`. Server hit tracking is in `DamageNumbers.java`. The visuals are client-only and do not create saved entities.

## Verification

```powershell
.\gradlew.bat build --offline
.\gradlew.bat runGameTestServer --offline
```

`build` includes the dependency-free `statMathTest` checks. The game tests exercise real equipment updates, melee damage, critical rolls, mitigation, evasion, and arrow stat snapshots on a dedicated test server.
