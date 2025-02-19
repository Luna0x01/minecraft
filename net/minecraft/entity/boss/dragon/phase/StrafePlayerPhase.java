package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrafePlayerPhase extends AbstractPhase {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_30440 = 5;
	private int seenTargetTimes;
	private Path path;
	private Vec3d pathTarget;
	private LivingEntity target;
	private boolean shouldFindNewPath;

	public StrafePlayerPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public void serverTick() {
		if (this.target == null) {
			LOGGER.warn("Skipping player strafe phase because no player was found");
			this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
		} else {
			if (this.path != null && this.path.isFinished()) {
				double d = this.target.getX();
				double e = this.target.getZ();
				double f = d - this.dragon.getX();
				double g = e - this.dragon.getZ();
				double h = Math.sqrt(f * f + g * g);
				double i = Math.min(0.4F + h / 80.0 - 1.0, 10.0);
				this.pathTarget = new Vec3d(d, this.target.getY() + i, e);
			}

			double j = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
			if (j < 100.0 || j > 22500.0) {
				this.updatePath();
			}

			double k = 64.0;
			if (this.target.squaredDistanceTo(this.dragon) < 4096.0) {
				if (this.dragon.canSee(this.target)) {
					this.seenTargetTimes++;
					Vec3d vec3d = new Vec3d(this.target.getX() - this.dragon.getX(), 0.0, this.target.getZ() - this.dragon.getZ()).normalize();
					Vec3d vec3d2 = new Vec3d(
							(double)MathHelper.sin(this.dragon.getYaw() * (float) (Math.PI / 180.0)),
							0.0,
							(double)(-MathHelper.cos(this.dragon.getYaw() * (float) (Math.PI / 180.0)))
						)
						.normalize();
					float l = (float)vec3d2.dotProduct(vec3d);
					float m = (float)(Math.acos((double)l) * 180.0F / (float)Math.PI);
					m += 0.5F;
					if (this.seenTargetTimes >= 5 && m >= 0.0F && m < 10.0F) {
						double n = 1.0;
						Vec3d vec3d3 = this.dragon.getRotationVec(1.0F);
						double o = this.dragon.head.getX() - vec3d3.x * 1.0;
						double p = this.dragon.head.getBodyY(0.5) + 0.5;
						double q = this.dragon.head.getZ() - vec3d3.z * 1.0;
						double r = this.target.getX() - o;
						double s = this.target.getBodyY(0.5) - p;
						double t = this.target.getZ() - q;
						if (!this.dragon.isSilent()) {
							this.dragon.world.syncWorldEvent(null, 1017, this.dragon.getBlockPos(), 0);
						}

						DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.world, this.dragon, r, s, t);
						dragonFireballEntity.refreshPositionAndAngles(o, p, q, 0.0F, 0.0F);
						this.dragon.world.spawnEntity(dragonFireballEntity);
						this.seenTargetTimes = 0;
						if (this.path != null) {
							while (!this.path.isFinished()) {
								this.path.next();
							}
						}

						this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
					}
				} else if (this.seenTargetTimes > 0) {
					this.seenTargetTimes--;
				}
			} else if (this.seenTargetTimes > 0) {
				this.seenTargetTimes--;
			}
		}
	}

	private void updatePath() {
		if (this.path == null || this.path.isFinished()) {
			int i = this.dragon.getNearestPathNodeIndex();
			int j = i;
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.shouldFindNewPath = !this.shouldFindNewPath;
				j = i + 6;
			}

			if (this.shouldFindNewPath) {
				j++;
			} else {
				j--;
			}

			if (this.dragon.getFight() != null && this.dragon.getFight().getAliveEndCrystals() > 0) {
				j %= 12;
				if (j < 0) {
					j += 12;
				}
			} else {
				j -= 12;
				j &= 7;
				j += 12;
			}

			this.path = this.dragon.findPath(i, j, null);
			if (this.path != null) {
				this.path.next();
			}
		}

		this.followPath();
	}

	private void followPath() {
		if (this.path != null && !this.path.isFinished()) {
			Vec3i vec3i = this.path.getCurrentNodePos();
			this.path.next();
			double d = (double)vec3i.getX();
			double e = (double)vec3i.getZ();

			double f;
			do {
				f = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
			} while (f < (double)vec3i.getY());

			this.pathTarget = new Vec3d(d, f, e);
		}
	}

	@Override
	public void beginPhase() {
		this.seenTargetTimes = 0;
		this.pathTarget = null;
		this.path = null;
		this.target = null;
	}

	public void setTargetEntity(LivingEntity targetEntity) {
		this.target = targetEntity;
		int i = this.dragon.getNearestPathNodeIndex();
		int j = this.dragon.getNearestPathNodeIndex(this.target.getX(), this.target.getY(), this.target.getZ());
		int k = this.target.getBlockX();
		int l = this.target.getBlockZ();
		double d = (double)k - this.dragon.getX();
		double e = (double)l - this.dragon.getZ();
		double f = Math.sqrt(d * d + e * e);
		double g = Math.min(0.4F + f / 80.0 - 1.0, 10.0);
		int m = MathHelper.floor(this.target.getY() + g);
		PathNode pathNode = new PathNode(k, m, l);
		this.path = this.dragon.findPath(i, j, pathNode);
		if (this.path != null) {
			this.path.next();
			this.followPath();
		}
	}

	@Nullable
	@Override
	public Vec3d getPathTarget() {
		return this.pathTarget;
	}

	@Override
	public PhaseType<StrafePlayerPhase> getType() {
		return PhaseType.STRAFE_PLAYER;
	}
}
