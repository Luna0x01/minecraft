package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		ServerPlayerEntity serverPlayerEntity = args.length == 0 ? getAsPlayer(source) : getPlayer(source, args[0]);
		Item item = args.length >= 2 ? getItem(source, args[1]) : null;
		int i = args.length >= 3 ? parseClampedInt(args[2], -1) : -1;
		int j = args.length >= 4 ? parseClampedInt(args[3], -1) : -1;
		NbtCompound nbtCompound = null;
		if (args.length >= 5) {
			try {
				nbtCompound = StringNbtReader.parse(method_10706(args, 4));
			} catch (NbtException var9) {
				throw new CommandException("commands.clear.tagError", var9.getMessage());
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

			source.setStat(CommandStats.Type.AFFECTED_ITEMS, k);
			if (k == 0) {
				throw new CommandException("commands.clear.failure", serverPlayerEntity.getTranslationKey());
			} else {
				if (j == 0) {
					source.sendMessage(new TranslatableText("commands.clear.testing", serverPlayerEntity.getTranslationKey(), k));
				} else {
					run(source, this, "commands.clear.success", new Object[]{serverPlayerEntity.getTranslationKey(), k});
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, this.method_4402());
		} else {
			return args.length == 2 ? method_10708(args, Item.REGISTRY.keySet()) : null;
		}
	}

	protected String[] method_4402() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
