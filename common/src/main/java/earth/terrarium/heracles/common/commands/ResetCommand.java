package earth.terrarium.heracles.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public class ResetCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> reset() {
        return Commands.literal("reset")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("quest", StringArgumentType.string())
                .suggests(ModCommands.QUESTS)
                .then(Commands.argument("target", EntityArgument.players())
                    .executes(context -> reset(EntityArgument.getPlayers(context, "target"), context))
                )
                .executes(context -> reset(List.of(context.getSource().getPlayerOrException()), context))
            );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> resetAll() {
        return Commands.literal("resetall")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("target", EntityArgument.players())
                .executes(context -> resetAll(EntityArgument.getPlayers(context, "target"), context))
            )
            .executes(context -> resetAll(List.of(context.getSource().getPlayerOrException()), context));
    }

    private static int reset(Collection<ServerPlayer> players, CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String quest = StringArgumentType.getString(context, "quest");
        for (ServerPlayer player : players) {
            QuestProgressHandler.getProgress(source.getServer(), player.getUUID()).resetQuest(quest, player);
        }
        source.sendSuccess(
            () -> Component.translatable("commands.heracles.reset.success", quest),
            false
        );
        return 1;
    }

    private static int resetAll(Collection<ServerPlayer> players, CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        for (ServerPlayer player : players) {
            QuestProgressHandler.getProgress(source.getServer(), player.getUUID()).reset(player);
        }
        source.sendSuccess(
            () -> Component.translatable("commands.heracles.resetall.success"),
            false
        );
        return 1;
    }
}
