package net.minecraft.block.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.class_2763;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class StructureBlockEntity extends BlockEntity {
	private String field_12858 = "";
	private String field_12859 = "";
	private String field_12860 = "";
	private BlockPos field_12861 = new BlockPos(1, 1, 1);
	private BlockPos field_12862 = BlockPos.ORIGIN;
	private BlockMirror field_12863 = BlockMirror.NONE;
	private BlockRotation field_12864 = BlockRotation.NONE;
	private StructureBlockEntity.class_2739 field_12865 = StructureBlockEntity.class_2739.DATA;
	private boolean field_12866;

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putString("name", this.field_12858);
		nbt.putString("author", this.field_12859);
		nbt.putString("metadata", this.field_12860);
		nbt.putInt("posX", this.field_12861.getX());
		nbt.putInt("posY", this.field_12861.getY());
		nbt.putInt("posZ", this.field_12861.getZ());
		nbt.putInt("sizeX", this.field_12862.getX());
		nbt.putInt("sizeY", this.field_12862.getY());
		nbt.putInt("sizeZ", this.field_12862.getZ());
		nbt.putString("rotation", this.field_12864.toString());
		nbt.putString("mirror", this.field_12863.toString());
		nbt.putString("mode", this.field_12865.toString());
		nbt.putBoolean("ignoreEntities", this.field_12866);
		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_12858 = nbt.getString("name");
		this.field_12859 = nbt.getString("author");
		this.field_12860 = nbt.getString("metadata");
		this.field_12861 = new BlockPos(nbt.getInt("posX"), nbt.getInt("posY"), nbt.getInt("posZ"));
		this.field_12862 = new BlockPos(nbt.getInt("sizeX"), nbt.getInt("sizeY"), nbt.getInt("sizeZ"));

		try {
			this.field_12864 = BlockRotation.valueOf(nbt.getString("rotation"));
		} catch (IllegalArgumentException var5) {
			this.field_12864 = BlockRotation.NONE;
		}

		try {
			this.field_12863 = BlockMirror.valueOf(nbt.getString("mirror"));
		} catch (IllegalArgumentException var4) {
			this.field_12863 = BlockMirror.NONE;
		}

		try {
			this.field_12865 = StructureBlockEntity.class_2739.valueOf(nbt.getString("mode"));
		} catch (IllegalArgumentException var3) {
			this.field_12865 = StructureBlockEntity.class_2739.DATA;
		}

		this.field_12866 = nbt.getBoolean("ignoreEntities");
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 7, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public void method_11673(String string) {
		this.field_12858 = string;
	}

	public void method_11677(BlockPos blockPos) {
		this.field_12861 = blockPos;
	}

	public void method_11679(BlockPos blockPos) {
		this.field_12862 = blockPos;
	}

	public void method_11667(BlockMirror blockMirror) {
		this.field_12863 = blockMirror;
	}

	public void method_11668(BlockRotation blockRotation) {
		this.field_12864 = blockRotation;
	}

	public void method_11678(String string) {
		this.field_12860 = string;
	}

	public void method_11669(StructureBlockEntity.class_2739 arg) {
		this.field_12865 = arg;
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
			this.world.setBlockState(this.getPos(), blockState.with(StructureBlock.field_12799, arg), 2);
		}
	}

	public void method_11675(boolean bl) {
		this.field_12866 = bl;
	}

	public boolean method_11680() {
		if (this.field_12865 != StructureBlockEntity.class_2739.SAVE) {
			return false;
		} else {
			BlockPos blockPos = this.getPos();
			int i = 128;
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 128, 0, blockPos.getZ() - 128);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 128, 255, blockPos.getZ() + 128);
			List<StructureBlockEntity> list = this.method_11671(blockPos2, blockPos3);
			List<StructureBlockEntity> list2 = this.method_11674(list);
			if (list2.size() < 1) {
				return false;
			} else {
				BlockBox blockBox = this.method_11672(blockPos, list2);
				if (blockBox.maxX - blockBox.minX > 1 && blockBox.maxY - blockBox.minY > 1 && blockBox.maxZ - blockBox.minZ > 1) {
					this.field_12861 = new BlockPos(blockBox.minX - blockPos.getX() + 1, blockBox.minY - blockPos.getY() + 1, blockBox.minZ - blockPos.getZ() + 1);
					this.field_12862 = new BlockPos(blockBox.maxX - blockBox.minX - 1, blockBox.maxY - blockBox.minY - 1, blockBox.maxZ - blockBox.minZ - 1);
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.method_11481(blockPos, blockState, blockState, 3);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private List<StructureBlockEntity> method_11674(List<StructureBlockEntity> list) {
		Iterable<StructureBlockEntity> iterable = Iterables.filter(
			list,
			new Predicate<StructureBlockEntity>() {
				public boolean apply(@Nullable StructureBlockEntity structureBlockEntity) {
					return structureBlockEntity.field_12865 == StructureBlockEntity.class_2739.CORNER
						&& StructureBlockEntity.this.field_12858.equals(structureBlockEntity.field_12858);
				}
			}
		);
		return Lists.newArrayList(iterable);
	}

	private List<StructureBlockEntity> method_11671(BlockPos blockPos, BlockPos blockPos2) {
		List<StructureBlockEntity> list = Lists.newArrayList();

		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos, blockPos2)) {
			BlockState blockState = this.world.getBlockState(mutable);
			if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
				BlockEntity blockEntity = this.world.getBlockEntity(mutable);
				if (blockEntity != null && blockEntity instanceof StructureBlockEntity) {
					list.add((StructureBlockEntity)blockEntity);
				}
			}
		}

		return list;
	}

	private BlockBox method_11672(BlockPos blockPos, List<StructureBlockEntity> list) {
		BlockBox blockBox;
		if (list.size() > 1) {
			BlockPos blockPos2 = ((StructureBlockEntity)list.get(0)).getPos();
			blockBox = new BlockBox(blockPos2, blockPos2);
		} else {
			blockBox = new BlockBox(blockPos, blockPos);
		}

		for (StructureBlockEntity structureBlockEntity : list) {
			BlockPos blockPos3 = structureBlockEntity.getPos();
			if (blockPos3.getX() < blockBox.minX) {
				blockBox.minX = blockPos3.getX();
			} else if (blockPos3.getX() > blockBox.maxX) {
				blockBox.maxX = blockPos3.getX();
			}

			if (blockPos3.getY() < blockBox.minY) {
				blockBox.minY = blockPos3.getY();
			} else if (blockPos3.getY() > blockBox.maxY) {
				blockBox.maxY = blockPos3.getY();
			}

			if (blockPos3.getZ() < blockBox.minZ) {
				blockBox.minZ = blockPos3.getZ();
			} else if (blockPos3.getZ() > blockBox.maxZ) {
				blockBox.maxZ = blockPos3.getZ();
			}
		}

		return blockBox;
	}

	public boolean method_11681() {
		if (this.field_12865 == StructureBlockEntity.class_2739.SAVE && !this.world.isClient) {
			BlockPos blockPos = this.getPos().add(this.field_12861);
			ServerWorld serverWorld = (ServerWorld)this.world;
			MinecraftServer minecraftServer = this.world.getServer();
			class_2763 lv = serverWorld.method_12783();
			Structure structure = lv.method_11861(minecraftServer, new Identifier(this.field_12858));
			structure.saveFromWorld(this.world, blockPos, this.field_12862, !this.field_12866, Blocks.BARRIER);
			structure.setAuthor(this.field_12859);
			lv.method_11863(minecraftServer, new Identifier(this.field_12858));
			return true;
		} else {
			return false;
		}
	}

	public boolean method_11682() {
		if (this.field_12865 == StructureBlockEntity.class_2739.LOAD && !this.world.isClient) {
			BlockPos blockPos = this.getPos().add(this.field_12861);
			ServerWorld serverWorld = (ServerWorld)this.world;
			MinecraftServer minecraftServer = this.world.getServer();
			class_2763 lv = serverWorld.method_12783();
			Structure structure = lv.method_11861(minecraftServer, new Identifier(this.field_12858));
			if (!ChatUtil.isEmpty(structure.getAuthor())) {
				this.field_12859 = structure.getAuthor();
			}

			if (!this.field_12862.equals(structure.getSize())) {
				this.field_12862 = structure.getSize();
				return false;
			} else {
				BlockPos blockPos2 = structure.method_11885(this.field_12864);

				for (Entity entity : this.world.getEntitiesIn(null, new Box(blockPos, blockPos2.add(blockPos).add(-1, -1, -1)))) {
					this.world.method_3700(entity);
				}

				StructurePlacementData structurePlacementData = new StructurePlacementData()
					.method_11867(this.field_12863)
					.method_11868(this.field_12864)
					.method_11870(this.field_12866)
					.method_11865(null)
					.method_11866(null)
					.method_11873(false);
				structure.method_11882(this.world, blockPos, structurePlacementData);
				return true;
			}
		} else {
			return false;
		}
	}

	public static enum class_2739 implements StringIdentifiable {
		SAVE("save", 0),
		LOAD("load", 1),
		CORNER("corner", 2),
		DATA("data", 3);

		private static final StructureBlockEntity.class_2739[] field_12872 = new StructureBlockEntity.class_2739[values().length];
		private final String field_12873;
		private final int field_12874;

		private class_2739(String string2, int j) {
			this.field_12873 = string2;
			this.field_12874 = j;
		}

		@Override
		public String asString() {
			return this.field_12873;
		}

		public int method_11684() {
			return this.field_12874;
		}

		public static StructureBlockEntity.class_2739 method_11685(int i) {
			if (i < 0 || i >= field_12872.length) {
				i = 0;
			}

			return field_12872[i];
		}

		static {
			for (StructureBlockEntity.class_2739 lv : values()) {
				field_12872[lv.method_11684()] = lv;
			}
		}
	}
}
