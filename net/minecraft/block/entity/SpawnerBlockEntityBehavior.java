package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;

public abstract class SpawnerBlockEntityBehavior {
	private int spawnDelay = 20;
	private final List<SpawnerBlockEntityBehaviorEntry> spawnPotentials = Lists.newArrayList();
	private SpawnerBlockEntityBehaviorEntry entry = new SpawnerBlockEntityBehaviorEntry();
	private double mobAngleNext;
	private double mobAngle;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	private Entity renderedEntity;
	private int maxNearbyEntities = 6;
	private int requiredPlayerRange = 16;
	private int spawnRange = 4;

	private String getEntityId() {
		return this.entry.getCompoundTag().getString("id");
	}

	public void setEntityId(String entityId) {
		this.entry.getCompoundTag().putString("id", entityId);
	}

	private boolean isPlayerInRange() {
		BlockPos blockPos = this.getPos();
		return this.getWorld()
			.isPlayerInRange((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void tick() {
		if (!this.isPlayerInRange()) {
			this.mobAngle = this.mobAngleNext;
		} else {
			BlockPos blockPos = this.getPos();
			if (this.getWorld().isClient) {
				double d = (double)((float)blockPos.getX() + this.getWorld().random.nextFloat());
				double e = (double)((float)blockPos.getY() + this.getWorld().random.nextFloat());
				double f = (double)((float)blockPos.getZ() + this.getWorld().random.nextFloat());
				this.getWorld().addParticle(ParticleType.SMOKE, d, e, f, 0.0, 0.0, 0.0);
				this.getWorld().addParticle(ParticleType.FIRE, d, e, f, 0.0, 0.0, 0.0);
				if (this.spawnDelay > 0) {
					this.spawnDelay--;
				}

				this.mobAngle = this.mobAngleNext;
				this.mobAngleNext = (this.mobAngleNext + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
			} else {
				if (this.spawnDelay == -1) {
					this.updateSpawns();
				}

				if (this.spawnDelay > 0) {
					this.spawnDelay--;
					return;
				}

				boolean bl = false;

				for (int i = 0; i < this.spawnCount; i++) {
					NbtCompound nbtCompound = this.entry.getCompoundTag();
					NbtList nbtList = nbtCompound.getList("Pos", 6);
					World world = this.getWorld();
					int j = nbtList.size();
					double g = j >= 1
						? nbtList.getDouble(0)
						: (double)blockPos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					double h = j >= 2 ? nbtList.getDouble(1) : (double)(blockPos.getY() + world.random.nextInt(3) - 1);
					double k = j >= 3
						? nbtList.getDouble(2)
						: (double)blockPos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					Entity entity = ThreadedAnvilChunkStorage.method_11782(nbtCompound, world, g, h, k, false);
					if (entity == null) {
						return;
					}

					int l = world.getEntitiesInBox(
							entity.getClass(),
							new Box(
									(double)blockPos.getX(),
									(double)blockPos.getY(),
									(double)blockPos.getZ(),
									(double)(blockPos.getX() + 1),
									(double)(blockPos.getY() + 1),
									(double)(blockPos.getZ() + 1)
								)
								.expand((double)this.spawnRange)
						)
						.size();
					if (l >= this.maxNearbyEntities) {
						this.updateSpawns();
						return;
					}

					MobEntity mobEntity = entity instanceof MobEntity ? (MobEntity)entity : null;
					entity.refreshPositionAndAngles(entity.x, entity.y, entity.z, world.random.nextFloat() * 360.0F, 0.0F);
					if (mobEntity == null || mobEntity.canSpawn() && mobEntity.hasNoSpawnCollisions()) {
						if (this.entry.getCompoundTag().getSize() == 1 && this.entry.getCompoundTag().contains("id", 8) && entity instanceof MobEntity) {
							((MobEntity)entity).initialize(world.getLocalDifficulty(new BlockPos(entity)), null);
						}

						ThreadedAnvilChunkStorage.method_11785(entity, world);
						world.syncGlobalEvent(2004, blockPos, 0);
						if (mobEntity != null) {
							mobEntity.playSpawnEffects();
						}

						bl = true;
					}
				}

				if (bl) {
					this.updateSpawns();
				}
			}
		}
	}

	private void updateSpawns() {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnData(Weighting.getRandom(this.getWorld().random, this.spawnPotentials));
		}

		this.sendStatus(1);
	}

	public void deserialize(NbtCompound nbt) {
		this.spawnDelay = nbt.getShort("Delay");
		this.spawnPotentials.clear();
		if (nbt.contains("SpawnPotentials", 9)) {
			NbtList nbtList = nbt.getList("SpawnPotentials", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				this.spawnPotentials.add(new SpawnerBlockEntityBehaviorEntry(nbtList.getCompound(i)));
			}
		}

		NbtCompound nbtCompound = nbt.getCompound("SpawnData");
		if (!nbtCompound.contains("id", 8)) {
			nbtCompound.putString("id", "Pig");
		}

		this.setSpawnData(new SpawnerBlockEntityBehaviorEntry(1, nbtCompound));
		if (nbt.contains("MinSpawnDelay", 99)) {
			this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
			this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
			this.spawnCount = nbt.getShort("SpawnCount");
		}

		if (nbt.contains("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
			this.requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
		}

		if (nbt.contains("SpawnRange", 99)) {
			this.spawnRange = nbt.getShort("SpawnRange");
		}

		if (this.getWorld() != null) {
			this.renderedEntity = null;
		}
	}

	public NbtCompound toTag(NbtCompound tag) {
		String string = this.getEntityId();
		if (ChatUtil.isEmpty(string)) {
			return tag;
		} else {
			tag.putShort("Delay", (short)this.spawnDelay);
			tag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
			tag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
			tag.putShort("SpawnCount", (short)this.spawnCount);
			tag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
			tag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
			tag.putShort("SpawnRange", (short)this.spawnRange);
			tag.put("SpawnData", this.entry.getCompoundTag().copy());
			NbtList nbtList = new NbtList();
			if (this.spawnPotentials.isEmpty()) {
				nbtList.add(this.entry.toCompoundTag());
			} else {
				for (SpawnerBlockEntityBehaviorEntry spawnerBlockEntityBehaviorEntry : this.spawnPotentials) {
					nbtList.add(spawnerBlockEntityBehaviorEntry.toCompoundTag());
				}
			}

			tag.put("SpawnPotentials", nbtList);
			return tag;
		}
	}

	public Entity method_11473() {
		if (this.renderedEntity == null) {
			this.renderedEntity = ThreadedAnvilChunkStorage.method_11784(this.entry.getCompoundTag(), this.getWorld(), false);
			if (this.entry.getCompoundTag().getSize() == 1 && this.entry.getCompoundTag().contains("id", 8) && this.renderedEntity instanceof MobEntity) {
				((MobEntity)this.renderedEntity).initialize(this.getWorld().getLocalDifficulty(new BlockPos(this.renderedEntity)), null);
			}
		}

		return this.renderedEntity;
	}

	public boolean handleStatus(int status) {
		if (status == 1 && this.getWorld().isClient) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	public void setSpawnData(SpawnerBlockEntityBehaviorEntry data) {
		this.entry = data;
	}

	public abstract void sendStatus(int status);

	public abstract World getWorld();

	public abstract BlockPos getPos();

	public double method_8463() {
		return this.mobAngleNext;
	}

	public double method_8464() {
		return this.mobAngle;
	}
}
