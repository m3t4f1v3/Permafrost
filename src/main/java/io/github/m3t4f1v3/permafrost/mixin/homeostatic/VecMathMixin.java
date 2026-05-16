package io.github.m3t4f1v3.permafrost.mixin.homeostatic;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.CompatUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import homeostatic.util.VecMath;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(VecMath.class)
public class VecMathMixin {
    @Inject(method = "getDistance", at = @At("HEAD"), cancellable = true, remap = false)
    private static void permafrost$useShipAwareDistance(ServerPlayer sp, Vector3d vPos,
            CallbackInfoReturnable<Double> cir) {
        vPos = CompatUtil.INSTANCE.toSameSpaceAs(sp.level(), vPos, VectorConversionsMCKt.toJOML(sp.getEyePosition()));
        cir.setReturnValue(vPos.distance(VectorConversionsMCKt.toJOML(sp.getEyePosition())));
    }

    @Inject(method = "isBlockObscured", at = @At("HEAD"), cancellable = true, remap = false)
    private static void permafrost$useShipAwareClip(
            ServerPlayer sp,
            Vec3 blockVec,
            CallbackInfoReturnable<Boolean> cir) {
        ClipContext clipContext = new ClipContext(sp.getEyePosition(),
                CompatUtil.INSTANCE.toSameSpaceAs(sp.level(), blockVec, sp.getEyePosition()),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, sp);
        cir.setReturnValue(
                RaycastUtilsKt.clipIncludeShips(sp.level(), clipContext).getType() != HitResult.Type.MISS);
    }
}
