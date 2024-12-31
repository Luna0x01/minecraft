package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3998;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EvocationIllagerEntity;
import net.minecraft.entity.VindicationIllagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3072 {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(class_3072.class_3081.class, "WMP");
	}

	public static void method_13778(class_3998 arg, BlockPos blockPos, BlockRotation blockRotation, List<class_3072.class_3081> list, Random random) {
		class_3072.class_3075 lv = new class_3072.class_3075(random);
		class_3072.class_3076 lv2 = new class_3072.class_3076(arg, random);
		lv2.method_13798(blockPos, blockRotation, list, lv);
	}

	static class class_3073 extends class_3072.class_3074 {
		private class_3073() {
		}

		@Override
		public String method_13779(Random random) {
			return "1x1_a" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_13781(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_13780(Random random, boolean bl) {
			return "1x2_a" + (random.nextInt(9) + 1);
		}

		@Override
		public String method_13782(Random random, boolean bl) {
			return "1x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_13783(Random random) {
			return "1x2_s" + (random.nextInt(2) + 1);
		}

		@Override
		public String method_13784(Random random) {
			return "2x2_a" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_13785(Random random) {
			return "2x2_s1";
		}
	}

	abstract static class class_3074 {
		private class_3074() {
		}

		public abstract String method_13779(Random random);

		public abstract String method_13781(Random random);

		public abstract String method_13780(Random random, boolean bl);

		public abstract String method_13782(Random random, boolean bl);

		public abstract String method_13783(Random random);

		public abstract String method_13784(Random random);

		public abstract String method_13785(Random random);
	}

	static class class_3075 {
		private final Random field_15197;
		private final class_3072.class_3079 field_15198;
		private final class_3072.class_3079 field_15199;
		private final class_3072.class_3079[] field_15200;
		private final int field_15201;
		private final int field_15202;

		public class_3075(Random random) {
			this.field_15197 = random;
			int i = 11;
			this.field_15201 = 7;
			this.field_15202 = 4;
			this.field_15198 = new class_3072.class_3079(11, 11, 5);
			this.field_15198.method_13812(this.field_15201, this.field_15202, this.field_15201 + 1, this.field_15202 + 1, 3);
			this.field_15198.method_13812(this.field_15201 - 1, this.field_15202, this.field_15201 - 1, this.field_15202 + 1, 2);
			this.field_15198.method_13812(this.field_15201 + 2, this.field_15202 - 2, this.field_15201 + 3, this.field_15202 + 3, 5);
			this.field_15198.method_13812(this.field_15201 + 1, this.field_15202 - 2, this.field_15201 + 1, this.field_15202 - 1, 1);
			this.field_15198.method_13812(this.field_15201 + 1, this.field_15202 + 2, this.field_15201 + 1, this.field_15202 + 3, 1);
			this.field_15198.method_13810(this.field_15201 - 1, this.field_15202 - 1, 1);
			this.field_15198.method_13810(this.field_15201 - 1, this.field_15202 + 2, 1);
			this.field_15198.method_13812(0, 0, 11, 1, 5);
			this.field_15198.method_13812(0, 9, 11, 11, 5);
			this.method_13790(this.field_15198, this.field_15201, this.field_15202 - 2, Direction.WEST, 6);
			this.method_13790(this.field_15198, this.field_15201, this.field_15202 + 3, Direction.WEST, 6);
			this.method_13790(this.field_15198, this.field_15201 - 2, this.field_15202 - 1, Direction.WEST, 3);
			this.method_13790(this.field_15198, this.field_15201 - 2, this.field_15202 + 2, Direction.WEST, 3);

			while (this.method_13787(this.field_15198)) {
			}

			this.field_15200 = new class_3072.class_3079[3];
			this.field_15200[0] = new class_3072.class_3079(11, 11, 5);
			this.field_15200[1] = new class_3072.class_3079(11, 11, 5);
			this.field_15200[2] = new class_3072.class_3079(11, 11, 5);
			this.method_13791(this.field_15198, this.field_15200[0]);
			this.method_13791(this.field_15198, this.field_15200[1]);
			this.field_15200[0].method_13812(this.field_15201 + 1, this.field_15202, this.field_15201 + 1, this.field_15202 + 1, 8388608);
			this.field_15200[1].method_13812(this.field_15201 + 1, this.field_15202, this.field_15201 + 1, this.field_15202 + 1, 8388608);
			this.field_15199 = new class_3072.class_3079(this.field_15198.field_15211, this.field_15198.field_15212, 5);
			this.method_13792();
			this.method_13791(this.field_15199, this.field_15200[2]);
		}

		public static boolean method_13788(class_3072.class_3079 arg, int i, int j) {
			int k = arg.method_13809(i, j);
			return k == 1 || k == 2 || k == 3 || k == 4;
		}

		public boolean method_13789(class_3072.class_3079 arg, int i, int j, int k, int l) {
			return (this.field_15200[k].method_13809(i, j) & 65535) == l;
		}

		@Nullable
		public Direction method_13794(class_3072.class_3079 arg, int i, int j, int k, int l) {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (this.method_13789(arg, i + direction.getOffsetX(), j + direction.getOffsetZ(), k, l)) {
					return direction;
				}
			}

			return null;
		}

		private void method_13790(class_3072.class_3079 arg, int i, int j, Direction direction, int k) {
			if (k > 0) {
				arg.method_13810(i, j, 1);
				arg.method_13811(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);

				for (int l = 0; l < 8; l++) {
					Direction direction2 = Direction.fromHorizontal(this.field_15197.nextInt(4));
					if (direction2 != direction.getOpposite() && (direction2 != Direction.EAST || !this.field_15197.nextBoolean())) {
						int m = i + direction.getOffsetX();
						int n = j + direction.getOffsetZ();
						if (arg.method_13809(m + direction2.getOffsetX(), n + direction2.getOffsetZ()) == 0
							&& arg.method_13809(m + direction2.getOffsetX() * 2, n + direction2.getOffsetZ() * 2) == 0) {
							this.method_13790(arg, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, k - 1);
							break;
						}
					}
				}

				Direction direction3 = direction.rotateYClockwise();
				Direction direction4 = direction.rotateYCounterclockwise();
				arg.method_13811(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
				arg.method_13811(i + direction4.getOffsetX(), j + direction4.getOffsetZ(), 0, 2);
				arg.method_13811(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
				arg.method_13811(i + direction.getOffsetX() + direction4.getOffsetX(), j + direction.getOffsetZ() + direction4.getOffsetZ(), 0, 2);
				arg.method_13811(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
				arg.method_13811(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
				arg.method_13811(i + direction4.getOffsetX() * 2, j + direction4.getOffsetZ() * 2, 0, 2);
			}
		}

		private boolean method_13787(class_3072.class_3079 arg) {
			boolean bl = false;

			for (int i = 0; i < arg.field_15212; i++) {
				for (int j = 0; j < arg.field_15211; j++) {
					if (arg.method_13809(j, i) == 0) {
						int k = 0;
						k += method_13788(arg, j + 1, i) ? 1 : 0;
						k += method_13788(arg, j - 1, i) ? 1 : 0;
						k += method_13788(arg, j, i + 1) ? 1 : 0;
						k += method_13788(arg, j, i - 1) ? 1 : 0;
						if (k >= 3) {
							arg.method_13810(j, i, 2);
							bl = true;
						} else if (k == 2) {
							int l = 0;
							l += method_13788(arg, j + 1, i + 1) ? 1 : 0;
							l += method_13788(arg, j - 1, i + 1) ? 1 : 0;
							l += method_13788(arg, j + 1, i - 1) ? 1 : 0;
							l += method_13788(arg, j - 1, i - 1) ? 1 : 0;
							if (l <= 1) {
								arg.method_13810(j, i, 2);
								bl = true;
							}
						}
					}
				}
			}

			return bl;
		}

		private void method_13792() {
			List<Pair<Integer, Integer>> list = Lists.newArrayList();
			class_3072.class_3079 lv = this.field_15200[1];

			for (int i = 0; i < this.field_15199.field_15212; i++) {
				for (int j = 0; j < this.field_15199.field_15211; j++) {
					int k = lv.method_13809(j, i);
					int l = k & 983040;
					if (l == 131072 && (k & 2097152) == 2097152) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			if (list.isEmpty()) {
				this.field_15199.method_13812(0, 0, this.field_15199.field_15211, this.field_15199.field_15212, 5);
			} else {
				Pair<Integer, Integer> pair = (Pair<Integer, Integer>)list.get(this.field_15197.nextInt(list.size()));
				int m = lv.method_13809(pair.getLeft(), pair.getRight());
				lv.method_13810(pair.getLeft(), pair.getRight(), m | 4194304);
				Direction direction = this.method_13794(this.field_15198, pair.getLeft(), pair.getRight(), 1, m & 65535);
				int n = pair.getLeft() + direction.getOffsetX();
				int o = pair.getRight() + direction.getOffsetZ();

				for (int p = 0; p < this.field_15199.field_15212; p++) {
					for (int q = 0; q < this.field_15199.field_15211; q++) {
						if (!method_13788(this.field_15198, q, p)) {
							this.field_15199.method_13810(q, p, 5);
						} else if (q == pair.getLeft() && p == pair.getRight()) {
							this.field_15199.method_13810(q, p, 3);
						} else if (q == n && p == o) {
							this.field_15199.method_13810(q, p, 3);
							this.field_15200[2].method_13810(q, p, 8388608);
						}
					}
				}

				List<Direction> list2 = Lists.newArrayList();

				for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
					if (this.field_15199.method_13809(n + direction2.getOffsetX(), o + direction2.getOffsetZ()) == 0) {
						list2.add(direction2);
					}
				}

				if (list2.isEmpty()) {
					this.field_15199.method_13812(0, 0, this.field_15199.field_15211, this.field_15199.field_15212, 5);
					lv.method_13810(pair.getLeft(), pair.getRight(), m);
				} else {
					Direction direction3 = (Direction)list2.get(this.field_15197.nextInt(list2.size()));
					this.method_13790(this.field_15199, n + direction3.getOffsetX(), o + direction3.getOffsetZ(), direction3, 4);

					while (this.method_13787(this.field_15199)) {
					}
				}
			}
		}

		private void method_13791(class_3072.class_3079 arg, class_3072.class_3079 arg2) {
			List<Pair<Integer, Integer>> list = Lists.newArrayList();

			for (int i = 0; i < arg.field_15212; i++) {
				for (int j = 0; j < arg.field_15211; j++) {
					if (arg.method_13809(j, i) == 2) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			Collections.shuffle(list, this.field_15197);
			int k = 10;

			for (Pair<Integer, Integer> pair : list) {
				int l = pair.getLeft();
				int m = pair.getRight();
				if (arg2.method_13809(l, m) == 0) {
					int n = l;
					int o = l;
					int p = m;
					int q = m;
					int r = 65536;
					if (arg2.method_13809(l + 1, m) == 0
						&& arg2.method_13809(l, m + 1) == 0
						&& arg2.method_13809(l + 1, m + 1) == 0
						&& arg.method_13809(l + 1, m) == 2
						&& arg.method_13809(l, m + 1) == 2
						&& arg.method_13809(l + 1, m + 1) == 2) {
						o = l + 1;
						q = m + 1;
						r = 262144;
					} else if (arg2.method_13809(l - 1, m) == 0
						&& arg2.method_13809(l, m + 1) == 0
						&& arg2.method_13809(l - 1, m + 1) == 0
						&& arg.method_13809(l - 1, m) == 2
						&& arg.method_13809(l, m + 1) == 2
						&& arg.method_13809(l - 1, m + 1) == 2) {
						n = l - 1;
						q = m + 1;
						r = 262144;
					} else if (arg2.method_13809(l - 1, m) == 0
						&& arg2.method_13809(l, m - 1) == 0
						&& arg2.method_13809(l - 1, m - 1) == 0
						&& arg.method_13809(l - 1, m) == 2
						&& arg.method_13809(l, m - 1) == 2
						&& arg.method_13809(l - 1, m - 1) == 2) {
						n = l - 1;
						p = m - 1;
						r = 262144;
					} else if (arg2.method_13809(l + 1, m) == 0 && arg.method_13809(l + 1, m) == 2) {
						o = l + 1;
						r = 131072;
					} else if (arg2.method_13809(l, m + 1) == 0 && arg.method_13809(l, m + 1) == 2) {
						q = m + 1;
						r = 131072;
					} else if (arg2.method_13809(l - 1, m) == 0 && arg.method_13809(l - 1, m) == 2) {
						n = l - 1;
						r = 131072;
					} else if (arg2.method_13809(l, m - 1) == 0 && arg.method_13809(l, m - 1) == 2) {
						p = m - 1;
						r = 131072;
					}

					int s = this.field_15197.nextBoolean() ? n : o;
					int t = this.field_15197.nextBoolean() ? p : q;
					int u = 2097152;
					if (!arg.method_13814(s, t, 1)) {
						s = s == n ? o : n;
						t = t == p ? q : p;
						if (!arg.method_13814(s, t, 1)) {
							t = t == p ? q : p;
							if (!arg.method_13814(s, t, 1)) {
								s = s == n ? o : n;
								t = t == p ? q : p;
								if (!arg.method_13814(s, t, 1)) {
									u = 0;
									s = n;
									t = p;
								}
							}
						}
					}

					for (int v = p; v <= q; v++) {
						for (int w = n; w <= o; w++) {
							if (w == s && v == t) {
								arg2.method_13810(w, v, 1048576 | u | r | k);
							} else {
								arg2.method_13810(w, v, r | k);
							}
						}
					}

					k++;
				}
			}
		}
	}

	static class class_3076 {
		private final class_3998 field_15203;
		private final Random field_15204;
		private int field_15205;
		private int field_15206;

		public class_3076(class_3998 arg, Random random) {
			this.field_15203 = arg;
			this.field_15204 = random;
		}

		public void method_13798(BlockPos blockPos, BlockRotation blockRotation, List<class_3072.class_3081> list, class_3072.class_3075 arg) {
			class_3072.class_3077 lv = new class_3072.class_3077();
			lv.field_15208 = blockPos;
			lv.field_15207 = blockRotation;
			lv.field_15209 = "wall_flat";
			class_3072.class_3077 lv2 = new class_3072.class_3077();
			this.method_13799(list, lv);
			lv2.field_15208 = lv.field_15208.up(8);
			lv2.field_15207 = lv.field_15207;
			lv2.field_15209 = "wall_window";
			if (!list.isEmpty()) {
			}

			class_3072.class_3079 lv3 = arg.field_15198;
			class_3072.class_3079 lv4 = arg.field_15199;
			this.field_15205 = arg.field_15201 + 1;
			this.field_15206 = arg.field_15202 + 1;
			int i = arg.field_15201 + 1;
			int j = arg.field_15202;
			this.method_13800(list, lv, lv3, Direction.SOUTH, this.field_15205, this.field_15206, i, j);
			this.method_13800(list, lv2, lv3, Direction.SOUTH, this.field_15205, this.field_15206, i, j);
			class_3072.class_3077 lv5 = new class_3072.class_3077();
			lv5.field_15208 = lv.field_15208.up(19);
			lv5.field_15207 = lv.field_15207;
			lv5.field_15209 = "wall_window";
			boolean bl = false;

			for (int k = 0; k < lv4.field_15212 && !bl; k++) {
				for (int l = lv4.field_15211 - 1; l >= 0 && !bl; l--) {
					if (class_3072.class_3075.method_13788(lv4, l, k)) {
						lv5.field_15208 = lv5.field_15208.offset(blockRotation.rotate(Direction.SOUTH), 8 + (k - this.field_15206) * 8);
						lv5.field_15208 = lv5.field_15208.offset(blockRotation.rotate(Direction.EAST), (l - this.field_15205) * 8);
						this.method_13806(list, lv5);
						this.method_13800(list, lv5, lv4, Direction.SOUTH, l, k, l, k);
						bl = true;
					}
				}
			}

			this.method_13802(list, blockPos.up(16), blockRotation, lv3, lv4);
			this.method_13802(list, blockPos.up(27), blockRotation, lv4, null);
			if (!list.isEmpty()) {
			}

			class_3072.class_3074[] lvs = new class_3072.class_3074[]{new class_3072.class_3073(), new class_3072.class_3078(), new class_3072.class_3080()};

			for (int m = 0; m < 3; m++) {
				BlockPos blockPos2 = blockPos.up(8 * m + (m == 2 ? 3 : 0));
				class_3072.class_3079 lv6 = arg.field_15200[m];
				class_3072.class_3079 lv7 = m == 2 ? lv4 : lv3;
				String string = m == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = m == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int n = 0; n < lv7.field_15212; n++) {
					for (int o = 0; o < lv7.field_15211; o++) {
						if (lv7.method_13809(o, n) == 1) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (n - this.field_15206) * 8);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.EAST), (o - this.field_15205) * 8);
							list.add(new class_3072.class_3081(this.field_15203, "corridor_floor", blockPos3, blockRotation));
							if (lv7.method_13809(o, n - 1) == 1 || (lv6.method_13809(o, n - 1) & 8388608) == 8388608) {
								list.add(new class_3072.class_3081(this.field_15203, "carpet_north", blockPos3.offset(blockRotation.rotate(Direction.EAST), 1).up(), blockRotation));
							}

							if (lv7.method_13809(o + 1, n) == 1 || (lv6.method_13809(o + 1, n) & 8388608) == 8388608) {
								list.add(
									new class_3072.class_3081(
										this.field_15203,
										"carpet_east",
										blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 1).offset(blockRotation.rotate(Direction.EAST), 5).up(),
										blockRotation
									)
								);
							}

							if (lv7.method_13809(o, n + 1) == 1 || (lv6.method_13809(o, n + 1) & 8388608) == 8388608) {
								list.add(
									new class_3072.class_3081(
										this.field_15203, string, blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 5).offset(blockRotation.rotate(Direction.WEST), 1), blockRotation
									)
								);
							}

							if (lv7.method_13809(o - 1, n) == 1 || (lv6.method_13809(o - 1, n) & 8388608) == 8388608) {
								list.add(
									new class_3072.class_3081(
										this.field_15203, string2, blockPos3.offset(blockRotation.rotate(Direction.WEST), 1).offset(blockRotation.rotate(Direction.NORTH), 1), blockRotation
									)
								);
							}
						}
					}
				}

				String string3 = m == 0 ? "indoors_wall_1" : "indoors_wall_2";
				String string4 = m == 0 ? "indoors_door_1" : "indoors_door_2";
				List<Direction> list2 = Lists.newArrayList();

				for (int p = 0; p < lv7.field_15212; p++) {
					for (int q = 0; q < lv7.field_15211; q++) {
						boolean bl2 = m == 2 && lv7.method_13809(q, p) == 3;
						if (lv7.method_13809(q, p) == 2 || bl2) {
							int r = lv6.method_13809(q, p);
							int s = r & 983040;
							int t = r & 65535;
							bl2 = bl2 && (r & 8388608) == 8388608;
							list2.clear();
							if ((r & 2097152) == 2097152) {
								for (Direction direction : Direction.DirectionType.HORIZONTAL) {
									if (lv7.method_13809(q + direction.getOffsetX(), p + direction.getOffsetZ()) == 1) {
										list2.add(direction);
									}
								}
							}

							Direction direction2 = null;
							if (!list2.isEmpty()) {
								direction2 = (Direction)list2.get(this.field_15204.nextInt(list2.size()));
							} else if ((r & 1048576) == 1048576) {
								direction2 = Direction.UP;
							}

							BlockPos blockPos4 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), 8 + (p - this.field_15206) * 8);
							blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.EAST), -1 + (q - this.field_15205) * 8);
							if (class_3072.class_3075.method_13788(lv7, q - 1, p) && !arg.method_13789(lv7, q - 1, p, m, t)) {
								list.add(new class_3072.class_3081(this.field_15203, direction2 == Direction.WEST ? string4 : string3, blockPos4, blockRotation));
							}

							if (lv7.method_13809(q + 1, p) == 1 && !bl2) {
								BlockPos blockPos5 = blockPos4.offset(blockRotation.rotate(Direction.EAST), 8);
								list.add(new class_3072.class_3081(this.field_15203, direction2 == Direction.EAST ? string4 : string3, blockPos5, blockRotation));
							}

							if (class_3072.class_3075.method_13788(lv7, q, p + 1) && !arg.method_13789(lv7, q, p + 1, m, t)) {
								BlockPos blockPos6 = blockPos4.offset(blockRotation.rotate(Direction.SOUTH), 7);
								blockPos6 = blockPos6.offset(blockRotation.rotate(Direction.EAST), 7);
								list.add(
									new class_3072.class_3081(
										this.field_15203, direction2 == Direction.SOUTH ? string4 : string3, blockPos6, blockRotation.rotate(BlockRotation.CLOCKWISE_90)
									)
								);
							}

							if (lv7.method_13809(q, p - 1) == 1 && !bl2) {
								BlockPos blockPos7 = blockPos4.offset(blockRotation.rotate(Direction.NORTH), 1);
								blockPos7 = blockPos7.offset(blockRotation.rotate(Direction.EAST), 7);
								list.add(
									new class_3072.class_3081(
										this.field_15203, direction2 == Direction.NORTH ? string4 : string3, blockPos7, blockRotation.rotate(BlockRotation.CLOCKWISE_90)
									)
								);
							}

							if (s == 65536) {
								this.method_13803(list, blockPos4, blockRotation, direction2, lvs[m]);
							} else if (s == 131072 && direction2 != null) {
								Direction direction3 = arg.method_13794(lv7, q, p, m, t);
								boolean bl3 = (r & 4194304) == 4194304;
								this.method_13805(list, blockPos4, blockRotation, direction3, direction2, lvs[m], bl3);
							} else if (s == 262144 && direction2 != null && direction2 != Direction.UP) {
								Direction direction4 = direction2.rotateYClockwise();
								if (!arg.method_13789(lv7, q + direction4.getOffsetX(), p + direction4.getOffsetZ(), m, t)) {
									direction4 = direction4.getOpposite();
								}

								this.method_13804(list, blockPos4, blockRotation, direction4, direction2, lvs[m]);
							} else if (s == 262144 && direction2 == Direction.UP) {
								this.method_13801(list, blockPos4, blockRotation, lvs[m]);
							}
						}
					}
				}
			}
		}

		private void method_13800(
			List<class_3072.class_3081> list, class_3072.class_3077 arg, class_3072.class_3079 arg2, Direction direction, int i, int j, int k, int l
		) {
			int m = i;
			int n = j;
			Direction direction2 = direction;

			do {
				if (!class_3072.class_3075.method_13788(arg2, m + direction.getOffsetX(), n + direction.getOffsetZ())) {
					this.method_13807(list, arg);
					direction = direction.rotateYClockwise();
					if (m != k || n != l || direction2 != direction) {
						this.method_13806(list, arg);
					}
				} else if (class_3072.class_3075.method_13788(arg2, m + direction.getOffsetX(), n + direction.getOffsetZ())
					&& class_3072.class_3075.method_13788(
						arg2,
						m + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(),
						n + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ()
					)) {
					this.method_13808(list, arg);
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					direction = direction.rotateYCounterclockwise();
				} else {
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					if (m != k || n != l || direction2 != direction) {
						this.method_13806(list, arg);
					}
				}
			} while (m != k || n != l || direction2 != direction);
		}

		private void method_13802(
			List<class_3072.class_3081> list, BlockPos blockPos, BlockRotation blockRotation, class_3072.class_3079 arg, @Nullable class_3072.class_3079 arg2
		) {
			for (int i = 0; i < arg.field_15212; i++) {
				for (int j = 0; j < arg.field_15211; j++) {
					BlockPos blockPos16 = blockPos.offset(blockRotation.rotate(Direction.SOUTH), 8 + (i - this.field_15206) * 8);
					blockPos16 = blockPos16.offset(blockRotation.rotate(Direction.EAST), (j - this.field_15205) * 8);
					boolean bl = arg2 != null && class_3072.class_3075.method_13788(arg2, j, i);
					if (class_3072.class_3075.method_13788(arg, j, i) && !bl) {
						list.add(new class_3072.class_3081(this.field_15203, "roof", blockPos16.up(3), blockRotation));
						if (!class_3072.class_3075.method_13788(arg, j + 1, i)) {
							BlockPos blockPos3 = blockPos16.offset(blockRotation.rotate(Direction.EAST), 6);
							list.add(new class_3072.class_3081(this.field_15203, "roof_front", blockPos3, blockRotation));
						}

						if (!class_3072.class_3075.method_13788(arg, j - 1, i)) {
							BlockPos blockPos4 = blockPos16.offset(blockRotation.rotate(Direction.EAST), 0);
							blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.SOUTH), 7);
							list.add(new class_3072.class_3081(this.field_15203, "roof_front", blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
						}

						if (!class_3072.class_3075.method_13788(arg, j, i - 1)) {
							BlockPos blockPos5 = blockPos16.offset(blockRotation.rotate(Direction.WEST), 1);
							list.add(new class_3072.class_3081(this.field_15203, "roof_front", blockPos5, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
						}

						if (!class_3072.class_3075.method_13788(arg, j, i + 1)) {
							BlockPos blockPos6 = blockPos16.offset(blockRotation.rotate(Direction.EAST), 6);
							blockPos6 = blockPos6.offset(blockRotation.rotate(Direction.SOUTH), 6);
							list.add(new class_3072.class_3081(this.field_15203, "roof_front", blockPos6, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
						}
					}
				}
			}

			if (arg2 != null) {
				for (int k = 0; k < arg.field_15212; k++) {
					for (int l = 0; l < arg.field_15211; l++) {
						BlockPos var17 = blockPos.offset(blockRotation.rotate(Direction.SOUTH), 8 + (k - this.field_15206) * 8);
						var17 = var17.offset(blockRotation.rotate(Direction.EAST), (l - this.field_15205) * 8);
						boolean bl2 = class_3072.class_3075.method_13788(arg2, l, k);
						if (class_3072.class_3075.method_13788(arg, l, k) && bl2) {
							if (!class_3072.class_3075.method_13788(arg, l + 1, k)) {
								BlockPos blockPos8 = var17.offset(blockRotation.rotate(Direction.EAST), 7);
								list.add(new class_3072.class_3081(this.field_15203, "small_wall", blockPos8, blockRotation));
							}

							if (!class_3072.class_3075.method_13788(arg, l - 1, k)) {
								BlockPos blockPos9 = var17.offset(blockRotation.rotate(Direction.WEST), 1);
								blockPos9 = blockPos9.offset(blockRotation.rotate(Direction.SOUTH), 6);
								list.add(new class_3072.class_3081(this.field_15203, "small_wall", blockPos9, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
							}

							if (!class_3072.class_3075.method_13788(arg, l, k - 1)) {
								BlockPos blockPos10 = var17.offset(blockRotation.rotate(Direction.WEST), 0);
								blockPos10 = blockPos10.offset(blockRotation.rotate(Direction.NORTH), 1);
								list.add(new class_3072.class_3081(this.field_15203, "small_wall", blockPos10, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							}

							if (!class_3072.class_3075.method_13788(arg, l, k + 1)) {
								BlockPos blockPos11 = var17.offset(blockRotation.rotate(Direction.EAST), 6);
								blockPos11 = blockPos11.offset(blockRotation.rotate(Direction.SOUTH), 7);
								list.add(new class_3072.class_3081(this.field_15203, "small_wall", blockPos11, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
							}

							if (!class_3072.class_3075.method_13788(arg, l + 1, k)) {
								if (!class_3072.class_3075.method_13788(arg, l, k - 1)) {
									BlockPos blockPos12 = var17.offset(blockRotation.rotate(Direction.EAST), 7);
									blockPos12 = blockPos12.offset(blockRotation.rotate(Direction.NORTH), 2);
									list.add(new class_3072.class_3081(this.field_15203, "small_wall_corner", blockPos12, blockRotation));
								}

								if (!class_3072.class_3075.method_13788(arg, l, k + 1)) {
									BlockPos blockPos13 = var17.offset(blockRotation.rotate(Direction.EAST), 8);
									blockPos13 = blockPos13.offset(blockRotation.rotate(Direction.SOUTH), 7);
									list.add(new class_3072.class_3081(this.field_15203, "small_wall_corner", blockPos13, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
								}
							}

							if (!class_3072.class_3075.method_13788(arg, l - 1, k)) {
								if (!class_3072.class_3075.method_13788(arg, l, k - 1)) {
									BlockPos blockPos14 = var17.offset(blockRotation.rotate(Direction.WEST), 2);
									blockPos14 = blockPos14.offset(blockRotation.rotate(Direction.NORTH), 1);
									list.add(new class_3072.class_3081(this.field_15203, "small_wall_corner", blockPos14, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
								}

								if (!class_3072.class_3075.method_13788(arg, l, k + 1)) {
									BlockPos blockPos15 = var17.offset(blockRotation.rotate(Direction.WEST), 1);
									blockPos15 = blockPos15.offset(blockRotation.rotate(Direction.SOUTH), 8);
									list.add(new class_3072.class_3081(this.field_15203, "small_wall_corner", blockPos15, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
								}
							}
						}
					}
				}
			}

			for (int m = 0; m < arg.field_15212; m++) {
				for (int n = 0; n < arg.field_15211; n++) {
					BlockPos var19 = blockPos.offset(blockRotation.rotate(Direction.SOUTH), 8 + (m - this.field_15206) * 8);
					var19 = var19.offset(blockRotation.rotate(Direction.EAST), (n - this.field_15205) * 8);
					boolean bl3 = arg2 != null && class_3072.class_3075.method_13788(arg2, n, m);
					if (class_3072.class_3075.method_13788(arg, n, m) && !bl3) {
						if (!class_3072.class_3075.method_13788(arg, n + 1, m)) {
							BlockPos blockPos17 = var19.offset(blockRotation.rotate(Direction.EAST), 6);
							if (!class_3072.class_3075.method_13788(arg, n, m + 1)) {
								BlockPos blockPos18 = blockPos17.offset(blockRotation.rotate(Direction.SOUTH), 6);
								list.add(new class_3072.class_3081(this.field_15203, "roof_corner", blockPos18, blockRotation));
							} else if (class_3072.class_3075.method_13788(arg, n + 1, m + 1)) {
								BlockPos blockPos19 = blockPos17.offset(blockRotation.rotate(Direction.SOUTH), 5);
								list.add(new class_3072.class_3081(this.field_15203, "roof_inner_corner", blockPos19, blockRotation));
							}

							if (!class_3072.class_3075.method_13788(arg, n, m - 1)) {
								list.add(new class_3072.class_3081(this.field_15203, "roof_corner", blockPos17, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							} else if (class_3072.class_3075.method_13788(arg, n + 1, m - 1)) {
								BlockPos blockPos20 = var19.offset(blockRotation.rotate(Direction.EAST), 9);
								blockPos20 = blockPos20.offset(blockRotation.rotate(Direction.NORTH), 2);
								list.add(new class_3072.class_3081(this.field_15203, "roof_inner_corner", blockPos20, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
							}
						}

						if (!class_3072.class_3075.method_13788(arg, n - 1, m)) {
							BlockPos blockPos21 = var19.offset(blockRotation.rotate(Direction.EAST), 0);
							blockPos21 = blockPos21.offset(blockRotation.rotate(Direction.SOUTH), 0);
							if (!class_3072.class_3075.method_13788(arg, n, m + 1)) {
								BlockPos blockPos22 = blockPos21.offset(blockRotation.rotate(Direction.SOUTH), 6);
								list.add(new class_3072.class_3081(this.field_15203, "roof_corner", blockPos22, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
							} else if (class_3072.class_3075.method_13788(arg, n - 1, m + 1)) {
								BlockPos blockPos23 = blockPos21.offset(blockRotation.rotate(Direction.SOUTH), 8);
								blockPos23 = blockPos23.offset(blockRotation.rotate(Direction.WEST), 3);
								list.add(new class_3072.class_3081(this.field_15203, "roof_inner_corner", blockPos23, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
							}

							if (!class_3072.class_3075.method_13788(arg, n, m - 1)) {
								list.add(new class_3072.class_3081(this.field_15203, "roof_corner", blockPos21, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
							} else if (class_3072.class_3075.method_13788(arg, n - 1, m - 1)) {
								BlockPos blockPos24 = blockPos21.offset(blockRotation.rotate(Direction.SOUTH), 1);
								list.add(new class_3072.class_3081(this.field_15203, "roof_inner_corner", blockPos24, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
							}
						}
					}
				}
			}
		}

		private void method_13799(List<class_3072.class_3081> list, class_3072.class_3077 arg) {
			Direction direction = arg.field_15207.rotate(Direction.WEST);
			list.add(new class_3072.class_3081(this.field_15203, "entrance", arg.field_15208.offset(direction, 9), arg.field_15207));
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.SOUTH), 16);
		}

		private void method_13806(List<class_3072.class_3081> list, class_3072.class_3077 arg) {
			list.add(new class_3072.class_3081(this.field_15203, arg.field_15209, arg.field_15208.offset(arg.field_15207.rotate(Direction.EAST), 7), arg.field_15207));
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.SOUTH), 8);
		}

		private void method_13807(List<class_3072.class_3081> list, class_3072.class_3077 arg) {
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.SOUTH), -1);
			list.add(new class_3072.class_3081(this.field_15203, "wall_corner", arg.field_15208, arg.field_15207));
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.SOUTH), -7);
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.WEST), -6);
			arg.field_15207 = arg.field_15207.rotate(BlockRotation.CLOCKWISE_90);
		}

		private void method_13808(List<class_3072.class_3081> list, class_3072.class_3077 arg) {
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.SOUTH), 6);
			arg.field_15208 = arg.field_15208.offset(arg.field_15207.rotate(Direction.EAST), 8);
			arg.field_15207 = arg.field_15207.rotate(BlockRotation.COUNTERCLOCKWISE_90);
		}

		private void method_13803(List<class_3072.class_3081> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, class_3072.class_3074 arg) {
			BlockRotation blockRotation2 = BlockRotation.NONE;
			String string = arg.method_13779(this.field_15204);
			if (direction != Direction.EAST) {
				if (direction == Direction.NORTH) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.COUNTERCLOCKWISE_90);
				} else if (direction == Direction.WEST) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.CLOCKWISE_180);
				} else if (direction == Direction.SOUTH) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.CLOCKWISE_90);
				} else {
					string = arg.method_13781(this.field_15204);
				}
			}

			BlockPos blockPos2 = Structure.method_13817(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation2, 7, 7);
			blockRotation2 = blockRotation2.rotate(blockRotation);
			blockPos2 = blockPos2.rotate(blockRotation);
			BlockPos blockPos3 = blockPos.add(blockPos2.getX(), 0, blockPos2.getZ());
			list.add(new class_3072.class_3081(this.field_15203, string, blockPos3, blockRotation2));
		}

		private void method_13805(
			List<class_3072.class_3081> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			Direction direction,
			Direction direction2,
			class_3072.class_3074 arg,
			boolean bl
		) {
			if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13780(this.field_15204, bl), blockPos2, blockRotation));
			} else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
				BlockPos blockPos3 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
				blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.SOUTH), 6);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13780(this.field_15204, bl), blockPos3, blockRotation, BlockMirror.LEFT_RIGHT));
			} else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
				BlockPos blockPos4 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
				blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.SOUTH), 6);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13780(this.field_15204, bl), blockPos4, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
			} else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
				BlockPos blockPos5 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13780(this.field_15204, bl), blockPos5, blockRotation, BlockMirror.FRONT_BACK));
			} else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
				BlockPos blockPos6 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
				list.add(
					new class_3072.class_3081(
						this.field_15203, arg.method_13780(this.field_15204, bl), blockPos6, blockRotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT
					)
				);
			} else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
				BlockPos blockPos7 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13780(this.field_15204, bl), blockPos7, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
			} else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
				BlockPos blockPos8 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
				blockPos8 = blockPos8.offset(blockRotation.rotate(Direction.SOUTH), 6);
				list.add(
					new class_3072.class_3081(
						this.field_15203, arg.method_13780(this.field_15204, bl), blockPos8, blockRotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK
					)
				);
			} else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
				BlockPos blockPos9 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
				blockPos9 = blockPos9.offset(blockRotation.rotate(Direction.SOUTH), 6);
				list.add(
					new class_3072.class_3081(this.field_15203, arg.method_13780(this.field_15204, bl), blockPos9, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90))
				);
			} else if (direction2 == Direction.SOUTH && direction == Direction.NORTH) {
				BlockPos blockPos10 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
				blockPos10 = blockPos10.offset(blockRotation.rotate(Direction.NORTH), 8);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13782(this.field_15204, bl), blockPos10, blockRotation));
			} else if (direction2 == Direction.NORTH && direction == Direction.SOUTH) {
				BlockPos blockPos11 = blockPos.offset(blockRotation.rotate(Direction.EAST), 7);
				blockPos11 = blockPos11.offset(blockRotation.rotate(Direction.SOUTH), 14);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13782(this.field_15204, bl), blockPos11, blockRotation.rotate(BlockRotation.CLOCKWISE_180)));
			} else if (direction2 == Direction.WEST && direction == Direction.EAST) {
				BlockPos blockPos12 = blockPos.offset(blockRotation.rotate(Direction.EAST), 15);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13782(this.field_15204, bl), blockPos12, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
			} else if (direction2 == Direction.EAST && direction == Direction.WEST) {
				BlockPos blockPos13 = blockPos.offset(blockRotation.rotate(Direction.WEST), 7);
				blockPos13 = blockPos13.offset(blockRotation.rotate(Direction.SOUTH), 6);
				list.add(
					new class_3072.class_3081(this.field_15203, arg.method_13782(this.field_15204, bl), blockPos13, blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90))
				);
			} else if (direction2 == Direction.UP && direction == Direction.EAST) {
				BlockPos blockPos14 = blockPos.offset(blockRotation.rotate(Direction.EAST), 15);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13783(this.field_15204), blockPos14, blockRotation.rotate(BlockRotation.CLOCKWISE_90)));
			} else if (direction2 == Direction.UP && direction == Direction.SOUTH) {
				BlockPos blockPos15 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
				blockPos15 = blockPos15.offset(blockRotation.rotate(Direction.NORTH), 0);
				list.add(new class_3072.class_3081(this.field_15203, arg.method_13783(this.field_15204), blockPos15, blockRotation));
			}
		}

		private void method_13804(
			List<class_3072.class_3081> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, Direction direction2, class_3072.class_3074 arg
		) {
			int i = 0;
			int j = 0;
			BlockRotation blockRotation2 = blockRotation;
			BlockMirror blockMirror = BlockMirror.NONE;
			if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
				i = -7;
			} else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
				i = -7;
				j = 6;
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
				i = 1;
				j = 14;
				blockRotation2 = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
			} else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
				i = 7;
				j = 14;
				blockRotation2 = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
				i = 7;
				j = -8;
				blockRotation2 = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
			} else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
				i = 1;
				j = -8;
				blockRotation2 = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
				blockMirror = BlockMirror.LEFT_RIGHT;
			} else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
				i = 15;
				j = 6;
				blockRotation2 = blockRotation.rotate(BlockRotation.CLOCKWISE_180);
			} else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
				i = 15;
				blockMirror = BlockMirror.FRONT_BACK;
			}

			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), i);
			blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.SOUTH), j);
			list.add(new class_3072.class_3081(this.field_15203, arg.method_13784(this.field_15204), blockPos2, blockRotation2, blockMirror));
		}

		private void method_13801(List<class_3072.class_3081> list, BlockPos blockPos, BlockRotation blockRotation, class_3072.class_3074 arg) {
			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.EAST), 1);
			list.add(new class_3072.class_3081(this.field_15203, arg.method_13785(this.field_15204), blockPos2, blockRotation, BlockMirror.NONE));
		}
	}

	static class class_3077 {
		public BlockRotation field_15207;
		public BlockPos field_15208;
		public String field_15209;

		private class_3077() {
		}
	}

	static class class_3078 extends class_3072.class_3074 {
		private class_3078() {
		}

		@Override
		public String method_13779(Random random) {
			return "1x1_b" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_13781(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_13780(Random random, boolean bl) {
			return bl ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
		}

		@Override
		public String method_13782(Random random, boolean bl) {
			return bl ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_13783(Random random) {
			return "1x2_se" + (random.nextInt(1) + 1);
		}

		@Override
		public String method_13784(Random random) {
			return "2x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String method_13785(Random random) {
			return "2x2_s1";
		}
	}

	static class class_3079 {
		private final int[][] field_15210;
		private final int field_15211;
		private final int field_15212;
		private final int field_15213;

		public class_3079(int i, int j, int k) {
			this.field_15211 = i;
			this.field_15212 = j;
			this.field_15213 = k;
			this.field_15210 = new int[i][j];
		}

		public void method_13810(int i, int j, int k) {
			if (i >= 0 && i < this.field_15211 && j >= 0 && j < this.field_15212) {
				this.field_15210[i][j] = k;
			}
		}

		public void method_13812(int i, int j, int k, int l, int m) {
			for (int n = j; n <= l; n++) {
				for (int o = i; o <= k; o++) {
					this.method_13810(o, n, m);
				}
			}
		}

		public int method_13809(int i, int j) {
			return i >= 0 && i < this.field_15211 && j >= 0 && j < this.field_15212 ? this.field_15210[i][j] : this.field_15213;
		}

		public void method_13811(int i, int j, int k, int l) {
			if (this.method_13809(i, j) == k) {
				this.method_13810(i, j, l);
			}
		}

		public boolean method_13814(int i, int j, int k) {
			return this.method_13809(i - 1, j) == k || this.method_13809(i + 1, j) == k || this.method_13809(i, j + 1) == k || this.method_13809(i, j - 1) == k;
		}
	}

	static class class_3080 extends class_3072.class_3078 {
		private class_3080() {
		}
	}

	public static class class_3081 extends class_2762 {
		private String field_15214;
		private BlockRotation field_15215;
		private BlockMirror field_15216;

		public class_3081() {
		}

		public class_3081(class_3998 arg, String string, BlockPos blockPos, BlockRotation blockRotation) {
			this(arg, string, blockPos, blockRotation, BlockMirror.NONE);
		}

		public class_3081(class_3998 arg, String string, BlockPos blockPos, BlockRotation blockRotation, BlockMirror blockMirror) {
			super(0);
			this.field_15214 = string;
			this.field_13018 = blockPos;
			this.field_15215 = blockRotation;
			this.field_15216 = blockMirror;
			this.method_13816(arg);
		}

		private void method_13816(class_3998 arg) {
			Structure structure = arg.method_17682(new Identifier("woodland_mansion/" + this.field_15214));
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.method_11870(true)
				.method_11868(this.field_15215)
				.method_11867(this.field_15216);
			this.method_11856(structure, this.field_13018, structurePlacementData);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putString("Template", this.field_15214);
			structureNbt.putString("Rot", this.field_13017.method_11874().name());
			structureNbt.putString("Mi", this.field_13017.method_11871().name());
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.field_15214 = nbtCompound.getString("Template");
			this.field_15215 = BlockRotation.valueOf(nbtCompound.getString("Rot"));
			this.field_15216 = BlockMirror.valueOf(nbtCompound.getString("Mi"));
			this.method_13816(arg);
		}

		@Override
		protected void method_11857(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox) {
			if (string.startsWith("Chest")) {
				BlockRotation blockRotation = this.field_13017.method_11874();
				BlockState blockState = Blocks.CHEST.getDefaultState();
				if ("ChestWest".equals(string)) {
					blockState = blockState.withProperty(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
				} else if ("ChestEast".equals(string)) {
					blockState = blockState.withProperty(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
				} else if ("ChestSouth".equals(string)) {
					blockState = blockState.withProperty(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
				} else if ("ChestNorth".equals(string)) {
					blockState = blockState.withProperty(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
				}

				this.method_13775(iWorld, blockBox, random, blockPos, LootTables.WOODLAND_MANSION_CHEST, blockState);
			} else if ("Mage".equals(string)) {
				EvocationIllagerEntity evocationIllagerEntity = new EvocationIllagerEntity(iWorld.method_16348());
				evocationIllagerEntity.setPersistent();
				evocationIllagerEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
				iWorld.method_3686(evocationIllagerEntity);
				iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
			} else if ("Warrior".equals(string)) {
				VindicationIllagerEntity vindicationIllagerEntity = new VindicationIllagerEntity(iWorld.method_16348());
				vindicationIllagerEntity.setPersistent();
				vindicationIllagerEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
				vindicationIllagerEntity.initialize(iWorld.method_8482(new BlockPos(vindicationIllagerEntity)), null, null);
				iWorld.method_3686(vindicationIllagerEntity);
				iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
			}
		}
	}
}
