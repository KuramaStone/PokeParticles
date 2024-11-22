package com.github.kuramastone.pokeparticles.common;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.bUtilities.yaml.YamlConfig;
import com.github.kuramastone.pokeparticles.common.particles.ParticleAnimation;
import com.github.kuramastone.pokeparticles.common.particles.ParticleInfo;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public class PPCategory {

    private final List<String> targetAspects;
    private List<ParticleInfo> particleInfoList;

    // if true, this category only applies to shinies
    private PokemonProperties pokemonProperties;
    private @Nullable List<String> targetPokemonNames;
    private int minimumLevel = 0;

    // how important this category is
    private boolean ignoresPriority = false;
    private @Nullable String group;
    private int priority;

    private float sizeModifier;

    private ParticleAnimation animation = ParticleAnimation.AURA;

    public PPCategory(YamlConfig section) {
        particleInfoList = loadParticleList(section);
        ignoresPriority = section.get("priority.ignores priority", false);
        priority = section.get("priority.priority", 0);
        group = section.get("priority.priority group", null);
        sizeModifier = section.get("size modifier", 1.0d).floatValue();

        minimumLevel = section.get("conditions.minimum level", 0);
        targetPokemonNames = loadTargetPokemonNames(section);

        boolean mustBeShiny = section.get("conditions.shiny only", false);
        String targetForm = section.get("conditions.target form", "any");
        this.targetAspects = loadTargetAspects(section);

        pokemonProperties = new PokemonProperties();
        pokemonProperties.setShiny(mustBeShiny ? Boolean.TRUE : null);
        if(!targetForm.equalsIgnoreCase("any"))
            pokemonProperties.setForm(targetForm);

        animation = ParticleAnimation.valueOf(section.get("animation", "AURA").toString());
    }

    private List<ParticleInfo> loadParticleList(YamlConfig section) {
        section = section.getSection("particles to spawn");
        return section.getKeys("")
                .stream()
                .map(section::getSection)
                .map(ParticleInfo::new)
                .filter(ParticleInfo::validate)
                .toList();
    }

    /**
     * Reads the pokemon name from a config section. Allows adapting of strings into String Lists
     * @param section
     * @return
     */
    private List<String> loadTargetPokemonNames(YamlConfig section) {
        String key = "conditions.target pokemon";
        if(section.containsKey(key)) {
            return section.getStringListOrString(key);
        }

        return null;
    }

    /**
     * Reads the pokemon name from a config section. Allows adapting of strings into String Lists
     * @param section
     * @return
     */
    private List<String> loadTargetAspects(YamlConfig section) {
        String key = "conditions.target aspects";
        if(section.containsKey(key)) {
            return section.getStringListOrString(key);
        }

        return null;
    }

    /**
     * @return True if this aura can apply to this {@link PokemonEntity}
     */
    public boolean canApplyTo(PokemonEntity pe) {
        boolean propertiesCheck = pokemonProperties.matches(pe);
        boolean nameCheck = checkName(pe.getPokemon().getSpecies().getName());
        boolean levelCheck = checkLevel(pe.getPokemon().getLevel());
        boolean aspectCheck = checkAspects(pe);

        return propertiesCheck && nameCheck && levelCheck && aspectCheck;
    }

    private boolean checkAspects(PokemonEntity pe) {
        if(targetAspects == null) {
            return true;
        }
        Set<String> remaining = new HashSet<>(targetAspects);
        Set<String> peAspects = pe.getPokemon().getAspects();

        for (String peAspect : peAspects) {
            remaining.remove(peAspect);
        }

        return remaining.isEmpty();
    }

    private boolean checkLevel(int level) {
        return level >= minimumLevel;
    }

    /**
     * Check if this name appears on the list OR if the check is unnecessary.
     * @param name
     * @return
     */
    private boolean checkName(String name) {
        // if list is null, default to true
        if(targetPokemonNames == null) {
            return true;
        }
        // if list accepts any, return true
        if(targetPokemonNames.contains("any")) {
            return true;
        }

        // check if list contains name
        return targetPokemonNames.contains(name.toLowerCase());
    }

    public int getPriority() {
        return priority;
    }

    public @Nullable String getGroup() {
        return group;
    }

    public boolean ignoresPriority() {
        return ignoresPriority || group == null;
    }

    public List<ParticleInfo> getParticleInfoList() {
        return particleInfoList;
    }

    public ParticleInfo sampleParticle(SplittableRandom random) {
        double sum = 0;
        for (ParticleInfo pi : this.particleInfoList) {
            sum += pi.getPercentage();
        }

        double rnd = random.nextDouble() * sum;

        for (ParticleInfo pi : this.particleInfoList) {
            if(rnd <= pi.getPercentage()) {
                return pi;
            }
        }

        return particleInfoList.get(0);
    }

    public ParticleAnimation getAnimation() {
        return animation;
    }

    public float getSizeModifier() {
        return sizeModifier;
    }
}
