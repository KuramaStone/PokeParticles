package com.github.kuramastone.pokeparticles.fabric;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonRecalledEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentPostEvent;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.kuramastone.pokeparticles.common.PokeParticleManager;
import com.github.kuramastone.pokeparticles.common.PokemonFinder;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class PokeParticlesMod implements ModInitializer {

    public static String MODID = "pokeparticles";
    private static MinecraftServer minecraftServer;

    private PokeParticleManager manager;
    private PokemonFinder pokemonFinder;

    @Override
    public void onInitialize() {
        manager = new PokeParticleManager(new FabricModImpl());
        pokemonFinder = new PokemonFinder(manager);

        registerCommand();

        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
        CobblemonEvents.POKEMON_ENTITY_LOAD.subscribe(Priority.NORMAL, this::handlePokemonLoad);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, this::handlePokemonSentOut);
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, this::handlePokemonSpawn);
        CobblemonEvents.POKEMON_RECALLED.subscribe(Priority.NORMAL, this::handlePokemonRecall);
    }

    private void onServerStart(MinecraftServer minecraftServer) {
        PokeParticlesMod.minecraftServer = minecraftServer;
    }

    private void registerCommand() {

        // Register the "reload" command
        CommandRegistrationCallback.EVENT.register((dispatcher, cmdRegistry, regEnv) -> {
            dispatcher.register(
                    CommandManager.literal("reloadpokeparticles")
                            .requires(src -> src.hasPermissionLevel(2))
                            .executes(context -> {
                                manager.loadParticleCategories();
                                context.getSource().sendMessage(Text.literal("Reload complete!"));
                                return 1;
                            })
            );
        });
    }

    private long currentTick = 0;

    private void onServerTick(MinecraftServer minecraftServer) {
        manager.onServerTick();
        pokemonFinder.checkAlLExistingPokemon(minecraftServer);
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

    public static MinecraftServer getServer() {
        return minecraftServer;
    }
}
