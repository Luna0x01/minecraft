package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructurePoolRegistry REGISTRY = new StructurePoolRegistry();

	public static void addPieces(
		Identifier identifier,
		int i,
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		List<StructurePiece> list,
		Random random
	) {
		StructureFeatures.initialize();
		new StructurePoolBasedGenerator.StructurePoolGenerator(identifier, i, pieceFactory, chunkGenerator, structureManager, blockPos, list, random);
	}

	static {
		REGISTRY.add(StructurePool.EMPTY);
	}

	public interface PieceFactory {
		PoolStructurePiece create(
			StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, BlockRotation blockRotation, BlockBox blockBox
		);
	}

	static final class ShapedPoolStructurePiece {
		private final PoolStructurePiece piece;
		private final AtomicReference<VoxelShape> pieceShape;
		private final int minY;
		private final int currentSize;

		private ShapedPoolStructurePiece(PoolStructurePiece poolStructurePiece, AtomicReference<VoxelShape> atomicReference, int i, int j) {
			this.piece = poolStructurePiece;
			this.pieceShape = atomicReference;
			this.minY = i;
			this.currentSize = j;
		}
	}

	static final class StructurePoolGenerator {
		private final int maxSize;
		private final StructurePoolBasedGenerator.PieceFactory pieceFactory;
		private final ChunkGenerator<?> chunkGenerator;
		private final StructureManager structureManager;
		private final List<StructurePiece> children;
		private final Random random;
		private final Deque<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

		public StructurePoolGenerator(
			Identifier identifier,
			int i,
			StructurePoolBasedGenerator.PieceFactory pieceFactory,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			BlockPos blockPos,
			List<StructurePiece> list,
			Random random
		) {
			this.maxSize = i;
			this.pieceFactory = pieceFactory;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.children = list;
			this.random = random;
			BlockRotation blockRotation = BlockRotation.random(random);
			StructurePool structurePool = StructurePoolBasedGenerator.REGISTRY.get(identifier);
			StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
			PoolStructurePiece poolStructurePiece = pieceFactory.create(
				structureManager,
				structurePoolElement,
				blockPos,
				structurePoolElement.method_19308(),
				blockRotation,
				structurePoolElement.getBoundingBox(structureManager, blockPos, blockRotation)
			);
			BlockBox blockBox = poolStructurePiece.getBoundingBox();
			int j = (blockBox.maxX + blockBox.minX) / 2;
			int k = (blockBox.maxZ + blockBox.minZ) / 2;
			int l = chunkGenerator.method_20402(j, k, Heightmap.Type.field_13194);
			poolStructurePiece.translate(0, l - (blockBox.minY + poolStructurePiece.getGroundLevelDelta()), 0);
			list.add(poolStructurePiece);
			if (i > 0) {
				int m = 80;
				Box box = new Box((double)(j - 80), (double)(l - 80), (double)(k - 80), (double)(j + 80 + 1), (double)(l + 80 + 1), (double)(k + 80 + 1));
				this.structurePieces
					.addLast(
						new StructurePoolBasedGenerator.ShapedPoolStructurePiece(
							poolStructurePiece,
							new AtomicReference(VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST)),
							l + 80,
							0
						)
					);

				while (!this.structurePieces.isEmpty()) {
					StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = (StructurePoolBasedGenerator.ShapedPoolStructurePiece)this.structurePieces
						.removeFirst();
					this.generatePiece(
						shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.minY, shapedPoolStructurePiece.currentSize
					);
				}
			}
		}

		private void generatePiece(PoolStructurePiece poolStructurePiece, AtomicReference<VoxelShape> atomicReference, int i, int j) {
			StructurePoolElement structurePoolElement = poolStructurePiece.getPoolElement();
			BlockPos blockPos = poolStructurePiece.getPos();
			BlockRotation blockRotation = poolStructurePiece.getRotation();
			StructurePool.Projection projection = structurePoolElement.getProjection();
			boolean bl = projection == StructurePool.Projection.field_16687;
			AtomicReference<VoxelShape> atomicReference2 = new AtomicReference();
			BlockBox blockBox = poolStructurePiece.getBoundingBox();
			int k = blockBox.minY;

			label121:
			for (Structure.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(
				this.structureManager, blockPos, blockRotation, this.random
			)) {
				Direction direction = structureBlockInfo.state.get(JigsawBlock.FACING);
				BlockPos blockPos2 = structureBlockInfo.pos;
				BlockPos blockPos3 = blockPos2.offset(direction);
				int l = blockPos2.getY() - k;
				int m = -1;
				StructurePool structurePool = StructurePoolBasedGenerator.REGISTRY.get(new Identifier(structureBlockInfo.tag.getString("target_pool")));
				StructurePool structurePool2 = StructurePoolBasedGenerator.REGISTRY.get(structurePool.getTerminatorsId());
				if (structurePool != StructurePool.INVALID && (structurePool.getElementCount() != 0 || structurePool == StructurePool.EMPTY)) {
					boolean bl2 = blockBox.contains(blockPos3);
					AtomicReference<VoxelShape> atomicReference3;
					int n;
					if (bl2) {
						atomicReference3 = atomicReference2;
						n = k;
						if (atomicReference2.get() == null) {
							atomicReference2.set(VoxelShapes.cuboid(Box.from(blockBox)));
						}
					} else {
						atomicReference3 = atomicReference;
						n = i;
					}

					List<StructurePoolElement> list = Lists.newArrayList();
					if (j != this.maxSize) {
						list.addAll(structurePool.getElementIndicesInRandomOrder(this.random));
					}

					list.addAll(structurePool2.getElementIndicesInRandomOrder(this.random));

					for (StructurePoolElement structurePoolElement2 : list) {
						if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
							break;
						}

						for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
							List<Structure.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(
								this.structureManager, BlockPos.ORIGIN, blockRotation2, this.random
							);
							BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureManager, BlockPos.ORIGIN, blockRotation2);
							int p;
							if (blockBox2.getBlockCountY() > 16) {
								p = 0;
							} else {
								p = list2.stream().mapToInt(structureBlockInfox -> {
									if (!blockBox2.contains(structureBlockInfox.pos.offset(structureBlockInfox.state.get(JigsawBlock.FACING)))) {
										return 0;
									} else {
										Identifier identifier = new Identifier(structureBlockInfox.tag.getString("target_pool"));
										StructurePool structurePoolx = StructurePoolBasedGenerator.REGISTRY.get(identifier);
										StructurePool structurePool2x = StructurePoolBasedGenerator.REGISTRY.get(structurePoolx.getTerminatorsId());
										return Math.max(structurePoolx.method_19309(this.structureManager), structurePool2x.method_19309(this.structureManager));
									}
								}).max().orElse(0);
							}

							for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
								if (JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2)) {
									BlockPos blockPos4 = structureBlockInfo2.pos;
									BlockPos blockPos5 = new BlockPos(blockPos3.getX() - blockPos4.getX(), blockPos3.getY() - blockPos4.getY(), blockPos3.getZ() - blockPos4.getZ());
									BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, blockRotation2);
									int r = blockBox3.minY;
									StructurePool.Projection projection2 = structurePoolElement2.getProjection();
									boolean bl3 = projection2 == StructurePool.Projection.field_16687;
									int s = blockPos4.getY();
									int t = l - s + ((Direction)structureBlockInfo.state.get(JigsawBlock.FACING)).getOffsetY();
									int u;
									if (bl && bl3) {
										u = k + t;
									} else {
										if (m == -1) {
											m = this.chunkGenerator.method_20402(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.field_13194);
										}

										u = m - s;
									}

									int w = u - r;
									BlockBox blockBox4 = blockBox3.translated(0, w, 0);
									BlockPos blockPos6 = blockPos5.add(0, w, 0);
									if (p > 0) {
										int x = Math.max(p + 1, blockBox4.maxY - blockBox4.minY);
										blockBox4.maxY = blockBox4.minY + x;
									}

									if (!VoxelShapes.matchesAnywhere(
										(VoxelShape)atomicReference3.get(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND
									)) {
										atomicReference3.set(VoxelShapes.combine((VoxelShape)atomicReference3.get(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
										int y = poolStructurePiece.getGroundLevelDelta();
										int z;
										if (bl3) {
											z = y - t;
										} else {
											z = structurePoolElement2.method_19308();
										}

										PoolStructurePiece poolStructurePiece2 = this.pieceFactory
											.create(this.structureManager, structurePoolElement2, blockPos6, z, blockRotation2, blockBox4);
										int ab;
										if (bl) {
											ab = k + l;
										} else if (bl3) {
											ab = u + s;
										} else {
											if (m == -1) {
												m = this.chunkGenerator.method_20402(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.field_13194);
											}

											ab = m + t / 2;
										}

										poolStructurePiece.addJunction(new JigsawJunction(blockPos3.getX(), ab - l + y, blockPos3.getZ(), t, projection2));
										poolStructurePiece2.addJunction(new JigsawJunction(blockPos2.getX(), ab - s + z, blockPos2.getZ(), -t, projection));
										this.children.add(poolStructurePiece2);
										if (j + 1 <= this.maxSize) {
											this.structurePieces.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece2, atomicReference3, n, j + 1));
										}
										continue label121;
									}
								}
							}
						}
					}
				} else {
					StructurePoolBasedGenerator.LOGGER.warn("Empty or none existent pool: {}", structureBlockInfo.tag.getString("target_pool"));
				}
			}
		}
	}
}
