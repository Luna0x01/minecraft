package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EggItem extends Item {
	public EggItem() {
		this.maxCount = 16;
		this.setItemGroup(ItemGroup.MATERIALS);
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		if (!playerEntity.abilities.creativeMode) {
			itemStack.count--;
		}

		world.playSound(
			null, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ENTITY_EGG_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			EggEntity eggEntity = new EggEntity(world, playerEntity);
			eggEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(eggEntity);
		}

		playerEntity.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
