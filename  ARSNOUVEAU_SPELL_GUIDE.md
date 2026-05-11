# Melt Eternal - Ars Nouveau Spell Implementation Guide

## Overview
The **Melt Eternal** spell is an endgame Ars Nouveau spell designed to melt the unbreakable Permafrost Ice blocks. This spell represents the pinnacle of heat magic mastery.

## Spell Composition

### Glyph Breakdown

| Component | Glyph | Purpose |
|-----------|-------|---------|
| **Base** | Hex Bolt | Provides the targeted beam attack |
| **Modifier 1** | Projectile | Adds ranged capability (32 blocks) |
| **Modifier 2** | Amplify | Increases heat intensity |
| **Modifier 3** | Radius | Affects area of blocks (3x3x3) |
| **Finisher** | *Optional: Spell Turret* | Allows automated casting |

### Alternative Glyph Combinations

**Option A: Single Target**
```
Hex Bolt → Projectile → Amplify = Single powerful melting beam
Mana Cost: 800
Cooldown: 30 ticks
```

**Option B: Area Attack**
```
Hex Bolt → Projectile → Amplify → Radius = Area melt effect
Mana Cost: 1200
Cooldown: 45 ticks
```

**Option C: Continuous Casting**
```
Hex Bolt → Projectile → Amplify → Spell Turret = Auto-melting turret
Mana Cost: 1400 per activation
Cooldown: 60 ticks
```

## Spell Properties

```
Name:              Melt Eternal
Rarity:            Legendary (Purple/Gold)
Type:              Attack/Utility
Tier:              Endgame
Mana Cost:         1000 mana per cast
Cooldown:          30 ticks (1.5 seconds)
Cast Time:         0 ticks (instant)
Range:             32 blocks
Area Radius:       3 blocks
Effective Heat:    2000°C equivalent
Durability Loss:   50 per cast (high durability requirement)
Armor Enchant:     None required (but Mana cost reduction helps)
```

## Crafting Recipe

### Spell Scroll Recipe (Example)
```
Crafting Table:

[Shimmersilk] [Primordial Pearl] [Shimmersilk]
[Blazing Quartz] [Source Gem] [Blazing Quartz]
[Amethyst Shard] [Essence of Fire] [Amethyst Shard]

Result: Melt Eternal Spell Scroll
```

### Focus Item Recipe (If using Focus system)
```
Smithing Table:

Mana Ammo + Melt Eternal Base
= Melt Eternal Focus

Components:
- Mana Ammo: Mana Shard x64 (from Ars Nouveau)
- Melt Eternal Base:
  * Shimmersilk x3
  * Primordial Pearl x1
  * Source Gem x1
  * Essence of Fire x2
  * Gold Ingot x2
```

## Implementation Notes

### For Ars Nouveau Devs/Modders

The spell can be registered in several ways depending on Ars Nouveau version:

#### Method 1: JSON Registration (Recommended)
```
assets/permafrost/recipes/melt_eternal.json

{
  "type": "ars_nouveau:spell_recipe",
  "output": {
    "id": "permafrost:melt_eternal",
    "name": "Melt Eternal"
  },
  "glyphs": [
    "ars_nouveau:hex_bolt",
    "ars_nouveau:projectile", 
    "ars_nouveau:amplify",
    "ars_nouveau:radius"
  ],
  "mana_cost": 1000,
  "cooldown": 30,
  "range": 32,
  "area_radius": 3
}
```

#### Method 2: Java Registration
```java
public static void registerMeltEternalSpell() {
    SpellRecipe recipe = new SpellRecipe()
        .withGlyph(GlyphRegistry.get("hex_bolt"))
        .withGlyph(GlyphRegistry.get("projectile"))
        .withGlyph(GlyphRegistry.get("amplify"))
        .withGlyph(GlyphRegistry.get("radius"))
        .withManaCost(1000)
        .withCooldown(30)
        .withRange(32)
        .withAreaRadius(3)
        .withCallback(MeltEternalSpell::onCast);
    
    SpellRegistry.register("melt_eternal", recipe);
}
```

#### Method 3: Event-Based Registration
```java
@SubscribeEvent
public static void registerSpells(SpellRegistryEvent event) {
    event.register("melt_eternal", createMeltEternalSpell());
}
```

## Spell Behavior

### On Successful Cast
1. **Targeting Phase**: Player looks at a block within 32 blocks
2. **Activation**: Player casts the spell (consumes 1000 mana)
3. **Visual Effect**: 
   - Heat wave particle effect
   - Sound: Whoosh + crackling fire
   - Block break particles
4. **Effect Zone**: 3x3x3 area (if radius glyph used)
5. **Result**: Permafrost Ice blocks melt and disappear (no drops)
6. **Cooldown**: 30 seconds before next cast

### On Failed Cast
- **Not enough mana**: Spell fizzles, no mana consumed
- **Block too far**: Purple particle ring (out of range)
- **Wrong block type**: No effect (can only melt permafrost ice)
- **In cooldown**: Red particle effect, spell delayed

## Performance Considerations

### Optimization Tips
1. **Single block check**: Before area effect
2. **Cached block lookups**: Pre-check 3x3x3 area
3. **Lazy evaluation**: Don't check non-permafrost blocks
4. **Particle limits**: Reduce particles in multiplayer
5. **Mana check**: Verify mana before heavy calculations

### Network Sync
- Cast on server side only
- Sync block destruction to clients
- Use `world.destroyBlock()` for proper networking

## Balance Considerations

### Why This Cost?
- **1000 Mana**: High cost prevents spam
- **30 Tick Cooldown**: ~1.5 second wait
- **Endgame Requirement**: Requires advanced Ars spellcasting
- **Area Effect**: Compensates for high resource investment

### Comparison to Other Spells
- **Damage Spells**: Comparable mana cost
- **Utility Spells**: Higher mana cost (more powerful)
- **AOE Spells**: Similar cooldown

### Preventing Cheese
- ✓ Permafrost ice is unbreakable (can't use as resource)
- ✓ High mana cost (prevents infinite spam)
- ✓ Cooldown timer (prevents rapid melting)
- ✓ Area limited (prevents all-at-once)

## Texture & Visuals

### Spell Scroll Appearance
- Color: Deep blue with gold accents
- Icon: Snowflake transitioning to flame
- Animated: Gentle swirl of heat/cold particles

### Casting Animation
- Caster raises staff/wand upward
- Blue-to-orange gradient beam
- Heat distortion effect along beam path
- Impact flash at target

### Impact Particles
- Blue ice shards
- Orange/red heat waves
- White steam puffs
- Block break particles

## Multiplayer Considerations

### Server-Side Implementation
```java
// Only execute on server
if (!world.isClientSide) {
    meltPermafrostIce(world, targetPos);
    world.playSound(null, targetPos, SoundEvents.GENERIC_BURN, 
                    SoundSource.BLOCKS, 1.0f, 1.0f);
}
```

### Packet Sync
- Send block destruction packet to all players
- Send particle packets for visual feedback
- Sync cooldown timer

### Anti-Grief Measures
- Verify player has permission (creative mode check)
- Log ice melting events if audit logging enabled
- Consider claim system compatibility (coming soon)

## Addon/Extension Ideas

### Extension 1: Spell Variant - Melt Eternal (Single Target)
```
Cost: 800 mana
Cooldown: 20 ticks
Area: Single block only
Perfect for precise melting
```

### Extension 2: Mana-Efficient Version
```
Cost: 600 mana
Cooldown: 60 ticks
Area: 2x2x2
Slower but cheaper
```

### Extension 3: Instant Spell (One-Time)
```
Cost: 2000 mana
Cooldown: 120 ticks
Area: 5x5x5
Maximum power version
```

## Testing Checklist

- [ ] Spell appears in spell selection
- [ ] Spell can be cast (mana consumed)
- [ ] Single target mode works
- [ ] Area mode works (if radius included)
- [ ] Cooldown timer functions
- [ ] Permafrost ice melts
- [ ] Only permafrost ice melts (not regular ice)
- [ ] Particles display correctly
- [ ] Sound effects play
- [ ] Works in multiplayer
- [ ] Works on servers
- [ ] No crash on invalid targets
- [ ] Mana cost is correct
- [ ] Cooldown duration is correct

## References

- Ars Nouveau GitHub: https://github.com/baileyholl/Ars-Nouveau
- Modrinth Page: https://modrinth.com/mod/ars-nouveau
- Curse Forge: https://www.curseforge.com/minecraft/mods/ars-nouveau
- Documentation Wiki: (varies by version)

## Version Compatibility

This spell is designed for:
- **Ars Nouveau**: 3.0.0+
- **Minecraft**: 1.19.2 - 1.20.x
- **Forge**: 41.0.0+

## Support

For integration questions, refer to:
1. Main Permafrost mod author
2. Ars Nouveau community/Discord
3. Check if spell registration actually fires (enable debug logging)
