package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("weather")
							.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
						.then(
							((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), 6000)))
								.then(
									CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
										.executes(
											commandContext -> executeClear((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)
										)
								)
						))
					.then(
						((LiteralArgumentBuilder)CommandManager.literal("rain").executes(commandContext -> executeRain((ServerCommandSource)commandContext.getSource(), 6000)))
							.then(
								CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
									.executes(
										commandContext -> executeRain((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)
									)
							)
					))
				.then(
					((LiteralArgumentBuilder)CommandManager.literal("thunder")
							.executes(commandContext -> executeThunder((ServerCommandSource)commandContext.getSource(), 6000)))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(
									commandContext -> executeThunder((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)
								)
						)
				)
		);
	}

	private static int executeClear(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(duration, 0, false, false);
		source.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
		return duration;
	}

	private static int executeRain(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(0, duration, true, false);
		source.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
		return duration;
	}

	private static int executeThunder(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(0, duration, true, true);
		source.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
		return duration;
	}
}
