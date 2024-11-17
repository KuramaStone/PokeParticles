package com.github.kuramastone.pokeparticles.fabric;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonRecalledEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PokeParticleManager;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class PokeParticlesMod implements ModInitializer {
    public static String MODID = "pokeparticles";

    private PokeParticleManager manager;


    @Override
    public void onInitialize() {
        manager = new PokeParticleManager(new FabricModImpl());

        CobblemonEvents.POKEMON_ENTITY_LOAD.subscribe(Priority.NORMAL, this::handlePokemonLoad);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, this::handlePokemonSentOut);
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, this::handlePokemonSpawn);
        CobblemonEvents.POKEMON_RECALLED.subscribe(Priority.NORMAL, this::handlePokemonRecall);

        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerTick(MinecraftServer minecraftServer) {
        manager.onServerTick();
    }

    private Unit handlePokemonRecall(PokemonRecalledEvent event) {
        manager.removePokemon(event.getOldEntity());
        return null;
    }


    private Unit handlePokemonSpawn(SpawnEvent<PokemonEntity> event) {
        manager.addPokemon(event.getEntity());
        return null;
    }

    private Unit handlePokemonSentOut(PokemonSentPostEvent event) {
        manager.addPokemon(event.getPokemonEntity());
        return null;
    }

    private Unit handlePokemonLoad(PokemonEntityLoadEvent event) {
        manager.addPokemon(event.getPokemonEntity());
        return null;
    }
}
