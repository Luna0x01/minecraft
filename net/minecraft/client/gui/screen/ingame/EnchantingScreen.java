package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EnchantingPhrases;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantingScreenHandler;
import net.minecraft.text.Nameable;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EnchantingScreen extends HandledScreen {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
	private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
	private static final BookModel BOOK_MODEL = new BookModel();
	private final PlayerInventory playerInventory;
	private final Random random = new Random();
	private final EnchantingScreenHandler enchantingScreenHandler;
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float approximatePageAngle;
	public float pageRotationSpeed;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	private ItemStack stack = ItemStack.EMPTY;
	private final Nameable nameable;

	public EnchantingScreen(PlayerInventory playerInventory, World world, Nameable nameable) {
		super(new EnchantingScreenHandler(playerInventory, world));
		this.playerInventory = playerInventory;
		this.enchantingScreenHandler = (EnchantingScreenHandler)this.screenHandler;
		this.nameable = nameable;
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		this.textRenderer.method_18355(this.nameable.getName().asFormattedString(), 12.0F, 5.0F, 4210752);
		this.textRenderer.method_18355(this.playerInventory.getName().asFormattedString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		this.doTick();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = (this.width - this.backgroundWidth) / 2;
		int k = (this.height - this.backgroundHeight) / 2;

		for (int l = 0; l < 3; l++) {
			double f = d - (double)(j + 60);
			double g = e - (double)(k + 14 + 19 * l);
			if (f >= 0.0 && g >= 0.0 && f < 108.0 && g < 19.0 && this.enchantingScreenHandler.onButtonClick(this.client.player, l)) {
				this.client.interactionManager.clickButton(this.enchantingScreenHandler.syncId, l);
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		int k = (int)this.client.field_19944.method_18325();
		GlStateManager.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		GlStateManager.translate(-0.34F, 0.23F, 0.0F);
		GlStateManager.method_19121(Matrix4f.method_19642(90.0, 1.3333334F, 9.0F, 80.0F));
		float f = 1.0F;
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		DiffuseLighting.enableNormally();
		GlStateManager.translate(0.0F, 3.3F, -16.0F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		float g = 5.0F;
		GlStateManager.scale(5.0F, 5.0F, 5.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
		GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
		float h = this.pageTurningSpeed + (this.nextPageTurningSpeed - this.pageTurningSpeed) * delta;
		GlStateManager.translate((1.0F - h) * 0.2F, (1.0F - h) * 0.1F, (1.0F - h) * 0.25F);
		GlStateManager.rotate(-(1.0F - h) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		float l = this.pageAngle + (this.nextPageAngle - this.pageAngle) * delta + 0.25F;
		float m = this.pageAngle + (this.nextPageAngle - this.pageAngle) * delta + 0.75F;
		l = (l - (float)MathHelper.fastFloor((double)l)) * 1.6F - 0.3F;
		m = (m - (float)MathHelper.fastFloor((double)m)) * 1.6F - 0.3F;
		if (l < 0.0F) {
			l = 0.0F;
		}

		if (m < 0.0F) {
			m = 0.0F;
		}

		if (l > 1.0F) {
			l = 1.0F;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		GlStateManager.enableRescaleNormal();
		BOOK_MODEL.render(null, 0.0F, l, m, h, 0.0F, 0.0625F);
		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.client.field_19944.method_18317(), this.client.field_19944.method_18318());
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		DiffuseLighting.disable();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantingPhrases.getInstance().setSeed((long)this.enchantingScreenHandler.enchantmentPower);
		int n = this.enchantingScreenHandler.getLapisCount();

		for (int o = 0; o < 3; o++) {
			int p = i + 60;
			int q = p + 20;
			this.zOffset = 0.0F;
			this.client.getTextureManager().bindTexture(TEXTURE);
			int r = this.enchantingScreenHandler.enchantmentId[o];
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			if (r == 0) {
				this.drawTexture(p, j + 14 + 19 * o, 0, 185, 108, 19);
			} else {
				String string = "" + r;
				int s = 86 - this.textRenderer.getStringWidth(string);
				String string2 = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, s);
				TextRenderer textRenderer = this.client.method_9391().method_18453(MinecraftClient.field_19943);
				int t = 6839882;
				if ((n < o + 1 || this.client.player.experienceLevel < r) && !this.client.player.abilities.creativeMode) {
					this.drawTexture(p, j + 14 + 19 * o, 0, 185, 108, 19);
					this.drawTexture(p + 1, j + 15 + 19 * o, 16 * o, 239, 16, 16);
					textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, (t & 16711422) >> 1);
					t = 4226832;
				} else {
					int u = mouseX - (i + 60);
					int v = mouseY - (j + 14 + 19 * o);
					if (u >= 0 && v >= 0 && u < 108 && v < 19) {
						this.drawTexture(p, j + 14 + 19 * o, 0, 204, 108, 19);
						t = 16777088;
					} else {
						this.drawTexture(p, j + 14 + 19 * o, 0, 166, 108, 19);
					}

					this.drawTexture(p + 1, j + 15 + 19 * o, 16 * o, 223, 16, 16);
					textRenderer.drawTrimmed(string2, q, j + 16 + 19 * o, s, t);
					t = 8453920;
				}

				textRenderer = this.client.textRenderer;
				textRenderer.drawWithShadow(string, (float)(q + 86 - textRenderer.getStringWidth(string)), (float)(j + 16 + 19 * o + 7), t);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		tickDelta = this.client.method_12143();
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);
		this.renderTooltip(mouseX, mouseY);
		boolean bl = this.client.player.abilities.creativeMode;
		int i = this.enchantingScreenHandler.getLapisCount();

		for (int j = 0; j < 3; j++) {
			int k = this.enchantingScreenHandler.enchantmentId[j];
			Enchantment enchantment = Enchantment.byIndex(this.enchantingScreenHandler.enchantmentLevel[j]);
			int l = this.enchantingScreenHandler.field_12271[j];
			int m = j + 1;
			if (this.method_1134(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) && k > 0 && l >= 0 && enchantment != null) {
				List<String> list = Lists.newArrayList();
				list.add("" + Formatting.WHITE + Formatting.ITALIC + I18n.translate("container.enchant.clue", enchantment.method_16257(l).asFormattedString()));
				if (!bl) {
					list.add("");
					if (this.client.player.experienceLevel < k) {
						list.add(Formatting.RED + I18n.translate("container.enchant.level.requirement", this.enchantingScreenHandler.enchantmentId[j]));
					} else {
						String string;
						if (m == 1) {
							string = I18n.translate("container.enchant.lapis.one");
						} else {
							string = I18n.translate("container.enchant.lapis.many", m);
						}

						Formatting formatting = i >= m ? Formatting.GRAY : Formatting.RED;
						list.add(formatting + "" + string);
						if (m == 1) {
							string = I18n.translate("container.enchant.level.one");
						} else {
							string = I18n.translate("container.enchant.level.many", m);
						}

						list.add(Formatting.GRAY + "" + string);
					}
				}

				this.renderTooltip(list, mouseX, mouseY);
				break;
			}
		}
	}

	public void doTick() {
		ItemStack itemStack = this.screenHandler.getSlot(0).getStack();
		if (!ItemStack.equalsAll(itemStack, this.stack)) {
			this.stack = itemStack;

			do {
				this.approximatePageAngle = this.approximatePageAngle + (float)(this.random.nextInt(4) - this.random.nextInt(4));
			} while (this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
		}

		this.ticks++;
		this.pageAngle = this.nextPageAngle;
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		boolean bl = false;

		for (int i = 0; i < 3; i++) {
			if (this.enchantingScreenHandler.enchantmentId[i] != 0) {
				bl = true;
			}
		}

		if (bl) {
			this.nextPageTurningSpeed += 0.2F;
		} else {
			this.nextPageTurningSpeed -= 0.2F;
		}

		this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
		float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
		float g = 0.2F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.pageRotationSpeed = this.pageRotationSpeed + (f - this.pageRotationSpeed) * 0.9F;
		this.nextPageAngle = this.nextPageAngle + this.pageRotationSpeed;
	}
}
