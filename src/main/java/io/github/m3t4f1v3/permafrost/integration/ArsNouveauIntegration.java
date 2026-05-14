package io.github.m3t4f1v3.permafrost.integration;

import io.github.m3t4f1v3.permafrost.spell.FrostBurnSpell;
import net.minecraftforge.fml.ModList;

public class ArsNouveauIntegration {

    private ArsNouveauIntegration() {
    }
    
    public static boolean isArsNouveauLoaded() {
        return ModList.get().isLoaded("ars_nouveau");
    }

    public static void register() {
        if (!isArsNouveauLoaded()) return;

        FrostBurnSpell.register();
    }

    public static int getFrostBurnManaCost() {
        return 1200;
    }

    public static int getFrostBurnCooldown() {
        return 30;
    }

    public static double getFrostBurnRange() {
        return 32.0;
    }

    public static int getFrostBurnRadius() {
        return 3;
    }
}
