package net.minecraft.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class PortalForcer {
	private final ServerWorld world;
	private final Random random;

	public PortalForcer(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.random = new Random(serverWorld.getSeed());
	}

	public boolean usePortal(Entity entity, float f) {
		Vec3d vec3d = entity.getLastNetherPortalDirectionVector();
		Direction direction = entity.getLastNetherPortalDirection();
		BlockPattern.TeleportTarget teleportTarget = this.getPortal(
			new BlockPos(entity), entity.getVelocity(), direction, vec3d.x, vec3d.y, entity instanceof PlayerEntity
		);
		if (teleportTarget == null) {
			return false;
		} else {
			Vec3d vec3d2 = teleportTarget.pos;
			Vec3d vec3d3 = teleportTarget.velocity;
			entity.setVelocity(vec3d3);
			entity.yaw = f + (float)teleportTarget.yaw;
			entity.positAfterTeleport(vec3d2.x, vec3d2.y, vec3d2.z);
			return true;
		}
	}

	@Nullable
	public BlockPattern.TeleportTarget getPortal(BlockPos blockPos, Vec3d vec3d, Direction direction, double d, double e, boolean bl) {
		PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
		pointOfInterestStorage.method_22439(this.world, blockPos, 128);
		List<PointOfInterest> list = (List<PointOfInterest>)pointOfInterestStorage.method_22383(
				pointOfInterestType -> pointOfInterestType == PointOfInterestType.field_20632, blockPos, 128, PointOfInterestStorage.OccupationStatus.field_18489
			)
			.collect(Collectors.toList());
		Optional<PointOfInterest> optional = list.stream()
			.min(
				Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(blockPos))
					.thenComparingInt(pointOfInterest -> pointOfInterest.getPos().getY())
			);
		return (BlockPattern.TeleportTarget)optional.map(pointOfInterest -> {
			BlockPos blockPosx = pointOfInterest.getPos();
			this.world.getChunkManager().addTicket(ChunkTicketType.field_19280, new ChunkPos(blockPosx), 3, blockPosx);
			BlockPattern.Result result = NetherPortalBlock.findPortal(this.world, blockPosx);
			return result.getTeleportTarget(direction, blockPosx, e, vec3d, d);
		}).orElse(null);
	}

	public boolean createPortal(Entity entity) {
		int i = 16;
		double d = -1.0;
		int j = MathHelper.floor(entity.getX());
		int k = MathHelper.floor(entity.getY());
		int l = MathHelper.floor(entity.getZ());
		int m = j;
		int n = k;
		int o = l;
		int p = 0;
		int q = this.random.nextInt(4);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int r = j - 16; r <= j + 16; r++) {
			double e = (double)r + 0.5 - entity.getX();

			for (int s = l - 16; s <= l + 16; s++) {
				double f = (double)s + 0.5 - entity.getZ();

				label279:
				for (int t = this.world.getEffectiveHeight() - 1; t >= 0; t--) {
					if (this.world.isAir(mutable.set(r, t, s))) {
						while (t > 0 && this.world.isAir(mutable.set(r, t - 1, s))) {
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
										mutable.set(aa, ab, ac);
										if (z < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || z >= 0 && !this.world.isAir(mutable)) {
											continue label279;
										}
									}
								}
							}

							double g = (double)t + 0.5 - entity.getY();
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
				double ae = (double)ad + 0.5 - entity.getX();

				for (int af = l - 16; af <= l + 16; af++) {
					double ag = (double)af + 0.5 - entity.getZ();

					label216:
					for (int ah = this.world.getEffectiveHeight() - 1; ah >= 0; ah--) {
						if (this.world.isAir(mutable.set(ad, ah, af))) {
							while (ah > 0 && this.world.isAir(mutable.set(ad, ah - 1, af))) {
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
										mutable.set(an, ao, ap);
										if (am < 0 && !this.world.getBlockState(mutable).getMaterial().isSolid() || am >= 0 && !this.world.isAir(mutable)) {
											continue label216;
										}
									}
								}

								double aq = (double)ah + 0.5 - entity.getY();
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
						mutable.set(bb, bc, bd);
						this.world.setBlockState(mutable, bl ? Blocks.field_10540.getDefaultState() : Blocks.field_10124.getDefaultState());
					}
				}
			}
		}

		for (int be = -1; be < 3; be++) {
			for (int bf = -1; bf < 4; bf++) {
				if (be == -1 || be == 2 || bf == -1 || bf == 3) {
					mutable.set(at + be * aw, au + bf, av + be * ax);
					this.world.setBlockState(mutable, Blocks.field_10540.getDefaultState(), 3);
				}
			}
		}

		BlockState blockState = Blocks.field_10316.getDefaultState().with(NetherPortalBlock.AXIS, aw == 0 ? Direction.Axis.field_11051 : Direction.Axis.field_11048);

		for (int bg = 0; bg < 2; bg++) {
			for (int bh = 0; bh < 3; bh++) {
				mutable.set(at + bg * aw, au + bh, av + bg * ax);
				this.world.setBlockState(mutable, blockState, 18);
			}
		}

		return true;
	}
}
