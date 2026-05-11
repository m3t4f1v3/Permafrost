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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MeltEternalSpell extends AbstractEffect {

    public static final MeltEternalSpell INSTANCE = new MeltEternalSpell();

    private MeltEternalSpell() {
        super(ResourceLocation.fromNamespaceAndPath("permafrost", "glyph_melt_eternal"), "Melt Eternal");
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
            BlockState state = world.getBlockState(targetPos);
            ThermodynamicaIntegration.applyHeatToSimulation(world, targetPos,
                    ArsNouveauIntegration.getMeltEternalTemperature());
            Permafrost.melt(rayTraceResult.getBlockPos(), state, world, targetPos);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return ArsNouveauIntegration.getMeltEternalManaCost();
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
        return "An endgame thermal glyph that drives hostile ice into nothingness.";
    }

}
