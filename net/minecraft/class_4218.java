package net.minecraft;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.CullingCameraView;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4218 implements AutoCloseable, ResourceReloadListener {
	private static final Logger field_20678 = LogManager.getLogger();
	private static final Identifier field_20679 = new Identifier("textures/environment/rain.png");
	private static final Identifier field_20680 = new Identifier("textures/environment/snow.png");
	private final MinecraftClient field_20681;
	private final ResourceManager field_20682;
	private final Random field_20683 = new Random();
	private float field_20684;
	public final class_4225 field_20676;
	private final MapRenderer field_20685;
	private int field_20686;
	private Entity field_20687;
	private final float field_20688 = 4.0F;
	private float field_20689 = 4.0F;
	private float field_20690;
	private float field_20691;
	private float field_20692;
	private float field_20693;
	private boolean field_20694 = true;
	private boolean field_20695 = true;
	private long field_20696;
	private long field_20697 = Util.method_20227();
	private final class_4226 field_20698;
	private int field_20699;
	private final float[] field_20700 = new float[1024];
	private final float[] field_20701 = new float[1024];
	private final class_4217 field_20660;
	private boolean field_20661;
	private double field_20662 = 1.0;
	private double field_20663;
	private double field_20664;
	private ItemStack field_20665;
	private int field_20666;
	private float field_20667;
	private float field_20668;
	private ShaderEffect field_20669;
	private float field_20670;
	private float field_20671;
	private static final Identifier[] field_20672 = new Identifier[]{
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
	public static final int field_20677 = field_20672.length;
	private int field_20673 = field_20677;
	private boolean field_20674;
	private int field_20675;

	public class_4218(MinecraftClient minecraftClient, ResourceManager resourceManager) {
		this.field_20681 = minecraftClient;
		this.field_20682 = resourceManager;
		this.field_20676 = minecraftClient.method_18201();
		this.field_20685 = new MapRenderer(minecraftClient.getTextureManager());
		this.field_20698 = new class_4226(this);
		this.field_20660 = new class_4217(this);
		this.field_20669 = null;

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = MathHelper.sqrt(f * f + g * g);
				this.field_20700[i << 5 | j] = -g / h;
				this.field_20701[i << 5 | j] = f / h;
			}
		}
	}

	public void close() {
		this.field_20698.close();
		this.field_20685.close();
		this.method_19072();
	}

	public boolean method_19058() {
		return GLX.shadersSupported && this.field_20669 != null;
	}

	public void method_19072() {
		if (this.field_20669 != null) {
			this.field_20669.close();
		}

		this.field_20669 = null;
		this.field_20673 = field_20677;
	}

	public void method_19076() {
		this.field_20674 = !this.field_20674;
	}

	public void method_19065(@Nullable Entity entity) {
		if (GLX.shadersSupported) {
			if (this.field_20669 != null) {
				this.field_20669.close();
			}

			this.field_20669 = null;
			if (entity instanceof CreeperEntity) {
				this.method_19071(new Identifier("shaders/post/creeper.json"));
			} else if (entity instanceof SpiderEntity) {
				this.method_19071(new Identifier("shaders/post/spider.json"));
			} else if (entity instanceof EndermanEntity) {
				this.method_19071(new Identifier("shaders/post/invert.json"));
			}
		}
	}

	private void method_19071(Identifier identifier) {
		if (this.field_20669 != null) {
			this.field_20669.close();
		}

		try {
			this.field_20669 = new ShaderEffect(this.field_20681.getTextureManager(), this.field_20682, this.field_20681.getFramebuffer(), identifier);
			this.field_20669.setupDimensions(this.field_20681.field_19944.method_18317(), this.field_20681.field_19944.method_18318());
			this.field_20674 = true;
		} catch (IOException var3) {
			field_20678.warn("Failed to load shader: {}", identifier, var3);
			this.field_20673 = field_20677;
			this.field_20674 = false;
		} catch (JsonSyntaxException var4) {
			field_20678.warn("Failed to load shader: {}", identifier, var4);
			this.field_20673 = field_20677;
			this.field_20674 = false;
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		if (this.field_20669 != null) {
			this.field_20669.close();
		}

		this.field_20669 = null;
		if (this.field_20673 == field_20677) {
			this.method_19065(this.field_20681.getCameraEntity());
		} else {
			this.method_19071(field_20672[this.field_20673]);
		}
	}

	public void method_19080() {
		if (GLX.shadersSupported && GlProgramManager.getInstance() == null) {
			GlProgramManager.newInstance();
		}

		this.method_19093();
		this.field_20698.method_19170();
		this.field_20689 = 4.0F;
		if (this.field_20681.getCameraEntity() == null) {
			this.field_20681.setCameraEntity(this.field_20681.player);
		}

		this.field_20671 = this.field_20670;
		this.field_20670 = this.field_20670 + (this.field_20681.getCameraEntity().getEyeHeight() - this.field_20670) * 0.5F;
		this.field_20686++;
		this.field_20676.method_19129();
		this.method_19096();
		this.field_20693 = this.field_20692;
		if (this.field_20681.inGameHud.method_12167().method_12173()) {
			this.field_20692 += 0.05F;
			if (this.field_20692 > 1.0F) {
				this.field_20692 = 1.0F;
			}
		} else if (this.field_20692 > 0.0F) {
			this.field_20692 -= 0.0125F;
		}

		if (this.field_20666 > 0) {
			this.field_20666--;
			if (this.field_20666 == 0) {
				this.field_20665 = null;
			}
		}
	}

	public ShaderEffect method_19082() {
		return this.field_20669;
	}

	public void method_19063(int i, int j) {
		if (GLX.shadersSupported) {
			if (this.field_20669 != null) {
				this.field_20669.setupDimensions(i, j);
			}

			this.field_20681.worldRenderer.onResized(i, j);
		}
	}

	public void method_19059(float f) {
		Entity entity = this.field_20681.getCameraEntity();
		if (entity != null) {
			if (this.field_20681.world != null) {
				this.field_20681.profiler.push("pick");
				this.field_20681.targetedEntity = null;
				double d = (double)this.field_20681.interactionManager.getReachDistance();
				this.field_20681.result = entity.method_10931(d, f, class_4079.NEVER);
				Vec3d vec3d = entity.getCameraPosVec(f);
				boolean bl = false;
				int i = 3;
				double e = d;
				if (this.field_20681.interactionManager.hasExtendedReach()) {
					e = 6.0;
					d = e;
				} else {
					if (d > 3.0) {
						bl = true;
					}

					d = d;
				}

				if (this.field_20681.result != null) {
					e = this.field_20681.result.pos.distanceTo(vec3d);
				}

				Vec3d vec3d2 = entity.getRotationVector(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				this.field_20687 = null;
				Vec3d vec3d4 = null;
				float g = 1.0F;
				List<Entity> list = this.field_20681
					.world
					.method_16288(
						entity,
						entity.getBoundingBox().stretch(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).expand(1.0, 1.0, 1.0),
						EntityPredicate.field_16705.and(Entity::collides)
					);
				double h = e;

				for (int j = 0; j < list.size(); j++) {
					Entity entity2 = (Entity)list.get(j);
					Box box = entity2.getBoundingBox().expand((double)entity2.getTargetingMargin());
					BlockHitResult blockHitResult = box.method_585(vec3d, vec3d3);
					if (box.contains(vec3d)) {
						if (h >= 0.0) {
							this.field_20687 = entity2;
							vec3d4 = blockHitResult == null ? vec3d : blockHitResult.pos;
							h = 0.0;
						}
					} else if (blockHitResult != null) {
						double k = vec3d.distanceTo(blockHitResult.pos);
						if (k < h || h == 0.0) {
							if (entity2.getRootVehicle() == entity.getRootVehicle()) {
								if (h == 0.0) {
									this.field_20687 = entity2;
									vec3d4 = blockHitResult.pos;
								}
							} else {
								this.field_20687 = entity2;
								vec3d4 = blockHitResult.pos;
								h = k;
							}
						}
					}
				}

				if (this.field_20687 != null && bl && vec3d.distanceTo(vec3d4) > 3.0) {
					this.field_20687 = null;
					this.field_20681.result = new BlockHitResult(BlockHitResult.Type.MISS, vec3d4, null, new BlockPos(vec3d4));
				}

				if (this.field_20687 != null && (h < e || this.field_20681.result == null)) {
					this.field_20681.result = new BlockHitResult(this.field_20687, vec3d4);
					if (this.field_20687 instanceof LivingEntity || this.field_20687 instanceof ItemFrameEntity) {
						this.field_20681.targetedEntity = this.field_20687;
					}
				}

				this.field_20681.profiler.pop();
			}
		}
	}

	private void method_19093() {
		float f = 1.0F;
		if (this.field_20681.getCameraEntity() instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)this.field_20681.getCameraEntity();
			f = abstractClientPlayerEntity.getSpeed();
		}

		this.field_20691 = this.field_20690;
		this.field_20690 = this.field_20690 + (f - this.field_20690) * 0.5F;
		if (this.field_20690 > 1.5F) {
			this.field_20690 = 1.5F;
		}

		if (this.field_20690 < 0.1F) {
			this.field_20690 = 0.1F;
		}
	}

	private double method_19062(float f, boolean bl) {
		if (this.field_20661) {
			return 90.0;
		} else {
			Entity entity = this.field_20681.getCameraEntity();
			double d = 70.0;
			if (bl) {
				d = this.field_20681.options.field_19984;
				d *= (double)(this.field_20691 + (this.field_20690 - this.field_20691) * f);
			}

			if (entity instanceof LivingEntity && ((LivingEntity)entity).getHealth() <= 0.0F) {
				float g = (float)((LivingEntity)entity).deathTime + f;
				d /= (double)((1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F);
			}

			FluidState fluidState = Camera.method_18136(this.field_20681.world, entity, f);
			if (!fluidState.isEmpty()) {
				d = d * 60.0 / 70.0;
			}

			return d;
		}
	}

	private void method_19081(float f) {
		if (this.field_20681.getCameraEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)this.field_20681.getCameraEntity();
			float g = (float)livingEntity.hurtTime - f;
			if (livingEntity.getHealth() <= 0.0F) {
				float h = (float)livingEntity.deathTime + f;
				GlStateManager.rotate(40.0F - 8000.0F / (h + 200.0F), 0.0F, 0.0F, 1.0F);
			}

			if (g < 0.0F) {
				return;
			}

			g /= (float)livingEntity.maxHurtTime;
			g = MathHelper.sin(g * g * g * g * (float) Math.PI);
			float i = livingEntity.knockbackVelocity;
			GlStateManager.rotate(-i, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-g * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(i, 0.0F, 1.0F, 0.0F);
		}
	}

	private void method_19083(float f) {
		if (this.field_20681.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.field_20681.getCameraEntity();
			float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
			float h = -(playerEntity.horizontalSpeed + g * f);
			float i = playerEntity.prevStrideDistance + (playerEntity.strideDistance - playerEntity.prevStrideDistance) * f;
			float j = playerEntity.field_6752 + (playerEntity.field_6753 - playerEntity.field_6752) * f;
			GlStateManager.translate(MathHelper.sin(h * (float) Math.PI) * i * 0.5F, -Math.abs(MathHelper.cos(h * (float) Math.PI) * i), 0.0F);
			GlStateManager.rotate(MathHelper.sin(h * (float) Math.PI) * i * 3.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(Math.abs(MathHelper.cos(h * (float) Math.PI - 0.2F) * i) * 5.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(j, 1.0F, 0.0F, 0.0F);
		}
	}

	private void method_19084(float f) {
		Entity entity = this.field_20681.getCameraEntity();
		float g = this.field_20671 + (this.field_20670 - this.field_20671) * f;
		double d = entity.prevX + (entity.x - entity.prevX) * (double)f;
		double e = entity.prevY + (entity.y - entity.prevY) * (double)f + (double)entity.getEyeHeight();
		double h = entity.prevZ + (entity.z - entity.prevZ) * (double)f;
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			g = (float)((double)g + 1.0);
			GlStateManager.translate(0.0F, 0.3F, 0.0F);
			if (!this.field_20681.options.field_955) {
				BlockPos blockPos = new BlockPos(entity);
				BlockState blockState = this.field_20681.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof BedBlock) {
					GlStateManager.rotate(((Direction)blockState.getProperty(BedBlock.FACING)).method_12578(), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.prevYaw + (entity.yaw - entity.prevYaw) * f + 180.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.rotate(entity.prevPitch + (entity.pitch - entity.prevPitch) * f, -1.0F, 0.0F, 0.0F);
			}
		} else if (this.field_20681.options.perspective > 0) {
			double i = (double)(this.field_20689 + (4.0F - this.field_20689) * f);
			if (this.field_20681.options.field_955) {
				GlStateManager.translate(0.0F, 0.0F, (float)(-i));
			} else {
				float j = entity.yaw;
				float k = entity.pitch;
				if (this.field_20681.options.perspective == 2) {
					k += 180.0F;
				}

				double l = (double)(-MathHelper.sin(j * (float) (Math.PI / 180.0)) * MathHelper.cos(k * (float) (Math.PI / 180.0))) * i;
				double m = (double)(MathHelper.cos(j * (float) (Math.PI / 180.0)) * MathHelper.cos(k * (float) (Math.PI / 180.0))) * i;
				double n = (double)(-MathHelper.sin(k * (float) (Math.PI / 180.0))) * i;

				for (int o = 0; o < 8; o++) {
					float p = (float)((o & 1) * 2 - 1);
					float q = (float)((o >> 1 & 1) * 2 - 1);
					float r = (float)((o >> 2 & 1) * 2 - 1);
					p *= 0.1F;
					q *= 0.1F;
					r *= 0.1F;
					BlockHitResult blockHitResult = this.field_20681
						.world
						.rayTrace(new Vec3d(d + (double)p, e + (double)q, h + (double)r), new Vec3d(d - l + (double)p + (double)r, e - n + (double)q, h - m + (double)r));
					if (blockHitResult != null) {
						double s = blockHitResult.pos.distanceTo(new Vec3d(d, e, h));
						if (s < i) {
							i = s;
						}
					}
				}

				if (this.field_20681.options.perspective == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.pitch - k, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.yaw - j, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, 0.0F, (float)(-i));
				GlStateManager.rotate(j - entity.yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(k - entity.pitch, 1.0F, 0.0F, 0.0F);
			}
		} else if (!this.field_20661) {
			GlStateManager.translate(0.0F, 0.0F, 0.05F);
		}

		if (!this.field_20681.options.field_955) {
			GlStateManager.rotate(entity.method_15589(f), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(entity.method_15591(f) + 180.0F, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.translate(0.0F, -g, 0.0F);
	}

	private void method_19086(float f) {
		this.field_20684 = (float)(this.field_20681.options.viewDistance * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		if (this.field_20662 != 1.0) {
			GlStateManager.translate((float)this.field_20663, (float)(-this.field_20664), 0.0F);
			GlStateManager.scale(this.field_20662, this.field_20662, 1.0);
		}

		GlStateManager.method_19121(
			Matrix4f.method_19642(
				this.method_19062(f, true),
				(float)this.field_20681.field_19944.method_18317() / (float)this.field_20681.field_19944.method_18318(),
				0.05F,
				this.field_20684 * MathHelper.SQUARE_ROOT_OF_TWO
			)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		this.method_19081(f);
		if (this.field_20681.options.bobView) {
			this.method_19083(f);
		}

		float g = this.field_20681.player.lastTimeInPortal + (this.field_20681.player.timeInPortal - this.field_20681.player.lastTimeInPortal) * f;
		if (g > 0.0F) {
			int i = 20;
			if (this.field_20681.player.hasStatusEffect(StatusEffects.NAUSEA)) {
				i = 7;
			}

			float h = 5.0F / (g * g + 5.0F) - g * 0.04F;
			h *= h;
			GlStateManager.rotate(((float)this.field_20686 + f) * (float)i, 0.0F, 1.0F, 1.0F);
			GlStateManager.scale(1.0F / h, 1.0F, 1.0F);
			GlStateManager.rotate(-((float)this.field_20686 + f) * (float)i, 0.0F, 1.0F, 1.0F);
		}

		this.method_19084(f);
	}

	private void method_19088(float f) {
		if (!this.field_20661) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.method_19121(
				Matrix4f.method_19642(
					this.method_19062(f, false),
					(float)this.field_20681.field_19944.method_18317() / (float)this.field_20681.field_19944.method_18318(),
					0.05F,
					this.field_20684 * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.pushMatrix();
			this.method_19081(f);
			if (this.field_20681.options.bobView) {
				this.method_19083(f);
			}

			boolean bl = this.field_20681.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.field_20681.getCameraEntity()).isSleeping();
			if (this.field_20681.options.perspective == 0
				&& !bl
				&& !this.field_20681.options.field_19987
				&& this.field_20681.interactionManager.method_9667() != GameMode.SPECTATOR) {
				this.method_19087();
				this.field_20676.method_19130(f);
				this.method_19085();
			}

			GlStateManager.popMatrix();
			if (this.field_20681.options.perspective == 0 && !bl) {
				this.field_20676.method_19145(f);
				this.method_19081(f);
			}

			if (this.field_20681.options.bobView) {
				this.method_19083(f);
			}
		}
	}

	public void method_19085() {
		this.field_20698.method_19172();
	}

	public void method_19087() {
		this.field_20698.method_19173();
	}

	public float method_19066(LivingEntity livingEntity, float f) {
		int i = livingEntity.getEffectInstance(StatusEffects.NIGHT_VISION).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - f) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void method_19061(float f, long l, boolean bl) {
		if (!this.field_20681.isFullscreen()
			&& this.field_20681.options.field_19973
			&& (!this.field_20681.options.touchscreen || !this.field_20681.field_19945.method_18248())) {
			if (Util.method_20227() - this.field_20697 > 500L) {
				this.field_20681.openGameMenuScreen();
			}
		} else {
			this.field_20697 = Util.method_20227();
		}

		if (!this.field_20681.skipGameRender) {
			int i = (int)(
				this.field_20681.field_19945.method_18249() * (double)this.field_20681.field_19944.method_18321() / (double)this.field_20681.field_19944.method_18319()
			);
			int j = (int)(
				this.field_20681.field_19945.method_18250() * (double)this.field_20681.field_19944.method_18322() / (double)this.field_20681.field_19944.method_18320()
			);
			int k = this.field_20681.options.maxFramerate;
			if (bl && this.field_20681.world != null) {
				this.field_20681.profiler.push("level");
				int m = Math.min(MinecraftClient.getCurrentFps(), k);
				m = Math.max(m, 60);
				long n = Util.method_20230() - l;
				long o = Math.max((long)(1000000000 / m / 4) - n, 0L);
				this.method_19060(f, Util.method_20230() + o);
				if (this.field_20681.isInSingleplayer() && this.field_20696 < Util.method_20227() - 1000L) {
					this.field_20696 = Util.method_20227();
					if (!this.field_20681.getServer().method_12837()) {
						this.method_19094();
					}
				}

				if (GLX.shadersSupported) {
					this.field_20681.worldRenderer.drawEntityOutlineFramebuffer();
					if (this.field_20669 != null && this.field_20674) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.field_20669.render(f);
						GlStateManager.popMatrix();
					}

					this.field_20681.getFramebuffer().bind(true);
				}

				this.field_20681.profiler.swap("gui");
				if (!this.field_20681.options.field_19987 || this.field_20681.currentScreen != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.field_20681.field_19944.method_18293();
					this.method_19064(this.field_20681.field_19944.method_18321(), this.field_20681.field_19944.method_18322(), f);
					this.field_20681.inGameHud.render(f);
				}

				this.field_20681.profiler.pop();
			} else {
				GlStateManager.viewport(0, 0, this.field_20681.field_19944.method_18317(), this.field_20681.field_19944.method_18318());
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.field_20681.field_19944.method_18293();
			}

			if (this.field_20681.currentScreen != null) {
				GlStateManager.clear(256);

				try {
					this.field_20681.currentScreen.render(i, j, this.field_20681.method_14461());
				} catch (Throwable var13) {
					CrashReport crashReport = CrashReport.create(var13, "Rendering screen");
					CrashReportSection crashReportSection = crashReport.addElement("Screen render details");
					crashReportSection.add("Screen name", (CrashCallable<String>)(() -> this.field_20681.currentScreen.getClass().getCanonicalName()));
					crashReportSection.add(
						"Mouse location",
						(CrashCallable<String>)(() -> String.format(
								Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.field_20681.field_19945.method_18249(), this.field_20681.field_19945.method_18250()
							))
					);
					crashReportSection.add(
						"Screen size",
						(CrashCallable<String>)(() -> String.format(
								Locale.ROOT,
								"Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
								this.field_20681.field_19944.method_18321(),
								this.field_20681.field_19944.method_18322(),
								this.field_20681.field_19944.method_18317(),
								this.field_20681.field_19944.method_18318(),
								this.field_20681.field_19944.method_18325()
							))
					);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	private void method_19094() {
		if (this.field_20681.worldRenderer.method_12338() > 10 && this.field_20681.worldRenderer.method_12339() && !this.field_20681.getServer().method_12837()) {
			class_4277 lv = ScreenshotUtils.method_18269(
				this.field_20681.field_19944.method_18317(), this.field_20681.field_19944.method_18318(), this.field_20681.getFramebuffer()
			);
			class_4469.field_21928.execute(() -> {
				int i = lv.method_19458();
				int j = lv.method_19478();
				int k = 0;
				int l = 0;
				if (i > j) {
					k = (i - j) / 2;
					i = j;
				} else {
					l = (j - i) / 2;
					j = i;
				}

				try (class_4277 lvx = new class_4277(64, 64, false)) {
					lv.method_19465(k, l, i, j, lvx);
					lvx.method_19471(this.field_20681.getServer().method_12838());
				} catch (IOException var27) {
					field_20678.warn("Couldn't save auto screenshot", var27);
				} finally {
					lv.close();
				}
			});
		}
	}

	public void method_19073(float f) {
		this.field_20681.field_19944.method_18293();
	}

	private boolean method_19095() {
		if (!this.field_20695) {
			return false;
		} else {
			Entity entity = this.field_20681.getCameraEntity();
			boolean bl = entity instanceof PlayerEntity && !this.field_20681.options.field_19987;
			if (bl && !((PlayerEntity)entity).abilities.allowModifyWorld) {
				ItemStack itemStack = ((PlayerEntity)entity).getMainHandStack();
				if (this.field_20681.result != null && this.field_20681.result.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = this.field_20681.result.getBlockPos();
					Block block = this.field_20681.world.getBlockState(blockPos).getBlock();
					if (this.field_20681.interactionManager.method_9667() == GameMode.SPECTATOR) {
						bl = block.hasBlockEntity() && this.field_20681.world.getBlockEntity(blockPos) instanceof Inventory;
					} else {
						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.field_20681.world, blockPos, false);
						bl = !itemStack.isEmpty()
							&& (
								itemStack.method_16103(this.field_20681.world.method_16314(), cachedBlockPosition)
									|| itemStack.method_16106(this.field_20681.world.method_16314(), cachedBlockPosition)
							);
					}
				}
			}

			return bl;
		}
	}

	public void method_19060(float f, long l) {
		this.field_20698.method_19171(f);
		if (this.field_20681.getCameraEntity() == null) {
			this.field_20681.setCameraEntity(this.field_20681.player);
		}

		this.method_19059(f);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.5F);
		this.field_20681.profiler.push("center");
		this.method_19074(f, l);
		this.field_20681.profiler.pop();
	}

	private void method_19074(float f, long l) {
		WorldRenderer worldRenderer = this.field_20681.worldRenderer;
		ParticleManager particleManager = this.field_20681.particleManager;
		boolean bl = this.method_19095();
		GlStateManager.enableCull();
		this.field_20681.profiler.swap("clear");
		GlStateManager.viewport(0, 0, this.field_20681.field_19944.method_18317(), this.field_20681.field_19944.method_18318());
		this.field_20660.method_19053(f);
		GlStateManager.clear(16640);
		this.field_20681.profiler.swap("camera");
		this.method_19086(f);
		Camera.method_18134(this.field_20681.player, this.field_20681.options.perspective == 2, this.field_20684);
		this.field_20681.profiler.swap("frustum");
		Frustum.getInstance();
		this.field_20681.profiler.swap("culling");
		CameraView cameraView = new CullingCameraView();
		Entity entity = this.field_20681.getCameraEntity();
		double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)f;
		double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)f;
		double g = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)f;
		cameraView.setPos(d, e, g);
		if (this.field_20681.options.viewDistance >= 4) {
			this.field_20660.method_19054(-1, f);
			this.field_20681.profiler.swap("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.method_19121(
				Matrix4f.method_19642(
					this.method_19062(f, true),
					(float)this.field_20681.field_19944.method_18317() / (float)this.field_20681.field_19944.method_18318(),
					0.05F,
					this.field_20684 * 2.0F
				)
			);
			GlStateManager.matrixMode(5888);
			worldRenderer.method_9891(f);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.method_19121(
				Matrix4f.method_19642(
					this.method_19062(f, true),
					(float)this.field_20681.field_19944.method_18317() / (float)this.field_20681.field_19944.method_18318(),
					0.05F,
					this.field_20684 * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}

		this.field_20660.method_19054(0, f);
		GlStateManager.shadeModel(7425);
		if (entity.y + (double)entity.getEyeHeight() < 128.0) {
			this.method_19069(worldRenderer, f, d, e, g);
		}

		this.field_20681.profiler.swap("prepareterrain");
		this.field_20660.method_19054(0, f);
		this.field_20681.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		DiffuseLighting.disable();
		this.field_20681.profiler.swap("terrain_setup");
		worldRenderer.method_9906(entity, f, cameraView, this.field_20675++, this.field_20681.player.isSpectator());
		this.field_20681.profiler.swap("updatechunks");
		this.field_20681.worldRenderer.updateChunks(l);
		this.field_20681.profiler.swap("terrain");
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlphaTest();
		worldRenderer.method_9894(RenderLayer.SOLID, (double)f, entity);
		GlStateManager.enableAlphaTest();
		worldRenderer.method_9894(RenderLayer.CUTOUT_MIPPED, (double)f, entity);
		this.field_20681.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.method_9894(RenderLayer.CUTOUT, (double)f, entity);
		this.field_20681.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		DiffuseLighting.enableNormally();
		this.field_20681.profiler.swap("entities");
		worldRenderer.renderEntities(entity, cameraView, f);
		DiffuseLighting.disable();
		this.method_19085();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		if (bl && this.field_20681.result != null) {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			GlStateManager.disableAlphaTest();
			this.field_20681.profiler.swap("outline");
			worldRenderer.drawBlockOutline(playerEntity, this.field_20681.result, 0, f);
			GlStateManager.enableAlphaTest();
		}

		if (this.field_20681.debugRenderer.isEnabled()) {
			this.field_20681.debugRenderer.render(f, l);
		}

		this.field_20681.profiler.swap("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.method_12288(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO);
		this.field_20681.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		worldRenderer.drawBlockDamage(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, f);
		this.field_20681.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pop();
		GlStateManager.disableBlend();
		this.method_19087();
		this.field_20681.profiler.swap("litParticles");
		particleManager.method_1299(entity, f);
		DiffuseLighting.disable();
		this.field_20660.method_19054(0, f);
		this.field_20681.profiler.swap("particles");
		particleManager.renderParticles(entity, f);
		this.method_19085();
		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.field_20681.profiler.swap("weather");
		this.method_19077(f);
		GlStateManager.depthMask(true);
		worldRenderer.renderWorldBorder(entity, f);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.alphaFunc(516, 0.1F);
		this.field_20660.method_19054(0, f);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.field_20681.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.shadeModel(7425);
		this.field_20681.profiler.swap("translucent");
		worldRenderer.method_9894(RenderLayer.TRANSLUCENT, (double)f, entity);
		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();
		if (entity.y + (double)entity.getEyeHeight() >= 128.0) {
			this.field_20681.profiler.swap("aboveClouds");
			this.method_19069(worldRenderer, f, d, e, g);
		}

		this.field_20681.profiler.swap("hand");
		if (this.field_20694) {
			GlStateManager.clear(256);
			this.method_19088(f);
		}
	}

	private void method_19069(WorldRenderer worldRenderer, float f, double d, double e, double g) {
		if (this.field_20681.options.getCloudMode() != 0) {
			this.field_20681.profiler.swap("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.method_19121(
				Matrix4f.method_19642(
					this.method_19062(f, true),
					(float)this.field_20681.field_19944.method_18317() / (float)this.field_20681.field_19944.method_18318(),
					0.05F,
					this.field_20684 * 4.0F
				)
			);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.field_20660.method_19054(0, f);
			worldRenderer.method_19158(f, d, e, g);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.method_19121(
				Matrix4f.method_19642(
					this.method_19062(f, true),
					(float)this.field_20681.field_19944.method_18317() / (float)this.field_20681.field_19944.method_18318(),
					0.05F,
					this.field_20684 * MathHelper.SQUARE_ROOT_OF_TWO
				)
			);
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_19096() {
		float f = this.field_20681.world.getRainGradient(1.0F);
		if (!this.field_20681.options.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			this.field_20683.setSeed((long)this.field_20686 * 312987231L);
			Entity entity = this.field_20681.getCameraEntity();
			RenderBlockView renderBlockView = this.field_20681.world;
			BlockPos blockPos = new BlockPos(entity);
			int i = 10;
			double d = 0.0;
			double e = 0.0;
			double g = 0.0;
			int j = 0;
			int k = (int)(100.0F * f * f);
			if (this.field_20681.options.particle == 1) {
				k >>= 1;
			} else if (this.field_20681.options.particle == 2) {
				k = 0;
			}

			for (int l = 0; l < k; l++) {
				BlockPos blockPos2 = renderBlockView.method_16373(
					class_3804.class_3805.MOTION_BLOCKING,
					blockPos.add(this.field_20683.nextInt(10) - this.field_20683.nextInt(10), 0, this.field_20683.nextInt(10) - this.field_20683.nextInt(10))
				);
				Biome biome = renderBlockView.method_8577(blockPos2);
				BlockPos blockPos3 = blockPos2.down();
				if (blockPos2.getY() <= blockPos.getY() + 10
					&& blockPos2.getY() >= blockPos.getY() - 10
					&& biome.getPrecipitation() == Biome.Precipitation.RAIN
					&& biome.getTemperature(blockPos2) >= 0.15F) {
					double h = this.field_20683.nextDouble();
					double m = this.field_20683.nextDouble();
					BlockState blockState = renderBlockView.getBlockState(blockPos3);
					FluidState fluidState = renderBlockView.getFluidState(blockPos2);
					VoxelShape voxelShape = blockState.getCollisionShape(renderBlockView, blockPos3);
					double n = voxelShape.method_18084(Direction.Axis.Y, h, m);
					double o = (double)fluidState.method_17810();
					double p;
					double q;
					if (n >= o) {
						p = n;
						q = voxelShape.getBoundingBoxes(Direction.Axis.Y, h, m);
					} else {
						p = 0.0;
						q = 0.0;
					}

					if (p > -Double.MAX_VALUE) {
						if (!fluidState.matches(FluidTags.LAVA) && blockState.getBlock() != Blocks.MAGMA_BLOCK) {
							if (this.field_20683.nextInt(++j) == 0) {
								d = (double)blockPos3.getX() + h;
								e = (double)((float)blockPos3.getY() + 0.1F) + p - 1.0;
								g = (double)blockPos3.getZ() + m;
							}

							this.field_20681
								.world
								.method_16343(
									class_4342.field_21362, (double)blockPos3.getX() + h, (double)((float)blockPos3.getY() + 0.1F) + p, (double)blockPos3.getZ() + m, 0.0, 0.0, 0.0
								);
						} else {
							this.field_20681
								.world
								.method_16343(
									class_4342.field_21363, (double)blockPos2.getX() + h, (double)((float)blockPos2.getY() + 0.1F) - q, (double)blockPos2.getZ() + m, 0.0, 0.0, 0.0
								);
						}
					}
				}
			}

			if (j > 0 && this.field_20683.nextInt(3) < this.field_20699++) {
				this.field_20699 = 0;
				if (e > (double)(blockPos.getY() + 1)
					&& renderBlockView.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					this.field_20681.world.playSound(d, e, g, Sounds.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
				} else {
					this.field_20681.world.playSound(d, e, g, Sounds.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}
	}

	protected void method_19077(float f) {
		float g = this.field_20681.world.getRainGradient(f);
		if (!(g <= 0.0F)) {
			this.method_19087();
			Entity entity = this.field_20681.getCameraEntity();
			World world = this.field_20681.world;
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
			double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)f;
			double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)f;
			double h = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)f;
			int l = MathHelper.floor(e);
			int m = 5;
			if (this.field_20681.options.fancyGraphics) {
				m = 10;
			}

			int n = -1;
			float o = (float)this.field_20686 + f;
			bufferBuilder.offset(-d, -e, -h);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int p = k - m; p <= k + m; p++) {
				for (int q = i - m; q <= i + m; q++) {
					int r = (p - k + 16) * 32 + q - i + 16;
					double s = (double)this.field_20700[r] * 0.5;
					double t = (double)this.field_20701[r] * 0.5;
					mutable.setPosition(q, 0, p);
					Biome biome = world.method_8577(mutable);
					if (biome.getPrecipitation() != Biome.Precipitation.NONE) {
						int u = world.method_16373(class_3804.class_3805.MOTION_BLOCKING, mutable).getY();
						int v = j - m;
						int w = j + m;
						if (v < u) {
							v = u;
						}

						if (w < u) {
							w = u;
						}

						int x = u;
						if (u < l) {
							x = l;
						}

						if (v != w) {
							this.field_20683.setSeed((long)(q * q * 3121 + q * 45238971 ^ p * p * 418711 + p * 13761));
							mutable.setPosition(q, v, p);
							float y = biome.getTemperature(mutable);
							if (y >= 0.15F) {
								if (n != 0) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 0;
									this.field_20681.getTextureManager().bindTexture(field_20679);
									bufferBuilder.begin(7, VertexFormats.PARTICLE);
								}

								double z = -((double)(this.field_20686 + q * q * 3121 + q * 45238971 + p * p * 418711 + p * 13761 & 31) + (double)f)
									/ 32.0
									* (3.0 + this.field_20683.nextDouble());
								double aa = (double)((float)q + 0.5F) - entity.x;
								double ab = (double)((float)p + 0.5F) - entity.z;
								float ac = MathHelper.sqrt(aa * aa + ab * ab) / (float)m;
								float ad = ((1.0F - ac * ac) * 0.5F + 0.5F) * g;
								mutable.setPosition(q, x, p);
								int ae = world.method_8578(mutable, 0);
								int af = ae >> 16 & 65535;
								int ag = ae & 65535;
								bufferBuilder.vertex((double)q - s + 0.5, (double)w, (double)p - t + 0.5)
									.texture(0.0, (double)v * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture2(af, ag)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)w, (double)p + t + 0.5)
									.texture(1.0, (double)v * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture2(af, ag)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)v, (double)p + t + 0.5)
									.texture(1.0, (double)w * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture2(af, ag)
									.next();
								bufferBuilder.vertex((double)q - s + 0.5, (double)v, (double)p - t + 0.5)
									.texture(0.0, (double)w * 0.25 + z)
									.color(1.0F, 1.0F, 1.0F, ad)
									.texture2(af, ag)
									.next();
							} else {
								if (n != 1) {
									if (n >= 0) {
										tessellator.draw();
									}

									n = 1;
									this.field_20681.getTextureManager().bindTexture(field_20680);
									bufferBuilder.begin(7, VertexFormats.PARTICLE);
								}

								double ah = (double)(-((float)(this.field_20686 & 511) + f) / 512.0F);
								double ai = this.field_20683.nextDouble() + (double)o * 0.01 * (double)((float)this.field_20683.nextGaussian());
								double aj = this.field_20683.nextDouble() + (double)(o * (float)this.field_20683.nextGaussian()) * 0.001;
								double ak = (double)((float)q + 0.5F) - entity.x;
								double al = (double)((float)p + 0.5F) - entity.z;
								float am = MathHelper.sqrt(ak * ak + al * al) / (float)m;
								float an = ((1.0F - am * am) * 0.3F + 0.5F) * g;
								mutable.setPosition(q, x, p);
								int ao = (world.method_8578(mutable, 0) * 3 + 15728880) / 4;
								int ap = ao >> 16 & 65535;
								int aq = ao & 65535;
								bufferBuilder.vertex((double)q - s + 0.5, (double)w, (double)p - t + 0.5)
									.texture(0.0 + ai, (double)v * 0.25 + ah + aj)
									.color(1.0F, 1.0F, 1.0F, an)
									.texture2(ap, aq)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)w, (double)p + t + 0.5)
									.texture(1.0 + ai, (double)v * 0.25 + ah + aj)
									.color(1.0F, 1.0F, 1.0F, an)
									.texture2(ap, aq)
									.next();
								bufferBuilder.vertex((double)q + s + 0.5, (double)v, (double)p + t + 0.5)
									.texture(1.0 + ai, (double)w * 0.25 + ah + aj)
									.color(1.0F, 1.0F, 1.0F, an)
									.texture2(ap, aq)
									.next();
								bufferBuilder.vertex((double)q - s + 0.5, (double)v, (double)p - t + 0.5)
									.texture(0.0 + ai, (double)w * 0.25 + ah + aj)
									.color(1.0F, 1.0F, 1.0F, an)
									.texture2(ap, aq)
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
			this.method_19085();
		}
	}

	public void method_19079(boolean bl) {
		this.field_20660.method_19057(bl);
	}

	public void method_19089() {
		this.field_20665 = null;
		this.field_20685.clearStateTextures();
	}

	public MapRenderer method_19090() {
		return this.field_20685;
	}

	public static void method_19068(TextRenderer textRenderer, String string, float f, float g, float h, int i, float j, float k, boolean bl, boolean bl2) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(f, g, h);
		GlStateManager.method_12272(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-j, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(bl ? -1 : 1) * k, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		if (!bl2) {
			GlStateManager.disableDepthTest();
		}

		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		int l = textRenderer.getStringWidth(string) / 2;
		GlStateManager.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)(-l - 1), (double)(-1 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		bufferBuilder.vertex((double)(-l - 1), (double)(8 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		bufferBuilder.vertex((double)(l + 1), (double)(8 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		bufferBuilder.vertex((double)(l + 1), (double)(-1 + i), 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		if (!bl2) {
			textRenderer.method_18355(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)i, 553648127);
			GlStateManager.enableDepthTest();
		}

		GlStateManager.depthMask(true);
		textRenderer.method_18355(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)i, bl2 ? 553648127 : -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public void method_19067(ItemStack itemStack) {
		this.field_20665 = itemStack;
		this.field_20666 = 40;
		this.field_20667 = this.field_20683.nextFloat() * 2.0F - 1.0F;
		this.field_20668 = this.field_20683.nextFloat() * 2.0F - 1.0F;
	}

	private void method_19064(int i, int j, float f) {
		if (this.field_20665 != null && this.field_20666 > 0) {
			int k = 40 - this.field_20666;
			float g = ((float)k + f) / 40.0F;
			float h = g * g;
			float l = g * h;
			float m = 10.25F * l * h - 24.95F * h * h + 25.5F * l - 13.8F * h + 4.0F * g;
			float n = m * (float) Math.PI;
			float o = this.field_20667 * (float)(i / 4);
			float p = this.field_20668 * (float)(j / 4);
			GlStateManager.enableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.pushLightingAttributes();
			GlStateManager.enableDepthTest();
			GlStateManager.disableCull();
			DiffuseLighting.enableNormally();
			GlStateManager.translate(
				(float)(i / 2) + o * MathHelper.abs(MathHelper.sin(n * 2.0F)), (float)(j / 2) + p * MathHelper.abs(MathHelper.sin(n * 2.0F)), -50.0F
			);
			float q = 50.0F + 175.0F * MathHelper.sin(n);
			GlStateManager.scale(q, -q, q);
			GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(n)), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(6.0F * MathHelper.cos(g * 8.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(6.0F * MathHelper.cos(g * 8.0F), 0.0F, 0.0F, 1.0F);
			this.field_20681.getHeldItemRenderer().method_19380(this.field_20665, ModelTransformation.Mode.FIXED);
			GlStateManager.popAttributes();
			GlStateManager.popMatrix();
			DiffuseLighting.disable();
			GlStateManager.enableCull();
			GlStateManager.disableDepthTest();
		}
	}

	public MinecraftClient method_19091() {
		return this.field_20681;
	}

	public float method_19078(float f) {
		return this.field_20693 + (this.field_20692 - this.field_20693) * f;
	}

	public float method_19092() {
		return this.field_20684;
	}
}
