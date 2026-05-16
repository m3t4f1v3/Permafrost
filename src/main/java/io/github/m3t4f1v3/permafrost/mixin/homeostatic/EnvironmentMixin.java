package io.github.m3t4f1v3.permafrost.mixin.homeostatic;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import homeostatic.common.block.BlockRadiation;
import homeostatic.common.temperature.Environment;
import io.github.m3t4f1v3.permafrost.Permafrost;
import io.github.m3t4f1v3.permafrost.integration.ThermodynamicaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(Environment.class)
public class EnvironmentMixin {
    @WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/PalettedContainer;get(III)Ljava/lang/Object;"))
    private static Object permafrost$useShipAwareBlockState(
            PalettedContainer<?> container,
            int x,
            int y,
            int z,
            Operation<BlockState> original,
            @Local(argsOnly = true) LocalRef<ServerLevel> worldRef,
            @Local(name = "blockpos") LocalRef<BlockPos> blockposRef) {
        ServerLevel world = worldRef == null ? null : worldRef.get();
        BlockPos blockpos = blockposRef == null ? null : blockposRef.get();

        AABB blockBound = new AABB(blockpos);
        
        if (world != null && blockpos != null) {
            Iterable<Ship> ships = VSGameUtilsKt.getShipsIntersecting(world, blockBound);
            for (Ship ship : ships) {
                Vec3 blockVec = blockpos.getCenter();
                Vector3d shipPosVec = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(blockVec));
                BlockPos shipPos = BlockPos.containing(shipPosVec.x, shipPosVec.y, shipPosVec.z);
                BlockState state = world.getBlockState(shipPos);
//                Permafrost.getLogger().debug("Checking block state at {} (world {}, ship {}) for radiation: {}", shipPos, world.dimension().location(), ship.getId(), state);
                if (state != null) {
                    blockposRef.set(shipPos);
                    return state;
                }
            }
        }

        return original.call(container, x, y, z);
    }
}