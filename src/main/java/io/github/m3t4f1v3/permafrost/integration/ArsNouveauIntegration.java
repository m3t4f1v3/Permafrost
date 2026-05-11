package io.github.m3t4f1v3.permafrost.integration;

import io.github.m3t4f1v3.permafrost.spell.MeltEternalSpell;
import net.minecraftforge.fml.ModList;

public class ArsNouveauIntegration {

    private ArsNouveauIntegration() {
    }
    
    public static boolean isArsNouveauLoaded() {
        return ModList.get().isLoaded("ars_nouveau");
    }

    public static void register() {
        if (!isArsNouveauLoaded()) return;

        MeltEternalSpell.register();
    }

    public static int getMeltEternalManaCost() {
        return 1200;
    }

    public static int getMeltEternalCooldown() {
        return 30;
    }

    public static double getMeltEternalRange() {
        return 32.0;
    }

    public static int getMeltEternalRadius() {
        return 3;
    }

    public static float getMeltEternalTemperature() {
        return ThermodynamicaIntegration.getMeltEternalTemperature();
    }
}
