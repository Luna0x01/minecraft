package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.screen.ScreenHandler;

public abstract class InventoryScreen extends HandledScreen {
	private boolean offsetGuiForEffects;

	public InventoryScreen(ScreenHandler screenHandler) {
		super(screenHandler);
	}

	@Override
	public void init() {
		super.init();
		this.applyStatusEffectOffset();
	}

	protected void applyStatusEffectOffset() {
		if (!this.client.player.getStatusEffectInstances().isEmpty()) {
			this.x = 160 + (this.width - this.backgroundWidth - 200) / 2;
			this.offsetGuiForEffects = true;
		} else {
			this.x = (this.width - this.backgroundWidth) / 2;
			this.offsetGuiForEffects = false;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
		if (this.offsetGuiForEffects) {
			this.drawStatusEffects();
		}
	}

	private void drawStatusEffects() {
		int i = this.x - 124;
		int j = this.y;
		int k = 166;
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffectInstances();
		if (!collection.isEmpty()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			int l = 33;
			if (collection.size() > 5) {
				l = 132 / (collection.size() - 1);
			}

			for (StatusEffectInstance statusEffectInstance : Ordering.natural().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getStatusEffect();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.client.getTextureManager().bindTexture(INVENTORY_TEXTURE);
				this.drawTexture(i, j, 0, 166, 140, 32);
				if (statusEffect.hasIcon()) {
					int m = statusEffect.getIconLevel();
					this.drawTexture(i + 6, j + 7, 0 + m % 8 * 18, 198 + m / 8 * 18, 18, 18);
				}

				String string = I18n.translate(statusEffect.getTranslationKey());
				if (statusEffectInstance.getAmplifier() == 1) {
					string = string + " " + I18n.translate("enchantment.level.2");
				} else if (statusEffectInstance.getAmplifier() == 2) {
					string = string + " " + I18n.translate("enchantment.level.3");
				} else if (statusEffectInstance.getAmplifier() == 3) {
					string = string + " " + I18n.translate("enchantment.level.4");
				}

				this.textRenderer.drawWithShadow(string, (float)(i + 10 + 18), (float)(j + 6), 16777215);
				String string2 = StatusEffect.method_2436(statusEffectInstance, 1.0F);
				this.textRenderer.drawWithShadow(string2, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
				j += l;
			}
		}
	}
}
