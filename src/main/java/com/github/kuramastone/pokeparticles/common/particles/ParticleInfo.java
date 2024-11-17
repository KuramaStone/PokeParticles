package com.github.kuramastone.pokeparticles.common.particles;

import com.github.kuramastone.bUtilities.yaml.YamlConfig;
import com.github.kuramastone.bUtilities.yaml.YamlKey;
import com.github.kuramastone.bUtilities.yaml.YamlObject;
import org.jetbrains.annotations.Nullable;

@YamlObject
public class ParticleInfo {

    /**
     * Particle namespace to spawn
     */
    private String particleNamespace;

    /**
     * What percentage of particles this is requesting
     */
    @YamlKey(value = "percentage", required = false)
    private double percentage = 1;

    @YamlKey(value = "scaled amount",required = false)
    private double amountPerCubicBlock = 5.0;

    @YamlKey(value = "data",required = false)
    private @Nullable String data = null;

    public ParticleInfo(YamlConfig section) {
        YamlConfig.loadFromYaml(this, section);
        particleNamespace = section.getParentKeyName().replace("/", "");
    }

    public String getParticleNamespace() {
        return particleNamespace;
    }

    public double getPercentage() {
        return percentage;
    }

    public double getAmountPerCubicBlock() {
        return amountPerCubicBlock;
    }

    public @Nullable String getData() {
        return data;
    }

}
