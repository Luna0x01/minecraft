package net.minecraft.server.dedicated.command;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BanIpCommand extends AbstractCommand {
	public static final Pattern field_2725 = Pattern.compile(
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
	);

	@Override
	public String getCommandName() {
		return "ban-ip";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public boolean method_3278(MinecraftServer server, CommandSource source) {
		return server.getPlayerManager().getIpBanList().isEnabled() && super.method_3278(server, source);
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.banip.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].length() > 1) {
			Text text = args.length >= 2 ? method_4635(commandSource, args, 1) : null;
			Matcher matcher = field_2725.matcher(args[0]);
			if (matcher.matches()) {
				this.method_2054(minecraftServer, commandSource, args[0], text == null ? null : text.asUnformattedString());
			} else {
				ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(args[0]);
				if (serverPlayerEntity == null) {
					throw new PlayerNotFoundException("commands.banip.invalid");
				}

				this.method_2054(minecraftServer, commandSource, serverPlayerEntity.getIp(), text == null ? null : text.asUnformattedString());
			}
		} else {
			throw new IncorrectUsageException("commands.banip.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}

	protected void method_2054(MinecraftServer minecraftServer, CommandSource commandSource, String string, @Nullable String string2) {
		BannedIpEntry bannedIpEntry = new BannedIpEntry(string, null, commandSource.getTranslationKey(), null, string2);
		minecraftServer.getPlayerManager().getIpBanList().add(bannedIpEntry);
		List<ServerPlayerEntity> list = minecraftServer.getPlayerManager().getPlayersByIp(string);
		String[] strings = new String[list.size()];
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : list) {
			serverPlayerEntity.networkHandler.disconnect("You have been IP banned.");
			strings[i++] = serverPlayerEntity.getTranslationKey();
		}

		if (list.isEmpty()) {
			run(commandSource, this, "commands.banip.success", new Object[]{string});
		} else {
			run(commandSource, this, "commands.banip.success.players", new Object[]{string, concat(strings)});
		}
	}
}
