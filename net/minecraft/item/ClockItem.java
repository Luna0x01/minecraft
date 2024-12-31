package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ClockItem extends Item {
	public ClockItem(Item.Settings settings) {
		super(settings);
		this.addProperty(new Identifier("time"), new ItemPropertyGetter() {
			private double field_12286;
			private double field_12287;
			private long field_12288;

			@Override
			public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
				boolean bl = livingEntity != null;
				Entity entity = (Entity)(bl ? livingEntity : itemStack.getItemFrame());
				if (world == null && entity != null) {
					world = entity.world;
				}

				if (world == null) {
					return 0.0F;
				} else {
					double d;
					if (world.dimension.canPlayersSleep()) {
						d = (double)world.method_16349(1.0F);
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
