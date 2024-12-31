package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPos extends Vec3i {
	public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
	private static final int SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
	private static final int SIZE_BITS_Z = SIZE_BITS_X;
	private static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
	private static final int BIT_SHIFT_Z = 0 + SIZE_BITS_Z;
	private static final int BIT_SHIFT_X = BIT_SHIFT_Z + SIZE_BITS_Y;
	private static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
	private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
	private static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;

	public BlockPos(int i, int j, int k) {
		super(i, j, k);
	}

	public BlockPos(double d, double e, double f) {
		super(d, e, f);
	}

	public BlockPos(Entity entity) {
		this(entity.x, entity.y, entity.z);
	}

	public BlockPos(Vec3d vec3d) {
		this(vec3d.x, vec3d.y, vec3d.z);
	}

	public BlockPos(Vec3i vec3i) {
		this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public BlockPos add(double x, double y, double z) {
		return x == 0.0 && y == 0.0 && z == 0.0 ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
	}

	public BlockPos add(int x, int y, int z) {
		return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	public BlockPos add(Vec3i pos) {
		return pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0
			? this
			: new BlockPos(this.getX() + pos.getX(), this.getY() + pos.getY(), this.getZ() + pos.getZ());
	}

	public BlockPos subtract(Vec3i pos) {
		return pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0
			? this
			: new BlockPos(this.getX() - pos.getX(), this.getY() - pos.getY(), this.getZ() - pos.getZ());
	}

	public BlockPos up() {
		return this.up(1);
	}

	public BlockPos up(int distance) {
		return this.offset(Direction.UP, distance);
	}

	public BlockPos down() {
		return this.down(1);
	}

	public BlockPos down(int distance) {
		return this.offset(Direction.DOWN, distance);
	}

	public BlockPos north() {
		return this.north(1);
	}

	public BlockPos north(int distance) {
		return this.offset(Direction.NORTH, distance);
	}

	public BlockPos south() {
		return this.south(1);
	}

	public BlockPos south(int distance) {
		return this.offset(Direction.SOUTH, distance);
	}

	public BlockPos west() {
		return this.west(1);
	}

	public BlockPos west(int distance) {
		return this.offset(Direction.WEST, distance);
	}

	public BlockPos east() {
		return this.east(1);
	}

	public BlockPos east(int distance) {
		return this.offset(Direction.EAST, distance);
	}

	public BlockPos offset(Direction facing) {
		return this.offset(facing, 1);
	}

	public BlockPos offset(Direction facing, int distance) {
		return distance == 0
			? this
			: new BlockPos(this.getX() + facing.getOffsetX() * distance, this.getY() + facing.getOffsetY() * distance, this.getZ() + facing.getOffsetZ() * distance);
	}

	public BlockPos crossProduct(Vec3i vec3i) {
		return new BlockPos(
			this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(),
			this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(),
			this.getX() * vec3i.getY() - this.getY() * vec3i.getX()
		);
	}

	public long asLong() {
		return ((long)this.getX() & BITS_X) << BIT_SHIFT_X | ((long)this.getY() & BITS_Y) << BIT_SHIFT_Z | ((long)this.getZ() & BITS_Z) << 0;
	}

	public static BlockPos fromLong(long value) {
		int i = (int)(value << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
		int j = (int)(value << 64 - BIT_SHIFT_Z - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
		int k = (int)(value << 64 - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
		return new BlockPos(i, j, k);
	}

	public static Iterable<BlockPos> iterate(BlockPos pos1, BlockPos pos2) {
		final BlockPos blockPos = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
		final BlockPos blockPos2 = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
		return new Iterable<BlockPos>() {
			public Iterator<BlockPos> iterator() {
				return new AbstractIterator<BlockPos>() {
					private BlockPos field_11438 = null;

					protected BlockPos computeNext() {
						if (this.field_11438 == null) {
							this.field_11438 = blockPos;
							return this.field_11438;
						} else if (this.field_11438.equals(blockPos2)) {
							return (BlockPos)this.endOfData();
						} else {
							int i = this.field_11438.getX();
							int j = this.field_11438.getY();
							int k = this.field_11438.getZ();
							if (i < blockPos2.getX()) {
								i++;
							} else if (j < blockPos2.getY()) {
								i = blockPos.getX();
								j++;
							} else if (k < blockPos2.getZ()) {
								i = blockPos.getX();
								j = blockPos.getY();
								k++;
							}

							this.field_11438 = new BlockPos(i, j, k);
							return this.field_11438;
						}
					}
				};
			}
		};
	}

	public static Iterable<BlockPos.Mutable> mutableIterate(BlockPos pos1, BlockPos pos2) {
		final BlockPos blockPos = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
		final BlockPos blockPos2 = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
		return new Iterable<BlockPos.Mutable>() {
			public Iterator<BlockPos.Mutable> iterator() {
				return new AbstractIterator<BlockPos.Mutable>() {
					private BlockPos.Mutable mutable = null;

					protected BlockPos.Mutable computeNext() {
						if (this.mutable == null) {
							this.mutable = new BlockPos.Mutable(blockPos.getX(), blockPos.getY(), blockPos.getZ());
							return this.mutable;
						} else if (this.mutable.equals(blockPos2)) {
							return (BlockPos.Mutable)this.endOfData();
						} else {
							int i = this.mutable.getX();
							int j = this.mutable.getY();
							int k = this.mutable.getZ();
							if (i < blockPos2.getX()) {
								i++;
							} else if (j < blockPos2.getY()) {
								i = blockPos.getX();
								j++;
							} else if (k < blockPos2.getZ()) {
								i = blockPos.getX();
								j = blockPos.getY();
								k++;
							}

							this.mutable.posX = i;
							this.mutable.posY = j;
							this.mutable.posZ = k;
							return this.mutable;
						}
					}
				};
			}
		};
	}

	public static final class Mutable extends BlockPos {
		private int posX;
		private int posY;
		private int posZ;

		public Mutable() {
			this(0, 0, 0);
		}

		public Mutable(int i, int j, int k) {
			super(0, 0, 0);
			this.posX = i;
			this.posY = j;
			this.posZ = k;
		}

		@Override
		public int getX() {
			return this.posX;
		}

		@Override
		public int getY() {
			return this.posY;
		}

		@Override
		public int getZ() {
			return this.posZ;
		}

		public BlockPos.Mutable setPosition(int x, int y, int z) {
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			return this;
		}
	}
}
