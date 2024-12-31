package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class GiveCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "give";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.give.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.give.usage");
		} else {
			PlayerEntity playerEntity = getPlayer(source, args[0]);
			Item item = getItem(source, args[1]);
			int i = args.length >= 3 ? parseClampedInt(args[2], 1, 64) : 1;
			int j = args.length >= 4 ? parseInt(args[3]) : 0;
			ItemStack itemStack = new ItemStack(item, i, j);
			if (args.length >= 5) {
				String string = method_4635(source, args, 4).asUnformattedString();

				try {
					itemStack.setNbt(StringNbtReader.parse(string));
				} catch (NbtException var10) {
					throw new CommandException("commands.give.tagError", var10.getMessage());
				}
			}

			boolean bl = playerEntity.inventory.insertStack(itemStack);
			if (bl) {
				playerEntity.world
					.playSound((Entity)playerEntity, "random.pop", 0.2F, ((playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
				playerEntity.playerScreenHandler.sendContentUpdates();
			}

			if (bl && itemStack.count <= 0) {
				itemStack.count = 1;
				source.setStat(CommandStats.Type.AFFECTED_ITEMS, i);
				ItemEntity itemEntity2 = playerEntity.dropItem(itemStack, false);
				if (itemEntity2 != null) {
					itemEntity2.setDespawnImmediately();
				}
			} else {
				source.setStat(CommandStats.Type.AFFECTED_ITEMS, i - itemStack.count);
				ItemEntity itemEntity = playerEntity.dropItem(itemStack, false);
				if (itemEntity != null) {
					itemEntity.resetPickupDelay();
					itemEntity.setOwner(playerEntity.getTranslationKey());
				}
			}

			run(source, this, "commands.give.success", new Object[]{itemStack.toHoverableText(), i, playerEntity.getTranslationKey()});
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, this.method_3786());
		} else {
			return args.length == 2 ? method_10708(args, Item.REGISTRY.keySet()) : null;
		}
	}

	protected String[] method_3786() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
