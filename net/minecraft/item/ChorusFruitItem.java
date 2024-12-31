package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends FoodItem {
	public ChorusFruitItem(int i, float f) {
		super(i, f, false);
	}

	@Nullable
	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		ItemStack itemStack = super.method_3367(stack, world, entity);
		if (!world.isClient) {
			double d = entity.x;
			double e = entity.y;
			double f = entity.z;

			for (int i = 0; i < 16; i++) {
				double g = entity.x + (entity.getRandom().nextDouble() - 0.5) * 16.0;
				double h = MathHelper.clamp(entity.y + (double)(entity.getRandom().nextInt(16) - 8), 0.0, (double)(world.getEffectiveHeight() - 1));
				double j = entity.z + (entity.getRandom().nextDouble() - 0.5) * 16.0;
				if (entity.hasMount()) {
					entity.stopRiding();
				}

				if (entity.method_13071(g, h, j)) {
					world.playSound(null, d, e, f, Sounds.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
					entity.playSound(Sounds.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
					break;
				}
			}

			if (entity instanceof PlayerEntity) {
				((PlayerEntity)entity).getItemCooldownManager().method_11384(this, 20);
			}
		}

		return itemStack;
	}
}
