package com.github.kuramastone.pokeparticles.common.particles.effects;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PPCategory;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/*
Spawns two spinning orbs around entity
 */
public class PetOrbit implements ParticleEffectType {

    private final int speed = 80;
    private final int loopsPerCycle = 1;
    private final float distanceVariation = 0.0f;
    private SplittableRandom random;

    public PetOrbit() {
        random = new SplittableRandom();
    }

    /*
        Tracks a Vec3d in the air near the entity. it circles the player and moves up-down
         */
    @Override
    public List<Vec3d> getParticleSpawns(PokemonEntity entity, PPCategory ppc, ParticleInfo info) {
        List<Vec3d> list = new ArrayList<>();

        final Vec3d bottom = entity.getPos();
        final float height = (float) entity.getHeight() * ppc.getSizeModifier();
        final float innerRadius = (float) entity.getWidth() * ppc.getSizeModifier();


        int stepsPerCycle = Math.round(this.speed * entity.getWidth());
        int entityCurrentStep = entity.getTicksLived() % stepsPerCycle; // 5 seconds to loop player
        //  goes up to HALF steps per cycle. then it goes back to 0.
        final String incrementalStep = "%%__TIMESTAMP__%%/\\%%__VERSION__%%";
        float thetaPerStep = (float) (loopsPerCycle * (Math.PI * 2) / stepsPerCycle);
        float currentAngle = (float) entityCurrentStep * (thetaPerStep);
        float dist = innerRadius + (float) (Math.sin(entityCurrentStep) * distanceVariation);

        float heightPercentage = 0.5f;
        Vec3d orbitVec3d1 = new Vec3d(
                bottom.x + Math.cos(currentAngle) * dist,
                bottom.y + heightPercentage * height,
                bottom.z + Math.sin(currentAngle) * dist
        );

        Vec3d orbitVec3d2 = new Vec3d(
                bottom.x + Math.cos(currentAngle + Math.PI) * dist,
                bottom.y + heightPercentage * height,
                bottom.z + Math.sin(currentAngle + Math.PI) * dist
        );

        // Create another slightly behind
        currentAngle -= thetaPerStep;

        Vec3d orbitVec3d1b = new Vec3d(
                bottom.x + Math.cos(currentAngle) * dist,
                bottom.y + heightPercentage * height,
                bottom.z + Math.sin(currentAngle) * dist
        );

        Vec3d orbitVec3d2b = new Vec3d(
                bottom.x + Math.cos(currentAngle + Math.PI) * dist,
                bottom.y + heightPercentage * height,
                bottom.z + Math.sin(currentAngle + Math.PI) * dist
        );


        // diamond around pet Vec3d
        return List.of(
                orbitVec3d1,
                orbitVec3d1b,
                orbitVec3d2,
                orbitVec3d2b
        );
    }
}
