package net.minecraft.entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class MarkerEntity extends Entity {
	private static final String DATA_KEY = "data";
	private NbtCompound data = new NbtCompound();

	public MarkerEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
	}

	@Override
	public void tick() {
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.data = nbt.getCompound("data");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("data", this.data.copy());
	}

	@Override
	public Packet<?> createSpawnPacket() {
		throw new IllegalStateException("Markers should never be sent");
	}

	@Override
	protected void addPassenger(Entity passenger) {
		passenger.stopRiding();
	}
}
