package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemPlacementContext extends ItemUsageContext {
	private final BlockPos field_17156;
	protected boolean field_17155 = true;

	public ItemPlacementContext(ItemUsageContext itemUsageContext) {
		this(
			itemUsageContext.getWorld(),
			itemUsageContext.getPlayer(),
			itemUsageContext.getItemStack(),
			itemUsageContext.getBlockPos(),
			itemUsageContext.method_16151(),
			itemUsageContext.method_16152(),
			itemUsageContext.method_16153(),
			itemUsageContext.method_16154()
		);
	}

	protected ItemPlacementContext(
		World world, @Nullable PlayerEntity playerEntity, ItemStack itemStack, BlockPos blockPos, Direction direction, float f, float g, float h
	) {
		super(world, playerEntity, itemStack, blockPos, direction, f, g, h);
		this.field_17156 = this.field_17407.offset(this.field_17404);
		this.field_17155 = this.getWorld().getBlockState(this.field_17407).canReplace(this);
	}

	@Override
	public BlockPos getBlockPos() {
		return this.field_17155 ? this.field_17407 : this.field_17156;
	}

	public boolean method_16018() {
		return this.field_17155 || this.getWorld().getBlockState(this.getBlockPos()).canReplace(this);
	}

	public boolean method_16019() {
		return this.field_17155;
	}

	public Direction method_16020() {
		return Direction.method_19938(this.field_17400)[0];
	}

	public Direction[] method_16021() {
		Direction[] directions = Direction.method_19938(this.field_17400);
		if (this.field_17155) {
			return directions;
		} else {
			int i = 0;

			while (i < directions.length && directions[i] != this.field_17404.getOpposite()) {
				i++;
			}

			if (i > 0) {
				System.arraycopy(directions, 0, directions, 1, i);
				directions[0] = this.field_17404.getOpposite();
			}

			return directions;
		}
	}
}
