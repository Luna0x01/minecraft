package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
	public ArmorStandItem() {
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		if (direction == Direction.DOWN) {
			return ActionResult.FAIL;
		} else {
			boolean bl = world.getBlockState(pos).getBlock().method_8638(world, pos);
			BlockPos blockPos = bl ? pos : pos.offset(direction);
			ItemStack itemStack = player.getStackInHand(hand);
			if (!player.canModify(blockPos, direction, itemStack)) {
				return ActionResult.FAIL;
			} else {
				BlockPos blockPos2 = blockPos.up();
				boolean bl2 = !world.isAir(blockPos) && !world.getBlockState(blockPos).getBlock().method_8638(world, blockPos);
				bl2 |= !world.isAir(blockPos2) && !world.getBlockState(blockPos2).getBlock().method_8638(world, blockPos2);
				if (bl2) {
					return ActionResult.FAIL;
				} else {
					double d = (double)blockPos.getX();
					double e = (double)blockPos.getY();
					double f = (double)blockPos.getZ();
					List<Entity> list = world.getEntitiesIn(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
					if (!list.isEmpty()) {
						return ActionResult.FAIL;
					} else {
						if (!world.isClient) {
							world.setAir(blockPos);
							world.setAir(blockPos2);
							ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
							float g = (float)MathHelper.floor((MathHelper.wrapDegrees(player.yaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
							armorStandEntity.refreshPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
							this.place(armorStandEntity, world.random);
							SpawnEggItem.method_11406(world, player, itemStack, armorStandEntity);
							world.spawnEntity(armorStandEntity);
							world.playSound(null, armorStandEntity.x, armorStandEntity.y, armorStandEntity.z, Sounds.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
						}

						itemStack.decrement(1);
						return ActionResult.SUCCESS;
					}
				}
			}
		}
	}

	private void place(ArmorStandEntity armorStand, Random random) {
		EulerAngle eulerAngle = armorStand.getHeadAngle();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
		armorStand.setHeadAngle(eulerAngle2);
		eulerAngle = armorStand.getBodyAngle();
		f = random.nextFloat() * 10.0F - 5.0F;
		eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
		armorStand.setBodyAngle(eulerAngle2);
	}
}
