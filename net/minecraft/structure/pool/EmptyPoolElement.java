package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class EmptyPoolElement extends StructurePoolElement {
	public static final Codec<EmptyPoolElement> CODEC = Codec.unit(() -> EmptyPoolElement.INSTANCE);
	public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

	private EmptyPoolElement() {
		super(StructurePool.Projection.TERRAIN_MATCHING);
	}

	@Override
	public Vec3i getStart(StructureManager structureManager, BlockRotation rotation) {
		return Vec3i.ZERO;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		return Collections.emptyList();
	}

	@Override
	public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
		throw new IllegalStateException("Invalid call to EmtyPoolElement.getBoundingBox, filter me!");
	}

	@Override
	public boolean generate(
		StructureManager structureManager,
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos pos,
		BlockPos blockPos,
		BlockRotation rotation,
		BlockBox box,
		Random random,
		boolean keepJigsaws
	) {
		return true;
	}

	@Override
	public StructurePoolElementType<?> getType() {
		return StructurePoolElementType.EMPTY_POOL_ELEMENT;
	}

	public String toString() {
		return "Empty";
	}
}
