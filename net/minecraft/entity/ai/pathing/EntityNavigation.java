package net.minecraft.entity.ai.pathing;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;

public abstract class EntityNavigation {
	protected MobEntity mob;
	protected World world;
	protected Path currentPath;
	protected double speed;
	private final EntityAttributeInstance followRange;
	private int tickCount;
	private int pathStartTime;
	private Vec3d pathStartPos = new Vec3d(0.0, 0.0, 0.0);
	private float field_11967 = 1.0F;
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

	public final Path findPathTo(double x, double y, double z) {
		return this.findPathTo(new BlockPos(MathHelper.floor(x), (int)y, MathHelper.floor(z)));
	}

	public Path findPathTo(BlockPos pos) {
		if (!this.isAtValidPosition()) {
			return null;
		} else {
			float f = this.getFollowRange();
			this.world.profiler.push("pathfind");
			BlockPos blockPos = new BlockPos(this.mob);
			int i = (int)(f + 8.0F);
			ChunkCache chunkCache = new ChunkCache(this.world, blockPos.add(-i, -i, -i), blockPos.add(i, i, i), 0);
			Path path = this.navigator.findPathToAny(chunkCache, this.mob, pos, f);
			this.world.profiler.pop();
			return path;
		}
	}

	public boolean startMovingTo(double x, double y, double z, double speed) {
		Path path = this.findPathTo((double)MathHelper.floor(x), (double)((int)y), (double)MathHelper.floor(z));
		return this.startMovingAlong(path, speed);
	}

	public void method_11038(float f) {
		this.field_11967 = f;
	}

	public Path findPathTo(Entity entity) {
		if (!this.isAtValidPosition()) {
			return null;
		} else {
			float f = this.getFollowRange();
			this.world.profiler.push("pathfind");
			BlockPos blockPos = new BlockPos(this.mob).up();
			int i = (int)(f + 16.0F);
			ChunkCache chunkCache = new ChunkCache(this.world, blockPos.add(-i, -i, -i), blockPos.add(i, i, i), 0);
			Path path = this.navigator.findPathToAny(chunkCache, this.mob, entity, f);
			this.world.profiler.pop();
			return path;
		}
	}

	public boolean startMovingTo(Entity entity, double speed) {
		Path path = this.findPathTo(entity);
		return path != null ? this.startMovingAlong(path, speed) : false;
	}

	public boolean startMovingAlong(Path path, double speed) {
		if (path == null) {
			this.currentPath = null;
			return false;
		} else {
			if (!path.equalsPath(this.currentPath)) {
				this.currentPath = path;
			}

			this.adjustPath();
			if (this.currentPath.getNodeCount() == 0) {
				return false;
			} else {
				this.speed = speed;
				Vec3d vec3d = this.getPos();
				this.pathStartTime = this.tickCount;
				this.pathStartPos = vec3d;
				return true;
			}
		}
	}

	public Path getCurrentPath() {
		return this.currentPath;
	}

	public void tick() {
		this.tickCount++;
		if (!this.isIdle()) {
			if (this.isAtValidPosition()) {
				this.continueFollowingPath();
			} else if (this.currentPath != null && this.currentPath.getCurrentNode() < this.currentPath.getNodeCount()) {
				Vec3d vec3d = this.getPos();
				Vec3d vec3d2 = this.currentPath.getNodePosition(this.mob, this.currentPath.getCurrentNode());
				if (vec3d.y > vec3d2.y
					&& !this.mob.onGround
					&& MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
					&& MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
					this.currentPath.setCurrentNode(this.currentPath.getCurrentNode() + 1);
				}
			}

			if (!this.isIdle()) {
				Vec3d vec3d3 = this.currentPath.getCurrentPosition(this.mob);
				if (vec3d3 != null) {
					Box box = new Box(vec3d3.x, vec3d3.y, vec3d3.z, vec3d3.x, vec3d3.y, vec3d3.z).expand(0.5, 0.5, 0.5);
					List<Box> list = this.world.doesBoxCollide(this.mob, box.stretch(0.0, -1.0, 0.0));
					double d = -1.0;
					box = box.offset(0.0, 1.0, 0.0);

					for (Box box2 : list) {
						d = box2.method_589(box, d);
					}

					this.mob.getMotionHelper().moveTo(vec3d3.x, vec3d3.y + d, vec3d3.z, this.speed);
				}
			}
		}
	}

	protected void continueFollowingPath() {
		Vec3d vec3d = this.getPos();
		int i = this.currentPath.getNodeCount();

		for (int j = this.currentPath.getCurrentNode(); j < this.currentPath.getNodeCount(); j++) {
			if (this.currentPath.getNode(j).posY != (int)vec3d.y) {
				i = j;
				break;
			}
		}

		float f = this.mob.width * this.mob.width * this.field_11967;

		for (int k = this.currentPath.getCurrentNode(); k < i; k++) {
			Vec3d vec3d2 = this.currentPath.getNodePosition(this.mob, k);
			if (vec3d.squaredDistanceTo(vec3d2) < (double)f) {
				this.currentPath.setCurrentNode(k + 1);
			}
		}

		int l = MathHelper.ceil(this.mob.width);
		int m = (int)this.mob.height + 1;
		int n = l;

		for (int o = i - 1; o >= this.currentPath.getCurrentNode(); o--) {
			if (this.canPathDirectlyThrough(vec3d, this.currentPath.getNodePosition(this.mob, o), l, m, n)) {
				this.currentPath.setCurrentNode(o);
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
	}

	public boolean isIdle() {
		return this.currentPath == null || this.currentPath.isFinished();
	}

	public void stop() {
		this.currentPath = null;
	}

	protected abstract Vec3d getPos();

	protected abstract boolean isAtValidPosition();

	protected boolean isInLiquid() {
		return this.mob.isTouchingWater() || this.mob.isTouchingLava();
	}

	protected void adjustPath() {
	}

	protected abstract boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ);
}
