package net.minecraft.client.render.debug;

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
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillageDebugRenderer implements DebugRenderer.Renderer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final Map<BlockPos, VillageDebugRenderer.PointOfInterest> pointsOfInterest = Maps.newHashMap();
	private final Set<ChunkSectionPos> sections = Sets.newHashSet();
	private final Map<UUID, VillageDebugRenderer.Brain> brains = Maps.newHashMap();
	private UUID targetedEntity;

	public VillageDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void clear() {
		this.pointsOfInterest.clear();
		this.sections.clear();
		this.brains.clear();
		this.targetedEntity = null;
	}

	public void addPointOfInterest(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		this.pointsOfInterest.put(pointOfInterest.pos, pointOfInterest);
	}

	public void removePointOfInterest(BlockPos blockPos) {
		this.pointsOfInterest.remove(blockPos);
	}

	public void setFreeTicketCount(BlockPos blockPos, int i) {
		VillageDebugRenderer.PointOfInterest pointOfInterest = (VillageDebugRenderer.PointOfInterest)this.pointsOfInterest.get(blockPos);
		if (pointOfInterest == null) {
			LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
		} else {
			pointOfInterest.freeTicketCount = i;
		}
	}

	public void addSection(ChunkSectionPos chunkSectionPos) {
		this.sections.add(chunkSectionPos);
	}

	public void removeSection(ChunkSectionPos chunkSectionPos) {
		this.sections.remove(chunkSectionPos);
	}

	public void addBrain(VillageDebugRenderer.Brain brain) {
		this.brains.put(brain.uuid, brain);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		this.method_23135(d, e, f);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
		if (!this.client.player.isSpectator()) {
			this.updateTargetedEntity();
		}
	}

	private void method_23135(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		this.sections.forEach(chunkSectionPos -> {
			if (blockPos.isWithinDistance(chunkSectionPos.getCenterPos(), 60.0)) {
				drawSection(chunkSectionPos);
			}
		});
		this.brains.values().forEach(brain -> {
			if (this.isClose(brain)) {
				this.drawBrain(brain, d, e, f);
			}
		});

		for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
			if (blockPos.isWithinDistance(blockPos2, 30.0)) {
				drawPointOfInterest(blockPos2);
			}
		}

		this.pointsOfInterest.values().forEach(pointOfInterest -> {
			if (blockPos.isWithinDistance(pointOfInterest.pos, 30.0)) {
				this.drawPointOfInterestInfo(pointOfInterest);
			}
		});
		this.getGhostPointsOfInterest().forEach((blockPos2x, list) -> {
			if (blockPos.isWithinDistance(blockPos2x, 30.0)) {
				this.drawGhostPointOfInterest(blockPos2x, list);
			}
		});
	}

	private static void drawSection(ChunkSectionPos chunkSectionPos) {
		float f = 1.0F;
		BlockPos blockPos = chunkSectionPos.getCenterPos();
		BlockPos blockPos2 = blockPos.add(-1.0, -1.0, -1.0);
		BlockPos blockPos3 = blockPos.add(1.0, 1.0, 1.0);
		DebugRenderer.drawBox(blockPos2, blockPos3, 0.2F, 1.0F, 0.2F, 0.15F);
	}

	private static void drawPointOfInterest(BlockPos blockPos) {
		float f = 0.05F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void drawGhostPointOfInterest(BlockPos blockPos, List<String> list) {
		float f = 0.05F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DebugRenderer.drawBox(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
		drawString("" + list, blockPos, 0, -256);
		drawString("Ghost POI", blockPos, 1, -65536);
	}

	private void drawPointOfInterestInfo(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		int i = 0;
		if (this.getVillagerNames(pointOfInterest).size() < 4) {
			drawString("" + this.getVillagerNames(pointOfInterest), pointOfInterest, i, -256);
		} else {
			drawString("" + this.getVillagerNames(pointOfInterest).size() + " ticket holders", pointOfInterest, i, -256);
		}

		drawString("Free tickets: " + pointOfInterest.freeTicketCount, pointOfInterest, ++i, -256);
		drawString(pointOfInterest.field_18932, pointOfInterest, ++i, -1);
	}

	private void drawPath(VillageDebugRenderer.Brain brain, double d, double e, double f) {
		if (brain.path != null) {
			PathfindingDebugRenderer.drawPath(brain.path, 0.5F, false, false, d, e, f);
		}
	}

	private void drawBrain(VillageDebugRenderer.Brain brain, double d, double e, double f) {
		boolean bl = this.isTargeted(brain);
		int i = 0;
		drawString(brain.pos, i, brain.field_19328, -1, 0.03F);
		i++;
		if (bl) {
			drawString(brain.pos, i, brain.profession + " " + brain.xp + "xp", -1, 0.02F);
			i++;
		}

		if (bl && !brain.field_19372.equals("")) {
			drawString(brain.pos, i, brain.field_19372, -98404, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : brain.field_18928) {
				drawString(brain.pos, i, string, -16711681, 0.02F);
				i++;
			}
		}

		if (bl) {
			for (String string2 : brain.field_18927) {
				drawString(brain.pos, i, string2, -16711936, 0.02F);
				i++;
			}
		}

		if (brain.wantsGolem) {
			drawString(brain.pos, i, "Wants Golem", -23296, 0.02F);
			i++;
		}

		if (bl) {
			for (String string3 : brain.field_19375) {
				if (string3.startsWith(brain.field_19328)) {
					drawString(brain.pos, i, string3, -1, 0.02F);
				} else {
					drawString(brain.pos, i, string3, -23296, 0.02F);
				}

				i++;
			}
		}

		if (bl) {
			for (String string4 : Lists.reverse(brain.field_19374)) {
				drawString(brain.pos, i, string4, -3355444, 0.02F);
				i++;
			}
		}

		if (bl) {
			this.drawPath(brain, d, e, f);
		}
	}

	private static void drawString(String string, VillageDebugRenderer.PointOfInterest pointOfInterest, int i, int j) {
		BlockPos blockPos = pointOfInterest.pos;
		drawString(string, blockPos, i, j);
	}

	private static void drawString(String string, BlockPos blockPos, int i, int j) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)blockPos.getX() + 0.5;
		double g = (double)blockPos.getY() + 1.3 + (double)i * 0.2;
		double h = (double)blockPos.getZ() + 0.5;
		DebugRenderer.drawString(string, f, g, h, j, 0.02F, true, 0.0F, true);
	}

	private static void drawString(Position position, int i, String string, int j, float f) {
		double d = 2.4;
		double e = 0.25;
		BlockPos blockPos = new BlockPos(position);
		double g = (double)blockPos.getX() + 0.5;
		double h = position.getY() + 2.4 + (double)i * 0.25;
		double k = (double)blockPos.getZ() + 0.5;
		float l = 0.5F;
		DebugRenderer.drawString(string, g, h, k, j, f, false, 0.5F, true);
	}

	private Set<String> getVillagerNames(VillageDebugRenderer.PointOfInterest pointOfInterest) {
		return (Set<String>)this.getBrains(pointOfInterest.pos).stream().map(NameGenerator::name).collect(Collectors.toSet());
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

	private Collection<UUID> getBrains(BlockPos blockPos) {
		return (Collection<UUID>)this.brains
			.values()
			.stream()
			.filter(brain -> brain.isPointOfInterest(blockPos))
			.map(VillageDebugRenderer.Brain::getUuid)
			.collect(Collectors.toSet());
	}

	private Map<BlockPos, List<String>> getGhostPointsOfInterest() {
		Map<BlockPos, List<String>> map = Maps.newHashMap();

		for (VillageDebugRenderer.Brain brain : this.brains.values()) {
			for (BlockPos blockPos : brain.pointsOfInterest) {
				if (!this.pointsOfInterest.containsKey(blockPos)) {
					List<String> list = (List<String>)map.get(blockPos);
					if (list == null) {
						list = Lists.newArrayList();
						map.put(blockPos, list);
					}

					list.add(brain.field_19328);
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
		public final int field_18924;
		public final String field_19328;
		public final String profession;
		public final int xp;
		public final Position pos;
		public final String field_19372;
		public final Path path;
		public final boolean wantsGolem;
		public final List<String> field_18927 = Lists.newArrayList();
		public final List<String> field_18928 = Lists.newArrayList();
		public final List<String> field_19374 = Lists.newArrayList();
		public final List<String> field_19375 = Lists.newArrayList();
		public final Set<BlockPos> pointsOfInterest = Sets.newHashSet();

		public Brain(UUID uUID, int i, String string, String string2, int j, Position position, String string3, @Nullable Path path, boolean bl) {
			this.uuid = uUID;
			this.field_18924 = i;
			this.field_19328 = string;
			this.profession = string2;
			this.xp = j;
			this.pos = position;
			this.field_19372 = string3;
			this.path = path;
			this.wantsGolem = bl;
		}

		private boolean isPointOfInterest(BlockPos blockPos) {
			return this.pointsOfInterest.stream().anyMatch(blockPos::equals);
		}

		public UUID getUuid() {
			return this.uuid;
		}
	}

	public static class PointOfInterest {
		public final BlockPos pos;
		public String field_18932;
		public int freeTicketCount;

		public PointOfInterest(BlockPos blockPos, String string, int i) {
			this.pos = blockPos;
			this.field_18932 = string;
			this.freeTicketCount = i;
		}
	}
}
