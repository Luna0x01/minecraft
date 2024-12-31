package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Texts;
import net.minecraft.util.Util;

public class TellRawCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tellraw")
					.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("message", TextArgumentType.text())
								.executes(
									commandContext -> {
										int i = 0;

										for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers(commandContext, "targets")) {
											serverPlayerEntity.sendSystemMessage(
												Texts.parse((ServerCommandSource)commandContext.getSource(), TextArgumentType.getTextArgument(commandContext, "message"), serverPlayerEntity, 0),
												Util.NIL_UUID
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
