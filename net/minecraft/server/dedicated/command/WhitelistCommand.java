package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.whitelist.usage");
		} else {
			if ("on".equals(args[0])) {
				minecraftServer.getPlayerManager().setWhitelistEnabled(true);
				run(commandSource, this, "commands.whitelist.enabled", new Object[0]);
			} else if ("off".equals(args[0])) {
				minecraftServer.getPlayerManager().setWhitelistEnabled(false);
				run(commandSource, this, "commands.whitelist.disabled", new Object[0]);
			} else if ("list".equals(args[0])) {
				commandSource.sendMessage(
					new TranslatableText(
						"commands.whitelist.list", minecraftServer.getPlayerManager().getWhitelistedNames().length, minecraftServer.getPlayerManager().getSavedPlayerIds().length
					)
				);
				String[] strings = minecraftServer.getPlayerManager().getWhitelistedNames();
				commandSource.sendMessage(new LiteralText(concat(strings)));
			} else if ("add".equals(args[0])) {
				if (args.length < 2) {
					throw new IncorrectUsageException("commands.whitelist.add.usage");
				}

				GameProfile gameProfile = minecraftServer.getUserCache().findByName(args[1]);
				if (gameProfile == null) {
					throw new CommandException("commands.whitelist.add.failed", args[1]);
				}

				minecraftServer.getPlayerManager().whitelist(gameProfile);
				run(commandSource, this, "commands.whitelist.add.success", new Object[]{args[1]});
			} else if ("remove".equals(args[0])) {
				if (args.length < 2) {
					throw new IncorrectUsageException("commands.whitelist.remove.usage");
				}

				GameProfile gameProfile2 = minecraftServer.getPlayerManager().getWhitelist().getProfile(args[1]);
				if (gameProfile2 == null) {
					throw new CommandException("commands.whitelist.remove.failed", args[1]);
				}

				minecraftServer.getPlayerManager().unWhitelist(gameProfile2);
				run(commandSource, this, "commands.whitelist.remove.success", new Object[]{args[1]});
			} else if ("reload".equals(args[0])) {
				minecraftServer.getPlayerManager().reloadWhitelist();
				run(commandSource, this, "commands.whitelist.reloaded", new Object[0]);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"on", "off", "list", "add", "remove", "reload"});
		} else {
			if (strings.length == 2) {
				if ("remove".equals(strings[0])) {
					return method_2894(strings, server.getPlayerManager().getWhitelistedNames());
				}

				if ("add".equals(strings[0])) {
					return method_2894(strings, server.getUserCache().getNames());
				}
			}

			return Collections.emptyList();
		}
	}
}
