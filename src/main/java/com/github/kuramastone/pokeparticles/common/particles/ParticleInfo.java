package com.github.kuramastone.pokeparticles.common.particles;

import com.github.kuramastone.bUtilities.yaml.YamlConfig;
import com.github.kuramastone.bUtilities.yaml.YamlKey;
import com.github.kuramastone.bUtilities.yaml.YamlObject;
import com.github.kuramastone.pokeparticles.common.PokeParticleManager;
import com.github.kuramastone.pokeparticles.common.utils.StringUtils;
import com.github.kuramastone.pokeparticles.fabric.PokeParticlesMod;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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

    @YamlKey(value = "scaled amount", required = false)
    private double amountPerCubicBlock = 5.0;

    @YamlKey(value = "data", required = false)
    private @Nullable String data = null;

    public ParticleInfo(YamlConfig section) {
        YamlConfig.loadFromYaml(this, section);
        particleNamespace = section.getParentKeyName().replace("/", "");
    }

    /**
     * Used to validate that some particles have properly formatted data.
     * @return
     */
    public boolean validate() {

        if (data != null) {
            if (particleNamespace.equalsIgnoreCase("minecraft:dust")) {
                try {
                    Vector3f color = StringUtils.splitIntoVec3(data);
                }
                catch (Exception e) {
                    PokeParticleManager.logger.warning("Unable to load particle type '%s'. Invalid color format '%s'. Use like r/g/b or 0/0/0."
                            .formatted(particleNamespace, data));
                    return false;
                }
            }
            else if (particleNamespace.equalsIgnoreCase("minecraft:dust_color_transition")) {
                try {
                    if (!data.contains("->")) {
                        throw new IllegalArgumentException("Needs arrow.");
                    }

                    String[] split = data.split("->");
                    Vector3f color0 = StringUtils.splitIntoVec3(split[0]);
                    Vector3f color1 = StringUtils.splitIntoVec3(split[1]);
                }
                catch (Exception e) {
                    PokeParticleManager.logger.warning("Unable to load particle type '%s'. Invalid color format '%s'. Use like 'r/g/b->r/g/b' or '0/0/0->255/255/255'."
                            .formatted(particleNamespace, data));
                    return false;
                }
            }
        }

        return true;
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
