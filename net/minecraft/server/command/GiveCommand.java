package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.Sounds;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.give.usage");
		} else {
			PlayerEntity playerEntity = method_4639(minecraftServer, commandSource, args[0]);
			Item item = getItem(commandSource, args[1]);
			int i = args.length >= 3 ? parseClampedInt(args[2], 1, 64) : 1;
			int j = args.length >= 4 ? parseInt(args[3]) : 0;
			ItemStack itemStack = new ItemStack(item, i, j);
			if (args.length >= 5) {
				String string = method_4635(commandSource, args, 4).asUnformattedString();

				try {
					itemStack.setNbt(StringNbtReader.parse(string));
				} catch (NbtException var11) {
					throw new CommandException("commands.give.tagError", var11.getMessage());
				}
			}

			boolean bl = playerEntity.inventory.insertStack(itemStack);
			if (bl) {
				playerEntity.world
					.playSound(
						null,
						playerEntity.x,
						playerEntity.y,
						playerEntity.z,
						Sounds.ENTITY_ITEM_PICKUP,
						SoundCategory.PLAYERS,
						0.2F,
						((playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
					);
				playerEntity.playerScreenHandler.sendContentUpdates();
			}

			if (bl && itemStack.isEmpty()) {
				itemStack.setCount(1);
				commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, i);
				ItemEntity itemEntity2 = playerEntity.dropItem(itemStack, false);
				if (itemEntity2 != null) {
					itemEntity2.setDespawnImmediately();
				}
			} else {
				commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, i - itemStack.getCount());
				ItemEntity itemEntity = playerEntity.dropItem(itemStack, false);
				if (itemEntity != null) {
					itemEntity.resetPickupDelay();
					itemEntity.setOwner(playerEntity.getTranslationKey());
				}
			}

			run(commandSource, this, "commands.give.success", new Object[]{itemStack.toHoverableText(), i, playerEntity.getTranslationKey()});
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
