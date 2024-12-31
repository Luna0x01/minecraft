package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WallHangableItem extends Item {
	private final Class<? extends AbstractDecorationEntity> decorationClass;

	public WallHangableItem(Class<? extends AbstractDecorationEntity> class_) {
		this.decorationClass = class_;
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockPos blockPos2 = blockPos.offset(direction);
		if (direction != Direction.DOWN && direction != Direction.UP && playerEntity.canModify(blockPos2, direction, itemStack)) {
			AbstractDecorationEntity abstractDecorationEntity = this.createEntity(world, blockPos2, direction);
			if (abstractDecorationEntity != null && abstractDecorationEntity.isPosValid()) {
				if (!world.isClient) {
					abstractDecorationEntity.onPlace();
					world.spawnEntity(abstractDecorationEntity);
				}

				itemStack.count--;
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
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
