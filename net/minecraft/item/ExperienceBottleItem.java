package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ExperienceBottleItem extends Item {
	public ExperienceBottleItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
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
			Sounds.ENTITY_EXPERIENCE_BOTTLE_THROW,
			SoundCategory.NEUTRAL,
			0.5F,
			0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ExperienceBottleEntity experienceBottleEntity = new ExperienceBottleEntity(world, playerEntity);
			experienceBottleEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, -20.0F, 0.7F, 1.0F);
			world.spawnEntity(experienceBottleEntity);
		}

		playerEntity.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
