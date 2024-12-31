package net.minecraft.item;

import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EnderPearlItem extends Item {
	public EnderPearlItem() {
		this.maxCount = 16;
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		if (!playerEntity.abilities.creativeMode) {
			itemStack.count--;
		}

		world.playSound(
			null, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ENTITY_ENDERPEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
		);
		playerEntity.getItemCooldownManager().method_11384(this, 20);
		if (!world.isClient) {
			EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, playerEntity);
			enderPearlEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(enderPearlEntity);
		}

		playerEntity.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}
}
