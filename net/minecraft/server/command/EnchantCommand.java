package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.enchant.usage");
		} else {
			LivingEntity livingEntity = method_12702(minecraftServer, commandSource, args[0], LivingEntity.class);
			commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, 0);

			Enchantment enchantment;
			try {
				enchantment = Enchantment.byIndex(parseClampedInt(args[1], 0));
			} catch (InvalidNumberException var12) {
				enchantment = Enchantment.getByName(args[1]);
			}

			if (enchantment == null) {
				throw new InvalidNumberException("commands.enchant.notFound", Enchantment.getId(enchantment));
			} else {
				int i = 1;
				ItemStack itemStack = livingEntity.getMainHandStack();
				if (itemStack == null) {
					throw new CommandException("commands.enchant.noItem");
				} else if (!enchantment.isAcceptableItem(itemStack)) {
					throw new CommandException("commands.enchant.cantEnchant");
				} else {
					if (args.length >= 3) {
						i = parseClampedInt(args[2], enchantment.getMinimumLevel(), enchantment.getMaximumLevel());
					}

					if (itemStack.hasNbt()) {
						NbtList nbtList = itemStack.getEnchantments();
						if (nbtList != null) {
							for (int j = 0; j < nbtList.size(); j++) {
								int k = nbtList.getCompound(j).getShort("id");
								if (Enchantment.byIndex(k) != null) {
									Enchantment enchantment2 = Enchantment.byIndex(k);
									if (!enchantment.differs(enchantment2)) {
										throw new CommandException(
											"commands.enchant.cantCombine", enchantment.getTranslatedName(i), enchantment2.getTranslatedName(nbtList.getCompound(j).getShort("lvl"))
										);
									}
								}
							}
						}
					}

					itemStack.addEnchantment(enchantment, i);
					run(commandSource, this, "commands.enchant.success", new Object[0]);
					commandSource.setStat(CommandStats.Type.AFFECTED_ITEMS, 1);
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else {
			return strings.length == 2 ? method_10708(strings, Enchantment.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
