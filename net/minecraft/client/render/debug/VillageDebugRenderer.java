package net.minecraft.client.render.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillageDebugRenderer implements DebugRenderer.Renderer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final boolean field_32874 = true;
	private static final boolean field_32875 = false;
	private static final boolean field_32876 = false;
	private static final boolean field_32877 = false;
	private static final boolean field_32878 = false;
	private static final boolean field_32879 = false;
	private static final boolean field_32880 = false;
	private static final boolean field_32881 = false;
	private static final boolean field_32882 = true;
	private static final boolean field_32883 = true;
	private static final boolean field_32884 = true;
	private static final boolean field_32885 = true;
	private static final boolean field_32886 = true;
	private static final boolean field_32887 = true;
	private static final boolean field_32888 = true;
	private static final boolean field_32889 = true;
	private static final boolean field_32890 = true;
	private static final boolean field_32891 = true;
	private static final boolean field_32892 = true;
	private static final boolean field_32893 = true;
	private static final int POI_RANGE = 30;
	private static final int BRAIN_RANGE = 30;
	private static final int TARGET_ENTITY_RANGE = 8;
	private static final float DEFAULT_DRAWN_STRING_SIZE = 0.02F;
	private static final int WHITE = -1;
	private static final int YELLOW = -256;
	private static final int AQUA = -16711681;
	private static final int GREEN = -16711936;
	private static final int GRAY = -3355444;
	private static final int PINK = -98404;
	private static final int RED = -65536;
	private static final int ORANGE = -23296;
	private final MinecraftClient client;
	private final Map<BlockPos, VillageDebugRenderer.PointOfInterest> pointsOfInterest = Maps.newHashMap();
	private final Map<UUID, VillageDebugRenderer.Brain> brains = Maps.newHashMap();
	@Nullable
	private UUID targetedEntity;

	public VillageDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void clear() {
		this.pointsOfInterest.clear();
		this.brains.clear();
		this.targetedEntity = null;
	}

	public void addPointOfInterest(VillageDebugRenderer.PointOfInterest poi) {
		this.pointsOfInterest.put(poi.pos, poi);
	}

	public void removePointOfInterest(BlockPos pos) {
		this.pointsOfInterest.remove(pos);
	}

	public void setFreeTicketCount(BlockPos pos, int freeTicketCount) {
		VillageDebugRenderer.PointOfInterest pointOfInterest = (VillageDebugRenderer.PointOfInterest)this.pointsOfInterest.get(pos);
		if (pointOfInterest == null) {
			LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", pos);
		} else {
			pointOfInterest.freeTicketCount = freeTicketCount;
		}
	}

	public void addBrain(VillageDebugRenderer.Brain brain) {
		this.brains.put(brain.uuid, brain);
	}

	public void removeBrain(int entityId) {
		this.brains.values().removeIf(brain -> brain.entityId == entityId);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		this.removeRemovedBrains();
		this.draw(cameraX, cameraY, cameraZ);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		if (!this.client.player.isSpectator()) {
			this.updateTargetedEntity();
		}
	}

	private void removeRemovedBrains() {
		this.brains.entrySet().removeIf(entry -> {
			Entity entity = this.client.world.getEntityById(((VillageDebugRenderer.Brain)entry.getValue()).entityId);
			return entity == null || entity.isRemoved();
		});
	}

	private void draw(double x, double y, double z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		this.brains.values().forEach(brain -> {
			if (this.isClose(brain)) {
				this.drawBrain(brain, x, y, z);
			}
		});

		for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
			if (blockPos.isWithinDistance(blockPos2, 30.0)) {
				drawPointOfInterest(blockPos2);
			}
		}

		this.pointsOfInterest.values().forEach(poi -> {
			if (blockPos.isWithinDistance(poi.pos, 30.0)) {
				this.drawPointOfInterestInfo(poi);
			}
		});
		this.getGhostPointsOfInterest().forEach((pos, brains) -> {
			if (blockPos.isWithinDistance(pos, 30.0)) {
				this.drawGhostPointOfInterest(pos, brains);
			}
		});
	}

	private static void drawPointOfInterest(BlockPos pos) {
		float f = 0.05F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void drawGhostPointOfInterest(BlockPos pos, List<String> brains) {
		float f = 0.05F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(pos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
		drawString(brains + "", pos, 0, -256);
		drawString("Ghost POI", pos, 1, -65536);
	}

	private void drawPointOfInterestInfo(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		int i = 0;
		Set<String> set = this.getNamesOfPointOfInterestTicketHolders(pointOfInterest);
		if (set.size() < 4) {
			drawString("Owners: " + set, pointOfInterest, i, -256);
		} else {
			drawString(set.size() + " ticket holders", pointOfInterest, i, -256);
		}

		i++;
		Set<String> set2 = this.getNamesOfJobSitePotentialOwners(pointOfInterest);
		if (set2.size() < 4) {
			drawString("Candidates: " + set2, pointOfInterest, i, -23296);
		} else {
			drawString(set2.size() + " potential owners", pointOfInterest, i, -23296);
		}

		drawString("Free tickets: " + pointOfInterest.freeTicketCount, pointOfInterest, ++i, -256);
		drawString(pointOfInterest.field_18932, pointOfInterest, ++i, -1);
	}

	private void drawPath(VillageDebugRenderer.Brain brain, double cameraX, double cameraY, double cameraZ) {
		if (brain.path != null) {
			PathfindingDebugRenderer.drawPath(brain.path, 0.5F, false, false, cameraX, cameraY, cameraZ);
		}
	}

	private void drawBrain(VillageDebugRenderer.Brain brain, double cameraX, double cameraY, double cameraZ) {
		boolean bl = this.isTargeted(brain);
		int i = 0;
		drawString(brain.pos, i, brain.name, -1, 0.03F);
		i++;
		if (bl) {
			drawString(brain.pos, i, brain.profession + " " + brain.xp + " xp", -1, 0.02F);
			i++;
		}

		if (bl) {
			int j = brain.health < brain.maxHealth ? -23296 : -1;
			drawString(brain.pos, i, "health: " + String.format("%.1f", brain.health) + " / " + String.format("%.1f", brain.maxHealth), j, 0.02F);
			i++;
		}

		if (bl && !brain.inventory.equals("")) {
			drawString(brain.pos, i, brain.inventory, -98404, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : brain.runningTasks) {
				drawString(brain.pos, i, string, -16711681, 0.02F);
				i++;
			}
		}

		if (bl) {
			for (String string2 : brain.possibleActivities) {
				drawString(brain.pos, i, string2, -16711936, 0.02F);
				i++;
			}
		}

		if (brain.wantsGolem) {
			drawString(brain.pos, i, "Wants Golem", -23296, 0.02F);
			i++;
		}

		if (bl) {
			for (String string3 : brain.gossips) {
				if (string3.startsWith(brain.name)) {
					drawString(brain.pos, i, string3, -1, 0.02F);
				} else {
					drawString(brain.pos, i, string3, -23296, 0.02F);
				}

				i++;
			}
		}

		if (bl) {
			for (String string4 : Lists.reverse(brain.memories)) {
				drawString(brain.pos, i, string4, -3355444, 0.02F);
				i++;
			}
		}

		if (bl) {
			this.drawPath(brain, cameraX, cameraY, cameraZ);
		}
	}

	private static void drawString(String string, VillageDebugRenderer.PointOfInterest pointOfInterest, int offsetY, int color) {
		BlockPos blockPos = pointOfInterest.pos;
		drawString(string, blockPos, offsetY, color);
	}

	private static void drawString(String string, BlockPos pos, int offsetY, int color) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)pos.getX() + 0.5;
		double g = (double)pos.getY() + 1.3 + (double)offsetY * 0.2;
		double h = (double)pos.getZ() + 0.5;
		DebugRenderer.drawString(string, f, g, h, color, 0.02F, true, 0.0F, true);
	}

	private static void drawString(Position pos, int offsetY, String string, int color, float size) {
		double d = 2.4;
		double e = 0.25;
		BlockPos blockPos = new BlockPos(pos);
		double f = (double)blockPos.getX() + 0.5;
		double g = pos.getY() + 2.4 + (double)offsetY * 0.25;
		double h = (double)blockPos.getZ() + 0.5;
		float i = 0.5F;
		DebugRenderer.drawString(string, f, g, h, color, size, false, 0.5F, true);
	}

	private Set<String> getNamesOfPointOfInterestTicketHolders(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		return (Set<String>)this.getBrainsContainingPointOfInterest(pointOfInterest.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
	}

	private Set<String> getNamesOfJobSitePotentialOwners(VillageDebugRenderer.PointOfInterest potentialJobSite) {
		return (Set<String>)this.getBrainsContainingPotentialJobSite(potentialJobSite.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
	}

	private boolean isTargeted(VillageDebugRenderer.Brain brain) {
		return Objects.equals(this.targetedEntity, brain.uuid);
	}

	private boolean isClose(VillageDebugRenderer.Brain brain) {
		PlayerEntity playerEntity = this.client.player;
		BlockPos blockPos = new BlockPos(playerEntity.getX(), brain.pos.getY(), playerEntity.getZ());
		BlockPos blockPos2 = new BlockPos(brain.pos);
		return blockPos.isWithinDistance(blockPos2, 30.0);
	}

	private Collection<UUID> getBrainsContainingPointOfInterest(BlockPos pointOfInterest) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.isPointOfInterest(pointOfInterest))
			.map(VillageDebugRenderer.Brain::getUuid)
			.collect(Collectors.toSet());
	}

	private Collection<UUID> getBrainsContainingPotentialJobSite(BlockPos potentialJobSite) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.isPotentialJobSite(potentialJobSite))
			.map(VillageDebugRenderer.Brain::getUuid)
			.collect(Collectors.toSet());
	}

	private Map<BlockPos, List<String>> getGhostPointsOfInterest() {
		Map<BlockPos, List<String>> map = Maps.newHashMap();

		for (VillageDebugRenderer.Brain brain : this.brains.values()) {
			for (BlockPos blockPos : Iterables.concat(brain.pointsOfInterest, brain.potentialJobSites)) {
				if (!this.pointsOfInterest.containsKey(blockPos)) {
					((List)map.computeIfAbsent(blockPos, blockPosx -> Lists.newArrayList())).add(brain.name);
				}
			}
		}

		return map;
	}

	private void updateTargetedEntity() {
		DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(entity -> this.targetedEntity = entity.getUuid());
	}

	public static class Brain {
		public final UUID uuid;
		public final int entityId;
		public final String name;
		public final String profession;
		public final int xp;
		public final float health;
		public final float maxHealth;
		public final Position pos;
		public final String inventory;
		public final Path path;
		public final boolean wantsGolem;
		public final List<String> possibleActivities = Lists.newArrayList();
		public final List<String> runningTasks = Lists.newArrayList();
		public final List<String> memories = Lists.newArrayList();
		public final List<String> gossips = Lists.newArrayList();
		public final Set<BlockPos> pointsOfInterest = Sets.newHashSet();
		public final Set<BlockPos> potentialJobSites = Sets.newHashSet();

		public Brain(
			UUID uuid,
			int entityId,
			String name,
			String profession,
			int xp,
			float health,
			float maxHealth,
			Position pos,
			String inventory,
			@Nullable Path path,
			boolean wantsGolem
		) {
			this.uuid = uuid;
			this.entityId = entityId;
			this.name = name;
			this.profession = profession;
			this.xp = xp;
			this.health = health;
			this.maxHealth = maxHealth;
			this.pos = pos;
			this.inventory = inventory;
			this.path = path;
			this.wantsGolem = wantsGolem;
		}

		boolean isPointOfInterest(BlockPos pos) {
			return this.pointsOfInterest.stream().anyMatch(pos::equals);
		}

		boolean isPotentialJobSite(BlockPos pos) {
			return this.potentialJobSites.contains(pos);
		}

		public UUID getUuid() {
			return this.uuid;
		}
	}

	public static class PointOfInterest {
		public final BlockPos pos;
		public String field_18932;
		public int freeTicketCount;

		public PointOfInterest(BlockPos pos, String string, int freeTicketCount) {
			this.pos = pos;
			this.field_18932 = string;
			this.freeTicketCount = freeTicketCount;
		}
	}
}
