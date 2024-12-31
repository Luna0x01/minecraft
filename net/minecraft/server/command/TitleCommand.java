package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.class_3915;
import net.minecraft.class_4009;
import net.minecraft.class_4062;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class TitleCommand {
	public static void method_21140(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("title").requires(arg -> arg.method_17575(2)))
				.then(
					((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
												"targets", class_4062.method_17904()
											)
											.then(
												CommandManager.method_17529("clear")
													.executes(commandContext -> method_21137((class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets")))
											))
										.then(
											CommandManager.method_17529("reset")
												.executes(commandContext -> method_21142((class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets")))
										))
									.then(
										CommandManager.method_17529("title")
											.then(
												CommandManager.method_17530("title", class_4009.method_17711())
													.executes(
														commandContext -> method_21139(
																(class_3915)commandContext.getSource(),
																class_4062.method_17907(commandContext, "targets"),
																class_4009.method_17713(commandContext, "title"),
																TitleS2CPacket.Action.TITLE
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("subtitle")
										.then(
											CommandManager.method_17530("title", class_4009.method_17711())
												.executes(
													commandContext -> method_21139(
															(class_3915)commandContext.getSource(),
															class_4062.method_17907(commandContext, "targets"),
															class_4009.method_17713(commandContext, "title"),
															TitleS2CPacket.Action.SUBTITLE
														)
												)
										)
								))
							.then(
								CommandManager.method_17529("actionbar")
									.then(
										CommandManager.method_17530("title", class_4009.method_17711())
											.executes(
												commandContext -> method_21139(
														(class_3915)commandContext.getSource(),
														class_4062.method_17907(commandContext, "targets"),
														class_4009.method_17713(commandContext, "title"),
														TitleS2CPacket.Action.ACTIONBAR
													)
											)
									)
							))
						.then(
							CommandManager.method_17529("times")
								.then(
									CommandManager.method_17530("fadeIn", IntegerArgumentType.integer(0))
										.then(
											CommandManager.method_17530("stay", IntegerArgumentType.integer(0))
												.then(
													CommandManager.method_17530("fadeOut", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_21138(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17907(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "fadeIn"),
																	IntegerArgumentType.getInteger(commandContext, "stay"),
																	IntegerArgumentType.getInteger(commandContext, "fadeOut")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_21137(class_3915 arg, Collection<ServerPlayerEntity> collection) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.CLEAR, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.title.cleared.single", ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.title.cleared.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_21142(class_3915 arg, Collection<ServerPlayerEntity> collection) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.RESET, null);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.title.reset.single", ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.title.reset.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_21139(class_3915 arg, Collection<ServerPlayerEntity> collection, Text text, TitleS2CPacket.Action action) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(action, ChatSerializer.method_20185(arg, text, serverPlayerEntity)));
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText(
					"commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".single", ((ServerPlayerEntity)collection.iterator().next()).getName()
				),
				true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_21138(class_3915 arg, Collection<ServerPlayerEntity> collection, int i, int j, int k) {
		TitleS2CPacket titleS2CPacket = new TitleS2CPacket(i, j, k);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.title.times.single", ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.title.times.multiple", collection.size()), true);
		}

		return collection.size();
	}
}
