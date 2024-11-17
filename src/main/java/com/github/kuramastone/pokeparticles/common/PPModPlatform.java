package com.github.kuramastone.pokeparticles.common;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public abstract class PPModPlatform {

    /**
     * Spawns this particle type using this data
     */
    public abstract void createParticle(@Nullable UUID targetPlayer, PokemonEntity pe, ParticleInfo info, Vec3d spawnPos, float offsetX, float offsetY, float offsetZ, int count, float speed);

    public abstract File getDataFolder();
}
