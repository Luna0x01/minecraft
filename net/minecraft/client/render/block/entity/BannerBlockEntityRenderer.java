package net.minecraft.client.render.block.entity;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class BannerBlockEntityRenderer implements BlockEntityRenderer<BannerBlockEntity> {
	private static final int field_32817 = 20;
	private static final int field_32818 = 40;
	private static final int field_32819 = 16;
	public static final String BANNER = "flag";
	private static final String PILLAR = "pole";
	private static final String CROSSBAR = "bar";
	private final ModelPart banner;
	private final ModelPart pillar;
	private final ModelPart crossbar;

	public BannerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.BANNER);
		this.banner = modelPart.getChild("flag");
		this.pillar = modelPart.getChild("pole");
		this.crossbar = modelPart.getChild("bar");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("flag", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), ModelTransform.NONE);
		modelPartData.addChild("pole", ModelPartBuilder.create().uv(44, 0).cuboid(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("bar", ModelPartBuilder.create().uv(0, 42).cuboid(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void render(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		List<Pair<BannerPattern, DyeColor>> list = bannerBlockEntity.getPatterns();
		if (list != null) {
			float g = 0.6666667F;
			boolean bl = bannerBlockEntity.getWorld() == null;
			matrixStack.push();
			long l;
			if (bl) {
				l = 0L;
				matrixStack.translate(0.5, 0.5, 0.5);
				this.pillar.visible = true;
			} else {
				l = bannerBlockEntity.getWorld().getTime();
				BlockState blockState = bannerBlockEntity.getCachedState();
				if (blockState.getBlock() instanceof BannerBlock) {
					matrixStack.translate(0.5, 0.5, 0.5);
					float h = (float)(-(Integer)blockState.get(BannerBlock.ROTATION) * 360) / 16.0F;
					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
					this.pillar.visible = true;
				} else {
					matrixStack.translate(0.5, -0.16666667F, 0.5);
					float k = -((Direction)blockState.get(WallBannerBlock.FACING)).asRotation();
					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(k));
					matrixStack.translate(0.0, -0.3125, -0.4375);
					this.pillar.visible = false;
				}
			}

			matrixStack.push();
			matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
			VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
			this.pillar.render(matrixStack, vertexConsumer, i, j);
			this.crossbar.render(matrixStack, vertexConsumer, i, j);
			BlockPos blockPos = bannerBlockEntity.getPos();
			float n = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0F;
			this.banner.pitch = (-0.0125F + 0.01F * MathHelper.cos((float) (Math.PI * 2) * n)) * (float) Math.PI;
			this.banner.pivotY = -32.0F;
			renderCanvas(matrixStack, vertexConsumerProvider, i, j, this.banner, ModelLoader.BANNER_BASE, true, list);
			matrixStack.pop();
			matrixStack.pop();
		}
	}

	public static void renderCanvas(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		ModelPart canvas,
		SpriteIdentifier baseSprite,
		boolean isBanner,
		List<Pair<BannerPattern, DyeColor>> patterns
	) {
		renderCanvas(matrices, vertexConsumers, light, overlay, canvas, baseSprite, isBanner, patterns, false);
	}

	public static void renderCanvas(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		ModelPart canvas,
		SpriteIdentifier baseSprite,
		boolean isBanner,
		List<Pair<BannerPattern, DyeColor>> patterns,
		boolean glint
	) {
		canvas.render(matrices, baseSprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid, glint), light, overlay);

		for (int i = 0; i < 17 && i < patterns.size(); i++) {
			Pair<BannerPattern, DyeColor> pair = (Pair<BannerPattern, DyeColor>)patterns.get(i);
			float[] fs = ((DyeColor)pair.getSecond()).getColorComponents();
			BannerPattern bannerPattern = (BannerPattern)pair.getFirst();
			SpriteIdentifier spriteIdentifier = isBanner
				? TexturedRenderLayers.getBannerPatternTextureId(bannerPattern)
				: TexturedRenderLayers.getShieldPatternTextureId(bannerPattern);
			canvas.render(matrices, spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay, fs[0], fs[1], fs[2], 1.0F);
		}
	}
}
