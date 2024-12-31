package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class SpawnPointCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "spawnpoint";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.spawnpoint.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length > 1 && args.length < 4) {
			throw new IncorrectUsageException("commands.spawnpoint.usage");
		} else {
			ServerPlayerEntity serverPlayerEntity = args.length > 0 ? getPlayer(source, args[0]) : getAsPlayer(source);
			BlockPos blockPos = args.length > 3 ? getBlockPos(source, args, 1, true) : serverPlayerEntity.getBlockPos();
			if (serverPlayerEntity.world != null) {
				serverPlayerEntity.setPlayerSpawn(blockPos, true);
				run(source, this, "commands.spawnpoint.success", new Object[]{serverPlayerEntity.getTranslationKey(), blockPos.getX(), blockPos.getY(), blockPos.getZ()});
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, MinecraftServer.getServer().getPlayerNames());
		} else {
			return args.length > 1 && args.length <= 4 ? method_10707(args, 1, pos) : null;
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
