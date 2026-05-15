package io.github.m3t4f1v3.permafrost.mixin.homeostatic;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import homeostatic.util.VecMath;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@Mixin(VecMath.class)
public class VecMathMixin {
    @Inject(method = "getDistance", at = @At("HEAD"), cancellable = true, remap = false)
    private static void permafrost$useShipAwareDistance(ServerPlayer sp, Vector3d vPos,
            CallbackInfoReturnable<Double> cir) {
        ServerLevel world = sp.serverLevel();
        Ship ship = VSGameUtilsKt.getShipManagingPos(world, vPos);
        if (ship != null) {
            vPos = ship.getShipToWorld().transformPosition(vPos);
        }
        cir.setReturnValue(vPos.distance(new Vector3d(sp.getX(), sp.getY(), sp.getZ())));
    }
}
