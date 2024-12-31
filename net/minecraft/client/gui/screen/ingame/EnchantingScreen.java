package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EnchantingPhrases;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantingScreenHandler;
import net.minecraft.text.Nameable;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;

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
		this.textRenderer.draw(this.nameable.getName().asUnformattedString(), 12, 5, 4210752);
		this.textRenderer.draw(this.playerInventory.getName().asUnformattedString(), 8, this.backgroundHeight - 96 + 2, 4210752);
	}

	@Override
	public void tick() {
		super.tick();
		this.doTick();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;

		for (int k = 0; k < 3; k++) {
			int l = mouseX - (i + 60);
			int m = mouseY - (j + 14 + 19 * k);
			if (l >= 0 && m >= 0 && l < 108 && m < 19 && this.enchantingScreenHandler.onButtonClick(this.client.player, k)) {
				this.client.interactionManager.clickButton(this.enchantingScreenHandler.syncId, k);
			}
		}
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
		Window window = new Window(this.client);
		GlStateManager.viewport(
			(window.getWidth() - 320) / 2 * window.getScaleFactor(),
			(window.getHeight() - 240) / 2 * window.getScaleFactor(),
			320 * window.getScaleFactor(),
			240 * window.getScaleFactor()
		);
		GlStateManager.translate(-0.34F, 0.23F, 0.0F);
		Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
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
		float k = this.pageAngle + (this.nextPageAngle - this.pageAngle) * delta + 0.25F;
		float l = this.pageAngle + (this.nextPageAngle - this.pageAngle) * delta + 0.75F;
		k = (k - (float)MathHelper.fastFloor((double)k)) * 1.6F - 0.3F;
		l = (l - (float)MathHelper.fastFloor((double)l)) * 1.6F - 0.3F;
		if (k < 0.0F) {
			k = 0.0F;
		}

		if (l < 0.0F) {
			l = 0.0F;
		}

		if (k > 1.0F) {
			k = 1.0F;
		}

		if (l > 1.0F) {
			l = 1.0F;
		}

		GlStateManager.enableRescaleNormal();
		BOOK_MODEL.render(null, 0.0F, k, l, h, 0.0F, 0.0625F);
		GlStateManager.disableRescaleNormal();
		DiffuseLighting.disable();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.client.width, this.client.height);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		DiffuseLighting.disable();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantingPhrases.getInstance().setSeed((long)this.enchantingScreenHandler.enchantmentPower);
		int m = this.enchantingScreenHandler.getLapisCount();

		for (int n = 0; n < 3; n++) {
			int o = i + 60;
			int p = o + 20;
			this.zOffset = 0.0F;
			this.client.getTextureManager().bindTexture(TEXTURE);
			int q = this.enchantingScreenHandler.enchantmentId[n];
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			if (q == 0) {
				this.drawTexture(o, j + 14 + 19 * n, 0, 185, 108, 19);
			} else {
				String string = "" + q;
				int r = 86 - this.textRenderer.getStringWidth(string);
				String string2 = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, r);
				TextRenderer textRenderer = this.client.shadowTextRenderer;
				int s = 6839882;
				if ((m < n + 1 || this.client.player.experienceLevel < q) && !this.client.player.abilities.creativeMode) {
					this.drawTexture(o, j + 14 + 19 * n, 0, 185, 108, 19);
					this.drawTexture(o + 1, j + 15 + 19 * n, 16 * n, 239, 16, 16);
					textRenderer.drawTrimmed(string2, p, j + 16 + 19 * n, r, (s & 16711422) >> 1);
					s = 4226832;
				} else {
					int t = mouseX - (i + 60);
					int u = mouseY - (j + 14 + 19 * n);
					if (t >= 0 && u >= 0 && t < 108 && u < 19) {
						this.drawTexture(o, j + 14 + 19 * n, 0, 204, 108, 19);
						s = 16777088;
					} else {
						this.drawTexture(o, j + 14 + 19 * n, 0, 166, 108, 19);
					}

					this.drawTexture(o + 1, j + 15 + 19 * n, 16 * n, 223, 16, 16);
					textRenderer.drawTrimmed(string2, p, j + 16 + 19 * n, r, s);
					s = 8453920;
				}

				textRenderer = this.client.textRenderer;
				textRenderer.drawWithShadow(string, (float)(p + 86 - textRenderer.getStringWidth(string)), (float)(j + 16 + 19 * n + 7), s);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		tickDelta = this.client.method_14461();
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
			if (this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && k > 0 && l >= 0 && enchantment != null) {
				List<String> list = Lists.newArrayList();
				list.add("" + Formatting.WHITE + Formatting.ITALIC + I18n.translate("container.enchant.clue", enchantment.getTranslatedName(l)));
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
