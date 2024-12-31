package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SpawnerBlockEntityBehavior;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
	private final SpawnerBlockEntityBehavior spawner = new SpawnerBlockEntityBehavior() {
		@Override
		public void sendStatus(int status) {
			SpawnerMinecartEntity.this.world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)status);
		}

		@Override
		public World getWorld() {
			return SpawnerMinecartEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(SpawnerMinecartEntity.this);
		}
	};

	public SpawnerMinecartEntity(World world) {
		super(world);
	}

	public SpawnerMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.SPAWNER;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.SPAWNER.getDefaultState();
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.spawner.deserialize(nbt);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.spawner.serialize(nbt);
	}

	@Override
	public void handleStatus(byte status) {
		this.spawner.handleStatus(status);
	}

	@Override
	public void tick() {
		super.tick();
		this.spawner.tick();
	}

	public SpawnerBlockEntityBehavior getSpawnerBehavior() {
		return this.spawner;
	}
}
