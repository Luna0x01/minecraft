package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
	public ArmorStandItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		Direction direction = itemUsageContext.method_16151();
		if (direction == Direction.DOWN) {
			return ActionResult.FAIL;
		} else {
			World world = itemUsageContext.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			BlockPos blockPos2 = blockPos.up();
			if (itemPlacementContext.method_16018() && world.getBlockState(blockPos2).canReplace(itemPlacementContext)) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				List<Entity> list = world.getEntities(null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
				if (!list.isEmpty()) {
					return ActionResult.FAIL;
				} else {
					ItemStack itemStack = itemUsageContext.getItemStack();
					if (!world.isClient) {
						world.method_8553(blockPos);
						world.method_8553(blockPos2);
						ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
						float g = (float)MathHelper.floor((MathHelper.wrapDegrees(itemUsageContext.method_16147() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
						armorStandEntity.refreshPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
						this.place(armorStandEntity, world.random);
						EntityType.method_15618(world, itemUsageContext.getPlayer(), armorStandEntity, itemStack.getNbt());
						world.method_3686(armorStandEntity);
						world.playSound(null, armorStandEntity.x, armorStandEntity.y, armorStandEntity.z, Sounds.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
					}

					itemStack.decrement(1);
					return ActionResult.SUCCESS;
				}
			} else {
				return ActionResult.FAIL;
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
