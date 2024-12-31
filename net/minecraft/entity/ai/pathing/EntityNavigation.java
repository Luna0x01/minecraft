package net.minecraft.entity.ai.pathing;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;

public abstract class EntityNavigation {
	protected MobEntity mob;
	protected World world;
	@Nullable
	protected PathMinHeap field_14599;
	protected double speed;
	private final EntityAttributeInstance followRange;
	private int tickCount;
	private int pathStartTime;
	private Vec3d pathStartPos = Vec3d.ZERO;
	private Vec3d field_14602 = Vec3d.ZERO;
	private long field_14603;
	private long field_14604;
	private double field_14605;
	private float field_11967 = 0.5F;
	private boolean field_14606;
	private long field_14607;
	protected class_2771 field_14600;
	private BlockPos field_14608;
	private final PathNodeNavigator navigator;

	public EntityNavigation(MobEntity mobEntity, World world) {
		this.mob = mobEntity;
		this.world = world;
		this.followRange = mobEntity.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE);
		this.navigator = this.createNavigator();
	}

	protected abstract PathNodeNavigator createNavigator();

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public float getFollowRange() {
		return (float)this.followRange.getValue();
	}

	public boolean method_13111() {
		return this.field_14606;
	}

	public void method_13112() {
		if (this.world.getLastUpdateTime() - this.field_14607 > 20L) {
			if (this.field_14608 != null) {
				this.field_14599 = null;
				this.field_14599 = this.method_13108(this.field_14608);
				this.field_14607 = this.world.getLastUpdateTime();
				this.field_14606 = false;
			}
		} else {
			this.field_14606 = true;
		}
	}

	@Nullable
	public final PathMinHeap method_2772(double d, double e, double f) {
		return this.method_13108(new BlockPos(d, e, f));
	}

	@Nullable
	public PathMinHeap method_13108(BlockPos blockPos) {
		if (!this.isAtValidPosition()) {
			return null;
		} else if (this.field_14599 != null && !this.field_14599.method_11930() && blockPos.equals(this.field_14608)) {
			return this.field_14599;
		} else {
			this.field_14608 = blockPos;
			float f = this.getFollowRange();
			this.world.profiler.push("pathfind");
			BlockPos blockPos2 = new BlockPos(this.mob);
			int i = (int)(f + 8.0F);
			ChunkCache chunkCache = new ChunkCache(this.world, blockPos2.add(-i, -i, -i), blockPos2.add(i, i, i), 0);
			PathMinHeap pathMinHeap = this.navigator.method_11940(chunkCache, this.mob, this.field_14608, f);
			this.world.profiler.pop();
			return pathMinHeap;
		}
	}

	@Nullable
	public PathMinHeap method_13109(Entity entity) {
		if (!this.isAtValidPosition()) {
			return null;
		} else {
			BlockPos blockPos = new BlockPos(entity);
			if (this.field_14599 != null && !this.field_14599.method_11930() && blockPos.equals(this.field_14608)) {
				return this.field_14599;
			} else {
				this.field_14608 = blockPos;
				float f = this.getFollowRange();
				this.world.profiler.push("pathfind");
				BlockPos blockPos2 = new BlockPos(this.mob).up();
				int i = (int)(f + 16.0F);
				ChunkCache chunkCache = new ChunkCache(this.world, blockPos2.add(-i, -i, -i), blockPos2.add(i, i, i), 0);
				PathMinHeap pathMinHeap = this.navigator.method_11941(chunkCache, this.mob, entity, f);
				this.world.profiler.pop();
				return pathMinHeap;
			}
		}
	}

	public boolean startMovingTo(double x, double y, double z, double speed) {
		return this.method_13107(this.method_2772(x, y, z), speed);
	}

	public boolean startMovingTo(Entity entity, double speed) {
		PathMinHeap pathMinHeap = this.method_13109(entity);
		return pathMinHeap != null && this.method_13107(pathMinHeap, speed);
	}

	public boolean method_13107(@Nullable PathMinHeap pathMinHeap, double d) {
		if (pathMinHeap == null) {
			this.field_14599 = null;
			return false;
		} else {
			if (!pathMinHeap.method_11927(this.field_14599)) {
				this.field_14599 = pathMinHeap;
			}

			this.adjustPath();
			if (this.field_14599.method_11936() == 0) {
				return false;
			} else {
				this.speed = d;
				Vec3d vec3d = this.getPos();
				this.pathStartTime = this.tickCount;
				this.pathStartPos = vec3d;
				return true;
			}
		}
	}

	@Nullable
	public PathMinHeap method_13113() {
		return this.field_14599;
	}

	public void tick() {
		this.tickCount++;
		if (this.field_14606) {
			this.method_13112();
		}

		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.continueFollowingPath();
			} else if (this.field_14599 != null && this.field_14599.method_11937() < this.field_14599.method_11936()) {
				Vec3d vec3d = this.getPos();
				Vec3d vec3d2 = this.field_14599.method_11929(this.mob, this.field_14599.method_11937());
				if (vec3d.y > vec3d2.y
					&& !this.mob.onGround
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.field_14599.method_11935(this.field_14599.method_11937() + 1);
				}
			}

			if (!this.isIdle()) {
				Vec3d vec3d3 = this.field_14599.method_11928(this.mob);
				if (vec3d3 != null) {
					BlockPos blockPos = new BlockPos(vec3d3).down();
					Box box = this.world.getBlockState(blockPos).getCollisionBox((BlockView)this.world, blockPos);
					vec3d3 = vec3d3.subtract(0.0, 1.0 - box.maxY, 0.0);
					this.mob.getMotionHelper().moveTo(vec3d3.x, vec3d3.y, vec3d3.z, this.speed);
				}
			}
		}
	}

	protected void continueFollowingPath() {
		Vec3d vec3d = this.getPos();
		int i = this.field_14599.method_11936();

		for (int j = this.field_14599.method_11937(); j < this.field_14599.method_11936(); j++) {
			if ((double)this.field_14599.method_11925(j).posY != Math.floor(vec3d.y)) {
				i = j;
				break;
			}
		}

		this.field_11967 = this.mob.width > 0.75F ? this.mob.width / 2.0F : 0.75F - this.mob.width / 2.0F;
		Vec3d vec3d2 = this.field_14599.method_11938();
		if (MathHelper.abs((float)(this.mob.x - (vec3d2.x + 0.5))) < this.field_11967
			&& MathHelper.abs((float)(this.mob.z - (vec3d2.z + 0.5))) < this.field_11967
			&& Math.abs(this.mob.y - vec3d2.y) < 1.0) {
			this.field_14599.method_11935(this.field_14599.method_11937() + 1);
		}

		int k = MathHelper.ceil(this.mob.width);
		int l = MathHelper.ceil(this.mob.height);
		int m = k;

		for (int n = i - 1; n >= this.field_14599.method_11937(); n--) {
			if (this.canPathDirectlyThrough(vec3d, this.field_14599.method_11929(this.mob, n), k, l, m)) {
				this.field_14599.method_11935(n);
				break;
			}
		}

		this.checkTimeouts(vec3d);
	}

	protected void checkTimeouts(Vec3d currentPos) {
		if (this.tickCount - this.pathStartTime > 100) {
			if (currentPos.squaredDistanceTo(this.pathStartPos) < 2.25) {
				this.stop();
			}

			this.pathStartTime = this.tickCount;
			this.pathStartPos = currentPos;
		}

		if (this.field_14599 != null && !this.field_14599.method_11930()) {
			Vec3d vec3d = this.field_14599.method_11938();
			if (vec3d.equals(this.field_14602)) {
				this.field_14603 = this.field_14603 + (System.currentTimeMillis() - this.field_14604);
			} else {
				this.field_14602 = vec3d;
				double d = currentPos.distanceTo(this.field_14602);
				this.field_14605 = this.mob.getMovementSpeed() > 0.0F ? d / (double)this.mob.getMovementSpeed() * 1000.0 : 0.0;
			}

			if (this.field_14605 > 0.0 && (double)this.field_14603 > this.field_14605 * 3.0) {
				this.field_14602 = Vec3d.ZERO;
				this.field_14603 = 0L;
				this.field_14605 = 0.0;
				this.stop();
			}

			this.field_14604 = System.currentTimeMillis();
		}
	}

	public boolean isIdle() {
		return this.field_14599 == null || this.field_14599.method_11930();
	}

	public void stop() {
		this.field_14599 = null;
	}

	protected abstract Vec3d getPos();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.mob.isTouchingWater() || this.mob.isTouchingLava();
	}

	protected void adjustPath() {
	}

	protected abstract boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ);

	public boolean method_13110(BlockPos blockPos) {
		return this.world.getBlockState(blockPos.down()).isFullBlock();
	}

	public class_2771 method_13114() {
		return this.field_14600;
	}
}
