package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FillCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "fill";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.fill.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 7) {
			throw new IncorrectUsageException("commands.fill.usage");
		} else {
			commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(commandSource, args, 0, false);
			BlockPos blockPos2 = getBlockPos(commandSource, args, 3, false);
			Block block = AbstractCommand.getBlock(commandSource, args[6]);
			BlockState blockState;
			if (args.length >= 8) {
				blockState = method_13901(block, args[7]);
			} else {
				blockState = block.getDefaultState();
			}

			BlockPos blockPos3 = new BlockPos(
				Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ())
			);
			BlockPos blockPos4 = new BlockPos(
				Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ())
			);
			int i = (blockPos4.getX() - blockPos3.getX() + 1) * (blockPos4.getY() - blockPos3.getY() + 1) * (blockPos4.getZ() - blockPos3.getZ() + 1);
			if (i > 32768) {
				throw new CommandException("commands.fill.tooManyBlocks", i, 32768);
			} else if (blockPos3.getY() >= 0 && blockPos4.getY() < 256) {
				World world = commandSource.getWorld();

				for (int j = blockPos3.getZ(); j <= blockPos4.getZ(); j += 16) {
					for (int k = blockPos3.getX(); k <= blockPos4.getX(); k += 16) {
						if (!world.blockExists(new BlockPos(k, blockPos4.getY() - blockPos3.getY(), j))) {
							throw new CommandException("commands.fill.outOfWorld");
						}
					}
				}

				NbtCompound nbtCompound = new NbtCompound();
				boolean bl = false;
				if (args.length >= 10 && block.hasBlockEntity()) {
					String string = method_10706(args, 9);

					try {
						nbtCompound = StringNbtReader.parse(string);
						bl = true;
					} catch (NbtException var21) {
						throw new CommandException("commands.fill.tagError", var21.getMessage());
					}
				}

				List<BlockPos> list = Lists.newArrayList();
				i = 0;

				for (int l = blockPos3.getZ(); l <= blockPos4.getZ(); l++) {
					for (int m = blockPos3.getY(); m <= blockPos4.getY(); m++) {
						for (int n = blockPos3.getX(); n <= blockPos4.getX(); n++) {
							BlockPos blockPos5 = new BlockPos(n, m, l);
							if (args.length >= 9) {
								if (!"outline".equals(args[8]) && !"hollow".equals(args[8])) {
									if ("destroy".equals(args[8])) {
										world.removeBlock(blockPos5, true);
									} else if ("keep".equals(args[8])) {
										if (!world.isAir(blockPos5)) {
											continue;
										}
									} else if ("replace".equals(args[8]) && !block.hasBlockEntity() && args.length > 9) {
										Block block2 = AbstractCommand.getBlock(commandSource, args[9]);
										if (world.getBlockState(blockPos5).getBlock() != block2
											|| args.length > 10
												&& !"-1".equals(args[10])
												&& !"*".equals(args[10])
												&& !AbstractCommand.method_13904(block2, args[10]).apply(world.getBlockState(blockPos5))) {
											continue;
										}
									}
								} else if (n != blockPos3.getX()
									&& n != blockPos4.getX()
									&& m != blockPos3.getY()
									&& m != blockPos4.getY()
									&& l != blockPos3.getZ()
									&& l != blockPos4.getZ()) {
									if ("hollow".equals(args[8])) {
										world.setBlockState(blockPos5, Blocks.AIR.getDefaultState(), 2);
										list.add(blockPos5);
									}
									continue;
								}
							}

							BlockEntity blockEntity = world.getBlockEntity(blockPos5);
							if (blockEntity != null && blockEntity instanceof Inventory) {
								((Inventory)blockEntity).clear();
							}

							if (world.setBlockState(blockPos5, blockState, 2)) {
								list.add(blockPos5);
								i++;
								if (bl) {
									BlockEntity blockEntity2 = world.getBlockEntity(blockPos5);
									if (blockEntity2 != null) {
										nbtCompound.putInt("x", blockPos5.getX());
										nbtCompound.putInt("y", blockPos5.getY());
										nbtCompound.putInt("z", blockPos5.getZ());
										blockEntity2.fromNbt(nbtCompound);
									}
								}
							}
						}
					}
				}

				for (BlockPos blockPos6 : list) {
					Block block3 = world.getBlockState(blockPos6).getBlock();
					world.method_8531(blockPos6, block3, false);
				}

				if (i <= 0) {
					throw new CommandException("commands.fill.failed");
				} else {
					commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, i);
					run(commandSource, this, "commands.fill.success", new Object[]{i});
				}
			} else {
				throw new CommandException("commands.fill.outOfWorld");
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length > 0 && strings.length <= 3) {
			return method_10707(strings, 0, pos);
		} else if (strings.length > 3 && strings.length <= 6) {
			return method_10707(strings, 3, pos);
		} else if (strings.length == 7) {
			return method_10708(strings, Block.REGISTRY.getKeySet());
		} else if (strings.length == 9) {
			return method_2894(strings, new String[]{"replace", "destroy", "keep", "hollow", "outline"});
		} else {
			return strings.length == 10 && "replace".equals(strings[8]) ? method_10708(strings, Block.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}
}
