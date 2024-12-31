package net.minecraft.structure;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3016;
import net.minecraft.class_3017;
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
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.collection.IdList;
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
			BlockPos blockPos2 = method_11886(structurePlacementData, structureBlockInfo.pos).add(blockPos);
			if (blockBox == null || blockBox.contains(blockPos2)) {
				BlockState blockState = structureBlockInfo.state;
				if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK && structureBlockInfo.tag != null) {
					StructureBlockEntity.class_2739 lv = StructureBlockEntity.class_2739.valueOf(structureBlockInfo.tag.getString("mode"));
					if (lv == StructureBlockEntity.class_2739.DATA) {
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
		return method_11889(blockPos, structurePlacementData.method_11871(), structurePlacementData.method_11874());
	}

	public void method_11882(World world, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		structurePlacementData.method_11879();
		this.method_11896(world, blockPos, structurePlacementData);
	}

	public void method_11896(World world, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.method_13392(world, blockPos, new class_3016(blockPos, structurePlacementData), structurePlacementData, 2);
	}

	public void method_13391(World world, BlockPos blockPos, StructurePlacementData structurePlacementData, int i) {
		this.method_13392(world, blockPos, new class_3016(blockPos, structurePlacementData), structurePlacementData, i);
	}

	public void method_13392(World world, BlockPos blockPos, @Nullable class_3017 arg, StructurePlacementData structurePlacementData, int i) {
		if (!this.blockInfoLists.isEmpty() && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
			Block block = structurePlacementData.method_11876();
			BlockBox blockBox = structurePlacementData.method_11877();

			for (Structure.StructureBlockInfo structureBlockInfo : this.blockInfoLists) {
				BlockPos blockPos2 = method_11886(structurePlacementData, structureBlockInfo.pos).add(blockPos);
				Structure.StructureBlockInfo structureBlockInfo2 = arg != null ? arg.method_13390(world, blockPos2, structureBlockInfo) : structureBlockInfo;
				if (structureBlockInfo2 != null) {
					Block block2 = structureBlockInfo2.state.getBlock();
					if ((block == null || block != block2)
						&& (!structurePlacementData.method_11878() || block2 != Blocks.STRUCTURE_BLOCK)
						&& (blockBox == null || blockBox.contains(blockPos2))) {
						BlockState blockState = structureBlockInfo2.state.withMirror(structurePlacementData.method_11871());
						BlockState blockState2 = blockState.withRotation(structurePlacementData.method_11874());
						if (structureBlockInfo2.tag != null) {
							BlockEntity blockEntity = world.getBlockEntity(blockPos2);
							if (blockEntity != null) {
								if (blockEntity instanceof Inventory) {
									((Inventory)blockEntity).clear();
								}

								world.setBlockState(blockPos2, Blocks.BARRIER.getDefaultState(), 4);
							}
						}

						if (world.setBlockState(blockPos2, blockState2, i) && structureBlockInfo2.tag != null) {
							BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
							if (blockEntity2 != null) {
								structureBlockInfo2.tag.putInt("x", blockPos2.getX());
								structureBlockInfo2.tag.putInt("y", blockPos2.getY());
								structureBlockInfo2.tag.putInt("z", blockPos2.getZ());
								blockEntity2.fromNbt(structureBlockInfo2.tag);
								blockEntity2.method_13321(structurePlacementData.method_11871());
								blockEntity2.method_13322(structurePlacementData.method_11874());
							}
						}
					}
				}
			}

			for (Structure.StructureBlockInfo structureBlockInfo3 : this.blockInfoLists) {
				if (block == null || block != structureBlockInfo3.state.getBlock()) {
					BlockPos blockPos3 = method_11886(structurePlacementData, structureBlockInfo3.pos).add(blockPos);
					if (blockBox == null || blockBox.contains(blockPos3)) {
						world.updateNeighbors(blockPos3, structureBlockInfo3.state.getBlock());
						if (structureBlockInfo3.tag != null) {
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
			BlockPos blockPos2 = method_11889(structureEntityInfo.blockPos, blockMirror, blockRotation).add(blockPos);
			if (blockBox == null || blockBox.contains(blockPos2)) {
				NbtCompound nbtCompound = structureEntityInfo.tag;
				Vec3d vec3d = method_11888(structureEntityInfo.pos, blockMirror, blockRotation);
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
					float f = entity.applyMirror(blockMirror);
					f += entity.yaw - entity.applyRotation(blockRotation);
					entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.pitch);
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

	public BlockPos method_13393(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation) {
		int i = this.getSize().getX() - 1;
		int j = this.getSize().getZ() - 1;
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
		Structure.class_3018 lv = new Structure.class_3018();
		NbtList nbtList = new NbtList();

		for (Structure.StructureBlockInfo structureBlockInfo : this.blockInfoLists) {
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("pos", this.method_11894(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
			nbtCompound2.putInt("state", lv.method_13395(structureBlockInfo.state));
			if (structureBlockInfo.tag != null) {
				nbtCompound2.put("nbt", structureBlockInfo.tag);
			}

			nbtList.add(nbtCompound2);
		}

		NbtList nbtList2 = new NbtList();

		for (Structure.StructureEntityInfo structureEntityInfo : this.entities) {
			NbtCompound nbtCompound3 = new NbtCompound();
			nbtCompound3.put("pos", this.method_11893(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
			nbtCompound3.put(
				"blockPos", this.method_11894(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ())
			);
			if (structureEntityInfo.tag != null) {
				nbtCompound3.put("nbt", structureEntityInfo.tag);
			}

			nbtList2.add(nbtCompound3);
		}

		NbtList nbtList3 = new NbtList();

		for (BlockState blockState : lv) {
			nbtList3.add(NbtHelper.fromBlockState(new NbtCompound(), blockState));
		}

		nbtCompound.put("palette", nbtList3);
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
		Structure.class_3018 lv = new Structure.class_3018();
		NbtList nbtList2 = nbtCompound.getList("palette", 10);

		for (int i = 0; i < nbtList2.size(); i++) {
			lv.method_13396(NbtHelper.toBlockState(nbtList2.getCompound(i)), i);
		}

		NbtList nbtList3 = nbtCompound.getList("blocks", 10);

		for (int j = 0; j < nbtList3.size(); j++) {
			NbtCompound nbtCompound2 = nbtList3.getCompound(j);
			NbtList nbtList4 = nbtCompound2.getList("pos", 3);
			BlockPos blockPos = new BlockPos(nbtList4.getInt(0), nbtList4.getInt(1), nbtList4.getInt(2));
			BlockState blockState = lv.method_13394(nbtCompound2.getInt("state"));
			NbtCompound nbtCompound3;
			if (nbtCompound2.contains("nbt")) {
				nbtCompound3 = nbtCompound2.getCompound("nbt");
			} else {
				nbtCompound3 = null;
			}

			this.blockInfoLists.add(new Structure.StructureBlockInfo(blockPos, blockState, nbtCompound3));
		}

		NbtList nbtList5 = nbtCompound.getList("entities", 10);

		for (int k = 0; k < nbtList5.size(); k++) {
			NbtCompound nbtCompound5 = nbtList5.getCompound(k);
			NbtList nbtList6 = nbtCompound5.getList("pos", 6);
			Vec3d vec3d = new Vec3d(nbtList6.getDouble(0), nbtList6.getDouble(1), nbtList6.getDouble(2));
			NbtList nbtList7 = nbtCompound5.getList("blockPos", 3);
			BlockPos blockPos2 = new BlockPos(nbtList7.getInt(0), nbtList7.getInt(1), nbtList7.getInt(2));
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
		final IdList<BlockState> field_14879 = new IdList<>(16);
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
