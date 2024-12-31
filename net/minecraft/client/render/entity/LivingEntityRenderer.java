package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	private static final Logger LOGGER = LogManager.getLogger();
	protected M model;
	protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

	public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
		super(entityRenderDispatcher);
		this.model = entityModel;
		this.shadowSize = f;
	}

	protected final boolean addFeature(FeatureRenderer<T, M> featureRenderer) {
		return this.features.add(featureRenderer);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	public void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
		this.model.riding = livingEntity.hasVehicle();
		this.model.child = livingEntity.isBaby();
		float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
		float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
		float k = j - h;
		if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
			h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
			k = j - h;
			float l = MathHelper.wrapDegrees(k);
			if (l < -85.0F) {
				l = -85.0F;
			}

			if (l >= 85.0F) {
				l = 85.0F;
			}

			h = j - l;
			if (l * l > 2500.0F) {
				h += l * 0.2F;
			}

			k = j - h;
		}

		float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.pitch);
		if (livingEntity.getPose() == EntityPose.field_18078) {
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null) {
				float n = livingEntity.getEyeHeight(EntityPose.field_18076) - 0.1F;
				matrixStack.translate((double)((float)(-direction.getOffsetX()) * n), 0.0, (double)((float)(-direction.getOffsetZ()) * n));
			}
		}

		float o = this.getCustomAngle(livingEntity, g);
		this.setupTransforms(livingEntity, matrixStack, o, h, g);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntity, matrixStack, g);
		matrixStack.translate(0.0, -1.501F, 0.0);
		float p = 0.0F;
		float q = 0.0F;
		if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
			p = MathHelper.lerp(g, livingEntity.lastLimbDistance, livingEntity.limbDistance);
			q = livingEntity.limbAngle - livingEntity.limbDistance * (1.0F - g);
			if (livingEntity.isBaby()) {
				q *= 3.0F;
			}

			if (p > 1.0F) {
				p = 1.0F;
			}
		}

		this.model.animateModel(livingEntity, q, p, g);
		this.model.setAngles(livingEntity, q, p, o, k, m);
		boolean bl = this.isFullyVisible(livingEntity);
		boolean bl2 = !bl && !livingEntity.isInvisibleTo(MinecraftClient.getInstance().player);
		RenderLayer renderLayer = this.method_24302(livingEntity, bl, bl2);
		if (renderLayer != null) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
			int r = getOverlay(livingEntity, this.getWhiteOverlayProgress(livingEntity, g));
			this.model.render(matrixStack, vertexConsumer, i, r, 1.0F, 1.0F, 1.0F, bl2 ? 0.15F : 1.0F);
		}

		if (!livingEntity.isSpectator()) {
			for (FeatureRenderer<T, M> featureRenderer : this.features) {
				featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, q, p, g, o, k, m);
			}
		}

		matrixStack.pop();
		super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Nullable
	protected RenderLayer method_24302(T livingEntity, boolean bl, boolean bl2) {
		Identifier identifier = this.getTexture(livingEntity);
		if (bl2) {
			return RenderLayer.getEntityTranslucent(identifier);
		} else if (bl) {
			return this.model.getLayer(identifier);
		} else {
			return livingEntity.isGlowing() ? RenderLayer.getOutline(identifier) : null;
		}
	}

	public static int getOverlay(LivingEntity livingEntity, float f) {
		return OverlayTexture.packUv(OverlayTexture.getU(f), OverlayTexture.getV(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0));
	}

	protected boolean isFullyVisible(T livingEntity) {
		return !livingEntity.isInvisible();
	}

	private static float getYaw(Direction direction) {
		switch (direction) {
			case field_11035:
				return 90.0F;
			case field_11039:
				return 0.0F;
			case field_11043:
				return 270.0F;
			case field_11034:
				return 180.0F;
			default:
				return 0.0F;
		}
	}

	protected void setupTransforms(T livingEntity, MatrixStack matrixStack, float f, float g, float h) {
		EntityPose entityPose = livingEntity.getPose();
		if (entityPose != EntityPose.field_18078) {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - g));
		}

		if (livingEntity.deathTime > 0) {
			float i = ((float)livingEntity.deathTime + h - 1.0F) / 20.0F * 1.6F;
			i = MathHelper.sqrt(i);
			if (i > 1.0F) {
				i = 1.0F;
			}

			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(i * this.getLyingAngle(livingEntity)));
		} else if (livingEntity.isUsingRiptide()) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F - livingEntity.pitch));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(((float)livingEntity.age + h) * -75.0F));
		} else if (entityPose == EntityPose.field_18078) {
			Direction direction = livingEntity.getSleepingDirection();
			float j = direction != null ? getYaw(direction) : g;
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(livingEntity)));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
		} else if (livingEntity.hasCustomName() || livingEntity instanceof PlayerEntity) {
			String string = Formatting.strip(livingEntity.getName().getString());
			if (("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isPartVisible(PlayerModelPart.field_7559))) {
				matrixStack.translate(0.0, (double)(livingEntity.getHeight() + 0.1F), 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			}
		}
	}

	protected float getHandSwingProgress(T livingEntity, float f) {
		return livingEntity.getHandSwingProgress(f);
	}

	protected float getCustomAngle(T livingEntity, float f) {
		return (float)livingEntity.age + f;
	}

	protected float getLyingAngle(T livingEntity) {
		return 90.0F;
	}

	protected float getWhiteOverlayProgress(T livingEntity, float f) {
		return 0.0F;
	}

	protected void scale(T livingEntity, MatrixStack matrixStack, float f) {
	}

	protected boolean hasLabel(T livingEntity) {
		double d = this.renderManager.getSquaredDistanceToCamera(livingEntity);
		float f = livingEntity.isSneaky() ? 32.0F : 64.0F;
		if (d >= (double)(f * f)) {
			return false;
		} else {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
			boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
			if (livingEntity != clientPlayerEntity) {
				AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
				AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
				if (abstractTeam != null) {
					AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
					switch (visibilityRule) {
						case field_1442:
							return bl;
						case field_1443:
							return false;
						case field_1444:
							return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
						case field_1446:
							return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
						default:
							return true;
					}
				}
			}

			return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
		}
	}
}
