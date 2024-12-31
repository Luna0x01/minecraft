package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SnowballItem extends Item {
	public SnowballItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!player.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		world.playSound(null, player.x, player.y, player.z, Sounds.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
		if (!world.isClient) {
			SnowballEntity snowballEntity = new SnowballEntity(world, player);
			snowballEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
			world.method_3686(snowballEntity);
		}

		player.method_15932(Stats.USED.method_21429(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
