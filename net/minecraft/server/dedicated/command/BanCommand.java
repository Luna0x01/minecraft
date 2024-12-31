package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
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
	public boolean isAccessible(CommandSource source) {
		return MinecraftServer.getServer().getPlayerManager().getUserBanList().isEnabled() && super.isAccessible(source);
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].length() > 0) {
			MinecraftServer minecraftServer = MinecraftServer.getServer();
			GameProfile gameProfile = minecraftServer.getUserCache().findByName(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.ban.failed", args[0]);
			} else {
				String string = null;
				if (args.length >= 2) {
					string = method_4635(source, args, 1).asUnformattedString();
				}

				BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, null, source.getTranslationKey(), null, string);
				minecraftServer.getPlayerManager().getUserBanList().add(bannedPlayerEntry);
				ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(args[0]);
				if (serverPlayerEntity != null) {
					serverPlayerEntity.networkHandler.disconnect("You are banned from this server.");
				}

				run(source, this, "commands.ban.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.ban.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length >= 1 ? method_2894(args, MinecraftServer.getServer().getPlayerNames()) : null;
	}
}
