package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CompassItem extends Item {
	public CompassItem() {
		this.addProperty(new Identifier("angle"), new ItemPropertyGetter() {
			double field_12290;
			double field_12291;
			long field_12292;

			@Override
			public float method_11398(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				if (entity == null && !stack.isInItemFrame()) {
					return 0.0F;
				} else {
					boolean bl = entity != null;
					Entity entity2 = (Entity)(bl ? entity : stack.getItemFrame());
					if (world == null) {
						world = entity2.world;
					}

					double f;
					if (world.dimension.canPlayersSleep()) {
						double d = bl ? (double)entity2.yaw : this.method_11369((ItemFrameEntity)entity2);
						d = MathHelper.floorMod(d / 360.0, 1.0);
						double e = this.method_11368(world, entity2) / (float) (Math.PI * 2);
						f = 0.5 - (d - 0.25 - e);
					} else {
						f = Math.random();
					}

					if (bl) {
						f = this.method_11367(world, f);
					}

					return MathHelper.floorMod((float)f, 1.0F);
				}
			}

			private double method_11367(World world, double d) {
				if (world.getLastUpdateTime() != this.field_12292) {
					this.field_12292 = world.getLastUpdateTime();
					double e = d - this.field_12290;
					e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
					this.field_12291 += e * 0.1;
					this.field_12291 *= 0.8;
					this.field_12290 = MathHelper.floorMod(this.field_12290 + this.field_12291, 1.0);
				}

				return this.field_12290;
			}

			private double method_11369(ItemFrameEntity itemFrameEntity) {
				return (double)MathHelper.wrapDegrees(180 + itemFrameEntity.direction.getHorizontal() * 90);
			}

			private double method_11368(World world, Entity entity) {
				BlockPos blockPos = world.getSpawnPos();
				return Math.atan2((double)blockPos.getZ() - entity.z, (double)blockPos.getX() - entity.x);
			}
		});
	}
}
