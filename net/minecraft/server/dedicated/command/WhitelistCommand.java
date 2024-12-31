package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4073;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class WhitelistCommand {
	private static final SimpleCommandExceptionType field_21811 = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.alreadyOn"));
	private static final SimpleCommandExceptionType field_21812 = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.alreadyOff"));
	private static final SimpleCommandExceptionType field_21813 = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.add.failed"));
	private static final SimpleCommandExceptionType field_21814 = new SimpleCommandExceptionType(new TranslatableText("commands.whitelist.remove.failed"));

	public static void method_21171(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
											"whitelist"
										)
										.requires(arg -> arg.method_17575(3)))
									.then(CommandManager.method_17529("on").executes(commandContext -> method_21176((class_3915)commandContext.getSource()))))
								.then(CommandManager.method_17529("off").executes(commandContext -> method_21180((class_3915)commandContext.getSource()))))
							.then(CommandManager.method_17529("list").executes(commandContext -> method_21182((class_3915)commandContext.getSource()))))
						.then(
							CommandManager.method_17529("add")
								.then(
									CommandManager.method_17530("targets", class_4073.method_17988())
										.suggests(
											(commandContext, suggestionsBuilder) -> {
												PlayerManager playerManager = ((class_3915)commandContext.getSource()).method_17473().getPlayerManager();
												return class_3965.method_17573(
													playerManager.getPlayers()
														.stream()
														.filter(serverPlayerEntity -> !playerManager.getWhitelist().isAllowed(serverPlayerEntity.getGameProfile()))
														.map(serverPlayerEntity -> serverPlayerEntity.getGameProfile().getName()),
													suggestionsBuilder
												);
											}
										)
										.executes(commandContext -> method_21170((class_3915)commandContext.getSource(), class_4073.method_17991(commandContext, "targets")))
								)
						))
					.then(
						CommandManager.method_17529("remove")
							.then(
								CommandManager.method_17530("targets", class_4073.method_17988())
									.suggests(
										(commandContext, suggestionsBuilder) -> class_3965.method_17570(
												((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getWhitelistedNames(), suggestionsBuilder
											)
									)
									.executes(commandContext -> method_21177((class_3915)commandContext.getSource(), class_4073.method_17991(commandContext, "targets")))
							)
					))
				.then(CommandManager.method_17529("reload").executes(commandContext -> method_21169((class_3915)commandContext.getSource())))
		);
	}

	private static int method_21169(class_3915 arg) {
		arg.method_17473().getPlayerManager().reloadWhitelist();
		arg.method_17459(new TranslatableText("commands.whitelist.reloaded"), true);
		arg.method_17473().method_20313(arg);
		return 1;
	}

	private static int method_21170(class_3915 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		Whitelist whitelist = arg.method_17473().getPlayerManager().getWhitelist();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!whitelist.isAllowed(gameProfile)) {
				WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
				whitelist.add(whitelistEntry);
				arg.method_17459(new TranslatableText("commands.whitelist.add.success", ChatSerializer.method_20186(gameProfile)), true);
				i++;
			}
		}

		if (i == 0) {
			throw field_21813.create();
		} else {
			return i;
		}
	}

	private static int method_21177(class_3915 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		Whitelist whitelist = arg.method_17473().getPlayerManager().getWhitelist();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (whitelist.isAllowed(gameProfile)) {
				WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
				whitelist.method_21389(whitelistEntry);
				arg.method_17459(new TranslatableText("commands.whitelist.remove.success", ChatSerializer.method_20186(gameProfile)), true);
				i++;
			}
		}

		if (i == 0) {
			throw field_21814.create();
		} else {
			arg.method_17473().method_20313(arg);
			return i;
		}
	}

	private static int method_21176(class_3915 arg) throws CommandSyntaxException {
		PlayerManager playerManager = arg.method_17473().getPlayerManager();
		if (playerManager.isWhitelistEnabled()) {
			throw field_21811.create();
		} else {
			playerManager.setWhitelistEnabled(true);
			arg.method_17459(new TranslatableText("commands.whitelist.enabled"), true);
			arg.method_17473().method_20313(arg);
			return 1;
		}
	}

	private static int method_21180(class_3915 arg) throws CommandSyntaxException {
		PlayerManager playerManager = arg.method_17473().getPlayerManager();
		if (!playerManager.isWhitelistEnabled()) {
			throw field_21812.create();
		} else {
			playerManager.setWhitelistEnabled(false);
			arg.method_17459(new TranslatableText("commands.whitelist.disabled"), true);
			return 1;
		}
	}

	private static int method_21182(class_3915 arg) {
		String[] strings = arg.method_17473().getPlayerManager().getWhitelistedNames();
		if (strings.length == 0) {
			arg.method_17459(new TranslatableText("commands.whitelist.none"), false);
		} else {
			arg.method_17459(new TranslatableText("commands.whitelist.list", strings.length, String.join(", ", strings)), false);
		}

		return strings.length;
	}
}
