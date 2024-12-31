package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.InvalidNumberException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class EffectCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "effect";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.effect.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.effect.usage");
		} else {
			LivingEntity livingEntity = getEntity(source, args[0], LivingEntity.class);
			if (args[1].equals("clear")) {
				if (livingEntity.getStatusEffectInstances().isEmpty()) {
					throw new CommandException("commands.effect.failure.notActive.all", livingEntity.getTranslationKey());
				} else {
					livingEntity.clearStatusEffects();
					run(source, this, "commands.effect.success.removed.all", new Object[]{livingEntity.getTranslationKey()});
				}
			} else {
				int i;
				try {
					i = parseClampedInt(args[1], 1);
				} catch (InvalidNumberException var11) {
					StatusEffect statusEffect = StatusEffect.get(args[1]);
					if (statusEffect == null) {
						throw var11;
					}

					i = statusEffect.id;
				}

				int k = 600;
				int l = 30;
				int m = 0;
				if (i >= 0 && i < StatusEffect.STATUS_EFFECTS.length && StatusEffect.STATUS_EFFECTS[i] != null) {
					StatusEffect statusEffect2 = StatusEffect.STATUS_EFFECTS[i];
					if (args.length >= 3) {
						l = parseClampedInt(args[2], 0, 1000000);
						if (statusEffect2.isInstant()) {
							k = l;
						} else {
							k = l * 20;
						}
					} else if (statusEffect2.isInstant()) {
						k = 1;
					}

					if (args.length >= 4) {
						m = parseClampedInt(args[3], 0, 255);
					}

					boolean bl = true;
					if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
						bl = false;
					}

					if (l > 0) {
						StatusEffectInstance statusEffectInstance = new StatusEffectInstance(i, k, m, false, bl);
						livingEntity.addStatusEffect(statusEffectInstance);
						run(
							source,
							this,
							"commands.effect.success",
							new Object[]{new TranslatableText(statusEffectInstance.getTranslationKey()), i, m, livingEntity.getTranslationKey(), l}
						);
					} else if (livingEntity.hasStatusEffect(i)) {
						livingEntity.method_6149(i);
						run(
							source, this, "commands.effect.success.removed", new Object[]{new TranslatableText(statusEffect2.getTranslationKey()), livingEntity.getTranslationKey()}
						);
					} else {
						throw new CommandException("commands.effect.failure.notActive", new TranslatableText(statusEffect2.getTranslationKey()), livingEntity.getTranslationKey());
					}
				} else {
					throw new InvalidNumberException("commands.effect.notFound", i);
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, this.method_4729());
		} else if (args.length == 2) {
			return method_10708(args, StatusEffect.method_10923());
		} else {
			return args.length == 5 ? method_2894(args, new String[]{"true", "false"}) : null;
		}
	}

	protected String[] method_4729() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
