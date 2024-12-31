package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BedSleepS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPassengersS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackedEntityInstance {
	private static final Logger LOGGER = LogManager.getLogger();
	private Entity trackedEntity;
	private int trackingDistance;
	private int field_13858;
	private int tracingFrequency;
	private long field_13859;
	private long field_13860;
	private long field_13861;
	private int serializedYaw;
	private int serializedPitch;
	private int headRotationYaw;
	private double velocityX;
	private double velocityY;
	private double velocityZ;
	public int field_2871;
	private double x;
	private double y;
	private double z;
	private boolean field_2877;
	private boolean trackVelocity;
	private int field_2879;
	private List<Entity> field_13862 = Collections.emptyList();
	private boolean field_5303;
	private boolean onGround;
	public boolean field_2872;
	private Set<ServerPlayerEntity> players = Sets.newHashSet();

	public TrackedEntityInstance(Entity entity, int i, int j, int k, boolean bl) {
		this.trackedEntity = entity;
		this.trackingDistance = i;
		this.field_13858 = j;
		this.tracingFrequency = k;
		this.trackVelocity = bl;
		this.field_13859 = EntityTracker.method_12764(entity.x);
		this.field_13860 = EntityTracker.method_12764(entity.y);
		this.field_13861 = EntityTracker.method_12764(entity.z);
		this.serializedYaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
		this.serializedPitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
		this.headRotationYaw = MathHelper.floor(entity.getHeadRotation() * 256.0F / 360.0F);
		this.onGround = entity.onGround;
	}

	public boolean equals(Object obj) {
		return obj instanceof TrackedEntityInstance ? ((TrackedEntityInstance)obj).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
	}

	public int hashCode() {
		return this.trackedEntity.getEntityId();
	}

	public void method_2181(List<PlayerEntity> players) {
		this.field_2872 = false;
		if (!this.field_2877 || this.trackedEntity.squaredDistanceTo(this.x, this.y, this.z) > 16.0) {
			this.x = this.trackedEntity.x;
			this.y = this.trackedEntity.y;
			this.z = this.trackedEntity.z;
			this.field_2877 = true;
			this.field_2872 = true;
			this.method_2185(players);
		}

		List<Entity> list = this.trackedEntity.getPassengerList();
		if (!list.equals(this.field_13862)) {
			this.field_13862 = list;
			this.method_2179(new SetPassengersS2CPacket(this.trackedEntity));
		}

		if (this.trackedEntity instanceof ItemFrameEntity && this.field_2871 % 10 == 0) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.trackedEntity;
			ItemStack itemStack = itemFrameEntity.getHeldItemStack();
			if (itemStack != null && itemStack.getItem() instanceof FilledMapItem) {
				MapState mapState = Items.FILLED_MAP.getMapState(itemStack, this.trackedEntity.world);

				for (PlayerEntity playerEntity : players) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
					mapState.update(serverPlayerEntity, itemStack);
					Packet<?> packet = Items.FILLED_MAP.createSyncPacket(itemStack, this.trackedEntity.world, serverPlayerEntity);
					if (packet != null) {
						serverPlayerEntity.networkHandler.sendPacket(packet);
					}
				}
			}

			this.method_6068();
		}

		if (this.field_2871 % this.tracingFrequency == 0 || this.trackedEntity.velocityDirty || this.trackedEntity.getDataTracker().isDirty()) {
			if (!this.trackedEntity.hasMount()) {
				this.field_2879++;
				long l = EntityTracker.method_12764(this.trackedEntity.x);
				long m = EntityTracker.method_12764(this.trackedEntity.y);
				long n = EntityTracker.method_12764(this.trackedEntity.z);
				int i = MathHelper.floor(this.trackedEntity.yaw * 256.0F / 360.0F);
				int j = MathHelper.floor(this.trackedEntity.pitch * 256.0F / 360.0F);
				long o = l - this.field_13859;
				long p = m - this.field_13860;
				long q = n - this.field_13861;
				Packet<?> packet2 = null;
				boolean bl = o * o + p * p + q * q >= 128L || this.field_2871 % 60 == 0;
				boolean bl2 = Math.abs(i - this.serializedYaw) >= 1 || Math.abs(j - this.serializedPitch) >= 1;
				if (this.field_2871 > 0 || this.trackedEntity instanceof AbstractArrowEntity) {
					if (o >= -32768L
						&& o < 32768L
						&& p >= -32768L
						&& p < 32768L
						&& q >= -32768L
						&& q < 32768L
						&& this.field_2879 <= 400
						&& !this.field_5303
						&& this.onGround == this.trackedEntity.onGround) {
						if ((!bl || !bl2) && !(this.trackedEntity instanceof AbstractArrowEntity)) {
							if (bl) {
								packet2 = new EntityS2CPacket.MoveRelative(this.trackedEntity.getEntityId(), o, p, q, this.trackedEntity.onGround);
							} else if (bl2) {
								packet2 = new EntityS2CPacket.Rotate(this.trackedEntity.getEntityId(), (byte)i, (byte)j, this.trackedEntity.onGround);
							}
						} else {
							packet2 = new EntityS2CPacket.RotateAndMoveRelative(this.trackedEntity.getEntityId(), o, p, q, (byte)i, (byte)j, this.trackedEntity.onGround);
						}
					} else {
						this.onGround = this.trackedEntity.onGround;
						this.field_2879 = 0;
						this.method_12795();
						packet2 = new EntityPositionS2CPacket(this.trackedEntity);
					}
				}

				boolean bl3 = this.trackVelocity;
				if (this.trackedEntity instanceof LivingEntity && ((LivingEntity)this.trackedEntity).method_13055()) {
					bl3 = true;
				}

				if (bl3) {
					double d = this.trackedEntity.velocityX - this.velocityX;
					double e = this.trackedEntity.velocityY - this.velocityY;
					double f = this.trackedEntity.velocityZ - this.velocityZ;
					double g = 0.02;
					double h = d * d + e * e + f * f;
					if (h > 4.0E-4 || h > 0.0 && this.trackedEntity.velocityX == 0.0 && this.trackedEntity.velocityY == 0.0 && this.trackedEntity.velocityZ == 0.0) {
						this.velocityX = this.trackedEntity.velocityX;
						this.velocityY = this.trackedEntity.velocityY;
						this.velocityZ = this.trackedEntity.velocityZ;
						this.method_2179(new EntityVelocityUpdateS2CPacket(this.trackedEntity.getEntityId(), this.velocityX, this.velocityY, this.velocityZ));
					}
				}

				if (packet2 != null) {
					this.method_2179(packet2);
				}

				this.method_6068();
				if (bl) {
					this.field_13859 = l;
					this.field_13860 = m;
					this.field_13861 = n;
				}

				if (bl2) {
					this.serializedYaw = i;
					this.serializedPitch = j;
				}

				this.field_5303 = false;
			} else {
				int k = MathHelper.floor(this.trackedEntity.yaw * 256.0F / 360.0F);
				int r = MathHelper.floor(this.trackedEntity.pitch * 256.0F / 360.0F);
				boolean bl4 = Math.abs(k - this.serializedYaw) >= 1 || Math.abs(r - this.serializedPitch) >= 1;
				if (bl4) {
					this.method_2179(new EntityS2CPacket.Rotate(this.trackedEntity.getEntityId(), (byte)k, (byte)r, this.trackedEntity.onGround));
					this.serializedYaw = k;
					this.serializedPitch = r;
				}

				this.field_13859 = EntityTracker.method_12764(this.trackedEntity.x);
				this.field_13860 = EntityTracker.method_12764(this.trackedEntity.y);
				this.field_13861 = EntityTracker.method_12764(this.trackedEntity.z);
				this.method_6068();
				this.field_5303 = true;
			}

			int s = MathHelper.floor(this.trackedEntity.getHeadRotation() * 256.0F / 360.0F);
			if (Math.abs(s - this.headRotationYaw) >= 1) {
				this.method_2179(new EntitySetHeadYawS2CPacket(this.trackedEntity, (byte)s));
				this.headRotationYaw = s;
			}

			this.trackedEntity.velocityDirty = false;
		}

		this.field_2871++;
		if (this.trackedEntity.velocityModified) {
			this.method_2183(new EntityVelocityUpdateS2CPacket(this.trackedEntity));
			this.trackedEntity.velocityModified = false;
		}
	}

	private void method_6068() {
		DataTracker dataTracker = this.trackedEntity.getDataTracker();
		if (dataTracker.isDirty()) {
			this.method_2183(new EntityTrackerUpdateS2CPacket(this.trackedEntity.getEntityId(), dataTracker, false));
		}

		if (this.trackedEntity instanceof LivingEntity) {
			EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.trackedEntity).getAttributeContainer();
			Set<EntityAttributeInstance> set = entityAttributeContainer.getTrackedAttributes();
			if (!set.isEmpty()) {
				this.method_2183(new EntityAttributesS2CPacket(this.trackedEntity.getEntityId(), set));
			}

			set.clear();
		}
	}

	public void method_2179(Packet<?> packet) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.networkHandler.sendPacket(packet);
		}
	}

	public void method_2183(Packet<?> packet) {
		this.method_2179(packet);
		if (this.trackedEntity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)this.trackedEntity).networkHandler.sendPacket(packet);
		}
	}

	public void method_2178() {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			this.trackedEntity.onStoppedTrackingBy(serverPlayerEntity);
			serverPlayerEntity.stopTracking(this.trackedEntity);
		}
	}

	public void method_2180(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.contains(serverPlayerEntity)) {
			this.trackedEntity.onStoppedTrackingBy(serverPlayerEntity);
			serverPlayerEntity.stopTracking(this.trackedEntity);
			this.players.remove(serverPlayerEntity);
		}
	}

	public void method_2184(ServerPlayerEntity player) {
		if (player != this.trackedEntity) {
			if (this.method_10770(player)) {
				if (!this.players.contains(player) && (this.method_2187(player) || this.trackedEntity.teleporting)) {
					this.players.add(player);
					Packet<?> packet = this.method_2182();
					player.networkHandler.sendPacket(packet);
					if (!this.trackedEntity.getDataTracker().isEmpty()) {
						player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(this.trackedEntity.getEntityId(), this.trackedEntity.getDataTracker(), true));
					}

					boolean bl = this.trackVelocity;
					if (this.trackedEntity instanceof LivingEntity) {
						EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.trackedEntity).getAttributeContainer();
						Collection<EntityAttributeInstance> collection = entityAttributeContainer.buildTrackedAttributesCollection();
						if (!collection.isEmpty()) {
							player.networkHandler.sendPacket(new EntityAttributesS2CPacket(this.trackedEntity.getEntityId(), collection));
						}

						if (((LivingEntity)this.trackedEntity).method_13055()) {
							bl = true;
						}
					}

					this.velocityX = this.trackedEntity.velocityX;
					this.velocityY = this.trackedEntity.velocityY;
					this.velocityZ = this.trackedEntity.velocityZ;
					if (bl && !(packet instanceof MobSpawnS2CPacket)) {
						player.networkHandler
							.sendPacket(
								new EntityVelocityUpdateS2CPacket(
									this.trackedEntity.getEntityId(), this.trackedEntity.velocityX, this.trackedEntity.velocityY, this.trackedEntity.velocityZ
								)
							);
					}

					if (this.trackedEntity instanceof LivingEntity) {
						for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
							ItemStack itemStack = ((LivingEntity)this.trackedEntity).getStack(equipmentSlot);
							if (itemStack != null) {
								player.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(this.trackedEntity.getEntityId(), equipmentSlot, itemStack));
							}
						}
					}

					if (this.trackedEntity instanceof PlayerEntity) {
						PlayerEntity playerEntity = (PlayerEntity)this.trackedEntity;
						if (playerEntity.isSleeping()) {
							player.networkHandler.sendPacket(new BedSleepS2CPacket(playerEntity, new BlockPos(this.trackedEntity)));
						}
					}

					if (this.trackedEntity instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity)this.trackedEntity;

						for (StatusEffectInstance statusEffectInstance : livingEntity.getStatusEffectInstances()) {
							player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.trackedEntity.getEntityId(), statusEffectInstance));
						}
					}

					this.trackedEntity.onStartedTrackingBy(player);
					player.method_12790(this.trackedEntity);
				}
			} else if (this.players.contains(player)) {
				this.players.remove(player);
				this.trackedEntity.onStoppedTrackingBy(player);
				player.stopTracking(this.trackedEntity);
			}
		}
	}

	public boolean method_10770(ServerPlayerEntity player) {
		double d = player.x - (double)this.field_13859 / 4096.0;
		double e = player.z - (double)this.field_13861 / 4096.0;
		int i = Math.min(this.trackingDistance, this.field_13858);
		return d >= (double)(-i) && d <= (double)i && e >= (double)(-i) && e <= (double)i && this.trackedEntity.isSpectatedBy(player);
	}

	private boolean method_2187(ServerPlayerEntity player) {
		return player.getServerWorld().getPlayerWorldManager().method_2110(player, this.trackedEntity.chunkX, this.trackedEntity.chunkZ);
	}

	public void method_2185(List<PlayerEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			this.method_2184((ServerPlayerEntity)list.get(i));
		}
	}

	private Packet<?> method_2182() {
		if (this.trackedEntity.removed) {
			LOGGER.warn("Fetching addPacket for removed entity");
		}

		if (this.trackedEntity instanceof ItemEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 2, 1);
		} else if (this.trackedEntity instanceof ServerPlayerEntity) {
			return new PlayerSpawnS2CPacket((PlayerEntity)this.trackedEntity);
		} else if (this.trackedEntity instanceof AbstractMinecartEntity) {
			AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)this.trackedEntity;
			return new EntitySpawnS2CPacket(this.trackedEntity, 10, abstractMinecartEntity.getMinecartType().getId());
		} else if (this.trackedEntity instanceof BoatEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 1);
		} else if (this.trackedEntity instanceof EntityCategoryProvider) {
			this.headRotationYaw = MathHelper.floor(this.trackedEntity.getHeadRotation() * 256.0F / 360.0F);
			return new MobSpawnS2CPacket((LivingEntity)this.trackedEntity);
		} else if (this.trackedEntity instanceof FishingBobberEntity) {
			Entity entity = ((FishingBobberEntity)this.trackedEntity).thrower;
			return new EntitySpawnS2CPacket(this.trackedEntity, 90, entity != null ? entity.getEntityId() : this.trackedEntity.getEntityId());
		} else if (this.trackedEntity instanceof SpectralArrowEntity) {
			Entity entity2 = ((SpectralArrowEntity)this.trackedEntity).owner;
			return new EntitySpawnS2CPacket(this.trackedEntity, 91, 1 + (entity2 != null ? entity2.getEntityId() : this.trackedEntity.getEntityId()));
		} else if (this.trackedEntity instanceof ArrowEntity) {
			Entity entity3 = ((AbstractArrowEntity)this.trackedEntity).owner;
			return new EntitySpawnS2CPacket(this.trackedEntity, 60, 1 + (entity3 != null ? entity3.getEntityId() : this.trackedEntity.getEntityId()));
		} else if (this.trackedEntity instanceof SnowballEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 61);
		} else if (this.trackedEntity instanceof PotionEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 73);
		} else if (this.trackedEntity instanceof ExperienceBottleEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 75);
		} else if (this.trackedEntity instanceof EnderPearlEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 65);
		} else if (this.trackedEntity instanceof EyeOfEnderEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 72);
		} else if (this.trackedEntity instanceof FireworkRocketEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 76);
		} else if (this.trackedEntity instanceof ExplosiveProjectileEntity) {
			ExplosiveProjectileEntity explosiveProjectileEntity = (ExplosiveProjectileEntity)this.trackedEntity;
			EntitySpawnS2CPacket entitySpawnS2CPacket = null;
			int i = 63;
			if (this.trackedEntity instanceof SmallFireballEntity) {
				i = 64;
			} else if (this.trackedEntity instanceof DragonFireballEntity) {
				i = 93;
			} else if (this.trackedEntity instanceof WitherSkullEntity) {
				i = 66;
			}

			if (explosiveProjectileEntity.target != null) {
				entitySpawnS2CPacket = new EntitySpawnS2CPacket(this.trackedEntity, i, ((ExplosiveProjectileEntity)this.trackedEntity).target.getEntityId());
			} else {
				entitySpawnS2CPacket = new EntitySpawnS2CPacket(this.trackedEntity, i, 0);
			}

			entitySpawnS2CPacket.setVelocityX((int)(explosiveProjectileEntity.powerX * 8000.0));
			entitySpawnS2CPacket.setVelocityY((int)(explosiveProjectileEntity.powerY * 8000.0));
			entitySpawnS2CPacket.setVelocityZ((int)(explosiveProjectileEntity.powerZ * 8000.0));
			return entitySpawnS2CPacket;
		} else if (this.trackedEntity instanceof ShulkerBulletEntity) {
			EntitySpawnS2CPacket entitySpawnS2CPacket2 = new EntitySpawnS2CPacket(this.trackedEntity, 67, 0);
			entitySpawnS2CPacket2.setVelocityX((int)(this.trackedEntity.velocityX * 8000.0));
			entitySpawnS2CPacket2.setVelocityY((int)(this.trackedEntity.velocityY * 8000.0));
			entitySpawnS2CPacket2.setVelocityZ((int)(this.trackedEntity.velocityZ * 8000.0));
			return entitySpawnS2CPacket2;
		} else if (this.trackedEntity instanceof EggEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 62);
		} else if (this.trackedEntity instanceof TntEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 50);
		} else if (this.trackedEntity instanceof EndCrystalEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 51);
		} else if (this.trackedEntity instanceof FallingBlockEntity) {
			FallingBlockEntity fallingBlockEntity = (FallingBlockEntity)this.trackedEntity;
			return new EntitySpawnS2CPacket(this.trackedEntity, 70, Block.getByBlockState(fallingBlockEntity.getBlockState()));
		} else if (this.trackedEntity instanceof ArmorStandEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 78);
		} else if (this.trackedEntity instanceof PaintingEntity) {
			return new PaintingSpawnS2CPacket((PaintingEntity)this.trackedEntity);
		} else if (this.trackedEntity instanceof ItemFrameEntity) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.trackedEntity;
			return new EntitySpawnS2CPacket(this.trackedEntity, 71, itemFrameEntity.direction.getHorizontal(), itemFrameEntity.getTilePos());
		} else if (this.trackedEntity instanceof LeashKnotEntity) {
			LeashKnotEntity leashKnotEntity = (LeashKnotEntity)this.trackedEntity;
			return new EntitySpawnS2CPacket(this.trackedEntity, 77, 0, leashKnotEntity.getTilePos());
		} else if (this.trackedEntity instanceof ExperienceOrbEntity) {
			return new ExperienceOrbSpawnS2CPacket((ExperienceOrbEntity)this.trackedEntity);
		} else if (this.trackedEntity instanceof AreaEffectCloudEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 3);
		} else {
			throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
		}
	}

	public void removeTrackingPlayer(ServerPlayerEntity player) {
		if (this.players.contains(player)) {
			this.players.remove(player);
			this.trackedEntity.onStoppedTrackingBy(player);
			player.stopTracking(this.trackedEntity);
		}
	}

	public Entity method_12794() {
		return this.trackedEntity;
	}

	public void method_12793(int i) {
		this.field_13858 = i;
	}

	public void method_12795() {
		this.field_2877 = false;
	}
}
