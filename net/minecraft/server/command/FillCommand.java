package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 7) {
			throw new IncorrectUsageException("commands.fill.usage");
		} else {
			source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(source, args, 0, false);
			BlockPos blockPos2 = getBlockPos(source, args, 3, false);
			Block block = AbstractCommand.getBlock(source, args[6]);
			int i = 0;
			if (args.length >= 8) {
				i = parseClampedInt(args[7], 0, 15);
			}

			BlockPos blockPos3 = new BlockPos(
				Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ())
			);
			BlockPos blockPos4 = new BlockPos(
				Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ())
			);
			int j = (blockPos4.getX() - blockPos3.getX() + 1) * (blockPos4.getY() - blockPos3.getY() + 1) * (blockPos4.getZ() - blockPos3.getZ() + 1);
			if (j > 32768) {
				throw new CommandException("commands.fill.tooManyBlocks", j, 32768);
			} else if (blockPos3.getY() >= 0 && blockPos4.getY() < 256) {
				World world = source.getWorld();

				for (int k = blockPos3.getZ(); k < blockPos4.getZ() + 16; k += 16) {
					for (int l = blockPos3.getX(); l < blockPos4.getX() + 16; l += 16) {
						if (!world.blockExists(new BlockPos(l, blockPos4.getY() - blockPos3.getY(), k))) {
							throw new CommandException("commands.fill.outOfWorld");
						}
					}
				}

				NbtCompound nbtCompound = new NbtCompound();
				boolean bl = false;
				if (args.length >= 10 && block.hasBlockEntity()) {
					String string = method_4635(source, args, 9).asUnformattedString();

					try {
						nbtCompound = StringNbtReader.parse(string);
						bl = true;
					} catch (NbtException var21) {
						throw new CommandException("commands.fill.tagError", var21.getMessage());
					}
				}

				List<BlockPos> list = Lists.newArrayList();
				j = 0;

				for (int m = blockPos3.getZ(); m <= blockPos4.getZ(); m++) {
					for (int n = blockPos3.getY(); n <= blockPos4.getY(); n++) {
						for (int o = blockPos3.getX(); o <= blockPos4.getX(); o++) {
							BlockPos blockPos5 = new BlockPos(o, n, m);
							if (args.length >= 9) {
								if (!args[8].equals("outline") && !args[8].equals("hollow")) {
									if (args[8].equals("destroy")) {
										world.removeBlock(blockPos5, true);
									} else if (args[8].equals("keep")) {
										if (!world.isAir(blockPos5)) {
											continue;
										}
									} else if (args[8].equals("replace") && !block.hasBlockEntity()) {
										if (args.length > 9) {
											Block block2 = AbstractCommand.getBlock(source, args[9]);
											if (world.getBlockState(blockPos5).getBlock() != block2) {
												continue;
											}
										}

										if (args.length > 10) {
											int p = AbstractCommand.parseInt(args[10]);
											BlockState blockState = world.getBlockState(blockPos5);
											if (blockState.getBlock().getData(blockState) != p) {
												continue;
											}
										}
									}
								} else if (o != blockPos3.getX()
									&& o != blockPos4.getX()
									&& n != blockPos3.getY()
									&& n != blockPos4.getY()
									&& m != blockPos3.getZ()
									&& m != blockPos4.getZ()) {
									if (args[8].equals("hollow")) {
										world.setBlockState(blockPos5, Blocks.AIR.getDefaultState(), 2);
										list.add(blockPos5);
									}
									continue;
								}
							}

							BlockEntity blockEntity = world.getBlockEntity(blockPos5);
							if (blockEntity != null) {
								if (blockEntity instanceof Inventory) {
									((Inventory)blockEntity).clear();
								}

								world.setBlockState(blockPos5, Blocks.BARRIER.getDefaultState(), block == Blocks.BARRIER ? 2 : 4);
							}

							BlockState blockState2 = block.stateFromData(i);
							if (world.setBlockState(blockPos5, blockState2, 2)) {
								list.add(blockPos5);
								j++;
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
					world.updateNeighbors(blockPos6, block3);
				}

				if (j <= 0) {
					throw new CommandException("commands.fill.failed");
				} else {
					source.setStat(CommandStats.Type.AFFECTED_BLOCKS, j);
					run(source, this, "commands.fill.success", new Object[]{j});
				}
			} else {
				throw new CommandException("commands.fill.outOfWorld");
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length > 0 && args.length <= 3) {
			return method_10707(args, 0, pos);
		} else if (args.length > 3 && args.length <= 6) {
			return method_10707(args, 3, pos);
		} else if (args.length == 7) {
			return method_10708(args, Block.REGISTRY.keySet());
		} else if (args.length == 9) {
			return method_2894(args, new String[]{"replace", "destroy", "keep", "hollow", "outline"});
		} else {
			return args.length == 10 && "replace".equals(args[8]) ? method_10708(args, Block.REGISTRY.keySet()) : null;
		}
	}
}
