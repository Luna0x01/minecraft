package net.minecraft.util.hit;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockHitResult {
	private BlockPos blockPos;
	public BlockHitResult.Type type;
	public Direction direction;
	public Vec3d pos;
	public Entity entity;

	public BlockHitResult(Vec3d vec3d, Direction direction, BlockPos blockPos) {
		this(BlockHitResult.Type.BLOCK, vec3d, direction, blockPos);
	}

	public BlockHitResult(Entity entity) {
		this(entity, new Vec3d(entity.x, entity.y, entity.z));
	}

	public BlockHitResult(BlockHitResult.Type type, Vec3d vec3d, Direction direction, BlockPos blockPos) {
		this.type = type;
		this.blockPos = blockPos;
		this.direction = direction;
		this.pos = new Vec3d(vec3d.x, vec3d.y, vec3d.z);
	}

	public BlockHitResult(Entity entity, Vec3d vec3d) {
		this.type = BlockHitResult.Type.ENTITY;
		this.entity = entity;
		this.pos = vec3d;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public String toString() {
		return "HitResult{type=" + this.type + ", blockpos=" + this.blockPos + ", f=" + this.direction + ", pos=" + this.pos + ", entity=" + this.entity + '}';
	}

	public static enum Type {
		MISS,
		BLOCK,
		ENTITY;
	}
}
