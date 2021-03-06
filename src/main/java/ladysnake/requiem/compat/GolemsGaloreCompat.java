/*
 * Requiem
 * Copyright (C) 2017-2021 Ladysnake
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
package ladysnake.requiem.compat;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import io.github.franiscoder.golemsgalore.entity.LaserGolemEntity;
import io.github.franiscoder.golemsgalore.entity.ai.laser.FireLaserGoal;
import ladysnake.requiem.api.v1.RequiemApi;
import ladysnake.requiem.api.v1.RequiemPlugin;
import ladysnake.requiem.api.v1.annotation.CalledThroughReflection;
import ladysnake.requiem.api.v1.entity.ability.MobAbilityConfig;
import ladysnake.requiem.api.v1.entity.ability.MobAbilityRegistry;
import ladysnake.requiem.common.entity.ability.TickingGoalAbility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public final class GolemsGaloreCompat implements RequiemPlugin {

    @CalledThroughReflection
    public static void init() {
        RequiemCompatibilityManager.<LaserGolemEntity>findEntityType(GolemsGalore.id("laser_golem"), object -> {
            RequiemApi.registerPlugin(new GolemsGaloreCompat(object));
        });
    }

    private final EntityType<LaserGolemEntity> laserGolemType;

    private GolemsGaloreCompat(EntityType<LaserGolemEntity> laserGolemType) {
        this.laserGolemType = laserGolemType;
    }

    @Override
    public void registerMobAbilities(MobAbilityRegistry registry) {
        registry.register(laserGolemType, MobAbilityConfig.<LaserGolemEntity>builder()
            .directAttack(golem -> new TickingGoalAbility<>(golem, new FireLaserGoal(golem), 20*2, 15, LivingEntity.class))
            .build());
    }
}
