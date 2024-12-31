package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class EnchantCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "enchant";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.enchant.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.enchant.usage");
		} else {
			PlayerEntity playerEntity = getPlayer(source, args[0]);
			source.setStat(CommandStats.Type.AFFECTED_ITEMS, 0);

			int i;
			try {
				i = parseClampedInt(args[1], 0);
			} catch (InvalidNumberException var12) {
				Enchantment enchantment = Enchantment.getByName(args[1]);
				if (enchantment == null) {
					throw var12;
				}

				i = enchantment.id;
			}

			int k = 1;
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (itemStack == null) {
				throw new CommandException("commands.enchant.noItem");
			} else {
				Enchantment enchantment2 = Enchantment.byRawId(i);
				if (enchantment2 == null) {
					throw new InvalidNumberException("commands.enchant.notFound", i);
				} else if (!enchantment2.isAcceptableItem(itemStack)) {
					throw new CommandException("commands.enchant.cantEnchant");
				} else {
					if (args.length >= 3) {
						k = parseClampedInt(args[2], enchantment2.getMinimumLevel(), enchantment2.getMaximumLevel());
					}

					if (itemStack.hasNbt()) {
						NbtList nbtList = itemStack.getEnchantments();
						if (nbtList != null) {
							for (int l = 0; l < nbtList.size(); l++) {
								int m = nbtList.getCompound(l).getShort("id");
								if (Enchantment.byRawId(m) != null) {
									Enchantment enchantment3 = Enchantment.byRawId(m);
									if (!enchantment3.differs(enchantment2)) {
										throw new CommandException(
											"commands.enchant.cantCombine", enchantment2.getTranslatedName(k), enchantment3.getTranslatedName(nbtList.getCompound(l).getShort("lvl"))
										);
									}
								}
							}
						}
					}

					itemStack.addEnchantment(enchantment2, k);
					run(source, this, "commands.enchant.success", new Object[0]);
					source.setStat(CommandStats.Type.AFFECTED_ITEMS, 1);
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, this.method_4113());
		} else {
			return args.length == 2 ? method_10708(args, Enchantment.getSet()) : null;
		}
	}

	protected String[] method_4113() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
