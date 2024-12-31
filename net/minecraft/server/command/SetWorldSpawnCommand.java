package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "setworldspawn";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.setworldspawn.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		BlockPos blockPos;
		if (args.length == 0) {
			blockPos = getAsPlayer(source).getBlockPos();
		} else {
			if (args.length != 3 || source.getWorld() == null) {
				throw new IncorrectUsageException("commands.setworldspawn.usage");
			}

			blockPos = getBlockPos(source, args, 0, true);
		}

		source.getWorld().setSpawnPos(blockPos);
		MinecraftServer.getServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		run(source, this, "commands.setworldspawn.success", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length > 0 && args.length <= 3 ? method_10707(args, 0, pos) : null;
	}
}
