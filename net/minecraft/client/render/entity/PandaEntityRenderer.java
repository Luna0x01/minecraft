package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class PandaEntityRenderer extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>> {
	private static final Map<PandaEntity.Gene, Identifier> TEXTURES = Util.make(Maps.newEnumMap(PandaEntity.Gene.class), enumMap -> {
		enumMap.put(PandaEntity.Gene.NORMAL, new Identifier("textures/entity/panda/panda.png"));
		enumMap.put(PandaEntity.Gene.LAZY, new Identifier("textures/entity/panda/lazy_panda.png"));
		enumMap.put(PandaEntity.Gene.WORRIED, new Identifier("textures/entity/panda/worried_panda.png"));
		enumMap.put(PandaEntity.Gene.PLAYFUL, new Identifier("textures/entity/panda/playful_panda.png"));
		enumMap.put(PandaEntity.Gene.BROWN, new Identifier("textures/entity/panda/brown_panda.png"));
		enumMap.put(PandaEntity.Gene.WEAK, new Identifier("textures/entity/panda/weak_panda.png"));
		enumMap.put(PandaEntity.Gene.AGGRESSIVE, new Identifier("textures/entity/panda/aggressive_panda.png"));
	});

	public PandaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PandaEntityModel<>(9, 0.0F), 0.9F);
		this.addFeature(new PandaHeldItemFeatureRenderer(this));
	}

	public Identifier getTexture(PandaEntity pandaEntity) {
		return (Identifier)TEXTURES.getOrDefault(pandaEntity.getProductGene(), TEXTURES.get(PandaEntity.Gene.NORMAL));
	}

	protected void setupTransforms(PandaEntity pandaEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(pandaEntity, matrixStack, f, g, h);
		if (pandaEntity.playingTicks > 0) {
			int i = pandaEntity.playingTicks;
			int j = i + 1;
			float k = 7.0F;
			float l = pandaEntity.isBaby() ? 0.3F : 0.8F;
			if (i < 8) {
				float m = (float)(90 * i) / 7.0F;
				float n = (float)(90 * j) / 7.0F;
				float o = this.method_4086(m, n, j, h, 8.0F);
				matrixStack.translate(0.0, (double)((l + 0.2F) * (o / 90.0F)), 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-o));
			} else if (i < 16) {
				float p = ((float)i - 8.0F) / 7.0F;
				float q = 90.0F + 90.0F * p;
				float r = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
				float s = this.method_4086(q, r, j, h, 16.0F);
				matrixStack.translate(0.0, (double)(l + 0.2F + (l - 0.2F) * (s - 90.0F) / 90.0F), 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-s));
			} else if ((float)i < 24.0F) {
				float t = ((float)i - 16.0F) / 7.0F;
				float u = 180.0F + 90.0F * t;
				float v = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
				float w = this.method_4086(u, v, j, h, 24.0F);
				matrixStack.translate(0.0, (double)(l + l * (270.0F - w) / 90.0F), 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-w));
			} else if (i < 32) {
				float x = ((float)i - 24.0F) / 7.0F;
				float y = 270.0F + 90.0F * x;
				float z = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
				float aa = this.method_4086(y, z, j, h, 32.0F);
				matrixStack.translate(0.0, (double)(l * ((360.0F - aa) / 90.0F)), 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-aa));
			}
		}

		float ab = pandaEntity.getScaredAnimationProgress(h);
		if (ab > 0.0F) {
			matrixStack.translate(0.0, (double)(0.8F * ab), 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(ab, pandaEntity.pitch, pandaEntity.pitch + 90.0F)));
			matrixStack.translate(0.0, (double)(-1.0F * ab), 0.0);
			if (pandaEntity.isScaredByThunderstorm()) {
				float ac = (float)(Math.cos((double)pandaEntity.age * 1.25) * Math.PI * 0.05F);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(ac));
				if (pandaEntity.isBaby()) {
					matrixStack.translate(0.0, 0.8F, 0.55F);
				}
			}
		}

		float ad = pandaEntity.getLieOnBackAnimationProgress(h);
		if (ad > 0.0F) {
			float ae = pandaEntity.isBaby() ? 0.5F : 1.3F;
			matrixStack.translate(0.0, (double)(ae * ad), 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(ad, pandaEntity.pitch, pandaEntity.pitch + 180.0F)));
		}
	}

	private float method_4086(float f, float g, int i, float h, float j) {
		return (float)i < j ? MathHelper.lerp(h, f, g) : f;
	}
}
