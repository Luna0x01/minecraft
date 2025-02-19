package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class GameEventDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private static final int field_32899 = 32;
	private static final float field_32900 = 1.0F;
	private final List<GameEventDebugRenderer.Entry> entries = Lists.newArrayList();
	private final List<GameEventDebugRenderer.Listener> listeners = Lists.newArrayList();

	public GameEventDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		World world = this.client.world;
		if (world == null) {
			this.entries.clear();
			this.listeners.clear();
		} else {
			BlockPos blockPos = new BlockPos(cameraX, 0.0, cameraZ);
			this.entries.removeIf(GameEventDebugRenderer.Entry::hasExpired);
			this.listeners.removeIf(listener -> listener.isTooFar(world, blockPos));
			RenderSystem.disableTexture();
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

			for (GameEventDebugRenderer.Listener listener : this.listeners) {
				listener.getPos(world)
					.ifPresent(
						pos -> {
							int ix = pos.getX() - listener.getRange();
							int jx = pos.getY() - listener.getRange();
							int k = pos.getZ() - listener.getRange();
							int l = pos.getX() + listener.getRange();
							int m = pos.getY() + listener.getRange();
							int n = pos.getZ() + listener.getRange();
							Vec3f vec3f = new Vec3f(1.0F, 1.0F, 0.0F);
							WorldRenderer.method_22983(
								matrices,
								vertexConsumer,
								VoxelShapes.cuboid(new Box((double)ix, (double)jx, (double)k, (double)l, (double)m, (double)n)),
								-cameraX,
								-cameraY,
								-cameraZ,
								vec3f.getX(),
								vec3f.getY(),
								vec3f.getZ(),
								0.35F
							);
						}
					);
			}

			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

			for (GameEventDebugRenderer.Listener listener2 : this.listeners) {
				listener2.getPos(world)
					.ifPresent(
						pos -> {
							Vec3f vec3f = new Vec3f(1.0F, 1.0F, 0.0F);
							WorldRenderer.drawBox(
								bufferBuilder,
								(double)((float)pos.getX() - 0.25F) - cameraX,
								(double)pos.getY() - cameraY,
								(double)((float)pos.getZ() - 0.25F) - cameraZ,
								(double)((float)pos.getX() + 0.25F) - cameraX,
								(double)pos.getY() - cameraY + 1.0,
								(double)((float)pos.getZ() + 0.25F) - cameraZ,
								vec3f.getX(),
								vec3f.getY(),
								vec3f.getZ(),
								0.35F
							);
						}
					);
			}

			tessellator.draw();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.lineWidth(2.0F);
			RenderSystem.depthMask(false);

			for (GameEventDebugRenderer.Listener listener3 : this.listeners) {
				listener3.getPos(world).ifPresent(pos -> {
					DebugRenderer.drawString("Listener Origin", (double)pos.getX(), (double)((float)pos.getY() + 1.8F), (double)pos.getZ(), -1, 0.025F);
					DebugRenderer.drawString(new BlockPos(pos).toString(), (double)pos.getX(), (double)((float)pos.getY() + 1.5F), (double)pos.getZ(), -6959665, 0.025F);
				});
			}

			for (GameEventDebugRenderer.Entry entry : this.entries) {
				Vec3d vec3d = entry.pos;
				double d = 0.2F;
				double e = vec3d.x - 0.2F;
				double f = vec3d.y - 0.2F;
				double g = vec3d.z - 0.2F;
				double h = vec3d.x + 0.2F;
				double i = vec3d.y + 0.2F + 0.5;
				double j = vec3d.z + 0.2F;
				drawBoxIfCameraReady(new Box(e, f, g, h, i, j), 1.0F, 1.0F, 1.0F, 0.2F);
				DebugRenderer.drawString(entry.event.getId(), vec3d.x, vec3d.y + 0.85F, vec3d.z, -7564911, 0.0075F);
			}

			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}

	private static void drawBoxIfCameraReady(Box box, float red, float green, float blue, float alpha) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			Vec3d vec3d = camera.getPos().negate();
			DebugRenderer.drawBox(box.offset(vec3d), red, green, blue, alpha);
		}
	}

	public void addEvent(GameEvent event, BlockPos pos) {
		this.entries.add(new GameEventDebugRenderer.Entry(Util.getMeasuringTimeMs(), event, Vec3d.ofBottomCenter(pos)));
	}

	public void addListener(PositionSource positionSource, int range) {
		this.listeners.add(new GameEventDebugRenderer.Listener(positionSource, range));
	}

	static class Entry {
		public final long startingMs;
		public final GameEvent event;
		public final Vec3d pos;

		public Entry(long startingMs, GameEvent event, Vec3d pos) {
			this.startingMs = startingMs;
			this.event = event;
			this.pos = pos;
		}

		public boolean hasExpired() {
			return Util.getMeasuringTimeMs() - this.startingMs > 3000L;
		}
	}

	static class Listener implements GameEventListener {
		public final PositionSource positionSource;
		public final int range;

		public Listener(PositionSource positionSource, int range) {
			this.positionSource = positionSource;
			this.range = range;
		}

		public boolean isTooFar(World world, BlockPos pos) {
			Optional<BlockPos> optional = this.positionSource.getPos(world);
			return !optional.isPresent() || ((BlockPos)optional.get()).getSquaredDistance(pos) <= 1024.0;
		}

		public Optional<BlockPos> getPos(World world) {
			return this.positionSource.getPos(world);
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public int getRange() {
			return this.range;
		}

		@Override
		public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
			return false;
		}
	}
}
