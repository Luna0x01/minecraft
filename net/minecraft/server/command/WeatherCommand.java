package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_3915;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
	public static void method_21160(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("weather")
							.requires(arg -> arg.method_17575(2)))
						.then(
							((LiteralArgumentBuilder)CommandManager.method_17529("clear").executes(commandContext -> method_21159((class_3915)commandContext.getSource(), 6000)))
								.then(
									CommandManager.method_17530("duration", IntegerArgumentType.integer(0, 1000000))
										.executes(commandContext -> method_21159((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
								)
						))
					.then(
						((LiteralArgumentBuilder)CommandManager.method_17529("rain").executes(commandContext -> method_21162((class_3915)commandContext.getSource(), 6000)))
							.then(
								CommandManager.method_17530("duration", IntegerArgumentType.integer(0, 1000000))
									.executes(commandContext -> method_21162((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
							)
					))
				.then(
					((LiteralArgumentBuilder)CommandManager.method_17529("thunder").executes(commandContext -> method_21164((class_3915)commandContext.getSource(), 6000)))
						.then(
							CommandManager.method_17530("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_21164((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
		);
	}

	private static int method_21159(class_3915 arg, int i) {
		arg.method_17468().method_3588().setClearWeatherTime(i);
		arg.method_17468().method_3588().setRainTime(0);
		arg.method_17468().method_3588().setThunderTime(0);
		arg.method_17468().method_3588().setRaining(false);
		arg.method_17468().method_3588().setThundering(false);
		arg.method_17459(new TranslatableText("commands.weather.set.clear"), true);
		return i;
	}

	private static int method_21162(class_3915 arg, int i) {
		arg.method_17468().method_3588().setClearWeatherTime(0);
		arg.method_17468().method_3588().setRainTime(i);
		arg.method_17468().method_3588().setThunderTime(i);
		arg.method_17468().method_3588().setRaining(true);
		arg.method_17468().method_3588().setThundering(false);
		arg.method_17459(new TranslatableText("commands.weather.set.rain"), true);
		return i;
	}

	private static int method_21164(class_3915 arg, int i) {
		arg.method_17468().method_3588().setClearWeatherTime(0);
		arg.method_17468().method_3588().setRainTime(i);
		arg.method_17468().method_3588().setThunderTime(i);
		arg.method_17468().method_3588().setRaining(true);
		arg.method_17468().method_3588().setThundering(true);
		arg.method_17459(new TranslatableText("commands.weather.set.thunder"), true);
		return i;
	}
}
