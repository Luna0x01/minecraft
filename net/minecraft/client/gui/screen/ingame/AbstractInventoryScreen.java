package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public abstract class AbstractInventoryScreen<T extends ScreenHandler> extends HandledScreen<T> {
	protected boolean drawStatusEffects;

	public AbstractInventoryScreen(T screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Override
	protected void init() {
		super.init();
		this.applyStatusEffectOffset();
	}

	protected void applyStatusEffectOffset() {
		if (this.client.player.getStatusEffects().isEmpty()) {
			this.x = (this.width - this.backgroundWidth) / 2;
			this.drawStatusEffects = false;
		} else {
			this.x = 160 + (this.width - this.backgroundWidth - 200) / 2;
			this.drawStatusEffects = true;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		if (this.drawStatusEffects) {
			this.drawStatusEffects(matrices);
		}
	}

	private void drawStatusEffects(MatrixStack matrices) {
		int i = this.x - 124;
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty()) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int j = 33;
			if (collection.size() > 5) {
				j = 132 / (collection.size() - 1);
			}

			Iterable<StatusEffectInstance> iterable = Ordering.natural().sortedCopy(collection);
			this.drawStatusEffectBackgrounds(matrices, i, j, iterable);
			this.drawStatusEffectSprites(matrices, i, j, iterable);
			this.drawStatusEffectDescriptions(matrices, i, j, iterable);
		}
	}

	private void drawStatusEffectBackgrounds(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects) {
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(matrices, x, i, 0, 166, 140, 32);
			i += height;
		}
	}

	private void drawStatusEffectSprites(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects) {
		StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
			RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
			drawSprite(matrices, x + 6, i + 7, this.getZOffset(), 18, 18, sprite);
			i += height;
		}
	}

	private void drawStatusEffectDescriptions(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> statusEffects) {
		int i = this.y;

		for (StatusEffectInstance statusEffectInstance : statusEffects) {
			String string = I18n.translate(statusEffectInstance.getEffectType().getTranslationKey());
			if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
				string = string + " " + I18n.translate("enchantment.level." + (statusEffectInstance.getAmplifier() + 1));
			}

			this.textRenderer.drawWithShadow(matrices, string, (float)(x + 10 + 18), (float)(i + 6), 16777215);
			String string2 = StatusEffectUtil.durationToString(statusEffectInstance, 1.0F);
			this.textRenderer.drawWithShadow(matrices, string2, (float)(x + 10 + 18), (float)(i + 6 + 10), 8355711);
			i += height;
		}
	}
}
