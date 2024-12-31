package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashPotionItem extends PotionItem {
	@Override
	public String getDisplayName(ItemStack stack) {
		return CommonI18n.translate(PotionUtil.getPotion(stack).method_11414("splash_potion.effect."));
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		if (!playerEntity.abilities.creativeMode) {
			itemStack.count--;
		}

		world.playSound(
			null,
			playerEntity.x,
			playerEntity.y,
			playerEntity.z,
			Sounds.ENTITY_SPLASH_POTION_THROW,
			SoundCategory.NEUTRAL,
			0.5F,
			0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			PotionEntity potionEntity = new PotionEntity(world, playerEntity, itemStack);
			potionEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, -20.0F, 0.5F, 1.0F);
			world.spawnEntity(potionEntity);
		}

		playerEntity.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
