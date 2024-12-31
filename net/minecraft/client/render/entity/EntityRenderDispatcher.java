package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class EntityRenderDispatcher {
	private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityNoOutline(new Identifier("textures/misc/shadow.png"));
	private final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.newHashMap();
	private final Map<String, PlayerEntityRenderer> modelRenderers = Maps.newHashMap();
	private final PlayerEntityRenderer playerRenderer;
	private final TextRenderer textRenderer;
	public final TextureManager textureManager;
	private World world;
	public Camera camera;
	private Quaternion rotation;
	public Entity targetedEntity;
	public final GameOptions gameOptions;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public <E extends Entity> int getLight(E entity, float f) {
		return this.getRenderer(entity).getLight(entity, f);
	}

	private <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> entityRenderer) {
		this.renderers.put(entityType, entityRenderer);
	}

	private void registerRenderers(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
		this.register(EntityType.field_6083, new AreaEffectCloudEntityRenderer(this));
		this.register(EntityType.field_6131, new ArmorStandEntityRenderer(this));
		this.register(EntityType.field_6122, new ArrowEntityRenderer(this));
		this.register(EntityType.field_6108, new BatEntityRenderer(this));
		this.register(EntityType.field_20346, new BeeEntityRenderer(this));
		this.register(EntityType.field_6099, new BlazeEntityRenderer(this));
		this.register(EntityType.field_6121, new BoatEntityRenderer(this));
		this.register(EntityType.field_16281, new CatEntityRenderer(this));
		this.register(EntityType.field_6084, new CaveSpiderEntityRenderer(this));
		this.register(EntityType.field_6126, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6132, new ChickenEntityRenderer(this));
		this.register(EntityType.field_6070, new CodEntityRenderer(this));
		this.register(EntityType.field_6136, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6085, new CowEntityRenderer(this));
		this.register(EntityType.field_6046, new CreeperEntityRenderer(this));
		this.register(EntityType.field_6087, new DolphinEntityRenderer(this));
		this.register(EntityType.field_6067, new DonkeyEntityRenderer<>(this, 0.87F));
		this.register(EntityType.field_6129, new DragonFireballEntityRenderer(this));
		this.register(EntityType.field_6123, new DrownedEntityRenderer(this));
		this.register(EntityType.field_6144, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6086, new ElderGuardianEntityRenderer(this));
		this.register(EntityType.field_6110, new EnderCrystalEntityRenderer(this));
		this.register(EntityType.field_6116, new EnderDragonEntityRenderer(this));
		this.register(EntityType.field_6091, new EndermanEntityRenderer(this));
		this.register(EntityType.field_6128, new EndermiteEntityRenderer(this));
		this.register(EntityType.field_6082, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6060, new EvokerFangsEntityRenderer(this));
		this.register(EntityType.field_6090, new EvokerIllagerEntityRenderer<>(this));
		this.register(EntityType.field_6064, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6044, new ExperienceOrbEntityRenderer(this));
		this.register(EntityType.field_6061, new FlyingItemEntityRenderer<>(this, itemRenderer, 1.0F, true));
		this.register(EntityType.field_6089, new FallingBlockEntityRenderer(this));
		this.register(EntityType.field_6066, new FlyingItemEntityRenderer<>(this, itemRenderer, 3.0F, true));
		this.register(EntityType.field_6133, new FireworkEntityRenderer(this, itemRenderer));
		this.register(EntityType.field_6103, new FishingBobberEntityRenderer(this));
		this.register(EntityType.field_17943, new FoxEntityRenderer(this));
		this.register(EntityType.field_6080, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6107, new GhastEntityRenderer(this));
		this.register(EntityType.field_6095, new GiantEntityRenderer(this, 6.0F));
		this.register(EntityType.field_6118, new GuardianEntityRenderer(this));
		this.register(EntityType.field_6058, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6139, new HorseEntityRenderer(this));
		this.register(EntityType.field_6071, new HuskEntityRenderer(this));
		this.register(EntityType.field_6065, new IllusionerEntityRenderer(this));
		this.register(EntityType.field_6147, new IronGolemEntityRenderer(this));
		this.register(EntityType.field_6043, new ItemFrameEntityRenderer(this, itemRenderer));
		this.register(EntityType.field_6052, new ItemEntityRenderer(this, itemRenderer));
		this.register(EntityType.field_6138, new LeashKnotEntityRenderer(this));
		this.register(EntityType.field_6112, new LightningEntityRenderer(this));
		this.register(EntityType.field_6074, new LlamaEntityRenderer(this));
		this.register(EntityType.field_6124, new LlamaSpitEntityRenderer(this));
		this.register(EntityType.field_6102, new MagmaCubeEntityRenderer(this));
		this.register(EntityType.field_6096, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6143, new MooshroomEntityRenderer(this));
		this.register(EntityType.field_6057, new DonkeyEntityRenderer<>(this, 0.92F));
		this.register(EntityType.field_6081, new OcelotEntityRenderer(this));
		this.register(EntityType.field_6120, new PaintingEntityRenderer(this));
		this.register(EntityType.field_6146, new PandaEntityRenderer(this));
		this.register(EntityType.field_6104, new ParrotEntityRenderer(this));
		this.register(EntityType.field_6078, new PhantomEntityRenderer(this));
		this.register(EntityType.field_6093, new PigEntityRenderer(this));
		this.register(EntityType.field_6105, new PillagerEntityRenderer(this));
		this.register(EntityType.field_6042, new PolarBearEntityRenderer(this));
		this.register(EntityType.field_6045, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6062, new PufferfishEntityRenderer(this));
		this.register(EntityType.field_6140, new RabbitEntityRenderer(this));
		this.register(EntityType.field_6134, new RavagerEntityRenderer(this));
		this.register(EntityType.field_6073, new SalmonEntityRenderer(this));
		this.register(EntityType.field_6115, new SheepEntityRenderer(this));
		this.register(EntityType.field_6100, new ShulkerBulletEntityRenderer(this));
		this.register(EntityType.field_6109, new ShulkerEntityRenderer(this));
		this.register(EntityType.field_6125, new SilverfishEntityRenderer(this));
		this.register(EntityType.field_6075, new ZombieHorseEntityRenderer(this));
		this.register(EntityType.field_6137, new SkeletonEntityRenderer(this));
		this.register(EntityType.field_6069, new SlimeEntityRenderer(this));
		this.register(EntityType.field_6049, new FlyingItemEntityRenderer<>(this, itemRenderer, 0.75F, true));
		this.register(EntityType.field_6068, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6047, new SnowGolemEntityRenderer(this));
		this.register(EntityType.field_6142, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6135, new SpectralArrowEntityRenderer(this));
		this.register(EntityType.field_6079, new SpiderEntityRenderer<>(this));
		this.register(EntityType.field_6114, new SquidEntityRenderer(this));
		this.register(EntityType.field_6098, new StrayEntityRenderer(this));
		this.register(EntityType.field_6053, new TntMinecartEntityRenderer(this));
		this.register(EntityType.field_6063, new TntEntityRenderer(this));
		this.register(EntityType.field_17714, new LlamaEntityRenderer(this));
		this.register(EntityType.field_6127, new TridentEntityRenderer(this));
		this.register(EntityType.field_6111, new TropicalFishEntityRenderer(this));
		this.register(EntityType.field_6113, new TurtleEntityRenderer(this));
		this.register(EntityType.field_6059, new VexEntityRenderer(this));
		this.register(EntityType.field_6077, new VillagerEntityRenderer(this, reloadableResourceManager));
		this.register(EntityType.field_6117, new VindicatorEntityRenderer(this));
		this.register(EntityType.field_17713, new WanderingTraderEntityRenderer(this));
		this.register(EntityType.field_6145, new WitchEntityRenderer(this));
		this.register(EntityType.field_6119, new WitherEntityRenderer(this));
		this.register(EntityType.field_6076, new WitherSkeletonEntityRenderer(this));
		this.register(EntityType.field_6130, new WitherSkullEntityRenderer(this));
		this.register(EntityType.field_6055, new WolfEntityRenderer(this));
		this.register(EntityType.field_6048, new ZombieHorseEntityRenderer(this));
		this.register(EntityType.field_6051, new ZombieEntityRenderer(this));
		this.register(EntityType.field_6050, new ZombiePigmanEntityRenderer(this));
		this.register(EntityType.field_6054, new ZombieVillagerEntityRenderer(this, reloadableResourceManager));
	}

	public EntityRenderDispatcher(
		TextureManager textureManager,
		ItemRenderer itemRenderer,
		ReloadableResourceManager reloadableResourceManager,
		TextRenderer textRenderer,
		GameOptions gameOptions
	) {
		this.textureManager = textureManager;
		this.textRenderer = textRenderer;
		this.gameOptions = gameOptions;
		this.registerRenderers(itemRenderer, reloadableResourceManager);
		this.playerRenderer = new PlayerEntityRenderer(this);
		this.modelRenderers.put("default", this.playerRenderer);
		this.modelRenderers.put("slim", new PlayerEntityRenderer(this, true));

		for (EntityType<?> entityType : Registry.field_11145) {
			if (entityType != EntityType.field_6097 && !this.renderers.containsKey(entityType)) {
				throw new IllegalStateException("No renderer registered for " + Registry.field_11145.getId(entityType));
			}
		}
	}

	public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			String string = ((AbstractClientPlayerEntity)entity).getModel();
			PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.modelRenderers.get(string);
			return playerEntityRenderer != null ? playerEntityRenderer : this.playerRenderer;
		} else {
			return (EntityRenderer<? super T>)this.renderers.get(entity.getType());
		}
	}

	public void configure(World world, Camera camera, Entity entity) {
		this.world = world;
		this.camera = camera;
		this.rotation = camera.getRotation();
		this.targetedEntity = entity;
	}

	public void setRotation(Quaternion quaternion) {
		this.rotation = quaternion;
	}

	public void setRenderShadows(boolean bl) {
		this.renderShadows = bl;
	}

	public void setRenderHitboxes(boolean bl) {
		this.renderHitboxes = bl;
	}

	public boolean shouldRenderHitboxes() {
		return this.renderHitboxes;
	}

	public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double d, double e, double f) {
		EntityRenderer<? super E> entityRenderer = this.getRenderer(entity);
		return entityRenderer.shouldRender(entity, frustum, d, e, f);
	}

	public <E extends Entity> void render(
		E entity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		EntityRenderer<? super E> entityRenderer = this.getRenderer(entity);

		try {
			Vec3d vec3d = entityRenderer.getPositionOffset(entity, h);
			double j = d + vec3d.getX();
			double k = e + vec3d.getY();
			double l = f + vec3d.getZ();
			matrixStack.push();
			matrixStack.translate(j, k, l);
			entityRenderer.render(entity, g, h, matrixStack, vertexConsumerProvider, i);
			if (entity.doesRenderOnFire()) {
				this.renderFire(matrixStack, vertexConsumerProvider, entity);
			}

			matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
			if (this.gameOptions.entityShadows && this.renderShadows && entityRenderer.shadowSize > 0.0F && !entity.isInvisible()) {
				double m = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ());
				float n = (float)((1.0 - m / 256.0) * (double)entityRenderer.shadowDarkness);
				if (n > 0.0F) {
					renderShadow(matrixStack, vertexConsumerProvider, entity, n, h, this.world, entityRenderer.shadowSize);
				}
			}

			if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
				this.renderHitbox(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getLines()), entity, h);
			}

			matrixStack.pop();
		} catch (Throwable var24) {
			CrashReport crashReport = CrashReport.create(var24, "Rendering entity in world");
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

	private void renderHitbox(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f) {
		float g = entity.getWidth() / 2.0F;
		this.method_23164(matrixStack, vertexConsumer, entity, 1.0F, 1.0F, 1.0F);
		if (entity instanceof EnderDragonEntity) {
			double d = entity.getX() - MathHelper.lerp((double)f, entity.lastRenderX, entity.getX());
			double e = entity.getY() - MathHelper.lerp((double)f, entity.lastRenderY, entity.getY());
			double h = entity.getZ() - MathHelper.lerp((double)f, entity.lastRenderZ, entity.getZ());

			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
				matrixStack.push();
				double i = d + MathHelper.lerp((double)f, enderDragonPart.lastRenderX, enderDragonPart.getX());
				double j = e + MathHelper.lerp((double)f, enderDragonPart.lastRenderY, enderDragonPart.getY());
				double k = h + MathHelper.lerp((double)f, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
				matrixStack.translate(i, j, k);
				this.method_23164(matrixStack, vertexConsumer, enderDragonPart, 0.25F, 1.0F, 0.0F);
				matrixStack.pop();
			}
		}

		if (entity instanceof LivingEntity) {
			float l = 0.01F;
			WorldRenderer.drawBox(
				matrixStack,
				vertexConsumer,
				(double)(-g),
				(double)(entity.getStandingEyeHeight() - 0.01F),
				(double)(-g),
				(double)g,
				(double)(entity.getStandingEyeHeight() + 0.01F),
				(double)g,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		Vec3d vec3d = entity.getRotationVec(f);
		Matrix4f matrix4f = matrixStack.peek().getModel();
		vertexConsumer.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).next();
		vertexConsumer.vertex(matrix4f, (float)(vec3d.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d.y * 2.0), (float)(vec3d.z * 2.0))
			.color(0, 0, 255, 255)
			.next();
	}

	private void method_23164(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f, float g, float h) {
		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		WorldRenderer.drawBox(matrixStack, vertexConsumer, box, f, g, h, 1.0F);
	}

	private void renderFire(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity) {
		Sprite sprite = ModelLoader.FIRE_0.getSprite();
		Sprite sprite2 = ModelLoader.FIRE_1.getSprite();
		matrixStack.push();
		float f = entity.getWidth() * 1.4F;
		matrixStack.scale(f, f, f);
		float g = 0.5F;
		float h = 0.0F;
		float i = entity.getHeight() / f;
		float j = 0.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-this.camera.getYaw()));
		matrixStack.translate(0.0, 0.0, (double)(-0.3F + (float)((int)i) * 0.02F));
		float k = 0.0F;
		int l = 0;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntityCutout());

		for (MatrixStack.Entry entry = matrixStack.peek(); i > 0.0F; l++) {
			Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
			float m = sprite3.getMinU();
			float n = sprite3.getMinV();
			float o = sprite3.getMaxU();
			float p = sprite3.getMaxV();
			if (l / 2 % 2 == 0) {
				float q = o;
				o = m;
				m = q;
			}

			fireVertex(entry, vertexConsumer, g - 0.0F, 0.0F - j, k, o, p);
			fireVertex(entry, vertexConsumer, -g - 0.0F, 0.0F - j, k, m, p);
			fireVertex(entry, vertexConsumer, -g - 0.0F, 1.4F - j, k, m, n);
			fireVertex(entry, vertexConsumer, g - 0.0F, 1.4F - j, k, o, n);
			i -= 0.45F;
			j -= 0.45F;
			g *= 0.9F;
			k += 0.03F;
		}

		matrixStack.pop();
	}

	private static void fireVertex(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j) {
		vertexConsumer.vertex(entry.getModel(), f, g, h)
			.color(255, 255, 255, 255)
			.texture(i, j)
			.overlay(0, 10)
			.light(240)
			.normal(entry.getNormal(), 0.0F, 1.0F, 0.0F)
			.next();
	}

	private static void renderShadow(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity, float f, float g, WorldView worldView, float h
	) {
		float i = h;
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			if (mobEntity.isBaby()) {
				i = h * 0.5F;
			}
		}

		double d = MathHelper.lerp((double)g, entity.lastRenderX, entity.getX());
		double e = MathHelper.lerp((double)g, entity.lastRenderY, entity.getY());
		double j = MathHelper.lerp((double)g, entity.lastRenderZ, entity.getZ());
		int k = MathHelper.floor(d - (double)i);
		int l = MathHelper.floor(d + (double)i);
		int m = MathHelper.floor(e - (double)i);
		int n = MathHelper.floor(e);
		int o = MathHelper.floor(j - (double)i);
		int p = MathHelper.floor(j + (double)i);
		MatrixStack.Entry entry = matrixStack.peek();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(SHADOW_LAYER);

		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(k, m, o), new BlockPos(l, n, p))) {
			renderShadowPart(entry, vertexConsumer, worldView, blockPos, d, e, j, i, f);
		}
	}

	private static void renderShadowPart(
		MatrixStack.Entry entry, VertexConsumer vertexConsumer, WorldView worldView, BlockPos blockPos, double d, double e, double f, float g, float h
	) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = worldView.getBlockState(blockPos2);
		if (blockState.getRenderType() != BlockRenderType.field_11455 && worldView.getLightLevel(blockPos) > 3) {
			if (blockState.isFullCube(worldView, blockPos2)) {
				VoxelShape voxelShape = blockState.getOutlineShape(worldView, blockPos.down());
				if (!voxelShape.isEmpty()) {
					float i = (float)(((double)h - (e - (double)blockPos.getY()) / 2.0) * 0.5 * (double)worldView.getBrightness(blockPos));
					if (i >= 0.0F) {
						if (i > 1.0F) {
							i = 1.0F;
						}

						Box box = voxelShape.getBoundingBox();
						double j = (double)blockPos.getX() + box.x1;
						double k = (double)blockPos.getX() + box.x2;
						double l = (double)blockPos.getY() + box.y1;
						double m = (double)blockPos.getZ() + box.z1;
						double n = (double)blockPos.getZ() + box.z2;
						float o = (float)(j - d);
						float p = (float)(k - d);
						float q = (float)(l - e + 0.015625);
						float r = (float)(m - f);
						float s = (float)(n - f);
						float t = -o / 2.0F / g + 0.5F;
						float u = -p / 2.0F / g + 0.5F;
						float v = -r / 2.0F / g + 0.5F;
						float w = -s / 2.0F / g + 0.5F;
						shadowVertex(entry, vertexConsumer, i, o, q, r, t, v);
						shadowVertex(entry, vertexConsumer, i, o, q, s, t, w);
						shadowVertex(entry, vertexConsumer, i, p, q, s, u, w);
						shadowVertex(entry, vertexConsumer, i, p, q, r, u, v);
					}
				}
			}
		}
	}

	private static void shadowVertex(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k) {
		vertexConsumer.vertex(entry.getModel(), g, h, i)
			.color(1.0F, 1.0F, 1.0F, f)
			.texture(j, k)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(15728880)
			.normal(entry.getNormal(), 0.0F, 1.0F, 0.0F)
			.next();
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.camera = null;
		}
	}

	public double getSquaredDistanceToCamera(Entity entity) {
		return this.camera.getPos().squaredDistanceTo(entity.getPos());
	}

	public double getSquaredDistanceToCamera(double d, double e, double f) {
		return this.camera.getPos().squaredDistanceTo(d, e, f);
	}

	public Quaternion getRotation() {
		return this.rotation;
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}
}
