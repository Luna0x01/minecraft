package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPassengersS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerWorld world;
	private final Set<TrackedEntityInstance> trackedEntities = Sets.newHashSet();
	private final IntObjectStorage<TrackedEntityInstance> trackedEntityIds = new IntObjectStorage<>();
	private int field_2786;

	public EntityTracker(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.field_2786 = serverWorld.getServer().getPlayerManager().method_1978();
	}

	public static long method_12764(double d) {
		return MathHelper.lfloor(d * 4096.0);
	}

	public static void method_12766(Entity entity, double d, double e, double f) {
		entity.tracedX = method_12764(d);
		entity.tracedY = method_12764(e);
		entity.tracedZ = method_12764(f);
	}

	public void startTracking(Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			this.startTracking(entity, 512, 2);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;

			for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
				if (trackedEntityInstance.method_12794() != serverPlayerEntity) {
					trackedEntityInstance.method_2184(serverPlayerEntity);
				}
			}
		} else if (entity instanceof FishingBobberEntity) {
			this.startTracking(entity, 64, 5, true);
		} else if (entity instanceof AbstractArrowEntity) {
			this.startTracking(entity, 64, 20, false);
		} else if (entity instanceof SmallFireballEntity) {
			this.startTracking(entity, 64, 10, false);
		} else if (entity instanceof ExplosiveProjectileEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof SnowballEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof LlamaSpitEntity) {
			this.startTracking(entity, 64, 10, false);
		} else if (entity instanceof EnderPearlEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof EyeOfEnderEntity) {
			this.startTracking(entity, 64, 4, true);
		} else if (entity instanceof EggEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof PotionEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof ExperienceBottleEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof FireworkRocketEntity) {
			this.startTracking(entity, 64, 10, true);
		} else if (entity instanceof ItemEntity) {
			this.startTracking(entity, 64, 20, true);
		} else if (entity instanceof AbstractMinecartEntity) {
			this.startTracking(entity, 80, 3, true);
		} else if (entity instanceof BoatEntity) {
			this.startTracking(entity, 80, 3, true);
		} else if (entity instanceof SquidEntity) {
			this.startTracking(entity, 64, 3, true);
		} else if (entity instanceof WitherEntity) {
			this.startTracking(entity, 80, 3, false);
		} else if (entity instanceof ShulkerBulletEntity) {
			this.startTracking(entity, 80, 3, true);
		} else if (entity instanceof BatEntity) {
			this.startTracking(entity, 80, 3, false);
		} else if (entity instanceof EnderDragonEntity) {
			this.startTracking(entity, 160, 3, true);
		} else if (entity instanceof EntityCategoryProvider) {
			this.startTracking(entity, 80, 3, true);
		} else if (entity instanceof TntEntity) {
			this.startTracking(entity, 160, 10, true);
		} else if (entity instanceof FallingBlockEntity) {
			this.startTracking(entity, 160, 20, true);
		} else if (entity instanceof AbstractDecorationEntity) {
			this.startTracking(entity, 160, Integer.MAX_VALUE, false);
		} else if (entity instanceof ArmorStandEntity) {
			this.startTracking(entity, 160, 3, true);
		} else if (entity instanceof ExperienceOrbEntity) {
			this.startTracking(entity, 160, 20, true);
		} else if (entity instanceof AreaEffectCloudEntity) {
			this.startTracking(entity, 160, Integer.MAX_VALUE, true);
		} else if (entity instanceof EndCrystalEntity) {
			this.startTracking(entity, 256, Integer.MAX_VALUE, false);
		} else if (entity instanceof EvokerFangsEntity) {
			this.startTracking(entity, 160, 2, false);
		}
	}

	public void startTracking(Entity entity, int i, int j) {
		this.startTracking(entity, i, j, false);
	}

	public void startTracking(Entity entity, int i, int j, boolean bl) {
		try {
			if (this.trackedEntityIds.hasEntry(entity.getEntityId())) {
				throw new IllegalStateException("Entity is already tracked!");
			}

			TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance(entity, i, this.field_2786, j, bl);
			this.trackedEntities.add(trackedEntityInstance);
			this.trackedEntityIds.set(entity.getEntityId(), trackedEntityInstance);
			trackedEntityInstance.method_2185(this.world.playerEntities);
		} catch (Throwable var10) {
			CrashReport crashReport = CrashReport.create(var10, "Adding entity to track");
			CrashReportSection crashReportSection = crashReport.addElement("Entity To Track");
			crashReportSection.add("Tracking range", i + " blocks");
			crashReportSection.add("Update interval", (CrashCallable<String>)(() -> {
				String string = "Once per " + j + " ticks";
				if (j == Integer.MAX_VALUE) {
					string = "Maximum (" + string + ")";
				}

				return string;
			}));
			entity.populateCrashReport(crashReportSection);
			this.trackedEntityIds.get(entity.getEntityId()).method_12794().populateCrashReport(crashReport.addElement("Entity That Is Already Tracked"));

			try {
				throw new CrashException(crashReport);
			} catch (CrashException var9) {
				LOGGER.error("\"Silently\" catching entity tracking error.", var9);
			}
		}
	}

	public void method_2101(Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;

			for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
				trackedEntityInstance.method_2180(serverPlayerEntity);
			}
		}

		TrackedEntityInstance trackedEntityInstance2 = this.trackedEntityIds.remove(entity.getEntityId());
		if (trackedEntityInstance2 != null) {
			this.trackedEntities.remove(trackedEntityInstance2);
			trackedEntityInstance2.method_2178();
		}
	}

	public void method_2095() {
		List<ServerPlayerEntity> list = Lists.newArrayList();

		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			trackedEntityInstance.method_2181(this.world.playerEntities);
			if (trackedEntityInstance.field_2872) {
				Entity entity = trackedEntityInstance.method_12794();
				if (entity instanceof ServerPlayerEntity) {
					list.add((ServerPlayerEntity)entity);
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(i);

			for (TrackedEntityInstance trackedEntityInstance2 : this.trackedEntities) {
				if (trackedEntityInstance2.method_12794() != serverPlayerEntity) {
					trackedEntityInstance2.method_2184(serverPlayerEntity);
				}
			}
		}
	}

	public void method_10747(ServerPlayerEntity serverPlayerEntity) {
		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			if (trackedEntityInstance.method_12794() == serverPlayerEntity) {
				trackedEntityInstance.method_2185(this.world.playerEntities);
			} else {
				trackedEntityInstance.method_2184(serverPlayerEntity);
			}
		}
	}

	public void sendToOtherTrackingEntities(Entity entity, Packet<?> packet) {
		TrackedEntityInstance trackedEntityInstance = this.trackedEntityIds.get(entity.getEntityId());
		if (trackedEntityInstance != null) {
			trackedEntityInstance.method_2179(packet);
		}
	}

	public void sendToAllTrackingEntities(Entity entity, Packet<?> packet) {
		TrackedEntityInstance trackedEntityInstance = this.trackedEntityIds.get(entity.getEntityId());
		if (trackedEntityInstance != null) {
			trackedEntityInstance.method_2183(packet);
		}
	}

	public void method_2096(ServerPlayerEntity playerEntity) {
		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			trackedEntityInstance.removeTrackingPlayer(playerEntity);
		}
	}

	public void method_4410(ServerPlayerEntity playerEntity, Chunk chunk) {
		List<Entity> list = Lists.newArrayList();
		List<Entity> list2 = Lists.newArrayList();

		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			Entity entity = trackedEntityInstance.method_12794();
			if (entity != playerEntity && entity.chunkX == chunk.chunkX && entity.chunkZ == chunk.chunkZ) {
				trackedEntityInstance.method_2184(playerEntity);
				if (entity instanceof MobEntity && ((MobEntity)entity).getLeashOwner() != null) {
					list.add(entity);
				}

				if (!entity.getPassengerList().isEmpty()) {
					list2.add(entity);
				}
			}
		}

		if (!list.isEmpty()) {
			for (Entity entity2 : list) {
				playerEntity.networkHandler.sendPacket(new EntityAttachS2CPacket(entity2, ((MobEntity)entity2).getLeashOwner()));
			}
		}

		if (!list2.isEmpty()) {
			for (Entity entity3 : list2) {
				playerEntity.networkHandler.sendPacket(new SetPassengersS2CPacket(entity3));
			}
		}
	}

	public void method_12765(int i) {
		this.field_2786 = (i - 1) * 16;

		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			trackedEntityInstance.method_12793(this.field_2786);
		}
	}
}
