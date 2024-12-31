package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EggItem extends Item {
	public EggItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!player.abilities.creativeMode) {
			itemStack.decrement(1);
		}

		world.playSound(null, player.x, player.y, player.z, Sounds.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));
		if (!world.isClient) {
			EggEntity eggEntity = new EggEntity(world, player);
			eggEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 1.0F);
			world.method_3686(eggEntity);
		}

		player.method_15932(Stats.USED.method_21429(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
