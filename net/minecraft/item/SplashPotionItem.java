package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashPotionItem extends PotionItem {
	public SplashPotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		ItemStack itemStack2 = player.abilities.creativeMode ? itemStack.copy() : itemStack.split(1);
		world.playSound(null, player.x, player.y, player.z, Sounds.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
		if (!world.isClient) {
			PotionEntity potionEntity = new PotionEntity(world, player, itemStack2);
			potionEntity.setProperties(player, player.pitch, player.yaw, -20.0F, 0.5F, 1.0F);
			world.method_3686(potionEntity);
		}

		player.method_15932(Stats.USED.method_21429(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
