package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;

public class EntityDataCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "entitydata";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.entitydata.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.entitydata.usage");
		} else {
			Entity entity = getEntity(source, args[0]);
			if (entity instanceof PlayerEntity) {
				throw new CommandException("commands.entitydata.noPlayers", entity.getName());
			} else {
				NbtCompound nbtCompound = new NbtCompound();
				entity.writePlayerData(nbtCompound);
				NbtCompound nbtCompound2 = (NbtCompound)nbtCompound.copy();

				NbtCompound nbtCompound3;
				try {
					nbtCompound3 = StringNbtReader.parse(method_4635(source, args, 1).asUnformattedString());
				} catch (NbtException var8) {
					throw new CommandException("commands.entitydata.tagError", var8.getMessage());
				}

				nbtCompound3.remove("UUIDMost");
				nbtCompound3.remove("UUIDLeast");
				nbtCompound.copyFrom(nbtCompound3);
				if (nbtCompound.equals(nbtCompound2)) {
					throw new CommandException("commands.entitydata.failed", nbtCompound.toString());
				} else {
					entity.fromNbt(nbtCompound);
					run(source, this, "commands.entitydata.success", new Object[]{nbtCompound.toString()});
				}
			}
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
