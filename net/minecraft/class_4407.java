package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class class_4407 {
	private static final SimpleCommandExceptionType field_21692 = new SimpleCommandExceptionType(new TranslatableText("commands.ban.failed"));

	public static void method_20525(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("ban")
					.requires(arg -> arg.method_17473().getPlayerManager().getUserBanList().isEnabled() && arg.method_17575(3)))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4073.method_17988())
							.executes(commandContext -> method_20524((class_3915)commandContext.getSource(), class_4073.method_17991(commandContext, "targets"), null)))
						.then(
							CommandManager.method_17530("reason", class_4102.method_18091())
								.executes(
									commandContext -> method_20524(
											(class_3915)commandContext.getSource(), class_4073.method_17991(commandContext, "targets"), class_4102.method_18093(commandContext, "reason")
										)
								)
						)
				)
		);
	}

	private static int method_20524(class_3915 arg, Collection<GameProfile> collection, @Nullable Text text) throws CommandSyntaxException {
		BannedPlayerList bannedPlayerList = arg.method_17473().getPlayerManager().getUserBanList();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (!bannedPlayerList.contains(gameProfile)) {
				BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, arg.method_17466(), null, text == null ? null : text.getString());
				bannedPlayerList.add(bannedPlayerEntry);
				i++;
				arg.method_17459(new TranslatableText("commands.ban.success", ChatSerializer.method_20186(gameProfile), bannedPlayerEntry.getReason()), true);
				ServerPlayerEntity serverPlayerEntity = arg.method_17473().getPlayerManager().getPlayer(gameProfile.getId());
				if (serverPlayerEntity != null) {
					serverPlayerEntity.networkHandler.method_14977(new TranslatableText("multiplayer.disconnect.banned"));
				}
			}
		}

		if (i == 0) {
			throw field_21692.create();
		} else {
			return i;
		}
	}
}
