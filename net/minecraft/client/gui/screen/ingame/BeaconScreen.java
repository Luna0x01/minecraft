package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
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
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeaconScreen extends HandledScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
	private Inventory beaconInventory;
	private BeaconScreen.DoneButtonWidget doneButton;
	private boolean consumeGem;

	public BeaconScreen(PlayerInventory playerInventory, Inventory inventory) {
		super(new BeaconScreenHandler(playerInventory, inventory));
		this.beaconInventory = inventory;
		this.backgroundWidth = 230;
		this.backgroundHeight = 219;
	}

	@Override
	public void init() {
		super.init();
		this.buttons.add(this.doneButton = new BeaconScreen.DoneButtonWidget(-1, this.x + 164, this.y + 107));
		this.buttons.add(new BeaconScreen.CancelButtonWidget(-2, this.x + 190, this.y + 107));
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
					this.buttons.add(effectButtonWidget);
					if (k >= i) {
						effectButtonWidget.active = false;
					} else if (statusEffect3 == statusEffect) {
						effectButtonWidget.setDisabled(true);
					}
				}
			}

			int o = 3;
			int p = BeaconBlockEntity.EFFECTS[o].length + 1;
			int q = p * 22 + (p - 1) * 2;

			for (int r = 0; r < p - 1; r++) {
				StatusEffect statusEffect4 = BeaconBlockEntity.EFFECTS[o][r];
				BeaconScreen.EffectButtonWidget effectButtonWidget2 = new BeaconScreen.EffectButtonWidget(j++, this.x + 167 + r * 24 - q / 2, this.y + 47, statusEffect4, o);
				this.buttons.add(effectButtonWidget2);
				if (o >= i) {
					effectButtonWidget2.active = false;
				} else if (statusEffect4 == statusEffect2) {
					effectButtonWidget2.setDisabled(true);
				}
			}

			if (statusEffect != null) {
				BeaconScreen.EffectButtonWidget effectButtonWidget3 = new BeaconScreen.EffectButtonWidget(
					j++, this.x + 167 + (p - 1) * 24 - q / 2, this.y + 47, statusEffect, o
				);
				this.buttons.add(effectButtonWidget3);
				if (o >= i) {
					effectButtonWidget3.active = false;
				} else if (statusEffect == statusEffect2) {
					effectButtonWidget3.setDisabled(true);
				}
			}
		}

		this.doneButton.active = this.beaconInventory.getInvStack(0) != null && statusEffect != null;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == -2) {
			this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(this.client.player.openScreenHandler.syncId));
			this.client.setScreen(null);
		} else if (button.id == -1) {
			String string = "MC|Beacon";
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeInt(this.beaconInventory.getProperty(1));
			packetByteBuf.writeInt(this.beaconInventory.getProperty(2));
			this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(string, packetByteBuf));
			this.client.player.networkHandler.sendPacket(new GuiCloseC2SPacket(this.client.player.openScreenHandler.syncId));
			this.client.setScreen(null);
		} else if (button instanceof BeaconScreen.EffectButtonWidget) {
			BeaconScreen.EffectButtonWidget effectButtonWidget = (BeaconScreen.EffectButtonWidget)button;
			if (effectButtonWidget.isDisabled()) {
				return;
			}

			int i = StatusEffect.getIndex(effectButtonWidget.field_13329);
			if (effectButtonWidget.baseLevels < 3) {
				this.beaconInventory.setProperty(1, i);
			} else {
				this.beaconInventory.setProperty(2, i);
			}

			this.buttons.clear();
			this.init();
			this.tick();
		}
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		DiffuseLighting.disable();
		this.drawCenteredString(this.textRenderer, I18n.translate("tile.beacon.primary"), 62, 10, 14737632);
		this.drawCenteredString(this.textRenderer, I18n.translate("tile.beacon.secondary"), 169, 10, 14737632);

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
		this.itemRenderer.zOffset = 100.0F;
		this.itemRenderer.method_12461(new ItemStack(Items.EMERALD), i + 42, j + 109);
		this.itemRenderer.method_12461(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
		this.itemRenderer.method_12461(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
		this.itemRenderer.method_12461(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
		this.itemRenderer.zOffset = 0.0F;
	}

	static class BaseButtonWidget extends ButtonWidget {
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
		public void render(MinecraftClient client, int mouseX, int mouseY) {
			if (this.visible) {
				client.getTextureManager().bindTexture(BeaconScreen.TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = 219;
				int j = 0;
				if (!this.active) {
					j += this.width * 2;
				} else if (this.disabled) {
					j += this.width * 1;
				} else if (this.hovered) {
					j += this.width * 3;
				}

				this.drawTexture(this.x, this.y, j, i, this.width, this.height);
				if (!BeaconScreen.TEXTURE.equals(this.texture)) {
					client.getTextureManager().bindTexture(this.texture);
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
		public void renderToolTip(int mouseX, int mouseY) {
			BeaconScreen.this.renderTooltip(I18n.translate("gui.cancel"), mouseX, mouseY);
		}
	}

	class DoneButtonWidget extends BeaconScreen.BaseButtonWidget {
		public DoneButtonWidget(int i, int j, int k) {
			super(i, j, k, BeaconScreen.TEXTURE, 90, 220);
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
			super(i, j, k, HandledScreen.INVENTORY_TEXTURE, statusEffect.getIconLevel() % 8 * 18, 198 + statusEffect.getIconLevel() / 8 * 18);
			this.field_13329 = statusEffect;
			this.baseLevels = l;
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
