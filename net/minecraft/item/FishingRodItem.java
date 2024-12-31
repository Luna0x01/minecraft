package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
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
					return entity.getMainHandStack() == stack && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishHook != null ? 1.0F : 0.0F;
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
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.fishHook != null) {
			int i = playerEntity.fishHook.retract();
			itemStack.damage(i, playerEntity);
			playerEntity.swingHand(hand);
		} else {
			world.playSound(
				null, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
			);
			if (!world.isClient) {
				world.spawnEntity(new FishingBobberEntity(world, playerEntity));
			}

			playerEntity.swingHand(hand);
			playerEntity.incrementStat(Stats.used(this));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return super.isEnchantable(stack);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
