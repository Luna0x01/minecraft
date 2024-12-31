package net.minecraft.structure;

import java.util.Random;
import net.minecraft.class_3804;
import net.minecraft.class_3998;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public abstract class class_8 extends StructurePiece {
	protected int field_14;
	protected int field_15;
	protected int field_16;
	protected int field_17 = -1;

	public class_8() {
	}

	protected class_8(Random random, int i, int j, int k, int l, int m, int n) {
		super(0);
		this.field_14 = l;
		this.field_15 = m;
		this.field_16 = n;
		this.method_11853(Direction.DirectionType.HORIZONTAL.getRandomDirection(random));
		if (this.method_11854().getAxis() == Direction.Axis.Z) {
			this.boundingBox = new BlockBox(i, j, k, i + l - 1, j + m - 1, k + n - 1);
		} else {
			this.boundingBox = new BlockBox(i, j, k, i + n - 1, j + m - 1, k + l - 1);
		}
	}

	@Override
	protected void serialize(NbtCompound structureNbt) {
		structureNbt.putInt("Width", this.field_14);
		structureNbt.putInt("Height", this.field_15);
		structureNbt.putInt("Depth", this.field_16);
		structureNbt.putInt("HPos", this.field_17);
	}

	@Override
	protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		this.field_14 = nbtCompound.getInt("Width");
		this.field_15 = nbtCompound.getInt("Height");
		this.field_16 = nbtCompound.getInt("Depth");
		this.field_17 = nbtCompound.getInt("HPos");
	}

	protected boolean method_16(IWorld iWorld, BlockBox blockBox, int i) {
		if (this.field_17 >= 0) {
			return true;
		} else {
			int j = 0;
			int k = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int l = this.boundingBox.minZ; l <= this.boundingBox.maxZ; l++) {
				for (int m = this.boundingBox.minX; m <= this.boundingBox.maxX; m++) {
					mutable.setPosition(m, 64, l);
					if (blockBox.contains(mutable)) {
						j += iWorld.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, mutable).getY();
						k++;
					}
				}
			}

			if (k == 0) {
				return false;
			} else {
				this.field_17 = j / k;
				this.boundingBox.move(0, this.field_17 - this.boundingBox.minY + i, 0);
				return true;
			}
		}
	}
}
