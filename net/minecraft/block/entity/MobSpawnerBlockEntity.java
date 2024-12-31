package net.minecraft.block.entity;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity extends BlockEntity implements Tickable {
	private final SpawnerBlockEntityBehavior behaviorHandler = new SpawnerBlockEntityBehavior() {
		@Override
		public void sendStatus(int status) {
			MobSpawnerBlockEntity.this.world.addBlockAction(MobSpawnerBlockEntity.this.pos, Blocks.SPAWNER, status, 0);
		}

		@Override
		public World getWorld() {
			return MobSpawnerBlockEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return MobSpawnerBlockEntity.this.pos;
		}

		@Override
		public void setSpawnEntry(SpawnerBlockEntityBehavior.SpawnEntry spawnEntry) {
			super.setSpawnEntry(spawnEntry);
			if (this.getWorld() != null) {
				this.getWorld().onBlockUpdate(MobSpawnerBlockEntity.this.pos);
			}
		}
	};

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.behaviorHandler.deserialize(nbt);
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		this.behaviorHandler.serialize(nbt);
	}

	@Override
	public void tick() {
		this.behaviorHandler.tick();
	}

	@Override
	public Packet getPacket() {
		NbtCompound nbtCompound = new NbtCompound();
		this.toNbt(nbtCompound);
		nbtCompound.remove("SpawnPotentials");
		return new BlockEntityUpdateS2CPacket(this.pos, 1, nbtCompound);
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		return this.behaviorHandler.handleStatus(code) ? true : super.onBlockAction(code, data);
	}

	@Override
	public boolean shouldNotCopyNbtFromItem() {
		return true;
	}

	public SpawnerBlockEntityBehavior getLogic() {
		return this.behaviorHandler;
	}
}
