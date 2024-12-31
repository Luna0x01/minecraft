package net.minecraft.server.network;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerWorld world;
	private final Entity entity;
	private final int tickInterval;
	private final boolean alwaysUpdateVelocity;
	private final Consumer<Packet<?>> receiver;
	private long lastX;
	private long lastY;
	private long lastZ;
	private int lastYaw;
	private int lastPitch;
	private int lastHeadPitch;
	private Vec3d velocity = Vec3d.ZERO;
	private int trackingTick;
	private int updatesWithoutVehicle;
	private List<Entity> lastPassengers = Collections.emptyList();
	private boolean hadVehicle;
	private boolean lastOnGround;

	public EntityTrackerEntry(ServerWorld serverWorld, Entity entity, int i, boolean bl, Consumer<Packet<?>> consumer) {
		this.world = serverWorld;
		this.receiver = consumer;
		this.entity = entity;
		this.tickInterval = i;
		this.alwaysUpdateVelocity = bl;
		this.storeEncodedCoordinates();
		this.lastYaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.lastPitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.lastHeadPitch = MathHelper.floor(entity.getHeadYaw() * 256.0F / 360.0F);
		this.lastOnGround = entity.onGround;
	}

	public void tick() {
		List<Entity> list = this.entity.getPassengerList();
		if (!list.equals(this.lastPassengers)) {
			this.lastPassengers = list;
			this.receiver.accept(new EntityPassengersSetS2CPacket(this.entity));
		}

		if (this.entity instanceof ItemFrameEntity && this.trackingTick % 10 == 0) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.entity;
			ItemStack itemStack = itemFrameEntity.getHeldItemStack();
			if (itemStack.getItem() instanceof FilledMapItem) {
				MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.world);

				for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers()) {
					mapState.update(serverPlayerEntity, itemStack);
					Packet<?> packet = ((FilledMapItem)itemStack.getItem()).createSyncPacket(itemStack, this.world, serverPlayerEntity);
					if (packet != null) {
						serverPlayerEntity.networkHandler.sendPacket(packet);
					}
				}
			}

			this.syncEntityData();
		}

		if (this.trackingTick % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
			if (this.entity.hasVehicle()) {
				int i = MathHelper.floor(this.entity.yaw * 256.0F / 360.0F);
				int j = MathHelper.floor(this.entity.pitch * 256.0F / 360.0F);
				boolean bl = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
				if (bl) {
					this.receiver.accept(new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)i, (byte)j, this.entity.onGround));
					this.lastYaw = i;
					this.lastPitch = j;
				}

				this.storeEncodedCoordinates();
				this.syncEntityData();
				this.hadVehicle = true;
			} else {
				this.updatesWithoutVehicle++;
				int k = MathHelper.floor(this.entity.yaw * 256.0F / 360.0F);
				int l = MathHelper.floor(this.entity.pitch * 256.0F / 360.0F);
				Vec3d vec3d = this.entity.getPos().subtract(EntityS2CPacket.decodePacketCoordinates(this.lastX, this.lastY, this.lastZ));
				boolean bl2 = vec3d.lengthSquared() >= 7.6293945E-6F;
				Packet<?> packet2 = null;
				boolean bl3 = bl2 || this.trackingTick % 60 == 0;
				boolean bl4 = Math.abs(k - this.lastYaw) >= 1 || Math.abs(l - this.lastPitch) >= 1;
				if (this.trackingTick > 0 || this.entity instanceof ProjectileEntity) {
					long m = EntityS2CPacket.encodePacketCoordinate(vec3d.x);
					long n = EntityS2CPacket.encodePacketCoordinate(vec3d.y);
					long o = EntityS2CPacket.encodePacketCoordinate(vec3d.z);
					boolean bl5 = m < -32768L || m > 32767L || n < -32768L || n > 32767L || o < -32768L || o > 32767L;
					if (!bl5 && this.updatesWithoutVehicle <= 400 && !this.hadVehicle && this.lastOnGround == this.entity.onGround) {
						if ((!bl3 || !bl4) && !(this.entity instanceof ProjectileEntity)) {
							if (bl3) {
								packet2 = new EntityS2CPacket.MoveRelative(this.entity.getEntityId(), (short)((int)m), (short)((int)n), (short)((int)o), this.entity.onGround);
							} else if (bl4) {
								packet2 = new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)k, (byte)l, this.entity.onGround);
							}
						} else {
							packet2 = new EntityS2CPacket.RotateAndMoveRelative(
								this.entity.getEntityId(), (short)((int)m), (short)((int)n), (short)((int)o), (byte)k, (byte)l, this.entity.onGround
							);
						}
					} else {
						this.lastOnGround = this.entity.onGround;
						this.updatesWithoutVehicle = 0;
						packet2 = new EntityPositionS2CPacket(this.entity);
					}
				}

				if ((this.alwaysUpdateVelocity || this.entity.velocityDirty || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying())
					&& this.trackingTick > 0) {
					Vec3d vec3d2 = this.entity.getVelocity();
					double d = vec3d2.squaredDistanceTo(this.velocity);
					if (d > 1.0E-7 || d > 0.0 && vec3d2.lengthSquared() == 0.0) {
						this.velocity = vec3d2;
						this.receiver.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.velocity));
					}
				}

				if (packet2 != null) {
					this.receiver.accept(packet2);
				}

				this.syncEntityData();
				if (bl3) {
					this.storeEncodedCoordinates();
				}

				if (bl4) {
					this.lastYaw = k;
					this.lastPitch = l;
				}

				this.hadVehicle = false;
			}

			int p = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
			if (Math.abs(p - this.lastHeadPitch) >= 1) {
				this.receiver.accept(new EntitySetHeadYawS2CPacket(this.entity, (byte)p));
				this.lastHeadPitch = p;
			}

			this.entity.velocityDirty = false;
		}

		this.trackingTick++;
		if (this.entity.velocityModified) {
			this.sendSyncPacket(new EntityVelocityUpdateS2CPacket(this.entity));
			this.entity.velocityModified = false;
		}
	}

	public void stopTracking(ServerPlayerEntity serverPlayerEntity) {
		this.entity.onStoppedTrackingBy(serverPlayerEntity);
		serverPlayerEntity.onStoppedTracking(this.entity);
	}

	public void startTracking(ServerPlayerEntity serverPlayerEntity) {
		this.sendPackets(serverPlayerEntity.networkHandler::sendPacket);
		this.entity.onStartedTrackingBy(serverPlayerEntity);
		serverPlayerEntity.onStartedTracking(this.entity);
	}

	public void sendPackets(Consumer<Packet<?>> consumer) {
		if (this.entity.removed) {
			LOGGER.warn("Fetching packet for removed entity " + this.entity);
		}

		Packet<?> packet = this.entity.createSpawnPacket();
		this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
		consumer.accept(packet);
		if (!this.entity.getDataTracker().isEmpty()) {
			consumer.accept(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), this.entity.getDataTracker(), true));
		}

		boolean bl = this.alwaysUpdateVelocity;
		if (this.entity instanceof LivingEntity) {
			EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributes();
			Collection<EntityAttributeInstance> collection = entityAttributeContainer.buildTrackedAttributesCollection();
			if (!collection.isEmpty()) {
				consumer.accept(new EntityAttributesS2CPacket(this.entity.getEntityId(), collection));
			}

			if (((LivingEntity)this.entity).isFallFlying()) {
				bl = true;
			}
		}

		this.velocity = this.entity.getVelocity();
		if (bl && !(packet instanceof MobSpawnS2CPacket)) {
			consumer.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.velocity));
		}

		if (this.entity instanceof LivingEntity) {
			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack = ((LivingEntity)this.entity).getEquippedStack(equipmentSlot);
				if (!itemStack.isEmpty()) {
					consumer.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getEntityId(), equipmentSlot, itemStack));
				}
			}
		}

		if (this.entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.entity;

			for (StatusEffectInstance statusEffectInstance : livingEntity.getStatusEffects()) {
				consumer.accept(new EntityPotionEffectS2CPacket(this.entity.getEntityId(), statusEffectInstance));
			}
		}

		if (!this.entity.getPassengerList().isEmpty()) {
			consumer.accept(new EntityPassengersSetS2CPacket(this.entity));
		}

		if (this.entity.hasVehicle()) {
			consumer.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
		}

		if (this.entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)this.entity;
			if (mobEntity.isLeashed()) {
				consumer.accept(new EntityAttachS2CPacket(mobEntity, mobEntity.getHoldingEntity()));
			}
		}
	}

	private void syncEntityData() {
		DataTracker dataTracker = this.entity.getDataTracker();
		if (dataTracker.isDirty()) {
			this.sendSyncPacket(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), dataTracker, false));
		}

		if (this.entity instanceof LivingEntity) {
			EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributes();
			Set<EntityAttributeInstance> set = entityAttributeContainer.getTrackedAttributes();
			if (!set.isEmpty()) {
				this.sendSyncPacket(new EntityAttributesS2CPacket(this.entity.getEntityId(), set));
			}

			set.clear();
		}
	}

	private void storeEncodedCoordinates() {
		this.lastX = EntityS2CPacket.encodePacketCoordinate(this.entity.getX());
		this.lastY = EntityS2CPacket.encodePacketCoordinate(this.entity.getY());
		this.lastZ = EntityS2CPacket.encodePacketCoordinate(this.entity.getZ());
	}

	public Vec3d getLastPos() {
		return EntityS2CPacket.decodePacketCoordinates(this.lastX, this.lastY, this.lastZ);
	}

	private void sendSyncPacket(Packet<?> packet) {
		this.receiver.accept(packet);
		if (this.entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
		}
	}
}
