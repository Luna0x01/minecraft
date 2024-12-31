package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FishingRodItem extends Item {
	public FishingRodItem(Item.Settings settings) {
		super(settings);
		this.addProperty(new Identifier("cast"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				boolean bl = livingEntity.getMainHandStack() == itemStack;
				boolean bl2 = livingEntity.getOffHandStack() == itemStack;
				if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
					bl2 = false;
				}

				return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).fishHook != null ? 1.0F : 0.0F;
			}
		});
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (player.fishHook != null) {
			int i = player.fishHook.method_15844(itemStack);
			itemStack.damage(i, player);
			player.swingHand(hand);
			world.playSound(
				null, player.x, player.y, player.z, Sounds.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
			);
		} else {
			world.playSound(
				null, player.x, player.y, player.z, Sounds.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
			);
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

				world.method_3686(fishingBobberEntity);
			}

			player.swingHand(hand);
			player.method_15932(Stats.USED.method_21429(this));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
