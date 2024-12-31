package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerTradingScreen extends HandledScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager.png");
	private Trader trader;
	private VillagerTradingScreen.PaginationButton nextButton;
	private VillagerTradingScreen.PaginationButton previousButton;
	private int page;
	private Text title;

	public VillagerTradingScreen(PlayerInventory playerInventory, Trader trader, World world) {
		super(new VillagerScreenHandler(playerInventory, trader, world));
		this.trader = trader;
		this.title = trader.getName();
	}

	@Override
	public void init() {
		super.init();
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.buttons.add(this.nextButton = new VillagerTradingScreen.PaginationButton(1, i + 120 + 27, j + 24 - 1, true));
		this.buttons.add(this.previousButton = new VillagerTradingScreen.PaginationButton(2, i + 36 - 19, j + 24 - 1, false));
		this.nextButton.active = false;
		this.previousButton.active = false;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.title.asUnformattedString();
		this.textRenderer.draw(string, this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2, 6, 4210752);
		this.textRenderer.draw(I18n.translate("container.inventory"), 8, this.backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		TraderOfferList traderOfferList = this.trader.getOffers(this.client.player);
		if (traderOfferList != null) {
			this.nextButton.active = this.page < traderOfferList.size() - 1;
			this.previousButton.active = this.page > 0;
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		boolean bl = false;
		if (button == this.nextButton) {
			this.page++;
			TraderOfferList traderOfferList = this.trader.getOffers(this.client.player);
			if (traderOfferList != null && this.page >= traderOfferList.size()) {
				this.page = traderOfferList.size() - 1;
			}

			bl = true;
		} else if (button == this.previousButton) {
			this.page--;
			if (this.page < 0) {
				this.page = 0;
			}

			bl = true;
		}

		if (bl) {
			((VillagerScreenHandler)this.screenHandler).setRecipeIndex(this.page);
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeInt(this.page);
			this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|TrSel", packetByteBuf));
		}
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		TraderOfferList traderOfferList = this.trader.getOffers(this.client.player);
		if (traderOfferList != null && !traderOfferList.isEmpty()) {
			int k = this.page;
			if (k < 0 || k >= traderOfferList.size()) {
				return;
			}

			TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(k);
			if (tradeOffer.isDisabled()) {
				this.client.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				this.drawTexture(this.x + 83, this.y + 21, 212, 0, 28, 21);
				this.drawTexture(this.x + 83, this.y + 51, 212, 0, 28, 21);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
		TraderOfferList traderOfferList = this.trader.getOffers(this.client.player);
		if (traderOfferList != null && !traderOfferList.isEmpty()) {
			int i = (this.width - this.backgroundWidth) / 2;
			int j = (this.height - this.backgroundHeight) / 2;
			int k = this.page;
			TradeOffer tradeOffer = (TradeOffer)traderOfferList.get(k);
			ItemStack itemStack = tradeOffer.getFirstStack();
			ItemStack itemStack2 = tradeOffer.getSecondStack();
			ItemStack itemStack3 = tradeOffer.getResult();
			GlStateManager.pushMatrix();
			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.itemRenderer.zOffset = 100.0F;
			this.itemRenderer.renderInGuiWithOverrides(itemStack, i + 36, j + 24);
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, i + 36, j + 24);
			if (itemStack2 != null) {
				this.itemRenderer.renderInGuiWithOverrides(itemStack2, i + 62, j + 24);
				this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack2, i + 62, j + 24);
			}

			this.itemRenderer.renderInGuiWithOverrides(itemStack3, i + 120, j + 24);
			this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack3, i + 120, j + 24);
			this.itemRenderer.zOffset = 0.0F;
			GlStateManager.disableLighting();
			if (this.isPointWithinBounds(36, 24, 16, 16, mouseX, mouseY) && itemStack != null) {
				this.renderTooltip(itemStack, mouseX, mouseY);
			} else if (itemStack2 != null && this.isPointWithinBounds(62, 24, 16, 16, mouseX, mouseY) && itemStack2 != null) {
				this.renderTooltip(itemStack2, mouseX, mouseY);
			} else if (itemStack3 != null && this.isPointWithinBounds(120, 24, 16, 16, mouseX, mouseY) && itemStack3 != null) {
				this.renderTooltip(itemStack3, mouseX, mouseY);
			} else if (tradeOffer.isDisabled() && (this.isPointWithinBounds(83, 21, 28, 21, mouseX, mouseY) || this.isPointWithinBounds(83, 51, 28, 21, mouseX, mouseY))
				)
			 {
				this.renderTooltip(I18n.translate("merchant.deprecated"), mouseX, mouseY);
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			DiffuseLighting.enableNormally();
		}
	}

	public Trader getTrader() {
		return this.trader;
	}

	static class PaginationButton extends ButtonWidget {
		private final boolean isRight;

		public PaginationButton(int i, int j, int k, boolean bl) {
			super(i, j, k, 12, 19, "");
			this.isRight = bl;
		}

		@Override
		public void render(MinecraftClient client, int mouseX, int mouseY) {
			if (this.visible) {
				client.getTextureManager().bindTexture(VillagerTradingScreen.TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				boolean bl = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				int i = 0;
				int j = 176;
				if (!this.active) {
					j += this.width * 2;
				} else if (bl) {
					j += this.width;
				}

				if (!this.isRight) {
					i += this.height;
				}

				this.drawTexture(this.x, this.y, j, i, this.width, this.height);
			}
		}
	}
}
