package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WallHangableItem extends Item {
	private final Class<? extends AbstractDecorationEntity> decorationClass;

	public WallHangableItem(Class<? extends AbstractDecorationEntity> class_, Item.Settings settings) {
		super(settings);
		this.decorationClass = class_;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		BlockPos blockPos = itemUsageContext.getBlockPos();
		Direction direction = itemUsageContext.method_16151();
		BlockPos blockPos2 = blockPos.offset(direction);
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		if (playerEntity != null && !this.method_16066(playerEntity, direction, itemUsageContext.getItemStack(), blockPos2)) {
			return ActionResult.FAIL;
		} else {
			World world = itemUsageContext.getWorld();
			AbstractDecorationEntity abstractDecorationEntity = this.createEntity(world, blockPos2, direction);
			if (abstractDecorationEntity != null && abstractDecorationEntity.isPosValid()) {
				if (!world.isClient) {
					abstractDecorationEntity.onPlace();
					world.method_3686(abstractDecorationEntity);
				}

				itemUsageContext.getItemStack().decrement(1);
			}

			return ActionResult.SUCCESS;
		}
	}

	protected boolean method_16066(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !direction.getAxis().method_19950() && playerEntity.canModify(blockPos, direction, itemStack);
	}

	@Nullable
	private AbstractDecorationEntity createEntity(World world, BlockPos pos, Direction dir) {
		if (this.decorationClass == PaintingEntity.class) {
			return new PaintingEntity(world, pos, dir);
		} else {
			return this.decorationClass == ItemFrameEntity.class ? new ItemFrameEntity(world, pos, dir) : null;
		}
	}
}
