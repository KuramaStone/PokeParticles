package com.github.kuramastone.pokeparticles.common.particles.effects;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PPCategory;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import com.github.kuramastone.pokeparticles.common.utils.ParticleMath;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class PetDualDisk implements ParticleEffectType {

    private SplittableRandom random;

    private List<Vec3d> precalculatedVec3ds;

    public PetDualDisk() {
        random = new SplittableRandom();

        double step = 0.1;
        precalculatedVec3ds = new ArrayList<>();
        double r = 3.5;
        for (double t = -r; t < r; t += step) {
            double x = sin(t);
            double z = cos(t);
            Vec3d v1 = new Vec3d(-x, 0.0D, -z);
            precalculatedVec3ds.add(ParticleMath.rotateAroundAxisX(v1, 45.0));
            precalculatedVec3ds.add(ParticleMath.rotateAroundAxisX(v1, -45.0));
        }
    }


    @Override
    public List<Vec3d> getParticleSpawns(PokemonEntity entity, PPCategory ppc, ParticleInfo info) {
        List<Vec3d> list = new ArrayList<>();


        Vec3d midpoint = entity.getPos().add(0, entity.getHeight() / 2, 0);

        double size = entity.getWidth() * ppc.getSizeModifier();
        double height = entity.getHeight() * ppc.getSizeModifier();
        for (Vec3d v : precalculatedVec3ds) {
            v = ParticleMath.rotateAroundAxisY(v, (-entity.getYaw() + 90.0));
            list.add(new Vec3d(
                    midpoint.x + v.getX() * size,
                    midpoint.y + v.getY() * (height / 1.8),
                    midpoint.z + v.getZ() * size
            ));
        }

        return list;
    }


}
