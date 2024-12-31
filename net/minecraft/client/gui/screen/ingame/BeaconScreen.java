package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
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
		int j = this.beaconInventory.getProperty(1);
		int k = this.beaconInventory.getProperty(2);
		if (this.consumeGem && i >= 0) {
			this.consumeGem = false;

			for (int l = 0; l <= 2; l++) {
				int m = BeaconBlockEntity.EFFECTS[l].length;
				int n = m * 22 + (m - 1) * 2;

				for (int o = 0; o < m; o++) {
					int p = BeaconBlockEntity.EFFECTS[l][o].id;
					BeaconScreen.EffectButtonWidget effectButtonWidget = new BeaconScreen.EffectButtonWidget(
						l << 8 | p, this.x + 76 + o * 24 - n / 2, this.y + 22 + l * 25, p, l
					);
					this.buttons.add(effectButtonWidget);
					if (l >= i) {
						effectButtonWidget.active = false;
					} else if (p == j) {
						effectButtonWidget.setDisabled(true);
					}
				}
			}

			int q = 3;
			int r = BeaconBlockEntity.EFFECTS[q].length + 1;
			int s = r * 22 + (r - 1) * 2;

			for (int t = 0; t < r - 1; t++) {
				int u = BeaconBlockEntity.EFFECTS[q][t].id;
				BeaconScreen.EffectButtonWidget effectButtonWidget2 = new BeaconScreen.EffectButtonWidget(q << 8 | u, this.x + 167 + t * 24 - s / 2, this.y + 47, u, q);
				this.buttons.add(effectButtonWidget2);
				if (q >= i) {
					effectButtonWidget2.active = false;
				} else if (u == k) {
					effectButtonWidget2.setDisabled(true);
				}
			}

			if (j > 0) {
				BeaconScreen.EffectButtonWidget effectButtonWidget3 = new BeaconScreen.EffectButtonWidget(
					q << 8 | j, this.x + 167 + (r - 1) * 24 - s / 2, this.y + 47, j, q
				);
				this.buttons.add(effectButtonWidget3);
				if (q >= i) {
					effectButtonWidget3.active = false;
				} else if (j == k) {
					effectButtonWidget3.setDisabled(true);
				}
			}
		}

		this.doneButton.active = this.beaconInventory.getInvStack(0) != null && j > 0;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == -2) {
			this.client.setScreen(null);
		} else if (button.id == -1) {
			String string = "MC|Beacon";
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeInt(this.beaconInventory.getProperty(1));
			packetByteBuf.writeInt(this.beaconInventory.getProperty(2));
			this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(string, packetByteBuf));
			this.client.setScreen(null);
		} else if (button instanceof BeaconScreen.EffectButtonWidget) {
			if (((BeaconScreen.EffectButtonWidget)button).isDisabled()) {
				return;
			}

			int i = button.id;
			int j = i & 0xFF;
			int k = i >> 8;
			if (k < 3) {
				this.beaconInventory.setProperty(1, j);
			} else {
				this.beaconInventory.setProperty(2, j);
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
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.EMERALD), i + 42, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
		this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
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
		private final int secondaryEffectId;
		private final int baseLevels;

		public EffectButtonWidget(int i, int j, int k, int l, int m) {
			super(
				i,
				j,
				k,
				HandledScreen.INVENTORY_TEXTURE,
				0 + StatusEffect.STATUS_EFFECTS[l].getIconLevel() % 8 * 18,
				198 + StatusEffect.STATUS_EFFECTS[l].getIconLevel() / 8 * 18
			);
			this.secondaryEffectId = l;
			this.baseLevels = m;
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			String string = I18n.translate(StatusEffect.STATUS_EFFECTS[this.secondaryEffectId].getTranslationKey());
			if (this.baseLevels >= 3 && this.secondaryEffectId != StatusEffect.REGENERATION.id) {
				string = string + " II";
			}

			BeaconScreen.this.renderTooltip(string, mouseX, mouseY);
		}
	}
}
