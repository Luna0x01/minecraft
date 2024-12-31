package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_3915;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class TimeCommand {
	public static void method_21123(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("time")
							.requires(arg -> arg.method_17575(2)))
						.then(
							((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("set")
												.then(CommandManager.method_17529("day").executes(commandContext -> method_21122((class_3915)commandContext.getSource(), 1000))))
											.then(CommandManager.method_17529("noon").executes(commandContext -> method_21122((class_3915)commandContext.getSource(), 6000))))
										.then(CommandManager.method_17529("night").executes(commandContext -> method_21122((class_3915)commandContext.getSource(), 13000))))
									.then(CommandManager.method_17529("midnight").executes(commandContext -> method_21122((class_3915)commandContext.getSource(), 18000))))
								.then(
									CommandManager.method_17530("time", IntegerArgumentType.integer(0))
										.executes(commandContext -> method_21122((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
								)
						))
					.then(
						CommandManager.method_17529("add")
							.then(
								CommandManager.method_17530("time", IntegerArgumentType.integer(0))
									.executes(commandContext -> method_21126((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
							)
					))
				.then(
					((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("query")
								.then(
									CommandManager.method_17529("daytime")
										.executes(
											commandContext -> method_21128((class_3915)commandContext.getSource(), method_21125(((class_3915)commandContext.getSource()).method_17468()))
										)
								))
							.then(
								CommandManager.method_17529("gametime")
									.executes(
										commandContext -> method_21128(
												(class_3915)commandContext.getSource(), (int)(((class_3915)commandContext.getSource()).method_17468().getLastUpdateTime() % 2147483647L)
											)
									)
							))
						.then(
							CommandManager.method_17529("day")
								.executes(
									commandContext -> method_21128(
											(class_3915)commandContext.getSource(), (int)(((class_3915)commandContext.getSource()).method_17468().getTimeOfDay() / 24000L % 2147483647L)
										)
								)
						)
				)
		);
	}

	private static int method_21125(ServerWorld serverWorld) {
		return (int)(serverWorld.getTimeOfDay() % 24000L);
	}

	private static int method_21128(class_3915 arg, int i) {
		arg.method_17459(new TranslatableText("commands.time.query", i), false);
		return i;
	}

	public static int method_21122(class_3915 arg, int i) {
		for (ServerWorld serverWorld : arg.method_17473().method_20351()) {
			serverWorld.setTimeOfDay((long)i);
		}

		arg.method_17459(new TranslatableText("commands.time.set", i), true);
		return method_21125(arg.method_17468());
	}

	public static int method_21126(class_3915 arg, int i) {
		for (ServerWorld serverWorld : arg.method_17473().method_20351()) {
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)i);
		}

		int j = method_21125(arg.method_17468());
		arg.method_17459(new TranslatableText("commands.time.set", j), true);
		return j;
	}
}
