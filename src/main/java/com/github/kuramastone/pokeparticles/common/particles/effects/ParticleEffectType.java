package com.github.kuramastone.pokeparticles.common.particles.effects;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PPCategory;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface ParticleEffectType {

    List<Vec3d> getParticleSpawns(PokemonEntity entity, PPCategory ppc, ParticleInfo info);

}
