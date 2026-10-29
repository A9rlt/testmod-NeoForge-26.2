# Combat stats

Use `/teststats` in game to view your current equipped totals, including Attack Speed and Fire Rate. Critical Chance and Evasion use **1 = 1%**. Damage, Magic Damage, Defence, and Resistance are flat point values.

## Player base values

`stats/ModCustomStats.java` defines the defaults: Damage **1**, Magic Damage **0**, Critical Chance **0%**, Defence **0**, Resistance **0**, Evasion **0%**, Fire Rate **2 shots per second**. Item bonuses add to these values. Base values and permanent modifiers are saved with the player and preserved through respawn; equipment bonuses are recalculated.

Damage uses `minecraft:attack_damage` and Defence uses `minecraft:armor`. The other attributes are `testmod:magic_damage`, `testmod:critical_chance`, `testmod:resistance`, `testmod:evasion`, and `testmod:fire_rate`. Damage, Magic Damage, and Resistance cap at 2048, Defence at 30, and percentage stats at 100%. Fire Rate ranges from 0.1 to 100 shots per second, subject to Minecraft's 20 ticks per second.

With cheats/operator permissions, change an individual player's base stats with:

```mcfunction
attribute @s minecraft:attack_damage base set 1
attribute @s testmod:magic_damage base set 4
attribute @s minecraft:armor base set 0
attribute @s testmod:critical_chance base set 1
attribute @s testmod:resistance base set 5
attribute @s testmod:evasion base set 2
attribute @s testmod:fire_rate base set 2
```

Changing a default constant affects newly created base attributes. Use `/attribute` to change an existing player's saved base value.

## Item values

Edit `item/ModItemStats.modifyItemDefaults()`. The same helpers accept vanilla items and your `DeferredItem` registrations; existing ordinary item stacks also receive the new item defaults after restarting.

```java
// Physical total with default player base 1; critical chance in percent.
weapon(event, Items.WOODEN_SWORD, 5.0D, 1.0D);

// Physical total, additional Magic Damage, Critical Chance %.
weapon(event, ModItems.TEST_SWORD, 7.0D, 4.0D, 1.0D);

// Physical bonus per arrow, Magic Damage per arrow, Critical Chance %.
rangedWeapon(event, ModItems.TEST_CUSTOMBOW_ITEM, 0.0D, 4.0D, 1.0D);

// Defence, flat magic Resistance, Evasion %; uses the item's own armour slot.
armour(event, ModItems.TEST_HELMET, 5.0D, 2.0D, 1.0D);
```

`weapon(..., 5, ...)` installs an item bonus of 4. The normal base player deals **1 + 4 = 5** damage. A player whose base Damage is 3 deals **3 + 4 = 7** with that sword. Attack cooldown and enchantments still affect melee damage.

Existing four-argument `weapon` and `rangedWeapon` calls still work, with zero additional Magic Damage. The test staff is configured with **1 physical damage**, **5 magic damage**, and **0% critical chance**. Left-click fires its beam using Magic Damage only; the client suppresses the ordinary melee attack so it cannot hit twice. Other melee weapons configured with both stats deal both portions together.

Unlisted items keep their existing attack damage and armour values and contribute no new custom stats. Your current leather armour configuration supplies 1/2/1/1 Defence and zero Resistance/Evasion. To add 2 flat Resistance and 1% Evasion to a vanilla chestplate, use `armour(event, Items.IRON_CHESTPLATE, 6, 2, 1)`. Holding armour in a hand does not grant its armour bonuses.

## Combat rules

- **Critical Chance:** fully charged standard melee attacks roll the equipped chance for **1.5×** damage, replacing guaranteed jumping criticals. Spear jabs/charges roll through their own damage path. Arrow hits roll the snapshotted chance and also deal 1.5× damage on success. Bow charge no longer grants a separate vanilla critical bonus.
- **Magic Damage:** adds a separate magic portion to physical weapon hits, including arrows and spear attacks. Standard melee magic follows attack cooldown. Weapon critical hits multiply both portions. A native magic damage source supplies its own magic amount; caster Magic Damage is not added again. Attributed magic attacks can roll the caster's Critical Chance and deal 1.5 times damage before Resistance.
- **Defence:** subtracts a flat amount from the physical portion only, down to zero, in place of vanilla's armour/toughness reduction. Armour durability and the armour bar still work. Armour-bypassing damage continues to bypass Defence.
- **Resistance:** subtracts a flat amount from the magic portion only, down to zero. Excess Resistance cannot reduce physical damage. It has no effect on physical-only attacks, fire tick, fall damage, or other non-magic sources. Sources tagged to bypass resistance or invulnerability bypass this custom reduction too. The vanilla Resistance potion remains a separate percentage effect.
- **Evasion:** rolls once per incoming combat hit to cancel it entirely. It works against attacks from entities and projectiles, including arrows with no owner. It does not evade ordinary fall, hunger, fire tick, or void damage. Normal damage immunity frames still apply.
- **Fire Rate:** controls staff shots per second. The server enforces a cooldown of `ceil(20 / Fire Rate)` ticks, so the default 2 shots per second permits one shot every 10 ticks. The beam hits the first living target in its path, stops at solid blocks, and uses the caster's current Magic Damage and Critical Chance.

For example, a hit with **5 physical + 4 magic** against **2 Defence + 1 Resistance** deals **max(0, 5 - 2) + max(0, 4 - 1) = 6** damage, displaying a physical **3** and magic **3**. A 10-point magic hit against 3 Resistance deals 7; a 10-point physical hit ignores Resistance entirely. Both portions resolve in one damage event, preserving shields, immunity frames, and absorption. Partial blocks and immunity-frame adjustments preserve their original proportions. Vanilla potion/enchantment reductions apply after the flat reductions; the displayed portions use their proportions after Defence and Resistance.

Native magic damage types are listed in `data/testmod/tags/damage_type/magic_damage.json`: magic, indirect magic, wither, wither skull, dragon breath, and thorns. Add future custom damage types to the `testmod:magic_damage` tag. Armour bypass alone never makes damage magical, so falling and starvation are not mistaken for spells.

Arrow shots store their firing stats when spawned, including Magic Damage, base/permanent bonuses, and the actual firing weapon. Switching weapons afterwards cannot increase a shot's damage or critical chance. A bow held in the off-hand uses that bow's stats, rather than a sword in the main hand. Ranged physical Damage is a bonus to the arrow's existing speed-dependent damage, not a replacement for its physics.

## Damage numbers

Player attacks show separate floating damage numbers above the victim:

| Damage portion | Colour |
| --- | --- |
| Physical | Yellow |
| Physical critical | Yellow |
| Magic | Blue (`0xFF0000FF`) |
| Magic critical | Purple (`0xFFAA00FF`) |

A hit dealing **1 physical + 4 magic** displays a yellow **1** and a blue **4**. Critical hits mark each surviving portion as critical. Fully prevented portions produce no number.

Numbers rise and fade over 25 ticks (about 1.25 seconds). Their sum uses final health/absorption damage after reductions, with overkill capped to the victim's remaining health. Dodges and fully blocked hits produce no numbers. The display supports regular melee, spear attacks, arrows, and staff magic. Whole numbers omit decimals; fractional damage uses one decimal, and positive damage below 0.1 displays as `<0.1`.

Edit the colours and formatting in `network/style/DamageNumberStyle.java`. `client/DamageNumberRenderer.java` draws the client visuals, and `network/style/DamageNumbers.java` tracks server hits. Packets live in `network/payload` and register through `network/ModNetworking.java`. Combat events live in `event/ModCombatEvents.java`; the obsolete copies in `stats` have been removed. The visuals do not create saved entities.

## Verification

```powershell
.\gradlew.bat build --offline
.\gradlew.bat runGameTestServer --offline
```

`build` includes the dependency-free `statMathTest` checks. The game tests exercise real equipment updates, melee damage, critical rolls, mitigation, evasion, and arrow stat snapshots on a dedicated test server.
