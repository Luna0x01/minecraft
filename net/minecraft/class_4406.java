package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.server.BanEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4406 {
	public static void method_20518(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("banlist")
							.requires(
								arg -> (arg.method_17473().getPlayerManager().getUserBanList().isEnabled() || arg.method_17473().getPlayerManager().getIpBanList().isEnabled())
										&& arg.method_17575(3)
							))
						.executes(
							commandContext -> {
								PlayerManager playerManager = ((class_3915)commandContext.getSource()).method_17473().getPlayerManager();
								return method_20517(
									(class_3915)commandContext.getSource(),
									Lists.newArrayList(Iterables.concat(playerManager.getUserBanList().method_21390(), playerManager.getIpBanList().method_21390()))
								);
							}
						))
					.then(
						CommandManager.method_17529("ips")
							.executes(
								commandContext -> method_20517(
										(class_3915)commandContext.getSource(), ((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getIpBanList().method_21390()
									)
							)
					))
				.then(
					CommandManager.method_17529("players")
						.executes(
							commandContext -> method_20517(
									(class_3915)commandContext.getSource(), ((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getUserBanList().method_21390()
								)
						)
				)
		);
	}

	private static int method_20517(class_3915 arg, Collection<? extends BanEntry<?>> collection) {
		if (collection.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.banlist.none"), false);
		} else {
			arg.method_17459(new TranslatableText("commands.banlist.list", collection.size()), false);

			for (BanEntry<?> banEntry : collection) {
				arg.method_17459(new TranslatableText("commands.banlist.entry", banEntry.method_21379(), banEntry.method_21378(), banEntry.getReason()), false);
			}
		}

		return collection.size();
	}
}
