package com.github.kuramastone.pokeparticles.fabric;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PPModPlatform;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FabricModImpl extends PPModPlatform {

    public Object getRedstoneDustData(String data) {
        String[] val = data.split("/");
        float x = Float.parseFloat(val[0]) / 255.0f;
        float y = Float.parseFloat(val[1]) / 255.0f;
        float z = Float.parseFloat(val[2]) / 255.0f;
        return new DustParticleEffect(new Vector3f(x, y, z), 1.0f);
    }

    @Override
    public void createParticle(@Nullable UUID targetPlayer, PokemonEntity pe, ParticleInfo info, Vec3d spawnPos, float offsetX, float offsetY, float offsetZ, int count, float speed) {
        ParticleEffect particleEffect = getParticleEffect(info);


        List<ServerPlayerEntity> playersToSendTo = new ArrayList<>();
        if (targetPlayer == null) {
            playersToSendTo = pe.getServer().getPlayerManager().getPlayerList();
        }
        else {
            playersToSendTo.add(pe.getServer().getPlayerManager().getPlayer(targetPlayer));
        }

        for (ServerPlayerEntity player : playersToSendTo) {


            // generic particle
            ((ServerWorld) pe.getWorld()).spawnParticles(
                    player,
                    particleEffect,
                    false,
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    count,
                    offsetX, offsetY, offsetZ,
                    speed);
        }


    }

    private ParticleEffect getParticleEffect(ParticleInfo info) {
        String particleNamespace = info.getParticleNamespace();
        ParticleEffect particleEffect;
        // if particle is redstone, apply the color to it
        if (particleNamespace.equalsIgnoreCase("minecraft:dust")) {
            String data = info.getData() == null ? "255/255/255" : info.getData();
            particleEffect = (DustParticleEffect) getRedstoneDustData(data);
        }
        // load as normal particle
        else {
            try {
                particleEffect = ParticleEffectArgumentType.readParameters(
                        new StringReader(particleNamespace), Registries.PARTICLE_TYPE.getReadOnlyWrapper());
            }
            catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return particleEffect;
    }

    @Override
    public File getDataFolder() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), PokeParticlesMod.MODID);
    }
}
