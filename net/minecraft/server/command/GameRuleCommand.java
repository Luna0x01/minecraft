package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		final LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = (LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("gamerule")
			.requires(source -> source.hasPermissionLevel(2));
		GameRules.accept(
			new GameRules.Visitor() {
				@Override
				public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
					literalArgumentBuilder.then(
						((LiteralArgumentBuilder)CommandManager.literal(key.getName())
								.executes(context -> GameRuleCommand.executeQuery((ServerCommandSource)context.getSource(), key)))
							.then(type.argument("value").executes(context -> GameRuleCommand.executeSet(context, key)))
					);
				}
			}
		);
		dispatcher.register(literalArgumentBuilder);
	}

	static <T extends GameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> context, GameRules.Key<T> key) {
		ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
		T rule = serverCommandSource.getServer().getGameRules().get(key);
		rule.set(context, "value");
		serverCommandSource.sendFeedback(new TranslatableText("commands.gamerule.set", key.getName(), rule.toString()), true);
		return rule.getCommandResult();
	}

	static <T extends GameRules.Rule<T>> int executeQuery(ServerCommandSource source, GameRules.Key<T> key) {
		T rule = source.getServer().getGameRules().get(key);
		source.sendFeedback(new TranslatableText("commands.gamerule.query", key.getName(), rule.toString()), false);
		return rule.getCommandResult();
	}
}
