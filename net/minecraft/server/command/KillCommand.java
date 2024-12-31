package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class KillCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "kill";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.kill.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length == 0) {
			PlayerEntity playerEntity = getAsPlayer(commandSource);
			playerEntity.kill();
			run(commandSource, this, "commands.kill.successful", new Object[]{playerEntity.getName()});
		} else {
			Entity entity = method_10711(minecraftServer, commandSource, args[0]);
			entity.kill();
			run(commandSource, this, "commands.kill.successful", new Object[]{entity.getName()});
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}
}
