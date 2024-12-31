package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 9) {
			throw new IncorrectUsageException("commands.compare.usage");
		} else {
			commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(commandSource, args, 0, false);
			BlockPos blockPos2 = getBlockPos(commandSource, args, 3, false);
			BlockPos blockPos3 = getBlockPos(commandSource, args, 6, false);
			BlockBox blockBox = new BlockBox(blockPos, blockPos2);
			BlockBox blockBox2 = new BlockBox(blockPos3, blockPos3.add(blockBox.getDimensions()));
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 524288) {
				throw new CommandException("commands.compare.tooManyBlocks", i, 524288);
			} else if (blockBox.minY >= 0 && blockBox.maxY < 256 && blockBox2.minY >= 0 && blockBox2.maxY < 256) {
				World world = commandSource.getWorld();
				if (world.isRegionLoaded(blockBox) && world.isRegionLoaded(blockBox2)) {
					boolean bl = false;
					if (args.length > 9 && "masked".equals(args[9])) {
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
											NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
											nbtCompound.remove("x");
											nbtCompound.remove("y");
											nbtCompound.remove("z");
											NbtCompound nbtCompound2 = blockEntity2.toNbt(new NbtCompound());
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

					commandSource.setStat(CommandStats.Type.AFFECTED_BLOCKS, i);
					run(commandSource, this, "commands.compare.success", new Object[]{i});
				} else {
					throw new CommandException("commands.compare.outOfWorld");
				}
			} else {
				throw new CommandException("commands.compare.outOfWorld");
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length > 0 && strings.length <= 3) {
			return method_10707(strings, 0, pos);
		} else if (strings.length > 3 && strings.length <= 6) {
			return method_10707(strings, 3, pos);
		} else if (strings.length > 6 && strings.length <= 9) {
			return method_10707(strings, 6, pos);
		} else {
			return strings.length == 10 ? method_2894(strings, new String[]{"masked", "all"}) : Collections.emptyList();
		}
	}
}
