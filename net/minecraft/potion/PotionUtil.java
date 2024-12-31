package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_3459;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class PotionUtil {
	public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
		return getPotionEffects(stack.getNbt());
	}

	public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> custom) {
		List<StatusEffectInstance> list = Lists.newArrayList();
		list.addAll(potion.getEffects());
		list.addAll(custom);
		return list;
	}

	public static List<StatusEffectInstance> getPotionEffects(@Nullable NbtCompound nbt) {
		List<StatusEffectInstance> list = Lists.newArrayList();
		list.addAll(getPotion(nbt).getEffects());
		getCustomPotionEffects(nbt, list);
		return list;
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
		return getCustomPotionEffects(stack.getNbt());
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable NbtCompound nbt) {
		List<StatusEffectInstance> list = Lists.newArrayList();
		getCustomPotionEffects(nbt, list);
		return list;
	}

	public static void getCustomPotionEffects(@Nullable NbtCompound nbt, List<StatusEffectInstance> list) {
		if (nbt != null && nbt.contains("CustomPotionEffects", 9)) {
			NbtList nbtList = nbt.getList("CustomPotionEffects", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
				if (statusEffectInstance != null) {
					list.add(statusEffectInstance);
				}
			}
		}
	}

	public static int getColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", 99)) {
			return nbtCompound.getInt("CustomPotionColor");
		} else {
			return getPotion(stack) == Potions.EMPTY ? 16253176 : getColor(getPotionEffects(stack));
		}
	}

	public static int getColor(Potion potion) {
		return potion == Potions.EMPTY ? 16253176 : getColor(potion.getEffects());
	}

	public static int getColor(Collection<StatusEffectInstance> effects) {
		int i = 3694022;
		if (effects.isEmpty()) {
			return 3694022;
		} else {
			float f = 0.0F;
			float g = 0.0F;
			float h = 0.0F;
			int j = 0;

			for (StatusEffectInstance statusEffectInstance : effects) {
				if (statusEffectInstance.shouldShowParticles()) {
					int k = statusEffectInstance.getStatusEffect().getColor();
					int l = statusEffectInstance.getAmplifier() + 1;
					f += (float)(l * (k >> 16 & 0xFF)) / 255.0F;
					g += (float)(l * (k >> 8 & 0xFF)) / 255.0F;
					h += (float)(l * (k >> 0 & 0xFF)) / 255.0F;
					j += l;
				}
			}

			if (j == 0) {
				return 0;
			} else {
				f = f / (float)j * 255.0F;
				g = g / (float)j * 255.0F;
				h = h / (float)j * 255.0F;
				return (int)f << 16 | (int)g << 8 | (int)h;
			}
		}
	}

	public static Potion getPotion(ItemStack stack) {
		return getPotion(stack.getNbt());
	}

	public static Potion getPotion(@Nullable NbtCompound nbt) {
		return nbt == null ? Potions.EMPTY : Potion.get(nbt.getString("Potion"));
	}

	public static ItemStack setPotion(ItemStack stack, Potion potion) {
		Identifier identifier = Registry.POTION.getId(potion);
		if (potion == Potions.EMPTY) {
			stack.removeNbt("Potion");
		} else {
			stack.getOrCreateNbt().putString("Potion", identifier.toString());
		}

		return stack;
	}

	public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
		if (effects.isEmpty()) {
			return stack;
		} else {
			NbtCompound nbtCompound = stack.getOrCreateNbt();
			NbtList nbtList = nbtCompound.getList("CustomPotionEffects", 9);

			for (StatusEffectInstance statusEffectInstance : effects) {
				nbtList.add((NbtElement)statusEffectInstance.toNbt(new NbtCompound()));
			}

			nbtCompound.put("CustomPotionEffects", nbtList);
			return stack;
		}
	}

	public static void buildTooltip(ItemStack stack, List<Text> list, float f) {
		List<StatusEffectInstance> list2 = getPotionEffects(stack);
		List<Pair<String, AttributeModifier>> list3 = Lists.newArrayList();
		if (list2.isEmpty()) {
			list.add(new TranslatableText("effect.none").formatted(Formatting.GRAY));
		} else {
			for (StatusEffectInstance statusEffectInstance : list2) {
				Text text = new TranslatableText(statusEffectInstance.getTranslationKey());
				StatusEffect statusEffect = statusEffectInstance.getStatusEffect();
				Map<EntityAttribute, AttributeModifier> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Entry<EntityAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
						AttributeModifier attributeModifier2 = new AttributeModifier(
							attributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), attributeModifier), attributeModifier.getOperation()
						);
						list3.add(new Pair<>(((EntityAttribute)entry.getKey()).getId(), attributeModifier2));
					}
				}

				if (statusEffectInstance.getAmplifier() > 0) {
					text.append(" ").append(new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier()));
				}

				if (statusEffectInstance.getDuration() > 20) {
					text.append(" (").append(class_3459.method_15553(statusEffectInstance, f)).append(")");
				}

				list.add(text.formatted(statusEffect.isNegative() ? Formatting.RED : Formatting.BLUE));
			}
		}

		if (!list3.isEmpty()) {
			list.add(new LiteralText(""));
			list.add(new TranslatableText("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

			for (Pair<String, AttributeModifier> pair : list3) {
				AttributeModifier attributeModifier3 = pair.getRight();
				double d = attributeModifier3.getAmount();
				double g;
				if (attributeModifier3.getOperation() != 1 && attributeModifier3.getOperation() != 2) {
					g = attributeModifier3.getAmount();
				} else {
					g = attributeModifier3.getAmount() * 100.0;
				}

				if (d > 0.0) {
					list.add(
						new TranslatableText(
								"attribute.modifier.plus." + attributeModifier3.getOperation(),
								ItemStack.MODIFIER_FORMAT.format(g),
								new TranslatableText("attribute.name." + pair.getLeft())
							)
							.formatted(Formatting.BLUE)
					);
				} else if (d < 0.0) {
					g *= -1.0;
					list.add(
						new TranslatableText(
								"attribute.modifier.take." + attributeModifier3.getOperation(),
								ItemStack.MODIFIER_FORMAT.format(g),
								new TranslatableText("attribute.name." + pair.getLeft())
							)
							.formatted(Formatting.RED)
					);
				}
			}
		}
	}
}
