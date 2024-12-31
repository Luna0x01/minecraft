package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
	private static final Logger LOGGER = LogManager.getLogger();
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
		return this.add(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockPos subtract(Vec3i pos) {
		return this.add(-pos.getX(), -pos.getY(), -pos.getZ());
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

	public BlockPos rotate(BlockRotation rotation) {
		switch (rotation) {
			case NONE:
			default:
				return this;
			case CLOCKWISE_90:
				return new BlockPos(-this.getZ(), this.getY(), this.getX());
			case CLOCKWISE_180:
				return new BlockPos(-this.getX(), this.getY(), -this.getZ());
			case COUNTERCLOCKWISE_90:
				return new BlockPos(this.getZ(), this.getY(), -this.getX());
		}
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
		return iterate(
			Math.min(pos1.getX(), pos2.getX()),
			Math.min(pos1.getY(), pos2.getY()),
			Math.min(pos1.getZ(), pos2.getZ()),
			Math.max(pos1.getX(), pos2.getX()),
			Math.max(pos1.getY(), pos2.getY()),
			Math.max(pos1.getZ(), pos2.getZ())
		);
	}

	public static Iterable<BlockPos> iterate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return new Iterable<BlockPos>() {
			public Iterator<BlockPos> iterator() {
				return new AbstractIterator<BlockPos>() {
					private boolean field_15332 = true;
					private int field_15333;
					private int field_15334;
					private int field_15335;

					protected BlockPos computeNext() {
						if (this.field_15332) {
							this.field_15332 = false;
							this.field_15333 = minX;
							this.field_15334 = minY;
							this.field_15335 = minZ;
							return new BlockPos(minX, minY, minZ);
						} else if (this.field_15333 == maxX && this.field_15334 == maxY && this.field_15335 == maxZ) {
							return (BlockPos)this.endOfData();
						} else {
							if (this.field_15333 < maxX) {
								this.field_15333++;
							} else if (this.field_15334 < maxY) {
								this.field_15333 = minX;
								this.field_15334++;
							} else if (this.field_15335 < maxZ) {
								this.field_15333 = minX;
								this.field_15334 = minY;
								this.field_15335++;
							}

							return new BlockPos(this.field_15333, this.field_15334, this.field_15335);
						}
					}
				};
			}
		};
	}

	public BlockPos toImmutable() {
		return this;
	}

	public static Iterable<BlockPos.Mutable> mutableIterate(BlockPos pos1, BlockPos pos2) {
		return mutableIterate(
			Math.min(pos1.getX(), pos2.getX()),
			Math.min(pos1.getY(), pos2.getY()),
			Math.min(pos1.getZ(), pos2.getZ()),
			Math.max(pos1.getX(), pos2.getX()),
			Math.max(pos1.getY(), pos2.getY()),
			Math.max(pos1.getZ(), pos2.getZ())
		);
	}

	public static Iterable<BlockPos.Mutable> mutableIterate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return new Iterable<BlockPos.Mutable>() {
			public Iterator<BlockPos.Mutable> iterator() {
				return new AbstractIterator<BlockPos.Mutable>() {
					private BlockPos.Mutable field_14994;

					protected BlockPos.Mutable computeNext() {
						if (this.field_14994 == null) {
							this.field_14994 = new BlockPos.Mutable(minX, minY, minZ);
							return this.field_14994;
						} else if (this.field_14994.posX == maxX && this.field_14994.posY == maxY && this.field_14994.posZ == maxZ) {
							return (BlockPos.Mutable)this.endOfData();
						} else {
							if (this.field_14994.posX < maxX) {
								this.field_14994.posX++;
							} else if (this.field_14994.posY < maxY) {
								this.field_14994.posX = minX;
								this.field_14994.posY++;
							} else if (this.field_14994.posZ < maxZ) {
								this.field_14994.posX = minX;
								this.field_14994.posY = minY;
								this.field_14994.posZ++;
							}

							return this.field_14994;
						}
					}
				};
			}
		};
	}

	public static class Mutable extends BlockPos {
		protected int posX;
		protected int posY;
		protected int posZ;

		public Mutable() {
			this(0, 0, 0);
		}

		public Mutable(BlockPos blockPos) {
			this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}

		public Mutable(int i, int j, int k) {
			super(0, 0, 0);
			this.posX = i;
			this.posY = j;
			this.posZ = k;
		}

		@Override
		public BlockPos add(double x, double y, double z) {
			return super.add(x, y, z).toImmutable();
		}

		@Override
		public BlockPos add(int x, int y, int z) {
			return super.add(x, y, z).toImmutable();
		}

		@Override
		public BlockPos offset(Direction facing, int distance) {
			return super.offset(facing, distance).toImmutable();
		}

		@Override
		public BlockPos rotate(BlockRotation rotation) {
			return super.rotate(rotation).toImmutable();
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

		public BlockPos.Mutable set(Entity entity) {
			return this.set(entity.x, entity.y, entity.z);
		}

		public BlockPos.Mutable set(double x, double y, double z) {
			return this.setPosition(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
		}

		public BlockPos.Mutable set(Vec3i pos) {
			return this.setPosition(pos.getX(), pos.getY(), pos.getZ());
		}

		public BlockPos.Mutable move(Direction direction) {
			return this.move(direction, 1);
		}

		public BlockPos.Mutable move(Direction direction, int distance) {
			return this.setPosition(
				this.posX + direction.getOffsetX() * distance, this.posY + direction.getOffsetY() * distance, this.posZ + direction.getOffsetZ() * distance
			);
		}

		public void setY(int y) {
			this.posY = y;
		}

		@Override
		public BlockPos toImmutable() {
			return new BlockPos(this);
		}
	}

	public static final class Pooled extends BlockPos.Mutable {
		private boolean field_13716;
		private static final List<BlockPos.Pooled> field_13717 = Lists.newArrayList();

		private Pooled(int i, int j, int k) {
			super(i, j, k);
		}

		public static BlockPos.Pooled get() {
			return method_12571(0, 0, 0);
		}

		public static BlockPos.Pooled method_12567(double d, double e, double f) {
			return method_12571(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
		}

		public static BlockPos.Pooled method_12573(Vec3i vec3i) {
			return method_12571(vec3i.getX(), vec3i.getY(), vec3i.getZ());
		}

		public static BlockPos.Pooled method_12571(int i, int j, int k) {
			synchronized (field_13717) {
				if (!field_13717.isEmpty()) {
					BlockPos.Pooled pooled = (BlockPos.Pooled)field_13717.remove(field_13717.size() - 1);
					if (pooled != null && pooled.field_13716) {
						pooled.field_13716 = false;
						pooled.setPosition(i, j, k);
						return pooled;
					}
				}
			}

			return new BlockPos.Pooled(i, j, k);
		}

		public void method_12576() {
			synchronized (field_13717) {
				if (field_13717.size() < 100) {
					field_13717.add(this);
				}

				this.field_13716 = true;
			}
		}

		public BlockPos.Pooled setPosition(int i, int j, int k) {
			if (this.field_13716) {
				BlockPos.LOGGER.error("PooledMutableBlockPosition modified after it was released.", new Throwable());
				this.field_13716 = false;
			}

			return (BlockPos.Pooled)super.setPosition(i, j, k);
		}

		public BlockPos.Pooled set(Entity entity) {
			return (BlockPos.Pooled)super.set(entity);
		}

		public BlockPos.Pooled set(double d, double e, double f) {
			return (BlockPos.Pooled)super.set(d, e, f);
		}

		public BlockPos.Pooled set(Vec3i vec3i) {
			return (BlockPos.Pooled)super.set(vec3i);
		}

		public BlockPos.Pooled move(Direction direction) {
			return (BlockPos.Pooled)super.move(direction);
		}

		public BlockPos.Pooled move(Direction direction, int i) {
			return (BlockPos.Pooled)super.move(direction, i);
		}
	}
}
