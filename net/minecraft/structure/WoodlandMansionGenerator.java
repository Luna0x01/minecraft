package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class WoodlandMansionGenerator {
	public static void addPieces(
		StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, List<WoodlandMansionGenerator.Piece> list, Random random
	) {
		WoodlandMansionGenerator.MansionParameters mansionParameters = new WoodlandMansionGenerator.MansionParameters(random);
		WoodlandMansionGenerator.LayoutGenerator layoutGenerator = new WoodlandMansionGenerator.LayoutGenerator(structureManager, random);
		layoutGenerator.generate(blockPos, blockRotation, list, mansionParameters);
	}

	static class FirstFloorRoomPool extends WoodlandMansionGenerator.RoomPool {
		private FirstFloorRoomPool() {
		}

		@Override
		public String getSmallRoom(Random random) {
			return "1x1_a" + (random.nextInt(5) + 1);
		}

		@Override
		public String getSmallSecretRoom(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumFunctionalRoom(Random random, boolean bl) {
			return "1x2_a" + (random.nextInt(9) + 1);
		}

		@Override
		public String getMediumGenericRoom(Random random, boolean bl) {
			return "1x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String getMediumSecretRoom(Random random) {
			return "1x2_s" + (random.nextInt(2) + 1);
		}

		@Override
		public String getBigRoom(Random random) {
			return "2x2_a" + (random.nextInt(4) + 1);
		}

		@Override
		public String getBigSecretRoom(Random random) {
			return "2x2_s1";
		}
	}

	static class GenerationPiece {
		public BlockRotation rotation;
		public BlockPos position;
		public String template;

		private GenerationPiece() {
		}
	}

	static class LayoutGenerator {
		private final StructureManager manager;
		private final Random random;
		private int field_15446;
		private int field_15445;

		public LayoutGenerator(StructureManager structureManager, Random random) {
			this.manager = structureManager;
			this.random = random;
		}

		public void generate(
			BlockPos blockPos, BlockRotation blockRotation, List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.MansionParameters mansionParameters
		) {
			WoodlandMansionGenerator.GenerationPiece generationPiece = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece.position = blockPos;
			generationPiece.rotation = blockRotation;
			generationPiece.template = "wall_flat";
			WoodlandMansionGenerator.GenerationPiece generationPiece2 = new WoodlandMansionGenerator.GenerationPiece();
			this.addEntrance(list, generationPiece);
			generationPiece2.position = generationPiece.position.up(8);
			generationPiece2.rotation = generationPiece.rotation;
			generationPiece2.template = "wall_window";
			if (!list.isEmpty()) {
			}

			WoodlandMansionGenerator.class_3478 lv = mansionParameters.field_15440;
			WoodlandMansionGenerator.class_3478 lv2 = mansionParameters.field_15439;
			this.field_15446 = mansionParameters.field_15442 + 1;
			this.field_15445 = mansionParameters.field_15441 + 1;
			int i = mansionParameters.field_15442 + 1;
			int j = mansionParameters.field_15441;
			this.addRoof(list, generationPiece, lv, Direction.field_11035, this.field_15446, this.field_15445, i, j);
			this.addRoof(list, generationPiece2, lv, Direction.field_11035, this.field_15446, this.field_15445, i, j);
			WoodlandMansionGenerator.GenerationPiece generationPiece3 = new WoodlandMansionGenerator.GenerationPiece();
			generationPiece3.position = generationPiece.position.up(19);
			generationPiece3.rotation = generationPiece.rotation;
			generationPiece3.template = "wall_window";
			boolean bl = false;

			for (int k = 0; k < lv2.field_15453 && !bl; k++) {
				for (int l = lv2.field_15454 - 1; l >= 0 && !bl; l--) {
					if (WoodlandMansionGenerator.MansionParameters.method_15047(lv2, l, k)) {
						generationPiece3.position = generationPiece3.position.offset(blockRotation.rotate(Direction.field_11035), 8 + (k - this.field_15445) * 8);
						generationPiece3.position = generationPiece3.position.offset(blockRotation.rotate(Direction.field_11034), (l - this.field_15446) * 8);
						this.method_15052(list, generationPiece3);
						this.addRoof(list, generationPiece3, lv2, Direction.field_11035, l, k, l, k);
						bl = true;
					}
				}
			}

			this.method_15055(list, blockPos.up(16), blockRotation, lv, lv2);
			this.method_15055(list, blockPos.up(27), blockRotation, lv2, null);
			if (!list.isEmpty()) {
			}

			WoodlandMansionGenerator.RoomPool[] roomPools = new WoodlandMansionGenerator.RoomPool[]{
				new WoodlandMansionGenerator.FirstFloorRoomPool(), new WoodlandMansionGenerator.SecondFloorRoomPool(), new WoodlandMansionGenerator.ThirdFloorRoomPool()
			};

			for (int m = 0; m < 3; m++) {
				BlockPos blockPos2 = blockPos.up(8 * m + (m == 2 ? 3 : 0));
				WoodlandMansionGenerator.class_3478 lv3 = mansionParameters.field_15443[m];
				WoodlandMansionGenerator.class_3478 lv4 = m == 2 ? lv2 : lv;
				String string = m == 0 ? "carpet_south_1" : "carpet_south_2";
				String string2 = m == 0 ? "carpet_west_1" : "carpet_west_2";

				for (int n = 0; n < lv4.field_15453; n++) {
					for (int o = 0; o < lv4.field_15454; o++) {
						if (lv4.method_15066(o, n) == 1) {
							BlockPos blockPos3 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 8 + (n - this.field_15445) * 8);
							blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11034), (o - this.field_15446) * 8);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "corridor_floor", blockPos3, blockRotation));
							if (lv4.method_15066(o, n - 1) == 1 || (lv3.method_15066(o, n - 1) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(this.manager, "carpet_north", blockPos3.offset(blockRotation.rotate(Direction.field_11034), 1).up(), blockRotation)
								);
							}

							if (lv4.method_15066(o + 1, n) == 1 || (lv3.method_15066(o + 1, n) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.manager,
										"carpet_east",
										blockPos3.offset(blockRotation.rotate(Direction.field_11035), 1).offset(blockRotation.rotate(Direction.field_11034), 5).up(),
										blockRotation
									)
								);
							}

							if (lv4.method_15066(o, n + 1) == 1 || (lv3.method_15066(o, n + 1) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.manager,
										string,
										blockPos3.offset(blockRotation.rotate(Direction.field_11035), 5).offset(blockRotation.rotate(Direction.field_11039), 1),
										blockRotation
									)
								);
							}

							if (lv4.method_15066(o - 1, n) == 1 || (lv3.method_15066(o - 1, n) & 8388608) == 8388608) {
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.manager,
										string2,
										blockPos3.offset(blockRotation.rotate(Direction.field_11039), 1).offset(blockRotation.rotate(Direction.field_11043), 1),
										blockRotation
									)
								);
							}
						}
					}
				}

				String string3 = m == 0 ? "indoors_wall_1" : "indoors_wall_2";
				String string4 = m == 0 ? "indoors_door_1" : "indoors_door_2";
				List<Direction> list2 = Lists.newArrayList();

				for (int p = 0; p < lv4.field_15453; p++) {
					for (int q = 0; q < lv4.field_15454; q++) {
						boolean bl2 = m == 2 && lv4.method_15066(q, p) == 3;
						if (lv4.method_15066(q, p) == 2 || bl2) {
							int r = lv3.method_15066(q, p);
							int s = r & 983040;
							int t = r & 65535;
							bl2 = bl2 && (r & 8388608) == 8388608;
							list2.clear();
							if ((r & 2097152) == 2097152) {
								for (Direction direction : Direction.Type.field_11062) {
									if (lv4.method_15066(q + direction.getOffsetX(), p + direction.getOffsetZ()) == 1) {
										list2.add(direction);
									}
								}
							}

							Direction direction2 = null;
							if (!list2.isEmpty()) {
								direction2 = (Direction)list2.get(this.random.nextInt(list2.size()));
							} else if ((r & 1048576) == 1048576) {
								direction2 = Direction.field_11036;
							}

							BlockPos blockPos4 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), 8 + (p - this.field_15445) * 8);
							blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.field_11034), -1 + (q - this.field_15446) * 8);
							if (WoodlandMansionGenerator.MansionParameters.method_15047(lv4, q - 1, p) && !mansionParameters.method_15039(lv4, q - 1, p, m, t)) {
								list.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.field_11039 ? string4 : string3, blockPos4, blockRotation));
							}

							if (lv4.method_15066(q + 1, p) == 1 && !bl2) {
								BlockPos blockPos5 = blockPos4.offset(blockRotation.rotate(Direction.field_11034), 8);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.field_11034 ? string4 : string3, blockPos5, blockRotation));
							}

							if (WoodlandMansionGenerator.MansionParameters.method_15047(lv4, q, p + 1) && !mansionParameters.method_15039(lv4, q, p + 1, m, t)) {
								BlockPos blockPos6 = blockPos4.offset(blockRotation.rotate(Direction.field_11035), 7);
								blockPos6 = blockPos6.offset(blockRotation.rotate(Direction.field_11034), 7);
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, direction2 == Direction.field_11035 ? string4 : string3, blockPos6, blockRotation.rotate(BlockRotation.field_11463)
									)
								);
							}

							if (lv4.method_15066(q, p - 1) == 1 && !bl2) {
								BlockPos blockPos7 = blockPos4.offset(blockRotation.rotate(Direction.field_11043), 1);
								blockPos7 = blockPos7.offset(blockRotation.rotate(Direction.field_11034), 7);
								list.add(
									new WoodlandMansionGenerator.Piece(
										this.manager, direction2 == Direction.field_11043 ? string4 : string3, blockPos7, blockRotation.rotate(BlockRotation.field_11463)
									)
								);
							}

							if (s == 65536) {
								this.addSmallRoom(list, blockPos4, blockRotation, direction2, roomPools[m]);
							} else if (s == 131072 && direction2 != null) {
								Direction direction3 = mansionParameters.method_15040(lv4, q, p, m, t);
								boolean bl3 = (r & 4194304) == 4194304;
								this.addMediumRoom(list, blockPos4, blockRotation, direction3, direction2, roomPools[m], bl3);
							} else if (s == 262144 && direction2 != null && direction2 != Direction.field_11036) {
								Direction direction4 = direction2.rotateYClockwise();
								if (!mansionParameters.method_15039(lv4, q + direction4.getOffsetX(), p + direction4.getOffsetZ(), m, t)) {
									direction4 = direction4.getOpposite();
								}

								this.addBigRoom(list, blockPos4, blockRotation, direction4, direction2, roomPools[m]);
							} else if (s == 262144 && direction2 == Direction.field_11036) {
								this.addBigSecretRoom(list, blockPos4, blockRotation, roomPools[m]);
							}
						}
					}
				}
			}
		}

		private void addRoof(
			List<WoodlandMansionGenerator.Piece> list,
			WoodlandMansionGenerator.GenerationPiece generationPiece,
			WoodlandMansionGenerator.class_3478 arg,
			Direction direction,
			int i,
			int j,
			int k,
			int l
		) {
			int m = i;
			int n = j;
			Direction direction2 = direction;

			do {
				if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, m + direction.getOffsetX(), n + direction.getOffsetZ())) {
					this.method_15058(list, generationPiece);
					direction = direction.rotateYClockwise();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(list, generationPiece);
					}
				} else if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, m + direction.getOffsetX(), n + direction.getOffsetZ())
					&& WoodlandMansionGenerator.MansionParameters.method_15047(
						arg,
						m + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(),
						n + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ()
					)) {
					this.method_15060(list, generationPiece);
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					direction = direction.rotateYCounterclockwise();
				} else {
					m += direction.getOffsetX();
					n += direction.getOffsetZ();
					if (m != k || n != l || direction2 != direction) {
						this.method_15052(list, generationPiece);
					}
				}
			} while (m != k || n != l || direction2 != direction);
		}

		private void method_15055(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			WoodlandMansionGenerator.class_3478 arg,
			@Nullable WoodlandMansionGenerator.class_3478 arg2
		) {
			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					BlockPos blockPos16 = blockPos.offset(blockRotation.rotate(Direction.field_11035), 8 + (i - this.field_15445) * 8);
					blockPos16 = blockPos16.offset(blockRotation.rotate(Direction.field_11034), (j - this.field_15446) * 8);
					boolean bl = arg2 != null && WoodlandMansionGenerator.MansionParameters.method_15047(arg2, j, i);
					if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, j, i) && !bl) {
						list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof", blockPos16.up(3), blockRotation));
						if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, j + 1, i)) {
							BlockPos blockPos3 = blockPos16.offset(blockRotation.rotate(Direction.field_11034), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos3, blockRotation));
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, j - 1, i)) {
							BlockPos blockPos4 = blockPos16.offset(blockRotation.rotate(Direction.field_11034), 0);
							blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.field_11035), 7);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos4, blockRotation.rotate(BlockRotation.field_11464)));
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, j, i - 1)) {
							BlockPos blockPos5 = blockPos16.offset(blockRotation.rotate(Direction.field_11039), 1);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos5, blockRotation.rotate(BlockRotation.field_11465)));
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, j, i + 1)) {
							BlockPos blockPos6 = blockPos16.offset(blockRotation.rotate(Direction.field_11034), 6);
							blockPos6 = blockPos6.offset(blockRotation.rotate(Direction.field_11035), 6);
							list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos6, blockRotation.rotate(BlockRotation.field_11463)));
						}
					}
				}
			}

			if (arg2 != null) {
				for (int k = 0; k < arg.field_15453; k++) {
					for (int l = 0; l < arg.field_15454; l++) {
						BlockPos var17 = blockPos.offset(blockRotation.rotate(Direction.field_11035), 8 + (k - this.field_15445) * 8);
						var17 = var17.offset(blockRotation.rotate(Direction.field_11034), (l - this.field_15446) * 8);
						boolean bl2 = WoodlandMansionGenerator.MansionParameters.method_15047(arg2, l, k);
						if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k) && bl2) {
							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l + 1, k)) {
								BlockPos blockPos8 = var17.offset(blockRotation.rotate(Direction.field_11034), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos8, blockRotation));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l - 1, k)) {
								BlockPos blockPos9 = var17.offset(blockRotation.rotate(Direction.field_11039), 1);
								blockPos9 = blockPos9.offset(blockRotation.rotate(Direction.field_11035), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos9, blockRotation.rotate(BlockRotation.field_11464)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k - 1)) {
								BlockPos blockPos10 = var17.offset(blockRotation.rotate(Direction.field_11039), 0);
								blockPos10 = blockPos10.offset(blockRotation.rotate(Direction.field_11043), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos10, blockRotation.rotate(BlockRotation.field_11465)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k + 1)) {
								BlockPos blockPos11 = var17.offset(blockRotation.rotate(Direction.field_11034), 6);
								blockPos11 = blockPos11.offset(blockRotation.rotate(Direction.field_11035), 7);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos11, blockRotation.rotate(BlockRotation.field_11463)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l + 1, k)) {
								if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k - 1)) {
									BlockPos blockPos12 = var17.offset(blockRotation.rotate(Direction.field_11034), 7);
									blockPos12 = blockPos12.offset(blockRotation.rotate(Direction.field_11043), 2);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos12, blockRotation));
								}

								if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k + 1)) {
									BlockPos blockPos13 = var17.offset(blockRotation.rotate(Direction.field_11034), 8);
									blockPos13 = blockPos13.offset(blockRotation.rotate(Direction.field_11035), 7);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos13, blockRotation.rotate(BlockRotation.field_11463)));
								}
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l - 1, k)) {
								if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k - 1)) {
									BlockPos blockPos14 = var17.offset(blockRotation.rotate(Direction.field_11039), 2);
									blockPos14 = blockPos14.offset(blockRotation.rotate(Direction.field_11043), 1);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos14, blockRotation.rotate(BlockRotation.field_11465)));
								}

								if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, l, k + 1)) {
									BlockPos blockPos15 = var17.offset(blockRotation.rotate(Direction.field_11039), 1);
									blockPos15 = blockPos15.offset(blockRotation.rotate(Direction.field_11035), 8);
									list.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos15, blockRotation.rotate(BlockRotation.field_11464)));
								}
							}
						}
					}
				}
			}

			for (int m = 0; m < arg.field_15453; m++) {
				for (int n = 0; n < arg.field_15454; n++) {
					BlockPos var19 = blockPos.offset(blockRotation.rotate(Direction.field_11035), 8 + (m - this.field_15445) * 8);
					var19 = var19.offset(blockRotation.rotate(Direction.field_11034), (n - this.field_15446) * 8);
					boolean bl3 = arg2 != null && WoodlandMansionGenerator.MansionParameters.method_15047(arg2, n, m);
					if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, n, m) && !bl3) {
						if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, n + 1, m)) {
							BlockPos blockPos17 = var19.offset(blockRotation.rotate(Direction.field_11034), 6);
							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, n, m + 1)) {
								BlockPos blockPos18 = blockPos17.offset(blockRotation.rotate(Direction.field_11035), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos18, blockRotation));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, n + 1, m + 1)) {
								BlockPos blockPos19 = blockPos17.offset(blockRotation.rotate(Direction.field_11035), 5);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos19, blockRotation));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, n, m - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos17, blockRotation.rotate(BlockRotation.field_11465)));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, n + 1, m - 1)) {
								BlockPos blockPos20 = var19.offset(blockRotation.rotate(Direction.field_11034), 9);
								blockPos20 = blockPos20.offset(blockRotation.rotate(Direction.field_11043), 2);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos20, blockRotation.rotate(BlockRotation.field_11463)));
							}
						}

						if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, n - 1, m)) {
							BlockPos blockPos21 = var19.offset(blockRotation.rotate(Direction.field_11034), 0);
							blockPos21 = blockPos21.offset(blockRotation.rotate(Direction.field_11035), 0);
							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, n, m + 1)) {
								BlockPos blockPos22 = blockPos21.offset(blockRotation.rotate(Direction.field_11035), 6);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos22, blockRotation.rotate(BlockRotation.field_11463)));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, n - 1, m + 1)) {
								BlockPos blockPos23 = blockPos21.offset(blockRotation.rotate(Direction.field_11035), 8);
								blockPos23 = blockPos23.offset(blockRotation.rotate(Direction.field_11039), 3);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos23, blockRotation.rotate(BlockRotation.field_11465)));
							}

							if (!WoodlandMansionGenerator.MansionParameters.method_15047(arg, n, m - 1)) {
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos21, blockRotation.rotate(BlockRotation.field_11464)));
							} else if (WoodlandMansionGenerator.MansionParameters.method_15047(arg, n - 1, m - 1)) {
								BlockPos blockPos24 = blockPos21.offset(blockRotation.rotate(Direction.field_11035), 1);
								list.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos24, blockRotation.rotate(BlockRotation.field_11464)));
							}
						}
					}
				}
			}
		}

		private void addEntrance(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			Direction direction = generationPiece.rotation.rotate(Direction.field_11039);
			list.add(new WoodlandMansionGenerator.Piece(this.manager, "entrance", generationPiece.position.offset(direction, 9), generationPiece.rotation));
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), 16);
		}

		private void method_15052(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			list.add(
				new WoodlandMansionGenerator.Piece(
					this.manager,
					generationPiece.template,
					generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11034), 7),
					generationPiece.rotation
				)
			);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), 8);
		}

		private void method_15058(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), -1);
			list.add(new WoodlandMansionGenerator.Piece(this.manager, "wall_corner", generationPiece.position, generationPiece.rotation));
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), -7);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11039), -6);
			generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.field_11463);
		}

		private void method_15060(List<WoodlandMansionGenerator.Piece> list, WoodlandMansionGenerator.GenerationPiece generationPiece) {
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11035), 6);
			generationPiece.position = generationPiece.position.offset(generationPiece.rotation.rotate(Direction.field_11034), 8);
			generationPiece.rotation = generationPiece.rotation.rotate(BlockRotation.field_11465);
		}

		private void addSmallRoom(
			List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, BlockRotation blockRotation, Direction direction, WoodlandMansionGenerator.RoomPool roomPool
		) {
			BlockRotation blockRotation2 = BlockRotation.field_11467;
			String string = roomPool.getSmallRoom(this.random);
			if (direction != Direction.field_11034) {
				if (direction == Direction.field_11043) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.field_11465);
				} else if (direction == Direction.field_11039) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.field_11464);
				} else if (direction == Direction.field_11035) {
					blockRotation2 = blockRotation2.rotate(BlockRotation.field_11463);
				} else {
					string = roomPool.getSmallSecretRoom(this.random);
				}
			}

			BlockPos blockPos2 = Structure.method_15162(new BlockPos(1, 0, 0), BlockMirror.field_11302, blockRotation2, 7, 7);
			blockRotation2 = blockRotation2.rotate(blockRotation);
			blockPos2 = blockPos2.rotate(blockRotation);
			BlockPos blockPos3 = blockPos.add(blockPos2.getX(), 0, blockPos2.getZ());
			list.add(new WoodlandMansionGenerator.Piece(this.manager, string, blockPos3, blockRotation2));
		}

		private void addMediumRoom(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.RoomPool roomPool,
			boolean bl
		) {
			if (direction2 == Direction.field_11034 && direction == Direction.field_11035) {
				BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				list.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos2, blockRotation));
			} else if (direction2 == Direction.field_11034 && direction == Direction.field_11043) {
				BlockPos blockPos3 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos3, blockRotation, BlockMirror.field_11300)
				);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11043) {
				BlockPos blockPos4 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos4, blockRotation.rotate(BlockRotation.field_11464)
					)
				);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11035) {
				BlockPos blockPos5 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				list.add(
					new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos5, blockRotation, BlockMirror.field_11301)
				);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11034) {
				BlockPos blockPos6 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos6, blockRotation.rotate(BlockRotation.field_11463), BlockMirror.field_11300
					)
				);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11039) {
				BlockPos blockPos7 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos7, blockRotation.rotate(BlockRotation.field_11463)
					)
				);
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11039) {
				BlockPos blockPos8 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				blockPos8 = blockPos8.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos8, blockRotation.rotate(BlockRotation.field_11463), BlockMirror.field_11301
					)
				);
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11034) {
				BlockPos blockPos9 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos9 = blockPos9.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumFunctionalRoom(this.random, bl), blockPos9, blockRotation.rotate(BlockRotation.field_11465)
					)
				);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11043) {
				BlockPos blockPos10 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos10 = blockPos10.offset(blockRotation.rotate(Direction.field_11043), 8);
				list.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumGenericRoom(this.random, bl), blockPos10, blockRotation));
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11035) {
				BlockPos blockPos11 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 7);
				blockPos11 = blockPos11.offset(blockRotation.rotate(Direction.field_11035), 14);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumGenericRoom(this.random, bl), blockPos11, blockRotation.rotate(BlockRotation.field_11464)
					)
				);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11034) {
				BlockPos blockPos12 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 15);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumGenericRoom(this.random, bl), blockPos12, blockRotation.rotate(BlockRotation.field_11463)
					)
				);
			} else if (direction2 == Direction.field_11034 && direction == Direction.field_11039) {
				BlockPos blockPos13 = blockPos.offset(blockRotation.rotate(Direction.field_11039), 7);
				blockPos13 = blockPos13.offset(blockRotation.rotate(Direction.field_11035), 6);
				list.add(
					new WoodlandMansionGenerator.Piece(
						this.manager, roomPool.getMediumGenericRoom(this.random, bl), blockPos13, blockRotation.rotate(BlockRotation.field_11465)
					)
				);
			} else if (direction2 == Direction.field_11036 && direction == Direction.field_11034) {
				BlockPos blockPos14 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 15);
				list.add(
					new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos14, blockRotation.rotate(BlockRotation.field_11463))
				);
			} else if (direction2 == Direction.field_11036 && direction == Direction.field_11035) {
				BlockPos blockPos15 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
				blockPos15 = blockPos15.offset(blockRotation.rotate(Direction.field_11043), 0);
				list.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getMediumSecretRoom(this.random), blockPos15, blockRotation));
			}
		}

		private void addBigRoom(
			List<WoodlandMansionGenerator.Piece> list,
			BlockPos blockPos,
			BlockRotation blockRotation,
			Direction direction,
			Direction direction2,
			WoodlandMansionGenerator.RoomPool roomPool
		) {
			int i = 0;
			int j = 0;
			BlockRotation blockRotation2 = blockRotation;
			BlockMirror blockMirror = BlockMirror.field_11302;
			if (direction2 == Direction.field_11034 && direction == Direction.field_11035) {
				i = -7;
			} else if (direction2 == Direction.field_11034 && direction == Direction.field_11043) {
				i = -7;
				j = 6;
				blockMirror = BlockMirror.field_11300;
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11034) {
				i = 1;
				j = 14;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11465);
			} else if (direction2 == Direction.field_11043 && direction == Direction.field_11039) {
				i = 7;
				j = 14;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11465);
				blockMirror = BlockMirror.field_11300;
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11039) {
				i = 7;
				j = -8;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11463);
			} else if (direction2 == Direction.field_11035 && direction == Direction.field_11034) {
				i = 1;
				j = -8;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11463);
				blockMirror = BlockMirror.field_11300;
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11043) {
				i = 15;
				j = 6;
				blockRotation2 = blockRotation.rotate(BlockRotation.field_11464);
			} else if (direction2 == Direction.field_11039 && direction == Direction.field_11035) {
				i = 15;
				blockMirror = BlockMirror.field_11301;
			}

			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), i);
			blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.field_11035), j);
			list.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getBigRoom(this.random), blockPos2, blockRotation2, blockMirror));
		}

		private void addBigSecretRoom(
			List<WoodlandMansionGenerator.Piece> list, BlockPos blockPos, BlockRotation blockRotation, WoodlandMansionGenerator.RoomPool roomPool
		) {
			BlockPos blockPos2 = blockPos.offset(blockRotation.rotate(Direction.field_11034), 1);
			list.add(new WoodlandMansionGenerator.Piece(this.manager, roomPool.getBigSecretRoom(this.random), blockPos2, blockRotation, BlockMirror.field_11302));
		}
	}

	static class MansionParameters {
		private final Random random;
		private final WoodlandMansionGenerator.class_3478 field_15440;
		private final WoodlandMansionGenerator.class_3478 field_15439;
		private final WoodlandMansionGenerator.class_3478[] field_15443;
		private final int field_15442;
		private final int field_15441;

		public MansionParameters(Random random) {
			this.random = random;
			int i = 11;
			this.field_15442 = 7;
			this.field_15441 = 4;
			this.field_15440 = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.field_15440.method_15062(this.field_15442, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 3);
			this.field_15440.method_15062(this.field_15442 - 1, this.field_15441, this.field_15442 - 1, this.field_15441 + 1, 2);
			this.field_15440.method_15062(this.field_15442 + 2, this.field_15441 - 2, this.field_15442 + 3, this.field_15441 + 3, 5);
			this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 - 2, this.field_15442 + 1, this.field_15441 - 1, 1);
			this.field_15440.method_15062(this.field_15442 + 1, this.field_15441 + 2, this.field_15442 + 1, this.field_15441 + 3, 1);
			this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 - 1, 1);
			this.field_15440.method_15065(this.field_15442 - 1, this.field_15441 + 2, 1);
			this.field_15440.method_15062(0, 0, 11, 1, 5);
			this.field_15440.method_15062(0, 9, 11, 11, 5);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 - 2, Direction.field_11039, 6);
			this.method_15045(this.field_15440, this.field_15442, this.field_15441 + 3, Direction.field_11039, 6);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 - 1, Direction.field_11039, 3);
			this.method_15045(this.field_15440, this.field_15442 - 2, this.field_15441 + 2, Direction.field_11039, 3);

			while (this.method_15046(this.field_15440)) {
			}

			this.field_15443 = new WoodlandMansionGenerator.class_3478[3];
			this.field_15443[0] = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.field_15443[1] = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.field_15443[2] = new WoodlandMansionGenerator.class_3478(11, 11, 5);
			this.method_15042(this.field_15440, this.field_15443[0]);
			this.method_15042(this.field_15440, this.field_15443[1]);
			this.field_15443[0].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15443[1].method_15062(this.field_15442 + 1, this.field_15441, this.field_15442 + 1, this.field_15441 + 1, 8388608);
			this.field_15439 = new WoodlandMansionGenerator.class_3478(this.field_15440.field_15454, this.field_15440.field_15453, 5);
			this.method_15048();
			this.method_15042(this.field_15439, this.field_15443[2]);
		}

		public static boolean method_15047(WoodlandMansionGenerator.class_3478 arg, int i, int j) {
			int k = arg.method_15066(i, j);
			return k == 1 || k == 2 || k == 3 || k == 4;
		}

		public boolean method_15039(WoodlandMansionGenerator.class_3478 arg, int i, int j, int k, int l) {
			return (this.field_15443[k].method_15066(i, j) & 65535) == l;
		}

		@Nullable
		public Direction method_15040(WoodlandMansionGenerator.class_3478 arg, int i, int j, int k, int l) {
			for (Direction direction : Direction.Type.field_11062) {
				if (this.method_15039(arg, i + direction.getOffsetX(), j + direction.getOffsetZ(), k, l)) {
					return direction;
				}
			}

			return null;
		}

		private void method_15045(WoodlandMansionGenerator.class_3478 arg, int i, int j, Direction direction, int k) {
			if (k > 0) {
				arg.method_15065(i, j, 1);
				arg.method_15061(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);

				for (int l = 0; l < 8; l++) {
					Direction direction2 = Direction.fromHorizontal(this.random.nextInt(4));
					if (direction2 != direction.getOpposite() && (direction2 != Direction.field_11034 || !this.random.nextBoolean())) {
						int m = i + direction.getOffsetX();
						int n = j + direction.getOffsetZ();
						if (arg.method_15066(m + direction2.getOffsetX(), n + direction2.getOffsetZ()) == 0
							&& arg.method_15066(m + direction2.getOffsetX() * 2, n + direction2.getOffsetZ() * 2) == 0) {
							this.method_15045(arg, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, k - 1);
							break;
						}
					}
				}

				Direction direction3 = direction.rotateYClockwise();
				Direction direction4 = direction.rotateYCounterclockwise();
				arg.method_15061(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction4.getOffsetX(), j + direction4.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction.getOffsetX() + direction4.getOffsetX(), j + direction.getOffsetZ() + direction4.getOffsetZ(), 0, 2);
				arg.method_15061(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
				arg.method_15061(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
				arg.method_15061(i + direction4.getOffsetX() * 2, j + direction4.getOffsetZ() * 2, 0, 2);
			}
		}

		private boolean method_15046(WoodlandMansionGenerator.class_3478 arg) {
			boolean bl = false;

			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					if (arg.method_15066(j, i) == 0) {
						int k = 0;
						k += method_15047(arg, j + 1, i) ? 1 : 0;
						k += method_15047(arg, j - 1, i) ? 1 : 0;
						k += method_15047(arg, j, i + 1) ? 1 : 0;
						k += method_15047(arg, j, i - 1) ? 1 : 0;
						if (k >= 3) {
							arg.method_15065(j, i, 2);
							bl = true;
						} else if (k == 2) {
							int l = 0;
							l += method_15047(arg, j + 1, i + 1) ? 1 : 0;
							l += method_15047(arg, j - 1, i + 1) ? 1 : 0;
							l += method_15047(arg, j + 1, i - 1) ? 1 : 0;
							l += method_15047(arg, j - 1, i - 1) ? 1 : 0;
							if (l <= 1) {
								arg.method_15065(j, i, 2);
								bl = true;
							}
						}
					}
				}
			}

			return bl;
		}

		private void method_15048() {
			List<Pair<Integer, Integer>> list = Lists.newArrayList();
			WoodlandMansionGenerator.class_3478 lv = this.field_15443[1];

			for (int i = 0; i < this.field_15439.field_15453; i++) {
				for (int j = 0; j < this.field_15439.field_15454; j++) {
					int k = lv.method_15066(j, i);
					int l = k & 983040;
					if (l == 131072 && (k & 2097152) == 2097152) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			if (list.isEmpty()) {
				this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
			} else {
				Pair<Integer, Integer> pair = (Pair<Integer, Integer>)list.get(this.random.nextInt(list.size()));
				int m = lv.method_15066(pair.getLeft(), pair.getRight());
				lv.method_15065(pair.getLeft(), pair.getRight(), m | 4194304);
				Direction direction = this.method_15040(this.field_15440, pair.getLeft(), pair.getRight(), 1, m & 65535);
				int n = pair.getLeft() + direction.getOffsetX();
				int o = pair.getRight() + direction.getOffsetZ();

				for (int p = 0; p < this.field_15439.field_15453; p++) {
					for (int q = 0; q < this.field_15439.field_15454; q++) {
						if (!method_15047(this.field_15440, q, p)) {
							this.field_15439.method_15065(q, p, 5);
						} else if (q == pair.getLeft() && p == pair.getRight()) {
							this.field_15439.method_15065(q, p, 3);
						} else if (q == n && p == o) {
							this.field_15439.method_15065(q, p, 3);
							this.field_15443[2].method_15065(q, p, 8388608);
						}
					}
				}

				List<Direction> list2 = Lists.newArrayList();

				for (Direction direction2 : Direction.Type.field_11062) {
					if (this.field_15439.method_15066(n + direction2.getOffsetX(), o + direction2.getOffsetZ()) == 0) {
						list2.add(direction2);
					}
				}

				if (list2.isEmpty()) {
					this.field_15439.method_15062(0, 0, this.field_15439.field_15454, this.field_15439.field_15453, 5);
					lv.method_15065(pair.getLeft(), pair.getRight(), m);
				} else {
					Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
					this.method_15045(this.field_15439, n + direction3.getOffsetX(), o + direction3.getOffsetZ(), direction3, 4);

					while (this.method_15046(this.field_15439)) {
					}
				}
			}
		}

		private void method_15042(WoodlandMansionGenerator.class_3478 arg, WoodlandMansionGenerator.class_3478 arg2) {
			List<Pair<Integer, Integer>> list = Lists.newArrayList();

			for (int i = 0; i < arg.field_15453; i++) {
				for (int j = 0; j < arg.field_15454; j++) {
					if (arg.method_15066(j, i) == 2) {
						list.add(new Pair<>(j, i));
					}
				}
			}

			Collections.shuffle(list, this.random);
			int k = 10;

			for (Pair<Integer, Integer> pair : list) {
				int l = pair.getLeft();
				int m = pair.getRight();
				if (arg2.method_15066(l, m) == 0) {
					int n = l;
					int o = l;
					int p = m;
					int q = m;
					int r = 65536;
					if (arg2.method_15066(l + 1, m) == 0
						&& arg2.method_15066(l, m + 1) == 0
						&& arg2.method_15066(l + 1, m + 1) == 0
						&& arg.method_15066(l + 1, m) == 2
						&& arg.method_15066(l, m + 1) == 2
						&& arg.method_15066(l + 1, m + 1) == 2) {
						o = l + 1;
						q = m + 1;
						r = 262144;
					} else if (arg2.method_15066(l - 1, m) == 0
						&& arg2.method_15066(l, m + 1) == 0
						&& arg2.method_15066(l - 1, m + 1) == 0
						&& arg.method_15066(l - 1, m) == 2
						&& arg.method_15066(l, m + 1) == 2
						&& arg.method_15066(l - 1, m + 1) == 2) {
						n = l - 1;
						q = m + 1;
						r = 262144;
					} else if (arg2.method_15066(l - 1, m) == 0
						&& arg2.method_15066(l, m - 1) == 0
						&& arg2.method_15066(l - 1, m - 1) == 0
						&& arg.method_15066(l - 1, m) == 2
						&& arg.method_15066(l, m - 1) == 2
						&& arg.method_15066(l - 1, m - 1) == 2) {
						n = l - 1;
						p = m - 1;
						r = 262144;
					} else if (arg2.method_15066(l + 1, m) == 0 && arg.method_15066(l + 1, m) == 2) {
						o = l + 1;
						r = 131072;
					} else if (arg2.method_15066(l, m + 1) == 0 && arg.method_15066(l, m + 1) == 2) {
						q = m + 1;
						r = 131072;
					} else if (arg2.method_15066(l - 1, m) == 0 && arg.method_15066(l - 1, m) == 2) {
						n = l - 1;
						r = 131072;
					} else if (arg2.method_15066(l, m - 1) == 0 && arg.method_15066(l, m - 1) == 2) {
						p = m - 1;
						r = 131072;
					}

					int s = this.random.nextBoolean() ? n : o;
					int t = this.random.nextBoolean() ? p : q;
					int u = 2097152;
					if (!arg.method_15067(s, t, 1)) {
						s = s == n ? o : n;
						t = t == p ? q : p;
						if (!arg.method_15067(s, t, 1)) {
							t = t == p ? q : p;
							if (!arg.method_15067(s, t, 1)) {
								s = s == n ? o : n;
								t = t == p ? q : p;
								if (!arg.method_15067(s, t, 1)) {
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
								arg2.method_15065(w, v, 1048576 | u | r | k);
							} else {
								arg2.method_15065(w, v, r | k);
							}
						}
					}

					k++;
				}
			}
		}
	}

	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final BlockMirror mirror;

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation) {
			this(structureManager, string, blockPos, blockRotation, BlockMirror.field_11302);
		}

		public Piece(StructureManager structureManager, String string, BlockPos blockPos, BlockRotation blockRotation, BlockMirror blockMirror) {
			super(StructurePieceType.WOODLAND_MANSION, 0);
			this.template = string;
			this.pos = blockPos;
			this.rotation = blockRotation;
			this.mirror = blockMirror;
			this.method_15068(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.WOODLAND_MANSION, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.mirror = BlockMirror.valueOf(compoundTag.getString("Mi"));
			this.method_15068(structureManager);
		}

		private void method_15068(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(new Identifier("woodland_mansion/" + this.template));
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setIgnoreEntities(true)
				.setRotation(this.rotation)
				.setMirrored(this.mirror)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template);
			compoundTag.putString("Rot", this.placementData.getRotation().name());
			compoundTag.putString("Mi", this.placementData.getMirror().name());
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox) {
			if (string.startsWith("Chest")) {
				BlockRotation blockRotation = this.placementData.getRotation();
				BlockState blockState = Blocks.field_10034.getDefaultState();
				if ("ChestWest".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.field_11039));
				} else if ("ChestEast".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.field_11034));
				} else if ("ChestSouth".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.field_11035));
				} else if ("ChestNorth".equals(string)) {
					blockState = blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.field_11043));
				}

				this.addChest(iWorld, blockBox, random, blockPos, LootTables.field_484, blockState);
			} else {
				IllagerEntity illagerEntity;
				switch (string) {
					case "Mage":
						illagerEntity = EntityType.field_6090.create(iWorld.getWorld());
						break;
					case "Warrior":
						illagerEntity = EntityType.field_6117.create(iWorld.getWorld());
						break;
					default:
						return;
				}

				illagerEntity.setPersistent();
				illagerEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
				illagerEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(illagerEntity)), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(illagerEntity);
				iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 2);
			}
		}
	}

	abstract static class RoomPool {
		private RoomPool() {
		}

		public abstract String getSmallRoom(Random random);

		public abstract String getSmallSecretRoom(Random random);

		public abstract String getMediumFunctionalRoom(Random random, boolean bl);

		public abstract String getMediumGenericRoom(Random random, boolean bl);

		public abstract String getMediumSecretRoom(Random random);

		public abstract String getBigRoom(Random random);

		public abstract String getBigSecretRoom(Random random);
	}

	static class SecondFloorRoomPool extends WoodlandMansionGenerator.RoomPool {
		private SecondFloorRoomPool() {
		}

		@Override
		public String getSmallRoom(Random random) {
			return "1x1_b" + (random.nextInt(4) + 1);
		}

		@Override
		public String getSmallSecretRoom(Random random) {
			return "1x1_as" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumFunctionalRoom(Random random, boolean bl) {
			return bl ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
		}

		@Override
		public String getMediumGenericRoom(Random random, boolean bl) {
			return bl ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
		}

		@Override
		public String getMediumSecretRoom(Random random) {
			return "1x2_se" + (random.nextInt(1) + 1);
		}

		@Override
		public String getBigRoom(Random random) {
			return "2x2_b" + (random.nextInt(5) + 1);
		}

		@Override
		public String getBigSecretRoom(Random random) {
			return "2x2_s1";
		}
	}

	static class ThirdFloorRoomPool extends WoodlandMansionGenerator.SecondFloorRoomPool {
		private ThirdFloorRoomPool() {
		}
	}

	static class class_3478 {
		private final int[][] field_15451;
		private final int field_15454;
		private final int field_15453;
		private final int field_15452;

		public class_3478(int i, int j, int k) {
			this.field_15454 = i;
			this.field_15453 = j;
			this.field_15452 = k;
			this.field_15451 = new int[i][j];
		}

		public void method_15065(int i, int j, int k) {
			if (i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453) {
				this.field_15451[i][j] = k;
			}
		}

		public void method_15062(int i, int j, int k, int l, int m) {
			for (int n = j; n <= l; n++) {
				for (int o = i; o <= k; o++) {
					this.method_15065(o, n, m);
				}
			}
		}

		public int method_15066(int i, int j) {
			return i >= 0 && i < this.field_15454 && j >= 0 && j < this.field_15453 ? this.field_15451[i][j] : this.field_15452;
		}

		public void method_15061(int i, int j, int k, int l) {
			if (this.method_15066(i, j) == k) {
				this.method_15065(i, j, l);
			}
		}

		public boolean method_15067(int i, int j, int k) {
			return this.method_15066(i - 1, j) == k || this.method_15066(i + 1, j) == k || this.method_15066(i, j + 1) == k || this.method_15066(i, j - 1) == k;
		}
	}
}
