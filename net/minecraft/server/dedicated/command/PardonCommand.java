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
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class PardonCommand {
	private static final SimpleCommandExceptionType field_21762 = new SimpleCommandExceptionType(new TranslatableText("commands.pardon.failed"));

	public static void method_20877(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("pardon")
					.requires(arg -> arg.method_17473().getPlayerManager().getIpBanList().isEnabled() && arg.method_17575(3)))
				.then(
					CommandManager.method_17530("targets", class_4073.method_17988())
						.suggests(
							(commandContext, suggestionsBuilder) -> class_3965.method_17570(
									((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getUserBanList().getNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_20876((class_3915)commandContext.getSource(), class_4073.method_17991(commandContext, "targets")))
				)
		);
	}

	private static int method_20876(class_3915 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		BannedPlayerList bannedPlayerList = arg.method_17473().getPlayerManager().getUserBanList();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (bannedPlayerList.contains(gameProfile)) {
				bannedPlayerList.remove(gameProfile);
				i++;
				arg.method_17459(new TranslatableText("commands.pardon.success", ChatSerializer.method_20186(gameProfile)), true);
			}
		}

		if (i == 0) {
			throw field_21762.create();
		} else {
			return i;
		}
	}
}
