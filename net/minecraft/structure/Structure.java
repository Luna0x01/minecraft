package net.minecraft.structure;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Structure {
	private final List<Structure.StructureBlockInfo> blockInfoLists = Lists.newArrayList();
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
					} else if (!blockState.isFullBlock() && !blockState.method_11730()) {
						list3.add(new Structure.StructureBlockInfo(blockPos4, blockState, null));
					} else {
						list.add(new Structure.StructureBlockInfo(blockPos4, blockState, null));
					}
				}
			}

			this.blockInfoLists.clear();
			this.blockInfoLists.addAll(list);
			this.blockInfoLists.addAll(list2);
			this.blockInfoLists.addAll(list3);
			if (includeEntities) {
				this.method_11883(world, blockPos2, blockPos3.add(1, 1, 1));
			} else {
				this.entities.clear();
			}
		}
	}

	private void method_11883(World world, BlockPos blockPos, BlockPos blockPos2) {
		List<Entity> list = world.getEntitiesInBox(Entity.class, new Box(blockPos, blockPos2), new Predicate<Entity>() {
			public boolean apply(@Nullable Entity entity) {
				return !(entity instanceof PlayerEntity);
			}
		});
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

		for (Structure.StructureBlockInfo structureBlockInfo : this.blockInfoLists) {
			BlockPos blockPos2 = method_11886(structurePlacementData, structureBlockInfo.field_13038).add(blockPos);
			if (blockBox == null || blockBox.contains(blockPos2)) {
				BlockState blockState = structureBlockInfo.field_13039;
				if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK && structureBlockInfo.field_13040 != null) {
					StructureBlockEntity.class_2739 lv = StructureBlockEntity.class_2739.valueOf(structureBlockInfo.field_13040.getString("mode"));
					if (lv == StructureBlockEntity.class_2739.DATA) {
						map.put(blockPos2, structureBlockInfo.field_13040.getString("metadata"));
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
		return method_11889(blockPos, structurePlacementData.method_11871(), structurePlacementData.method_11874());
	}

	public void method_11882(World world, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		structurePlacementData.method_11879();
		this.method_11896(world, blockPos, structurePlacementData);
	}

	public void method_11896(World world, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		if (!this.blockInfoLists.isEmpty() && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
			Block block = structurePlacementData.method_11876();
			BlockBox blockBox = structurePlacementData.method_11877();

			for (Structure.StructureBlockInfo structureBlockInfo : this.blockInfoLists) {
				Block block2 = structureBlockInfo.field_13039.getBlock();
				if ((block == null || block != block2) && (!structurePlacementData.method_11878() || block2 != Blocks.STRUCTURE_BLOCK)) {
					BlockPos blockPos2 = method_11886(structurePlacementData, structureBlockInfo.field_13038).add(blockPos);
					if (blockBox == null || blockBox.contains(blockPos2)) {
						BlockState blockState = structureBlockInfo.field_13039.withMirror(structurePlacementData.method_11871());
						BlockState blockState2 = blockState.withRotation(structurePlacementData.method_11874());
						if (structureBlockInfo.field_13040 != null) {
							BlockEntity blockEntity = world.getBlockEntity(blockPos2);
							if (blockEntity != null) {
								if (blockEntity instanceof Inventory) {
									((Inventory)blockEntity).clear();
								}

								world.setBlockState(blockPos2, Blocks.BARRIER.getDefaultState(), 4);
							}
						}

						if (world.setBlockState(blockPos2, blockState2, 2) && structureBlockInfo.field_13040 != null) {
							BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
							if (blockEntity2 != null) {
								structureBlockInfo.field_13040.putInt("x", blockPos2.getX());
								structureBlockInfo.field_13040.putInt("y", blockPos2.getY());
								structureBlockInfo.field_13040.putInt("z", blockPos2.getZ());
								blockEntity2.fromNbt(structureBlockInfo.field_13040);
							}
						}
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo2 : this.blockInfoLists) {
				if (block == null || block != structureBlockInfo2.field_13039.getBlock()) {
					BlockPos blockPos3 = method_11886(structurePlacementData, structureBlockInfo2.field_13038).add(blockPos);
					if (blockBox == null || blockBox.contains(blockPos3)) {
						world.updateNeighbors(blockPos3, structureBlockInfo2.field_13039.getBlock());
						if (structureBlockInfo2.field_13040 != null) {
							BlockEntity blockEntity3 = world.getBlockEntity(blockPos3);
							if (blockEntity3 != null) {
								blockEntity3.markDirty();
							}
						}
					}
				}
			}

			if (!structurePlacementData.method_11875()) {
				this.method_11881(world, blockPos, structurePlacementData.method_11871(), structurePlacementData.method_11874(), blockBox);
			}
		}
	}

	private void method_11881(World world, BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, @Nullable BlockBox blockBox) {
		for (Structure.StructureEntityInfo structureEntityInfo : this.entities) {
			BlockPos blockPos2 = method_11889(structureEntityInfo.field_13042, blockMirror, blockRotation).add(blockPos);
			if (blockBox == null || blockBox.contains(blockPos2)) {
				NbtCompound nbtCompound = structureEntityInfo.field_13043;
				Vec3d vec3d = method_11888(structureEntityInfo.field_13041, blockMirror, blockRotation);
				Vec3d vec3d2 = vec3d.add((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				NbtList nbtList = new NbtList();
				nbtList.add(new NbtDouble(vec3d2.x));
				nbtList.add(new NbtDouble(vec3d2.y));
				nbtList.add(new NbtDouble(vec3d2.z));
				nbtCompound.put("Pos", nbtList);
				nbtCompound.putUuid("UUID", UUID.randomUUID());

				Entity entity;
				try {
					entity = EntityType.createInstanceFromNbt(nbtCompound, world);
				} catch (Exception var15) {
					entity = null;
				}

				if (entity != null) {
					if (entity instanceof PaintingEntity) {
						entity.applyMirror(blockMirror);
						entity.applyRotation(blockRotation);
						entity.updatePosition((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
						entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, entity.yaw, entity.pitch);
					} else {
						float f = entity.applyMirror(blockMirror);
						f += entity.yaw - entity.applyRotation(blockRotation);
						entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.pitch);
					}

					world.spawnEntity(entity);
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

	private static BlockPos method_11889(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation) {
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

		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
				return new BlockPos(k, j, -i);
			case CLOCKWISE_90:
				return new BlockPos(-k, j, i);
			case CLOCKWISE_180:
				return new BlockPos(-i, j, -k);
			default:
				return bl ? new BlockPos(i, j, k) : blockPos;
		}
	}

	private static Vec3d method_11888(Vec3d vec3d, BlockMirror blockMirror, BlockRotation blockRotation) {
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

		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
				return new Vec3d(f, e, 1.0 - d);
			case CLOCKWISE_90:
				return new Vec3d(1.0 - f, e, d);
			case CLOCKWISE_180:
				return new Vec3d(1.0 - d, e, 1.0 - f);
			default:
				return bl ? new Vec3d(d, e, f) : vec3d;
		}
	}

	public NbtCompound method_11891(NbtCompound nbtCompound) {
		NbtList nbtList = new NbtList();

		for (Structure.StructureBlockInfo structureBlockInfo : this.blockInfoLists) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put(
				"pos", this.method_11894(structureBlockInfo.field_13038.getX(), structureBlockInfo.field_13038.getY(), structureBlockInfo.field_13038.getZ())
			);
			nbtCompound2.putInt("state", Block.getByBlockState(structureBlockInfo.field_13039));
			if (structureBlockInfo.field_13040 != null) {
				nbtCompound2.put("nbt", structureBlockInfo.field_13040);
			}

			nbtList.add(nbtCompound2);
		}

		NbtList nbtList2 = new NbtList();

		for (Structure.StructureEntityInfo structureEntityInfo : this.entities) {
			NbtCompound nbtCompound3 = new NbtCompound();
			nbtCompound3.put("pos", this.method_11893(structureEntityInfo.field_13041.x, structureEntityInfo.field_13041.y, structureEntityInfo.field_13041.z));
			nbtCompound3.put(
				"blockPos", this.method_11894(structureEntityInfo.field_13042.getX(), structureEntityInfo.field_13042.getY(), structureEntityInfo.field_13042.getZ())
			);
			if (structureEntityInfo.field_13043 != null) {
				nbtCompound3.put("nbt", structureEntityInfo.field_13043);
			}

			nbtList2.add(nbtCompound3);
		}

		nbtCompound.put("blocks", nbtList);
		nbtCompound.put("entities", nbtList2);
		nbtCompound.put("size", this.method_11894(this.size.getX(), this.size.getY(), this.size.getZ()));
		nbtCompound.putInt("version", 1);
		nbtCompound.putString("author", this.author);
		return nbtCompound;
	}

	public void method_11897(NbtCompound nbtCompound) {
		this.blockInfoLists.clear();
		this.entities.clear();
		NbtList nbtList = nbtCompound.getList("size", 3);
		this.size = new BlockPos(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
		this.author = nbtCompound.getString("author");
		NbtList nbtList2 = nbtCompound.getList("blocks", 10);

		for (int i = 0; i < nbtList2.size(); i++) {
			NbtCompound nbtCompound2 = nbtList2.getCompound(i);
			NbtList nbtList3 = nbtCompound2.getList("pos", 3);
			BlockPos blockPos = new BlockPos(nbtList3.getInt(0), nbtList3.getInt(1), nbtList3.getInt(2));
			int j = nbtCompound2.getInt("state");
			BlockState blockState = Block.getStateFromRawId(j);
			NbtCompound nbtCompound3;
			if (nbtCompound2.contains("nbt")) {
				nbtCompound3 = nbtCompound2.getCompound("nbt");
			} else {
				nbtCompound3 = null;
			}

			this.blockInfoLists.add(new Structure.StructureBlockInfo(blockPos, blockState, nbtCompound3));
		}

		NbtList nbtList4 = nbtCompound.getList("entities", 10);

		for (int k = 0; k < nbtList4.size(); k++) {
			NbtCompound nbtCompound5 = nbtList4.getCompound(k);
			NbtList nbtList5 = nbtCompound5.getList("pos", 6);
			Vec3d vec3d = new Vec3d(nbtList5.getDouble(0), nbtList5.getDouble(1), nbtList5.getDouble(2));
			NbtList nbtList6 = nbtCompound5.getList("blockPos", 3);
			BlockPos blockPos2 = new BlockPos(nbtList6.getInt(0), nbtList6.getInt(1), nbtList6.getInt(2));
			if (nbtCompound5.contains("nbt")) {
				NbtCompound nbtCompound6 = nbtCompound5.getCompound("nbt");
				this.entities.add(new Structure.StructureEntityInfo(vec3d, blockPos2, nbtCompound6));
			}
		}
	}

	private NbtList method_11894(int... is) {
		NbtList nbtList = new NbtList();

		for (int k : is) {
			nbtList.add(new NbtInt(k));
		}

		return nbtList;
	}

	private NbtList method_11893(double... ds) {
		NbtList nbtList = new NbtList();

		for (double d : ds) {
			nbtList.add(new NbtDouble(d));
		}

		return nbtList;
	}

	static class StructureBlockInfo {
		public final BlockPos field_13038;
		public final BlockState field_13039;
		public final NbtCompound field_13040;

		private StructureBlockInfo(BlockPos blockPos, BlockState blockState, @Nullable NbtCompound nbtCompound) {
			this.field_13038 = blockPos;
			this.field_13039 = blockState;
			this.field_13040 = nbtCompound;
		}
	}

	static class StructureEntityInfo {
		public final Vec3d field_13041;
		public final BlockPos field_13042;
		public final NbtCompound field_13043;

		private StructureEntityInfo(Vec3d vec3d, BlockPos blockPos, NbtCompound nbtCompound) {
			this.field_13041 = vec3d;
			this.field_13042 = blockPos;
			this.field_13043 = nbtCompound;
		}
	}
}
