package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		BlockPos blockPos;
		if (args.length == 0) {
			blockPos = getAsPlayer(commandSource).getBlockPos();
		} else {
			if (args.length != 3 || commandSource.getWorld() == null) {
				throw new IncorrectUsageException("commands.setworldspawn.usage");
			}

			blockPos = getBlockPos(commandSource, args, 0, true);
		}

		commandSource.getWorld().setSpawnPos(blockPos);
		minecraftServer.getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		run(commandSource, this, "commands.setworldspawn.success", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length > 0 && strings.length <= 3 ? method_10707(strings, 0, pos) : Collections.emptyList();
	}
}
