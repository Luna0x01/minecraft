package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DebugRenderer {
	public final PathfindingDebugRenderer pathfindingDebugRenderer = new PathfindingDebugRenderer();
	public final DebugRenderer.Renderer waterDebugRenderer;
	public final DebugRenderer.Renderer chunkBorderDebugRenderer;
	public final DebugRenderer.Renderer heightmapDebugRenderer;
	public final DebugRenderer.Renderer collisionDebugRenderer;
	public final DebugRenderer.Renderer neighborUpdateDebugRenderer;
	public final CaveDebugRenderer caveDebugRenderer;
	public final StructureDebugRenderer structureDebugRenderer;
	public final DebugRenderer.Renderer skyLightDebugRenderer;
	public final DebugRenderer.Renderer worldGenAttemptDebugRenderer;
	public final DebugRenderer.Renderer blockOutlineDebugRenderer;
	public final DebugRenderer.Renderer chunkLoadingDebugRenderer;
	public final VillageDebugRenderer villageDebugRenderer;
	public final BeeDebugRenderer beeDebugRenderer;
	public final RaidCenterDebugRenderer raidCenterDebugRenderer;
	public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
	public final GameTestDebugRenderer gameTestDebugRenderer;
	private boolean showChunkBorder;

	public DebugRenderer(MinecraftClient minecraftClient) {
		this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
		this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
		this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
		this.collisionDebugRenderer = new CollisionDebugRenderer(minecraftClient);
		this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
		this.caveDebugRenderer = new CaveDebugRenderer();
		this.structureDebugRenderer = new StructureDebugRenderer(minecraftClient);
		this.skyLightDebugRenderer = new SkyLightDebugRenderer(minecraftClient);
		this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer();
		this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(minecraftClient);
		this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(minecraftClient);
		this.villageDebugRenderer = new VillageDebugRenderer(minecraftClient);
		this.beeDebugRenderer = new BeeDebugRenderer(minecraftClient);
		this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(minecraftClient);
		this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(minecraftClient);
		this.gameTestDebugRenderer = new GameTestDebugRenderer();
	}

	public void reset() {
		this.pathfindingDebugRenderer.clear();
		this.waterDebugRenderer.clear();
		this.chunkBorderDebugRenderer.clear();
		this.heightmapDebugRenderer.clear();
		this.collisionDebugRenderer.clear();
		this.neighborUpdateDebugRenderer.clear();
		this.caveDebugRenderer.clear();
		this.structureDebugRenderer.clear();
		this.skyLightDebugRenderer.clear();
		this.worldGenAttemptDebugRenderer.clear();
		this.blockOutlineDebugRenderer.clear();
		this.chunkLoadingDebugRenderer.clear();
		this.villageDebugRenderer.clear();
		this.beeDebugRenderer.clear();
		this.raidCenterDebugRenderer.clear();
		this.goalSelectorDebugRenderer.clear();
		this.gameTestDebugRenderer.clear();
	}

	public boolean toggleShowChunkBorder() {
		this.showChunkBorder = !this.showChunkBorder;
		return this.showChunkBorder;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, double d, double e, double f) {
		if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
			this.chunkBorderDebugRenderer.render(matrixStack, immediate, d, e, f);
		}

		this.gameTestDebugRenderer.render(matrixStack, immediate, d, e, f);
	}

	public static Optional<Entity> getTargetedEntity(@Nullable Entity entity, int i) {
		if (entity == null) {
			return Optional.empty();
		} else {
			Vec3d vec3d = entity.getCameraPosVec(1.0F);
			Vec3d vec3d2 = entity.getRotationVec(1.0F).multiply((double)i);
			Vec3d vec3d3 = vec3d.add(vec3d2);
			Box box = entity.getBoundingBox().stretch(vec3d2).expand(1.0);
			int j = i * i;
			Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.collides();
			EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity, vec3d, vec3d3, box, predicate, (double)j);
			if (entityHitResult == null) {
				return Optional.empty();
			} else {
				return vec3d.squaredDistanceTo(entityHitResult.getPos()) > (double)j ? Optional.empty() : Optional.of(entityHitResult.getEntity());
			}
		}
	}

	public static void drawBox(BlockPos blockPos, BlockPos blockPos2, float f, float g, float h, float i) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			Box box = new Box(blockPos, blockPos2).offset(vec3d);
			drawBox(box, f, g, h, i);
		}
	}

	public static void drawBox(BlockPos blockPos, float f, float g, float h, float i, float j) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			Box box = new Box(blockPos).offset(vec3d).expand((double)f);
			drawBox(box, g, h, i, j);
		}
	}

	public static void drawBox(Box box, float f, float g, float h, float i) {
		drawBox(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2, f, g, h, i);
	}

	public static void drawBox(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
		WorldRenderer.drawBox(bufferBuilder, d, e, f, g, h, i, j, k, l, m);
		tessellator.draw();
	}

	public static void drawString(String string, int i, int j, int k, int l) {
		drawString(string, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, l);
	}

	public static void drawString(String string, double d, double e, double f, int i) {
		drawString(string, d, e, f, i, 0.02F);
	}

	public static void drawString(String string, double d, double e, double f, int i, float g) {
		drawString(string, d, e, f, i, g, true, 0.0F, false);
	}

	public static void drawString(String string, double d, double e, double f, int i, float g, boolean bl, float h, boolean bl2) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Camera camera = minecraftClient.gameRenderer.getCamera();
		if (camera.isReady() && minecraftClient.getEntityRenderManager().gameOptions != null) {
			TextRenderer textRenderer = minecraftClient.textRenderer;
			double j = camera.getPos().x;
			double k = camera.getPos().y;
			double l = camera.getPos().z;
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)(d - j), (float)(e - k) + 0.07F, (float)(f - l));
			RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
			RenderSystem.multMatrix(new Matrix4f(camera.getRotation()));
			RenderSystem.scalef(g, -g, g);
			RenderSystem.enableTexture();
			if (bl2) {
				RenderSystem.disableDepthTest();
			} else {
				RenderSystem.enableDepthTest();
			}

			RenderSystem.depthMask(true);
			RenderSystem.scalef(-1.0F, 1.0F, 1.0F);
			float m = bl ? (float)(-textRenderer.getStringWidth(string)) / 2.0F : 0.0F;
			m -= h / g;
			RenderSystem.enableAlphaTest();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			textRenderer.draw(string, m, 0.0F, i, false, Rotation3.identity().getMatrix(), immediate, bl2, 0, 15728880);
			immediate.draw();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableDepthTest();
			RenderSystem.popMatrix();
		}
	}

	public interface Renderer {
		void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f);

		default void clear() {
		}
	}
}
