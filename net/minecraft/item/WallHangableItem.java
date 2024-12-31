package net.minecraft.item;

import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction == Direction.DOWN) {
			return false;
		} else if (direction == Direction.UP) {
			return false;
		} else {
			BlockPos blockPos = pos.offset(direction);
			if (!player.canModify(blockPos, direction, itemStack)) {
				return false;
			} else {
				AbstractDecorationEntity abstractDecorationEntity = this.createEntity(world, blockPos, direction);
				if (abstractDecorationEntity != null && abstractDecorationEntity.isPosValid()) {
					if (!world.isClient) {
						world.spawnEntity(abstractDecorationEntity);
					}

					itemStack.count--;
				}

				return true;
			}
		}
	}

	private AbstractDecorationEntity createEntity(World world, BlockPos pos, Direction dir) {
		if (this.decorationClass == PaintingEntity.class) {
			return new PaintingEntity(world, pos, dir);
		} else {
			return this.decorationClass == ItemFrameEntity.class ? new ItemFrameEntity(world, pos, dir) : null;
		}
	}
}
