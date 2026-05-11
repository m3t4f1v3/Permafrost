# Permafrost Mod - Implementation Guide

## Project Structure
```
src/main/java/io/github/m3t4f1v3/permafrost/
├── Permafrost.java (main mod class - UPDATED)
├── block/
│   └── PermafrostIceBlock.java (core ice block logic)
├── spell/
│   └── MeltEternalSpell.java (Ars Nouveau integration)
└── integration/
    ├── PowerGridIntegration.java (Power Grid compatibility)
    ├── ArsNouveauIntegration.java (Ars Nouveau compatibility)
    └── INTEGRATION_SUGGESTIONS.md (extended features)
```

## Core Features Implemented

### 1. Unbreakable Permafrost Ice Block
**File**: `block/PermafrostIceBlock.java`

**Features**:
- Cannot be mined by players (strength = -1.0)
- Cannot be destroyed by entities
- Prevents water/lava from causing it to melt
- Resists natural biome melting
- Cannot be obtained via block picking (middle-click)
- Only obtainable in creative mode or via commands

**Block Properties**:
- Texture: `permafrost:block/permafrost_ice`
- Blast resistance: 3,600,000 (highest possible)
- Friction: 0.98 (like normal ice)
- Light level: 0 (opaque)
- Collision box: Full block

### 2. Power Grid Integration
**File**: `integration/PowerGridIntegration.java`

**How It Works**:
1. When a Power Grid heating coil is within 4 blocks of permafrost ice
2. If the coil is powered/active
3. The ice block will melt automatically
4. Visual effect: block break particles

**Configuration**:
```java
// Adjust these constants in PowerGridIntegration.java:
int searchRadius = 8;      // Search for coils within this range
double meltDistance = 4.0; // Effective melt range
```

**Heating Coil Detection**:
- Registry name: `power_grid:heating_coil`
- Must be powered (checks Power Grid's energy system)
- Temperature: ~1500°C

### 3. Ars Nouveau Melt Eternal Spell
**File**: `spell/MeltEternalSpell.java`

**Spell Details**:
- **Name**: Melt Eternal
- **Type**: Endgame heat spell
- **Mana Cost**: 1000 mana per cast
- **Cooldown**: 30 ticks (1.5 seconds)
- **Range**: 32 blocks
- **Area of Effect**: 3x3x3 blocks (with radius modifier)
- **Temperature**: 2000°C equivalent

**Suggested Glyph Composition**:
```
Base: Hex Bolt (targeted beam)
  + Projectile (adds range)
  + Amplify (increases effect)
  + Radius (area melting)
  = Melt Eternal
```

**Effect**:
- Instantly melts single permafrost ice block
- Can affect multiple blocks with radius modifier
- Visual: Block break particles + heat effect

## Compilation & Deployment

### Build the Mod
```bash
cd /media/Software/programming/java/modpack/Permafrost
./gradlew build
```

### Output JAR
```
build/libs/permafrost-[version].jar
```

### Install to Minecraft
Copy the JAR to your `mods` folder in your Minecraft instance.

## Dependencies Required

Add to your `build.gradle` if not already present:

```gradle
// Power Grid mod (optional, for integration)
deobfuscate 'curse.maven:power_grid-XXXXX:XXXXX'

// Ars Nouveau mod (optional, for spell integration)
deobfuscate 'curse.maven:ars_nouveau-XXXXX:XXXXX'
```

## Testing Checklist

- [ ] Block appears in creative inventory
- [ ] Block cannot be broken in survival mode
- [ ] Water/lava doesn't melt the block
- [ ] Hot biomes don't melt the block
- [ ] Power Grid coil melts nearby ice (if Power Grid installed)
- [ ] Ars Nouveau spell can melt ice (if Ars Nouveau installed)
- [ ] Block drop is air (no item drop)
- [ ] Multiple ice blocks can be placed
- [ ] Ice block doesn't cause FPS issues

## Configuration File (Future)

Create `permafrost-common.toml` in `config/`:

```toml
[permafrost]
    # Power Grid Integration Settings
    [permafrost.powergrid]
        # Search radius for heating coils
        searchRadius = 8
        
        # Effective melt distance (blocks)
        meltDistance = 4.0
        
        # Temperature required to melt
        meltTemperature = 1500
    
    # Ars Nouveau Integration Settings
    [permafrost.arsnouveau]
        # Spell mana cost
        manaCost = 1000
        
        # Spell cooldown (ticks)
        cooldown = 30
        
        # Spell range (blocks)
        range = 32.0
        
        # Area effect radius
        areaRadius = 3
```

## Advanced: Adding More Integrations

See `integration/INTEGRATION_SUGGESTIONS.md` for detailed plans for:
- Valkyrien Skies airship integration
- Create mod mechanical heating
- Mekanism advanced tech
- Immersive Engineering industrial approach
- And 6 more mod integrations

### Template for New Integration

```java
package io.github.m3t4f1v3.permafrost.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "permafrost", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NewModIntegration {
    
    public static boolean isModLoaded() {
        return ModList.get().isLoaded("modname");
    }
    
    // Integration logic here
}
```

## Troubleshooting

**Block doesn't appear in inventory**:
- Check that `Permafrost.java` has the registration
- Run `./gradlew build` again
- Clear Minecraft's shader cache

**Ice doesn't melt with coils**:
- Verify Power Grid mod is installed
- Check that coil is actually powered
- Verify block is within 4 blocks of coil

**Ars Nouveau spell doesn't work**:
- Verify Ars Nouveau mod is installed
- Check that spell was properly registered
- Ensure player has enough mana

**Performance issues with many blocks**:
- Reduce the search radius in PowerGridIntegration
- Implement block ticking limit
- Use spatial hash for block detection

## Next Steps

1. **Textures**: Create a unique texture for `permafrost_ice.png` (16x16)
   - Slightly different from regular ice
   - More ethereal/permanent appearance
   - Bluish tint

2. **Language File**: Add to `en_us.json`:
   ```json
   {
     "block.permafrost.permafrost_ice": "Permafrost Ice",
     "item.permafrost.permafrost_ice": "Permafrost Ice"
   }
   ```

3. **Recipes**: Add crafting/smelting recipes if needed

4. **Loot Tables**: Configure drop behavior (currently: no drops)

5. **Biome Integration**: Add to specific biomes or structures

## References

- Minecraft Forge Documentation: https://docs.minecraftforge.net/
- Power Grid Mod: https://modrinth.com/mod/power-grid
- Ars Nouveau: https://modrinth.com/mod/ars-nouveau
