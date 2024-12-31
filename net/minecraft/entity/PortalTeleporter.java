package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.LongObjectStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class PortalTeleporter {
	private final ServerWorld world;
	private final Random random;
	private final LongObjectStorage<PortalTeleporter.Position> field_5472 = new LongObjectStorage<>();
	private final List<Long> field_5473 = Lists.newArrayList();

	public PortalTeleporter(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.random = new Random(serverWorld.getSeed());
	}

	public void method_8583(Entity entity, float f) {
		if (this.world.dimension.getType() != 1) {
			if (!this.method_8584(entity, f)) {
				this.method_3803(entity);
				this.method_8584(entity, f);
			}
		} else {
			int i = MathHelper.floor(entity.x);
			int j = MathHelper.floor(entity.y) - 1;
			int k = MathHelper.floor(entity.z);
			int l = 1;
			int m = 0;

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					for (int p = -1; p < 3; p++) {
						int q = i + o * l + n * m;
						int r = j + p;
						int s = k + o * m - n * l;
						boolean bl = p < 0;
						this.world.setBlockState(new BlockPos(q, r, s), bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}

			entity.refreshPositionAndAngles((double)i, (double)j, (double)k, entity.yaw, 0.0F);
			entity.velocityX = entity.velocityY = entity.velocityZ = 0.0;
		}
	}

	public boolean method_8584(Entity entity, float f) {
		int i = 128;
		double d = -1.0;
		int j = MathHelper.floor(entity.x);
		int k = MathHelper.floor(entity.z);
		boolean bl = true;
		BlockPos blockPos = BlockPos.ORIGIN;
		long l = ChunkPos.getIdFromCoords(j, k);
		if (this.field_5472.contains(l)) {
			PortalTeleporter.Position position = this.field_5472.get(l);
			d = 0.0;
			blockPos = position;
			position.pos = this.world.getLastUpdateTime();
			bl = false;
		} else {
			BlockPos blockPos2 = new BlockPos(entity);

			for (int m = -128; m <= 128; m++) {
				for (int n = -128; n <= 128; n++) {
					BlockPos blockPos3 = blockPos2.add(m, this.world.getEffectiveHeight() - 1 - blockPos2.getY(), n);

					while (blockPos3.getY() >= 0) {
						BlockPos blockPos4 = blockPos3.down();
						if (this.world.getBlockState(blockPos3).getBlock() == Blocks.NETHER_PORTAL) {
							while (this.world.getBlockState(blockPos4 = blockPos3.down()).getBlock() == Blocks.NETHER_PORTAL) {
								blockPos3 = blockPos4;
							}

							double e = blockPos3.getSquaredDistance(blockPos2);
							if (d < 0.0 || e < d) {
								d = e;
								blockPos = blockPos3;
							}
						}

						blockPos3 = blockPos4;
					}
				}
			}
		}

		if (d >= 0.0) {
			if (bl) {
				this.field_5472.set(l, new PortalTeleporter.Position(blockPos, this.world.getLastUpdateTime()));
				this.field_5473.add(l);
			}

			double g = (double)blockPos.getX() + 0.5;
			double h = (double)blockPos.getY() + 0.5;
			double o = (double)blockPos.getZ() + 0.5;
			BlockPattern.Result result = Blocks.NETHER_PORTAL.findPortal(this.world, blockPos);
			boolean bl2 = result.getForwards().rotateYClockwise().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
			double p = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
			h = (double)(result.getFrontTopLeft().getY() + 1) - entity.getLastNetherPortalDirectionVector().y * (double)result.getHeight();
			if (bl2) {
				p++;
			}

			if (result.getForwards().getAxis() == Direction.Axis.X) {
				o = p
					+ (1.0 - entity.getLastNetherPortalDirectionVector().x)
						* (double)result.getWidth()
						* (double)result.getForwards().rotateYClockwise().getAxisDirection().offset();
			} else {
				g = p
					+ (1.0 - entity.getLastNetherPortalDirectionVector().x)
						* (double)result.getWidth()
						* (double)result.getForwards().rotateYClockwise().getAxisDirection().offset();
			}

			float q = 0.0F;
			float r = 0.0F;
			float s = 0.0F;
			float t = 0.0F;
			if (result.getForwards().getOpposite() == entity.getLastNetherPortalDirection()) {
				q = 1.0F;
				r = 1.0F;
			} else if (result.getForwards().getOpposite() == entity.getLastNetherPortalDirection().getOpposite()) {
				q = -1.0F;
				r = -1.0F;
			} else if (result.getForwards().getOpposite() == entity.getLastNetherPortalDirection().rotateYClockwise()) {
				s = 1.0F;
				t = -1.0F;
			} else {
				s = -1.0F;
				t = 1.0F;
			}

			double u = entity.velocityX;
			double v = entity.velocityZ;
			entity.velocityX = u * (double)q + v * (double)t;
			entity.velocityZ = u * (double)s + v * (double)r;
			entity.yaw = f - (float)(entity.getLastNetherPortalDirection().getOpposite().getHorizontal() * 90) + (float)(result.getForwards().getHorizontal() * 90);
			entity.refreshPositionAndAngles(g, h, o, entity.yaw, entity.pitch);
			return true;
		} else {
			return false;
		}
	}

	public boolean method_3803(Entity entity) {
		int i = 16;
		double d = -1.0;
		int j = MathHelper.floor(entity.x);
		int k = MathHelper.floor(entity.y);
		int l = MathHelper.floor(entity.z);
		int m = j;
		int n = k;
		int o = l;
		int p = 0;
		int q = this.random.nextInt(4);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int r = j - i; r <= j + i; r++) {
			double e = (double)r + 0.5 - entity.x;

			for (int s = l - i; s <= l + i; s++) {
				double f = (double)s + 0.5 - entity.z;

				label296:
				for (int t = this.world.getEffectiveHeight() - 1; t >= 0; t--) {
					if (this.world.isAir(mutable.setPosition(r, t, s))) {
						while (t > 0 && this.world.isAir(mutable.setPosition(r, t - 1, s))) {
							t--;
						}

						for (int u = q; u < q + 4; u++) {
							int v = u % 2;
							int w = 1 - v;
							if (u % 4 >= 2) {
								v = -v;
								w = -w;
							}

							for (int x = 0; x < 3; x++) {
								for (int y = 0; y < 4; y++) {
									for (int z = -1; z < 4; z++) {
										int aa = r + (y - 1) * v + x * w;
										int ab = t + z;
										int ac = s + (y - 1) * w - x * v;
										mutable.setPosition(aa, ab, ac);
										if (z < 0 && !this.world.getBlockState(mutable).getBlock().getMaterial().isSolid() || z >= 0 && !this.world.isAir(mutable)) {
											continue label296;
										}
									}
								}
							}

							double g = (double)t + 0.5 - entity.y;
							double h = e * e + g * g + f * f;
							if (d < 0.0 || h < d) {
								d = h;
								m = r;
								n = t;
								o = s;
								p = u % 4;
							}
						}
					}
				}
			}
		}

		if (d < 0.0) {
			for (int ad = j - i; ad <= j + i; ad++) {
				double ae = (double)ad + 0.5 - entity.x;

				for (int af = l - i; af <= l + i; af++) {
					double ag = (double)af + 0.5 - entity.z;

					label233:
					for (int ah = this.world.getEffectiveHeight() - 1; ah >= 0; ah--) {
						if (this.world.isAir(mutable.setPosition(ad, ah, af))) {
							while (ah > 0 && this.world.isAir(mutable.setPosition(ad, ah - 1, af))) {
								ah--;
							}

							for (int ai = q; ai < q + 2; ai++) {
								int aj = ai % 2;
								int ak = 1 - aj;

								for (int al = 0; al < 4; al++) {
									for (int am = -1; am < 4; am++) {
										int an = ad + (al - 1) * aj;
										int ao = ah + am;
										int ap = af + (al - 1) * ak;
										mutable.setPosition(an, ao, ap);
										if (am < 0 && !this.world.getBlockState(mutable).getBlock().getMaterial().isSolid() || am >= 0 && !this.world.isAir(mutable)) {
											continue label233;
										}
									}
								}

								double aq = (double)ah + 0.5 - entity.y;
								double ar = ae * ae + aq * aq + ag * ag;
								if (d < 0.0 || ar < d) {
									d = ar;
									m = ad;
									n = ah;
									o = af;
									p = ai % 2;
								}
							}
						}
					}
				}
			}
		}

		int at = m;
		int au = n;
		int av = o;
		int aw = p % 2;
		int ax = 1 - aw;
		if (p % 4 >= 2) {
			aw = -aw;
			ax = -ax;
		}

		if (d < 0.0) {
			n = MathHelper.clamp(n, 70, this.world.getEffectiveHeight() - 10);
			au = n;

			for (int ay = -1; ay <= 1; ay++) {
				for (int az = 1; az < 3; az++) {
					for (int ba = -1; ba < 3; ba++) {
						int bb = at + (az - 1) * aw + ay * ax;
						int bc = au + ba;
						int bd = av + (az - 1) * ax - ay * aw;
						boolean bl = ba < 0;
						this.world.setBlockState(new BlockPos(bb, bc, bd), bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}
		}

		BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, aw != 0 ? Direction.Axis.X : Direction.Axis.Z);

		for (int be = 0; be < 4; be++) {
			for (int bf = 0; bf < 4; bf++) {
				for (int bg = -1; bg < 4; bg++) {
					int bh = at + (bf - 1) * aw;
					int bi = au + bg;
					int bj = av + (bf - 1) * ax;
					boolean bl2 = bf == 0 || bf == 3 || bg == -1 || bg == 3;
					this.world.setBlockState(new BlockPos(bh, bi, bj), bl2 ? Blocks.OBSIDIAN.getDefaultState() : blockState, 2);
				}
			}

			for (int bk = 0; bk < 4; bk++) {
				for (int bm = -1; bm < 4; bm++) {
					int bn = at + (bk - 1) * aw;
					int bo = au + bm;
					int bp = av + (bk - 1) * ax;
					BlockPos blockPos = new BlockPos(bn, bo, bp);
					this.world.updateNeighborsAlways(blockPos, this.world.getBlockState(blockPos).getBlock());
				}
			}
		}

		return true;
	}

	public void method_4698(long l) {
		if (l % 100L == 0L) {
			Iterator<Long> iterator = this.field_5473.iterator();
			long m = l - 300L;

			while (iterator.hasNext()) {
				Long long_ = (Long)iterator.next();
				PortalTeleporter.Position position = this.field_5472.get(long_);
				if (position == null || position.pos < m) {
					iterator.remove();
					this.field_5472.remove(long_);
				}
			}
		}
	}

	public class Position extends BlockPos {
		public long pos;

		public Position(BlockPos blockPos, long l) {
			super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.pos = l;
		}
	}
}
