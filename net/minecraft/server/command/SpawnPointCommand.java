package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length > 1 && args.length < 4) {
			throw new IncorrectUsageException("commands.spawnpoint.usage");
		} else {
			ServerPlayerEntity serverPlayerEntity = args.length > 0 ? method_4639(minecraftServer, commandSource, args[0]) : getAsPlayer(commandSource);
			BlockPos blockPos = args.length > 3 ? getBlockPos(commandSource, args, 1, true) : serverPlayerEntity.getBlockPos();
			if (serverPlayerEntity.world != null) {
				serverPlayerEntity.setPlayerSpawn(blockPos, true);
				run(
					commandSource,
					this,
					"commands.spawnpoint.success",
					new Object[]{serverPlayerEntity.getTranslationKey(), blockPos.getX(), blockPos.getY(), blockPos.getZ()}
				);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else {
			return strings.length > 1 && strings.length <= 4 ? method_10707(strings, 1, pos) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
