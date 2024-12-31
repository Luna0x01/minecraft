package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4390;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeaconScreen extends HandledScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
	private final Inventory beaconInventory;
	private BeaconScreen.DoneButtonWidget doneButton;
	private boolean consumeGem;

	public BeaconScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new BeaconScreenHandler(playerInventory, inventory));
		this.beaconInventory = inventory;
		this.backgroundWidth = 230;
		this.backgroundHeight = 219;
	}

	@Override
	protected void init() {
		super.init();
		this.doneButton = new BeaconScreen.DoneButtonWidget(-1, this.x + 164, this.y + 107);
		this.addButton(this.doneButton);
		this.addButton(new BeaconScreen.CancelButtonWidget(-2, this.x + 190, this.y + 107));
		this.consumeGem = true;
		this.doneButton.active = false;
	}

	@Override
	public void tick() {
		super.tick();
		int i = this.beaconInventory.getProperty(0);
		StatusEffect statusEffect = StatusEffect.byIndex(this.beaconInventory.getProperty(1));
		StatusEffect statusEffect2 = StatusEffect.byIndex(this.beaconInventory.getProperty(2));
		if (this.consumeGem && i >= 0) {
			this.consumeGem = false;
			int j = 100;

			for (int k = 0; k <= 2; k++) {
				int l = BeaconBlockEntity.EFFECTS[k].length;
				int m = l * 22 + (l - 1) * 2;

				for (int n = 0; n < l; n++) {
					StatusEffect statusEffect3 = BeaconBlockEntity.EFFECTS[k][n];
					BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(
						j++, this.x + 76 + n * 24 - m / 2, this.y + 22 + k * 25, statusEffect3, k
					);
					this.addButton(effectButtonWidget);
					if (k >= i) {
						effectButtonWidget.active = false;
					} else if (statusEffect3 == statusEffect) {
						effectButtonWidget.setDisabled(true);
					}
				}
			}

			int o = 3;
			int p = BeaconBlockEntity.EFFECTS[3].length + 1;
			int q = p * 22 + (p - 1) * 2;

			for (int r = 0; r < p - 1; r++) {
				StatusEffect statusEffect4 = BeaconBlockEntity.EFFECTS[3][r];
				BeaconScreen.EffectButtonWidget effectButtonWidget2 = new BeaconScreen.EffectButtonWidget(j++, this.x + 167 + r * 24 - q / 2, this.y + 47, statusEffect4, 3);
				this.addButton(effectButtonWidget2);
				if (3 >= i) {
					effectButtonWidget2.active = false;
				} else if (statusEffect4 == statusEffect2) {
					effectButtonWidget2.setDisabled(true);
				}
			}

			if (statusEffect != null) {
				BeaconScreen.EffectButtonWidget effectButtonWidget3 = new BeaconScreen.EffectButtonWidget(
					j++, this.x + 167 + (p - 1) * 24 - q / 2, this.y + 47, statusEffect, 3
				);
				this.addButton(effectButtonWidget3);
				if (3 >= i) {
					effectButtonWidget3.active = false;
				} else if (statusEffect == statusEffect2) {
					effectButtonWidget3.setDisabled(true);
				}
			}
		}

		this.doneButton.active = !this.beaconInventory.getInvStack(0).isEmpty() && statusEffect != null;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		DiffuseLighting.disable();
		this.drawCenteredString(this.textRenderer, I18n.translate("block.minecraft.beacon.primary"), 62, 10, 14737632);
		this.drawCenteredString(this.textRenderer, I18n.translate("block.minecraft.beacon.secondary"), 169, 10, 14737632);

		for (ButtonWidget buttonWidget : this.buttons) {
			if (buttonWidget.isHovered()) {
				buttonWidget.renderToolTip(mouseX - this.x, mouseY - this.y);
				break;
			}
		}

		DiffuseLighting.enable();
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.field_20308.field_20932 = 100.0F;
		this.field_20308.method_19397(new ItemStack(Items.EMERALD), i + 42, j + 109);
		this.field_20308.method_19397(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
		this.field_20308.method_19397(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
		this.field_20308.method_19397(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
		this.field_20308.field_20932 = 0.0F;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
	}

	abstract static class BaseButtonWidget extends ButtonWidget {
		private final Identifier texture;
		private final int u;
		private final int v;
		private boolean disabled;

		protected BaseButtonWidget(int i, int j, int k, Identifier identifier, int l, int m) {
			super(i, j, k, 22, 22, "");
			this.texture = identifier;
			this.u = l;
			this.v = m;
		}

		@Override
		public void method_891(int i, int j, float f) {
			if (this.visible) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconScreen.TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				int k = 219;
				int l = 0;
				if (!this.active) {
					l += this.width * 2;
				} else if (this.disabled) {
					l += this.width * 1;
				} else if (this.hovered) {
					l += this.width * 3;
				}

				this.drawTexture(this.x, this.y, l, 219, this.width, this.height);
				if (!BeaconScreen.TEXTURE.equals(this.texture)) {
					MinecraftClient.getInstance().getTextureManager().bindTexture(this.texture);
				}

				this.drawTexture(this.x + 2, this.y + 2, this.u, this.v, 18, 18);
			}
		}

		public boolean isDisabled() {
			return this.disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
	}

	class CancelButtonWidget extends BeaconScreen.BaseButtonWidget {
		public CancelButtonWidget(int i, int j, int k) {
			super(i, j, k, BeaconScreen.TEXTURE, 112, 220);
		}

		@Override
		public void method_18374(double d, double e) {
			BeaconScreen.this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.client.player.openScreenHandler.syncId));
			BeaconScreen.this.client.setScreen(null);
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(I18n.translate("gui.cancel"), mouseX, mouseY);
		}
	}

	class DoneButtonWidget extends BeaconScreen.BaseButtonWidget {
		public DoneButtonWidget(int i, int j, int k) {
			super(i, j, k, BeaconScreen.TEXTURE, 90, 220);
		}

		@Override
		public void method_18374(double d, double e) {
			BeaconScreen.this.client
				.getNetworkHandler()
				.sendPacket(new class_4390(BeaconScreen.this.beaconInventory.getProperty(1), BeaconScreen.this.beaconInventory.getProperty(2)));
			BeaconScreen.this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(BeaconScreen.this.client.player.openScreenHandler.syncId));
			BeaconScreen.this.client.setScreen(null);
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(I18n.translate("gui.done"), mouseX, mouseY);
		}
	}

	class EffectButtonWidget extends BeaconScreen.BaseButtonWidget {
		private final StatusEffect field_13329;
		private final int baseLevels;

		public EffectButtonWidget(int i, int j, int k, StatusEffect statusEffect, int l) {
			super(i, j, k, HandledScreen.INVENTORY_TEXTURE, statusEffect.getIconLevel() % 12 * 18, 198 + statusEffect.getIconLevel() / 12 * 18);
			this.field_13329 = statusEffect;
			this.baseLevels = l;
		}

		@Override
		public void method_18374(double d, double e) {
			if (!this.isDisabled()) {
				int i = StatusEffect.getIndex(this.field_13329);
				if (this.baseLevels < 3) {
					BeaconScreen.this.beaconInventory.setProperty(1, i);
				} else {
					BeaconScreen.this.beaconInventory.setProperty(2, i);
				}

				BeaconScreen.this.buttons.clear();
				BeaconScreen.this.field_20307.clear();
				BeaconScreen.this.init();
				BeaconScreen.this.tick();
			}
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			String string = I18n.translate(this.field_13329.getTranslationKey());
			if (this.baseLevels >= 3 && this.field_13329 != StatusEffects.REGENERATION) {
				string = string + " II";
			}

			BeaconScreen.this.renderTooltip(string, mouseX, mouseY);
		}
	}
}
