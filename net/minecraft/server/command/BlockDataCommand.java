package net.minecraft.server.command;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDataCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "blockdata";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.blockdata.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new IncorrectUsageException("commands.blockdata.usage");
		} else {
			source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockPos = getBlockPos(source, args, 0, false);
			World world = source.getWorld();
			if (!world.blockExists(blockPos)) {
				throw new CommandException("commands.blockdata.outOfWorld");
			} else {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity == null) {
					throw new CommandException("commands.blockdata.notValid");
				} else {
					NbtCompound nbtCompound = new NbtCompound();
					blockEntity.toNbt(nbtCompound);
					NbtCompound nbtCompound2 = (NbtCompound)nbtCompound.copy();

					NbtCompound nbtCompound3;
					try {
						nbtCompound3 = StringNbtReader.parse(method_4635(source, args, 3).asUnformattedString());
					} catch (NbtException var10) {
						throw new CommandException("commands.blockdata.tagError", var10.getMessage());
					}

					nbtCompound.copyFrom(nbtCompound3);
					nbtCompound.putInt("x", blockPos.getX());
					nbtCompound.putInt("y", blockPos.getY());
					nbtCompound.putInt("z", blockPos.getZ());
					if (nbtCompound.equals(nbtCompound2)) {
						throw new CommandException("commands.blockdata.failed", nbtCompound.toString());
					} else {
						blockEntity.fromNbt(nbtCompound);
						blockEntity.markDirty();
						world.onBlockUpdate(blockPos);
						source.setStat(CommandStats.Type.AFFECTED_BLOCKS, 1);
						run(source, this, "commands.blockdata.success", new Object[]{nbtCompound.toString()});
					}
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length > 0 && args.length <= 3 ? method_10707(args, 0, pos) : null;
	}
}
