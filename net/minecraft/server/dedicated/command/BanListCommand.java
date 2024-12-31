package net.minecraft.server.dedicated.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.server.BanEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class BanListCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("banlist")
							.requires(
								serverCommandSource -> (
											serverCommandSource.getMinecraftServer().getPlayerManager().getUserBanList().isEnabled()
												|| serverCommandSource.getMinecraftServer().getPlayerManager().getIpBanList().isEnabled()
										)
										&& serverCommandSource.hasPermissionLevel(3)
							))
						.executes(
							commandContext -> {
								PlayerManager playerManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager();
								return execute(
									(ServerCommandSource)commandContext.getSource(),
									Lists.newArrayList(Iterables.concat(playerManager.getUserBanList().values(), playerManager.getIpBanList().values()))
								);
							}
						))
					.then(
						CommandManager.literal("ips")
							.executes(
								commandContext -> execute(
										(ServerCommandSource)commandContext.getSource(),
										((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getIpBanList().values()
									)
							)
					))
				.then(
					CommandManager.literal("players")
						.executes(
							commandContext -> execute(
									(ServerCommandSource)commandContext.getSource(),
									((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getUserBanList().values()
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<? extends BanEntry<?>> collection) {
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.banlist.none"), false);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.banlist.list", collection.size()), false);

			for (BanEntry<?> banEntry : collection) {
				serverCommandSource.sendFeedback(new TranslatableText("commands.banlist.entry", banEntry.toText(), banEntry.getSource(), banEntry.getReason()), false);
			}
		}

		return collection.size();
	}
}
