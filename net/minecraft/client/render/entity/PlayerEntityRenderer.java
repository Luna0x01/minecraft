package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3302;
import net.minecraft.class_4272;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity> {
	private float field_20974;

	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, false);
	}

	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
		super(entityRenderDispatcher, new PlayerEntityModel(0.0F, bl), 0.5F);
		this.addFeature(new ArmorRenderer(this));
		this.addFeature(new HeldItemRenderer(this));
		this.addFeature(new StuckArrowsFeatureRenderer(this));
		this.addFeature(new Deadmau5FeatureRenderer(this));
		this.addFeature(new CapeFeatureRenderer(this));
		this.addFeature(new HeadFeatureRenderer(this.getModel().head));
		this.addFeature(new ElytraFeatureRenderer(this));
		this.addFeature(new class_3302(entityRenderDispatcher));
		this.addFeature(new class_4272(this));
	}

	public PlayerEntityModel getModel() {
		return (PlayerEntityModel)super.getModel();
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h) {
		if (!abstractClientPlayerEntity.isMainPlayer() || this.dispatcher.field_11098 == abstractClientPlayerEntity) {
			double i = e;
			if (abstractClientPlayerEntity.isSneaking()) {
				i = e - 0.125;
			}

			this.setModelPose(abstractClientPlayerEntity);
			GlStateManager.method_12286(GlStateManager.class_2869.PLAYER_SKIN);
			super.render(abstractClientPlayerEntity, d, i, f, g, h);
			GlStateManager.method_12299(GlStateManager.class_2869.PLAYER_SKIN);
		}
	}

	private void setModelPose(AbstractClientPlayerEntity player) {
		PlayerEntityModel playerEntityModel = this.getModel();
		if (player.isSpectator()) {
			playerEntityModel.setVisible(false);
			playerEntityModel.head.visible = true;
			playerEntityModel.hat.visible = true;
		} else {
			ItemStack itemStack = player.getMainHandStack();
			ItemStack itemStack2 = player.getOffHandStack();
			playerEntityModel.setVisible(true);
			playerEntityModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
			playerEntityModel.jacket.visible = player.isPartVisible(PlayerModelPart.JACKET);
			playerEntityModel.leftPants.visible = player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
			playerEntityModel.rightPants.visible = player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
			playerEntityModel.leftSleeve.visible = player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
			playerEntityModel.rightSleeve.visible = player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
			playerEntityModel.sneaking = player.isSneaking();
			BiPedModel.class_2850 lv = this.method_19440(player, itemStack);
			BiPedModel.class_2850 lv2 = this.method_19440(player, itemStack2);
			if (player.getDurability() == HandOption.RIGHT) {
				playerEntityModel.field_13385 = lv;
				playerEntityModel.field_13384 = lv2;
			} else {
				playerEntityModel.field_13385 = lv2;
				playerEntityModel.field_13384 = lv;
			}
		}
	}

	public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		return abstractClientPlayerEntity.getCapeId();
	}

	protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
	}

	protected void method_10209(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, double g) {
		if (g < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
			if (scoreboardObjective != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.method_15586(), scoreboardObjective);
				this.renderLabelIfPresent(
					abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.method_4849().asFormattedString(), d, e, f, 64
				);
				e += (double)((float)this.getFontRenderer().fontHeight * 1.15F * 0.025F);
			}
		}

		super.method_10209(abstractClientPlayerEntity, d, e, f, string, g);
	}

	public void renderRightArm(AbstractClientPlayerEntity player) {
		float f = 1.0F;
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel playerEntityModel = this.getModel();
		this.setModelPose(player);
		GlStateManager.enableBlend();
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.sneaking = false;
		playerEntityModel.field_20533 = 0.0F;
		playerEntityModel.setAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		playerEntityModel.rightArm.posX = 0.0F;
		playerEntityModel.rightArm.render(0.0625F);
		playerEntityModel.rightSleeve.posX = 0.0F;
		playerEntityModel.rightSleeve.render(0.0625F);
		GlStateManager.disableBlend();
	}

	public void renderLeftArm(AbstractClientPlayerEntity player) {
		float f = 1.0F;
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel playerEntityModel = this.getModel();
		this.setModelPose(player);
		GlStateManager.enableBlend();
		playerEntityModel.sneaking = false;
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.field_20533 = 0.0F;
		playerEntityModel.setAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		playerEntityModel.leftArm.posX = 0.0F;
		playerEntityModel.leftArm.render(0.0625F);
		playerEntityModel.leftSleeve.posX = 0.0F;
		playerEntityModel.leftSleeve.render(0.0625F);
		GlStateManager.disableBlend();
	}

	protected void method_5772(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f) {
		if (abstractClientPlayerEntity.isAlive() && abstractClientPlayerEntity.isSleeping()) {
			super.method_5772(
				abstractClientPlayerEntity,
				d + (double)abstractClientPlayerEntity.field_3993,
				e + (double)abstractClientPlayerEntity.field_4009,
				f + (double)abstractClientPlayerEntity.field_3994
			);
		} else {
			super.method_5772(abstractClientPlayerEntity, d, e, f);
		}
	}

	protected void method_5777(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h) {
		float i = abstractClientPlayerEntity.method_15642(h);
		if (abstractClientPlayerEntity.isAlive() && abstractClientPlayerEntity.isSleeping()) {
			GlStateManager.rotate(abstractClientPlayerEntity.method_3183(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(this.method_5771(abstractClientPlayerEntity), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
		} else if (abstractClientPlayerEntity.method_13055()) {
			super.method_5777(abstractClientPlayerEntity, f, g, h);
			float j = (float)abstractClientPlayerEntity.method_13056() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.method_15646()) {
				GlStateManager.rotate(k * (-90.0F - abstractClientPlayerEntity.pitch), 1.0F, 0.0F, 0.0F);
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVector(h);
			double d = abstractClientPlayerEntity.velocityX * abstractClientPlayerEntity.velocityX
				+ abstractClientPlayerEntity.velocityZ * abstractClientPlayerEntity.velocityZ;
			double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
			if (d > 0.0 && e > 0.0) {
				double l = (abstractClientPlayerEntity.velocityX * vec3d.x + abstractClientPlayerEntity.velocityZ * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
				double m = abstractClientPlayerEntity.velocityX * vec3d.z - abstractClientPlayerEntity.velocityZ * vec3d.x;
				GlStateManager.rotate((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
			}
		} else if (i > 0.0F) {
			super.method_5777(abstractClientPlayerEntity, f, g, h);
			float n = this.method_19441(abstractClientPlayerEntity.pitch, -90.0F - abstractClientPlayerEntity.pitch, i);
			if (!abstractClientPlayerEntity.method_15584()) {
				n = this.method_5769(this.field_20974, 0.0F, 1.0F - i);
			}

			GlStateManager.rotate(n, 1.0F, 0.0F, 0.0F);
			if (abstractClientPlayerEntity.method_15584()) {
				this.field_20974 = n;
				GlStateManager.translate(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.method_5777(abstractClientPlayerEntity, f, g, h);
		}
	}

	private float method_19441(float f, float g, float h) {
		return f + (g - f) * h;
	}

	private BiPedModel.class_2850 method_19440(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return BiPedModel.class_2850.EMPTY;
		} else {
			if (abstractClientPlayerEntity.method_13065() > 0) {
				UseAction useAction = itemStack.getUseAction();
				if (useAction == UseAction.BLOCK) {
					return BiPedModel.class_2850.BLOCK;
				}

				if (useAction == UseAction.BOW) {
					return BiPedModel.class_2850.BOW_AND_ARROW;
				}

				if (useAction == UseAction.SPEAR) {
					return BiPedModel.class_2850.THROW_SPEAR;
				}
			}

			return BiPedModel.class_2850.ITEM;
		}
	}
}
