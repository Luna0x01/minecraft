package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.LinkedList;
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
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CloneCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "clone";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.clone.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 9) {
			throw new IncorrectUsageException("commands.clone.usage");
		} else {
			source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(source, args, 0, false);
			BlockPos blockPos2 = getBlockPos(source, args, 3, false);
			BlockPos blockPos3 = getBlockPos(source, args, 6, false);
			BlockBox blockBox = new BlockBox(blockPos, blockPos2);
			BlockBox blockBox2 = new BlockBox(blockPos3, blockPos3.add(blockBox.getDimensions()));
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 32768) {
				throw new CommandException("commands.clone.tooManyBlocks", i, 32768);
			} else {
				boolean bl = false;
				Block block = null;
				int j = -1;
				if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && blockBox.intersects(blockBox2)) {
					throw new CommandException("commands.clone.noOverlap");
				} else {
					if (args.length >= 11 && args[10].equals("move")) {
						bl = true;
					}

					if (blockBox.minY >= 0 && blockBox.maxY < 256 && blockBox2.minY >= 0 && blockBox2.maxY < 256) {
						World world = source.getWorld();
						if (world.isRegionLoaded(blockBox) && world.isRegionLoaded(blockBox2)) {
							boolean bl2 = false;
							if (args.length >= 10) {
								if (args[9].equals("masked")) {
									bl2 = true;
								} else if (args[9].equals("filtered")) {
									if (args.length < 12) {
										throw new IncorrectUsageException("commands.clone.usage");
									}

									block = getBlock(source, args[11]);
									if (args.length >= 13) {
										j = parseClampedInt(args[12], 0, 15);
									}
								}
							}

							List<CloneCommand.BlockInfo> list = Lists.newArrayList();
							List<CloneCommand.BlockInfo> list2 = Lists.newArrayList();
							List<CloneCommand.BlockInfo> list3 = Lists.newArrayList();
							LinkedList<BlockPos> linkedList = Lists.newLinkedList();
							BlockPos blockPos4 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);

							for (int k = blockBox.minZ; k <= blockBox.maxZ; k++) {
								for (int l = blockBox.minY; l <= blockBox.maxY; l++) {
									for (int m = blockBox.minX; m <= blockBox.maxX; m++) {
										BlockPos blockPos5 = new BlockPos(m, l, k);
										BlockPos blockPos6 = blockPos5.add(blockPos4);
										BlockState blockState = world.getBlockState(blockPos5);
										if ((!bl2 || blockState.getBlock() != Blocks.AIR)
											&& (block == null || blockState.getBlock() == block && (j < 0 || blockState.getBlock().getData(blockState) == j))) {
											BlockEntity blockEntity = world.getBlockEntity(blockPos5);
											if (blockEntity != null) {
												NbtCompound nbtCompound = new NbtCompound();
												blockEntity.toNbt(nbtCompound);
												list2.add(new CloneCommand.BlockInfo(blockPos6, blockState, nbtCompound));
												linkedList.addLast(blockPos5);
											} else if (!blockState.getBlock().isFullBlock() && !blockState.getBlock().renderAsNormalBlock()) {
												list3.add(new CloneCommand.BlockInfo(blockPos6, blockState, null));
												linkedList.addFirst(blockPos5);
											} else {
												list.add(new CloneCommand.BlockInfo(blockPos6, blockState, null));
												linkedList.addLast(blockPos5);
											}
										}
									}
								}
							}

							if (bl) {
								for (BlockPos blockPos7 : linkedList) {
									BlockEntity blockEntity2 = world.getBlockEntity(blockPos7);
									if (blockEntity2 instanceof Inventory) {
										((Inventory)blockEntity2).clear();
									}

									world.setBlockState(blockPos7, Blocks.BARRIER.getDefaultState(), 2);
								}

								for (BlockPos blockPos8 : linkedList) {
									world.setBlockState(blockPos8, Blocks.AIR.getDefaultState(), 3);
								}
							}

							List<CloneCommand.BlockInfo> list4 = Lists.newArrayList();
							list4.addAll(list);
							list4.addAll(list2);
							list4.addAll(list3);
							List<CloneCommand.BlockInfo> list5 = Lists.reverse(list4);

							for (CloneCommand.BlockInfo blockInfo : list5) {
								BlockEntity blockEntity3 = world.getBlockEntity(blockInfo.field_12011);
								if (blockEntity3 instanceof Inventory) {
									((Inventory)blockEntity3).clear();
								}

								world.setBlockState(blockInfo.field_12011, Blocks.BARRIER.getDefaultState(), 2);
							}

							i = 0;

							for (CloneCommand.BlockInfo blockInfo2 : list4) {
								if (world.setBlockState(blockInfo2.field_12011, blockInfo2.field_12012, 2)) {
									i++;
								}
							}

							for (CloneCommand.BlockInfo blockInfo3 : list2) {
								BlockEntity blockEntity4 = world.getBlockEntity(blockInfo3.field_12011);
								if (blockInfo3.field_12013 != null && blockEntity4 != null) {
									blockInfo3.field_12013.putInt("x", blockInfo3.field_12011.getX());
									blockInfo3.field_12013.putInt("y", blockInfo3.field_12011.getY());
									blockInfo3.field_12013.putInt("z", blockInfo3.field_12011.getZ());
									blockEntity4.fromNbt(blockInfo3.field_12013);
									blockEntity4.markDirty();
								}

								world.setBlockState(blockInfo3.field_12011, blockInfo3.field_12012, 2);
							}

							for (CloneCommand.BlockInfo blockInfo4 : list5) {
								world.updateNeighbors(blockInfo4.field_12011, blockInfo4.field_12012.getBlock());
							}

							List<ScheduledTick> list6 = world.getScheduledTicks(blockBox, false);
							if (list6 != null) {
								for (ScheduledTick scheduledTick : list6) {
									if (blockBox.contains(scheduledTick.pos)) {
										BlockPos blockPos9 = scheduledTick.pos.add(blockPos4);
										world.scheduleTick(blockPos9, scheduledTick.getBlock(), (int)(scheduledTick.time - world.getLevelProperties().getTime()), scheduledTick.priority);
									}
								}
							}

							if (i <= 0) {
								throw new CommandException("commands.clone.failed");
							} else {
								source.setStat(CommandStats.Type.AFFECTED_BLOCKS, i);
								run(source, this, "commands.clone.success", new Object[]{i});
							}
						} else {
							throw new CommandException("commands.clone.outOfWorld");
						}
					} else {
						throw new CommandException("commands.clone.outOfWorld");
					}
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length > 0 && args.length <= 3) {
			return method_10707(args, 0, pos);
		} else if (args.length > 3 && args.length <= 6) {
			return method_10707(args, 3, pos);
		} else if (args.length > 6 && args.length <= 9) {
			return method_10707(args, 6, pos);
		} else if (args.length == 10) {
			return method_2894(args, new String[]{"replace", "masked", "filtered"});
		} else if (args.length == 11) {
			return method_2894(args, new String[]{"normal", "force", "move"});
		} else {
			return args.length == 12 && "filtered".equals(args[9]) ? method_10708(args, Block.REGISTRY.keySet()) : null;
		}
	}

	static class BlockInfo {
		public final BlockPos field_12011;
		public final BlockState field_12012;
		public final NbtCompound field_12013;

		public BlockInfo(BlockPos blockPos, BlockState blockState, NbtCompound nbtCompound) {
			this.field_12011 = blockPos;
			this.field_12012 = blockState;
			this.field_12013 = nbtCompound;
		}
	}
}
