package net.minecraft.server.command;

import java.util.UUID;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;

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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.entitydata.usage");
		} else {
			Entity entity = method_10711(minecraftServer, commandSource, args[0]);
			if (entity instanceof PlayerEntity) {
				throw new CommandException("commands.entitydata.noPlayers", entity.getName());
			} else {
				NbtCompound nbtCompound = getEntityNbt(entity);
				NbtCompound nbtCompound2 = nbtCompound.copy();

				NbtCompound nbtCompound3;
				try {
					nbtCompound3 = StringNbtReader.parse(method_10706(args, 1));
				} catch (NbtException var9) {
					throw new CommandException("commands.entitydata.tagError", var9.getMessage());
				}

				UUID uUID = entity.getUuid();
				nbtCompound.copyFrom(nbtCompound3);
				entity.setUuid(uUID);
				if (nbtCompound.equals(nbtCompound2)) {
					throw new CommandException("commands.entitydata.failed", nbtCompound.toString());
				} else {
					entity.fromNbt(nbtCompound);
					run(commandSource, this, "commands.entitydata.success", new Object[]{nbtCompound.toString()});
				}
			}
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
