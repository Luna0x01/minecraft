package net.minecraft.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

public class PortalTeleporter {
	private static final NetherPortalBlock field_17510 = (NetherPortalBlock)Blocks.NETHER_PORTAL;
	private final ServerWorld world;
	private final Random random;
	private final Long2ObjectMap<PortalTeleporter.Position> cache = new Long2ObjectOpenHashMap(4096);

	public PortalTeleporter(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.random = new Random(serverWorld.method_3581());
	}

	public void method_8583(Entity entity, float f) {
		if (this.world.dimension.method_11789() != DimensionType.THE_END) {
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
						int q = i + o * 1 + n * 0;
						int r = j + p;
						int s = k + o * 0 - n * 1;
						boolean bl = p < 0;
						this.world.setBlockState(new BlockPos(q, r, s), bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}

			entity.refreshPositionAndAngles((double)i, (double)j, (double)k, entity.yaw, 0.0F);
			entity.velocityX = 0.0;
			entity.velocityY = 0.0;
			entity.velocityZ = 0.0;
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
		if (this.cache.containsKey(l)) {
			PortalTeleporter.Position position = (PortalTeleporter.Position)this.cache.get(l);
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
						if (this.world.getBlockState(blockPos3).getBlock() == field_17510) {
							for (blockPos4 = blockPos3.down(); this.world.getBlockState(blockPos4).getBlock() == field_17510; blockPos4 = blockPos4.down()) {
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
				this.cache.put(l, new PortalTeleporter.Position(blockPos, this.world.getLastUpdateTime()));
			}

			double g = (double)blockPos.getX() + 0.5;
			double h = (double)blockPos.getZ() + 0.5;
			BlockPattern.Result result = field_17510.method_8848(this.world, blockPos);
			boolean bl2 = result.getForwards().rotateYClockwise().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
			double o = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
			double p = (double)(result.getFrontTopLeft().getY() + 1) - entity.getLastNetherPortalDirectionVector().y * (double)result.getHeight();
			if (bl2) {
				o++;
			}

			if (result.getForwards().getAxis() == Direction.Axis.X) {
				h = o
					+ (1.0 - entity.getLastNetherPortalDirectionVector().x)
						* (double)result.getWidth()
						* (double)result.getForwards().rotateYClockwise().getAxisDirection().offset();
			} else {
				g = o
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
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).networkHandler.requestTeleport(g, p, h, entity.yaw, entity.pitch);
				((ServerPlayerEntity)entity).networkHandler.method_12823();
			} else {
				entity.refreshPositionAndAngles(g, p, h, entity.yaw, entity.pitch);
			}

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

		for (int r = j - 16; r <= j + 16; r++) {
			double e = (double)r + 0.5 - entity.x;

			for (int s = l - 16; s <= l + 16; s++) {
				double f = (double)s + 0.5 - entity.z;

				label279:
				for (int t = this.world.getEffectiveHeight() - 1; t >= 0; t--) {
					if (this.world.method_8579(mutable.setPosition(r, t, s))) {
						while (t > 0 && this.world.method_8579(mutable.setPosition(r, t - 1, s))) {
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
										if (z < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !this.world.method_8579(mutable)) {
											continue label279;
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
			for (int ad = j - 16; ad <= j + 16; ad++) {
				double ae = (double)ad + 0.5 - entity.x;

				for (int af = l - 16; af <= l + 16; af++) {
					double ag = (double)af + 0.5 - entity.z;

					label216:
					for (int ah = this.world.getEffectiveHeight() - 1; ah >= 0; ah--) {
						if (this.world.method_8579(mutable.setPosition(ad, ah, af))) {
							while (ah > 0 && this.world.method_8579(mutable.setPosition(ad, ah - 1, af))) {
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
										if (am < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || am >= 0 && !this.world.method_8579(mutable)) {
											continue label216;
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
						mutable.setPosition(bb, bc, bd);
						this.world.setBlockState(mutable, bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}
		}

		for (int be = -1; be < 3; be++) {
			for (int bf = -1; bf < 4; bf++) {
				if (be == -1 || be == 2 || bf == -1 || bf == 3) {
					mutable.setPosition(at + be * aw, au + bf, av + be * ax);
					this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
				}
			}
		}

		BlockState blockState = field_17510.getDefaultState().withProperty(NetherPortalBlock.field_18409, aw == 0 ? Direction.Axis.Z : Direction.Axis.X);

		for (int bg = 0; bg < 2; bg++) {
			for (int bh = 0; bh < 3; bh++) {
				mutable.setPosition(at + bg * aw, au + bh, av + bg * ax);
				this.world.setBlockState(mutable, blockState, 18);
			}
		}

		return true;
	}

	public void method_4698(long l) {
		if (l % 100L == 0L) {
			long m = l - 300L;
			ObjectIterator<PortalTeleporter.Position> objectIterator = this.cache.values().iterator();

			while (objectIterator.hasNext()) {
				PortalTeleporter.Position position = (PortalTeleporter.Position)objectIterator.next();
				if (position == null || position.pos < m) {
					objectIterator.remove();
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
