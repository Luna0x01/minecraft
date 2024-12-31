package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class WaterPathNodeMaker extends class_2771 {
	@Override
	public PathNode method_11918() {
		return this.method_11912(
			MathHelper.floor(this.field_13076.getBoundingBox().minX),
			MathHelper.floor(this.field_13076.getBoundingBox().minY + 0.5),
			MathHelper.floor(this.field_13076.getBoundingBox().minZ)
		);
	}

	@Override
	public PathNode method_11911(double d, double e, double f) {
		return this.method_11912(
			MathHelper.floor(d - (double)(this.field_13076.width / 2.0F)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(this.field_13076.width / 2.0F))
		);
	}

	@Override
	public int method_11917(PathNode[] pathNodes, PathNode pathNode, PathNode pathNode2, float f) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			PathNode pathNode3 = this.method_11944(
				pathNode.posX + direction.getOffsetX(), pathNode.posY + direction.getOffsetY(), pathNode.posZ + direction.getOffsetZ()
			);
			if (pathNode3 != null && !pathNode3.visited && pathNode3.getDistance(pathNode2) < f) {
				pathNodes[i++] = pathNode3;
			}
		}

		return i;
	}

	@Override
	public LandType method_11914(BlockView blockView, int i, int j, int k, MobEntity mobEntity, int l, int m, int n, boolean bl, boolean bl2) {
		return LandType.WATER;
	}

	@Override
	public LandType method_11913(BlockView blockView, int i, int j, int k) {
		return LandType.WATER;
	}

	@Nullable
	private PathNode method_11944(int i, int j, int k) {
		LandType landType = this.method_11945(i, j, k);
		return landType == LandType.WATER ? this.method_11912(i, j, k) : null;
	}

	private LandType method_11945(int i, int j, int k) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = i; l < i + this.field_13078; l++) {
			for (int m = j; m < j + this.field_13079; m++) {
				for (int n = k; n < k + this.field_13080; n++) {
					BlockState blockState = this.field_13075.getBlockState(mutable.setPosition(l, m, n));
					if (blockState.getMaterial() != Material.WATER) {
						return LandType.BLOCKED;
					}
				}
			}
		}

		return LandType.WATER;
	}
}
