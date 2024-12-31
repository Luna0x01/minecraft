package net.minecraft.client.render;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.client.util.Window;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GameRenderer implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier RAIN_TEXTURE = new Identifier("textures/environment/rain.png");
	private static final Identifier SNOW_TEXTURE = new Identifier("textures/environment/snow.png");
	public static boolean anaglyphEnabled;
	public static int anaglyphFilter;
	private MinecraftClient client;
	private final ResourceManager resourceManager;
	private Random random = new Random();
	private float viewDistance;
	public final HeldItemRenderer firstPersonRenderer;
	private final MapRenderer mapRenderer;
	private int ticks;
	private Entity targetedEntity;
	private SmoothUtil cursorXSmoother = new SmoothUtil();
	private SmoothUtil cursorYSmoother = new SmoothUtil();
	private float thirdPersonDistance = 4.0F;
	private float lastThirdPersonDistance = 4.0F;
	private float cursorDeltaX;
	private float cursorDeltaY;
	private float smoothedCursorDeltaX;
	private float smoothedCursorDeltaY;
	private float lastTickDelta;
	private float movementFovMultiplier;
	private float lastMovementFovMultiplier;
	private float skyDarkness;
	private float lastSkyDarkness;
	private boolean thickFog;
	private boolean renderHand = true;
	private boolean blockOutlineEnabled = true;
	private long field_13463;
	private long lastWindowFocusedTime = MinecraftClient.getTime();
	private long lastWorldRenderNanoTime;
	private final NativeImageBackedTexture lightmapTexture;
	private final int[] lightmapTexturePixels;
	private final Identifier lightmapTextureId;
	private boolean lightmapDirty;
	private float lightmapFlicker;
	private float lastLightmapFlicker;
	private int weatherSoundAttempts;
	private float[] rainOffsetX = new float[1024];
	private float[] rainOffsetY = new float[1024];
	private FloatBuffer fogColorBuffer = GlAllocationUtils.allocateFloatBuffer(16);
	private float fogRed;
	private float fogGreen;
	private float fogBlue;
	private float prevFogColor;
	private float fogColor;
	private int panoramaDirection = 0;
	private boolean renderingPanorama = false;
	private double zoom = 1.0;
	private double zoomX;
	private double zoomY;
	private ShaderEffect shader;
	private static final Identifier[] SHADERS_LOCATIONS = new Identifier[]{
		new Identifier("shaders/post/notch.json"),
		new Identifier("shaders/post/fxaa.json"),
		new Identifier("shaders/post/art.json"),
		new Identifier("shaders/post/bumpy.json"),
		new Identifier("shaders/post/blobs2.json"),
		new Identifier("shaders/post/pencil.json"),
		new Identifier("shaders/post/color_convolve.json"),
		new Identifier("shaders/post/deconverge.json"),
		new Identifier("shaders/post/flip.json"),
		new Identifier("shaders/post/invert.json"),
		new Identifier("shaders/post/ntsc.json"),
		new Identifier("shaders/post/outline.json"),
		new Identifier("shaders/post/phosphor.json"),
		new Identifier("shaders/post/scan_pincushion.json"),
		new Identifier("shaders/post/sobel.json"),
		new Identifier("shaders/post/bits.json"),
		new Identifier("shaders/post/desaturate.json"),
		new Identifier("shaders/post/green.json"),
		new Identifier("shaders/post/blur.json"),
		new Identifier("shaders/post/wobble.json"),
		new Identifier("shaders/post/blobs.json"),
		new Identifier("shaders/post/antialias.json"),
		new Identifier("shaders/post/creeper.json"),
		new Identifier("shaders/post/spider.json")
	};
	public static final int SHADER_COUNT = SHADERS_LOCATIONS.length;
	private int forcedShaderIndex = SHADER_COUNT;
	private boolean shadersEnabled = false;
	private int frameCount = 0;

	public GameRenderer(MinecraftClient minecraftClient, ResourceManager resourceManager) {
		this.client = minecraftClient;
		this.resourceManager = resourceManager;
		this.firstPersonRenderer = minecraftClient.getHeldItemRenderer();
		this.mapRenderer = new MapRenderer(minecraftClient.getTextureManager());
		this.lightmapTexture = new NativeImageBackedTexture(16, 16);
		this.lightmapTextureId = minecraftClient.getTextureManager().registerDynamicTexture("lightMap", this.lightmapTexture);
		this.lightmapTexturePixels = this.lightmapTexture.getPixels();
		this.shader = null;

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = MathHelper.sqrt(f * f + g * g);
				this.rainOffsetX[i << 5 | j] = -g / h;
				this.rainOffsetY[i << 5 | j] = f / h;
			}
		}
	}

	public boolean areShadersSupported() {
		return GLX.shadersSupported && this.shader != null;
	}

	public void disableShader() {
		if (this.shader != null) {
			this.shader.disable();
		}

		this.shader = null;
		this.forcedShaderIndex = SHADER_COUNT;
	}

	public void toggleShadersEnabled() {
		this.shadersEnabled = !this.shadersEnabled;
	}

	public void onCameraEntitySet(Entity entity) {
		if (GLX.shadersSupported) {
			if (this.shader != null) {
				this.shader.disable();
			}

			this.shader = null;
			if (entity instanceof CreeperEntity) {
				this.loadShader(new Identifier("shaders/post/creeper.json"));
			} else if (entity instanceof SpiderEntity) {
				this.loadShader(new Identifier("shaders/post/spider.json"));
			} else if (entity instanceof EndermanEntity) {
				this.loadShader(new Identifier("shaders/post/invert.json"));
			}
		}
	}

	private void loadShader(Identifier id) {
		try {
			this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceManager, this.client.getFramebuffer(), id);
			this.shader.setupDimensions(this.client.width, this.client.height);
			this.shadersEnabled = true;
		} catch (IOException var3) {
			LOGGER.warn("Failed to load shader: " + id, var3);
			this.forcedShaderIndex = SHADER_COUNT;
			this.shadersEnabled = false;
		} catch (JsonSyntaxException var4) {
			LOGGER.warn("Failed to load shader: " + id, var4);
			this.forcedShaderIndex = SHADER_COUNT;
			this.shadersEnabled = false;
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		if (this.shader != null) {
			this.shader.disable();
		}

		this.shader = null;
		if (this.forcedShaderIndex != SHADER_COUNT) {
			this.loadShader(SHADERS_LOCATIONS[this.forcedShaderIndex]);
		} else {
			this.onCameraEntitySet(this.client.getCameraEntity());
		}
	}

	public void tick() {
		if (GLX.shadersSupported && GlProgramManager.getInstance() == null) {
			GlProgramManager.newInstance();
		}

		this.updateMovementFovMultiplier();
		this.tickLightmap();
		this.prevFogColor = this.fogColor;
		this.lastThirdPersonDistance = this.thirdPersonDistance;
		if (this.client.options.smoothCameraEnabled) {
			float f = this.client.options.sensitivity * 0.6F + 0.2F;
			float g = f * f * f * 8.0F;
			this.smoothedCursorDeltaX = this.cursorXSmoother.smooth(this.cursorDeltaX, 0.05F * g);
			this.smoothedCursorDeltaY = this.cursorYSmoother.smooth(this.cursorDeltaY, 0.05F * g);
			this.lastTickDelta = 0.0F;
			this.cursorDeltaX = 0.0F;
			this.cursorDeltaY = 0.0F;
		} else {
			this.smoothedCursorDeltaX = 0.0F;
			this.smoothedCursorDeltaY = 0.0F;
			this.cursorXSmoother.clear();
			this.cursorYSmoother.clear();
		}

		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		float h = this.client.world.getBrightness(new BlockPos(this.client.getCameraEntity()));
		float i = (float)this.client.options.viewDistance / 32.0F;
		float j = h * (1.0F - i) + i;
		this.fogColor = this.fogColor + (j - this.fogColor) * 0.1F;
		this.ticks++;
		this.firstPersonRenderer.updateHeldItems();
		this.tickRainSplashing();
		this.lastSkyDarkness = this.skyDarkness;
		if (this.client.inGameHud.method_12167().method_12173()) {
			this.skyDarkness += 0.05F;
			if (this.skyDarkness > 1.0F) {
				this.skyDarkness = 1.0F;
			}
		} else if (this.skyDarkness > 0.0F) {
			this.skyDarkness -= 0.0125F;
		}
	}

	public ShaderEffect getShader() {
		return this.shader;
	}

	public void onResized(int width, int height) {
		if (GLX.shadersSupported) {
			if (this.shader != null) {
				this.shader.setupDimensions(width, height);
			}

			this.client.worldRenderer.onResized(width, height);
		}
	}

	public void updateTargetedEntity(float tickDelta) {
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			if (this.client.world != null) {
				this.client.profiler.push("pick");
				this.client.targetedEntity = null;
				double d = (double)this.client.interactionManager.getReachDistance();
				this.client.result = entity.rayTrace(d, tickDelta);
				double e = d;
				Vec3d vec3d = entity.getCameraPosVec(tickDelta);
				boolean bl = false;
				int i = 3;
				if (this.client.interactionManager.hasExtendedReach()) {
					d = 6.0;
					e = 6.0;
				} else {
					if (d > 3.0) {
						bl = true;
					}

					d = d;
				}

				if (this.client.result != null) {
					e = this.client.result.pos.distanceTo(vec3d);
				}

				Vec3d vec3d2 = entity.getRotationVector(tickDelta);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				this.targetedEntity = null;
				Vec3d vec3d4 = null;
				float f = 1.0F;
				List<Entity> list = this.client
					.world
					.getEntitiesIn(
						entity,
						entity.getBoundingBox().stretch(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).expand((double)f, (double)f, (double)f),
						Predicates.and(EntityPredicate.EXCEPT_SPECTATOR, new Predicate<Entity>() {
							public boolean apply(@Nullable Entity entity) {
								return entity != null && entity.collides();
							}
						})
					);
				double g = e;

				for (int j = 0; j < list.size(); j++) {
					Entity entity2 = (Entity)list.get(j);
					Box box = entity2.getBoundingBox().expand((double)entity2.getTargetingMargin());
					BlockHitResult blockHitResult = box.method_585(vec3d, vec3d3);
					if (box.contains(vec3d)) {
						if (g >= 0.0) {
							this.targetedEntity = entity2;
							vec3d4 = blockHitResult == null ? vec3d : blockHitResult.pos;
							g = 0.0;
						}
					} else if (blockHitResult != null) {
						double h = vec3d.distanceTo(blockHitResult.pos);
						if (h < g || g == 0.0) {
							if (entity2.getRootVehicle() == entity.getRootVehicle()) {
								if (g == 0.0) {
									this.targetedEntity = entity2;
									vec3d4 = blockHitResult.pos;
								}
							} else {
								this.targetedEntity = entity2;
								vec3d4 = blockHitResult.pos;
								g = h;
							}
						}
					}
				}

				if (this.targetedEntity != null && bl && vec3d.distanceTo(vec3d4) > 3.0) {
					this.targetedEntity = null;
					this.client.result = new BlockHitResult(BlockHitResult.Type.MISS, vec3d4, null, new BlockPos(vec3d4));
				}

				if (this.targetedEntity != null && (g < e || this.client.result == null)) {
					this.client.result = new BlockHitResult(this.targetedEntity, vec3d4);
					if (this.targetedEntity instanceof LivingEntity || this.targetedEntity instanceof ItemFrameEntity) {
						this.client.targetedEntity = this.targetedEntity;
					}
				}

				this.client.profiler.pop();
			}
		}
	}

	private void updateMovementFovMultiplier() {
		float f = 1.0F;
		if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.client.getCameraEntity();
			f = abstractClientPlayerEntity.getSpeed();
		}

		this.lastMovementFovMultiplier = this.movementFovMultiplier;
		this.movementFovMultiplier = this.movementFovMultiplier + (f - this.movementFovMultiplier) * 0.5F;
		if (this.movementFovMultiplier > 1.5F) {
			this.movementFovMultiplier = 1.5F;
		}

		if (this.movementFovMultiplier < 0.1F) {
			this.movementFovMultiplier = 0.1F;
		}
	}

	private float getFov(float tickDelta, boolean changingFov) {
		if (this.renderingPanorama) {
			return 90.0F;
		} else {
			Entity entity = this.client.getCameraEntity();
			float f = 70.0F;
			if (changingFov) {
				f = this.client.options.fov;
				f *= this.lastMovementFovMultiplier + (this.movementFovMultiplier - this.lastMovementFovMultiplier) * tickDelta;
			}

			if (entity instanceof LivingEntity && ((LivingEntity)entity).getHealth() <= 0.0F) {
				float g = (float)((LivingEntity)entity).deathTime + tickDelta;
				f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
			}

			BlockState blockState = Camera.method_9371(this.client.world, entity, tickDelta);
			if (blockState.getMaterial() == Material.WATER) {
				f = f * 60.0F / 70.0F;
			}

			return f;
		}
	}

	private void bobViewWhenHurt(float tickDelta) {
		if (this.client.getCameraEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.client.getCameraEntity();
			float f = (float)livingEntity.hurtTime - tickDelta;
			if (livingEntity.getHealth() <= 0.0F) {
				float g = (float)livingEntity.deathTime + tickDelta;
				GlStateManager.rotate(40.0F - 8000.0F / (g + 200.0F), 0.0F, 0.0F, 1.0F);
			}

			if (f < 0.0F) {
				return;
			}

			f /= (float)livingEntity.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float h = livingEntity.knockbackVelocity;
			GlStateManager.rotate(-h, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(h, 0.0F, 1.0F, 0.0F);
		}
	}

	private void bobView(float tickDelta) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float g = -(playerEntity.horizontalSpeed + f * tickDelta);
			float h = playerEntity.prevStrideDistance + (playerEntity.strideDistance - playerEntity.prevStrideDistance) * tickDelta;
			float i = playerEntity.field_6752 + (playerEntity.field_6753 - playerEntity.field_6752) * tickDelta;
			GlStateManager.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
			GlStateManager.rotate(MathHelper.sin(g * (float) Math.PI) * h * 3.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(i, 1.0F, 0.0F, 0.0F);
		}
	}

	private void transformCamera(float tickDelta) {
		Entity entity = this.client.getCameraEntity();
		float f = entity.getEyeHeight();
		double d = entity.prevX + (entity.x - entity.prevX) * (double)tickDelta;
		double e = entity.prevY + (entity.y - entity.prevY) * (double)tickDelta + (double)f;
		double g = entity.prevZ + (entity.z - entity.prevZ) * (double)tickDelta;
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			f = (float)((double)f + 1.0);
			GlStateManager.translate(0.0F, 0.3F, 0.0F);
			if (!this.client.options.field_955) {
				BlockPos blockPos = new BlockPos(entity);
				BlockState blockState = this.client.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block == Blocks.BED) {
					int i = ((Direction)blockState.get(BedBlock.DIRECTION)).getHorizontal();
					GlStateManager.rotate((float)(i * 90), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.prevYaw + (entity.yaw - entity.prevYaw) * tickDelta + 180.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.rotate(entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta, -1.0F, 0.0F, 0.0F);
			}
		} else if (this.client.options.perspective > 0) {
			double h = (double)(this.lastThirdPersonDistance + (this.thirdPersonDistance - this.lastThirdPersonDistance) * tickDelta);
			if (this.client.options.field_955) {
				GlStateManager.translate(0.0F, 0.0F, (float)(-h));
			} else {
				float j = entity.yaw;
				float k = entity.pitch;
				if (this.client.options.perspective == 2) {
					k += 180.0F;
				}

				double l = (double)(-MathHelper.sin(j * (float) (Math.PI / 180.0)) * MathHelper.cos(k * (float) (Math.PI / 180.0))) * h;
				double m = (double)(MathHelper.cos(j * (float) (Math.PI / 180.0)) * MathHelper.cos(k * (float) (Math.PI / 180.0))) * h;
				double n = (double)(-MathHelper.sin(k * (float) (Math.PI / 180.0))) * h;

				for (int o = 0; o < 8; o++) {
					float p = (float)((o & 1) * 2 - 1);
					float q = (float)((o >> 1 & 1) * 2 - 1);
					float r = (float)((o >> 2 & 1) * 2 - 1);
					p *= 0.1F;
					q *= 0.1F;
					r *= 0.1F;
					BlockHitResult blockHitResult = this.client
						.world
						.rayTrace(new Vec3d(d + (double)p, e + (double)q, g + (double)r), new Vec3d(d - l + (double)p + (double)r, e - n + (double)q, g - m + (double)r));
					if (blockHitResult != null) {
						double s = blockHitResult.pos.distanceTo(new Vec3d(d, e, g));
						if (s < h) {
							h = s;
						}
					}
				}

				if (this.client.options.perspective == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.pitch - k, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.yaw - j, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, 0.0F, (float)(-h));
				GlStateManager.rotate(j - entity.yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(k - entity.pitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.translate(0.0F, 0.0F, 0.05F);
		}

		if (!this.client.options.field_955) {
			GlStateManager.rotate(entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta, 1.0F, 0.0F, 0.0F);
			if (entity instanceof AnimalEntity) {
				AnimalEntity animalEntity = (AnimalEntity)entity;
				GlStateManager.rotate(animalEntity.prevHeadYaw + (animalEntity.headYaw - animalEntity.prevHeadYaw) * tickDelta + 180.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.rotate(entity.prevYaw + (entity.yaw - entity.prevYaw) * tickDelta + 180.0F, 0.0F, 1.0F, 0.0F);
			}
		}

		GlStateManager.translate(0.0F, -f, 0.0F);
		d = entity.prevX + (entity.x - entity.prevX) * (double)tickDelta;
		e = entity.prevY + (entity.y - entity.prevY) * (double)tickDelta + (double)f;
		g = entity.prevZ + (entity.z - entity.prevZ) * (double)tickDelta;
		this.thickFog = this.client.worldRenderer.hasThickFog(d, e, g, tickDelta);
	}

	private void setupCamera(float tickDelta, int anaglyphFilter) {
		this.viewDistance = (float)(this.client.options.viewDistance * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		float f = 0.07F;
		if (this.client.options.anaglyph3d) {
			GlStateManager.translate((float)(-(anaglyphFilter * 2 - 1)) * f, 0.0F, 0.0F);
		}

		if (this.zoom != 1.0) {
			GlStateManager.translate((float)this.zoomX, (float)(-this.zoomY), 0.0F);
			GlStateManager.scale(this.zoom, this.zoom, 1.0);
		}

		Project.gluPerspective(
			this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		if (this.client.options.anaglyph3d) {
			GlStateManager.translate((float)(anaglyphFilter * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		this.bobViewWhenHurt(tickDelta);
		if (this.client.options.bobView) {
			this.bobView(tickDelta);
		}

		float g = this.client.player.lastTimeInPortal + (this.client.player.timeInPortal - this.client.player.lastTimeInPortal) * tickDelta;
		if (g > 0.0F) {
			int i = 20;
			if (this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
				i = 7;
			}

			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			GlStateManager.rotate(((float)this.ticks + tickDelta) * (float)i, 0.0F, 1.0F, 1.0F);
			GlStateManager.scale(1.0F / h, 1.0F, 1.0F);
			GlStateManager.rotate(-((float)this.ticks + tickDelta) * (float)i, 0.0F, 1.0F, 1.0F);
		}

		this.transformCamera(tickDelta);
		if (this.renderingPanorama) {
			switch (this.panoramaDirection) {
				case 0:
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
					break;
				case 1:
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					break;
				case 2:
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
					break;
				case 3:
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					break;
				case 4:
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			}
		}
	}

	private void renderHand(float tickDelta, int anaglyphOffset) {
		if (!this.renderingPanorama) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			float f = 0.07F;
			if (this.client.options.anaglyph3d) {
				GlStateManager.translate((float)(-(anaglyphOffset * 2 - 1)) * f, 0.0F, 0.0F);
			}

			Project.gluPerspective(this.getFov(tickDelta, false), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * 2.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			if (this.client.options.anaglyph3d) {
				GlStateManager.translate((float)(anaglyphOffset * 2 - 1) * 0.1F, 0.0F, 0.0F);
			}

			GlStateManager.pushMatrix();
			this.bobViewWhenHurt(tickDelta);
			if (this.client.options.bobView) {
				this.bobView(tickDelta);
			}

			boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
			if (this.client.options.perspective == 0 && !bl && !this.client.options.hudHidden && !this.client.interactionManager.isSpectator()) {
				this.enableLightmap();
				this.firstPersonRenderer.renderArmHoldingItem(tickDelta);
				this.disableLightmap();
			}

			GlStateManager.popMatrix();
			if (this.client.options.perspective == 0 && !bl) {
				this.firstPersonRenderer.renderOverlays(tickDelta);
				this.bobViewWhenHurt(tickDelta);
			}

			if (this.client.options.bobView) {
				this.bobView(tickDelta);
			}
		}
	}

	public void disableLightmap() {
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	public void enableLightmap() {
		GlStateManager.activeTexture(GLX.lightmapTextureUnit);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = 0.00390625F;
		GlStateManager.scale(f, f, f);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		this.client.getTextureManager().bindTexture(this.lightmapTextureId);
		GlStateManager.method_12294(3553, 10241, 9729);
		GlStateManager.method_12294(3553, 10240, 9729);
		GlStateManager.method_12294(3553, 10242, 10496);
		GlStateManager.method_12294(3553, 10243, 10496);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.textureUnit);
	}

	private void tickLightmap() {
		this.lastLightmapFlicker = (float)((double)this.lastLightmapFlicker + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.lastLightmapFlicker = (float)((double)this.lastLightmapFlicker * 0.9);
		this.lightmapFlicker = this.lightmapFlicker + (this.lastLightmapFlicker - this.lightmapFlicker);
		this.lightmapDirty = true;
	}

	private void updateLightmap(float tickDelta) {
		if (this.lightmapDirty) {
			this.client.profiler.push("lightTex");
			World world = this.client.world;
			if (world != null) {
				float f = world.method_3649(1.0F);
				float g = f * 0.95F + 0.05F;

				for (int i = 0; i < 256; i++) {
					float h = world.dimension.getLightLevelToBrightness()[i / 16] * g;
					float j = world.dimension.getLightLevelToBrightness()[i % 16] * (this.lightmapFlicker * 0.1F + 1.5F);
					if (world.getLightningTicksLeft() > 0) {
						h = world.dimension.getLightLevelToBrightness()[i / 16];
					}

					float k = h * (f * 0.65F + 0.35F);
					float l = h * (f * 0.65F + 0.35F);
					float o = j * ((j * 0.6F + 0.4F) * 0.6F + 0.4F);
					float p = j * (j * j * 0.6F + 0.4F);
					float q = k + j;
					float r = l + o;
					float s = h + p;
					q = q * 0.96F + 0.03F;
					r = r * 0.96F + 0.03F;
					s = s * 0.96F + 0.03F;
					if (this.skyDarkness > 0.0F) {
						float t = this.lastSkyDarkness + (this.skyDarkness - this.lastSkyDarkness) * tickDelta;
						q = q * (1.0F - t) + q * 0.7F * t;
						r = r * (1.0F - t) + r * 0.6F * t;
						s = s * (1.0F - t) + s * 0.6F * t;
					}

					if (world.dimension.getDimensionType().getId() == 1) {
						q = 0.22F + j * 0.75F;
						r = 0.28F + o * 0.75F;
						s = 0.25F + p * 0.75F;
					}

					if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
						float u = this.getNightVisionStrength(this.client.player, tickDelta);
						float v = 1.0F / q;
						if (v > 1.0F / r) {
							v = 1.0F / r;
						}

						if (v > 1.0F / s) {
							v = 1.0F / s;
						}

						q = q * (1.0F - u) + q * v * u;
						r = r * (1.0F - u) + r * v * u;
						s = s * (1.0F - u) + s * v * u;
					}

					if (q > 1.0F) {
						q = 1.0F;
					}

					if (r > 1.0F) {
						r = 1.0F;
					}

					if (s > 1.0F) {
						s = 1.0F;
					}

					float w = this.client.options.gamma;
					float x = 1.0F - q;
					float y = 1.0F - r;
					float z = 1.0F - s;
					x = 1.0F - x * x * x * x;
					y = 1.0F - y * y * y * y;
					z = 1.0F - z * z * z * z;
					q = q * (1.0F - w) + x * w;
					r = r * (1.0F - w) + y * w;
					s = s * (1.0F - w) + z * w;
					q = q * 0.96F + 0.03F;
					r = r * 0.96F + 0.03F;
					s = s * 0.96F + 0.03F;
					if (q > 1.0F) {
						q = 1.0F;
					}

					if (r > 1.0F) {
						r = 1.0F;
					}

					if (s > 1.0F) {
						s = 1.0F;
					}

					if (q < 0.0F) {
						q = 0.0F;
					}

					if (r < 0.0F) {
						r = 0.0F;
					}

					if (s < 0.0F) {
						s = 0.0F;
					}

					int aa = 255;
					int ab = (int)(q * 255.0F);
					int ac = (int)(r * 255.0F);
					int ad = (int)(s * 255.0F);
					this.lightmapTexturePixels[i] = aa << 24 | ab << 16 | ac << 8 | ad;
				}

				this.lightmapTexture.upload();
				this.lightmapDirty = false;
				this.client.profiler.pop();
			}
		}
	}

	private float getNightVisionStrength(LivingEntity entity, float tickDelta) {
		int i = entity.getEffectInstance(StatusEffects.NIGHT_VISION).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - tickDelta) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void render(float tickDelta, long nanoTime) {
		boolean bl = Display.isActive();
		if (!bl && this.client.options.pauseOnLostFocus && (!this.client.options.touchscreen || !Mouse.isButtonDown(1))) {
			if (MinecraftClient.getTime() - this.lastWindowFocusedTime > 500L) {
				this.client.openGameMenuScreen();
			}
		} else {
			this.lastWindowFocusedTime = MinecraftClient.getTime();
		}

		this.client.profiler.push("mouse");
		if (bl && MinecraftClient.IS_MAC && this.client.focused && !Mouse.isInsideWindow()) {
			Mouse.setGrabbed(false);
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2 - 20);
			Mouse.setGrabbed(true);
		}

		if (this.client.focused && bl) {
			this.client.mouse.updateMouse();
			float f = this.client.options.sensitivity * 0.6F + 0.2F;
			float g = f * f * f * 8.0F;
			float h = (float)this.client.mouse.x * g;
			float i = (float)this.client.mouse.y * g;
			int j = 1;
			if (this.client.options.invertYMouse) {
				j = -1;
			}

			if (this.client.options.smoothCameraEnabled) {
				this.cursorDeltaX += h;
				this.cursorDeltaY += i;
				float k = tickDelta - this.lastTickDelta;
				this.lastTickDelta = tickDelta;
				h = this.smoothedCursorDeltaX * k;
				i = this.smoothedCursorDeltaY * k;
				this.client.player.increaseTransforms(h, i * (float)j);
			} else {
				this.cursorDeltaX = 0.0F;
				this.cursorDeltaY = 0.0F;
				this.client.player.increaseTransforms(h, i * (float)j);
			}
		}

		this.client.profiler.pop();
		if (!this.client.skipGameRender) {
			anaglyphEnabled = this.client.options.anaglyph3d;
			final Window window = new Window(this.client);
			int l = window.getWidth();
			int m = window.getHeight();
			final int n = Mouse.getX() * l / this.client.width;
			final int o = m - Mouse.getY() * m / this.client.height - 1;
			int p = this.client.options.maxFramerate;
			if (this.client.world != null) {
				this.client.profiler.push("level");
				int q = Math.min(MinecraftClient.getCurrentFps(), p);
				q = Math.max(q, 60);
				long r = System.nanoTime() - nanoTime;
				long s = Math.max((long)(1000000000 / q / 4) - r, 0L);
				this.renderWorld(tickDelta, System.nanoTime() + s);
				if (this.client.isInSingleplayer() && this.field_13463 < MinecraftClient.getTime() - 1000L) {
					this.field_13463 = MinecraftClient.getTime();
					if (!this.client.getServer().method_12837()) {
						this.method_12268();
					}
				}

				if (GLX.shadersSupported) {
					this.client.worldRenderer.drawEntityOutlineFramebuffer();
					if (this.shader != null && this.shadersEnabled) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.shader.render(tickDelta);
						GlStateManager.popMatrix();
					}

					this.client.getFramebuffer().bind(true);
				}

				this.lastWorldRenderNanoTime = System.nanoTime();
				this.client.profiler.swap("gui");
				if (!this.client.options.hudHidden || this.client.currentScreen != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.client.inGameHud.render(tickDelta);
				}

				this.client.profiler.pop();
			} else {
				GlStateManager.viewport(0, 0, this.client.width, this.client.height);
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.setupHudMatrixMode();
				this.lastWorldRenderNanoTime = System.nanoTime();
			}

			if (this.client.currentScreen != null) {
				GlStateManager.clear(256);

				try {
					this.client.currentScreen.render(n, o, tickDelta);
				} catch (Throwable var16) {
					CrashReport crashReport = CrashReport.create(var16, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
					crashReportSection.add("Screen name", new CrashCallable<String>() {
						public String call() throws Exception {
							return GameRenderer.this.client.currentScreen.getClass().getCanonicalName();
						}
					});
					crashReportSection.add("Mouse location", new CrashCallable<String>() {
						public String call() throws Exception {
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", n, o, Mouse.getX(), Mouse.getY());
						}
					});
					crashReportSection.add(
						"Screen size",
						new CrashCallable<String>() {
							public String call() throws Exception {
								return String.format(
									"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
									window.getWidth(),
									window.getHeight(),
									GameRenderer.this.client.width,
									GameRenderer.this.client.height,
									window.getScaleFactor()
								);
							}
						}
					);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void method_12268() {
		if (this.client.worldRenderer.method_12338() > 10 && this.client.worldRenderer.method_12339() && !this.client.getServer().method_12837()) {
			BufferedImage bufferedImage = ScreenshotUtils.method_12153(this.client.width, this.client.height, this.client.getFramebuffer());
			int i = bufferedImage.getWidth();
			int j = bufferedImage.getHeight();
			int k = 0;
			int l = 0;
			if (i > j) {
				k = (i - j) / 2;
				i = j;
			} else {
				l = (j - i) / 2;
			}

			try {
				BufferedImage bufferedImage2 = new BufferedImage(64, 64, 1);
				Graphics graphics = bufferedImage2.createGraphics();
				graphics.drawImage(bufferedImage, 0, 0, 64, 64, k, l, k + i, l + i, null);
				graphics.dispose();
				ImageIO.write(bufferedImage2, "png", this.client.getServer().method_12838());
			} catch (IOException var8) {
				LOGGER.warn("Couldn't save auto screenshot", var8);
			}
		}
	}

	public void renderStreamIndicator(float tickDelta) {
		this.setupHudMatrixMode();
	}

	private boolean shouldRenderBlockOutline() {
		if (!this.blockOutlineEnabled) {
			return false;
		} else {
			Entity entity = this.client.getCameraEntity();
			boolean bl = entity instanceof PlayerEntity && !this.client.options.hudHidden;
			if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
				ItemStack itemStack = ((PlayerEntity)entity).getMainHandStack();
				if (this.client.result != null && this.client.result.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = this.client.result.getBlockPos();
					Block block = this.client.world.getBlockState(blockPos).getBlock();
					if (this.client.interactionManager.getCurrentGameMode() == LevelInfo.GameMode.SPECTATOR) {
						bl = block.hasBlockEntity() && this.client.world.getBlockEntity(blockPos) instanceof Inventory;
					} else {
						bl = itemStack != null && (itemStack.canDestroy(block) || itemStack.canPlaceOn(block));
					}
				}
			}

			return bl;
		}
	}

	public void renderWorld(float tickDelta, long limitTime) {
		this.updateLightmap(tickDelta);
		if (this.client.getCameraEntity() == null) {
			this.client.setCameraEntity(this.client.player);
		}

		this.updateTargetedEntity(tickDelta);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.5F);
		this.client.profiler.push("center");
		if (this.client.options.anaglyph3d) {
			anaglyphFilter = 0;
			GlStateManager.colorMask(false, true, true, false);
			this.renderWorld(0, tickDelta, limitTime);
			anaglyphFilter = 1;
			GlStateManager.colorMask(true, false, false, false);
			this.renderWorld(1, tickDelta, limitTime);
			GlStateManager.colorMask(true, true, true, false);
		} else {
			this.renderWorld(2, tickDelta, limitTime);
		}

		this.client.profiler.pop();
	}

	private void renderWorld(int anaglyphFilter, float tickDelta, long limitTime) {
		WorldRenderer worldRenderer = this.client.worldRenderer;
		ParticleManager particleManager = this.client.particleManager;
		boolean bl = this.shouldRenderBlockOutline();
		GlStateManager.enableCull();
		this.client.profiler.swap("clear");
		GlStateManager.viewport(0, 0, this.client.width, this.client.height);
		this.updateFog(tickDelta);
		GlStateManager.clear(16640);
		this.client.profiler.swap("camera");
		this.setupCamera(tickDelta, anaglyphFilter);
		Camera.update(this.client.player, this.client.options.perspective == 2);
		this.client.profiler.swap("frustum");
		Frustum.getInstance();
		this.client.profiler.swap("culling");
		CameraView cameraView = new CullingCameraView();
		Entity entity = this.client.getCameraEntity();
		double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
		double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
		double f = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
		cameraView.setPos(d, e, f);
		if (this.client.options.viewDistance >= 4) {
			this.renderFog(-1, tickDelta);
			this.client.profiler.swap("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * 2.0F);
			GlStateManager.matrixMode(5888);
			worldRenderer.renderSky(tickDelta, anaglyphFilter);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(
				this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
			);
			GlStateManager.matrixMode(5888);
		}

		this.renderFog(0, tickDelta);
		GlStateManager.shadeModel(7425);
		if (entity.y + (double)entity.getEyeHeight() < 128.0) {
			this.renderClouds(worldRenderer, tickDelta, anaglyphFilter);
		}

		this.client.profiler.swap("prepareterrain");
		this.renderFog(0, tickDelta);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		DiffuseLighting.disable();
		this.client.profiler.swap("terrain_setup");
		worldRenderer.setupTerrain(entity, (double)tickDelta, cameraView, this.frameCount++, this.client.player.isSpectator());
		if (anaglyphFilter == 0 || anaglyphFilter == 2) {
			this.client.profiler.swap("updatechunks");
			this.client.worldRenderer.updateChunks(limitTime);
		}

		this.client.profiler.swap("terrain");
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlphaTest();
		worldRenderer.renderLayer(RenderLayer.SOLID, (double)tickDelta, anaglyphFilter, entity);
		GlStateManager.enableAlphaTest();
		worldRenderer.renderLayer(RenderLayer.CUTOUT_MIPPED, (double)tickDelta, anaglyphFilter, entity);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.renderLayer(RenderLayer.CUTOUT, (double)tickDelta, anaglyphFilter, entity);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);
		if (!this.renderingPanorama) {
			GlStateManager.matrixMode(5888);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			DiffuseLighting.enableNormally();
			this.client.profiler.swap("entities");
			worldRenderer.renderEntities(entity, cameraView, tickDelta);
			DiffuseLighting.disable();
			this.disableLightmap();
		}

		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		if (bl && this.client.result != null && !entity.isSubmergedIn(Material.WATER)) {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			GlStateManager.disableAlphaTest();
			this.client.profiler.swap("outline");
			worldRenderer.drawBlockOutline(playerEntity, this.client.result, 0, tickDelta);
			GlStateManager.enableAlphaTest();
		}

		this.client.profiler.swap("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.method_12288(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.drawBlockDamage(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, tickDelta);
		this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
		GlStateManager.disableBlend();
		if (!this.renderingPanorama) {
			this.enableLightmap();
			this.client.profiler.swap("litParticles");
			particleManager.method_1299(entity, tickDelta);
			DiffuseLighting.disable();
			this.renderFog(0, tickDelta);
			this.client.profiler.swap("particles");
			particleManager.renderParticles(entity, tickDelta);
			this.disableLightmap();
		}

		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.client.profiler.swap("weather");
		this.renderWeather(tickDelta);
		GlStateManager.depthMask(true);
		worldRenderer.renderWorldBorder(entity, tickDelta);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.alphaFunc(516, 0.1F);
		this.renderFog(0, tickDelta);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.shadeModel(7425);
		this.client.profiler.swap("translucent");
		worldRenderer.renderLayer(RenderLayer.TRANSLUCENT, (double)tickDelta, anaglyphFilter, entity);
		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();
		if (entity.y + (double)entity.getEyeHeight() >= 128.0) {
			this.client.profiler.swap("aboveClouds");
			this.renderClouds(worldRenderer, tickDelta, anaglyphFilter);
		}

		this.client.profiler.swap("hand");
		if (this.renderHand) {
			GlStateManager.clear(256);
			this.renderHand(tickDelta, anaglyphFilter);
		}
	}

	private void renderClouds(WorldRenderer worldRenderer, float tickDelta, int anaglyphFilter) {
		if (this.client.options.getCloudMode() != 0) {
			this.client.profiler.swap("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * 4.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.renderFog(0, tickDelta);
			worldRenderer.renderClouds(tickDelta, anaglyphFilter);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(
				this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO
			);
			GlStateManager.matrixMode(5888);
		}
	}

	private void tickRainSplashing() {
		float f = this.client.world.getRainGradient(1.0F);
		if (!this.client.options.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			this.random.setSeed((long)this.ticks * 312987231L);
			Entity entity = this.client.getCameraEntity();
			World world = this.client.world;
			BlockPos blockPos = new BlockPos(entity);
			int i = 10;
			double d = 0.0;
			double e = 0.0;
			double g = 0.0;
			int j = 0;
			int k = (int)(100.0F * f * f);
			if (this.client.options.particle == 1) {
				k >>= 1;
			} else if (this.client.options.particle == 2) {
				k = 0;
			}

			for (int l = 0; l < k; l++) {
				BlockPos blockPos2 = world.method_8562(blockPos.add(this.random.nextInt(i) - this.random.nextInt(i), 0, this.random.nextInt(i) - this.random.nextInt(i)));
				Biome biome = world.getBiome(blockPos2);
				BlockPos blockPos3 = blockPos2.down();
				BlockState blockState = world.getBlockState(blockPos3);
				if (blockPos2.getY() <= blockPos.getY() + i && blockPos2.getY() >= blockPos.getY() - i && biome.method_3830() && biome.getTemperature(blockPos2) >= 0.15F) {
					double h = this.random.nextDouble();
					double m = this.random.nextDouble();
					Box box = blockState.getCollisionBox((BlockView)world, blockPos3);
					if (blockState.getMaterial() == Material.LAVA) {
						this.client
							.world
							.addParticle(
								ParticleType.SMOKE,
								(double)blockPos2.getX() + h,
								(double)((float)blockPos2.getY() + 0.1F) - box.minY,
								(double)blockPos2.getZ() + m,
								0.0,
								0.0,
								0.0,
								new int[0]
							);
					} else if (blockState.getMaterial() != Material.AIR) {
						if (this.random.nextInt(++j) == 0) {
							d = (double)blockPos3.getX() + h;
							e = (double)((float)blockPos3.getY() + 0.1F) + box.maxY - 1.0;
							g = (double)blockPos3.getZ() + m;
						}

						this.client
							.world
							.addParticle(
								ParticleType.WATER_DROP,
								(double)blockPos3.getX() + h,
								(double)((float)blockPos3.getY() + 0.1F) + box.maxY,
								(double)blockPos3.getZ() + m,
								0.0,
								0.0,
								0.0,
								new int[0]
							);
					}
				}
			}

			if (j > 0 && this.random.nextInt(3) < this.weatherSoundAttempts++) {
				this.weatherSoundAttempts = 0;
				if (e > (double)(blockPos.getY() + 1) && world.method_8562(blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					this.client.world.playSound(d, e, g, Sounds.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
				} else {
					this.client.world.playSound(d, e, g, Sounds.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}
	}

	protected void renderWeather(float tickDelta) {
		float f = this.client.world.getRainGradient(tickDelta);
		if (!(f <= 0.0F)) {
			this.enableLightmap();
			Entity entity = this.client.getCameraEntity();
			World world = this.client.world;
			int i = MathHelper.floor(entity.x);
			int j = MathHelper.floor(entity.y);
			int k = MathHelper.floor(entity.z);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			GlStateManager.disableCull();
			GlStateManager.method_12272(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			GlStateManager.alphaFunc(516, 0.1F);
			double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
			double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
			double g = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
			int l = MathHelper.floor(e);
			int m = 5;
			if (this.client.options.fancyGraphics) {
				m = 10;
			}

			int n = -1;
			float h = (float)this.ticks + tickDelta;
			bufferBuilder.offset(-d, -e, -g);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int o = k - m; o <= k + m; o++) {
				for (int p = i - m; p <= i + m; p++) {
					int q = (o - k + 16) * 32 + p - i + 16;
					double r = (double)this.rainOffsetX[q] * 0.5;
					double s = (double)this.rainOffsetY[q] * 0.5;
					mutable.setPosition(p, 0, o);
					Biome biome = world.getBiome(mutable);
					if (biome.method_3830() || biome.isMutated()) {
						int t = world.method_8562(mutable).getY();
						int u = j - m;
						int v = j + m;
						if (u < t) {
							u = t;
						}

						if (v < t) {
							v = t;
						}

						int w = t;
						if (t < l) {
							w = l;
						}

						if (u != v) {
							this.random.setSeed((long)(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761));
							mutable.setPosition(p, u, o);
							float x = biome.getTemperature(mutable);
							if (world.method_3726().method_11533(x, t) >= 0.15F) {
								if (n != 0) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 0;
									this.client.getTextureManager().bindTexture(RAIN_TEXTURE);
									bufferBuilder.begin(7, VertexFormats.PARTICLE);
								}

								double y = -((double)(this.ticks + p * p * 3121 + p * 45238971 + o * o * 418711 + o * 13761 & 31) + (double)tickDelta)
									/ 32.0
									* (3.0 + this.random.nextDouble());
								double z = (double)((float)p + 0.5F) - entity.x;
								double aa = (double)((float)o + 0.5F) - entity.z;
								float ab = MathHelper.sqrt(z * z + aa * aa) / (float)m;
								float ac = ((1.0F - ab * ab) * 0.5F + 0.5F) * f;
								mutable.setPosition(p, w, o);
								int ad = world.getLight(mutable, 0);
								int ae = ad >> 16 & 65535;
								int af = ad & 65535;
								bufferBuilder.vertex((double)p - r + 0.5, (double)v, (double)o - s + 0.5)
									.texture(0.0, (double)u * 0.25 + y)
									.color(1.0F, 1.0F, 1.0F, ac)
									.texture2(ae, af)
									.next();
								bufferBuilder.vertex((double)p + r + 0.5, (double)v, (double)o + s + 0.5)
									.texture(1.0, (double)u * 0.25 + y)
									.color(1.0F, 1.0F, 1.0F, ac)
									.texture2(ae, af)
									.next();
								bufferBuilder.vertex((double)p + r + 0.5, (double)u, (double)o + s + 0.5)
									.texture(1.0, (double)v * 0.25 + y)
									.color(1.0F, 1.0F, 1.0F, ac)
									.texture2(ae, af)
									.next();
								bufferBuilder.vertex((double)p - r + 0.5, (double)u, (double)o - s + 0.5)
									.texture(0.0, (double)v * 0.25 + y)
									.color(1.0F, 1.0F, 1.0F, ac)
									.texture2(ae, af)
									.next();
							} else {
								if (n != 1) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 1;
									this.client.getTextureManager().bindTexture(SNOW_TEXTURE);
									bufferBuilder.begin(7, VertexFormats.PARTICLE);
								}

								double ag = (double)(-((float)(this.ticks & 511) + tickDelta) / 512.0F);
								double ah = this.random.nextDouble() + (double)h * 0.01 * (double)((float)this.random.nextGaussian());
								double ai = this.random.nextDouble() + (double)(h * (float)this.random.nextGaussian()) * 0.001;
								double aj = (double)((float)p + 0.5F) - entity.x;
								double ak = (double)((float)o + 0.5F) - entity.z;
								float al = MathHelper.sqrt(aj * aj + ak * ak) / (float)m;
								float am = ((1.0F - al * al) * 0.3F + 0.5F) * f;
								mutable.setPosition(p, w, o);
								int an = (world.getLight(mutable, 0) * 3 + 15728880) / 4;
								int ao = an >> 16 & 65535;
								int ap = an & 65535;
								bufferBuilder.vertex((double)p - r + 0.5, (double)v, (double)o - s + 0.5)
									.texture(0.0 + ah, (double)u * 0.25 + ag + ai)
									.color(1.0F, 1.0F, 1.0F, am)
									.texture2(ao, ap)
									.next();
								bufferBuilder.vertex((double)p + r + 0.5, (double)v, (double)o + s + 0.5)
									.texture(1.0 + ah, (double)u * 0.25 + ag + ai)
									.color(1.0F, 1.0F, 1.0F, am)
									.texture2(ao, ap)
									.next();
								bufferBuilder.vertex((double)p + r + 0.5, (double)u, (double)o + s + 0.5)
									.texture(1.0 + ah, (double)v * 0.25 + ag + ai)
									.color(1.0F, 1.0F, 1.0F, am)
									.texture2(ao, ap)
									.next();
								bufferBuilder.vertex((double)p - r + 0.5, (double)u, (double)o - s + 0.5)
									.texture(0.0 + ah, (double)v * 0.25 + ag + ai)
									.color(1.0F, 1.0F, 1.0F, am)
									.texture2(ao, ap)
									.next();
							}
						}
					}
				}
			}

			if (n >= 0) {
				tessellator.draw();
			}

			bufferBuilder.offset(0.0, 0.0, 0.0);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			this.disableLightmap();
		}
	}

	public void setupHudMatrixMode() {
		Window window = new Window(this.client);
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0, window.getScaledWidth(), window.getScaledHeight(), 0.0, 1000.0, 3000.0);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
	}

	private void updateFog(float tickDelta) {
		World world = this.client.world;
		Entity entity = this.client.getCameraEntity();
		float f = 0.25F + 0.75F * (float)this.client.options.viewDistance / 32.0F;
		f = 1.0F - (float)Math.pow((double)f, 0.25);
		Vec3d vec3d = world.method_3631(this.client.getCameraEntity(), tickDelta);
		float g = (float)vec3d.x;
		float h = (float)vec3d.y;
		float i = (float)vec3d.z;
		Vec3d vec3d2 = world.getFogColor(tickDelta);
		this.fogRed = (float)vec3d2.x;
		this.fogGreen = (float)vec3d2.y;
		this.fogBlue = (float)vec3d2.z;
		if (this.client.options.viewDistance >= 4) {
			double d = -1.0;
			Vec3d vec3d3 = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? new Vec3d(d, 0.0, 0.0) : new Vec3d(1.0, 0.0, 0.0);
			float j = (float)entity.getRotationVector(tickDelta).dotProduct(vec3d3);
			if (j < 0.0F) {
				j = 0.0F;
			}

			if (j > 0.0F) {
				float[] fs = world.dimension.getBackgroundColor(world.getSkyAngle(tickDelta), tickDelta);
				if (fs != null) {
					j *= fs[3];
					this.fogRed = this.fogRed * (1.0F - j) + fs[0] * j;
					this.fogGreen = this.fogGreen * (1.0F - j) + fs[1] * j;
					this.fogBlue = this.fogBlue * (1.0F - j) + fs[2] * j;
				}
			}
		}

		this.fogRed = this.fogRed + (g - this.fogRed) * f;
		this.fogGreen = this.fogGreen + (h - this.fogGreen) * f;
		this.fogBlue = this.fogBlue + (i - this.fogBlue) * f;
		float k = world.getRainGradient(tickDelta);
		if (k > 0.0F) {
			float l = 1.0F - k * 0.5F;
			float m = 1.0F - k * 0.4F;
			this.fogRed *= l;
			this.fogGreen *= l;
			this.fogBlue *= m;
		}

		float n = world.getThunderGradient(tickDelta);
		if (n > 0.0F) {
			float o = 1.0F - n * 0.5F;
			this.fogRed *= o;
			this.fogGreen *= o;
			this.fogBlue *= o;
		}

		BlockState blockState = Camera.method_9371(this.client.world, entity, tickDelta);
		if (this.thickFog) {
			Vec3d vec3d4 = world.getCloudColor(tickDelta);
			this.fogRed = (float)vec3d4.x;
			this.fogGreen = (float)vec3d4.y;
			this.fogBlue = (float)vec3d4.z;
		} else if (blockState.getMaterial() == Material.WATER) {
			float p = 0.0F;
			if (entity instanceof LivingEntity) {
				p = (float)EnchantmentHelper.getRespiration((LivingEntity)entity) * 0.2F;
				if (((LivingEntity)entity).hasStatusEffect(StatusEffects.WATER_BREATHING)) {
					p = p * 0.3F + 0.6F;
				}
			}

			this.fogRed = 0.02F + p;
			this.fogGreen = 0.02F + p;
			this.fogBlue = 0.2F + p;
		} else if (blockState.getMaterial() == Material.LAVA) {
			this.fogRed = 0.6F;
			this.fogGreen = 0.1F;
			this.fogBlue = 0.0F;
		}

		float q = this.prevFogColor + (this.fogColor - this.prevFogColor) * tickDelta;
		this.fogRed *= q;
		this.fogGreen *= q;
		this.fogBlue *= q;
		double e = (entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta) * world.dimension.method_3994();
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
			int r = ((LivingEntity)entity).getEffectInstance(StatusEffects.BLINDNESS).getDuration();
			if (r < 20) {
				e *= (double)(1.0F - (float)r / 20.0F);
			} else {
				e = 0.0;
			}
		}

		if (e < 1.0) {
			if (e < 0.0) {
				e = 0.0;
			}

			e *= e;
			this.fogRed = (float)((double)this.fogRed * e);
			this.fogGreen = (float)((double)this.fogGreen * e);
			this.fogBlue = (float)((double)this.fogBlue * e);
		}

		if (this.skyDarkness > 0.0F) {
			float s = this.lastSkyDarkness + (this.skyDarkness - this.lastSkyDarkness) * tickDelta;
			this.fogRed = this.fogRed * (1.0F - s) + this.fogRed * 0.7F * s;
			this.fogGreen = this.fogGreen * (1.0F - s) + this.fogGreen * 0.6F * s;
			this.fogBlue = this.fogBlue * (1.0F - s) + this.fogBlue * 0.6F * s;
		}

		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION)) {
			float t = this.getNightVisionStrength((LivingEntity)entity, tickDelta);
			float u = 1.0F / this.fogRed;
			if (u > 1.0F / this.fogGreen) {
				u = 1.0F / this.fogGreen;
			}

			if (u > 1.0F / this.fogBlue) {
				u = 1.0F / this.fogBlue;
			}

			this.fogRed = this.fogRed * (1.0F - t) + this.fogRed * u * t;
			this.fogGreen = this.fogGreen * (1.0F - t) + this.fogGreen * u * t;
			this.fogBlue = this.fogBlue * (1.0F - t) + this.fogBlue * u * t;
		}

		if (this.client.options.anaglyph3d) {
			float v = (this.fogRed * 30.0F + this.fogGreen * 59.0F + this.fogBlue * 11.0F) / 100.0F;
			float w = (this.fogRed * 30.0F + this.fogGreen * 70.0F) / 100.0F;
			float x = (this.fogRed * 30.0F + this.fogBlue * 70.0F) / 100.0F;
			this.fogRed = v;
			this.fogGreen = w;
			this.fogBlue = x;
		}

		GlStateManager.clearColor(this.fogRed, this.fogGreen, this.fogBlue, 0.0F);
	}

	private void renderFog(int i, float tickDelta) {
		Entity entity = this.client.getCameraEntity();
		GlStateManager.method_12298(2918, this.updateFogColorBuffer(this.fogRed, this.fogGreen, this.fogBlue, 1.0F));
		GlStateManager.method_12272(0.0F, -1.0F, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		BlockState blockState = Camera.method_9371(this.client.world, entity, tickDelta);
		if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
			float f = 5.0F;
			int j = ((LivingEntity)entity).getEffectInstance(StatusEffects.BLINDNESS).getDuration();
			if (j < 20) {
				f = 5.0F + (this.viewDistance - 5.0F) * (1.0F - (float)j / 20.0F);
			}

			GlStateManager.method_12285(GlStateManager.class_2867.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(f * 0.8F);
			} else {
				GlStateManager.fogStart(f * 0.25F);
				GlStateManager.fogEnd(f);
			}

			if (GLContext.getCapabilities().GL_NV_fog_distance) {
				GlStateManager.method_12300(34138, 34139);
			}
		} else if (this.thickFog) {
			GlStateManager.method_12285(GlStateManager.class_2867.EXP);
			GlStateManager.fogDensity(0.1F);
		} else if (blockState.getMaterial() == Material.WATER) {
			GlStateManager.method_12285(GlStateManager.class_2867.EXP);
			if (entity instanceof LivingEntity) {
				if (((LivingEntity)entity).hasStatusEffect(StatusEffects.WATER_BREATHING)) {
					GlStateManager.fogDensity(0.01F);
				} else {
					GlStateManager.fogDensity(0.1F - (float)EnchantmentHelper.getRespiration((LivingEntity)entity) * 0.03F);
				}
			} else {
				GlStateManager.fogDensity(0.1F);
			}
		} else if (blockState.getMaterial() == Material.LAVA) {
			GlStateManager.method_12285(GlStateManager.class_2867.EXP);
			GlStateManager.fogDensity(2.0F);
		} else {
			float g = this.viewDistance;
			GlStateManager.method_12285(GlStateManager.class_2867.LINEAR);
			if (i == -1) {
				GlStateManager.fogStart(0.0F);
				GlStateManager.fogEnd(g);
			} else {
				GlStateManager.fogStart(g * 0.75F);
				GlStateManager.fogEnd(g);
			}

			if (GLContext.getCapabilities().GL_NV_fog_distance) {
				GlStateManager.method_12300(34138, 34139);
			}

			if (this.client.world.dimension.isFogThick((int)entity.x, (int)entity.z) || this.client.inGameHud.method_12167().method_12174()) {
				GlStateManager.fogStart(g * 0.05F);
				GlStateManager.fogEnd(Math.min(g, 192.0F) * 0.5F);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	private FloatBuffer updateFogColorBuffer(float red, float green, float blue, float alpha) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}

	public MapRenderer getMapRenderer() {
		return this.mapRenderer;
	}
}
