package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.effect.usage");
		} else {
			LivingEntity livingEntity = method_12702(minecraftServer, commandSource, args[0], LivingEntity.class);
			if ("clear".equals(args[1])) {
				if (livingEntity.getStatusEffectInstances().isEmpty()) {
					throw new CommandException("commands.effect.failure.notActive.all", livingEntity.getTranslationKey());
				} else {
					livingEntity.clearStatusEffects();
					run(commandSource, this, "commands.effect.success.removed.all", new Object[]{livingEntity.getTranslationKey()});
				}
			} else {
				StatusEffect statusEffect;
				try {
					statusEffect = StatusEffect.byIndex(parseClampedInt(args[1], 1));
				} catch (InvalidNumberException var11) {
					statusEffect = StatusEffect.get(args[1]);
				}

				if (statusEffect == null) {
					throw new InvalidNumberException("commands.effect.notFound", args[1]);
				} else {
					int i = 600;
					int j = 30;
					int k = 0;
					if (args.length >= 3) {
						j = parseClampedInt(args[2], 0, 1000000);
						if (statusEffect.isInstant()) {
							i = j;
						} else {
							i = j * 20;
						}
					} else if (statusEffect.isInstant()) {
						i = 1;
					}

					if (args.length >= 4) {
						k = parseClampedInt(args[3], 0, 255);
					}

					boolean bl = true;
					if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
						bl = false;
					}

					if (j > 0) {
						StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect, i, k, false, bl);
						livingEntity.addStatusEffect(statusEffectInstance);
						run(
							commandSource,
							this,
							"commands.effect.success",
							new Object[]{
								new TranslatableText(statusEffectInstance.getTranslationKey()), StatusEffect.getIndex(statusEffect), k, livingEntity.getTranslationKey(), j
							}
						);
					} else if (livingEntity.hasStatusEffect(statusEffect)) {
						livingEntity.removeStatusEffect(statusEffect);
						run(
							commandSource,
							this,
							"commands.effect.success.removed",
							new Object[]{new TranslatableText(statusEffect.getTranslationKey()), livingEntity.getTranslationKey()}
						);
					} else {
						throw new CommandException("commands.effect.failure.notActive", new TranslatableText(statusEffect.getTranslationKey()), livingEntity.getTranslationKey());
					}
				}
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, server.getPlayerNames());
		} else if (strings.length == 2) {
			return method_10708(strings, StatusEffect.REGISTRY.getKeySet());
		} else {
			return strings.length == 5 ? method_2894(strings, new String[]{"true", "false"}) : Collections.emptyList();
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 0;
	}
}
