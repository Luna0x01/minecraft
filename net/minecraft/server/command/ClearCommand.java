package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ClearCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "clear";
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.clear.usage";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		ServerPlayerEntity serverPlayerEntity = args.length == 0 ? getAsPlayer(commandSource) : method_4639(minecraftServer, commandSource, args[0]);
		Item item = args.length >= 2 ? getItem(commandSource, args[1]) : null;
		int i = args.length >= 3 ? parseClampedInt(args[2], -1) : -1;
		int j = args.length >= 4 ? parseClampedInt(args[3], -1) : -1;
		NbtCompound nbtCompound = null;
		if (args.length >= 5) {
			try {
				nbtCompound = StringNbtReader.parse(method_10706(args, 4));
			} catch (NbtException var10) {
				throw new CommandException("commands.clear.tagError", var10.getMessage());
			}
		}

		if (args.length >= 2 && item == null) {
			throw new CommandException("commands.clear.failure", serverPlayerEntity.getTranslationKey());
		} else {
			int k = serverPlayerEntity.inventory.method_11232(item, i, j, nbtCompound);
			serverPlayerEntity.playerScreenHandler.sendContentUpdates();
			if (!serverPlayerEntity.abilities.creativeMode) {
				serverPlayerEntity.method_2158();
			}

			commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, k);
			if (k == 0) {
				throw new CommandException("commands.clear.failure", serverPlayerEntity.getTranslationKey());
			} else {
				if (j == 0) {
					commandSource.sendMessage(new TranslatableText("commands.clear.testing", serverPlayerEntity.getTranslationKey(), k));
				} else {
					run(commandSource, this, "commands.clear.success", new Object[]{serverPlayerEntity.getTranslationKey(), k});
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else {
			return strings.length == 2 ? method_10708(strings, Item.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
