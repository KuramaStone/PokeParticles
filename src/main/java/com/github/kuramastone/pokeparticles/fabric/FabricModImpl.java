package com.github.kuramastone.pokeparticles.fabric;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PPModPlatform;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import com.github.kuramastone.pokeparticles.common.utils.StringUtils;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FabricModImpl extends PPModPlatform {

    public DustParticleEffect getRedstoneDustData(String data) {
        Vector3f color = StringUtils.splitIntoVec3(data);
        return new DustParticleEffect(color, 1.0f);
    }

    public DustColorTransitionParticleEffect getRedstoneDustTransitionData(String data) {
        String[] valueSplit = data.split("->");
        Vector3f color1 = StringUtils.splitIntoVec3(valueSplit[0]);

        Vector3f color2 = StringUtils.splitIntoVec3(valueSplit[1]);
        return new DustColorTransitionParticleEffect(color1, color2, 1.0f);
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
        // load transitioning color
        else if(particleNamespace.equalsIgnoreCase("minecraft:dust_color_transition")) {
            String data = info.getData() == null ? "0/0/0->255/255/255" : info.getData();
            particleEffect = getRedstoneDustTransitionData(data);
        }
        // load as normal particle
        else {
            try {
                particleEffect = ParticleEffectArgumentType.readParameters(
                        new StringReader(particleNamespace), PokeParticlesMod.getServer().getRegistryManager());
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
