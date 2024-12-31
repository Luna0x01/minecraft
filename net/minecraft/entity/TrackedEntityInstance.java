package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BedSleepS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
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
import net.minecraft.network.packet.s2c.play.UpdateEntityNbtS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackedEntityInstance {
	private static final Logger LOGGER = LogManager.getLogger();
	public Entity trackedEntity;
	public int trackingDistance;
	public int tracingFrequency;
	public int serializedX;
	public int serializedY;
	public int serializedZ;
	public int serializedYaw;
	public int serializedPitch;
	public int headRotationYaw;
	public double velocityX;
	public double velocityY;
	public double velocityZ;
	public int field_2871;
	private double x;
	private double y;
	private double z;
	private boolean field_2877;
	private boolean trackVelocity;
	private int field_2879;
	private Entity field_2880;
	private boolean field_5303;
	private boolean onGround;
	public boolean field_2872;
	public Set<ServerPlayerEntity> players = Sets.newHashSet();

	public TrackedEntityInstance(Entity entity, int i, int j, boolean bl) {
		this.trackedEntity = entity;
		this.trackingDistance = i;
		this.tracingFrequency = j;
		this.trackVelocity = bl;
		this.serializedX = MathHelper.floor(entity.x * 32.0);
		this.serializedY = MathHelper.floor(entity.y * 32.0);
		this.serializedZ = MathHelper.floor(entity.z * 32.0);
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

		if (this.field_2880 != this.trackedEntity.vehicle || this.trackedEntity.vehicle != null && this.field_2871 % 60 == 0) {
			this.field_2880 = this.trackedEntity.vehicle;
			this.method_2179(new EntityAttachS2CPacket(0, this.trackedEntity, this.trackedEntity.vehicle));
		}

		if (this.trackedEntity instanceof ItemFrameEntity && this.field_2871 % 10 == 0) {
			ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.trackedEntity;
			ItemStack itemStack = itemFrameEntity.getHeldItemStack();
			if (itemStack != null && itemStack.getItem() instanceof FilledMapItem) {
				MapState mapState = Items.FILLED_MAP.getMapState(itemStack, this.trackedEntity.world);

				for (PlayerEntity playerEntity : players) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
					mapState.update(serverPlayerEntity, itemStack);
					Packet packet = Items.FILLED_MAP.createSyncPacket(itemStack, this.trackedEntity.world, serverPlayerEntity);
					if (packet != null) {
						serverPlayerEntity.networkHandler.sendPacket(packet);
					}
				}
			}

			this.method_6068();
		}

		if (this.field_2871 % this.tracingFrequency == 0 || this.trackedEntity.velocityDirty || this.trackedEntity.getDataTracker().isDirty()) {
			if (this.trackedEntity.vehicle == null) {
				this.field_2879++;
				int i = MathHelper.floor(this.trackedEntity.x * 32.0);
				int j = MathHelper.floor(this.trackedEntity.y * 32.0);
				int k = MathHelper.floor(this.trackedEntity.z * 32.0);
				int l = MathHelper.floor(this.trackedEntity.yaw * 256.0F / 360.0F);
				int m = MathHelper.floor(this.trackedEntity.pitch * 256.0F / 360.0F);
				int n = i - this.serializedX;
				int o = j - this.serializedY;
				int p = k - this.serializedZ;
				Packet packet2 = null;
				boolean bl = Math.abs(n) >= 4 || Math.abs(o) >= 4 || Math.abs(p) >= 4 || this.field_2871 % 60 == 0;
				boolean bl2 = Math.abs(l - this.serializedYaw) >= 4 || Math.abs(m - this.serializedPitch) >= 4;
				if (this.field_2871 > 0 || this.trackedEntity instanceof AbstractArrowEntity) {
					if (n >= -128
						&& n < 128
						&& o >= -128
						&& o < 128
						&& p >= -128
						&& p < 128
						&& this.field_2879 <= 400
						&& !this.field_5303
						&& this.onGround == this.trackedEntity.onGround) {
						if ((!bl || !bl2) && !(this.trackedEntity instanceof AbstractArrowEntity)) {
							if (bl) {
								packet2 = new EntityS2CPacket.MoveRelative(this.trackedEntity.getEntityId(), (byte)n, (byte)o, (byte)p, this.trackedEntity.onGround);
							} else if (bl2) {
								packet2 = new EntityS2CPacket.Rotate(this.trackedEntity.getEntityId(), (byte)l, (byte)m, this.trackedEntity.onGround);
							}
						} else {
							packet2 = new EntityS2CPacket.RotateAndMoveRelative(
								this.trackedEntity.getEntityId(), (byte)n, (byte)o, (byte)p, (byte)l, (byte)m, this.trackedEntity.onGround
							);
						}
					} else {
						this.onGround = this.trackedEntity.onGround;
						this.field_2879 = 0;
						packet2 = new EntityPositionS2CPacket(this.trackedEntity.getEntityId(), i, j, k, (byte)l, (byte)m, this.trackedEntity.onGround);
					}
				}

				if (this.trackVelocity) {
					double d = this.trackedEntity.velocityX - this.velocityX;
					double e = this.trackedEntity.velocityY - this.velocityY;
					double f = this.trackedEntity.velocityZ - this.velocityZ;
					double g = 0.02;
					double h = d * d + e * e + f * f;
					if (h > g * g || h > 0.0 && this.trackedEntity.velocityX == 0.0 && this.trackedEntity.velocityY == 0.0 && this.trackedEntity.velocityZ == 0.0) {
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
					this.serializedX = i;
					this.serializedY = j;
					this.serializedZ = k;
				}

				if (bl2) {
					this.serializedYaw = l;
					this.serializedPitch = m;
				}

				this.field_5303 = false;
			} else {
				int q = MathHelper.floor(this.trackedEntity.yaw * 256.0F / 360.0F);
				int r = MathHelper.floor(this.trackedEntity.pitch * 256.0F / 360.0F);
				boolean bl3 = Math.abs(q - this.serializedYaw) >= 4 || Math.abs(r - this.serializedPitch) >= 4;
				if (bl3) {
					this.method_2179(new EntityS2CPacket.Rotate(this.trackedEntity.getEntityId(), (byte)q, (byte)r, this.trackedEntity.onGround));
					this.serializedYaw = q;
					this.serializedPitch = r;
				}

				this.serializedX = MathHelper.floor(this.trackedEntity.x * 32.0);
				this.serializedY = MathHelper.floor(this.trackedEntity.y * 32.0);
				this.serializedZ = MathHelper.floor(this.trackedEntity.z * 32.0);
				this.method_6068();
				this.field_5303 = true;
			}

			int s = MathHelper.floor(this.trackedEntity.getHeadRotation() * 256.0F / 360.0F);
			if (Math.abs(s - this.headRotationYaw) >= 4) {
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

	public void method_2179(Packet packet) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.networkHandler.sendPacket(packet);
		}
	}

	public void method_2183(Packet packet) {
		this.method_2179(packet);
		if (this.trackedEntity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)this.trackedEntity).networkHandler.sendPacket(packet);
		}
	}

	public void method_2178() {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.stopTracking(this.trackedEntity);
		}
	}

	public void method_2180(ServerPlayerEntity serverPlayerEntity) {
		if (this.players.contains(serverPlayerEntity)) {
			serverPlayerEntity.stopTracking(this.trackedEntity);
			this.players.remove(serverPlayerEntity);
		}
	}

	public void method_2184(ServerPlayerEntity player) {
		if (player != this.trackedEntity) {
			if (this.method_10770(player)) {
				if (!this.players.contains(player) && (this.method_2187(player) || this.trackedEntity.teleporting)) {
					this.players.add(player);
					Packet packet = this.method_2182();
					player.networkHandler.sendPacket(packet);
					if (!this.trackedEntity.getDataTracker().isEmpty()) {
						player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(this.trackedEntity.getEntityId(), this.trackedEntity.getDataTracker(), true));
					}

					NbtCompound nbtCompound = this.trackedEntity.method_10948();
					if (nbtCompound != null) {
						player.networkHandler.sendPacket(new UpdateEntityNbtS2CPacket(this.trackedEntity.getEntityId(), nbtCompound));
					}

					if (this.trackedEntity instanceof LivingEntity) {
						EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.trackedEntity).getAttributeContainer();
						Collection<EntityAttributeInstance> collection = entityAttributeContainer.buildTrackedAttributesCollection();
						if (!collection.isEmpty()) {
							player.networkHandler.sendPacket(new EntityAttributesS2CPacket(this.trackedEntity.getEntityId(), collection));
						}
					}

					this.velocityX = this.trackedEntity.velocityX;
					this.velocityY = this.trackedEntity.velocityY;
					this.velocityZ = this.trackedEntity.velocityZ;
					if (this.trackVelocity && !(packet instanceof MobSpawnS2CPacket)) {
						player.networkHandler
							.sendPacket(
								new EntityVelocityUpdateS2CPacket(
									this.trackedEntity.getEntityId(), this.trackedEntity.velocityX, this.trackedEntity.velocityY, this.trackedEntity.velocityZ
								)
							);
					}

					if (this.trackedEntity.vehicle != null) {
						player.networkHandler.sendPacket(new EntityAttachS2CPacket(0, this.trackedEntity, this.trackedEntity.vehicle));
					}

					if (this.trackedEntity instanceof MobEntity && ((MobEntity)this.trackedEntity).getLeashOwner() != null) {
						player.networkHandler.sendPacket(new EntityAttachS2CPacket(1, this.trackedEntity, ((MobEntity)this.trackedEntity).getLeashOwner()));
					}

					if (this.trackedEntity instanceof LivingEntity) {
						for (int i = 0; i < 5; i++) {
							ItemStack itemStack = ((LivingEntity)this.trackedEntity).getMainSlot(i);
							if (itemStack != null) {
								player.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(this.trackedEntity.getEntityId(), i, itemStack));
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
				}
			} else if (this.players.contains(player)) {
				this.players.remove(player);
				player.stopTracking(this.trackedEntity);
			}
		}
	}

	public boolean method_10770(ServerPlayerEntity player) {
		double d = player.x - (double)(this.serializedX / 32);
		double e = player.z - (double)(this.serializedZ / 32);
		return d >= (double)(-this.trackingDistance)
			&& d <= (double)this.trackingDistance
			&& e >= (double)(-this.trackingDistance)
			&& e <= (double)this.trackingDistance
			&& this.trackedEntity.isSpectatedBy(player);
	}

	private boolean method_2187(ServerPlayerEntity player) {
		return player.getServerWorld().getPlayerWorldManager().method_2110(player, this.trackedEntity.chunkX, this.trackedEntity.chunkZ);
	}

	public void method_2185(List<PlayerEntity> list) {
		for (int i = 0; i < list.size(); i++) {
			this.method_2184((ServerPlayerEntity)list.get(i));
		}
	}

	private Packet method_2182() {
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
		} else if (this.trackedEntity instanceof AbstractArrowEntity) {
			Entity entity2 = ((AbstractArrowEntity)this.trackedEntity).owner;
			return new EntitySpawnS2CPacket(this.trackedEntity, 60, entity2 != null ? entity2.getEntityId() : this.trackedEntity.getEntityId());
		} else if (this.trackedEntity instanceof SnowballEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 61);
		} else if (this.trackedEntity instanceof PotionEntity) {
			return new EntitySpawnS2CPacket(this.trackedEntity, 73, ((PotionEntity)this.trackedEntity).method_3237());
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
			EntitySpawnS2CPacket entitySpawnS2CPacket2 = new EntitySpawnS2CPacket(this.trackedEntity, 71, itemFrameEntity.direction.getHorizontal());
			BlockPos blockPos = itemFrameEntity.getTilePos();
			entitySpawnS2CPacket2.setX(MathHelper.floor((float)(blockPos.getX() * 32)));
			entitySpawnS2CPacket2.setY(MathHelper.floor((float)(blockPos.getY() * 32)));
			entitySpawnS2CPacket2.setZ(MathHelper.floor((float)(blockPos.getZ() * 32)));
			return entitySpawnS2CPacket2;
		} else if (this.trackedEntity instanceof LeashKnotEntity) {
			LeashKnotEntity leashKnotEntity = (LeashKnotEntity)this.trackedEntity;
			EntitySpawnS2CPacket entitySpawnS2CPacket3 = new EntitySpawnS2CPacket(this.trackedEntity, 77);
			BlockPos blockPos2 = leashKnotEntity.getTilePos();
			entitySpawnS2CPacket3.setX(MathHelper.floor((float)(blockPos2.getX() * 32)));
			entitySpawnS2CPacket3.setY(MathHelper.floor((float)(blockPos2.getY() * 32)));
			entitySpawnS2CPacket3.setZ(MathHelper.floor((float)(blockPos2.getZ() * 32)));
			return entitySpawnS2CPacket3;
		} else if (this.trackedEntity instanceof ExperienceOrbEntity) {
			return new ExperienceOrbSpawnS2CPacket((ExperienceOrbEntity)this.trackedEntity);
		} else {
			throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
		}
	}

	public void removeTrackingPlayer(ServerPlayerEntity player) {
		if (this.players.contains(player)) {
			this.players.remove(player);
			player.stopTracking(this.trackedEntity);
		}
	}
}
