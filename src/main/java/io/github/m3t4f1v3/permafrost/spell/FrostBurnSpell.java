package io.github.m3t4f1v3.permafrost.spell;

import io.github.m3t4f1v3.permafrost.Permafrost;
import io.github.m3t4f1v3.permafrost.integration.ArsNouveauIntegration;
import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.setup.registry.APIRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FrostBurnSpell extends AbstractEffect {

    public static final FrostBurnSpell INSTANCE = new FrostBurnSpell();

    private static final float TEMPERATURE = 3500F;

    private FrostBurnSpell() {
        super(ResourceLocation.fromNamespaceAndPath("permafrost", "glyph_frostburn"), "Frost Burn");
    }

    public static void register() {
        APIRegistry.registerSpell(INSTANCE);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter,
            SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (world.isClientSide) {
            return;
        }

        for (BlockPos targetPos : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult,
                spellStats)) {
            double baseTemp = TEMPERATURE * (spellStats.getAmpMultiplier() + 1.);
            if (ModList.get().isLoaded("thermodynamica")) {
                ThermodynamicaIntegration.applyHeatToSimulation(world, targetPos,
                        baseTemp);
            }
            else {
                float distance = targetPos.distManhattan(rayTraceResult.getBlockPos());
                float temperature = (float) baseTemp;
                if (distance > 0.) {
                    temperature /= distance;
                }
                Permafrost.melt(world, targetPos, temperature);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return ArsNouveauIntegration.getFrostBurnManaCost();
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE, AugmentPierce.INSTANCE,
                AugmentSensitive.INSTANCE);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }

    @Override
    public String getBookDescription() {
        return "DESCRIBE ME";
    }

}
