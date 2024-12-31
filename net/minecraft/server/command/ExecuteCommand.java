package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3289;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExecuteCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "execute";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.execute.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 5) {
			throw new IncorrectUsageException("commands.execute.usage");
		} else {
			Entity entity = method_12702(minecraftServer, commandSource, args[0], Entity.class);
			double d = parseDouble(entity.x, args[1], false);
			double e = parseDouble(entity.y, args[2], false);
			double f = parseDouble(entity.z, args[3], false);
			new BlockPos(d, e, f);
			int i = 4;
			if ("detect".equals(args[4]) && args.length > 10) {
				World world = entity.getWorld();
				double g = parseDouble(d, args[5], false);
				double h = parseDouble(e, args[6], false);
				double j = parseDouble(f, args[7], false);
				Block block = getBlock(commandSource, args[8]);
				BlockPos blockPos2 = new BlockPos(g, h, j);
				if (!world.blockExists(blockPos2)) {
					throw new CommandException("commands.execute.failed", "detect", entity.getTranslationKey());
				}

				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getBlock() != block) {
					throw new CommandException("commands.execute.failed", "detect", entity.getTranslationKey());
				}

				if (!AbstractCommand.method_13904(block, args[9]).apply(blockState)) {
					throw new CommandException("commands.execute.failed", "detect", entity.getTranslationKey());
				}

				i = 10;
			}

			String string = method_10706(args, i);
			CommandSource commandSource2 = class_3289.method_14640(commandSource)
				.method_14641(entity, new Vec3d(d, e, f))
				.method_14642(minecraftServer.worlds[0].getGameRules().getBoolean("commandBlockOutput"));
			CommandRegistryProvider commandRegistryProvider = minecraftServer.getCommandManager();

			try {
				int k = commandRegistryProvider.execute(commandSource2, string);
				if (k < 1) {
					throw new CommandException("commands.execute.allInvocationsFailed", string);
				}
			} catch (Throwable var23) {
				throw new CommandException("commands.execute.failed", string, entity.getTranslationKey());
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else if (strings.length > 1 && strings.length <= 4) {
			return method_10707(strings, 1, pos);
		} else if (strings.length > 5 && strings.length <= 8 && "detect".equals(strings[4])) {
			return method_10707(strings, 5, pos);
		} else {
			return strings.length == 9 && "detect".equals(strings[4]) ? method_10708(strings, Block.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
