package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;

public class HoldingPatternPhase extends AbstractPhase {
	private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility();
	private Path path;
	private Vec3d pathTarget;
	private boolean shouldFindNewPath;

	public HoldingPatternPhase(EnderDragonEntity enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Override
	public PhaseType<HoldingPatternPhase> getType() {
		return PhaseType.HOLDING_PATTERN;
	}

	@Override
	public void serverTick() {
		double d = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
		if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
			this.tickInRange();
		}
	}

	@Override
	public void beginPhase() {
		this.path = null;
		this.pathTarget = null;
	}

	@Nullable
	@Override
	public Vec3d getPathTarget() {
		return this.pathTarget;
	}

	private void tickInRange() {
		if (this.path != null && this.path.isFinished()) {
			BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPortalFeature.ORIGIN));
			int i = this.dragon.getFight() == null ? 0 : this.dragon.getFight().getAliveEndCrystals();
			if (this.dragon.getRandom().nextInt(i + 3) == 0) {
				this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
				return;
			}

			double d = 64.0;
			PlayerEntity playerEntity = this.dragon
				.world
				.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, this.dragon, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			if (playerEntity != null) {
				d = blockPos.getSquaredDistance(playerEntity.getPos(), true) / 512.0;
			}

			if (playerEntity != null && (this.dragon.getRandom().nextInt(MathHelper.abs((int)d) + 2) == 0 || this.dragon.getRandom().nextInt(i + 2) == 0)) {
				this.strafePlayer(playerEntity);
				return;
			}
		}

		if (this.path == null || this.path.isFinished()) {
			int j = this.dragon.getNearestPathNodeIndex();
			int k = j;
			if (this.dragon.getRandom().nextInt(8) == 0) {
				this.shouldFindNewPath = !this.shouldFindNewPath;
				k = j + 6;
			}

			if (this.shouldFindNewPath) {
				k++;
			} else {
				k--;
			}

			if (this.dragon.getFight() != null && this.dragon.getFight().getAliveEndCrystals() >= 0) {
				k %= 12;
				if (k < 0) {
					k += 12;
				}
			} else {
				k -= 12;
				k &= 7;
				k += 12;
			}

			this.path = this.dragon.findPath(j, k, null);
			if (this.path != null) {
				this.path.next();
			}
		}

		this.followPath();
	}

	private void strafePlayer(PlayerEntity player) {
		this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
		this.dragon.getPhaseManager().create(PhaseType.STRAFE_PLAYER).setTargetEntity(player);
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
	public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {
		if (player != null && this.dragon.canTarget(player)) {
			this.strafePlayer(player);
		}
	}
}
