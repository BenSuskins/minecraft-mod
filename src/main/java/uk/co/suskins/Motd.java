package uk.co.suskins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;

public class Motd implements ModInitializer {

    private MinecraftServer server;
    private MotdConfig motdConfig;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            File configDir = server.getRunDirectory().toFile();
            motdConfig = new MotdConfig();
            motdConfig.loadConfig(configDir);
        });

        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            player.sendMessage(Text.literal(motdConfig.getMotd()), false);
        });
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                  CommandRegistryAccess registryAccess,
                                  CommandManager.RegistrationEnvironment env) {
        dispatcher.register(CommandManager.literal("setmotd")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("motd", StringArgumentType.greedyString())
                        .executes(context -> {
                            String newMotd = StringArgumentType.getString(context, "motd");
                            motdConfig.setMotd(newMotd);
                            motdConfig.saveConfig(server.getRunDirectory().toFile());
                            context.getSource().sendFeedback(() -> Text.literal("MOTD set to: " + newMotd), true);
                            return 1;
                        })
                )
        );

        dispatcher.register(CommandManager.literal("motd")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal(motdConfig.getMotd()), false);
                    return 1;
                }));
    }
}
