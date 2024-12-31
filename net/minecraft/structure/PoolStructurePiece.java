package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PoolStructurePiece extends StructurePiece {
	private static final Logger field_24991 = LogManager.getLogger();
	protected final StructurePoolElement poolElement;
	protected BlockPos pos;
	private final int groundLevelDelta;
	protected final BlockRotation rotation;
	private final List<JigsawJunction> junctions = Lists.newArrayList();
	private final StructureManager structureManager;

	public PoolStructurePiece(
		StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, BlockRotation blockRotation, BlockBox blockBox
	) {
		super(StructurePieceType.JIGSAW, 0);
		this.structureManager = structureManager;
		this.poolElement = structurePoolElement;
		this.pos = blockPos;
		this.groundLevelDelta = i;
		this.rotation = blockRotation;
		this.boundingBox = blockBox;
	}

	public PoolStructurePiece(StructureManager manager, CompoundTag tag) {
		super(StructurePieceType.JIGSAW, tag);
		this.structureManager = manager;
		this.pos = new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
		this.groundLevelDelta = tag.getInt("ground_level_delta");
		this.poolElement = (StructurePoolElement)StructurePoolElement.CODEC
			.parse(NbtOps.INSTANCE, tag.getCompound("pool_element"))
			.resultOrPartial(field_24991::error)
			.orElse(EmptyPoolElement.INSTANCE);
		this.rotation = BlockRotation.valueOf(tag.getString("rotation"));
		this.boundingBox = this.poolElement.getBoundingBox(manager, this.pos, this.rotation);
		ListTag listTag = tag.getList("junctions", 10);
		this.junctions.clear();
		listTag.forEach(tagx -> this.junctions.add(JigsawJunction.method_28873(new Dynamic(NbtOps.INSTANCE, tagx))));
	}

	@Override
	protected void toNbt(CompoundTag tag) {
		tag.putInt("PosX", this.pos.getX());
		tag.putInt("PosY", this.pos.getY());
		tag.putInt("PosZ", this.pos.getZ());
		tag.putInt("ground_level_delta", this.groundLevelDelta);
		StructurePoolElement.CODEC
			.encodeStart(NbtOps.INSTANCE, this.poolElement)
			.resultOrPartial(field_24991::error)
			.ifPresent(tagx -> tag.put("pool_element", tagx));
		tag.putString("rotation", this.rotation.name());
		ListTag listTag = new ListTag();

		for (JigsawJunction jigsawJunction : this.junctions) {
			listTag.add(jigsawJunction.serialize(NbtOps.INSTANCE).getValue());
		}

		tag.put("junctions", listTag);
	}

	@Override
	public boolean generate(
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos blockPos
	) {
		return this.method_27236(structureWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, blockPos, false);
	}

	public boolean method_27236(
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox blockBox,
		BlockPos blockPos,
		boolean keepJigsaws
	) {
		return this.poolElement
			.generate(this.structureManager, structureWorldAccess, structureAccessor, chunkGenerator, this.pos, blockPos, this.rotation, blockBox, random, keepJigsaws);
	}

	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.pos = this.pos.add(x, y, z);
	}

	@Override
	public BlockRotation getRotation() {
		return this.rotation;
	}

	public String toString() {
		return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.poolElement);
	}

	public StructurePoolElement getPoolElement() {
		return this.poolElement;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getGroundLevelDelta() {
		return this.groundLevelDelta;
	}

	public void addJunction(JigsawJunction junction) {
		this.junctions.add(junction);
	}

	public List<JigsawJunction> getJunctions() {
		return this.junctions;
	}
}
