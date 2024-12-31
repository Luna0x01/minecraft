package net.minecraft.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BowItem extends Item {
	public static final String[] animationFrames = new String[]{"pulling_0", "pulling_1", "pulling_2"};

	public BowItem() {
		this.maxCount = 1;
		this.setMaxDamage(384);
		this.setItemGroup(ItemGroup.COMBAT);
	}

	@Override
	public void onUseStopped(ItemStack stack, World world, PlayerEntity player, int remainingTicks) {
		boolean bl = player.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantment.INFINITY.id, stack) > 0;
		if (bl || player.inventory.containsItem(Items.ARROW)) {
			int i = this.getMaxUseTime(stack) - remainingTicks;
			float f = (float)i / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;
			if ((double)f < 0.1) {
				return;
			}

			if (f > 1.0F) {
				f = 1.0F;
			}

			AbstractArrowEntity abstractArrowEntity = new AbstractArrowEntity(world, player, f * 2.0F);
			if (f == 1.0F) {
				abstractArrowEntity.setCritical(true);
			}

			int j = EnchantmentHelper.getLevel(Enchantment.POWER.id, stack);
			if (j > 0) {
				abstractArrowEntity.setDamage(abstractArrowEntity.getDamage() + (double)j * 0.5 + 0.5);
			}

			int k = EnchantmentHelper.getLevel(Enchantment.PUNCH.id, stack);
			if (k > 0) {
				abstractArrowEntity.setPunch(k);
			}

			if (EnchantmentHelper.getLevel(Enchantment.FLAME.id, stack) > 0) {
				abstractArrowEntity.setOnFireFor(100);
			}

			stack.damage(1, player);
			world.playSound((Entity)player, "random.bow", 1.0F, 1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
			if (bl) {
				abstractArrowEntity.pickup = 2;
			} else {
				player.inventory.useItem(Items.ARROW);
			}

			player.incrementStat(Stats.USED[Item.getRawId(this)]);
			if (!world.isClient) {
				world.spawnEntity(abstractArrowEntity);
			}
		}
	}

	@Override
	public ItemStack onFinishUse(ItemStack stack, World world, PlayerEntity player) {
		return stack;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		if (player.abilities.creativeMode || player.inventory.containsItem(Items.ARROW)) {
			player.setUseItem(stack, this.getMaxUseTime(stack));
		}

		return stack;
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
