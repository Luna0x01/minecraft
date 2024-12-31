package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_3915;
import net.minecraft.class_4009;
import net.minecraft.class_4062;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ChatSerializer;

public class TellRawCommand {
	public static void method_21119(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("tellraw").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("targets", class_4062.method_17904())
						.then(
							CommandManager.method_17530("message", class_4009.method_17711())
								.executes(
									commandContext -> {
										int i = 0;

										for (ServerPlayerEntity serverPlayerEntity : class_4062.method_17907(commandContext, "targets")) {
											serverPlayerEntity.method_5505(
												ChatSerializer.method_20185((class_3915)commandContext.getSource(), class_4009.method_17713(commandContext, "message"), serverPlayerEntity)
											);
											i++;
										}

										return i;
									}
								)
						)
				)
		);
	}
}
