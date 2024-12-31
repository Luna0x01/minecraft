package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PotionUtil {
	private static final MutableText field_25817 = new TranslatableText("effect.none").formatted(Formatting.GRAY);

	public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 0: aload 0
		// 1: invokevirtual net/minecraft/item/ItemStack.getTag ()Lnet/minecraft/nbt/CompoundTag;
		// 4: invokestatic net/minecraft/potion/PotionUtil.getPotionEffects (Lnet/minecraft/nbt/CompoundTag;)Ljava/util/List;
		// 7: areturn
	}

	public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> custom) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 00: invokestatic com/google/common/collect/Lists.newArrayList ()Ljava/util/ArrayList;
		// 03: astore 2
		// 04: aload 2
		// 05: aload 0
		// 06: invokevirtual net/minecraft/potion/Potion.getEffects ()Ljava/util/List;
		// 09: invokeinterface java/util/List.addAll (Ljava/util/Collection;)Z 2
		// 0e: pop
		// 0f: aload 2
		// 10: aload 1
		// 11: invokeinterface java/util/List.addAll (Ljava/util/Collection;)Z 2
		// 16: pop
		// 17: aload 2
		// 18: areturn
	}

	public static List<StatusEffectInstance> getPotionEffects(@Nullable CompoundTag tag) {
		List<StatusEffectInstance> list = Lists.newArrayList();
		list.addAll(getPotion(tag).getEffects());
		getCustomPotionEffects(tag, list);
		return list;
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
		return getCustomPotionEffects(stack.getTag());
	}

	public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable CompoundTag tag) {
		List<StatusEffectInstance> list = Lists.newArrayList();
		getCustomPotionEffects(tag, list);
		return list;
	}

	public static void getCustomPotionEffects(@Nullable CompoundTag tag, List<StatusEffectInstance> list) {
		if (tag != null && tag.contains("CustomPotionEffects", 9)) {
			ListTag listTag = tag.getList("CustomPotionEffects", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromTag(compoundTag);
				if (statusEffectInstance != null) {
					list.add(statusEffectInstance);
				}
			}
		}
	}

	public static int getColor(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
			return compoundTag.getInt("CustomPotionColor");
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
					int k = statusEffectInstance.getEffectType().getColor();
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
		return getPotion(stack.getTag());
	}

	public static Potion getPotion(@Nullable CompoundTag compound) {
		return compound == null ? Potions.EMPTY : Potion.byId(compound.getString("Potion"));
	}

	public static ItemStack setPotion(ItemStack stack, Potion potion) {
		Identifier identifier = Registry.POTION.getId(potion);
		if (potion == Potions.EMPTY) {
			stack.removeSubTag("Potion");
		} else {
			stack.getOrCreateTag().putString("Potion", identifier.toString());
		}

		return stack;
	}

	public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
		if (effects.isEmpty()) {
			return stack;
		} else {
			CompoundTag compoundTag = stack.getOrCreateTag();
			ListTag listTag = compoundTag.getList("CustomPotionEffects", 9);

			for (StatusEffectInstance statusEffectInstance : effects) {
				listTag.add(statusEffectInstance.toTag(new CompoundTag()));
			}

			compoundTag.put("CustomPotionEffects", listTag);
			return stack;
		}
	}

	public static void buildTooltip(ItemStack stack, List<Text> list, float f) {
		List<StatusEffectInstance> list2 = getPotionEffects(stack);
		List<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();
		if (list2.isEmpty()) {
			list.add(field_25817);
		} else {
			for (StatusEffectInstance statusEffectInstance : list2) {
				MutableText mutableText = new TranslatableText(statusEffectInstance.getTranslationKey());
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
						EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
						EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(
							entityAttributeModifier.getName(),
							statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier),
							entityAttributeModifier.getOperation()
						);
						list3.add(new Pair(entry.getKey(), entityAttributeModifier2));
					}
				}

				if (statusEffectInstance.getAmplifier() > 0) {
					mutableText = new TranslatableText("potion.withAmplifier", mutableText, new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier()));
				}

				if (statusEffectInstance.getDuration() > 20) {
					mutableText = new TranslatableText("potion.withDuration", mutableText, StatusEffectUtil.durationToString(statusEffectInstance, f));
				}

				list.add(mutableText.formatted(statusEffect.getType().getFormatting()));
			}
		}

		if (!list3.isEmpty()) {
			list.add(LiteralText.EMPTY);
			list.add(new TranslatableText("potion.whenDrank").formatted(Formatting.DARK_PURPLE));

			for (Pair<EntityAttribute, EntityAttributeModifier> pair : list3) {
				EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getSecond();
				double d = entityAttributeModifier3.getValue();
				double g;
				if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE
					&& entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
					g = entityAttributeModifier3.getValue();
				} else {
					g = entityAttributeModifier3.getValue() * 100.0;
				}

				if (d > 0.0) {
					list.add(
						new TranslatableText(
								"attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(g),
								new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())
							)
							.formatted(Formatting.BLUE)
					);
				} else if (d < 0.0) {
					g *= -1.0;
					list.add(
						new TranslatableText(
								"attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(),
								ItemStack.MODIFIER_FORMAT.format(g),
								new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())
							)
							.formatted(Formatting.RED)
					);
				}
			}
		}
	}
}
