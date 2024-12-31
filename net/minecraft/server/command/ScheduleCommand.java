package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.FunctionTagTimerCallback;
import net.minecraft.world.timer.FunctionTimerCallback;
import net.minecraft.world.timer.Timer;

public class ScheduleCommand {
	private static final SimpleCommandExceptionType SAME_TICK_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.schedule.same_tick"));
	private static final DynamicCommandExceptionType field_20853 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.schedule.cleared.failure", object)
	);
	private static final SuggestionProvider<ServerCommandSource> field_20854 = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			((ServerCommandSource)commandContext.getSource()).getWorld().getLevelProperties().getScheduledEvents().method_22592(), suggestionsBuilder
		);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("schedule")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
					.then(
						CommandManager.literal("function")
							.then(
								CommandManager.argument("function", FunctionArgumentType.function())
									.suggests(FunctionCommand.SUGGESTION_PROVIDER)
									.then(
										((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("time", TimeArgumentType.time())
													.executes(
														commandContext -> execute(
																(ServerCommandSource)commandContext.getSource(),
																FunctionArgumentType.getFunctionOrTag(commandContext, "function"),
																IntegerArgumentType.getInteger(commandContext, "time"),
																true
															)
													))
												.then(
													CommandManager.literal("append")
														.executes(
															commandContext -> execute(
																	(ServerCommandSource)commandContext.getSource(),
																	FunctionArgumentType.getFunctionOrTag(commandContext, "function"),
																	IntegerArgumentType.getInteger(commandContext, "time"),
																	false
																)
														)
												))
											.then(
												CommandManager.literal("replace")
													.executes(
														commandContext -> execute(
																(ServerCommandSource)commandContext.getSource(),
																FunctionArgumentType.getFunctionOrTag(commandContext, "function"),
																IntegerArgumentType.getInteger(commandContext, "time"),
																true
															)
													)
											)
									)
							)
					))
				.then(
					CommandManager.literal("clear")
						.then(
							CommandManager.argument("function", StringArgumentType.greedyString())
								.suggests(field_20854)
								.executes(commandContext -> method_22833((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "function")))
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Either<CommandFunction, Tag<CommandFunction>> either, int i, boolean bl) throws CommandSyntaxException {
		if (i == 0) {
			throw SAME_TICK_EXCEPTION.create();
		} else {
			long l = serverCommandSource.getWorld().getTime() + (long)i;
			Timer<MinecraftServer> timer = serverCommandSource.getWorld().getLevelProperties().getScheduledEvents();
			either.ifLeft(commandFunction -> {
				Identifier identifier = commandFunction.getId();
				String string = identifier.toString();
				if (bl) {
					timer.method_22593(string);
				}

				timer.setEvent(string, l, new FunctionTimerCallback(identifier));
				serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.created.function", identifier, i, l), true);
			}).ifRight(tag -> {
				Identifier identifier = tag.getId();
				String string = "#" + identifier.toString();
				if (bl) {
					timer.method_22593(string);
				}

				timer.setEvent(string, l, new FunctionTagTimerCallback(identifier));
				serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.created.tag", identifier, i, l), true);
			});
			return (int)Math.floorMod(l, 2147483647L);
		}
	}

	private static int method_22833(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		int i = serverCommandSource.getWorld().getLevelProperties().getScheduledEvents().method_22593(string);
		if (i == 0) {
			throw field_20853.create(string);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.schedule.cleared.success", i, string), true);
			return i;
		}
	}
}
