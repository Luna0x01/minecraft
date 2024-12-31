package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.world.AbstractChunkRenderManager;
import net.minecraft.client.render.world.ChunkRenderFactory;
import net.minecraft.client.render.world.ListChunkRenderFactoryImpl;
import net.minecraft.client.render.world.ListedChunkRenderManager;
import net.minecraft.client.render.world.VboChunkRenderFactoryImpl;
import net.minecraft.client.render.world.VboChunkRenderManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.client.world.ChunkAssemblyHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEventListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class WorldRenderer implements WorldEventListener, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
	private static final Identifier SUN = new Identifier("textures/environment/sun.png");
	private static final Identifier CLOUDS = new Identifier("textures/environment/clouds.png");
	private static final Identifier END_SKY = new Identifier("textures/environment/end_sky.png");
	private static final Identifier FORCEFIELD = new Identifier("textures/misc/forcefield.png");
	private final MinecraftClient client;
	private final TextureManager textureManager;
	private final EntityRenderDispatcher entityRenderDispatcher;
	private ClientWorld world;
	private Set<BuiltChunk> chunksToRebuild = Sets.newLinkedHashSet();
	private List<WorldRenderer.ChunkInfo> visibleChunks = Lists.newArrayListWithCapacity(69696);
	private final Set<BlockEntity> noCullingBlockEntities = Sets.newHashSet();
	private BuiltChunkStorage chunks;
	private int starsList = -1;
	private int lightSkyList = -1;
	private int darkSkyList = -1;
	private VertexFormat skyVertexFormat;
	private VertexBuffer starsBuffer;
	private VertexBuffer lightSkyBuffer;
	private VertexBuffer darkSkyBuffer;
	private int ticks;
	private final Map<Integer, BlockBreakingInfo> blockBreakingInfos = Maps.newHashMap();
	private final Map<BlockPos, SoundInstance> playingSongs = Maps.newHashMap();
	private final Sprite[] destroySprites = new Sprite[10];
	private Framebuffer entityOutlineFramebuffer;
	private ShaderEffect entityOutlineShader;
	private double lastCameraChunkUpdateX = Double.MIN_VALUE;
	private double lastCameraChunkUpdateY = Double.MIN_VALUE;
	private double lastCameraChunkUpdateZ = Double.MIN_VALUE;
	private int cameraChunkX = Integer.MIN_VALUE;
	private int cameraChunkY = Integer.MIN_VALUE;
	private int cameraChunkZ = Integer.MIN_VALUE;
	private double lastCameraX = Double.MIN_VALUE;
	private double lastCameraY = Double.MIN_VALUE;
	private double lastCameraZ = Double.MIN_VALUE;
	private double lastCameraPitch = Double.MIN_VALUE;
	private double lastCameraYaw = Double.MIN_VALUE;
	private final ChunkBuilder chunkBuilder = new ChunkBuilder();
	private AbstractChunkRenderManager chunkRenderManager;
	private int renderDistance = -1;
	private int totalEntityCount = 2;
	private int renderedEntityCount;
	private int hiddenEntityCount;
	private int blockEntityCount;
	private boolean field_10813 = false;
	private BaseFrustum capturedFrustum;
	private final Vector4f[] capturedFrustumOrientation = new Vector4f[8];
	private final Vector3d capturedFrustumPosition = new Vector3d();
	private boolean vbo = false;
	ChunkRenderFactory chunkRenderFactory;
	private double lastTranslucentSortX;
	private double lastTranslucentSortY;
	private double lastTranslucentSortZ;
	private boolean needsTerrainUpdate = true;

	public WorldRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.entityRenderDispatcher = minecraftClient.getEntityRenderManager();
		this.textureManager = minecraftClient.getTextureManager();
		this.textureManager.bindTexture(FORCEFIELD);
		GL11.glTexParameteri(3553, 10242, 10497);
		GL11.glTexParameteri(3553, 10243, 10497);
		GlStateManager.bindTexture(0);
		this.fillDestroySprites();
		this.vbo = GLX.supportsVbo();
		if (this.vbo) {
			this.chunkRenderManager = new VboChunkRenderManager();
			this.chunkRenderFactory = new VboChunkRenderFactoryImpl();
		} else {
			this.chunkRenderManager = new ListedChunkRenderManager();
			this.chunkRenderFactory = new ListChunkRenderFactoryImpl();
		}

		this.skyVertexFormat = new VertexFormat();
		this.skyVertexFormat.addElement(new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3));
		this.renderStars();
		this.renderLightSky();
		this.renderDarkSky();
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.fillDestroySprites();
	}

	private void fillDestroySprites() {
		SpriteAtlasTexture spriteAtlasTexture = this.client.getSpriteAtlasTexture();

		for (int i = 0; i < this.destroySprites.length; i++) {
			this.destroySprites[i] = spriteAtlasTexture.getSprite("minecraft:blocks/destroy_stage_" + i);
		}
	}

	public void setupEntityOutlineShader() {
		if (GLX.shadersSupported) {
			if (GlProgramManager.getInstance() == null) {
				GlProgramManager.newInstance();
			}

			Identifier identifier = new Identifier("shaders/post/entity_outline.json");

			try {
				this.entityOutlineShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier);
				this.entityOutlineShader.setupDimensions(this.client.width, this.client.height);
				this.entityOutlineFramebuffer = this.entityOutlineShader.getSecondaryTarget("final");
			} catch (IOException var3) {
				LOGGER.warn("Failed to load shader: " + identifier, var3);
				this.entityOutlineShader = null;
				this.entityOutlineFramebuffer = null;
			} catch (JsonSyntaxException var4) {
				LOGGER.warn("Failed to load shader: " + identifier, var4);
				this.entityOutlineShader = null;
				this.entityOutlineFramebuffer = null;
			}
		} else {
			this.entityOutlineShader = null;
			this.entityOutlineFramebuffer = null;
		}
	}

	public void drawEntityOutlineFramebuffer() {
		if (this.isEntityOutline()) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 0, 1);
			this.entityOutlineFramebuffer.drawInternal(this.client.width, this.client.height, false);
			GlStateManager.disableBlend();
		}
	}

	protected boolean isEntityOutline() {
		return this.entityOutlineFramebuffer != null
			&& this.entityOutlineShader != null
			&& this.client.player != null
			&& this.client.player.isSpectator()
			&& this.client.options.spectatorOutlines.isPressed();
	}

	private void renderDarkSky() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		if (this.darkSkyBuffer != null) {
			this.darkSkyBuffer.delete();
		}

		if (this.darkSkyList >= 0) {
			GlAllocationUtils.deleteSingletonList(this.darkSkyList);
			this.darkSkyList = -1;
		}

		if (this.vbo) {
			this.darkSkyBuffer = new VertexBuffer(this.skyVertexFormat);
			this.renderSkyHalf(bufferBuilder, -16.0F, true);
			bufferBuilder.end();
			bufferBuilder.reset();
			this.darkSkyBuffer.data(bufferBuilder.getByteBuffer());
		} else {
			this.darkSkyList = GlAllocationUtils.genLists(1);
			GL11.glNewList(this.darkSkyList, 4864);
			this.renderSkyHalf(bufferBuilder, -16.0F, true);
			tessellator.draw();
			GL11.glEndList();
		}
	}

	private void renderLightSky() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		if (this.lightSkyBuffer != null) {
			this.lightSkyBuffer.delete();
		}

		if (this.lightSkyList >= 0) {
			GlAllocationUtils.deleteSingletonList(this.lightSkyList);
			this.lightSkyList = -1;
		}

		if (this.vbo) {
			this.lightSkyBuffer = new VertexBuffer(this.skyVertexFormat);
			this.renderSkyHalf(bufferBuilder, 16.0F, false);
			bufferBuilder.end();
			bufferBuilder.reset();
			this.lightSkyBuffer.data(bufferBuilder.getByteBuffer());
		} else {
			this.lightSkyList = GlAllocationUtils.genLists(1);
			GL11.glNewList(this.lightSkyList, 4864);
			this.renderSkyHalf(bufferBuilder, 16.0F, false);
			tessellator.draw();
			GL11.glEndList();
		}
	}

	private void renderSkyHalf(BufferBuilder buffer, float y, boolean bottom) {
		int i = 64;
		int j = 6;
		buffer.begin(7, VertexFormats.POSITION);

		for (int k = -384; k <= 384; k += 64) {
			for (int l = -384; l <= 384; l += 64) {
				float f = (float)k;
				float g = (float)(k + 64);
				if (bottom) {
					g = (float)k;
					f = (float)(k + 64);
				}

				buffer.vertex((double)f, (double)y, (double)l).next();
				buffer.vertex((double)g, (double)y, (double)l).next();
				buffer.vertex((double)g, (double)y, (double)(l + 64)).next();
				buffer.vertex((double)f, (double)y, (double)(l + 64)).next();
			}
		}
	}

	private void renderStars() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		if (this.starsBuffer != null) {
			this.starsBuffer.delete();
		}

		if (this.starsList >= 0) {
			GlAllocationUtils.deleteSingletonList(this.starsList);
			this.starsList = -1;
		}

		if (this.vbo) {
			this.starsBuffer = new VertexBuffer(this.skyVertexFormat);
			this.renderStars(bufferBuilder);
			bufferBuilder.end();
			bufferBuilder.reset();
			this.starsBuffer.data(bufferBuilder.getByteBuffer());
		} else {
			this.starsList = GlAllocationUtils.genLists(1);
			GlStateManager.pushMatrix();
			GL11.glNewList(this.starsList, 4864);
			this.renderStars(bufferBuilder);
			tessellator.draw();
			GL11.glEndList();
			GlStateManager.popMatrix();
		}
	}

	private void renderStars(BufferBuilder buffer) {
		Random random = new Random(10842L);
		buffer.begin(7, VertexFormats.POSITION);

		for (int i = 0; i < 1500; i++) {
			double d = (double)(random.nextFloat() * 2.0F - 1.0F);
			double e = (double)(random.nextFloat() * 2.0F - 1.0F);
			double f = (double)(random.nextFloat() * 2.0F - 1.0F);
			double g = (double)(0.15F + random.nextFloat() * 0.1F);
			double h = d * d + e * e + f * f;
			if (h < 1.0 && h > 0.01) {
				h = 1.0 / Math.sqrt(h);
				d *= h;
				e *= h;
				f *= h;
				double j = d * 100.0;
				double k = e * 100.0;
				double l = f * 100.0;
				double m = Math.atan2(d, f);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(d * d + f * f), e);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				for (int v = 0; v < 4; v++) {
					double w = 0.0;
					double x = (double)((v & 2) - 1) * g;
					double y = (double)((v + 1 & 2) - 1) * g;
					double z = 0.0;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					buffer.vertex(j + af, k + ad, l + ah).next();
				}
			}
		}
	}

	public void setWorld(ClientWorld world) {
		if (this.world != null) {
			this.world.removeListener(this);
		}

		this.lastCameraChunkUpdateX = Double.MIN_VALUE;
		this.lastCameraChunkUpdateY = Double.MIN_VALUE;
		this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
		this.cameraChunkX = Integer.MIN_VALUE;
		this.cameraChunkY = Integer.MIN_VALUE;
		this.cameraChunkZ = Integer.MIN_VALUE;
		this.entityRenderDispatcher.setWorld(world);
		this.world = world;
		if (world != null) {
			world.addListener(this);
			this.reload();
		}
	}

	public void reload() {
		if (this.world != null) {
			this.needsTerrainUpdate = true;
			Blocks.LEAVES.setGraphics(this.client.options.fancyGraphics);
			Blocks.LEAVES2.setGraphics(this.client.options.fancyGraphics);
			this.renderDistance = this.client.options.viewDistance;
			boolean bl = this.vbo;
			this.vbo = GLX.supportsVbo();
			if (bl && !this.vbo) {
				this.chunkRenderManager = new ListedChunkRenderManager();
				this.chunkRenderFactory = new ListChunkRenderFactoryImpl();
			} else if (!bl && this.vbo) {
				this.chunkRenderManager = new VboChunkRenderManager();
				this.chunkRenderFactory = new VboChunkRenderFactoryImpl();
			}

			if (bl != this.vbo) {
				this.renderStars();
				this.renderLightSky();
				this.renderDarkSky();
			}

			if (this.chunks != null) {
				this.chunks.clear();
			}

			this.clearChunkRenderers();
			synchronized (this.noCullingBlockEntities) {
				this.noCullingBlockEntities.clear();
			}

			this.chunks = new BuiltChunkStorage(this.world, this.client.options.viewDistance, this, this.chunkRenderFactory);
			if (this.world != null) {
				Entity entity = this.client.getCameraEntity();
				if (entity != null) {
					this.chunks.updateCameraPosition(entity.x, entity.z);
				}
			}

			this.totalEntityCount = 2;
		}
	}

	protected void clearChunkRenderers() {
		this.chunksToRebuild.clear();
		this.chunkBuilder.stop();
	}

	public void onResized(int width, int height) {
		if (GLX.shadersSupported) {
			if (this.entityOutlineShader != null) {
				this.entityOutlineShader.setupDimensions(width, height);
			}
		}
	}

	public void renderEntities(Entity entity, CameraView cameraView, float tickDelta) {
		if (this.totalEntityCount > 0) {
			this.totalEntityCount--;
		} else {
			double d = entity.prevX + (entity.x - entity.prevX) * (double)tickDelta;
			double e = entity.prevY + (entity.y - entity.prevY) * (double)tickDelta;
			double f = entity.prevZ + (entity.z - entity.prevZ) * (double)tickDelta;
			this.world.profiler.push("prepare");
			BlockEntityRenderDispatcher.INSTANCE
				.updateCamera(this.world, this.client.getTextureManager(), this.client.textRenderer, this.client.getCameraEntity(), tickDelta);
			this.entityRenderDispatcher
				.updateCamera(this.world, this.client.textRenderer, this.client.getCameraEntity(), this.client.targetedEntity, this.client.options, tickDelta);
			this.renderedEntityCount = 0;
			this.hiddenEntityCount = 0;
			this.blockEntityCount = 0;
			Entity entity2 = this.client.getCameraEntity();
			double g = entity2.prevTickX + (entity2.x - entity2.prevTickX) * (double)tickDelta;
			double h = entity2.prevTickY + (entity2.y - entity2.prevTickY) * (double)tickDelta;
			double i = entity2.prevTickZ + (entity2.z - entity2.prevTickZ) * (double)tickDelta;
			BlockEntityRenderDispatcher.CAMERA_X = g;
			BlockEntityRenderDispatcher.CAMERA_Y = h;
			BlockEntityRenderDispatcher.CAMERA_Z = i;
			this.entityRenderDispatcher.updateCamera(g, h, i);
			this.client.gameRenderer.enableLightmap();
			this.world.profiler.swap("global");
			List<Entity> list = this.world.getLoadedEntities();
			this.renderedEntityCount = list.size();

			for (int j = 0; j < this.world.entities.size(); j++) {
				Entity entity3 = (Entity)this.world.entities.get(j);
				this.hiddenEntityCount++;
				if (entity3.shouldRender(d, e, f)) {
					this.entityRenderDispatcher.renderEntity(entity3, tickDelta);
				}
			}

			if (this.isEntityOutline()) {
				GlStateManager.depthFunc(519);
				GlStateManager.disableFog();
				this.entityOutlineFramebuffer.clear();
				this.entityOutlineFramebuffer.bind(false);
				this.world.profiler.swap("entityOutlines");
				DiffuseLighting.disable();
				this.entityRenderDispatcher.method_10206(true);

				for (int k = 0; k < list.size(); k++) {
					Entity entity4 = (Entity)list.get(k);
					boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
					boolean bl2 = entity4.shouldRender(d, e, f)
						&& (entity4.ignoreCameraFrustum || cameraView.isBoxInFrustum(entity4.getBoundingBox()) || entity4.rider == this.client.player)
						&& entity4 instanceof PlayerEntity;
					if ((entity4 != this.client.getCameraEntity() || this.client.options.perspective != 0 || bl) && bl2) {
						this.entityRenderDispatcher.renderEntity(entity4, tickDelta);
					}
				}

				this.entityRenderDispatcher.method_10206(false);
				DiffuseLighting.enableNormally();
				GlStateManager.depthMask(false);
				this.entityOutlineShader.render(tickDelta);
				GlStateManager.enableLighting();
				GlStateManager.depthMask(true);
				this.client.getFramebuffer().bind(false);
				GlStateManager.enableFog();
				GlStateManager.enableBlend();
				GlStateManager.enableColorMaterial();
				GlStateManager.depthFunc(515);
				GlStateManager.enableDepthTest();
				GlStateManager.enableAlphaTest();
			}

			this.world.profiler.swap("entities");

			for (WorldRenderer.ChunkInfo chunkInfo : this.visibleChunks) {
				Chunk chunk = this.world.getChunk(chunkInfo.chunk.getPos());
				TypeFilterableList<Entity> typeFilterableList = chunk.getEntities()[chunkInfo.chunk.getPos().getY() / 16];
				if (!typeFilterableList.isEmpty()) {
					for (Entity entity5 : typeFilterableList) {
						boolean bl3 = this.entityRenderDispatcher.shouldRender(entity5, cameraView, d, e, f) || entity5.rider == this.client.player;
						if (bl3) {
							boolean bl4 = this.client.getCameraEntity() instanceof LivingEntity ? ((LivingEntity)this.client.getCameraEntity()).isSleeping() : false;
							if (entity5 == this.client.getCameraEntity() && this.client.options.perspective == 0 && !bl4
								|| entity5.y >= 0.0 && entity5.y < 256.0 && !this.world.blockExists(new BlockPos(entity5))) {
								continue;
							}

							this.hiddenEntityCount++;
							this.entityRenderDispatcher.renderEntity(entity5, tickDelta);
						}

						if (!bl3 && entity5 instanceof WitherSkullEntity) {
							this.client.getEntityRenderManager().method_10204(entity5, tickDelta);
						}
					}
				}
			}

			this.world.profiler.swap("blockentities");
			DiffuseLighting.enableNormally();

			for (WorldRenderer.ChunkInfo chunkInfo2 : this.visibleChunks) {
				List<BlockEntity> list2 = chunkInfo2.chunk.method_10170().getBlockEntities();
				if (!list2.isEmpty()) {
					for (BlockEntity blockEntity : list2) {
						BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity, tickDelta, -1);
					}
				}
			}

			synchronized (this.noCullingBlockEntities) {
				for (BlockEntity blockEntity2 : this.noCullingBlockEntities) {
					BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity2, tickDelta, -1);
				}
			}

			this.preDrawBlockDamage();

			for (BlockBreakingInfo blockBreakingInfo : this.blockBreakingInfos.values()) {
				BlockPos blockPos = blockBreakingInfo.getPos();
				BlockEntity blockEntity3 = this.world.getBlockEntity(blockPos);
				if (blockEntity3 instanceof ChestBlockEntity) {
					ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity3;
					if (chestBlockEntity.neighborChestWest != null) {
						blockPos = blockPos.offset(Direction.WEST);
						blockEntity3 = this.world.getBlockEntity(blockPos);
					} else if (chestBlockEntity.neighborChestNorth != null) {
						blockPos = blockPos.offset(Direction.NORTH);
						blockEntity3 = this.world.getBlockEntity(blockPos);
					}
				}

				Block block = this.world.getBlockState(blockPos).getBlock();
				if (blockEntity3 != null
					&& (block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof AbstractSignBlock || block instanceof SkullBlock)) {
					BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity3, tickDelta, blockBreakingInfo.getStage());
				}
			}

			this.postDrawBlockDamage();
			this.client.gameRenderer.disableLightmap();
			this.client.profiler.pop();
		}
	}

	public String getChunksDebugString() {
		int i = this.chunks.chunks.length;
		int j = 0;

		for (WorldRenderer.ChunkInfo chunkInfo : this.visibleChunks) {
			ChunkAssemblyHelper chunkAssemblyHelper = chunkInfo.chunk.field_11070;
			if (chunkAssemblyHelper != ChunkAssemblyHelper.UNSUPPORTED && !chunkAssemblyHelper.method_10142()) {
				j++;
			}
		}

		return String.format("C: %d/%d %sD: %d, %s", j, i, this.client.chunkCullingEnabled ? "(s) " : "", this.renderDistance, this.chunkBuilder.getDebugString());
	}

	public String getEntitiesDebugString() {
		return "E: "
			+ this.hiddenEntityCount
			+ "/"
			+ this.renderedEntityCount
			+ ", B: "
			+ this.blockEntityCount
			+ ", I: "
			+ (this.renderedEntityCount - this.blockEntityCount - this.hiddenEntityCount);
	}

	public void setupTerrain(Entity entity, double tickDelta, CameraView cameraView, int frame, boolean spectator) {
		if (this.client.options.viewDistance != this.renderDistance) {
			this.reload();
		}

		this.world.profiler.push("camera");
		double d = entity.x - this.lastCameraChunkUpdateX;
		double e = entity.y - this.lastCameraChunkUpdateY;
		double f = entity.z - this.lastCameraChunkUpdateZ;
		if (this.cameraChunkX != entity.chunkX || this.cameraChunkY != entity.chunkY || this.cameraChunkZ != entity.chunkZ || d * d + e * e + f * f > 16.0) {
			this.lastCameraChunkUpdateX = entity.x;
			this.lastCameraChunkUpdateY = entity.y;
			this.lastCameraChunkUpdateZ = entity.z;
			this.cameraChunkX = entity.chunkX;
			this.cameraChunkY = entity.chunkY;
			this.cameraChunkZ = entity.chunkZ;
			this.chunks.updateCameraPosition(entity.x, entity.z);
		}

		this.world.profiler.swap("renderlistcamera");
		double g = entity.prevTickX + (entity.x - entity.prevTickX) * tickDelta;
		double h = entity.prevTickY + (entity.y - entity.prevTickY) * tickDelta;
		double i = entity.prevTickZ + (entity.z - entity.prevTickZ) * tickDelta;
		this.chunkRenderManager.setViewPos(g, h, i);
		this.world.profiler.swap("cull");
		if (this.capturedFrustum != null) {
			CullingCameraView cullingCameraView = new CullingCameraView(this.capturedFrustum);
			cullingCameraView.setPos(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
			cameraView = cullingCameraView;
		}

		this.client.profiler.swap("culling");
		BlockPos blockPos = new BlockPos(g, h + (double)entity.getEyeHeight(), i);
		BuiltChunk builtChunk = this.chunks.getRenderedChunk(blockPos);
		BlockPos blockPos2 = new BlockPos(MathHelper.floor(g / 16.0) * 16, MathHelper.floor(h / 16.0) * 16, MathHelper.floor(i / 16.0) * 16);
		this.needsTerrainUpdate = this.needsTerrainUpdate
			|| !this.chunksToRebuild.isEmpty()
			|| entity.x != this.lastCameraX
			|| entity.y != this.lastCameraY
			|| entity.z != this.lastCameraZ
			|| (double)entity.pitch != this.lastCameraPitch
			|| (double)entity.yaw != this.lastCameraYaw;
		this.lastCameraX = entity.x;
		this.lastCameraY = entity.y;
		this.lastCameraZ = entity.z;
		this.lastCameraPitch = (double)entity.pitch;
		this.lastCameraYaw = (double)entity.yaw;
		boolean bl = this.capturedFrustum != null;
		if (!bl && this.needsTerrainUpdate) {
			this.needsTerrainUpdate = false;
			this.visibleChunks = Lists.newArrayList();
			Queue<WorldRenderer.ChunkInfo> queue = Lists.newLinkedList();
			boolean bl2 = this.client.chunkCullingEnabled;
			if (builtChunk != null) {
				boolean bl3 = false;
				WorldRenderer.ChunkInfo chunkInfo = new WorldRenderer.ChunkInfo(builtChunk, null, 0);
				Set<Direction> set = this.getOpenChunkFaces(blockPos);
				if (set.size() == 1) {
					Vector3f vector3f = this.getFacing(entity, tickDelta);
					Direction direction = Direction.getFacing(vector3f.x, vector3f.y, vector3f.z).getOpposite();
					set.remove(direction);
				}

				if (set.isEmpty()) {
					bl3 = true;
				}

				if (bl3 && !spectator) {
					this.visibleChunks.add(chunkInfo);
				} else {
					if (spectator && this.world.getBlockState(blockPos).getBlock().hasTransparency()) {
						bl2 = false;
					}

					builtChunk.method_10156(frame);
					queue.add(chunkInfo);
				}
			} else {
				int j = blockPos.getY() > 0 ? 248 : 8;

				for (int k = -this.renderDistance; k <= this.renderDistance; k++) {
					for (int l = -this.renderDistance; l <= this.renderDistance; l++) {
						BuiltChunk builtChunk2 = this.chunks.getRenderedChunk(new BlockPos((k << 4) + 8, j, (l << 4) + 8));
						if (builtChunk2 != null && cameraView.isBoxInFrustum(builtChunk2.field_11071)) {
							builtChunk2.method_10156(frame);
							queue.add(new WorldRenderer.ChunkInfo(builtChunk2, null, 0));
						}
					}
				}
			}

			while (!queue.isEmpty()) {
				WorldRenderer.ChunkInfo chunkInfo2 = (WorldRenderer.ChunkInfo)queue.poll();
				BuiltChunk builtChunk3 = chunkInfo2.chunk;
				Direction direction2 = chunkInfo2.direction;
				BlockPos blockPos3 = builtChunk3.getPos();
				this.visibleChunks.add(chunkInfo2);

				for (Direction direction3 : Direction.values()) {
					BuiltChunk builtChunk4 = this.getAdjacentChunk(blockPos2, builtChunk3, direction3);
					if ((!bl2 || !chunkInfo2.facingSet.contains(direction3.getOpposite()))
						&& (!bl2 || direction2 == null || builtChunk3.method_10170().isVisibleThrough(direction2.getOpposite(), direction3))
						&& builtChunk4 != null
						&& builtChunk4.method_10156(frame)
						&& cameraView.isBoxInFrustum(builtChunk4.field_11071)) {
						WorldRenderer.ChunkInfo chunkInfo3 = new WorldRenderer.ChunkInfo(builtChunk4, direction3, chunkInfo2.propagationLevel + 1);
						chunkInfo3.facingSet.addAll(chunkInfo2.facingSet);
						chunkInfo3.facingSet.add(direction3);
						queue.add(chunkInfo3);
					}
				}
			}
		}

		if (this.field_10813) {
			this.captureFrustum(g, h, i);
			this.field_10813 = false;
		}

		this.chunkBuilder.clear();
		Set<BuiltChunk> set2 = this.chunksToRebuild;
		this.chunksToRebuild = Sets.newLinkedHashSet();

		for (WorldRenderer.ChunkInfo chunkInfo4 : this.visibleChunks) {
			BuiltChunk builtChunk5 = chunkInfo4.chunk;
			if (builtChunk5.method_10173() || set2.contains(builtChunk5)) {
				this.needsTerrainUpdate = true;
				if (this.isInChunk(blockPos2, chunkInfo4.chunk)) {
					this.client.profiler.push("build near");
					this.chunkBuilder.upload(builtChunk5);
					builtChunk5.method_10162(false);
					this.client.profiler.pop();
				} else {
					this.chunksToRebuild.add(builtChunk5);
				}
			}
		}

		this.chunksToRebuild.addAll(set2);
		this.client.profiler.pop();
	}

	private boolean isInChunk(BlockPos pos, BuiltChunk chunk) {
		BlockPos blockPos = chunk.getPos();
		if (MathHelper.abs(pos.getX() - blockPos.getX()) > 16) {
			return false;
		} else {
			return MathHelper.abs(pos.getY() - blockPos.getY()) > 16 ? false : MathHelper.abs(pos.getZ() - blockPos.getZ()) <= 16;
		}
	}

	private Set<Direction> getOpenChunkFaces(BlockPos pos) {
		ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
		BlockPos blockPos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
		Chunk chunk = this.world.getChunk(blockPos);

		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos, blockPos.add(15, 15, 15))) {
			if (chunk.getBlockAtPos(mutable).hasTransparency()) {
				chunkOcclusionDataBuilder.markClosed(mutable);
			}
		}

		return chunkOcclusionDataBuilder.getOpenFaces(pos);
	}

	private BuiltChunk getAdjacentChunk(BlockPos pos, BuiltChunk chunk, Direction direction) {
		BlockPos blockPos = chunk.method_10161(direction);
		if (MathHelper.abs(pos.getX() - blockPos.getX()) > this.renderDistance * 16) {
			return null;
		} else if (blockPos.getY() < 0 || blockPos.getY() >= 256) {
			return null;
		} else {
			return MathHelper.abs(pos.getZ() - blockPos.getZ()) > this.renderDistance * 16 ? null : this.chunks.getRenderedChunk(blockPos);
		}
	}

	private void captureFrustum(double x, double y, double z) {
		this.capturedFrustum = new Frustum();
		((Frustum)this.capturedFrustum).start();
		Matrix4f matrix4f = new Matrix4f(this.capturedFrustum.modelMatrix);
		matrix4f.transpose();
		Matrix4f matrix4f2 = new Matrix4f(this.capturedFrustum.projectionMatrix);
		matrix4f2.transpose();
		Matrix4f matrix4f3 = new Matrix4f();
		Matrix4f.mul(matrix4f2, matrix4f, matrix4f3);
		matrix4f3.invert();
		this.capturedFrustumPosition.x = x;
		this.capturedFrustumPosition.y = y;
		this.capturedFrustumPosition.z = z;
		this.capturedFrustumOrientation[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
		this.capturedFrustumOrientation[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
		this.capturedFrustumOrientation[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
		this.capturedFrustumOrientation[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.capturedFrustumOrientation[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

		for (int i = 0; i < 8; i++) {
			Matrix4f.transform(matrix4f3, this.capturedFrustumOrientation[i], this.capturedFrustumOrientation[i]);
			this.capturedFrustumOrientation[i].x = this.capturedFrustumOrientation[i].x / this.capturedFrustumOrientation[i].w;
			this.capturedFrustumOrientation[i].y = this.capturedFrustumOrientation[i].y / this.capturedFrustumOrientation[i].w;
			this.capturedFrustumOrientation[i].z = this.capturedFrustumOrientation[i].z / this.capturedFrustumOrientation[i].w;
			this.capturedFrustumOrientation[i].w = 1.0F;
		}
	}

	protected Vector3f getFacing(Entity entity, double tickDelta) {
		float f = (float)((double)entity.prevPitch + (double)(entity.pitch - entity.prevPitch) * tickDelta);
		float g = (float)((double)entity.prevYaw + (double)(entity.yaw - entity.prevYaw) * tickDelta);
		if (MinecraftClient.getInstance().options.perspective == 2) {
			f += 180.0F;
		}

		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		return new Vector3f(i * j, k, h * j);
	}

	public int renderLayer(RenderLayer renderLayer, double tickDelta, int anaglyphFilter, Entity entity) {
		DiffuseLighting.disable();
		if (renderLayer == RenderLayer.TRANSLUCENT) {
			this.client.profiler.push("translucent_sort");
			double d = entity.x - this.lastTranslucentSortX;
			double e = entity.y - this.lastTranslucentSortY;
			double f = entity.z - this.lastTranslucentSortZ;
			if (d * d + e * e + f * f > 1.0) {
				this.lastTranslucentSortX = entity.x;
				this.lastTranslucentSortY = entity.y;
				this.lastTranslucentSortZ = entity.z;
				int i = 0;

				for (WorldRenderer.ChunkInfo chunkInfo : this.visibleChunks) {
					if (chunkInfo.chunk.field_11070.isUnused(renderLayer) && i++ < 15) {
						this.chunkBuilder.method_10133(chunkInfo.chunk);
					}
				}
			}

			this.client.profiler.pop();
		}

		this.client.profiler.push("filterempty");
		int j = 0;
		boolean bl = renderLayer == RenderLayer.TRANSLUCENT;
		int k = bl ? this.visibleChunks.size() - 1 : 0;
		int l = bl ? -1 : this.visibleChunks.size();
		int m = bl ? -1 : 1;

		for (int n = k; n != l; n += m) {
			BuiltChunk builtChunk = ((WorldRenderer.ChunkInfo)this.visibleChunks.get(n)).chunk;
			if (!builtChunk.method_10170().method_10149(renderLayer)) {
				j++;
				this.chunkRenderManager.method_9771(builtChunk, renderLayer);
			}
		}

		this.client.profiler.swap("render_" + renderLayer);
		this.renderLayer(renderLayer);
		this.client.profiler.pop();
		return j;
	}

	private void renderLayer(RenderLayer renderLayer) {
		this.client.gameRenderer.enableLightmap();
		if (GLX.supportsVbo()) {
			GL11.glEnableClientState(32884);
			GLX.gl13ClientActiveTexture(GLX.textureUnit);
			GL11.glEnableClientState(32888);
			GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
			GL11.glEnableClientState(32888);
			GLX.gl13ClientActiveTexture(GLX.textureUnit);
			GL11.glEnableClientState(32886);
		}

		this.chunkRenderManager.render(renderLayer);
		if (GLX.supportsVbo()) {
			for (VertexFormatElement vertexFormatElement : VertexFormats.BLOCK.getElements()) {
				VertexFormatElement.Type type = vertexFormatElement.getType();
				int i = vertexFormatElement.getIndex();
				switch (type) {
					case POSITION:
						GL11.glDisableClientState(32884);
						break;
					case UV:
						GLX.gl13ClientActiveTexture(GLX.textureUnit + i);
						GL11.glDisableClientState(32888);
						GLX.gl13ClientActiveTexture(GLX.textureUnit);
						break;
					case COLOR:
						GL11.glDisableClientState(32886);
						GlStateManager.clearColor();
				}
			}
		}

		this.client.gameRenderer.disableLightmap();
	}

	private void clearBlockBreakingInfo(Iterator<BlockBreakingInfo> iterator) {
		while (iterator.hasNext()) {
			BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)iterator.next();
			int i = blockBreakingInfo.getLastUpdateTick();
			if (this.ticks - i > 400) {
				iterator.remove();
			}
		}
	}

	public void tick() {
		this.ticks++;
		if (this.ticks % 20 == 0) {
			this.clearBlockBreakingInfo(this.blockBreakingInfos.values().iterator());
		}
	}

	private void renderEndSky() {
		GlStateManager.disableFog();
		GlStateManager.disableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		DiffuseLighting.disable();
		GlStateManager.depthMask(false);
		this.textureManager.bindTexture(END_SKY);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int i = 0; i < 6; i++) {
			GlStateManager.pushMatrix();
			if (i == 1) {
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 2) {
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 3) {
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			}

			if (i == 4) {
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			}

			if (i == 5) {
				GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(-100.0, -100.0, 100.0).texture(0.0, 16.0).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(100.0, -100.0, 100.0).texture(16.0, 16.0).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(100.0, -100.0, -100.0).texture(16.0, 0.0).color(40, 40, 40, 255).next();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.enableAlphaTest();
	}

	public void renderSky(float tickDelta, int anaglyphFilter) {
		if (this.client.world.dimension.getType() == 1) {
			this.renderEndSky();
		} else if (this.client.world.dimension.canPlayersSleep()) {
			GlStateManager.disableTexture();
			Vec3d vec3d = this.world.method_3631(this.client.getCameraEntity(), tickDelta);
			float f = (float)vec3d.x;
			float g = (float)vec3d.y;
			float h = (float)vec3d.z;
			if (anaglyphFilter != 2) {
				float i = (f * 30.0F + g * 59.0F + h * 11.0F) / 100.0F;
				float j = (f * 30.0F + g * 70.0F) / 100.0F;
				float k = (f * 30.0F + h * 70.0F) / 100.0F;
				f = i;
				g = j;
				h = k;
			}

			GlStateManager.color(f, g, h);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			GlStateManager.depthMask(false);
			GlStateManager.enableFog();
			GlStateManager.color(f, g, h);
			if (this.vbo) {
				this.lightSkyBuffer.bind();
				GL11.glEnableClientState(32884);
				GL11.glVertexPointer(3, 5126, 12, 0L);
				this.lightSkyBuffer.draw(7);
				this.lightSkyBuffer.unbind();
				GL11.glDisableClientState(32884);
			} else {
				GlStateManager.callList(this.lightSkyList);
			}

			GlStateManager.disableFog();
			GlStateManager.disableAlphaTest();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			DiffuseLighting.disable();
			float[] fs = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(tickDelta), tickDelta);
			if (fs != null) {
				GlStateManager.disableTexture();
				GlStateManager.shadeModel(7425);
				GlStateManager.pushMatrix();
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
				float l = fs[0];
				float m = fs[1];
				float n = fs[2];
				if (anaglyphFilter != 2) {
					float o = (l * 30.0F + m * 59.0F + n * 11.0F) / 100.0F;
					float p = (l * 30.0F + m * 70.0F) / 100.0F;
					float q = (l * 30.0F + n * 70.0F) / 100.0F;
					l = o;
					m = p;
					n = q;
				}

				bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(0.0, 100.0, 0.0).color(l, m, n, fs[3]).next();
				int r = 16;

				for (int s = 0; s <= 16; s++) {
					float t = (float)s * (float) Math.PI * 2.0F / 16.0F;
					float u = MathHelper.sin(t);
					float v = MathHelper.cos(t);
					bufferBuilder.vertex((double)(u * 120.0F), (double)(v * 120.0F), (double)(-v * 40.0F * fs[3])).color(fs[0], fs[1], fs[2], 0.0F).next();
				}

				tessellator.draw();
				GlStateManager.popMatrix();
				GlStateManager.shadeModel(7424);
			}

			GlStateManager.enableTexture();
			GlStateManager.blendFuncSeparate(770, 1, 1, 0);
			GlStateManager.pushMatrix();
			float w = 1.0F - this.world.getRainGradient(tickDelta);
			GlStateManager.color(1.0F, 1.0F, 1.0F, w);
			GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(this.world.getSkyAngle(tickDelta) * 360.0F, 1.0F, 0.0F, 0.0F);
			float x = 30.0F;
			this.textureManager.bindTexture(SUN);
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex((double)(-x), 100.0, (double)(-x)).texture(0.0, 0.0).next();
			bufferBuilder.vertex((double)x, 100.0, (double)(-x)).texture(1.0, 0.0).next();
			bufferBuilder.vertex((double)x, 100.0, (double)x).texture(1.0, 1.0).next();
			bufferBuilder.vertex((double)(-x), 100.0, (double)x).texture(0.0, 1.0).next();
			tessellator.draw();
			x = 20.0F;
			this.textureManager.bindTexture(MOON_PHASES);
			int y = this.world.getMoonPhase();
			int z = y % 4;
			int aa = y / 4 % 2;
			float ab = (float)(z + 0) / 4.0F;
			float ac = (float)(aa + 0) / 2.0F;
			float ad = (float)(z + 1) / 4.0F;
			float ae = (float)(aa + 1) / 2.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex((double)(-x), -100.0, (double)x).texture((double)ad, (double)ae).next();
			bufferBuilder.vertex((double)x, -100.0, (double)x).texture((double)ab, (double)ae).next();
			bufferBuilder.vertex((double)x, -100.0, (double)(-x)).texture((double)ab, (double)ac).next();
			bufferBuilder.vertex((double)(-x), -100.0, (double)(-x)).texture((double)ad, (double)ac).next();
			tessellator.draw();
			GlStateManager.disableTexture();
			float af = this.world.method_3707(tickDelta) * w;
			if (af > 0.0F) {
				GlStateManager.color(af, af, af, af);
				if (this.vbo) {
					this.starsBuffer.bind();
					GL11.glEnableClientState(32884);
					GL11.glVertexPointer(3, 5126, 12, 0L);
					this.starsBuffer.draw(7);
					this.starsBuffer.unbind();
					GL11.glDisableClientState(32884);
				} else {
					GlStateManager.callList(this.starsList);
				}
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableAlphaTest();
			GlStateManager.enableFog();
			GlStateManager.popMatrix();
			GlStateManager.disableTexture();
			GlStateManager.color(0.0F, 0.0F, 0.0F);
			double d = this.client.player.getCameraPosVec(tickDelta).y - this.world.getHorizonHeight();
			if (d < 0.0) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 12.0F, 0.0F);
				if (this.vbo) {
					this.darkSkyBuffer.bind();
					GL11.glEnableClientState(32884);
					GL11.glVertexPointer(3, 5126, 12, 0L);
					this.darkSkyBuffer.draw(7);
					this.darkSkyBuffer.unbind();
					GL11.glDisableClientState(32884);
				} else {
					GlStateManager.callList(this.darkSkyList);
				}

				GlStateManager.popMatrix();
				float ag = 1.0F;
				float ah = -((float)(d + 65.0));
				float ai = -1.0F;
				bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(-1.0, (double)ah, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, (double)ah, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, (double)ah, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, (double)ah, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, (double)ah, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, (double)ah, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, (double)ah, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, (double)ah, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(-1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, -1.0, 1.0).color(0, 0, 0, 255).next();
				bufferBuilder.vertex(1.0, -1.0, -1.0).color(0, 0, 0, 255).next();
				tessellator.draw();
			}

			if (this.world.dimension.hasGround()) {
				GlStateManager.color(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F);
			} else {
				GlStateManager.color(f, g, h);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, -((float)(d - 16.0)), 0.0F);
			GlStateManager.callList(this.darkSkyList);
			GlStateManager.popMatrix();
			GlStateManager.enableTexture();
			GlStateManager.depthMask(true);
		}
	}

	public void renderClouds(float tickDelta, int anaglyphFilter) {
		if (this.client.world.dimension.canPlayersSleep()) {
			if (this.client.options.getCloudMode() == 2) {
				this.renderFancyClouds(tickDelta, anaglyphFilter);
			} else {
				GlStateManager.disableCull();
				float f = (float)(this.client.getCameraEntity().prevTickY + (this.client.getCameraEntity().y - this.client.getCameraEntity().prevTickY) * (double)tickDelta);
				int i = 32;
				int j = 8;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				this.textureManager.bindTexture(CLOUDS);
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
				Vec3d vec3d = this.world.getCloudColor(tickDelta);
				float g = (float)vec3d.x;
				float h = (float)vec3d.y;
				float k = (float)vec3d.z;
				if (anaglyphFilter != 2) {
					float l = (g * 30.0F + h * 59.0F + k * 11.0F) / 100.0F;
					float m = (g * 30.0F + h * 70.0F) / 100.0F;
					float n = (g * 30.0F + k * 70.0F) / 100.0F;
					g = l;
					h = m;
					k = n;
				}

				float o = 4.8828125E-4F;
				double d = (double)((float)this.ticks + tickDelta);
				double e = this.client.getCameraEntity().prevX + (this.client.getCameraEntity().x - this.client.getCameraEntity().prevX) * (double)tickDelta + d * 0.03F;
				double p = this.client.getCameraEntity().prevZ + (this.client.getCameraEntity().z - this.client.getCameraEntity().prevZ) * (double)tickDelta;
				int q = MathHelper.floor(e / 2048.0);
				int r = MathHelper.floor(p / 2048.0);
				e -= (double)(q * 2048);
				p -= (double)(r * 2048);
				float s = this.world.dimension.getCloudHeight() - f + 0.33F;
				float t = (float)(e * 4.8828125E-4);
				float u = (float)(p * 4.8828125E-4);
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);

				for (int v = -256; v < 256; v += 32) {
					for (int w = -256; w < 256; w += 32) {
						bufferBuilder.vertex((double)(v + 0), (double)s, (double)(w + 32))
							.texture((double)((float)(v + 0) * 4.8828125E-4F + t), (double)((float)(w + 32) * 4.8828125E-4F + u))
							.color(g, h, k, 0.8F)
							.next();
						bufferBuilder.vertex((double)(v + 32), (double)s, (double)(w + 32))
							.texture((double)((float)(v + 32) * 4.8828125E-4F + t), (double)((float)(w + 32) * 4.8828125E-4F + u))
							.color(g, h, k, 0.8F)
							.next();
						bufferBuilder.vertex((double)(v + 32), (double)s, (double)(w + 0))
							.texture((double)((float)(v + 32) * 4.8828125E-4F + t), (double)((float)(w + 0) * 4.8828125E-4F + u))
							.color(g, h, k, 0.8F)
							.next();
						bufferBuilder.vertex((double)(v + 0), (double)s, (double)(w + 0))
							.texture((double)((float)(v + 0) * 4.8828125E-4F + t), (double)((float)(w + 0) * 4.8828125E-4F + u))
							.color(g, h, k, 0.8F)
							.next();
					}
				}

				tessellator.draw();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableBlend();
				GlStateManager.enableCull();
			}
		}
	}

	public boolean hasThickFog(double d, double e, double f, float g) {
		return false;
	}

	private void renderFancyClouds(float tickDelta, int anaglyphFilter) {
		GlStateManager.disableCull();
		float f = (float)(this.client.getCameraEntity().prevTickY + (this.client.getCameraEntity().y - this.client.getCameraEntity().prevTickY) * (double)tickDelta);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float g = 12.0F;
		float h = 4.0F;
		double d = (double)((float)this.ticks + tickDelta);
		double e = (this.client.getCameraEntity().prevX + (this.client.getCameraEntity().x - this.client.getCameraEntity().prevX) * (double)tickDelta + d * 0.03F)
			/ 12.0;
		double i = (this.client.getCameraEntity().prevZ + (this.client.getCameraEntity().z - this.client.getCameraEntity().prevZ) * (double)tickDelta) / 12.0 + 0.33F;
		float j = this.world.dimension.getCloudHeight() - f + 0.33F;
		int k = MathHelper.floor(e / 2048.0);
		int l = MathHelper.floor(i / 2048.0);
		e -= (double)(k * 2048);
		i -= (double)(l * 2048);
		this.textureManager.bindTexture(CLOUDS);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		Vec3d vec3d = this.world.getCloudColor(tickDelta);
		float m = (float)vec3d.x;
		float n = (float)vec3d.y;
		float o = (float)vec3d.z;
		if (anaglyphFilter != 2) {
			float p = (m * 30.0F + n * 59.0F + o * 11.0F) / 100.0F;
			float q = (m * 30.0F + n * 70.0F) / 100.0F;
			float r = (m * 30.0F + o * 70.0F) / 100.0F;
			m = p;
			n = q;
			o = r;
		}

		float s = m * 0.9F;
		float t = n * 0.9F;
		float u = o * 0.9F;
		float v = m * 0.7F;
		float w = n * 0.7F;
		float x = o * 0.7F;
		float y = m * 0.8F;
		float z = n * 0.8F;
		float aa = o * 0.8F;
		float ab = 0.00390625F;
		float ac = (float)MathHelper.floor(e) * 0.00390625F;
		float ad = (float)MathHelper.floor(i) * 0.00390625F;
		float ae = (float)(e - (double)MathHelper.floor(e));
		float af = (float)(i - (double)MathHelper.floor(i));
		int ag = 8;
		int ah = 4;
		float ai = 9.765625E-4F;
		GlStateManager.scale(12.0F, 1.0F, 12.0F);

		for (int aj = 0; aj < 2; aj++) {
			if (aj == 0) {
				GlStateManager.colorMask(false, false, false, false);
			} else {
				switch (anaglyphFilter) {
					case 0:
						GlStateManager.colorMask(false, true, true, true);
						break;
					case 1:
						GlStateManager.colorMask(true, false, false, true);
						break;
					case 2:
						GlStateManager.colorMask(true, true, true, true);
				}
			}

			for (int ak = -3; ak <= 4; ak++) {
				for (int al = -3; al <= 4; al++) {
					bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
					float am = (float)(ak * 8);
					float an = (float)(al * 8);
					float ao = am - ae;
					float ap = an - af;
					if (j > -5.0F) {
						bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 0.0F), (double)(ap + 8.0F))
							.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
							.color(v, w, x, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 0.0F), (double)(ap + 8.0F))
							.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
							.color(v, w, x, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 0.0F), (double)(ap + 0.0F))
							.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
							.color(v, w, x, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 0.0F), (double)(ap + 0.0F))
							.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
							.color(v, w, x, 0.8F)
							.normal(0.0F, -1.0F, 0.0F)
							.next();
					}

					if (j <= 5.0F) {
						bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 4.0F - 9.765625E-4F), (double)(ap + 8.0F))
							.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 4.0F - 9.765625E-4F), (double)(ap + 8.0F))
							.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 4.0F - 9.765625E-4F), (double)(ap + 0.0F))
							.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
						bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 4.0F - 9.765625E-4F), (double)(ap + 0.0F))
							.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
							.color(m, n, o, 0.8F)
							.normal(0.0F, 1.0F, 0.0F)
							.next();
					}

					if (ak > -1) {
						for (int aq = 0; aq < 8; aq++) {
							bufferBuilder.vertex((double)(ao + (float)aq + 0.0F), (double)(j + 0.0F), (double)(ap + 8.0F))
								.texture((double)((am + (float)aq + 0.5F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ao + (float)aq + 0.0F), (double)(j + 4.0F), (double)(ap + 8.0F))
								.texture((double)((am + (float)aq + 0.5F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ao + (float)aq + 0.0F), (double)(j + 4.0F), (double)(ap + 0.0F))
								.texture((double)((am + (float)aq + 0.5F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ao + (float)aq + 0.0F), (double)(j + 0.0F), (double)(ap + 0.0F))
								.texture((double)((am + (float)aq + 0.5F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(-1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (ak <= 1) {
						for (int ar = 0; ar < 8; ar++) {
							bufferBuilder.vertex((double)(ao + (float)ar + 1.0F - 9.765625E-4F), (double)(j + 0.0F), (double)(ap + 8.0F))
								.texture((double)((am + (float)ar + 0.5F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ao + (float)ar + 1.0F - 9.765625E-4F), (double)(j + 4.0F), (double)(ap + 8.0F))
								.texture((double)((am + (float)ar + 0.5F) * 0.00390625F + ac), (double)((an + 8.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ao + (float)ar + 1.0F - 9.765625E-4F), (double)(j + 4.0F), (double)(ap + 0.0F))
								.texture((double)((am + (float)ar + 0.5F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
							bufferBuilder.vertex((double)(ao + (float)ar + 1.0F - 9.765625E-4F), (double)(j + 0.0F), (double)(ap + 0.0F))
								.texture((double)((am + (float)ar + 0.5F) * 0.00390625F + ac), (double)((an + 0.0F) * 0.00390625F + ad))
								.color(s, t, u, 0.8F)
								.normal(1.0F, 0.0F, 0.0F)
								.next();
						}
					}

					if (al > -1) {
						for (int as = 0; as < 8; as++) {
							bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 4.0F), (double)(ap + (float)as + 0.0F))
								.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + (float)as + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 4.0F), (double)(ap + (float)as + 0.0F))
								.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + (float)as + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 0.0F), (double)(ap + (float)as + 0.0F))
								.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + (float)as + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
							bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 0.0F), (double)(ap + (float)as + 0.0F))
								.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + (float)as + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, -1.0F)
								.next();
						}
					}

					if (al <= 1) {
						for (int at = 0; at < 8; at++) {
							bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 4.0F), (double)(ap + (float)at + 1.0F - 9.765625E-4F))
								.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + (float)at + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 4.0F), (double)(ap + (float)at + 1.0F - 9.765625E-4F))
								.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + (float)at + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ao + 8.0F), (double)(j + 0.0F), (double)(ap + (float)at + 1.0F - 9.765625E-4F))
								.texture((double)((am + 8.0F) * 0.00390625F + ac), (double)((an + (float)at + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
							bufferBuilder.vertex((double)(ao + 0.0F), (double)(j + 0.0F), (double)(ap + (float)at + 1.0F - 9.765625E-4F))
								.texture((double)((am + 0.0F) * 0.00390625F + ac), (double)((an + (float)at + 0.5F) * 0.00390625F + ad))
								.color(y, z, aa, 0.8F)
								.normal(0.0F, 0.0F, 1.0F)
								.next();
						}
					}

					tessellator.draw();
				}
			}
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}

	public void updateChunks(long limitTime) {
		this.needsTerrainUpdate = this.needsTerrainUpdate | this.chunkBuilder.upload(limitTime);
		if (!this.chunksToRebuild.isEmpty()) {
			Iterator<BuiltChunk> iterator = this.chunksToRebuild.iterator();

			while (iterator.hasNext()) {
				BuiltChunk builtChunk = (BuiltChunk)iterator.next();
				if (!this.chunkBuilder.send(builtChunk)) {
					break;
				}

				builtChunk.method_10162(false);
				iterator.remove();
				long l = limitTime - System.nanoTime();
				if (l < 0L) {
					break;
				}
			}
		}
	}

	public void renderWorldBorder(Entity entity, float tickDelta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		WorldBorder worldBorder = this.world.getWorldBorder();
		double d = (double)(this.client.options.viewDistance * 16);
		if (!(entity.x < worldBorder.getBoundEast() - d)
			|| !(entity.x > worldBorder.getBoundWest() + d)
			|| !(entity.z < worldBorder.getBoundSouth() - d)
			|| !(entity.z > worldBorder.getBoundNorth() + d)) {
			double e = 1.0 - worldBorder.getDistanceInsideBorder(entity) / d;
			e = Math.pow(e, 4.0);
			double f = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
			double g = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
			double h = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 1, 1, 0);
			this.textureManager.bindTexture(FORCEFIELD);
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			int i = worldBorder.getWorldBorderStage().getColor();
			float j = (float)(i >> 16 & 0xFF) / 255.0F;
			float k = (float)(i >> 8 & 0xFF) / 255.0F;
			float l = (float)(i & 0xFF) / 255.0F;
			GlStateManager.color(j, k, l, (float)e);
			GlStateManager.polygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolyOffset();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableAlphaTest();
			GlStateManager.disableCull();
			float m = (float)(MinecraftClient.getTime() % 3000L) / 3000.0F;
			float n = 0.0F;
			float o = 0.0F;
			float p = 128.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.offset(-f, -g, -h);
			double q = Math.max((double)MathHelper.floor(h - d), worldBorder.getBoundNorth());
			double r = Math.min((double)MathHelper.ceil(h + d), worldBorder.getBoundSouth());
			if (f > worldBorder.getBoundEast() - d) {
				float s = 0.0F;

				for (double t = q; t < r; s += 0.5F) {
					double u = Math.min(1.0, r - t);
					float v = (float)u * 0.5F;
					bufferBuilder.vertex(worldBorder.getBoundEast(), 256.0, t).texture((double)(m + s), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundEast(), 256.0, t + u).texture((double)(m + v + s), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundEast(), 0.0, t + u).texture((double)(m + v + s), (double)(m + 128.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundEast(), 0.0, t).texture((double)(m + s), (double)(m + 128.0F)).next();
					t++;
				}
			}

			if (f < worldBorder.getBoundWest() + d) {
				float w = 0.0F;

				for (double x = q; x < r; w += 0.5F) {
					double y = Math.min(1.0, r - x);
					float z = (float)y * 0.5F;
					bufferBuilder.vertex(worldBorder.getBoundWest(), 256.0, x).texture((double)(m + w), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundWest(), 256.0, x + y).texture((double)(m + z + w), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundWest(), 0.0, x + y).texture((double)(m + z + w), (double)(m + 128.0F)).next();
					bufferBuilder.vertex(worldBorder.getBoundWest(), 0.0, x).texture((double)(m + w), (double)(m + 128.0F)).next();
					x++;
				}
			}

			q = Math.max((double)MathHelper.floor(f - d), worldBorder.getBoundWest());
			r = Math.min((double)MathHelper.ceil(f + d), worldBorder.getBoundEast());
			if (h > worldBorder.getBoundSouth() - d) {
				float aa = 0.0F;

				for (double ab = q; ab < r; aa += 0.5F) {
					double ac = Math.min(1.0, r - ab);
					float ad = (float)ac * 0.5F;
					bufferBuilder.vertex(ab, 256.0, worldBorder.getBoundSouth()).texture((double)(m + aa), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(ab + ac, 256.0, worldBorder.getBoundSouth()).texture((double)(m + ad + aa), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(ab + ac, 0.0, worldBorder.getBoundSouth()).texture((double)(m + ad + aa), (double)(m + 128.0F)).next();
					bufferBuilder.vertex(ab, 0.0, worldBorder.getBoundSouth()).texture((double)(m + aa), (double)(m + 128.0F)).next();
					ab++;
				}
			}

			if (h < worldBorder.getBoundNorth() + d) {
				float ae = 0.0F;

				for (double af = q; af < r; ae += 0.5F) {
					double ag = Math.min(1.0, r - af);
					float ah = (float)ag * 0.5F;
					bufferBuilder.vertex(af, 256.0, worldBorder.getBoundNorth()).texture((double)(m + ae), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(af + ag, 256.0, worldBorder.getBoundNorth()).texture((double)(m + ah + ae), (double)(m + 0.0F)).next();
					bufferBuilder.vertex(af + ag, 0.0, worldBorder.getBoundNorth()).texture((double)(m + ah + ae), (double)(m + 128.0F)).next();
					bufferBuilder.vertex(af, 0.0, worldBorder.getBoundNorth()).texture((double)(m + ae), (double)(m + 128.0F)).next();
					af++;
				}
			}

			tessellator.draw();
			bufferBuilder.offset(0.0, 0.0, 0.0);
			GlStateManager.enableCull();
			GlStateManager.disableAlphaTest();
			GlStateManager.polygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolyOffset();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
		}
	}

	private void preDrawBlockDamage() {
		GlStateManager.blendFuncSeparate(774, 768, 1, 0);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.polygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolyOffset();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableAlphaTest();
		GlStateManager.pushMatrix();
	}

	private void postDrawBlockDamage() {
		GlStateManager.disableAlphaTest();
		GlStateManager.polygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolyOffset();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	public void drawBlockDamage(Tessellator tessellator, BufferBuilder bufferBuilder, Entity entity, float tickDelta) {
		double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
		double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
		double f = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
		if (!this.blockBreakingInfos.isEmpty()) {
			this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.preDrawBlockDamage();
			bufferBuilder.begin(7, VertexFormats.BLOCK);
			bufferBuilder.offset(-d, -e, -f);
			bufferBuilder.enableTexture();
			Iterator<BlockBreakingInfo> iterator = this.blockBreakingInfos.values().iterator();

			while (iterator.hasNext()) {
				BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)iterator.next();
				BlockPos blockPos = blockBreakingInfo.getPos();
				double g = (double)blockPos.getX() - d;
				double h = (double)blockPos.getY() - e;
				double i = (double)blockPos.getZ() - f;
				Block block = this.world.getBlockState(blockPos).getBlock();
				if (!(block instanceof ChestBlock) && !(block instanceof EnderChestBlock) && !(block instanceof AbstractSignBlock) && !(block instanceof SkullBlock)) {
					if (g * g + h * h + i * i > 1024.0) {
						iterator.remove();
					} else {
						BlockState blockState = this.world.getBlockState(blockPos);
						if (blockState.getBlock().getMaterial() != Material.AIR) {
							int j = blockBreakingInfo.getStage();
							Sprite sprite = this.destroySprites[j];
							BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
							blockRenderManager.renderDamage(blockState, blockPos, sprite, this.world);
						}
					}
				}
			}

			tessellator.draw();
			bufferBuilder.offset(0.0, 0.0, 0.0);
			this.postDrawBlockDamage();
		}
	}

	public void drawBlockOutline(PlayerEntity player, BlockHitResult hitResult, int i, float tickDelta) {
		if (i == 0 && hitResult.type == BlockHitResult.Type.BLOCK) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GlStateManager.disableTexture();
			GlStateManager.depthMask(false);
			float f = 0.002F;
			BlockPos blockPos = hitResult.getBlockPos();
			Block block = this.world.getBlockState(blockPos).getBlock();
			if (block.getMaterial() != Material.AIR && this.world.getWorldBorder().contains(blockPos)) {
				block.setBoundingBox(this.world, blockPos);
				double d = player.prevTickX + (player.x - player.prevTickX) * (double)tickDelta;
				double e = player.prevTickY + (player.y - player.prevTickY) * (double)tickDelta;
				double g = player.prevTickZ + (player.z - player.prevTickZ) * (double)tickDelta;
				drawBox(block.getSelectionBox(this.world, blockPos).expand(0.002F, 0.002F, 0.002F).offset(-d, -e, -g));
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
		}
	}

	public static void drawBox(Box box) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(3, VertexFormats.POSITION);
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
		tessellator.draw();
		bufferBuilder.begin(3, VertexFormats.POSITION);
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
		tessellator.draw();
		bufferBuilder.begin(1, VertexFormats.POSITION);
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
		tessellator.draw();
	}

	public static void drawBox(Box box, int red, int green, int blue, int alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).next();
		tessellator.draw();
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
		tessellator.draw();
		bufferBuilder.begin(1, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
		tessellator.draw();
	}

	private void updateBlock(int i, int j, int k, int l, int m, int n) {
		this.chunks.scheduleRebuild(i, j, k, l, m, n);
	}

	@Override
	public void onBlockUpdate(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		this.updateBlock(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
	}

	@Override
	public void onLightUpdate(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		this.updateBlock(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
	}

	@Override
	public void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.updateBlock(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
	}

	@Override
	public void playMusicDisc(String id, BlockPos pos) {
		SoundInstance soundInstance = (SoundInstance)this.playingSongs.get(pos);
		if (soundInstance != null) {
			this.client.getSoundManager().stop(soundInstance);
			this.playingSongs.remove(pos);
		}

		if (id != null) {
			MusicDiscItem musicDiscItem = MusicDiscItem.getByName(id);
			if (musicDiscItem != null) {
				this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem.getDescription());
			}

			SoundInstance var5 = PositionedSoundInstance.method_7053(new Identifier(id), (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
			this.playingSongs.put(pos, var5);
			this.client.getSoundManager().play(var5);
		}
	}

	@Override
	public void playSound(String name, double x, double y, double z, float volume, float pitch) {
	}

	@Override
	public void playSound(PlayerEntity except, String name, double x, double y, double z, float volume, float pitch) {
	}

	@Override
	public void addParticle(int id, boolean bl, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args) {
		try {
			this.addParticleInternal(id, bl, x, y, z, velocityX, velocityY, velocityZ, args);
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Exception while adding particle");
			CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
			crashReportSection.add("ID", id);
			if (args != null) {
				crashReportSection.add("Parameters", args);
			}

			crashReportSection.add("Position", new Callable<String>() {
				public String call() throws Exception {
					return CrashReportSection.createPositionString(x, y, z);
				}
			});
			throw new CrashException(crashReport);
		}
	}

	private void addParticleInternal(ParticleType type, double d, double e, double f, double g, double h, double i, int... is) {
		this.addParticle(type.getId(), type.getAlwaysShow(), d, e, f, g, h, i, is);
	}

	private Particle addParticleInternal(int i, boolean bl, double d, double e, double f, double g, double h, double j, int... is) {
		if (this.client != null && this.client.getCameraEntity() != null && this.client.particleManager != null) {
			int k = this.client.options.particle;
			if (k == 1 && this.world.random.nextInt(3) == 0) {
				k = 2;
			}

			double l = this.client.getCameraEntity().x - d;
			double m = this.client.getCameraEntity().y - e;
			double n = this.client.getCameraEntity().z - f;
			if (bl) {
				return this.client.particleManager.addParticle(i, d, e, f, g, h, j, is);
			} else {
				double o = 16.0;
				if (l * l + m * m + n * n > 256.0) {
					return null;
				} else {
					return k > 1 ? null : this.client.particleManager.addParticle(i, d, e, f, g, h, j, is);
				}
			}
		} else {
			return null;
		}
	}

	@Override
	public void onEntitySpawned(Entity entity) {
	}

	@Override
	public void onEntityRemoved(Entity entity) {
	}

	public void cleanUp() {
	}

	@Override
	public void processGlobalEvent(int eventId, BlockPos pos, int j) {
		switch (eventId) {
			case 1013:
			case 1018:
				if (this.client.getCameraEntity() != null) {
					double d = (double)pos.getX() - this.client.getCameraEntity().x;
					double e = (double)pos.getY() - this.client.getCameraEntity().y;
					double f = (double)pos.getZ() - this.client.getCameraEntity().z;
					double g = Math.sqrt(d * d + e * e + f * f);
					double h = this.client.getCameraEntity().x;
					double i = this.client.getCameraEntity().y;
					double k = this.client.getCameraEntity().z;
					if (g > 0.0) {
						h += d / g * 2.0;
						i += e / g * 2.0;
						k += f / g * 2.0;
					}

					if (eventId == 1013) {
						this.world.playSound(h, i, k, "mob.wither.spawn", 1.0F, 1.0F, false);
					} else {
						this.world.playSound(h, i, k, "mob.enderdragon.end", 5.0F, 1.0F, false);
					}
				}
		}
	}

	@Override
	public void processWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
		Random random = this.world.random;
		switch (eventId) {
			case 1000:
				this.world.playSound(pos, "random.click", 1.0F, 1.0F, false);
				break;
			case 1001:
				this.world.playSound(pos, "random.click", 1.0F, 1.2F, false);
				break;
			case 1002:
				this.world.playSound(pos, "random.bow", 1.0F, 1.2F, false);
				break;
			case 1003:
				this.world.playSound(pos, "random.door_open", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1004:
				this.world.playSound(pos, "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
				break;
			case 1005:
				if (Item.byRawId(data) instanceof MusicDiscItem) {
					this.world.playMusicDisc(pos, "records." + ((MusicDiscItem)Item.byRawId(data)).recordType);
				} else {
					this.world.playMusicDisc(pos, null);
				}
				break;
			case 1006:
				this.world.playSound(pos, "random.door_close", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1007:
				this.world.playSound(pos, "mob.ghast.charge", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1008:
				this.world.playSound(pos, "mob.ghast.fireball", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1009:
				this.world.playSound(pos, "mob.ghast.fireball", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1010:
				this.world.playSound(pos, "mob.zombie.wood", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1011:
				this.world.playSound(pos, "mob.zombie.metal", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1012:
				this.world.playSound(pos, "mob.zombie.woodbreak", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1014:
				this.world.playSound(pos, "mob.wither.shoot", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1015:
				this.world.playSound(pos, "mob.bat.takeoff", 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.world.playSound(pos, "mob.zombie.infect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.world.playSound(pos, "mob.zombie.unfect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1020:
				this.world.playSound(pos, "random.anvil_break", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1021:
				this.world.playSound(pos, "random.anvil_use", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1022:
				this.world.playSound(pos, "random.anvil_land", 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2000:
				int i = data % 3 - 1;
				int j = data / 3 % 3 - 1;
				double d = (double)pos.getX() + (double)i * 0.6 + 0.5;
				double e = (double)pos.getY() + 0.5;
				double f = (double)pos.getZ() + (double)j * 0.6 + 0.5;

				for (int k = 0; k < 10; k++) {
					double g = random.nextDouble() * 0.2 + 0.01;
					double h = d + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
					double l = e + (random.nextDouble() - 0.5) * 0.5;
					double m = f + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
					double n = (double)i * g + random.nextGaussian() * 0.01;
					double o = -0.03 + random.nextGaussian() * 0.01;
					double p = (double)j * g + random.nextGaussian() * 0.01;
					this.addParticleInternal(ParticleType.SMOKE, h, l, m, n, o, p);
				}
				break;
			case 2001:
				Block block = Block.getById(data & 4095);
				if (block.getMaterial() != Material.AIR) {
					this.client
						.getSoundManager()
						.play(
							new PositionedSoundInstance(
								new Identifier(block.sound.getDigSound()),
								(block.sound.getVolume() + 1.0F) / 2.0F,
								block.sound.getPitch() * 0.8F,
								(float)pos.getX() + 0.5F,
								(float)pos.getY() + 0.5F,
								(float)pos.getZ() + 0.5F
							)
						);
				}

				this.client.particleManager.addBlockBreakParticles(pos, block.stateFromData(data >> 12 & 0xFF));
				break;
			case 2002:
				double v = (double)pos.getX();
				double w = (double)pos.getY();
				double x = (double)pos.getZ();

				for (int y = 0; y < 8; y++) {
					this.addParticleInternal(
						ParticleType.ITEM_CRACK,
						v,
						w,
						x,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15,
						Item.getRawId(Items.POTION),
						data
					);
				}

				int z = Items.POTION.getColor(data);
				float aa = (float)(z >> 16 & 0xFF) / 255.0F;
				float ab = (float)(z >> 8 & 0xFF) / 255.0F;
				float ac = (float)(z >> 0 & 0xFF) / 255.0F;
				ParticleType particleType = ParticleType.SPELL;
				if (Items.POTION.isInstant(data)) {
					particleType = ParticleType.INSTANT_SPELL;
				}

				for (int ad = 0; ad < 100; ad++) {
					double ae = random.nextDouble() * 4.0;
					double af = random.nextDouble() * Math.PI * 2.0;
					double ag = Math.cos(af) * ae;
					double ah = 0.01 + random.nextDouble() * 0.5;
					double ai = Math.sin(af) * ae;
					Particle particle = this.addParticleInternal(particleType.getId(), particleType.getAlwaysShow(), v + ag * 0.1, w + 0.3, x + ai * 0.1, ag, ah, ai);
					if (particle != null) {
						float aj = 0.75F + random.nextFloat() * 0.25F;
						particle.setColor(aa * aj, ab * aj, ac * aj);
						particle.move((float)ae);
					}
				}

				this.world.playSound(pos, "game.potion.smash", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double q = (double)pos.getX() + 0.5;
				double r = (double)pos.getY();
				double s = (double)pos.getZ() + 0.5;

				for (int t = 0; t < 8; t++) {
					this.addParticleInternal(
						ParticleType.ITEM_CRACK,
						q,
						r,
						s,
						random.nextGaussian() * 0.15,
						random.nextDouble() * 0.2,
						random.nextGaussian() * 0.15,
						Item.getRawId(Items.EYE_OF_ENDER)
					);
				}

				for (double u = 0.0; u < Math.PI * 2; u += Math.PI / 20) {
					this.addParticleInternal(ParticleType.NETHER_PORTAL, q + Math.cos(u) * 5.0, r - 0.4, s + Math.sin(u) * 5.0, Math.cos(u) * -5.0, 0.0, Math.sin(u) * -5.0);
					this.addParticleInternal(ParticleType.NETHER_PORTAL, q + Math.cos(u) * 5.0, r - 0.4, s + Math.sin(u) * 5.0, Math.cos(u) * -7.0, 0.0, Math.sin(u) * -7.0);
				}
				break;
			case 2004:
				for (int ak = 0; ak < 20; ak++) {
					double al = (double)pos.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					double am = (double)pos.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					double an = (double)pos.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
					this.world.addParticle(ParticleType.SMOKE, al, am, an, 0.0, 0.0, 0.0, new int[0]);
					this.world.addParticle(ParticleType.FIRE, al, am, an, 0.0, 0.0, 0.0, new int[0]);
				}
				break;
			case 2005:
				DyeItem.spawnParticles(this.world, pos, data);
		}
	}

	@Override
	public void setBlockBreakInfo(int entityId, BlockPos pos, int progress) {
		if (progress >= 0 && progress < 10) {
			BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.get(entityId);
			if (blockBreakingInfo == null
				|| blockBreakingInfo.getPos().getX() != pos.getX()
				|| blockBreakingInfo.getPos().getY() != pos.getY()
				|| blockBreakingInfo.getPos().getZ() != pos.getZ()) {
				blockBreakingInfo = new BlockBreakingInfo(entityId, pos);
				this.blockBreakingInfos.put(entityId, blockBreakingInfo);
			}

			blockBreakingInfo.setStage(progress);
			blockBreakingInfo.setLastUpdateTick(this.ticks);
		} else {
			this.blockBreakingInfos.remove(entityId);
		}
	}

	public void scheduleTerrainUpdate() {
		this.needsTerrainUpdate = true;
	}

	public void updateNoCullingBlockEntities(Collection<BlockEntity> removed, Collection<BlockEntity> added) {
		synchronized (this.noCullingBlockEntities) {
			this.noCullingBlockEntities.removeAll(removed);
			this.noCullingBlockEntities.addAll(added);
		}
	}

	class ChunkInfo {
		final BuiltChunk chunk;
		final Direction direction;
		final Set<Direction> facingSet = EnumSet.noneOf(Direction.class);
		final int propagationLevel;

		private ChunkInfo(BuiltChunk builtChunk, Direction direction, int i) {
			this.chunk = builtChunk;
			this.direction = direction;
			this.propagationLevel = i;
		}
	}
}
