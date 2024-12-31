package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Formatting;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class PotionItem extends Item {
	private Map<Integer, List<StatusEffectInstance>> STATUS_EFFECTS_1 = Maps.newHashMap();
	private static final Map<List<StatusEffectInstance>, Integer> STATUS_EFFECTS_2 = Maps.newLinkedHashMap();

	public PotionItem() {
		this.setMaxCount(1);
		this.setUnbreakable(true);
		this.setMaxDamage(0);
		this.setItemGroup(ItemGroup.BREWING);
	}

	public List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
		if (stack.hasNbt() && stack.getNbt().contains("CustomPotionEffects", 9)) {
			List<StatusEffectInstance> list2 = Lists.newArrayList();
			NbtList nbtList = stack.getNbt().getList("CustomPotionEffects", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
				if (statusEffectInstance != null) {
					list2.add(statusEffectInstance);
				}
			}

			return list2;
		} else {
			List<StatusEffectInstance> list = (List<StatusEffectInstance>)this.STATUS_EFFECTS_1.get(stack.getData());
			if (list == null) {
				list = StatusEffectStrings.getPotionEffects(stack.getData(), false);
				this.STATUS_EFFECTS_1.put(stack.getData(), list);
			}

			return list;
		}
	}

	public List<StatusEffectInstance> getPotionEffects(int id) {
		List<StatusEffectInstance> list = (List<StatusEffectInstance>)this.STATUS_EFFECTS_1.get(id);
		if (list == null) {
			list = StatusEffectStrings.getPotionEffects(id, false);
			this.STATUS_EFFECTS_1.put(id, list);
		}

		return list;
	}

	@Override
	public ItemStack onFinishUse(ItemStack stack, World world, PlayerEntity player) {
		if (!player.abilities.creativeMode) {
			stack.count--;
		}

		if (!world.isClient) {
			List<StatusEffectInstance> list = this.getCustomPotionEffects(stack);
			if (list != null) {
				for (StatusEffectInstance statusEffectInstance : list) {
					player.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
				}
			}
		}

		player.incrementStat(Stats.USED[Item.getRawId(this)]);
		if (!player.abilities.creativeMode) {
			if (stack.count <= 0) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			player.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
		}

		return stack;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		if (isThrowable(stack.getData())) {
			if (!player.abilities.creativeMode) {
				stack.count--;
			}

			world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
			if (!world.isClient) {
				world.spawnEntity(new PotionEntity(world, player, stack));
			}

			player.incrementStat(Stats.USED[Item.getRawId(this)]);
			return stack;
		} else {
			player.setUseItem(stack, this.getMaxUseTime(stack));
			return stack;
		}
	}

	public static boolean isThrowable(int i) {
		return (i & 16384) != 0;
	}

	public int getColor(int i) {
		return StatusEffectStrings.getColor(i, false);
	}

	@Override
	public int getDisplayColor(ItemStack stack, int color) {
		return color > 0 ? 16777215 : this.getColor(stack.getData());
	}

	public boolean isInstant(int id) {
		List<StatusEffectInstance> list = this.getPotionEffects(id);
		if (list != null && !list.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance : list) {
				if (StatusEffect.STATUS_EFFECTS[statusEffectInstance.getEffectId()].isInstant()) {
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		if (stack.getData() == 0) {
			return CommonI18n.translate("item.emptyPotion.name").trim();
		} else {
			String string = "";
			if (isThrowable(stack.getData())) {
				string = CommonI18n.translate("potion.prefix.grenade").trim() + " ";
			}

			List<StatusEffectInstance> list = Items.POTION.getCustomPotionEffects(stack);
			if (list != null && !list.isEmpty()) {
				String string2 = ((StatusEffectInstance)list.get(0)).getTranslationKey();
				string2 = string2 + ".postfix";
				return string + CommonI18n.translate(string2).trim();
			} else {
				String string3 = StatusEffectStrings.method_3478(stack.getData());
				return CommonI18n.translate(string3).trim() + " " + super.getDisplayName(stack);
			}
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		if (stack.getData() != 0) {
			List<StatusEffectInstance> list = Items.POTION.getCustomPotionEffects(stack);
			Multimap<String, AttributeModifier> multimap = HashMultimap.create();
			if (list != null && !list.isEmpty()) {
				for (StatusEffectInstance statusEffectInstance : list) {
					String string = CommonI18n.translate(statusEffectInstance.getTranslationKey()).trim();
					StatusEffect statusEffect = StatusEffect.STATUS_EFFECTS[statusEffectInstance.getEffectId()];
					Map<EntityAttribute, AttributeModifier> map = statusEffect.getAttributeModifiers();
					if (map != null && map.size() > 0) {
						for (Entry<EntityAttribute, AttributeModifier> entry : map.entrySet()) {
							AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
							AttributeModifier attributeModifier2 = new AttributeModifier(
								attributeModifier.getName(),
								statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), attributeModifier),
								attributeModifier.getOperation()
							);
							multimap.put(((EntityAttribute)entry.getKey()).getId(), attributeModifier2);
						}
					}

					if (statusEffectInstance.getAmplifier() > 0) {
						string = string + " " + CommonI18n.translate("potion.potency." + statusEffectInstance.getAmplifier()).trim();
					}

					if (statusEffectInstance.getDuration() > 20) {
						string = string + " (" + StatusEffect.getFormattedDuration(statusEffectInstance) + ")";
					}

					if (statusEffect.isNegative()) {
						lines.add(Formatting.RED + string);
					} else {
						lines.add(Formatting.GRAY + string);
					}
				}
			} else {
				String string2 = CommonI18n.translate("potion.empty").trim();
				lines.add(Formatting.GRAY + string2);
			}

			if (!multimap.isEmpty()) {
				lines.add("");
				lines.add(Formatting.DARK_PURPLE + CommonI18n.translate("potion.effects.whenDrank"));

				for (Entry<String, AttributeModifier> entry2 : multimap.entries()) {
					AttributeModifier attributeModifier3 = (AttributeModifier)entry2.getValue();
					double d = attributeModifier3.getAmount();
					double f;
					if (attributeModifier3.getOperation() != 1 && attributeModifier3.getOperation() != 2) {
						f = attributeModifier3.getAmount();
					} else {
						f = attributeModifier3.getAmount() * 100.0;
					}

					if (d > 0.0) {
						lines.add(
							Formatting.BLUE
								+ CommonI18n.translate(
									"attribute.modifier.plus." + attributeModifier3.getOperation(),
									ItemStack.MODIFIER_FORMAT.format(f),
									CommonI18n.translate("attribute.name." + (String)entry2.getKey())
								)
						);
					} else if (d < 0.0) {
						f *= -1.0;
						lines.add(
							Formatting.RED
								+ CommonI18n.translate(
									"attribute.modifier.take." + attributeModifier3.getOperation(),
									ItemStack.MODIFIER_FORMAT.format(f),
									CommonI18n.translate("attribute.name." + (String)entry2.getKey())
								)
						);
					}
				}
			}
		}
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		List<StatusEffectInstance> list = this.getCustomPotionEffects(stack);
		return list != null && !list.isEmpty();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		super.appendItemStacks(item, group, list);
		if (STATUS_EFFECTS_2.isEmpty()) {
			for (int i = 0; i <= 15; i++) {
				for (int j = 0; j <= 1; j++) {
					int k;
					if (j == 0) {
						k = i | 8192;
					} else {
						k = i | 16384;
					}

					for (int l = 0; l <= 2; l++) {
						int m = k;
						if (l != 0) {
							if (l == 1) {
								m = k | 32;
							} else if (l == 2) {
								m = k | 64;
							}
						}

						List<StatusEffectInstance> list2 = StatusEffectStrings.getPotionEffects(m, false);
						if (list2 != null && !list2.isEmpty()) {
							STATUS_EFFECTS_2.put(list2, m);
						}
					}
				}
			}
		}

		for (int n : STATUS_EFFECTS_2.values()) {
			list.add(new ItemStack(item, 1, n));
		}
	}
}
