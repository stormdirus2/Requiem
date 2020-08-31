/*
 * Requiem
 * Copyright (C) 2017-2020 Ladysnake
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses>.
 *
 * Linking this mod statically or dynamically with other
 * modules is making a combined work based on this mod.
 * Thus, the terms and conditions of the GNU General Public License cover the whole combination.
 *
 * In addition, as a special exception, the copyright holders of
 * this mod give you permission to combine this mod
 * with free software programs or libraries that are released under the GNU LGPL
 * and with code included in the standard release of Minecraft under All Rights Reserved (or
 * modified versions of such code, with unchanged license).
 * You may copy and distribute such a system following the terms of the GNU GPL for this mod
 * and the licenses of the other code concerned.
 *
 * Note that people who make modified versions of this mod are not obligated to grant
 * this special exception for their modified versions; it is their choice whether to do so.
 * The GNU General Public License gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version which carries forward this exception.
 */
package ladysnake.requiem.mixin.common.possession.player;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import ladysnake.requiem.api.v1.RequiemPlayer;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PossessorLivingEntityMixin extends Entity {
    @Shadow
    protected abstract float getEyeHeight(EntityPose pose, EntityDimensions size);

    public PossessorLivingEntityMixin(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @ModifyVariable(
        method = "travel",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/enchantment/EnchantmentHelper;getDepthStrider(Lnet/minecraft/entity/LivingEntity;)I"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
            ordinal = 0
        ),
        ordinal = 0
    )
    private float fixUnderwaterVelocity(float /* float_4 */ speedAmount) {
        if (this instanceof RequiemPlayer) {
            return ((RequiemPlayer) this).getMovementAlterer().getSwimmingAcceleration(speedAmount);
        }
        return speedAmount;
    }

    @Inject(method = "collides", at = @At("RETURN"), cancellable = true)
    private void preventSoulsCollision(CallbackInfoReturnable<Boolean> info) {
        if (RemnantComponent.isSoul(this)) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    private void canClimb(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && this instanceof RequiemPlayer && this.horizontalCollision) {
            cir.setReturnValue(((RequiemPlayer) this).getMovementAlterer().canClimbWalls());
        }
    }

    @Inject(
        method = "fall",
        at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/LivingEntity;fallDistance:F", ordinal = 0),
        cancellable = true
    )
    private void onFall(double fallY, boolean onGround, BlockState floorBlock, BlockPos floorPos, CallbackInfo info) {
        if (world.isClient) return;

        Entity possessed = PossessionComponent.getPossessedEntity(this);
        if (possessed != null) {
            possessed.fallDistance = this.fallDistance;
            possessed.copyPositionAndRotation(this);
            possessed.move(MovementType.SELF, Vec3d.ZERO);
            // We know that possessed is a LivingEntity, Mixin will translate to that type automatically
            //noinspection ConstantConditions
            ((PossessorLivingEntityMixin) possessed).fall(fallY, onGround, floorBlock, floorPos);
        }
    }

    @Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
    private void adjustEyeHeight(EntityPose pose, EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        // This method can be called in the Entity constructor
        if (ComponentProvider.fromEntity(this).getComponentContainer() != null) {
            LivingEntity possessed = PossessionComponent.getPossessedEntity(this);
            if (possessed != null) {
                //noinspection ConstantConditions
                cir.setReturnValue(((PossessorLivingEntityMixin) (Object) possessed).getEyeHeight(pose, possessed.getDimensions(pose)));
            }
        }
    }
}
