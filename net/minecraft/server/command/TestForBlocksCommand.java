package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestForBlocksCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "testforblocks";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.compare.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 9) {
			throw new IncorrectUsageException("commands.compare.usage");
		} else {
			source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(source, args, 0, false);
			BlockPos blockPos2 = getBlockPos(source, args, 3, false);
			BlockPos blockPos3 = getBlockPos(source, args, 6, false);
			BlockBox blockBox = new BlockBox(blockPos, blockPos2);
			BlockBox blockBox2 = new BlockBox(blockPos3, blockPos3.add(blockBox.getDimensions()));
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 524288) {
				throw new CommandException("commands.compare.tooManyBlocks", i, 524288);
			} else if (blockBox.minY >= 0 && blockBox.maxY < 256 && blockBox2.minY >= 0 && blockBox2.maxY < 256) {
				World world = source.getWorld();
				if (world.isRegionLoaded(blockBox) && world.isRegionLoaded(blockBox2)) {
					boolean bl = false;
					if (args.length > 9 && args[9].equals("masked")) {
						bl = true;
					}

					i = 0;
					BlockPos blockPos4 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);
					BlockPos.Mutable mutable = new BlockPos.Mutable();
					BlockPos.Mutable mutable2 = new BlockPos.Mutable();

					for (int j = blockBox.minZ; j <= blockBox.maxZ; j++) {
						for (int k = blockBox.minY; k <= blockBox.maxY; k++) {
							for (int l = blockBox.minX; l <= blockBox.maxX; l++) {
								mutable.setPosition(l, k, j);
								mutable2.setPosition(l + blockPos4.getX(), k + blockPos4.getY(), j + blockPos4.getZ());
								boolean bl2 = false;
								BlockState blockState = world.getBlockState(mutable);
								if (!bl || blockState.getBlock() != Blocks.AIR) {
									if (blockState == world.getBlockState(mutable2)) {
										BlockEntity blockEntity = world.getBlockEntity(mutable);
										BlockEntity blockEntity2 = world.getBlockEntity(mutable2);
										if (blockEntity != null && blockEntity2 != null) {
											NbtCompound nbtCompound = new NbtCompound();
											blockEntity.toNbt(nbtCompound);
											nbtCompound.remove("x");
											nbtCompound.remove("y");
											nbtCompound.remove("z");
											NbtCompound nbtCompound2 = new NbtCompound();
											blockEntity2.toNbt(nbtCompound2);
											nbtCompound2.remove("x");
											nbtCompound2.remove("y");
											nbtCompound2.remove("z");
											if (!nbtCompound.equals(nbtCompound2)) {
												bl2 = true;
											}
										} else if (blockEntity != null) {
											bl2 = true;
										}
									} else {
										bl2 = true;
									}

									i++;
									if (bl2) {
										throw new CommandException("commands.compare.failed");
									}
								}
							}
						}
					}

					source.setStat(CommandStats.Type.AFFECTED_BLOCKS, i);
					run(source, this, "commands.compare.success", new Object[]{i});
				} else {
					throw new CommandException("commands.compare.outOfWorld");
				}
			} else {
				throw new CommandException("commands.compare.outOfWorld");
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
		} else {
			return args.length == 10 ? method_2894(args, new String[]{"masked", "all"}) : null;
		}
	}
}
