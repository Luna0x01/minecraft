package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 5) {
			throw new IncorrectUsageException("commands.execute.usage");
		} else {
			final Entity entity = getEntity(source, args[0], Entity.class);
			final double d = parseDouble(entity.x, args[1], false);
			final double e = parseDouble(entity.y, args[2], false);
			final double f = parseDouble(entity.z, args[3], false);
			final BlockPos blockPos = new BlockPos(d, e, f);
			int i = 4;
			if ("detect".equals(args[4]) && args.length > 10) {
				World world = entity.getWorld();
				double g = parseDouble(d, args[5], false);
				double h = parseDouble(e, args[6], false);
				double j = parseDouble(f, args[7], false);
				Block block = getBlock(source, args[8]);
				int k = parseClampedInt(args[9], -1, 15);
				BlockPos blockPos2 = new BlockPos(g, h, j);
				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getBlock() != block || k >= 0 && blockState.getBlock().getData(blockState) != k) {
					throw new CommandException("commands.execute.failed", "detect", entity.getTranslationKey());
				}

				i = 10;
			}

			String string = method_10706(args, i);
			final CommandSource commandSource = source;
			CommandSource commandSource2 = new CommandSource() {
				@Override
				public String getTranslationKey() {
					return entity.getTranslationKey();
				}

				@Override
				public Text getName() {
					return entity.getName();
				}

				@Override
				public void sendMessage(Text text) {
					commandSource.sendMessage(text);
				}

				@Override
				public boolean canUseCommand(int permissionLevel, String commandLiteral) {
					return commandSource.canUseCommand(permissionLevel, commandLiteral);
				}

				@Override
				public BlockPos getBlockPos() {
					return blockPos;
				}

				@Override
				public Vec3d getPos() {
					return new Vec3d(d, e, f);
				}

				@Override
				public World getWorld() {
					return entity.world;
				}

				@Override
				public Entity getEntity() {
					return entity;
				}

				@Override
				public boolean sendCommandFeedback() {
					MinecraftServer minecraftServer = MinecraftServer.getServer();
					return minecraftServer == null || minecraftServer.worlds[0].getGameRules().getBoolean("commandBlockOutput");
				}

				@Override
				public void setStat(CommandStats.Type statsType, int value) {
					entity.setStat(statsType, value);
				}
			};
			CommandRegistryProvider commandRegistryProvider = MinecraftServer.getServer().getCommandManager();

			try {
				int l = commandRegistryProvider.execute(commandSource2, string);
				if (l < 1) {
					throw new CommandException("commands.execute.allInvocationsFailed", string);
				}
			} catch (Throwable var23) {
				throw new CommandException("commands.execute.failed", string, entity.getTranslationKey());
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, MinecraftServer.getServer().getPlayerNames());
		} else if (args.length > 1 && args.length <= 4) {
			return method_10707(args, 1, pos);
		} else if (args.length > 5 && args.length <= 8 && "detect".equals(args[4])) {
			return method_10707(args, 5, pos);
		} else {
			return args.length == 9 && "detect".equals(args[4]) ? method_10708(args, Block.REGISTRY.keySet()) : null;
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
