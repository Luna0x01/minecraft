package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.class_4218;
import net.minecraft.class_4288;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.CameraView;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.RenderBlockView;

public abstract class EntityRenderer<T extends Entity> {
	private static final Identifier SHADOW_TEXTURE = new Identifier("textures/misc/shadow.png");
	protected final EntityRenderDispatcher dispatcher;
	protected float shadowSize;
	protected float shadowDarkness = 1.0F;
	protected boolean field_13631;

	protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this.dispatcher = entityRenderDispatcher;
	}

	public void method_12452(boolean bl) {
		this.field_13631 = bl;
	}

	public boolean shouldRender(T entity, CameraView cameraView, double x, double y, double z) {
		Box box = entity.getVisibilityBoundingBox().expand(0.5);
		if (box.isInvalid() || box.getAverage() == 0.0) {
			box = new Box(entity.x - 2.0, entity.y - 2.0, entity.z - 2.0, entity.x + 2.0, entity.y + 2.0, entity.z + 2.0);
		}

		return entity.shouldRender(x, y, z) && (entity.ignoreCameraFrustum || cameraView.isBoxInFrustum(box));
	}

	public void render(T entity, double x, double y, double z, float yaw, float tickDelta) {
		if (!this.field_13631) {
			this.method_10208(entity, x, y, z);
		}
	}

	protected int method_12454(T entity) {
		Team team = (Team)entity.getScoreboardTeam();
		return team != null && team.method_12130().method_15108() != null ? team.method_12130().method_15108() : 16777215;
	}

	protected void method_10208(T entity, double d, double e, double f) {
		if (this.hasLabel(entity)) {
			this.renderLabelIfPresent(entity, entity.getName().asFormattedString(), d, e, f, 64);
		}
	}

	protected boolean hasLabel(T entity) {
		return entity.shouldRenderName() && entity.hasCustomName();
	}

	protected void method_10209(T entity, double d, double e, double f, String string, double g) {
		this.renderLabelIfPresent(entity, string, d, e, f, 64);
	}

	@Nullable
	protected abstract Identifier getTexture(T entity);

	protected boolean bindTexture(T entity) {
		Identifier identifier = this.getTexture(entity);
		if (identifier == null) {
			return false;
		} else {
			this.bindTexture(identifier);
			return true;
		}
	}

	public void bindTexture(Identifier id) {
		this.dispatcher.textureManager.bindTexture(id);
	}

	private void renderFire(Entity entity, double x, double y, double z, float f) {
		GlStateManager.disableLighting();
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlasTexture();
		Sprite sprite = spriteAtlasTexture.method_19509(class_4288.field_21064);
		Sprite sprite2 = spriteAtlasTexture.method_19509(class_4288.field_21065);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
		float g = entity.width * 1.4F;
		GlStateManager.scale(g, g, g);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float h = 0.5F;
		float i = 0.0F;
		float j = entity.height / g;
		float k = (float)(entity.y - entity.getBoundingBox().minY);
		GlStateManager.rotate(-this.dispatcher.yaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, -0.3F + (float)((int)j) * 0.02F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float l = 0.0F;
		int m = 0;
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);

		while (j > 0.0F) {
			Sprite sprite3 = m % 2 == 0 ? sprite : sprite2;
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float n = sprite3.getMinU();
			float o = sprite3.getMinV();
			float p = sprite3.getMaxU();
			float q = sprite3.getMaxV();
			if (m / 2 % 2 == 0) {
				float r = p;
				p = n;
				n = r;
			}

			bufferBuilder.vertex((double)(h - 0.0F), (double)(0.0F - k), (double)l).texture((double)p, (double)q).next();
			bufferBuilder.vertex((double)(-h - 0.0F), (double)(0.0F - k), (double)l).texture((double)n, (double)q).next();
			bufferBuilder.vertex((double)(-h - 0.0F), (double)(1.4F - k), (double)l).texture((double)n, (double)o).next();
			bufferBuilder.vertex((double)(h - 0.0F), (double)(1.4F - k), (double)l).texture((double)p, (double)o).next();
			j -= 0.45F;
			k -= 0.45F;
			h *= 0.9F;
			l += 0.03F;
			m++;
		}

		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
	}

	private void renderShadow(Entity entity, double x, double y, double z, float f, float tickDelta) {
		GlStateManager.enableBlend();
		GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
		this.dispatcher.textureManager.bindTexture(SHADOW_TEXTURE);
		RenderBlockView renderBlockView = this.method_1533();
		GlStateManager.depthMask(false);
		float g = this.shadowSize;
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			g *= mobEntity.method_2638();
			if (mobEntity.isBaby()) {
				g *= 0.5F;
			}
		}

		double d = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
		double e = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
		double h = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
		int i = MathHelper.floor(d - (double)g);
		int j = MathHelper.floor(d + (double)g);
		int k = MathHelper.floor(e - (double)g);
		int l = MathHelper.floor(e);
		int m = MathHelper.floor(h - (double)g);
		int n = MathHelper.floor(h + (double)g);
		double o = x - d;
		double p = y - e;
		double q = z - h;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);

		for (BlockPos blockPos : BlockPos.mutableIterate(new BlockPos(i, k, m), new BlockPos(j, l, n))) {
			BlockState blockState = renderBlockView.getBlockState(blockPos.down());
			if (blockState.getRenderType() != BlockRenderType.INVISIBLE && renderBlockView.method_16358(blockPos) > 3) {
				this.method_12451(blockState, x, y, z, blockPos, f, g, o, p, q);
			}
		}

		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	private RenderBlockView method_1533() {
		return this.dispatcher.world;
	}

	private void method_12451(BlockState blockState, double d, double e, double f, BlockPos blockPos, float g, float h, double i, double j, double k) {
		if (blockState.method_16897()) {
			VoxelShape voxelShape = blockState.getOutlineShape(this.method_1533(), blockPos.down());
			if (!voxelShape.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				double l = ((double)g - (e - ((double)blockPos.getY() + j)) / 2.0) * 0.5 * (double)this.method_1533().method_16356(blockPos);
				if (!(l < 0.0)) {
					if (l > 1.0) {
						l = 1.0;
					}

					Box box = voxelShape.getBoundingBox();
					double m = (double)blockPos.getX() + box.minX + i;
					double n = (double)blockPos.getX() + box.maxX + i;
					double o = (double)blockPos.getY() + box.minY + j + 0.015625;
					double p = (double)blockPos.getZ() + box.minZ + k;
					double q = (double)blockPos.getZ() + box.maxZ + k;
					float r = (float)((d - m) / 2.0 / (double)h + 0.5);
					float s = (float)((d - n) / 2.0 / (double)h + 0.5);
					float t = (float)((f - p) / 2.0 / (double)h + 0.5);
					float u = (float)((f - q) / 2.0 / (double)h + 0.5);
					bufferBuilder.vertex(m, o, p).texture((double)r, (double)t).color(1.0F, 1.0F, 1.0F, (float)l).next();
					bufferBuilder.vertex(m, o, q).texture((double)r, (double)u).color(1.0F, 1.0F, 1.0F, (float)l).next();
					bufferBuilder.vertex(n, o, q).texture((double)s, (double)u).color(1.0F, 1.0F, 1.0F, (float)l).next();
					bufferBuilder.vertex(n, o, p).texture((double)s, (double)t).color(1.0F, 1.0F, 1.0F, (float)l).next();
				}
			}
		}
	}

	public static void method_1527(Box box, double d, double e, double f) {
		GlStateManager.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bufferBuilder.offset(d, e, f);
		bufferBuilder.begin(7, VertexFormats.POSITION_NORMAL);
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(1.0F, 0.0F, 0.0F).next();
		tessellator.draw();
		bufferBuilder.offset(0.0, 0.0, 0.0);
		GlStateManager.enableTexture();
	}

	public void postRender(Entity entity, double x, double y, double z, float yaw, float tickDelta) {
		if (this.dispatcher.options != null) {
			if (this.dispatcher.options.entityShadows && this.shadowSize > 0.0F && !entity.isInvisible() && this.dispatcher.shouldRenderShadows()) {
				double d = this.dispatcher.squaredDistanceToCamera(entity.x, entity.y, entity.z);
				float f = (float)((1.0 - d / 256.0) * (double)this.shadowDarkness);
				if (f > 0.0F) {
					this.renderShadow(entity, x, y, z, f, tickDelta);
				}
			}

			if (entity.doesRenderOnFire() && (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).isSpectator())) {
				this.renderFire(entity, x, y, z, tickDelta);
			}
		}
	}

	public TextRenderer getFontRenderer() {
		return this.dispatcher.getTextRenderer();
	}

	protected void renderLabelIfPresent(T entity, String text, double x, double y, double z, int maxDistance) {
		double d = entity.squaredDistanceTo(this.dispatcher.field_11098);
		if (!(d > (double)(maxDistance * maxDistance))) {
			boolean bl = entity.isSneaking();
			float f = this.dispatcher.yaw;
			float g = this.dispatcher.pitch;
			boolean bl2 = this.dispatcher.options.perspective == 2;
			float h = entity.height + 0.5F - (bl ? 0.25F : 0.0F);
			int i = "deadmau5".equals(text) ? -10 : 0;
			class_4218.method_19068(this.getFontRenderer(), text, (float)x, (float)y + h, (float)z, i, f, g, bl2, bl);
		}
	}

	public EntityRenderDispatcher getRenderManager() {
		return this.dispatcher;
	}

	public boolean method_12450() {
		return false;
	}

	public void method_12453(T entity, double d, double e, double f, float g, float h) {
	}
}
