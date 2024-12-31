package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4389;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerTradingScreen extends HandledScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager.png");
	private final Trader trader;
	private VillagerTradingScreen.PaginationButton nextButton;
	private VillagerTradingScreen.PaginationButton previousButton;
	private int page;
	private final Text title;
	private final PlayerInventory field_20410;

	public VillagerTradingScreen(PlayerInventory playerInventory, Trader trader, World world) {
		super(new VillagerScreenHandler(playerInventory, trader, world));
		this.trader = trader;
		this.title = trader.getName();
		this.field_20410 = playerInventory;
	}

	private void method_18745() {
		((VillagerScreenHandler)this.screenHandler).setRecipeIndex(this.page);
		this.client.getNetworkHandler().sendPacket(new class_4389(this.page));
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.nextButton = this.addButton(new VillagerTradingScreen.PaginationButton(1, i + 120 + 27, j + 24 - 1, true) {
			@Override
			public void method_18374(double d, double e) {
				VillagerTradingScreen.this.page++;
				TraderOfferList traderOfferList = VillagerTradingScreen.this.trader.getOffers(VillagerTradingScreen.this.client.player);
				if (traderOfferList != null && VillagerTradingScreen.this.page >= traderOfferList.size()) {
					VillagerTradingScreen.this.page = traderOfferList.size() - 1;
				}

				VillagerTradingScreen.this.method_18745();
			}
		});
		this.previousButton = this.addButton(new VillagerTradingScreen.PaginationButton(2, i + 36 - 19, j + 24 - 1, false) {
			@Override
			public void method_18374(double d, double e) {
				VillagerTradingScreen.this.page--;
				if (VillagerTradingScreen.this.page < 0) {
					VillagerTradingScreen.this.page = 0;
				}

				VillagerTradingScreen.this.method_18745();
			}
		});
		this.nextButton.active = false;
		this.previousButton.active = false;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		String string = this.title.asFormattedString();
		this.textRenderer.method_18355(string, (float)(this.backgroundWidth / 2 - this.textRenderer.getStringWidth(string) / 2), 6.0F, 4210752);
		this.textRenderer.method_18355(this.field_20410.getName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
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
		this.renderBackground();
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
			this.field_20308.field_20932 = 100.0F;
			this.field_20308.method_19397(itemStack, i + 36, j + 24);
			this.field_20308.method_19383(this.textRenderer, itemStack, i + 36, j + 24);
			if (!itemStack2.isEmpty()) {
				this.field_20308.method_19397(itemStack2, i + 62, j + 24);
				this.field_20308.method_19383(this.textRenderer, itemStack2, i + 62, j + 24);
			}

			this.field_20308.method_19397(itemStack3, i + 120, j + 24);
			this.field_20308.method_19383(this.textRenderer, itemStack3, i + 120, j + 24);
			this.field_20308.field_20932 = 0.0F;
			GlStateManager.disableLighting();
			if (this.method_1134(36, 24, 16, 16, (double)mouseX, (double)mouseY) && !itemStack.isEmpty()) {
				this.renderTooltip(itemStack, mouseX, mouseY);
			} else if (!itemStack2.isEmpty() && this.method_1134(62, 24, 16, 16, (double)mouseX, (double)mouseY) && !itemStack2.isEmpty()) {
				this.renderTooltip(itemStack2, mouseX, mouseY);
			} else if (!itemStack3.isEmpty() && this.method_1134(120, 24, 16, 16, (double)mouseX, (double)mouseY) && !itemStack3.isEmpty()) {
				this.renderTooltip(itemStack3, mouseX, mouseY);
			} else if (tradeOffer.isDisabled()
				&& (this.method_1134(83, 21, 28, 21, (double)mouseX, (double)mouseY) || this.method_1134(83, 51, 28, 21, (double)mouseX, (double)mouseY))) {
				this.renderTooltip(I18n.translate("merchant.deprecated"), mouseX, mouseY);
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			DiffuseLighting.enableNormally();
		}

		this.renderTooltip(mouseX, mouseY);
	}

	public Trader getTrader() {
		return this.trader;
	}

	abstract static class PaginationButton extends ButtonWidget {
		private final boolean isRight;

		public PaginationButton(int i, int j, int k, boolean bl) {
			super(i, j, k, 12, 19, "");
			this.isRight = bl;
		}

		@Override
		public void method_891(int i, int j, float f) {
			if (this.visible) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(VillagerTradingScreen.TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				boolean bl = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
				int k = 0;
				int l = 176;
				if (!this.active) {
					l += this.width * 2;
				} else if (bl) {
					l += this.width;
				}

				if (!this.isRight) {
					k += this.height;
				}

				this.drawTexture(this.x, this.y, l, k, this.width, this.height);
			}
		}
	}
}
