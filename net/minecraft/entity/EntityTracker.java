package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ServerWorld world;
	private Set<TrackedEntityInstance> trackedEntities = Sets.newHashSet();
	private IntObjectStorage<TrackedEntityInstance> trackedEntityIds = new IntObjectStorage<>();
	private int field_2786;

	public EntityTracker(ServerWorld serverWorld) {
		this.world = serverWorld;
		this.field_2786 = serverWorld.getServer().getPlayerManager().method_1978();
	}

	public void startTracking(Entity entity) {
		if (entity instanceof ServerPlayerEntity) {
			this.startTracking(entity, 512, 2);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;

			for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
				if (trackedEntityInstance.trackedEntity != serverPlayerEntity) {
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
			this.startTracking(entity, 64, 10, false);
		} else if (entity instanceof SnowballEntity) {
			this.startTracking(entity, 64, 10, true);
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
		} else if (entity instanceof EndCrystalEntity) {
			this.startTracking(entity, 256, Integer.MAX_VALUE, false);
		}
	}

	public void startTracking(Entity entity, int i, int j) {
		this.startTracking(entity, i, j, false);
	}

	public void startTracking(Entity entity, int i, int j, boolean bl) {
		if (i > this.field_2786) {
			i = this.field_2786;
		}

		try {
			if (this.trackedEntityIds.hasEntry(entity.getEntityId())) {
				throw new IllegalStateException("Entity is already tracked!");
			}

			TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance(entity, i, j, bl);
			this.trackedEntities.add(trackedEntityInstance);
			this.trackedEntityIds.set(entity.getEntityId(), trackedEntityInstance);
			trackedEntityInstance.method_2185(this.world.playerEntities);
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Adding entity to track");
			CrashReportSection crashReportSection = crashReport.addElement("Entity To Track");
			crashReportSection.add("Tracking range", i + " blocks");
			crashReportSection.add("Update interval", new Callable<String>() {
				public String call() throws Exception {
					String string = "Once per " + j + " ticks";
					if (j == Integer.MAX_VALUE) {
						string = "Maximum (" + string + ")";
					}

					return string;
				}
			});
			entity.populateCrashReport(crashReportSection);
			CrashReportSection crashReportSection2 = crashReport.addElement("Entity That Is Already Tracked");
			this.trackedEntityIds.get(entity.getEntityId()).trackedEntity.populateCrashReport(crashReportSection2);

			try {
				throw new CrashException(crashReport);
			} catch (CrashException var10) {
				LOGGER.error("\"Silently\" catching entity tracking error.", var10);
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
			if (trackedEntityInstance.field_2872 && trackedEntityInstance.trackedEntity instanceof ServerPlayerEntity) {
				list.add((ServerPlayerEntity)trackedEntityInstance.trackedEntity);
			}
		}

		for (int i = 0; i < list.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(i);

			for (TrackedEntityInstance trackedEntityInstance2 : this.trackedEntities) {
				if (trackedEntityInstance2.trackedEntity != serverPlayerEntity) {
					trackedEntityInstance2.method_2184(serverPlayerEntity);
				}
			}
		}
	}

	public void method_10747(ServerPlayerEntity serverPlayerEntity) {
		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			if (trackedEntityInstance.trackedEntity == serverPlayerEntity) {
				trackedEntityInstance.method_2185(this.world.playerEntities);
			} else {
				trackedEntityInstance.method_2184(serverPlayerEntity);
			}
		}
	}

	public void sendToOtherTrackingEntities(Entity entity, Packet packet) {
		TrackedEntityInstance trackedEntityInstance = this.trackedEntityIds.get(entity.getEntityId());
		if (trackedEntityInstance != null) {
			trackedEntityInstance.method_2179(packet);
		}
	}

	public void sendToAllTrackingEntities(Entity entity, Packet packet) {
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
		for (TrackedEntityInstance trackedEntityInstance : this.trackedEntities) {
			if (trackedEntityInstance.trackedEntity != playerEntity
				&& trackedEntityInstance.trackedEntity.chunkX == chunk.chunkX
				&& trackedEntityInstance.trackedEntity.chunkZ == chunk.chunkZ) {
				trackedEntityInstance.method_2184(playerEntity);
			}
		}
	}
}
