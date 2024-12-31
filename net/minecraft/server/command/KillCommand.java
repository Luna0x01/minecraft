package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length == 0) {
			PlayerEntity playerEntity = getAsPlayer(source);
			playerEntity.kill();
			run(source, this, "commands.kill.successful", new Object[]{playerEntity.getName()});
		} else {
			Entity entity = getEntity(source, args[0]);
			entity.kill();
			run(source, this, "commands.kill.successful", new Object[]{entity.getName()});
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, MinecraftServer.getServer().getPlayerNames()) : null;
	}
}
