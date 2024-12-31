package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3016;
import net.minecraft.class_3999;
import net.minecraft.class_4081;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Structure {
	private final List<List<Structure.StructureBlockInfo>> blockInfoLists = Lists.newArrayList();
	private final List<Structure.StructureEntityInfo> entities = Lists.newArrayList();
	private BlockPos size = BlockPos.ORIGIN;
	private String author = "?";

	public BlockPos getSize() {
		return this.size;
	}

	public void setAuthor(String name) {
		this.author = name;
	}

	public String getAuthor() {
		return this.author;
	}

	public void saveFromWorld(World world, BlockPos start, BlockPos size, boolean includeEntities, @Nullable Block ignoredBlock) {
		if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
			BlockPos blockPos = start.add(size).add(-1, -1, -1);
			List<Structure.StructureBlockInfo> list = Lists.newArrayList();
			List<Structure.StructureBlockInfo> list2 = Lists.newArrayList();
			List<Structure.StructureBlockInfo> list3 = Lists.newArrayList();
			BlockPos blockPos2 = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
			BlockPos blockPos3 = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
			this.size = size;

			for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos2, blockPos3)) {
				BlockPos blockPos4 = mutable.subtract(blockPos2);
				BlockState blockState = world.getBlockState(mutable);
				if (ignoredBlock == null || ignoredBlock != blockState.getBlock()) {
					BlockEntity blockEntity = world.getBlockEntity(mutable);
					if (blockEntity != null) {
						NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
						nbtCompound.remove("x");
						nbtCompound.remove("y");
						nbtCompound.remove("z");
						list2.add(new Structure.StructureBlockInfo(blockPos4, blockState, nbtCompound));
					} else if (!blockState.isFullOpaque(world, mutable) && !blockState.method_16897()) {
						list3.add(new Structure.StructureBlockInfo(blockPos4, blockState, null));
					} else {
						list.add(new Structure.StructureBlockInfo(blockPos4, blockState, null));
					}
				}
			}

			List<Structure.StructureBlockInfo> list4 = Lists.newArrayList();
			list4.addAll(list);
			list4.addAll(list2);
			list4.addAll(list3);
			this.blockInfoLists.clear();
			this.blockInfoLists.add(list4);
			if (includeEntities) {
				this.method_11883(world, blockPos2, blockPos3.add(1, 1, 1));
			} else {
				this.entities.clear();
			}
		}
	}

	private void method_11883(World world, BlockPos blockPos, BlockPos blockPos2) {
		List<Entity> list = world.method_16325(Entity.class, new Box(blockPos, blockPos2), entityx -> !(entityx instanceof PlayerEntity));
		this.entities.clear();

		for (Entity entity : list) {
			Vec3d vec3d = new Vec3d(entity.x - (double)blockPos.getX(), entity.y - (double)blockPos.getY(), entity.z - (double)blockPos.getZ());
			NbtCompound nbtCompound = new NbtCompound();
			entity.saveToNbt(nbtCompound);
			BlockPos blockPos3;
			if (entity instanceof PaintingEntity) {
				blockPos3 = ((PaintingEntity)entity).getTilePos().subtract(blockPos);
			} else {
				blockPos3 = new BlockPos(vec3d);
			}

			this.entities.add(new Structure.StructureEntityInfo(vec3d, blockPos3, nbtCompound));
		}
	}

	public Map<BlockPos, String> method_11890(BlockPos blockPos, StructurePlacementData structurePlacementData) {
		Map<BlockPos, String> map = Maps.newHashMap();
		BlockBox blockBox = structurePlacementData.method_11877();

		for (Structure.StructureBlockInfo structureBlockInfo : structurePlacementData.method_17692(this.blockInfoLists, blockPos)) {
			BlockPos blockPos2 = method_11886(structurePlacementData, structureBlockInfo.pos).add(blockPos);
			if (blockBox == null || blockBox.contains(blockPos2)) {
				BlockState blockState = structureBlockInfo.state;
				if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK && structureBlockInfo.tag != null) {
					StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"));
					if (structureBlockMode == StructureBlockMode.DATA) {
						map.put(blockPos2, structureBlockInfo.tag.getString("metadata"));
					}
				}
			}
		}

		return map;
	}

	public BlockPos method_11887(
		StructurePlacementData structurePlacementData, BlockPos blockPos, StructurePlacementData structurePlacementData2, BlockPos blockPos2
	) {
		BlockPos blockPos3 = method_11886(structurePlacementData, blockPos);
		BlockPos blockPos4 = method_11886(structurePlacementData2, blockPos2);
		return blockPos3.subtract(blockPos4);
	}

	public static BlockPos method_11886(StructurePlacementData structurePlacementData, BlockPos blockPos) {
		return method_11889(blockPos, structurePlacementData.method_11871(), structurePlacementData.method_11874(), structurePlacementData.method_17693());
	}

	public void method_11882(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		structurePlacementData.method_11879();
		this.method_11896(iWorld, blockPos, structurePlacementData);
	}

	public void method_11896(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.method_17698(iWorld, blockPos, new class_3016(blockPos, structurePlacementData), structurePlacementData, 2);
	}

	public boolean method_17697(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData, int i) {
		return this.method_17698(iWorld, blockPos, new class_3016(blockPos, structurePlacementData), structurePlacementData, i);
	}

	public boolean method_17698(IWorld iWorld, BlockPos blockPos, @Nullable class_3999 arg, StructurePlacementData structurePlacementData, int i) {
		if (this.blockInfoLists.isEmpty()) {
			return false;
		} else {
			List<Structure.StructureBlockInfo> list = structurePlacementData.method_17692(this.blockInfoLists, blockPos);
			if ((!list.isEmpty() || !structurePlacementData.method_11875() && !this.entities.isEmpty())
				&& this.size.getX() >= 1
				&& this.size.getY() >= 1
				&& this.size.getZ() >= 1) {
				Block block = structurePlacementData.method_11876();
				BlockBox blockBox = structurePlacementData.method_11877();
				List<BlockPos> list2 = Lists.newArrayListWithCapacity(structurePlacementData.method_17694() ? list.size() : 0);
				List<Pair<BlockPos, NbtCompound>> list3 = Lists.newArrayListWithCapacity(list.size());
				int j = Integer.MAX_VALUE;
				int k = Integer.MAX_VALUE;
				int l = Integer.MAX_VALUE;
				int m = Integer.MIN_VALUE;
				int n = Integer.MIN_VALUE;
				int o = Integer.MIN_VALUE;

				for (Structure.StructureBlockInfo structureBlockInfo : list) {
					BlockPos blockPos2 = method_11886(structurePlacementData, structureBlockInfo.pos).add(blockPos);
					Structure.StructureBlockInfo structureBlockInfo2 = arg != null ? arg.method_13390(iWorld, blockPos2, structureBlockInfo) : structureBlockInfo;
					if (structureBlockInfo2 != null) {
						Block block2 = structureBlockInfo2.state.getBlock();
						if ((block == null || block != block2)
							&& (!structurePlacementData.method_11878() || block2 != Blocks.STRUCTURE_BLOCK)
							&& (blockBox == null || blockBox.contains(blockPos2))) {
							FluidState fluidState = structurePlacementData.method_17694() ? iWorld.getFluidState(blockPos2) : null;
							BlockState blockState = structureBlockInfo2.state.mirror(structurePlacementData.method_11871());
							BlockState blockState2 = blockState.rotate(structurePlacementData.method_11874());
							if (structureBlockInfo2.tag != null) {
								BlockEntity blockEntity = iWorld.getBlockEntity(blockPos2);
								if (blockEntity instanceof Inventory) {
									((Inventory)blockEntity).clear();
								}

								iWorld.setBlockState(blockPos2, Blocks.BARRIER.getDefaultState(), 4);
							}

							if (iWorld.setBlockState(blockPos2, blockState2, i)) {
								j = Math.min(j, blockPos2.getX());
								k = Math.min(k, blockPos2.getY());
								l = Math.min(l, blockPos2.getZ());
								m = Math.max(m, blockPos2.getX());
								n = Math.max(n, blockPos2.getY());
								o = Math.max(o, blockPos2.getZ());
								list3.add(Pair.of(blockPos2, structureBlockInfo.tag));
								if (structureBlockInfo2.tag != null) {
									BlockEntity blockEntity2 = iWorld.getBlockEntity(blockPos2);
									if (blockEntity2 != null) {
										structureBlockInfo2.tag.putInt("x", blockPos2.getX());
										structureBlockInfo2.tag.putInt("y", blockPos2.getY());
										structureBlockInfo2.tag.putInt("z", blockPos2.getZ());
										blockEntity2.fromNbt(structureBlockInfo2.tag);
										blockEntity2.method_13321(structurePlacementData.method_11871());
										blockEntity2.method_13322(structurePlacementData.method_11874());
									}
								}

								if (fluidState != null && blockState2.getBlock() instanceof FluidFillable) {
									((FluidFillable)blockState2.getBlock()).tryFillWithFluid(iWorld, blockPos2, blockState2, fluidState);
									if (!fluidState.isStill()) {
										list2.add(blockPos2);
									}
								}
							}
						}
					}
				}

				boolean bl = true;
				Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

				while (bl && !list2.isEmpty()) {
					bl = false;
					Iterator<BlockPos> iterator = list2.iterator();

					while (iterator.hasNext()) {
						BlockPos blockPos3 = (BlockPos)iterator.next();
						FluidState fluidState2 = iWorld.getFluidState(blockPos3);

						for (int p = 0; p < directions.length && !fluidState2.isStill(); p++) {
							FluidState fluidState3 = iWorld.getFluidState(blockPos3.offset(directions[p]));
							if (fluidState3.method_17810() > fluidState2.method_17810() || fluidState3.isStill() && !fluidState2.isStill()) {
								fluidState2 = fluidState3;
							}
						}

						if (fluidState2.isStill()) {
							BlockState blockState3 = iWorld.getBlockState(blockPos3);
							if (blockState3.getBlock() instanceof FluidFillable) {
								((FluidFillable)blockState3.getBlock()).tryFillWithFluid(iWorld, blockPos3, blockState3, fluidState2);
								bl = true;
								iterator.remove();
							}
						}
					}
				}

				if (j <= m) {
					VoxelSet voxelSet = new class_4081(m - j + 1, n - k + 1, o - l + 1);
					int q = j;
					int r = k;
					int s = l;

					for (Pair<BlockPos, NbtCompound> pair : list3) {
						BlockPos blockPos4 = (BlockPos)pair.getFirst();
						voxelSet.method_18022(blockPos4.getX() - q, blockPos4.getY() - r, blockPos4.getZ() - s, true, true);
					}

					voxelSet.method_18023((direction, mx, nx, ox) -> {
						BlockPos blockPosx = new BlockPos(q + mx, r + nx, s + ox);
						BlockPos blockPos2 = blockPosx.offset(direction);
						BlockState blockStatex = iWorld.getBlockState(blockPosx);
						BlockState blockState2x = iWorld.getBlockState(blockPos2);
						BlockState blockState3 = blockStatex.getStateForNeighborUpdate(direction, blockState2x, iWorld, blockPosx, blockPos2);
						if (blockStatex != blockState3) {
							iWorld.setBlockState(blockPosx, blockState3, i & -2 | 16);
						}

						BlockState blockState4x = blockState2x.getStateForNeighborUpdate(direction.getOpposite(), blockState3, iWorld, blockPos2, blockPosx);
						if (blockState2x != blockState4x) {
							iWorld.setBlockState(blockPos2, blockState4x, i & -2 | 16);
						}
					});

					for (Pair<BlockPos, NbtCompound> pair2 : list3) {
						BlockPos blockPos5 = (BlockPos)pair2.getFirst();
						BlockState blockState4 = iWorld.getBlockState(blockPos5);
						BlockState blockState5 = Block.method_16583(blockState4, iWorld, blockPos5);
						if (blockState4 != blockState5) {
							iWorld.setBlockState(blockPos5, blockState5, i & -2 | 16);
						}

						iWorld.method_16342(blockPos5, blockState5.getBlock());
						if (pair2.getSecond() != null) {
							BlockEntity blockEntity3 = iWorld.getBlockEntity(blockPos5);
							if (blockEntity3 != null) {
								blockEntity3.markDirty();
							}
						}
					}
				}

				if (!structurePlacementData.method_11875()) {
					this.method_11881(
						iWorld, blockPos, structurePlacementData.method_11871(), structurePlacementData.method_11874(), structurePlacementData.method_17693(), blockBox
					);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	private void method_11881(
		IWorld iWorld, BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos2, @Nullable BlockBox blockBox
	) {
		for (Structure.StructureEntityInfo structureEntityInfo : this.entities) {
			BlockPos blockPos3 = method_11889(structureEntityInfo.blockPos, blockMirror, blockRotation, blockPos2).add(blockPos);
			if (blockBox == null || blockBox.contains(blockPos3)) {
				NbtCompound nbtCompound = structureEntityInfo.tag;
				Vec3d vec3d = method_11888(structureEntityInfo.pos, blockMirror, blockRotation, blockPos2);
				Vec3d vec3d2 = vec3d.add((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				NbtList nbtList = new NbtList();
				nbtList.add((NbtElement)(new NbtDouble(vec3d2.x)));
				nbtList.add((NbtElement)(new NbtDouble(vec3d2.y)));
				nbtList.add((NbtElement)(new NbtDouble(vec3d2.z)));
				nbtCompound.put("Pos", nbtList);
				nbtCompound.putUuid("UUID", UUID.randomUUID());

				Entity entity;
				try {
					entity = EntityType.method_15623(nbtCompound, iWorld.method_16348());
				} catch (Exception var16) {
					entity = null;
				}

				if (entity != null) {
					float f = entity.applyMirror(blockMirror);
					f += entity.yaw - entity.applyRotation(blockRotation);
					entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.pitch);
					iWorld.method_3686(entity);
				}
			}
		}
	}

	public BlockPos method_11885(BlockRotation blockRotation) {
		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
			default:
				return this.size;
		}
	}

	public static BlockPos method_11889(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos2) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		boolean bl = true;
		switch (blockMirror) {
			case LEFT_RIGHT:
				k = -k;
				break;
			case FRONT_BACK:
				i = -i;
				break;
			default:
				bl = false;
		}

		int l = blockPos2.getX();
		int m = blockPos2.getZ();
		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
				return new BlockPos(l - m + k, j, l + m - i);
			case CLOCKWISE_90:
				return new BlockPos(l + m - k, j, m - l + i);
			case CLOCKWISE_180:
				return new BlockPos(l + l - i, j, m + m - k);
			default:
				return bl ? new BlockPos(i, j, k) : blockPos;
		}
	}

	private static Vec3d method_11888(Vec3d vec3d, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		boolean bl = true;
		switch (blockMirror) {
			case LEFT_RIGHT:
				f = 1.0 - f;
				break;
			case FRONT_BACK:
				d = 1.0 - d;
				break;
			default:
				bl = false;
		}

		int i = blockPos.getX();
		int j = blockPos.getZ();
		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
				return new Vec3d((double)(i - j) + f, e, (double)(i + j + 1) - d);
			case CLOCKWISE_90:
				return new Vec3d((double)(i + j + 1) - f, e, (double)(j - i) + d);
			case CLOCKWISE_180:
				return new Vec3d((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
			default:
				return bl ? new Vec3d(d, e, f) : vec3d;
		}
	}

	public BlockPos method_13393(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation) {
		return method_13817(blockPos, blockMirror, blockRotation, this.getSize().getX(), this.getSize().getZ());
	}

	public static BlockPos method_13817(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, int i, int j) {
		i--;
		j--;
		int k = blockMirror == BlockMirror.FRONT_BACK ? i : 0;
		int l = blockMirror == BlockMirror.LEFT_RIGHT ? j : 0;
		BlockPos blockPos2 = blockPos;
		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
				blockPos2 = blockPos.add(l, 0, i - k);
				break;
			case CLOCKWISE_90:
				blockPos2 = blockPos.add(j - l, 0, k);
				break;
			case CLOCKWISE_180:
				blockPos2 = blockPos.add(i - k, 0, j - l);
				break;
			case NONE:
				blockPos2 = blockPos.add(k, 0, l);
		}

		return blockPos2;
	}

	public NbtCompound method_11891(NbtCompound nbtCompound) {
		if (this.blockInfoLists.isEmpty()) {
			nbtCompound.put("blocks", new NbtList());
			nbtCompound.put("palette", new NbtList());
		} else {
			List<Structure.class_3018> list = Lists.newArrayList();
			Structure.class_3018 lv = new Structure.class_3018();
			list.add(lv);

			for (int i = 1; i < this.blockInfoLists.size(); i++) {
				list.add(new Structure.class_3018());
			}

			NbtList nbtList = new NbtList();
			List<Structure.StructureBlockInfo> list2 = (List<Structure.StructureBlockInfo>)this.blockInfoLists.get(0);

			for (int j = 0; j < list2.size(); j++) {
				Structure.StructureBlockInfo structureBlockInfo = (Structure.StructureBlockInfo)list2.get(j);
				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound2.put("pos", this.method_11894(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
				int k = lv.method_13395(structureBlockInfo.state);
				nbtCompound2.putInt("state", k);
				if (structureBlockInfo.tag != null) {
					nbtCompound2.put("nbt", structureBlockInfo.tag);
				}

				nbtList.add((NbtElement)nbtCompound2);

				for (int l = 1; l < this.blockInfoLists.size(); l++) {
					Structure.class_3018 lv2 = (Structure.class_3018)list.get(l);
					lv2.method_13396(((Structure.StructureBlockInfo)((List)this.blockInfoLists.get(j)).get(j)).state, k);
				}
			}

			nbtCompound.put("blocks", nbtList);
			if (list.size() == 1) {
				NbtList nbtList2 = new NbtList();

				for (BlockState blockState : lv) {
					nbtList2.add((NbtElement)NbtHelper.method_20139(blockState));
				}

				nbtCompound.put("palette", nbtList2);
			} else {
				NbtList nbtList3 = new NbtList();

				for (Structure.class_3018 lv3 : list) {
					NbtList nbtList4 = new NbtList();

					for (BlockState blockState2 : lv3) {
						nbtList4.add((NbtElement)NbtHelper.method_20139(blockState2));
					}

					nbtList3.add((NbtElement)nbtList4);
				}

				nbtCompound.put("palettes", nbtList3);
			}
		}

		NbtList nbtList5 = new NbtList();

		for (Structure.StructureEntityInfo structureEntityInfo : this.entities) {
			NbtCompound nbtCompound3 = new NbtCompound();
			nbtCompound3.put("pos", this.method_11893(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
			nbtCompound3.put(
				"blockPos", this.method_11894(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ())
			);
			if (structureEntityInfo.tag != null) {
				nbtCompound3.put("nbt", structureEntityInfo.tag);
			}

			nbtList5.add((NbtElement)nbtCompound3);
		}

		nbtCompound.put("entities", nbtList5);
		nbtCompound.put("size", this.method_11894(this.size.getX(), this.size.getY(), this.size.getZ()));
		nbtCompound.putInt("DataVersion", 1631);
		return nbtCompound;
	}

	public void method_11897(NbtCompound nbtCompound) {
		this.blockInfoLists.clear();
		this.entities.clear();
		NbtList nbtList = nbtCompound.getList("size", 3);
		this.size = new BlockPos(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
		NbtList nbtList2 = nbtCompound.getList("blocks", 10);
		if (nbtCompound.contains("palettes", 9)) {
			NbtList nbtList3 = nbtCompound.getList("palettes", 9);

			for (int i = 0; i < nbtList3.size(); i++) {
				this.method_17699(nbtList3.getList(i), nbtList2);
			}
		} else {
			this.method_17699(nbtCompound.getList("palette", 10), nbtList2);
		}

		NbtList nbtList4 = nbtCompound.getList("entities", 10);

		for (int j = 0; j < nbtList4.size(); j++) {
			NbtCompound nbtCompound2 = nbtList4.getCompound(j);
			NbtList nbtList5 = nbtCompound2.getList("pos", 6);
			Vec3d vec3d = new Vec3d(nbtList5.getDouble(0), nbtList5.getDouble(1), nbtList5.getDouble(2));
			NbtList nbtList6 = nbtCompound2.getList("blockPos", 3);
			BlockPos blockPos = new BlockPos(nbtList6.getInt(0), nbtList6.getInt(1), nbtList6.getInt(2));
			if (nbtCompound2.contains("nbt")) {
				NbtCompound nbtCompound3 = nbtCompound2.getCompound("nbt");
				this.entities.add(new Structure.StructureEntityInfo(vec3d, blockPos, nbtCompound3));
			}
		}
	}

	private void method_17699(NbtList nbtList, NbtList nbtList2) {
		Structure.class_3018 lv = new Structure.class_3018();
		List<Structure.StructureBlockInfo> list = Lists.newArrayList();

		for (int i = 0; i < nbtList.size(); i++) {
			lv.method_13396(NbtHelper.toBlockState(nbtList.getCompound(i)), i);
		}

		for (int j = 0; j < nbtList2.size(); j++) {
			NbtCompound nbtCompound = nbtList2.getCompound(j);
			NbtList nbtList3 = nbtCompound.getList("pos", 3);
			BlockPos blockPos = new BlockPos(nbtList3.getInt(0), nbtList3.getInt(1), nbtList3.getInt(2));
			BlockState blockState = lv.method_13394(nbtCompound.getInt("state"));
			NbtCompound nbtCompound2;
			if (nbtCompound.contains("nbt")) {
				nbtCompound2 = nbtCompound.getCompound("nbt");
			} else {
				nbtCompound2 = null;
			}

			list.add(new Structure.StructureBlockInfo(blockPos, blockState, nbtCompound2));
		}

		this.blockInfoLists.add(list);
	}

	private NbtList method_11894(int... is) {
		NbtList nbtList = new NbtList();

		for (int i : is) {
			nbtList.add((NbtElement)(new NbtInt(i)));
		}

		return nbtList;
	}

	private NbtList method_11893(double... ds) {
		NbtList nbtList = new NbtList();

		for (double d : ds) {
			nbtList.add((NbtElement)(new NbtDouble(d)));
		}

		return nbtList;
	}

	public static class StructureBlockInfo {
		public final BlockPos pos;
		public final BlockState state;
		public final NbtCompound tag;

		public StructureBlockInfo(BlockPos blockPos, BlockState blockState, @Nullable NbtCompound nbtCompound) {
			this.pos = blockPos;
			this.state = blockState;
			this.tag = nbtCompound;
		}
	}

	public static class StructureEntityInfo {
		public final Vec3d pos;
		public final BlockPos blockPos;
		public final NbtCompound tag;

		public StructureEntityInfo(Vec3d vec3d, BlockPos blockPos, NbtCompound nbtCompound) {
			this.pos = vec3d;
			this.blockPos = blockPos;
			this.tag = nbtCompound;
		}
	}

	static class class_3018 implements Iterable<BlockState> {
		public static final BlockState AIR = Blocks.AIR.getDefaultState();
		private final IdList<BlockState> field_14879 = new IdList<>(16);
		private int field_14880;

		private class_3018() {
		}

		public int method_13395(BlockState state) {
			int i = this.field_14879.getId(state);
			if (i == -1) {
				i = this.field_14880++;
				this.field_14879.set(state, i);
			}

			return i;
		}

		@Nullable
		public BlockState method_13394(int i) {
			BlockState blockState = this.field_14879.fromId(i);
			return blockState == null ? AIR : blockState;
		}

		public Iterator<BlockState> iterator() {
			return this.field_14879.iterator();
		}

		public void method_13396(BlockState blockState, int i) {
			this.field_14879.set(blockState, i);
		}
	}
}
