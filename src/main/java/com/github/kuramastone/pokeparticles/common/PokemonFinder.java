package com.github.kuramastone.pokeparticles.common;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.fabric.PokeParticlesMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

/*
Stretches the search over a number of ticks
 */
public class PokemonFinder {

    // custom values
    private static final int loopOver = 20;

    // constructor
    private PokeParticleManager manager;

    // variables
    private List<PokemonEntity> cachedPokemonToRemoveEffectFrom;
    private List<PokemonEntity> cachedPokemonDiscovered;
    private int currentTickChecker = 0;

    public PokemonFinder(PokeParticleManager manager) {
        this.manager = manager;
        cachedPokemonToRemoveEffectFrom = new ArrayList<>();
    }

    public void checkAlLExistingPokemon(MinecraftServer minecraftServer) {
        // put all tracked pokemon in this list

        if (currentTickChecker == 0) {
            // these pokemon werent found in any world. remove them from the tracker
            for (PokemonEntity pokemonEntity : cachedPokemonToRemoveEffectFrom) {
                manager.removePokemon(pokemonEntity);
            }

            cachedPokemonToRemoveEffectFrom = new ArrayList<>(manager.getPokemonToApplyEffect());
            cachedPokemonDiscovered = getAllLoadedPokemon(minecraftServer);
        }
        else {
            // if the list isnt entry, portion the pokemon into groups until it is empty. remove entries from cache after testing them.
            if (!cachedPokemonDiscovered.isEmpty()) {
                int pokemonToCheck = cachedPokemonDiscovered.size() / (loopOver - 1);
                if(pokemonToCheck == 0) {
                    pokemonToCheck = cachedPokemonDiscovered.size();
                }
                // remove any pokemon that are still loaded
                // add any pokemon that arent in the list
                for (int i = 0; i < pokemonToCheck && !cachedPokemonDiscovered.isEmpty(); i++) {
                    PokemonEntity pokemonEntity = cachedPokemonDiscovered.removeFirst();
                    if (pokemonEntity.isAlive()
                            && !pokemonEntity.isRemoved()
                            && !pokemonEntity.isRegionUnloaded()) {
                        cachedPokemonToRemoveEffectFrom.remove(pokemonEntity);
                        if (!cachedPokemonToRemoveEffectFrom.contains(pokemonEntity)) {
                            manager.addPokemon(pokemonEntity);
                        }
                    }
                }

            }
        }

        currentTickChecker = (currentTickChecker + 1) % loopOver;
    }

    private List<PokemonEntity> getAllLoadedPokemon(MinecraftServer minecraftServer) {
        List<PokemonEntity> allLoadedPokemon = new ArrayList<>();
        for (ServerWorld world : minecraftServer.getWorlds()) {
            for (PokemonEntity pokemonEntity : world.getEntitiesByType(CobblemonEntities.POKEMON, (p) -> true)) {
                allLoadedPokemon.add(pokemonEntity);
            }
        }

        return allLoadedPokemon;
    }
}
