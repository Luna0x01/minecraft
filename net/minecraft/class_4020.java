package net.minecraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

public class class_4020 extends class_4017 {
	public static final Direction[] field_19479 = new Direction[]{Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH};

	@Override
	public LightType method_17742() {
		return LightType.SKY;
	}

	public void method_17743(class_4441 arg, class_3781 arg2) {
		int i = arg2.method_3920().getActualX();
		int j = arg2.method_3920().getActualZ();

		try (
			BlockPos.Pooled pooled = BlockPos.Pooled.get();
			BlockPos.Pooled pooled2 = BlockPos.Pooled.get();
		) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					int m = arg2.method_16992(class_3804.class_3805.LIGHT_BLOCKING, k, l) + 1;
					int n = k + i;
					int o = l + j;

					for (int p = m; p < arg2.method_17003().length * 16 - 1; p++) {
						pooled.setPosition(n, p, o);
						this.method_17734(arg, pooled, 15);
					}

					this.method_17730(arg2.method_3920(), n, m, o, 15);

					for (Direction direction : field_19479) {
						int q = arg.method_16372(class_3804.class_3805.LIGHT_BLOCKING, n + direction.getOffsetX(), o + direction.getOffsetZ());
						if (q - m >= 2) {
							for (int r = m; r <= q; r++) {
								pooled2.setPosition(n + direction.getOffsetX(), r, o + direction.getOffsetZ());
								int s = arg.getBlockState(pooled2).method_16885(arg, pooled2);
								if (s != arg.getMaxLightLevel()) {
									this.method_17734(arg, pooled2, 15 - s - 1);
									this.method_17731(arg2.method_3920(), pooled2, 15 - s - 1);
								}
							}
						}
					}
				}
			}

			this.method_17732(arg, arg2.method_3920());
		}
	}
}
