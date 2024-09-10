package uk.co.suskins;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Motd implements ModInitializer {

	private String motd = "Welcome to the server!"; // Default MOTD
	private MinecraftServer server;

	@Override
	public void onInitialize() {
		// Register commands to set the MOTD
		CommandRegistrationCallback.EVENT.register(this::registerCommands);

		// Listen for when a player joins
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			player.sendMessage(Text.literal(motd), false);
		});

		// Listen for server start to set the server object
		ServerLifecycleEvents.SERVER_STARTED.register(server -> this.server = server);
	}

	// Register a command to change the MOTD
	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("setmotd")
				.requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2 (Server Operators)
				.then(CommandManager.argument("motd", StringArgumentType.greedyString())
						.executes(context -> {
							motd = StringArgumentType.getString(context, "motd");
							context.getSource().sendFeedback(Text.literal("MOTD set to: " + motd), true);
							return 1;
						})
				)
		);
	}
}