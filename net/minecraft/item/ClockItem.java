package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ClockItem extends Item {
	public ClockItem() {
		this.addProperty(new Identifier("time"), new ItemPropertyGetter() {
			double field_12286;
			double field_12287;
			long field_12288;

			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				boolean bl = entity != null;
				Entity entity2 = (Entity)(bl ? entity : stack.getItemFrame());
				if (world == null && entity2 != null) {
					world = entity2.world;
				}

				if (world == null) {
					return 0.0F;
				} else {
					double d;
					if (world.dimension.canPlayersSleep()) {
						d = (double)world.getSkyAngle(1.0F);
					} else {
						d = Math.random();
					}

					d = this.method_11366(world, d);
					return (float)d;
				}
			}

			private double method_11366(World world, double d) {
				if (world.getLastUpdateTime() != this.field_12288) {
					this.field_12288 = world.getLastUpdateTime();
					double e = d - this.field_12286;
					e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
					this.field_12287 += e * 0.1;
					this.field_12287 *= 0.9;
					this.field_12286 = MathHelper.floorMod(this.field_12286 + this.field_12287, 1.0);
				}

				return this.field_12286;
			}
		});
	}
}
