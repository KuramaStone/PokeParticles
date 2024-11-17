package com.github.kuramastone.pokeparticles.common.particles.effects;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PPCategory;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class PetAura implements ParticleEffectType {

    private SplittableRandom random;

    public PetAura() {
        random = new SplittableRandom();
    }

    public float getHeight(PokemonEntity entity) {
        return (float) entity.getHeight();
    }

    @Override
    public List<Vec3d> getParticleSpawns(PokemonEntity entity, PPCategory ppc, ParticleInfo info) {
        List<Vec3d> list = new ArrayList<>();

        final Vec3d bottom = entity.getPos();

        int totalParticles = (int) Math.ceil(info.getAmountPerCubicBlock() * entity.getWidth() * entity.getWidth() * entity.getHeight());

        float width = entity.getWidth() * ppc.getSizeModifier();
        float height = getHeight(entity) * ppc.getSizeModifier();

        for (int i = 0; i < totalParticles; i++) {
            // choose a random angle and a random height. spawn it outwards
            float rndAngle = (float) (random.nextFloat() * Math.PI * 2);

            float rndY = random.nextFloat() * height;

            float x = (random.nextFloat()-0.5f) * width;
            float z = (random.nextFloat()-0.5f) * width;
            list.add(new Vec3d(bottom.x + x, bottom.y + rndY, bottom.z + z));
        }

        return list;
    }
}
