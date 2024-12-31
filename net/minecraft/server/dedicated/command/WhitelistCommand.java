package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class WhitelistCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "whitelist";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.whitelist.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.whitelist.usage");
		} else {
			MinecraftServer minecraftServer = MinecraftServer.getServer();
			if (args[0].equals("on")) {
				minecraftServer.getPlayerManager().setWhitelistEnabled(true);
				run(source, this, "commands.whitelist.enabled", new Object[0]);
			} else if (args[0].equals("off")) {
				minecraftServer.getPlayerManager().setWhitelistEnabled(false);
				run(source, this, "commands.whitelist.disabled", new Object[0]);
			} else if (args[0].equals("list")) {
				source.sendMessage(
					new TranslatableText(
						"commands.whitelist.list", minecraftServer.getPlayerManager().getWhitelistedNames().length, minecraftServer.getPlayerManager().getSavedPlayerIds().length
					)
				);
				String[] strings = minecraftServer.getPlayerManager().getWhitelistedNames();
				source.sendMessage(new LiteralText(concat(strings)));
			} else if (args[0].equals("add")) {
				if (args.length < 2) {
					throw new IncorrectUsageException("commands.whitelist.add.usage");
				}

				GameProfile gameProfile = minecraftServer.getUserCache().findByName(args[1]);
				if (gameProfile == null) {
					throw new CommandException("commands.whitelist.add.failed", args[1]);
				}

				minecraftServer.getPlayerManager().whitelist(gameProfile);
				run(source, this, "commands.whitelist.add.success", new Object[]{args[1]});
			} else if (args[0].equals("remove")) {
				if (args.length < 2) {
					throw new IncorrectUsageException("commands.whitelist.remove.usage");
				}

				GameProfile gameProfile2 = minecraftServer.getPlayerManager().getWhitelist().getProfile(args[1]);
				if (gameProfile2 == null) {
					throw new CommandException("commands.whitelist.remove.failed", args[1]);
				}

				minecraftServer.getPlayerManager().unWhitelist(gameProfile2);
				run(source, this, "commands.whitelist.remove.success", new Object[]{args[1]});
			} else if (args[0].equals("reload")) {
				minecraftServer.getPlayerManager().reloadWhitelist();
				run(source, this, "commands.whitelist.reloaded", new Object[0]);
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"on", "off", "list", "add", "remove", "reload"});
		} else {
			if (args.length == 2) {
				if (args[0].equals("remove")) {
					return method_2894(args, MinecraftServer.getServer().getPlayerManager().getWhitelistedNames());
				}

				if (args[0].equals("add")) {
					return method_2894(args, MinecraftServer.getServer().getUserCache().getNames());
				}
			}

			return null;
		}
	}
}
