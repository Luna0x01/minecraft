package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class BanCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "ban";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.ban.usage";
	}

	@Override
	public boolean method_3278(MinecraftServer server, CommandSource source) {
		return server.getPlayerManager().getUserBanList().isEnabled() && super.method_3278(server, source);
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].length() > 0) {
			GameProfile gameProfile = minecraftServer.getUserCache().findByName(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.ban.failed", args[0]);
			} else {
				String string = null;
				if (args.length >= 2) {
					string = method_4635(commandSource, args, 1).asUnformattedString();
				}

				BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, commandSource.getTranslationKey(), null, string);
				minecraftServer.getPlayerManager().getUserBanList().add(bannedPlayerEntry);
				ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(args[0]);
				if (serverPlayerEntity != null) {
					serverPlayerEntity.networkHandler.disconnect("You are banned from this server.");
				}

				run(commandSource, this, "commands.ban.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.ban.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length >= 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}
}
