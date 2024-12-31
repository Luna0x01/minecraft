package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity> {
	private boolean slim;

	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, false);
	}

	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
		super(entityRenderDispatcher, new PlayerEntityModel(0.0F, bl), 0.5F);
		this.slim = bl;
		this.addFeature(new ArmorRenderer(this));
		this.addFeature(new HeldItemRenderer(this));
		this.addFeature(new StuckArrowsFeatureRenderer(this));
		this.addFeature(new Deadmau5FeatureRenderer(this));
		this.addFeature(new CapeFeatureRenderer(this));
		this.addFeature(new HeadFeatureRenderer(this.getModel().head));
	}

	public PlayerEntityModel getModel() {
		return (PlayerEntityModel)super.getModel();
	}

	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h) {
		if (!abstractClientPlayerEntity.isMainPlayer() || this.dispatcher.field_11098 == abstractClientPlayerEntity) {
			double i = e;
			if (abstractClientPlayerEntity.isSneaking() && !(abstractClientPlayerEntity instanceof ClientPlayerEntity)) {
				i = e - 0.125;
			}

			this.setModelPose(abstractClientPlayerEntity);
			super.render(abstractClientPlayerEntity, d, i, f, g, h);
		}
	}

	private void setModelPose(AbstractClientPlayerEntity player) {
		PlayerEntityModel playerEntityModel = this.getModel();
		if (player.isSpectator()) {
			playerEntityModel.setVisible(false);
			playerEntityModel.head.visible = true;
			playerEntityModel.hat.visible = true;
		} else {
			ItemStack itemStack = player.inventory.getMainHandStack();
			playerEntityModel.setVisible(true);
			playerEntityModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
			playerEntityModel.jacket.visible = player.isPartVisible(PlayerModelPart.JACKET);
			playerEntityModel.leftPants.visible = player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
			playerEntityModel.rightPants.visible = player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
			playerEntityModel.leftSleeve.visible = player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
			playerEntityModel.rightSleeve.visible = player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
			playerEntityModel.leftArmPose = 0;
			playerEntityModel.aiming = false;
			playerEntityModel.sneaking = player.isSneaking();
			if (itemStack == null) {
				playerEntityModel.rightArmPose = 0;
			} else {
				playerEntityModel.rightArmPose = 1;
				if (player.getItemUseTicks() > 0) {
					UseAction useAction = itemStack.getUseAction();
					if (useAction == UseAction.BLOCK) {
						playerEntityModel.rightArmPose = 3;
					} else if (useAction == UseAction.BOW) {
						playerEntityModel.aiming = true;
					}
				}
			}
		}
	}

	protected Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		return abstractClientPlayerEntity.getCapeId();
	}

	@Override
	public void translate() {
		GlStateManager.translate(0.0F, 0.1875F, 0.0F);
	}

	protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scale(g, g, g);
	}

	protected void method_10209(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, float g, double h) {
		if (h < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
			if (scoreboardObjective != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getTranslationKey(), scoreboardObjective);
				this.renderLabelIfPresent(abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName(), d, e, f, 64);
				e += (double)((float)this.getFontRenderer().fontHeight * 1.15F * g);
			}
		}

		super.method_10209(abstractClientPlayerEntity, d, e, f, string, g, h);
	}

	public void renderRightArm(AbstractClientPlayerEntity player) {
		float f = 1.0F;
		GlStateManager.color(f, f, f);
		PlayerEntityModel playerEntityModel = this.getModel();
		this.setModelPose(player);
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.sneaking = false;
		playerEntityModel.setAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		playerEntityModel.renderRightArm();
	}

	public void renderLeftArm(AbstractClientPlayerEntity player) {
		float f = 1.0F;
		GlStateManager.color(f, f, f);
		PlayerEntityModel playerEntityModel = this.getModel();
		this.setModelPose(player);
		playerEntityModel.sneaking = false;
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.setAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		playerEntityModel.renderLeftArm();
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
		if (abstractClientPlayerEntity.isAlive() && abstractClientPlayerEntity.isSleeping()) {
			GlStateManager.rotate(abstractClientPlayerEntity.method_3183(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(this.method_5771(abstractClientPlayerEntity), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			super.method_5777(abstractClientPlayerEntity, f, g, h);
		}
	}
}
