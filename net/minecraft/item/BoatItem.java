package net.minecraft.item;

import java.util.List;
import net.minecraft.class_4079;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
	private final BoatEntity.Type field_12282;

	public BoatItem(BoatEntity.Type type, Item.Settings settings) {
		super(settings);
		this.field_12282 = type;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		float f = 1.0F;
		float g = player.prevPitch + (player.pitch - player.prevPitch) * 1.0F;
		float h = player.prevYaw + (player.yaw - player.prevYaw) * 1.0F;
		double d = player.prevX + (player.x - player.prevX) * 1.0;
		double e = player.prevY + (player.y - player.prevY) * 1.0 + (double)player.getEyeHeight();
		double i = player.prevZ + (player.z - player.prevZ) * 1.0;
		Vec3d vec3d = new Vec3d(d, e, i);
		float j = MathHelper.cos(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = MathHelper.sin(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -MathHelper.cos(-g * (float) (Math.PI / 180.0));
		float m = MathHelper.sin(-g * (float) (Math.PI / 180.0));
		float n = k * l;
		float p = j * l;
		double q = 5.0;
		Vec3d vec3d2 = vec3d.add((double)n * 5.0, (double)m * 5.0, (double)p * 5.0);
		BlockHitResult blockHitResult = world.method_3614(vec3d, vec3d2, class_4079.ALWAYS);
		if (blockHitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			Vec3d vec3d3 = player.getRotationVector(1.0F);
			boolean bl = false;
			List<Entity> list = world.getEntities(player, player.getBoundingBox().stretch(vec3d3.x * 5.0, vec3d3.y * 5.0, vec3d3.z * 5.0).expand(1.0));

			for (int r = 0; r < list.size(); r++) {
				Entity entity = (Entity)list.get(r);
				if (entity.collides()) {
					Box box = entity.getBoundingBox().expand((double)entity.getTargetingMargin());
					if (box.contains(vec3d)) {
						bl = true;
					}
				}
			}

			if (bl) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				Block block = world.getBlockState(blockPos).getBlock();
				BoatEntity boatEntity = new BoatEntity(world, blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
				boatEntity.setBoatType(this.field_12282);
				boatEntity.yaw = player.yaw;
				if (!world.method_16387(boatEntity, boatEntity.getBoundingBox().expand(-0.1))) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				} else {
					if (!world.isClient) {
						world.method_3686(boatEntity);
					}

					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					player.method_15932(Stats.USED.method_21429(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}
}
