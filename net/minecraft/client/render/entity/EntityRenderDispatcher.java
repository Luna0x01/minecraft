package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.HorseBaseEntityModel;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PolarBearEntity;
import net.minecraft.entity.ShulkerBulletEntity;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.EyeOfEnderEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRenderDispatcher {
	private final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers = Maps.newHashMap();
	private final Map<String, PlayerEntityRenderer> modelRenderers = Maps.newHashMap();
	private final PlayerEntityRenderer playerRenderer;
	private TextRenderer textRenderer;
	private double CAMERA_X;
	private double CAMERA_Y;
	private double CAMERA_Z;
	public TextureManager textureManager;
	public World world;
	public Entity field_11098;
	public Entity field_7998;
	public float yaw;
	public float pitch;
	public GameOptions options;
	public double cameraX;
	public double cameraY;
	public double cameraZ;
	private boolean field_11101;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer) {
		this.textureManager = textureManager;
		this.renderers.put(CaveSpiderEntity.class, new CaveSpiderEntityRenderer(this));
		this.renderers.put(SpiderEntity.class, new SpiderEntityRenderer(this));
		this.renderers.put(PigEntity.class, new PigEntityRenderer(this, new PigEntityModel(), 0.7F));
		this.renderers.put(SheepEntity.class, new SheepEntityRenderer(this, new SheepEntityModel(), 0.7F));
		this.renderers.put(CowEntity.class, new CowEntityRenderer(this, new CowEntityModel(), 0.7F));
		this.renderers.put(MooshroomEntity.class, new MooshroomEntityRenderer(this, new CowEntityModel(), 0.7F));
		this.renderers.put(WolfEntity.class, new WolfEntityRenderer(this, new WolfEntityModel(), 0.5F));
		this.renderers.put(ChickenEntity.class, new ChickenEntityRenderer(this, new ChickenEntityModel(), 0.3F));
		this.renderers.put(OcelotEntity.class, new CatEntityRenderer(this, new OcelotEntityModel(), 0.4F));
		this.renderers.put(RabbitEntity.class, new RabbitEntityRenderer(this, new RabbitEntityModel(), 0.3F));
		this.renderers.put(SilverfishEntity.class, new SilverfishEntityRenderer(this));
		this.renderers.put(EndermiteEntity.class, new EndermiteEntityRenderer(this));
		this.renderers.put(CreeperEntity.class, new CreeperEntityRenderer(this));
		this.renderers.put(EndermanEntity.class, new EndermanEntityRenderer(this));
		this.renderers.put(SnowGolemEntity.class, new SnowGolemEntityRenderer(this));
		this.renderers.put(SkeletonEntity.class, new SkeletonEntityRenderer(this));
		this.renderers.put(WitchEntity.class, new WitchEntityRenderer(this));
		this.renderers.put(BlazeEntity.class, new BlazeEntityRenderer(this));
		this.renderers.put(ZombiePigmanEntity.class, new ZombiePigmanEntityRenderer(this));
		this.renderers.put(ZombieEntity.class, new ZombieBaseEntityRenderer(this));
		this.renderers.put(SlimeEntity.class, new SlimeEntityRenderer(this, new SlimeEntityModel(16), 0.25F));
		this.renderers.put(MagmaCubeEntity.class, new MagmaCubeEntityRenderer(this));
		this.renderers.put(GiantEntity.class, new GiantEntityRenderer(this, new AbstractZombieModel(), 0.5F, 6.0F));
		this.renderers.put(GhastEntity.class, new GhastEntityRenderer(this));
		this.renderers.put(SquidEntity.class, new SquidEntityRenderer(this, new SquidEntityModel(), 0.7F));
		this.renderers.put(VillagerEntity.class, new VillagerEntityRenderer(this));
		this.renderers.put(IronGolemEntity.class, new IronGolemEntityRenderer(this));
		this.renderers.put(BatEntity.class, new BatEntityRenderer(this));
		this.renderers.put(GuardianEntity.class, new GuardianEntityRenderer(this));
		this.renderers.put(ShulkerEntity.class, new ShulkerEntityRenderer(this, new ShulkerEntityModel()));
		this.renderers.put(PolarBearEntity.class, new PolarBearEntityRenderer(this, new PolarBearEntityModel(), 0.7F));
		this.renderers.put(EnderDragonEntity.class, new EnderDragonEntityRenderer(this));
		this.renderers.put(EndCrystalEntity.class, new EnderCrystalEntityRenderer(this));
		this.renderers.put(WitherEntity.class, new WitherEntityRenderer(this));
		this.renderers.put(Entity.class, new AreaEffectCloudEntityRenderer(this));
		this.renderers.put(PaintingEntity.class, new PaintingEntityRenderer(this));
		this.renderers.put(ItemFrameEntity.class, new ItemFrameEntityRenderer(this, itemRenderer));
		this.renderers.put(LeashKnotEntity.class, new LeashKnotEntityRenderer(this));
		this.renderers.put(ArrowEntity.class, new ArrowEntityRenderer(this));
		this.renderers.put(SpectralArrowEntity.class, new SpectralArrowEntityRenderer(this));
		this.renderers.put(SnowballEntity.class, new FlyingItemEntityRenderer(this, Items.SNOWBALL, itemRenderer));
		this.renderers.put(EnderPearlEntity.class, new FlyingItemEntityRenderer(this, Items.ENDER_PEARL, itemRenderer));
		this.renderers.put(EyeOfEnderEntity.class, new FlyingItemEntityRenderer(this, Items.EYE_OF_ENDER, itemRenderer));
		this.renderers.put(EggEntity.class, new FlyingItemEntityRenderer(this, Items.EGG, itemRenderer));
		this.renderers.put(PotionEntity.class, new FlyingPotionEntityRenderer(this, itemRenderer));
		this.renderers.put(ExperienceBottleEntity.class, new FlyingItemEntityRenderer(this, Items.EXPERIENCE_BOTTLE, itemRenderer));
		this.renderers.put(FireworkRocketEntity.class, new FlyingItemEntityRenderer(this, Items.FIREWORKS, itemRenderer));
		this.renderers.put(FireballEntity.class, new FireballEntityRenderer(this, 2.0F));
		this.renderers.put(SmallFireballEntity.class, new FireballEntityRenderer(this, 0.5F));
		this.renderers.put(DragonFireballEntity.class, new DragonFireballEntityRenderer(this));
		this.renderers.put(WitherSkullEntity.class, new WitherSkullEntityRenderer(this));
		this.renderers.put(ShulkerBulletEntity.class, new ShulkerBulletEntityRenderer(this));
		this.renderers.put(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
		this.renderers.put(ExperienceOrbEntity.class, new ExperienceOrbEntityRenderer(this));
		this.renderers.put(TntEntity.class, new TntEntityRenderer(this));
		this.renderers.put(FallingBlockEntity.class, new FallingBlockEntityRenderer(this));
		this.renderers.put(ArmorStandEntity.class, new ArmorStandEntityRenderer(this));
		this.renderers.put(TntMinecartEntity.class, new TntMinecartEntityRenderer(this));
		this.renderers.put(SpawnerMinecartEntity.class, new SpawnerMinecartEntityRenderer(this));
		this.renderers.put(AbstractMinecartEntity.class, new MinecartEntityRenderer(this));
		this.renderers.put(BoatEntity.class, new BoatEntityRenderer(this));
		this.renderers.put(FishingBobberEntity.class, new FishingBobberEntityRenderer(this));
		this.renderers.put(AreaEffectCloudEntity.class, new EntityEntityRenderer(this));
		this.renderers.put(HorseBaseEntity.class, new HorseBaseEntityRenderer(this, new HorseBaseEntityModel(), 0.75F));
		this.renderers.put(LightningBoltEntity.class, new LightningEntityRenderer(this));
		this.playerRenderer = new PlayerEntityRenderer(this);
		this.modelRenderers.put("default", this.playerRenderer);
		this.modelRenderers.put("slim", new PlayerEntityRenderer(this, true));
	}

	public void updateCamera(double x, double y, double z) {
		this.CAMERA_X = x;
		this.CAMERA_Y = y;
		this.CAMERA_Z = z;
	}

	public <T extends Entity> EntityRenderer<T> getRenderer(Class<? extends Entity> clazz) {
		EntityRenderer<? extends Entity> entityRenderer = (EntityRenderer<? extends Entity>)this.renderers.get(clazz);
		if (entityRenderer == null && clazz != Entity.class) {
			entityRenderer = this.getRenderer(clazz.getSuperclass());
			this.renderers.put(clazz, entityRenderer);
		}

		return (EntityRenderer<T>)entityRenderer;
	}

	@Nullable
	public <T extends Entity> EntityRenderer<T> getRenderer(Entity entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			String string = ((AbstractClientPlayerEntity)entity).getModel();
			PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.modelRenderers.get(string);
			return playerEntityRenderer != null ? playerEntityRenderer : this.playerRenderer;
		} else {
			return this.getRenderer(entity.getClass());
		}
	}

	public void updateCamera(World world, TextRenderer textRenderer, Entity entity, Entity entity2, GameOptions gameOptions, float f) {
		this.world = world;
		this.options = gameOptions;
		this.field_11098 = entity;
		this.field_7998 = entity2;
		this.textRenderer = textRenderer;
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			BlockState blockState = world.getBlockState(new BlockPos(entity));
			Block block = blockState.getBlock();
			if (block == Blocks.BED) {
				int i = ((Direction)blockState.get(BedBlock.DIRECTION)).getHorizontal();
				this.yaw = (float)(i * 90 + 180);
				this.pitch = 0.0F;
			}
		} else {
			this.yaw = entity.prevYaw + (entity.yaw - entity.prevYaw) * f;
			this.pitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * f;
		}

		if (gameOptions.perspective == 2) {
			this.yaw += 180.0F;
		}

		this.cameraX = entity.prevTickX + (entity.x - entity.prevTickX) * (double)f;
		this.cameraY = entity.prevTickY + (entity.y - entity.prevTickY) * (double)f;
		this.cameraZ = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)f;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public boolean shouldRenderShadows() {
		return this.renderShadows;
	}

	public void setRenderShadows(boolean value) {
		this.renderShadows = value;
	}

	public void setRenderHitboxes(boolean renderHitboxes) {
		this.renderHitboxes = renderHitboxes;
	}

	public boolean getRenderHitboxes() {
		return this.renderHitboxes;
	}

	public boolean method_12449(Entity entity) {
		return this.getRenderer(entity).method_12450();
	}

	public boolean shouldRender(Entity entity, CameraView cameraView, double d, double e, double f) {
		EntityRenderer<Entity> entityRenderer = this.getRenderer(entity);
		return entityRenderer != null && entityRenderer.shouldRender(entity, cameraView, d, e, f);
	}

	public void method_12448(Entity entity, float f, boolean bl) {
		if (entity.ticksAlive == 0) {
			entity.prevTickX = entity.x;
			entity.prevTickY = entity.y;
			entity.prevTickZ = entity.z;
		}

		double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)f;
		double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)f;
		double g = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)f;
		float h = entity.prevYaw + (entity.yaw - entity.prevYaw) * f;
		int i = entity.getLightmapCoordinates(f);
		if (entity.isOnFire()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)j, (float)k);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.method_12446(entity, d - this.CAMERA_X, e - this.CAMERA_Y, g - this.CAMERA_Z, h, f, bl);
	}

	public void method_12446(Entity entity, double d, double e, double f, float g, float h, boolean bl) {
		EntityRenderer<Entity> entityRenderer = null;

		try {
			entityRenderer = this.getRenderer(entity);
			if (entityRenderer != null && this.textureManager != null) {
				try {
					entityRenderer.method_12452(this.field_11101);
					entityRenderer.render(entity, d, e, f, g, h);
				} catch (Throwable var17) {
					throw new CrashException(CrashReport.create(var17, "Rendering entity in world"));
				}

				try {
					if (!this.field_11101) {
						entityRenderer.postRender(entity, d, e, f, g, h);
					}
				} catch (Throwable var18) {
					throw new CrashException(CrashReport.create(var18, "Post-rendering entity in world"));
				}

				if (this.renderHitboxes && !entity.isInvisible() && !bl && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
					try {
						this.renderHitbox(entity, d, e, f, g, h);
					} catch (Throwable var16) {
						throw new CrashException(CrashReport.create(var16, "Rendering entity hitbox in world"));
					}
				}
			}
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Rendering entity in world");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
			entity.populateCrashReport(crashReportSection);
			CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
			crashReportSection2.add("Assigned renderer", entityRenderer);
			crashReportSection2.add("Location", CrashReportSection.createPositionString(d, e, f));
			crashReportSection2.add("Rotation", g);
			crashReportSection2.add("Delta", h);
			throw new CrashException(crashReport);
		}
	}

	public void method_12447(Entity entity, float f) {
		if (entity.ticksAlive == 0) {
			entity.prevTickX = entity.x;
			entity.prevTickY = entity.y;
			entity.prevTickZ = entity.z;
		}

		double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)f;
		double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)f;
		double g = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)f;
		float h = entity.prevYaw + (entity.yaw - entity.prevYaw) * f;
		int i = entity.getLightmapCoordinates(f);
		if (entity.isOnFire()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)j, (float)k);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		EntityRenderer<Entity> entityRenderer = this.getRenderer(entity);
		if (entityRenderer != null && this.textureManager != null) {
			entityRenderer.method_12453(entity, d - this.CAMERA_X, e - this.CAMERA_Y, g - this.CAMERA_Z, h, f);
		}
	}

	private void renderHitbox(Entity entity, double d, double e, double f, float g, float h) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		float i = entity.width / 2.0F;
		Box box = entity.getBoundingBox();
		WorldRenderer.method_13429(
			box.minX - entity.x + d,
			box.minY - entity.y + e,
			box.minZ - entity.z + f,
			box.maxX - entity.x + d,
			box.maxY - entity.y + e,
			box.maxZ - entity.z + f,
			1.0F,
			1.0F,
			1.0F,
			1.0F
		);
		if (entity instanceof LivingEntity) {
			float j = 0.01F;
			WorldRenderer.method_13429(
				d - (double)i,
				e + (double)entity.getEyeHeight() - 0.01F,
				f - (double)i,
				d + (double)i,
				e + (double)entity.getEyeHeight() + 0.01F,
				f + (double)i,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Vec3d vec3d = entity.getRotationVector(h);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(d, e + (double)entity.getEyeHeight(), f).color(0, 0, 255, 255).next();
		bufferBuilder.vertex(d + vec3d.x * 2.0, e + (double)entity.getEyeHeight() + vec3d.y * 2.0, f + vec3d.z * 2.0).color(0, 0, 255, 255).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.field_11098 = null;
		}
	}

	public double squaredDistanceToCamera(double x, double y, double z) {
		double d = x - this.cameraX;
		double e = y - this.cameraY;
		double f = z - this.cameraZ;
		return d * d + e * e + f * f;
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	public void method_10206(boolean bl) {
		this.field_11101 = bl;
	}
}
