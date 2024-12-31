package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FishingRodItem extends Item {
	public FishingRodItem() {
		this.setMaxDamage(64);
		this.setMaxCount(1);
		this.setItemGroup(ItemGroup.TOOLS);
		this.addProperty(new Identifier("cast"), new ItemPropertyGetter() {
			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				if (entity == null) {
					return 0.0F;
				} else {
					boolean bl = entity.getMainHandStack() == stack;
					boolean bl2 = entity.getOffHandStack() == stack;
					if (entity.getMainHandStack().getItem() instanceof FishingRodItem) {
						bl2 = false;
					}

					return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishHook != null ? 1.0F : 0.0F;
				}
			}
		});
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	@Override
	public boolean shouldRotate() {
		return true;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (player.fishHook != null) {
			int i = player.fishHook.retract();
			itemStack.damage(i, player);
			player.swingHand(hand);
		} else {
			world.playSound(null, player.x, player.y, player.z, Sounds.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
			if (!world.isClient) {
				FishingBobberEntity fishingBobberEntity = new FishingBobberEntity(world, player);
				int j = EnchantmentHelper.getLure(itemStack);
				if (j > 0) {
					fishingBobberEntity.setLure(j);
				}

				int k = EnchantmentHelper.getLuckOfTheSea(itemStack);
				if (k > 0) {
					fishingBobberEntity.setLuckOfTheSea(k);
				}

				world.spawnEntity(fishingBobberEntity);
			}

			player.swingHand(hand);
			player.incrementStat(Stats.used(this));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
