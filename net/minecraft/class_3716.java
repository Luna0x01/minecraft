package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_3716 {
	private final World field_18436;
	private final BlockPos field_18437;
	private final AbstractRailBlock field_18438;
	private BlockState field_18439;
	private final boolean field_18440;
	private final List<BlockPos> field_18441 = Lists.newArrayList();

	public class_3716(World world, BlockPos blockPos, BlockState blockState) {
		this.field_18436 = world;
		this.field_18437 = blockPos;
		this.field_18439 = blockState;
		this.field_18438 = (AbstractRailBlock)blockState.getBlock();
		RailShape railShape = blockState.getProperty(this.field_18438.getShapeProperty());
		this.field_18440 = this.field_18438.cannotMakeCurves();
		this.method_16716(railShape);
	}

	public List<BlockPos> method_16714() {
		return this.field_18441;
	}

	private void method_16716(RailShape railShape) {
		this.field_18441.clear();
		switch (railShape) {
			case NORTH_SOUTH:
				this.field_18441.add(this.field_18437.north());
				this.field_18441.add(this.field_18437.south());
				break;
			case EAST_WEST:
				this.field_18441.add(this.field_18437.west());
				this.field_18441.add(this.field_18437.east());
				break;
			case ASCENDING_EAST:
				this.field_18441.add(this.field_18437.west());
				this.field_18441.add(this.field_18437.east().up());
				break;
			case ASCENDING_WEST:
				this.field_18441.add(this.field_18437.west().up());
				this.field_18441.add(this.field_18437.east());
				break;
			case ASCENDING_NORTH:
				this.field_18441.add(this.field_18437.north().up());
				this.field_18441.add(this.field_18437.south());
				break;
			case ASCENDING_SOUTH:
				this.field_18441.add(this.field_18437.north());
				this.field_18441.add(this.field_18437.south().up());
				break;
			case SOUTH_EAST:
				this.field_18441.add(this.field_18437.east());
				this.field_18441.add(this.field_18437.south());
				break;
			case SOUTH_WEST:
				this.field_18441.add(this.field_18437.west());
				this.field_18441.add(this.field_18437.south());
				break;
			case NORTH_WEST:
				this.field_18441.add(this.field_18437.west());
				this.field_18441.add(this.field_18437.north());
				break;
			case NORTH_EAST:
				this.field_18441.add(this.field_18437.east());
				this.field_18441.add(this.field_18437.north());
		}
	}

	private void method_16725() {
		for (int i = 0; i < this.field_18441.size(); i++) {
			class_3716 lv = this.method_16721((BlockPos)this.field_18441.get(i));
			if (lv != null && lv.method_16715(this)) {
				this.field_18441.set(i, lv.field_18437);
			} else {
				this.field_18441.remove(i--);
			}
		}
	}

	private boolean method_16717(BlockPos blockPos) {
		return AbstractRailBlock.isRail(this.field_18436, blockPos)
			|| AbstractRailBlock.isRail(this.field_18436, blockPos.up())
			|| AbstractRailBlock.isRail(this.field_18436, blockPos.down());
	}

	@Nullable
	private class_3716 method_16721(BlockPos blockPos) {
		BlockState blockState = this.field_18436.getBlockState(blockPos);
		if (AbstractRailBlock.isRail(blockState)) {
			return new class_3716(this.field_18436, blockPos, blockState);
		} else {
			BlockPos blockPos2 = blockPos.up();
			blockState = this.field_18436.getBlockState(blockPos2);
			if (AbstractRailBlock.isRail(blockState)) {
				return new class_3716(this.field_18436, blockPos2, blockState);
			} else {
				blockPos2 = blockPos.down();
				blockState = this.field_18436.getBlockState(blockPos2);
				return AbstractRailBlock.isRail(blockState) ? new class_3716(this.field_18436, blockPos2, blockState) : null;
			}
		}
	}

	private boolean method_16715(class_3716 arg) {
		return this.method_16724(arg.field_18437);
	}

	private boolean method_16724(BlockPos blockPos) {
		for (int i = 0; i < this.field_18441.size(); i++) {
			BlockPos blockPos2 = (BlockPos)this.field_18441.get(i);
			if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) {
				return true;
			}
		}

		return false;
	}

	protected int method_16719() {
		int i = 0;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (this.method_16717(this.field_18437.offset(direction))) {
				i++;
			}
		}

		return i;
	}

	private boolean method_16720(class_3716 arg) {
		return this.method_16715(arg) || this.field_18441.size() != 2;
	}

	private void method_16723(class_3716 arg) {
		this.field_18441.add(arg.field_18437);
		BlockPos blockPos = this.field_18437.north();
		BlockPos blockPos2 = this.field_18437.south();
		BlockPos blockPos3 = this.field_18437.west();
		BlockPos blockPos4 = this.field_18437.east();
		boolean bl = this.method_16724(blockPos);
		boolean bl2 = this.method_16724(blockPos2);
		boolean bl3 = this.method_16724(blockPos3);
		boolean bl4 = this.method_16724(blockPos4);
		RailShape railShape = null;
		if (bl || bl2) {
			railShape = RailShape.NORTH_SOUTH;
		}

		if (bl3 || bl4) {
			railShape = RailShape.EAST_WEST;
		}

		if (!this.field_18440) {
			if (bl2 && bl4 && !bl && !bl3) {
				railShape = RailShape.SOUTH_EAST;
			}

			if (bl2 && bl3 && !bl && !bl4) {
				railShape = RailShape.SOUTH_WEST;
			}

			if (bl && bl3 && !bl2 && !bl4) {
				railShape = RailShape.NORTH_WEST;
			}

			if (bl && bl4 && !bl2 && !bl3) {
				railShape = RailShape.NORTH_EAST;
			}
		}

		if (railShape == RailShape.NORTH_SOUTH) {
			if (AbstractRailBlock.isRail(this.field_18436, blockPos.up())) {
				railShape = RailShape.ASCENDING_NORTH;
			}

			if (AbstractRailBlock.isRail(this.field_18436, blockPos2.up())) {
				railShape = RailShape.ASCENDING_SOUTH;
			}
		}

		if (railShape == RailShape.EAST_WEST) {
			if (AbstractRailBlock.isRail(this.field_18436, blockPos4.up())) {
				railShape = RailShape.ASCENDING_EAST;
			}

			if (AbstractRailBlock.isRail(this.field_18436, blockPos3.up())) {
				railShape = RailShape.ASCENDING_WEST;
			}
		}

		if (railShape == null) {
			railShape = RailShape.NORTH_SOUTH;
		}

		this.field_18439 = this.field_18439.withProperty(this.field_18438.getShapeProperty(), railShape);
		this.field_18436.setBlockState(this.field_18437, this.field_18439, 3);
	}

	private boolean method_16726(BlockPos blockPos) {
		class_3716 lv = this.method_16721(blockPos);
		if (lv == null) {
			return false;
		} else {
			lv.method_16725();
			return lv.method_16720(this);
		}
	}

	public class_3716 method_16718(boolean bl, boolean bl2) {
		BlockPos blockPos = this.field_18437.north();
		BlockPos blockPos2 = this.field_18437.south();
		BlockPos blockPos3 = this.field_18437.west();
		BlockPos blockPos4 = this.field_18437.east();
		boolean bl3 = this.method_16726(blockPos);
		boolean bl4 = this.method_16726(blockPos2);
		boolean bl5 = this.method_16726(blockPos3);
		boolean bl6 = this.method_16726(blockPos4);
		RailShape railShape = null;
		if ((bl3 || bl4) && !bl5 && !bl6) {
			railShape = RailShape.NORTH_SOUTH;
		}

		if ((bl5 || bl6) && !bl3 && !bl4) {
			railShape = RailShape.EAST_WEST;
		}

		if (!this.field_18440) {
			if (bl4 && bl6 && !bl3 && !bl5) {
				railShape = RailShape.SOUTH_EAST;
			}

			if (bl4 && bl5 && !bl3 && !bl6) {
				railShape = RailShape.SOUTH_WEST;
			}

			if (bl3 && bl5 && !bl4 && !bl6) {
				railShape = RailShape.NORTH_WEST;
			}

			if (bl3 && bl6 && !bl4 && !bl5) {
				railShape = RailShape.NORTH_EAST;
			}
		}

		if (railShape == null) {
			if (bl3 || bl4) {
				railShape = RailShape.NORTH_SOUTH;
			}

			if (bl5 || bl6) {
				railShape = RailShape.EAST_WEST;
			}

			if (!this.field_18440) {
				if (bl) {
					if (bl4 && bl6) {
						railShape = RailShape.SOUTH_EAST;
					}

					if (bl5 && bl4) {
						railShape = RailShape.SOUTH_WEST;
					}

					if (bl6 && bl3) {
						railShape = RailShape.NORTH_EAST;
					}

					if (bl3 && bl5) {
						railShape = RailShape.NORTH_WEST;
					}
				} else {
					if (bl3 && bl5) {
						railShape = RailShape.NORTH_WEST;
					}

					if (bl6 && bl3) {
						railShape = RailShape.NORTH_EAST;
					}

					if (bl5 && bl4) {
						railShape = RailShape.SOUTH_WEST;
					}

					if (bl4 && bl6) {
						railShape = RailShape.SOUTH_EAST;
					}
				}
			}
		}

		if (railShape == RailShape.NORTH_SOUTH) {
			if (AbstractRailBlock.isRail(this.field_18436, blockPos.up())) {
				railShape = RailShape.ASCENDING_NORTH;
			}

			if (AbstractRailBlock.isRail(this.field_18436, blockPos2.up())) {
				railShape = RailShape.ASCENDING_SOUTH;
			}
		}

		if (railShape == RailShape.EAST_WEST) {
			if (AbstractRailBlock.isRail(this.field_18436, blockPos4.up())) {
				railShape = RailShape.ASCENDING_EAST;
			}

			if (AbstractRailBlock.isRail(this.field_18436, blockPos3.up())) {
				railShape = RailShape.ASCENDING_WEST;
			}
		}

		if (railShape == null) {
			railShape = RailShape.NORTH_SOUTH;
		}

		this.method_16716(railShape);
		this.field_18439 = this.field_18439.withProperty(this.field_18438.getShapeProperty(), railShape);
		if (bl2 || this.field_18436.getBlockState(this.field_18437) != this.field_18439) {
			this.field_18436.setBlockState(this.field_18437, this.field_18439, 3);

			for (int i = 0; i < this.field_18441.size(); i++) {
				class_3716 lv = this.method_16721((BlockPos)this.field_18441.get(i));
				if (lv != null) {
					lv.method_16725();
					if (lv.method_16720(this)) {
						lv.method_16723(this);
					}
				}
			}
		}

		return this;
	}

	public BlockState method_16722() {
		return this.field_18439;
	}
}
