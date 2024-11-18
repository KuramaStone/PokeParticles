package com.github.kuramastone.pokeparticles.common;

import com.cobblemon.mod.common.api.apricorn.Apricorn;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.bUtilities.yaml.YamlConfig;
import com.github.kuramastone.pokeparticles.common.particles.ParticleAnimation;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import com.github.kuramastone.pokeparticles.common.particles.effects.ParticleEffectType;
import com.github.kuramastone.pokeparticles.common.particles.effects.PetAura;
import com.github.kuramastone.pokeparticles.common.particles.effects.PetDualDisk;
import com.github.kuramastone.pokeparticles.common.particles.effects.PetOrbit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class PokeParticleManager {

    public static final Logger logger = Logger.getLogger("PokeParticles");


    private List<PokemonEntity> pokemonToApplyEffect;
    private List<PPCategory> particleCategory;
    private SplittableRandom random;
    private int updateEveryXTicks = 2;
    private PPModPlatform modPlatform;
    private Map<ParticleAnimation, ParticleEffectType> animationsByType;

    private long currentTick = -1;

    public PokeParticleManager(PPModPlatform modPlatform) {
        this.modPlatform = modPlatform;
        pokemonToApplyEffect = new ArrayList<>();
        random = new SplittableRandom();
        registerAnimations();
        loadParticleCategories();
    }

    private void registerAnimations() {
        animationsByType = new HashMap<>();
        animationsByType.put(ParticleAnimation.AURA, new PetAura());
        animationsByType.put(ParticleAnimation.DUAL_DISK, new PetDualDisk());
        animationsByType.put(ParticleAnimation.ORBIT, new PetOrbit());
    }

    public void loadParticleCategories() {
        YamlConfig config = new YamlConfig(modPlatform.getDataFolder(), null, null, "pokeparticles.yml", getClass());
        this.particleCategory = config.getKeys("").stream().map(config::getSection).map(PPCategory::new).toList();
    }

    public void onServerTick() {
        // tick starts at -1, so it will be 0.
        currentTick++;

        // update pokemon particles every X ticks
        if (currentTick % updateEveryXTicks == 0) {
            updatePokeParticle();
        }
    }

    private void updatePokeParticle() {
        for (PokemonEntity pe : new HashSet<>(pokemonToApplyEffect)) {
            if (!pe.isAlive() || pe.isRemoved() || pe.isRegionUnloaded()) {
                this.pokemonToApplyEffect.remove(pe);
            }

            List<PPCategory> category = getApplicablePPCategories(pe);

            for (PPCategory ppc : category) {
                applyParticleEffectTo(pe, ppc);
            }
        }
    }

    private void applyParticleEffectTo(PokemonEntity pe, PPCategory ppc) {
        ParticleInfo info = ppc.sampleParticle(random);
        List<Vec3d> particleLocations = this.animationsByType.get(ppc.getAnimation()).getParticleSpawns(pe, ppc, info);

        for (Vec3d pos : particleLocations) {
            modPlatform.createParticle(null, pe, info, pos, 0, 0, 0, 0, 0);
        }

    }

    private List<PPCategory> getApplicablePPCategories(PokemonEntity pe) {
        Map<String, List<PPCategory>> validCategories = new HashMap<>();

        // put applicable categories in different buckets
        for (PPCategory ppc : this.particleCategory) {
            if (ppc.canApplyTo(pe)) {
                if (ppc.ignoresPriority()) {
                    continue;
                }
                List<PPCategory> categories = validCategories.computeIfAbsent(ppc.getGroup(), groupName -> new ArrayList<>());
                categories.add(ppc);
            }
        }

        // get the highest priority in each bucket
        // Filter the highest priority in each bucket
        validCategories.replaceAll((group, categories) -> {
            int maxPriority = categories.stream()
                    .mapToInt(PPCategory::getPriority)
                    .max()
                    .orElse(Integer.MIN_VALUE);

            // only keep categories that are equal to the highest priority
            return categories.stream()
                    .filter(ppc -> ppc.getPriority() == maxPriority)
                    .toList();
        });

        // add categories that are ambivalent to group priority
        for (PPCategory ppc : this.particleCategory) {
            if (ppc.canApplyTo(pe)) {
                if (ppc.ignoresPriority()) {
                    List<PPCategory> categories = validCategories.computeIfAbsent("acceptingalltypesofgroupnames", groupName -> new ArrayList<>());
                    categories.add(ppc);
                }
            }
        }

        List<PPCategory> categories = new ArrayList<>();
        for (List<PPCategory> value : validCategories.values()) {
            categories.addAll(value);
        }

        return categories;
    }

    /**
     * Start tracking this entity to check if it should have effects applied
     * @param entity
     */
    public void addPokemon(PokemonEntity entity) {
        pokemonToApplyEffect.add(entity);
    }

    /**
     * Stop tracking this entity, ending all effects.
     * @param entity
     */
    public void removePokemon(PokemonEntity entity) {
        pokemonToApplyEffect.remove(entity);
    }
}
