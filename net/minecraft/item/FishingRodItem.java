package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FishingRodItem extends Item implements Vanishable {
	public FishingRodItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (user.fishHook != null) {
			if (!world.isClient) {
				int i = user.fishHook.use(itemStack);
				itemStack.damage(i, user, p -> p.sendToolBreakStatus(hand));
			}

			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE,
				SoundCategory.NEUTRAL,
				1.0F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
			);
			world.emitGameEvent(user, GameEvent.FISHING_ROD_REEL_IN, user);
		} else {
			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ENTITY_FISHING_BOBBER_THROW,
				SoundCategory.NEUTRAL,
				0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
			);
			if (!world.isClient) {
				int j = EnchantmentHelper.getLure(itemStack);
				int k = EnchantmentHelper.getLuckOfTheSea(itemStack);
				world.spawnEntity(new FishingBobberEntity(user, world, k, j));
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			world.emitGameEvent(user, GameEvent.FISHING_ROD_CAST, user);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
