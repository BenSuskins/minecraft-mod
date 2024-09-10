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

public class Motd implements ModInitializer {

    private String motd = "New default!";
    private MinecraftServer server;

    private static final Formatting[] RAINBOW_COLORS = {
            Formatting.RED,
            Formatting.GOLD,
            Formatting.YELLOW,
            Formatting.GREEN,
            Formatting.AQUA,
            Formatting.BLUE,
            Formatting.LIGHT_PURPLE
    };

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            player.sendMessage(getRainbowText(motd), false);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.server = server);
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                  CommandRegistryAccess commandRegistryAccess,
                                  CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(CommandManager.literal("setmotd")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("motd", StringArgumentType.greedyString())
                        .executes(context -> {
                            motd = StringArgumentType.getString(context, "motd");
                            context.getSource().sendFeedback(() -> Text.literal("MOTD set to: " + motd), true);
                            return 1;
                        })
                )
        );

        serverCommandSourceCommandDispatcher.register(CommandManager.literal("motd")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal(motd), false);
                    return 1;
                }));
    }

    private MutableText getRainbowText(String input) {
        MutableText rainbowText = Text.literal("");
        int colorIndex = 0;
        for (char c : input.toCharArray()) {
            rainbowText.append(Text.literal(String.valueOf(c)).formatted(RAINBOW_COLORS[colorIndex]));
            colorIndex = (colorIndex + 1) % RAINBOW_COLORS.length;
        }
        return rainbowText;
    }
}
