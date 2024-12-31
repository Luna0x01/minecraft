package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanMonumentGenerator {
	public static class Base extends OceanMonumentGenerator.Piece {
		private OceanMonumentGenerator.class_3388 field_14464;
		private OceanMonumentGenerator.class_3388 field_14466;
		private final List<OceanMonumentGenerator.Piece> field_14465 = Lists.newArrayList();

		public Base(Random random, int i, int j, Direction direction) {
			super(StructurePieceType.OCEAN_MONUMENT_BASE, 0);
			this.setOrientation(direction);
			Direction direction2 = this.getFacing();
			if (direction2.getAxis() == Direction.Axis.field_11051) {
				this.boundingBox = new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
			} else {
				this.boundingBox = new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
			}

			List<OceanMonumentGenerator.class_3388> list = this.method_14760(random);
			this.field_14464.field_14485 = true;
			this.field_14465.add(new OceanMonumentGenerator.Entry(direction2, this.field_14464));
			this.field_14465.add(new OceanMonumentGenerator.CoreRoom(direction2, this.field_14466));
			List<OceanMonumentGenerator.class_3375> list2 = Lists.newArrayList();
			list2.add(new OceanMonumentGenerator.class_3368());
			list2.add(new OceanMonumentGenerator.class_3370());
			list2.add(new OceanMonumentGenerator.class_3371());
			list2.add(new OceanMonumentGenerator.class_3367());
			list2.add(new OceanMonumentGenerator.class_3369());
			list2.add(new OceanMonumentGenerator.class_3373());
			list2.add(new OceanMonumentGenerator.class_3372());

			for (OceanMonumentGenerator.class_3388 lv : list) {
				if (!lv.field_14485 && !lv.method_14785()) {
					for (OceanMonumentGenerator.class_3375 lv2 : list2) {
						if (lv2.method_14769(lv)) {
							this.field_14465.add(lv2.method_14768(direction2, lv, random));
							break;
						}
					}
				}
			}

			int k = this.boundingBox.minY;
			int l = this.applyXTransform(9, 22);
			int m = this.applyZTransform(9, 22);

			for (OceanMonumentGenerator.Piece piece : this.field_14465) {
				piece.getBoundingBox().offset(l, k, m);
			}

			BlockBox blockBox = BlockBox.create(
				this.applyXTransform(1, 1),
				this.applyYTransform(1),
				this.applyZTransform(1, 1),
				this.applyXTransform(23, 21),
				this.applyYTransform(8),
				this.applyZTransform(23, 21)
			);
			BlockBox blockBox2 = BlockBox.create(
				this.applyXTransform(34, 1),
				this.applyYTransform(1),
				this.applyZTransform(34, 1),
				this.applyXTransform(56, 21),
				this.applyYTransform(8),
				this.applyZTransform(56, 21)
			);
			BlockBox blockBox3 = BlockBox.create(
				this.applyXTransform(22, 22),
				this.applyYTransform(13),
				this.applyZTransform(22, 22),
				this.applyXTransform(35, 35),
				this.applyYTransform(17),
				this.applyZTransform(35, 35)
			);
			int n = random.nextInt();
			this.field_14465.add(new OceanMonumentGenerator.WingRoom(direction2, blockBox, n++));
			this.field_14465.add(new OceanMonumentGenerator.WingRoom(direction2, blockBox2, n++));
			this.field_14465.add(new OceanMonumentGenerator.Penthouse(direction2, blockBox3));
		}

		public Base(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_BASE, compoundTag);
		}

		private List<OceanMonumentGenerator.class_3388> method_14760(Random random) {
			OceanMonumentGenerator.class_3388[] lvs = new OceanMonumentGenerator.class_3388[75];

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					int k = 0;
					int l = method_14770(i, 0, j);
					lvs[l] = new OceanMonumentGenerator.class_3388(l);
				}
			}

			for (int m = 0; m < 5; m++) {
				for (int n = 0; n < 4; n++) {
					int o = 1;
					int p = method_14770(m, 1, n);
					lvs[p] = new OceanMonumentGenerator.class_3388(p);
				}
			}

			for (int q = 1; q < 4; q++) {
				for (int r = 0; r < 2; r++) {
					int s = 2;
					int t = method_14770(q, 2, r);
					lvs[t] = new OceanMonumentGenerator.class_3388(t);
				}
			}

			this.field_14464 = lvs[field_14469];

			for (int u = 0; u < 5; u++) {
				for (int v = 0; v < 5; v++) {
					for (int w = 0; w < 3; w++) {
						int x = method_14770(u, w, v);
						if (lvs[x] != null) {
							for (Direction direction : Direction.values()) {
								int y = u + direction.getOffsetX();
								int z = w + direction.getOffsetY();
								int aa = v + direction.getOffsetZ();
								if (y >= 0 && y < 5 && aa >= 0 && aa < 5 && z >= 0 && z < 3) {
									int ab = method_14770(y, z, aa);
									if (lvs[ab] != null) {
										if (aa == v) {
											lvs[x].method_14786(direction, lvs[ab]);
										} else {
											lvs[x].method_14786(direction.getOpposite(), lvs[ab]);
										}
									}
								}
							}
						}
					}
				}
			}

			OceanMonumentGenerator.class_3388 lv = new OceanMonumentGenerator.class_3388(1003);
			OceanMonumentGenerator.class_3388 lv2 = new OceanMonumentGenerator.class_3388(1001);
			OceanMonumentGenerator.class_3388 lv3 = new OceanMonumentGenerator.class_3388(1002);
			lvs[field_14468].method_14786(Direction.field_11036, lv);
			lvs[field_14478].method_14786(Direction.field_11035, lv2);
			lvs[field_14477].method_14786(Direction.field_11035, lv3);
			lv.field_14485 = true;
			lv2.field_14485 = true;
			lv3.field_14485 = true;
			this.field_14464.field_14484 = true;
			this.field_14466 = lvs[method_14770(random.nextInt(4), 0, 2)];
			this.field_14466.field_14485 = true;
			this.field_14466.field_14487[Direction.field_11034.getId()].field_14485 = true;
			this.field_14466.field_14487[Direction.field_11043.getId()].field_14485 = true;
			this.field_14466.field_14487[Direction.field_11034.getId()].field_14487[Direction.field_11043.getId()].field_14485 = true;
			this.field_14466.field_14487[Direction.field_11036.getId()].field_14485 = true;
			this.field_14466.field_14487[Direction.field_11034.getId()].field_14487[Direction.field_11036.getId()].field_14485 = true;
			this.field_14466.field_14487[Direction.field_11043.getId()].field_14487[Direction.field_11036.getId()].field_14485 = true;
			this.field_14466.field_14487[Direction.field_11034.getId()].field_14487[Direction.field_11043.getId()].field_14487[Direction.field_11036.getId()].field_14485 = true;
			List<OceanMonumentGenerator.class_3388> list = Lists.newArrayList();

			for (OceanMonumentGenerator.class_3388 lv4 : lvs) {
				if (lv4 != null) {
					lv4.method_14780();
					list.add(lv4);
				}
			}

			lv.method_14780();
			Collections.shuffle(list, random);
			int ac = 1;

			for (OceanMonumentGenerator.class_3388 lv5 : list) {
				int ad = 0;
				int ae = 0;

				while (ad < 2 && ae < 5) {
					ae++;
					int af = random.nextInt(6);
					if (lv5.field_14482[af]) {
						int ag = Direction.byId(af).getOpposite().getId();
						lv5.field_14482[af] = false;
						lv5.field_14487[af].field_14482[ag] = false;
						if (lv5.method_14783(ac++) && lv5.field_14487[af].method_14783(ac++)) {
							ad++;
						} else {
							lv5.field_14482[af] = true;
							lv5.field_14487[af].field_14482[ag] = true;
						}
					}
				}
			}

			list.add(lv);
			list.add(lv2);
			list.add(lv3);
			return list;
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			int i = Math.max(iWorld.getSeaLevel(), 64) - this.boundingBox.minY;
			this.setAirAndWater(iWorld, blockBox, 0, 0, 0, 58, i, 58);
			this.method_14761(false, 0, iWorld, random, blockBox);
			this.method_14761(true, 33, iWorld, random, blockBox);
			this.method_14763(iWorld, random, blockBox);
			this.method_14762(iWorld, random, blockBox);
			this.method_14765(iWorld, random, blockBox);
			this.method_14764(iWorld, random, blockBox);
			this.method_14766(iWorld, random, blockBox);
			this.method_14767(iWorld, random, blockBox);

			for (int j = 0; j < 7; j++) {
				int k = 0;

				while (k < 7) {
					if (k == 0 && j == 3) {
						k = 6;
					}

					int l = j * 9;
					int m = k * 9;

					for (int n = 0; n < 4; n++) {
						for (int o = 0; o < 4; o++) {
							this.addBlock(iWorld, PRISMARINE_BRICKS, l + n, 0, m + o, blockBox);
							this.method_14936(iWorld, PRISMARINE_BRICKS, l + n, -1, m + o, blockBox);
						}
					}

					if (j != 0 && j != 6) {
						k += 6;
					} else {
						k++;
					}
				}
			}

			for (int p = 0; p < 5; p++) {
				this.setAirAndWater(iWorld, blockBox, -1 - p, 0 + p * 2, -1 - p, -1 - p, 23, 58 + p);
				this.setAirAndWater(iWorld, blockBox, 58 + p, 0 + p * 2, -1 - p, 58 + p, 23, 58 + p);
				this.setAirAndWater(iWorld, blockBox, 0 - p, 0 + p * 2, -1 - p, 57 + p, 23, -1 - p);
				this.setAirAndWater(iWorld, blockBox, 0 - p, 0 + p * 2, 58 + p, 57 + p, 23, 58 + p);
			}

			for (OceanMonumentGenerator.Piece piece : this.field_14465) {
				if (piece.getBoundingBox().intersects(blockBox)) {
					piece.generate(iWorld, chunkGenerator, random, blockBox, chunkPos);
				}
			}

			return true;
		}

		private void method_14761(boolean bl, int i, IWorld iWorld, Random random, BlockBox blockBox) {
			int j = 24;
			if (this.method_14775(blockBox, i, 0, i + 23, 20)) {
				this.fillWithOutline(iWorld, blockBox, i + 0, 0, 0, i + 24, 0, 20, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, i + 0, 1, 0, i + 24, 10, 20);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(iWorld, blockBox, i + k, k + 1, k, i + k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, i + k + 7, k + 5, k + 7, i + k + 7, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, i + 17 - k, k + 5, k + 7, i + 17 - k, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, i + 24 - k, k + 1, k, i + 24 - k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, i + k + 1, k + 1, k, i + 23 - k, k + 1, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, i + k + 8, k + 5, k + 7, i + 16 - k, k + 5, k + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(iWorld, blockBox, i + 4, 4, 4, i + 6, 4, 20, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 7, 4, 4, i + 17, 4, 6, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 18, 4, 4, i + 20, 4, 20, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 11, 8, 11, i + 13, 8, 20, PRISMARINE, PRISMARINE, false);
				this.addBlock(iWorld, field_14470, i + 12, 9, 12, blockBox);
				this.addBlock(iWorld, field_14470, i + 12, 9, 15, blockBox);
				this.addBlock(iWorld, field_14470, i + 12, 9, 18, blockBox);
				int l = i + (bl ? 19 : 5);
				int m = i + (bl ? 5 : 19);

				for (int n = 20; n >= 5; n -= 3) {
					this.addBlock(iWorld, field_14470, l, 5, n, blockBox);
				}

				for (int o = 19; o >= 7; o -= 3) {
					this.addBlock(iWorld, field_14470, m, 5, o, blockBox);
				}

				for (int p = 0; p < 4; p++) {
					int q = bl ? i + 24 - (17 - p * 3) : i + 17 - p * 3;
					this.addBlock(iWorld, field_14470, q, 5, 5, blockBox);
				}

				this.addBlock(iWorld, field_14470, m, 5, 5, blockBox);
				this.fillWithOutline(iWorld, blockBox, i + 11, 1, 12, i + 13, 7, 12, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 12, 1, 11, i + 12, 7, 13, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_14763(IWorld iWorld, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 22, 5, 35, 17)) {
				this.setAirAndWater(iWorld, blockBox, 25, 0, 0, 32, 8, 20);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(iWorld, blockBox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.addBlock(iWorld, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, blockBox);
					this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, 26, 5, 5 + i * 4, blockBox);
					this.fillWithOutline(iWorld, blockBox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.addBlock(iWorld, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, blockBox);
					this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, 31, 5, 5 + i * 4, blockBox);
					this.fillWithOutline(iWorld, blockBox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
				}
			}
		}

		private void method_14762(IWorld iWorld, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 15, 20, 42, 21)) {
				this.fillWithOutline(iWorld, blockBox, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 26, 1, 21, 31, 3, 21);
				this.fillWithOutline(iWorld, blockBox, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 27, 3, 21, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 30, 3, 21, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 2, 21, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 2, 21, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 25, 1, 21, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 32, 1, 21, blockBox);

				for (int i = 0; i < 7; i++) {
					this.addBlock(iWorld, DARK_PRISMARINE, 28 - i, 6 + i, 21, blockBox);
					this.addBlock(iWorld, DARK_PRISMARINE, 29 + i, 6 + i, 21, blockBox);
				}

				for (int j = 0; j < 4; j++) {
					this.addBlock(iWorld, DARK_PRISMARINE, 28 - j, 9 + j, 21, blockBox);
					this.addBlock(iWorld, DARK_PRISMARINE, 29 + j, 9 + j, 21, blockBox);
				}

				this.addBlock(iWorld, DARK_PRISMARINE, 28, 12, 21, blockBox);
				this.addBlock(iWorld, DARK_PRISMARINE, 29, 12, 21, blockBox);

				for (int k = 0; k < 3; k++) {
					this.addBlock(iWorld, DARK_PRISMARINE, 22 - k * 2, 8, 21, blockBox);
					this.addBlock(iWorld, DARK_PRISMARINE, 22 - k * 2, 9, 21, blockBox);
					this.addBlock(iWorld, DARK_PRISMARINE, 35 + k * 2, 8, 21, blockBox);
					this.addBlock(iWorld, DARK_PRISMARINE, 35 + k * 2, 9, 21, blockBox);
				}

				this.setAirAndWater(iWorld, blockBox, 15, 13, 21, 42, 15, 21);
				this.setAirAndWater(iWorld, blockBox, 15, 1, 21, 15, 6, 21);
				this.setAirAndWater(iWorld, blockBox, 16, 1, 21, 16, 5, 21);
				this.setAirAndWater(iWorld, blockBox, 17, 1, 21, 20, 4, 21);
				this.setAirAndWater(iWorld, blockBox, 21, 1, 21, 21, 3, 21);
				this.setAirAndWater(iWorld, blockBox, 22, 1, 21, 22, 2, 21);
				this.setAirAndWater(iWorld, blockBox, 23, 1, 21, 24, 1, 21);
				this.setAirAndWater(iWorld, blockBox, 42, 1, 21, 42, 6, 21);
				this.setAirAndWater(iWorld, blockBox, 41, 1, 21, 41, 5, 21);
				this.setAirAndWater(iWorld, blockBox, 37, 1, 21, 40, 4, 21);
				this.setAirAndWater(iWorld, blockBox, 36, 1, 21, 36, 3, 21);
				this.setAirAndWater(iWorld, blockBox, 33, 1, 21, 34, 1, 21);
				this.setAirAndWater(iWorld, blockBox, 35, 1, 21, 35, 2, 21);
			}
		}

		private void method_14765(IWorld iWorld, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 21, 21, 36, 36)) {
				this.fillWithOutline(iWorld, blockBox, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 21, 1, 22, 36, 23, 36);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(iWorld, blockBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(iWorld, blockBox, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 20, 26, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 27, 21, 27, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 27, 20, 27, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 26, 20, 31, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 27, 21, 30, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 27, 20, 30, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 20, 31, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 30, 21, 30, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 30, 20, 30, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 31, 20, 26, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 30, 21, 27, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 30, 20, 27, blockBox);
				this.fillWithOutline(iWorld, blockBox, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_14764(IWorld iWorld, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 0, 21, 6, 58)) {
				this.fillWithOutline(iWorld, blockBox, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 0, 1, 21, 6, 7, 57);
				this.fillWithOutline(iWorld, blockBox, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(iWorld, blockBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int j = 23; j < 53; j += 3) {
					this.addBlock(iWorld, field_14470, 5, 5, j, blockBox);
				}

				this.addBlock(iWorld, field_14470, 5, 5, 52, blockBox);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(iWorld, blockBox, k, k + 1, 21, k, k + 1, 57 - k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(iWorld, blockBox, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
			}

			if (this.method_14775(blockBox, 51, 21, 58, 58)) {
				this.fillWithOutline(iWorld, blockBox, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 51, 1, 21, 57, 7, 57);
				this.fillWithOutline(iWorld, blockBox, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);

				for (int l = 0; l < 4; l++) {
					this.fillWithOutline(iWorld, blockBox, 57 - l, l + 1, 21, 57 - l, l + 1, 57 - l, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int m = 23; m < 53; m += 3) {
					this.addBlock(iWorld, field_14470, 52, 5, m, blockBox);
				}

				this.addBlock(iWorld, field_14470, 52, 5, 52, blockBox);
				this.fillWithOutline(iWorld, blockBox, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
			}

			if (this.method_14775(blockBox, 0, 51, 57, 57)) {
				this.fillWithOutline(iWorld, blockBox, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 7, 1, 51, 50, 10, 57);

				for (int n = 0; n < 4; n++) {
					this.fillWithOutline(iWorld, blockBox, n + 1, n + 1, 57 - n, 56 - n, n + 1, 57 - n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			}
		}

		private void method_14766(IWorld iWorld, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 7, 21, 13, 50)) {
				this.fillWithOutline(iWorld, blockBox, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 7, 1, 21, 13, 10, 50);
				this.fillWithOutline(iWorld, blockBox, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(iWorld, blockBox, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int j = 21; j <= 45; j += 3) {
					this.addBlock(iWorld, field_14470, 12, 9, j, blockBox);
				}
			}

			if (this.method_14775(blockBox, 44, 21, 50, 54)) {
				this.fillWithOutline(iWorld, blockBox, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 44, 1, 21, 50, 10, 50);
				this.fillWithOutline(iWorld, blockBox, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(iWorld, blockBox, 50 - k, k + 5, 21, 50 - k, k + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int l = 21; l <= 45; l += 3) {
					this.addBlock(iWorld, field_14470, 45, 9, l, blockBox);
				}
			}

			if (this.method_14775(blockBox, 8, 44, 49, 54)) {
				this.fillWithOutline(iWorld, blockBox, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 14, 1, 44, 43, 10, 50);

				for (int m = 12; m <= 45; m += 3) {
					this.addBlock(iWorld, field_14470, m, 9, 45, blockBox);
					this.addBlock(iWorld, field_14470, m, 9, 52, blockBox);
					if (m == 12 || m == 18 || m == 24 || m == 33 || m == 39 || m == 45) {
						this.addBlock(iWorld, field_14470, m, 9, 47, blockBox);
						this.addBlock(iWorld, field_14470, m, 9, 50, blockBox);
						this.addBlock(iWorld, field_14470, m, 10, 45, blockBox);
						this.addBlock(iWorld, field_14470, m, 10, 46, blockBox);
						this.addBlock(iWorld, field_14470, m, 10, 51, blockBox);
						this.addBlock(iWorld, field_14470, m, 10, 52, blockBox);
						this.addBlock(iWorld, field_14470, m, 11, 47, blockBox);
						this.addBlock(iWorld, field_14470, m, 11, 50, blockBox);
						this.addBlock(iWorld, field_14470, m, 12, 48, blockBox);
						this.addBlock(iWorld, field_14470, m, 12, 49, blockBox);
					}
				}

				for (int n = 0; n < 3; n++) {
					this.fillWithOutline(iWorld, blockBox, 8 + n, 5 + n, 54, 49 - n, 5 + n, 54, PRISMARINE, PRISMARINE, false);
				}

				this.fillWithOutline(iWorld, blockBox, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_14767(IWorld iWorld, Random random, BlockBox blockBox) {
			if (this.method_14775(blockBox, 14, 21, 20, 43)) {
				this.fillWithOutline(iWorld, blockBox, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 14, 1, 22, 20, 14, 43);
				this.fillWithOutline(iWorld, blockBox, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(iWorld, blockBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int j = 23; j <= 39; j += 3) {
					this.addBlock(iWorld, field_14470, 19, 13, j, blockBox);
				}
			}

			if (this.method_14775(blockBox, 37, 21, 43, 43)) {
				this.fillWithOutline(iWorld, blockBox, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 37, 1, 22, 43, 14, 43);
				this.fillWithOutline(iWorld, blockBox, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(iWorld, blockBox, 43 - k, k + 9, 21, 43 - k, k + 9, 43 - k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int l = 23; l <= 39; l += 3) {
					this.addBlock(iWorld, field_14470, 38, 13, l, blockBox);
				}
			}

			if (this.method_14775(blockBox, 15, 37, 42, 43)) {
				this.fillWithOutline(iWorld, blockBox, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(iWorld, blockBox, 21, 1, 37, 36, 14, 43);
				this.fillWithOutline(iWorld, blockBox, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);

				for (int m = 0; m < 4; m++) {
					this.fillWithOutline(iWorld, blockBox, 15 + m, m + 9, 43 - m, 42 - m, m + 9, 43 - m, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int n = 21; n <= 36; n += 3) {
					this.addBlock(iWorld, field_14470, n, 13, 38, blockBox);
				}
			}
		}
	}

	public static class CoreRoom extends OceanMonumentGenerator.Piece {
		public CoreRoom(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, direction, arg, 2, 2, 2);
		}

		public CoreRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_14771(iWorld, blockBox, 1, 8, 0, 14, 8, 14, PRISMARINE);
			int i = 7;
			BlockState blockState = PRISMARINE_BRICKS;
			this.fillWithOutline(iWorld, blockBox, 0, 7, 0, 0, 7, 15, blockState, blockState, false);
			this.fillWithOutline(iWorld, blockBox, 15, 7, 0, 15, 7, 15, blockState, blockState, false);
			this.fillWithOutline(iWorld, blockBox, 1, 7, 0, 15, 7, 0, blockState, blockState, false);
			this.fillWithOutline(iWorld, blockBox, 1, 7, 15, 14, 7, 15, blockState, blockState, false);

			for (int j = 1; j <= 6; j++) {
				blockState = PRISMARINE_BRICKS;
				if (j == 2 || j == 6) {
					blockState = PRISMARINE;
				}

				for (int k = 0; k <= 15; k += 15) {
					this.fillWithOutline(iWorld, blockBox, k, j, 0, k, j, 1, blockState, blockState, false);
					this.fillWithOutline(iWorld, blockBox, k, j, 6, k, j, 9, blockState, blockState, false);
					this.fillWithOutline(iWorld, blockBox, k, j, 14, k, j, 15, blockState, blockState, false);
				}

				this.fillWithOutline(iWorld, blockBox, 1, j, 0, 1, j, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 6, j, 0, 9, j, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 14, j, 0, 14, j, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 1, j, 15, 14, j, 15, blockState, blockState, false);
			}

			this.fillWithOutline(iWorld, blockBox, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 7, 4, 7, 8, 5, 8, Blocks.field_10205.getDefaultState(), Blocks.field_10205.getDefaultState(), false);

			for (int l = 3; l <= 6; l += 3) {
				for (int m = 6; m <= 9; m += 3) {
					this.addBlock(iWorld, SEA_LANTERN, m, l, 6, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, m, l, 9, blockBox);
				}
			}

			this.fillWithOutline(iWorld, blockBox, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			return true;
		}
	}

	public static class DoubleXRoom extends OceanMonumentGenerator.Piece {
		public DoubleXRoom(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, direction, arg, 2, 1, 1);
		}

		public DoubleXRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			OceanMonumentGenerator.class_3388 lv = this.field_14479.field_14487[Direction.field_11034.getId()];
			OceanMonumentGenerator.class_3388 lv2 = this.field_14479;
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 8, 0, lv.field_14482[Direction.field_11033.getId()]);
				this.method_14774(iWorld, blockBox, 0, 0, lv2.field_14482[Direction.field_11033.getId()]);
			}

			if (lv2.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 4, 1, 7, 4, 6, PRISMARINE);
			}

			if (lv.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 8, 4, 1, 14, 4, 6, PRISMARINE);
			}

			this.fillWithOutline(iWorld, blockBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(iWorld, SEA_LANTERN, 6, 2, 3, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 9, 2, 3, blockBox);
			if (lv2.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 7, 4, 2, 7);
			}

			if (lv2.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 11, 1, 0, 12, 2, 0);
			}

			if (lv.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 11, 1, 7, 12, 2, 7);
			}

			if (lv.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 15, 1, 3, 15, 2, 4);
			}

			return true;
		}
	}

	public static class DoubleXYRoom extends OceanMonumentGenerator.Piece {
		public DoubleXYRoom(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, 1, direction, arg, 2, 2, 1);
		}

		public DoubleXYRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			OceanMonumentGenerator.class_3388 lv = this.field_14479.field_14487[Direction.field_11034.getId()];
			OceanMonumentGenerator.class_3388 lv2 = this.field_14479;
			OceanMonumentGenerator.class_3388 lv3 = lv2.field_14487[Direction.field_11036.getId()];
			OceanMonumentGenerator.class_3388 lv4 = lv.field_14487[Direction.field_11036.getId()];
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 8, 0, lv.field_14482[Direction.field_11033.getId()]);
				this.method_14774(iWorld, blockBox, 0, 0, lv2.field_14482[Direction.field_11033.getId()]);
			}

			if (lv3.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 8, 1, 7, 8, 6, PRISMARINE);
			}

			if (lv4.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 8, 8, 1, 14, 8, 6, PRISMARINE);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = PRISMARINE_BRICKS;
				if (i == 2 || i == 6) {
					blockState = PRISMARINE;
				}

				this.fillWithOutline(iWorld, blockBox, 0, i, 0, 0, i, 7, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 15, i, 0, 15, i, 7, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 1, i, 0, 15, i, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 1, i, 7, 14, i, 7, blockState, blockState, false);
			}

			this.fillWithOutline(iWorld, blockBox, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 6, 6, 2, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 9, 6, 2, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 6, 6, 5, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 9, 6, 5, blockBox);
			this.fillWithOutline(iWorld, blockBox, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(iWorld, SEA_LANTERN, 5, 4, 2, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 5, 4, 5, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 10, 4, 2, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 10, 4, 5, blockBox);
			if (lv2.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 7, 4, 2, 7);
			}

			if (lv2.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 11, 1, 0, 12, 2, 0);
			}

			if (lv.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 11, 1, 7, 12, 2, 7);
			}

			if (lv.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 15, 1, 3, 15, 2, 4);
			}

			if (lv3.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 5, 0, 4, 6, 0);
			}

			if (lv3.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 5, 7, 4, 6, 7);
			}

			if (lv3.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 5, 3, 0, 6, 4);
			}

			if (lv4.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 11, 5, 0, 12, 6, 0);
			}

			if (lv4.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 11, 5, 7, 12, 6, 7);
			}

			if (lv4.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 15, 5, 3, 15, 6, 4);
			}

			return true;
		}
	}

	public static class DoubleYRoom extends OceanMonumentGenerator.Piece {
		public DoubleYRoom(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, direction, arg, 1, 2, 1);
		}

		public DoubleYRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 0, 0, this.field_14479.field_14482[Direction.field_11033.getId()]);
			}

			OceanMonumentGenerator.class_3388 lv = this.field_14479.field_14487[Direction.field_11036.getId()];
			if (lv.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 8, 1, 6, 8, 6, PRISMARINE);
			}

			this.fillWithOutline(iWorld, blockBox, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			OceanMonumentGenerator.class_3388 lv2 = this.field_14479;

			for (int i = 1; i <= 5; i += 4) {
				int j = 0;
				if (lv2.field_14482[Direction.field_11035.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
				}

				int var11 = 7;
				if (lv2.field_14482[Direction.field_11043.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 2, i, var11, 2, i + 2, var11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 5, i, var11, 5, i + 2, var11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 3, i + 2, var11, 4, i + 2, var11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, 0, i, var11, 7, i + 2, var11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 0, i + 1, var11, 7, i + 1, var11, PRISMARINE, PRISMARINE, false);
				}

				int k = 0;
				if (lv2.field_14482[Direction.field_11039.getId()]) {
					this.fillWithOutline(iWorld, blockBox, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
				}

				int var12 = 7;
				if (lv2.field_14482[Direction.field_11034.getId()]) {
					this.fillWithOutline(iWorld, blockBox, var12, i, 2, var12, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, var12, i, 5, var12, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, var12, i + 2, 3, var12, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, var12, i, 0, var12, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, var12, i + 1, 0, var12, i + 1, 7, PRISMARINE, PRISMARINE, false);
				}

				lv2 = lv;
			}

			return true;
		}
	}

	public static class DoubleYZRoom extends OceanMonumentGenerator.Piece {
		public DoubleYZRoom(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, 1, direction, arg, 1, 2, 2);
		}

		public DoubleYZRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			OceanMonumentGenerator.class_3388 lv = this.field_14479.field_14487[Direction.field_11043.getId()];
			OceanMonumentGenerator.class_3388 lv2 = this.field_14479;
			OceanMonumentGenerator.class_3388 lv3 = lv.field_14487[Direction.field_11036.getId()];
			OceanMonumentGenerator.class_3388 lv4 = lv2.field_14487[Direction.field_11036.getId()];
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 0, 8, lv.field_14482[Direction.field_11033.getId()]);
				this.method_14774(iWorld, blockBox, 0, 0, lv2.field_14482[Direction.field_11033.getId()]);
			}

			if (lv4.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 8, 1, 6, 8, 7, PRISMARINE);
			}

			if (lv3.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 8, 8, 6, 8, 14, PRISMARINE);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = PRISMARINE_BRICKS;
				if (i == 2 || i == 6) {
					blockState = PRISMARINE;
				}

				this.fillWithOutline(iWorld, blockBox, 0, i, 0, 0, i, 15, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 7, i, 0, 7, i, 15, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 1, i, 0, 6, i, 0, blockState, blockState, false);
				this.fillWithOutline(iWorld, blockBox, 1, i, 15, 6, i, 15, blockState, blockState, false);
			}

			for (int j = 1; j <= 7; j++) {
				BlockState blockState2 = DARK_PRISMARINE;
				if (j == 2 || j == 6) {
					blockState2 = SEA_LANTERN;
				}

				this.fillWithOutline(iWorld, blockBox, 3, j, 7, 4, j, 8, blockState2, blockState2, false);
			}

			if (lv2.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 7, 1, 3, 7, 2, 4);
			}

			if (lv2.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 15, 4, 2, 15);
			}

			if (lv.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 11, 0, 2, 12);
			}

			if (lv.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 7, 1, 11, 7, 2, 12);
			}

			if (lv4.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 5, 0, 4, 6, 0);
			}

			if (lv4.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 7, 5, 3, 7, 6, 4);
				this.fillWithOutline(iWorld, blockBox, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (lv4.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 5, 3, 0, 6, 4);
				this.fillWithOutline(iWorld, blockBox, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (lv3.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 5, 15, 4, 6, 15);
			}

			if (lv3.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 5, 11, 0, 6, 12);
				this.fillWithOutline(iWorld, blockBox, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (lv3.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 7, 5, 11, 7, 6, 12);
				this.fillWithOutline(iWorld, blockBox, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			return true;
		}
	}

	public static class DoubleZRoom extends OceanMonumentGenerator.Piece {
		public DoubleZRoom(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, direction, arg, 1, 1, 2);
		}

		public DoubleZRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			OceanMonumentGenerator.class_3388 lv = this.field_14479.field_14487[Direction.field_11043.getId()];
			OceanMonumentGenerator.class_3388 lv2 = this.field_14479;
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 0, 8, lv.field_14482[Direction.field_11033.getId()]);
				this.method_14774(iWorld, blockBox, 0, 0, lv2.field_14482[Direction.field_11033.getId()]);
			}

			if (lv2.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 4, 1, 6, 4, 7, PRISMARINE);
			}

			if (lv.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 4, 8, 6, 4, 14, PRISMARINE);
			}

			this.fillWithOutline(iWorld, blockBox, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(iWorld, SEA_LANTERN, 2, 2, 5, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 5, 2, 5, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 2, 2, 10, blockBox);
			this.addBlock(iWorld, SEA_LANTERN, 5, 2, 10, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 2, 3, 5, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 5, 3, 5, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 2, 3, 10, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 5, 3, 10, blockBox);
			if (lv2.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 0, 4, 2, 0);
			}

			if (lv2.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 7, 1, 3, 7, 2, 4);
			}

			if (lv2.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 3, 0, 2, 4);
			}

			if (lv.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 15, 4, 2, 15);
			}

			if (lv.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 11, 0, 2, 12);
			}

			if (lv.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 7, 1, 11, 7, 2, 12);
			}

			return true;
		}
	}

	public static class Entry extends OceanMonumentGenerator.Piece {
		public Entry(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, direction, arg, 1, 1, 1);
		}

		public Entry(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, blockBox, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			if (this.field_14479.field_14482[Direction.field_11043.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 7, 4, 2, 7);
			}

			if (this.field_14479.field_14482[Direction.field_11039.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 0, 1, 3, 1, 2, 4);
			}

			if (this.field_14479.field_14482[Direction.field_11034.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 6, 1, 3, 7, 2, 4);
			}

			return true;
		}
	}

	public static class Penthouse extends OceanMonumentGenerator.Piece {
		public Penthouse(Direction direction, BlockBox blockBox) {
			super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, direction, blockBox);
		}

		public Penthouse(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.fillWithOutline(iWorld, blockBox, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

			for (int i = 2; i <= 11; i += 3) {
				this.addBlock(iWorld, SEA_LANTERN, 0, 0, i, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 13, 0, i, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, i, 0, 0, blockBox);
			}

			this.fillWithOutline(iWorld, blockBox, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 5, 0, 8, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 8, 0, 8, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 10, 0, 10, blockBox);
			this.addBlock(iWorld, PRISMARINE_BRICKS, 3, 0, 10, blockBox);
			this.fillWithOutline(iWorld, blockBox, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
			int j = 3;

			for (int k = 0; k < 2; k++) {
				for (int l = 2; l <= 8; l += 3) {
					this.fillWithOutline(iWorld, blockBox, j, 0, l, j, 2, l, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				j = 10;
			}

			this.fillWithOutline(iWorld, blockBox, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.setAirAndWater(iWorld, blockBox, 6, -1, 3, 7, -1, 4);
			this.method_14772(iWorld, blockBox, 6, 1, 6);
			return true;
		}
	}

	public abstract static class Piece extends StructurePiece {
		protected static final BlockState PRISMARINE = Blocks.field_10135.getDefaultState();
		protected static final BlockState PRISMARINE_BRICKS = Blocks.field_10006.getDefaultState();
		protected static final BlockState DARK_PRISMARINE = Blocks.field_10297.getDefaultState();
		protected static final BlockState field_14470 = PRISMARINE_BRICKS;
		protected static final BlockState SEA_LANTERN = Blocks.field_10174.getDefaultState();
		protected static final BlockState WATER = Blocks.field_10382.getDefaultState();
		protected static final Set<Block> ICE_BLOCKS = ImmutableSet.builder()
			.add(Blocks.field_10295)
			.add(Blocks.field_10225)
			.add(Blocks.field_10384)
			.add(WATER.getBlock())
			.build();
		protected static final int field_14469 = method_14770(2, 0, 0);
		protected static final int field_14468 = method_14770(2, 2, 0);
		protected static final int field_14478 = method_14770(0, 1, 0);
		protected static final int field_14477 = method_14770(4, 1, 0);
		protected OceanMonumentGenerator.class_3388 field_14479;

		protected static final int method_14770(int i, int j, int k) {
			return j * 25 + k * 5 + i;
		}

		public Piece(StructurePieceType structurePieceType, int i) {
			super(structurePieceType, i);
		}

		public Piece(StructurePieceType structurePieceType, Direction direction, BlockBox blockBox) {
			super(structurePieceType, 1);
			this.setOrientation(direction);
			this.boundingBox = blockBox;
		}

		protected Piece(StructurePieceType structurePieceType, int i, Direction direction, OceanMonumentGenerator.class_3388 arg, int j, int k, int l) {
			super(structurePieceType, i);
			this.setOrientation(direction);
			this.field_14479 = arg;
			int m = arg.field_14486;
			int n = m % 5;
			int o = m / 5 % 5;
			int p = m / 25;
			if (direction != Direction.field_11043 && direction != Direction.field_11035) {
				this.boundingBox = new BlockBox(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
			} else {
				this.boundingBox = new BlockBox(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1);
			}

			switch (direction) {
				case field_11043:
					this.boundingBox.offset(n * 8, p * 4, -(o + l) * 8 + 1);
					break;
				case field_11035:
					this.boundingBox.offset(n * 8, p * 4, o * 8);
					break;
				case field_11039:
					this.boundingBox.offset(-(o + l) * 8 + 1, p * 4, n * 8);
					break;
				default:
					this.boundingBox.offset(o * 8, p * 4, n * 8);
			}
		}

		public Piece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
			super(structurePieceType, compoundTag);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
		}

		protected void setAirAndWater(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n) {
			for (int o = j; o <= m; o++) {
				for (int p = i; p <= l; p++) {
					for (int q = k; q <= n; q++) {
						BlockState blockState = this.getBlockAt(iWorld, p, o, q, blockBox);
						if (!ICE_BLOCKS.contains(blockState.getBlock())) {
							if (this.applyYTransform(o) >= iWorld.getSeaLevel() && blockState != WATER) {
								this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), p, o, q, blockBox);
							} else {
								this.addBlock(iWorld, WATER, p, o, q, blockBox);
							}
						}
					}
				}
			}
		}

		protected void method_14774(IWorld iWorld, BlockBox blockBox, int i, int j, boolean bl) {
			if (bl) {
				this.fillWithOutline(iWorld, blockBox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 0, i + 4, 0, j + 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 2, i + 4, 0, j + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, i + 3, 0, j + 5, i + 4, 0, j + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, i + 2, 0, j + 3, i + 2, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, i + 5, 0, j + 3, i + 5, 0, j + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			} else {
				this.fillWithOutline(iWorld, blockBox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, PRISMARINE, PRISMARINE, false);
			}
		}

		protected void method_14771(IWorld iWorld, BlockBox blockBox, int i, int j, int k, int l, int m, int n, BlockState blockState) {
			for (int o = j; o <= m; o++) {
				for (int p = i; p <= l; p++) {
					for (int q = k; q <= n; q++) {
						if (this.getBlockAt(iWorld, p, o, q, blockBox) == WATER) {
							this.addBlock(iWorld, blockState, p, o, q, blockBox);
						}
					}
				}
			}
		}

		protected boolean method_14775(BlockBox blockBox, int i, int j, int k, int l) {
			int m = this.applyXTransform(i, j);
			int n = this.applyZTransform(i, j);
			int o = this.applyXTransform(k, l);
			int p = this.applyZTransform(k, l);
			return blockBox.intersectsXZ(Math.min(m, o), Math.min(n, p), Math.max(m, o), Math.max(n, p));
		}

		protected boolean method_14772(IWorld iWorld, BlockBox blockBox, int i, int j, int k) {
			int l = this.applyXTransform(i, k);
			int m = this.applyYTransform(j);
			int n = this.applyZTransform(i, k);
			if (blockBox.contains(new BlockPos(l, m, n))) {
				ElderGuardianEntity elderGuardianEntity = EntityType.field_6086.create(iWorld.getWorld());
				elderGuardianEntity.heal(elderGuardianEntity.getMaximumHealth());
				elderGuardianEntity.refreshPositionAndAngles((double)l + 0.5, (double)m, (double)n + 0.5, 0.0F, 0.0F);
				elderGuardianEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(elderGuardianEntity)), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(elderGuardianEntity);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class SimpleRoom extends OceanMonumentGenerator.Piece {
		private int field_14480;

		public SimpleRoom(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, direction, arg, 1, 1, 1);
			this.field_14480 = random.nextInt(3);
		}

		public SimpleRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 0, 0, this.field_14479.field_14482[Direction.field_11033.getId()]);
			}

			if (this.field_14479.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
			}

			boolean bl = this.field_14480 != 0
				&& random.nextBoolean()
				&& !this.field_14479.field_14482[Direction.field_11033.getId()]
				&& !this.field_14479.field_14482[Direction.field_11036.getId()]
				&& this.field_14479.method_14781() > 1;
			if (this.field_14480 == 0) {
				this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
				this.addBlock(iWorld, SEA_LANTERN, 1, 2, 1, blockBox);
				this.fillWithOutline(iWorld, blockBox, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
				this.addBlock(iWorld, SEA_LANTERN, 6, 2, 1, blockBox);
				this.fillWithOutline(iWorld, blockBox, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
				this.addBlock(iWorld, SEA_LANTERN, 1, 2, 6, blockBox);
				this.fillWithOutline(iWorld, blockBox, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
				this.addBlock(iWorld, SEA_LANTERN, 6, 2, 6, blockBox);
				if (this.field_14479.field_14482[Direction.field_11035.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.field_14479.field_14482[Direction.field_11043.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.field_14479.field_14482[Direction.field_11039.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.field_14479.field_14482[Direction.field_11034.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(iWorld, blockBox, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			} else if (this.field_14480 == 1) {
				this.fillWithOutline(iWorld, blockBox, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(iWorld, SEA_LANTERN, 2, 2, 2, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 2, 2, 5, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 5, 2, 5, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 5, 2, 2, blockBox);
				this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.addBlock(iWorld, PRISMARINE, 1, 2, 0, blockBox);
				this.addBlock(iWorld, PRISMARINE, 0, 2, 1, blockBox);
				this.addBlock(iWorld, PRISMARINE, 1, 2, 7, blockBox);
				this.addBlock(iWorld, PRISMARINE, 0, 2, 6, blockBox);
				this.addBlock(iWorld, PRISMARINE, 6, 2, 7, blockBox);
				this.addBlock(iWorld, PRISMARINE, 7, 2, 6, blockBox);
				this.addBlock(iWorld, PRISMARINE, 6, 2, 0, blockBox);
				this.addBlock(iWorld, PRISMARINE, 7, 2, 1, blockBox);
				if (!this.field_14479.field_14482[Direction.field_11035.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.field_14479.field_14482[Direction.field_11043.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.field_14479.field_14482[Direction.field_11039.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.field_14479.field_14482[Direction.field_11034.getId()]) {
					this.fillWithOutline(iWorld, blockBox, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(iWorld, blockBox, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			} else if (this.field_14480 == 2) {
				this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				if (this.field_14479.field_14482[Direction.field_11035.getId()]) {
					this.setAirAndWater(iWorld, blockBox, 3, 1, 0, 4, 2, 0);
				}

				if (this.field_14479.field_14482[Direction.field_11043.getId()]) {
					this.setAirAndWater(iWorld, blockBox, 3, 1, 7, 4, 2, 7);
				}

				if (this.field_14479.field_14482[Direction.field_11039.getId()]) {
					this.setAirAndWater(iWorld, blockBox, 0, 1, 3, 0, 2, 4);
				}

				if (this.field_14479.field_14482[Direction.field_11034.getId()]) {
					this.setAirAndWater(iWorld, blockBox, 7, 1, 3, 7, 2, 4);
				}
			}

			if (bl) {
				this.fillWithOutline(iWorld, blockBox, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			return true;
		}
	}

	public static class SimpleRoomTop extends OceanMonumentGenerator.Piece {
		public SimpleRoomTop(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, direction, arg, 1, 1, 1);
		}

		public SimpleRoomTop(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.field_14479.field_14486 / 25 > 0) {
				this.method_14774(iWorld, blockBox, 0, 0, this.field_14479.field_14482[Direction.field_11033.getId()]);
			}

			if (this.field_14479.field_14487[Direction.field_11036.getId()] == null) {
				this.method_14771(iWorld, blockBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
			}

			for (int i = 1; i <= 6; i++) {
				for (int j = 1; j <= 6; j++) {
					if (random.nextInt(3) != 0) {
						int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
						BlockState blockState = Blocks.field_10562.getDefaultState();
						this.fillWithOutline(iWorld, blockBox, i, k, j, i, 3, j, blockState, blockState, false);
					}
				}
			}

			this.fillWithOutline(iWorld, blockBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(iWorld, blockBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(iWorld, blockBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			if (this.field_14479.field_14482[Direction.field_11035.getId()]) {
				this.setAirAndWater(iWorld, blockBox, 3, 1, 0, 4, 2, 0);
			}

			return true;
		}
	}

	public static class WingRoom extends OceanMonumentGenerator.Piece {
		private int field_14481;

		public WingRoom(Direction direction, BlockBox blockBox, int i) {
			super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, direction, blockBox);
			this.field_14481 = i & 1;
		}

		public WingRoom(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, compoundTag);
		}

		@Override
		public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (this.field_14481 == 0) {
				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(iWorld, blockBox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(iWorld, blockBox, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);

				for (int j = 18; j >= 7; j -= 3) {
					this.addBlock(iWorld, SEA_LANTERN, 6, 3, j, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, 16, 3, j, blockBox);
				}

				this.addBlock(iWorld, SEA_LANTERN, 10, 0, 10, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 12, 0, 10, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 10, 0, 12, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 12, 0, 12, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 8, 3, 6, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 14, 3, 6, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 4, 2, 4, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 4, 1, 4, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 4, 0, 4, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 18, 2, 4, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 18, 1, 4, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 18, 0, 4, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 4, 2, 18, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 4, 1, 18, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 4, 0, 18, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 18, 2, 18, blockBox);
				this.addBlock(iWorld, SEA_LANTERN, 18, 1, 18, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 18, 0, 18, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 9, 7, 20, blockBox);
				this.addBlock(iWorld, PRISMARINE_BRICKS, 13, 7, 20, blockBox);
				this.fillWithOutline(iWorld, blockBox, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.method_14772(iWorld, blockBox, 11, 2, 16);
			} else if (this.field_14481 == 1) {
				this.fillWithOutline(iWorld, blockBox, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(iWorld, blockBox, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				int k = 9;
				int l = 20;
				int m = 5;

				for (int n = 0; n < 2; n++) {
					this.addBlock(iWorld, PRISMARINE_BRICKS, k, 6, 20, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, k, 5, 20, blockBox);
					this.addBlock(iWorld, PRISMARINE_BRICKS, k, 4, 20, blockBox);
					k = 13;
				}

				this.fillWithOutline(iWorld, blockBox, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				int var12 = 10;

				for (int o = 0; o < 2; o++) {
					this.fillWithOutline(iWorld, blockBox, var12, 0, 10, var12, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, var12, 0, 12, var12, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.addBlock(iWorld, SEA_LANTERN, var12, 0, 10, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, var12, 0, 12, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, var12, 4, 10, blockBox);
					this.addBlock(iWorld, SEA_LANTERN, var12, 4, 12, blockBox);
					var12 = 12;
				}

				var12 = 8;

				for (int p = 0; p < 2; p++) {
					this.fillWithOutline(iWorld, blockBox, var12, 0, 7, var12, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(iWorld, blockBox, var12, 0, 14, var12, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					var12 = 14;
				}

				this.fillWithOutline(iWorld, blockBox, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(iWorld, blockBox, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.method_14772(iWorld, blockBox, 11, 5, 13);
			}

			return true;
		}
	}

	static class class_3367 implements OceanMonumentGenerator.class_3375 {
		private class_3367() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			return arg.field_14482[Direction.field_11034.getId()] && !arg.field_14487[Direction.field_11034.getId()].field_14485;
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			arg.field_14485 = true;
			arg.field_14487[Direction.field_11034.getId()].field_14485 = true;
			return new OceanMonumentGenerator.DoubleXRoom(direction, arg);
		}
	}

	static class class_3368 implements OceanMonumentGenerator.class_3375 {
		private class_3368() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			if (arg.field_14482[Direction.field_11034.getId()]
				&& !arg.field_14487[Direction.field_11034.getId()].field_14485
				&& arg.field_14482[Direction.field_11036.getId()]
				&& !arg.field_14487[Direction.field_11036.getId()].field_14485) {
				OceanMonumentGenerator.class_3388 lv = arg.field_14487[Direction.field_11034.getId()];
				return lv.field_14482[Direction.field_11036.getId()] && !lv.field_14487[Direction.field_11036.getId()].field_14485;
			} else {
				return false;
			}
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			arg.field_14485 = true;
			arg.field_14487[Direction.field_11034.getId()].field_14485 = true;
			arg.field_14487[Direction.field_11036.getId()].field_14485 = true;
			arg.field_14487[Direction.field_11034.getId()].field_14487[Direction.field_11036.getId()].field_14485 = true;
			return new OceanMonumentGenerator.DoubleXYRoom(direction, arg);
		}
	}

	static class class_3369 implements OceanMonumentGenerator.class_3375 {
		private class_3369() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			return arg.field_14482[Direction.field_11036.getId()] && !arg.field_14487[Direction.field_11036.getId()].field_14485;
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			arg.field_14485 = true;
			arg.field_14487[Direction.field_11036.getId()].field_14485 = true;
			return new OceanMonumentGenerator.DoubleYRoom(direction, arg);
		}
	}

	static class class_3370 implements OceanMonumentGenerator.class_3375 {
		private class_3370() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			if (arg.field_14482[Direction.field_11043.getId()]
				&& !arg.field_14487[Direction.field_11043.getId()].field_14485
				&& arg.field_14482[Direction.field_11036.getId()]
				&& !arg.field_14487[Direction.field_11036.getId()].field_14485) {
				OceanMonumentGenerator.class_3388 lv = arg.field_14487[Direction.field_11043.getId()];
				return lv.field_14482[Direction.field_11036.getId()] && !lv.field_14487[Direction.field_11036.getId()].field_14485;
			} else {
				return false;
			}
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			arg.field_14485 = true;
			arg.field_14487[Direction.field_11043.getId()].field_14485 = true;
			arg.field_14487[Direction.field_11036.getId()].field_14485 = true;
			arg.field_14487[Direction.field_11043.getId()].field_14487[Direction.field_11036.getId()].field_14485 = true;
			return new OceanMonumentGenerator.DoubleYZRoom(direction, arg);
		}
	}

	static class class_3371 implements OceanMonumentGenerator.class_3375 {
		private class_3371() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			return arg.field_14482[Direction.field_11043.getId()] && !arg.field_14487[Direction.field_11043.getId()].field_14485;
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			OceanMonumentGenerator.class_3388 lv = arg;
			if (!arg.field_14482[Direction.field_11043.getId()] || arg.field_14487[Direction.field_11043.getId()].field_14485) {
				lv = arg.field_14487[Direction.field_11035.getId()];
			}

			lv.field_14485 = true;
			lv.field_14487[Direction.field_11043.getId()].field_14485 = true;
			return new OceanMonumentGenerator.DoubleZRoom(direction, lv);
		}
	}

	static class class_3372 implements OceanMonumentGenerator.class_3375 {
		private class_3372() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			return true;
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			arg.field_14485 = true;
			return new OceanMonumentGenerator.SimpleRoom(direction, arg, random);
		}
	}

	static class class_3373 implements OceanMonumentGenerator.class_3375 {
		private class_3373() {
		}

		@Override
		public boolean method_14769(OceanMonumentGenerator.class_3388 arg) {
			return !arg.field_14482[Direction.field_11039.getId()]
				&& !arg.field_14482[Direction.field_11034.getId()]
				&& !arg.field_14482[Direction.field_11043.getId()]
				&& !arg.field_14482[Direction.field_11035.getId()]
				&& !arg.field_14482[Direction.field_11036.getId()];
		}

		@Override
		public OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random) {
			arg.field_14485 = true;
			return new OceanMonumentGenerator.SimpleRoomTop(direction, arg);
		}
	}

	interface class_3375 {
		boolean method_14769(OceanMonumentGenerator.class_3388 arg);

		OceanMonumentGenerator.Piece method_14768(Direction direction, OceanMonumentGenerator.class_3388 arg, Random random);
	}

	static class class_3388 {
		private final int field_14486;
		private final OceanMonumentGenerator.class_3388[] field_14487 = new OceanMonumentGenerator.class_3388[6];
		private final boolean[] field_14482 = new boolean[6];
		private boolean field_14485;
		private boolean field_14484;
		private int field_14483;

		public class_3388(int i) {
			this.field_14486 = i;
		}

		public void method_14786(Direction direction, OceanMonumentGenerator.class_3388 arg) {
			this.field_14487[direction.getId()] = arg;
			arg.field_14487[direction.getOpposite().getId()] = this;
		}

		public void method_14780() {
			for (int i = 0; i < 6; i++) {
				this.field_14482[i] = this.field_14487[i] != null;
			}
		}

		public boolean method_14783(int i) {
			if (this.field_14484) {
				return true;
			} else {
				this.field_14483 = i;

				for (int j = 0; j < 6; j++) {
					if (this.field_14487[j] != null && this.field_14482[j] && this.field_14487[j].field_14483 != i && this.field_14487[j].method_14783(i)) {
						return true;
					}
				}

				return false;
			}
		}

		public boolean method_14785() {
			return this.field_14486 >= 75;
		}

		public int method_14781() {
			int i = 0;

			for (int j = 0; j < 6; j++) {
				if (this.field_14482[j]) {
					i++;
				}
			}

			return i;
		}
	}
}
