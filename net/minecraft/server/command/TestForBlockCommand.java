package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestForBlockCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "testforblock";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.testforblock.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new IncorrectUsageException("commands.testforblock.usage");
		} else {
			source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(source, args, 0, false);
			Block block = Block.get(args[3]);
			if (block == null) {
				throw new InvalidNumberException("commands.setblock.notFound", args[3]);
			} else {
				int i = -1;
				if (args.length >= 5) {
					i = parseClampedInt(args[4], -1, 15);
				}

				World world = source.getWorld();
				if (!world.blockExists(blockPos)) {
					throw new CommandException("commands.testforblock.outOfWorld");
				} else {
					NbtCompound nbtCompound = new NbtCompound();
					boolean bl = false;
					if (args.length >= 6 && block.hasBlockEntity()) {
						String string = method_4635(source, args, 5).asUnformattedString();

						try {
							nbtCompound = StringNbtReader.parse(string);
							bl = true;
						} catch (NbtException var13) {
							throw new CommandException("commands.setblock.tagError", var13.getMessage());
						}
					}

					BlockState blockState = world.getBlockState(blockPos);
					Block block2 = blockState.getBlock();
					if (block2 != block) {
						throw new CommandException(
							"commands.testforblock.failed.tile", blockPos.getX(), blockPos.getY(), blockPos.getZ(), block2.getTranslatedName(), block.getTranslatedName()
						);
					} else {
						if (i > -1) {
							int j = blockState.getBlock().getData(blockState);
							if (j != i) {
								throw new CommandException("commands.testforblock.failed.data", blockPos.getX(), blockPos.getY(), blockPos.getZ(), j, i);
							}
						}

						if (bl) {
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							if (blockEntity == null) {
								throw new CommandException("commands.testforblock.failed.tileEntity", blockPos.getX(), blockPos.getY(), blockPos.getZ());
							}

							NbtCompound nbtCompound2 = new NbtCompound();
							blockEntity.toNbt(nbtCompound2);
							if (!NbtHelper.matches(nbtCompound, nbtCompound2, true)) {
								throw new CommandException("commands.testforblock.failed.nbt", blockPos.getX(), blockPos.getY(), blockPos.getZ());
							}
						}

						source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 1);
						run(source, this, "commands.testforblock.success", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
					}
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length > 0 && args.length <= 3) {
			return method_10707(args, 0, pos);
		} else {
			return args.length == 4 ? method_10708(args, Block.REGISTRY.keySet()) : null;
		}
	}
}
