package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.advancement.Achievement;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.StatsListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class AchievementsScreen extends Screen implements StatsListener {
	private static final int MIN_PAN_X = AchievementsAndCriterions.minColumn * 24 - 112;
	private static final int MIN_PAN_Y = AchievementsAndCriterions.minRow * 24 - 112;
	private static final int MAX_PAN_X = AchievementsAndCriterions.maxColumn * 24 - 77;
	private static final int MAX_PAN_Y = AchievementsAndCriterions.maxRow * 24 - 77;
	private static final Identifier ACHIEVEMENT_BACKGROUND = new Identifier("textures/gui/achievement/achievement_background.png");
	protected Screen parent;
	protected int originX = 256;
	protected int originY = 202;
	protected int targetMouseX;
	protected int targetMouseY;
	protected float fov = 1.0F;
	protected double attemptedCenterX;
	protected double attemptedCenterY;
	protected double targetCenterX;
	protected double targetCenterY;
	protected double centeredX;
	protected double centeredY;
	private int isDragging;
	private final StatHandler handler;
	private boolean downloadingStats = true;

	public AchievementsScreen(Screen screen, StatHandler statHandler) {
		this.parent = screen;
		this.handler = statHandler;
		int i = 141;
		int j = 141;
		this.centeredX = (double)(AchievementsAndCriterions.TAKING_INVENTORY.column * 24 - 70 - 12);
		this.attemptedCenterX = this.centeredX;
		this.targetCenterX = this.centeredX;
		this.centeredY = (double)(AchievementsAndCriterions.TAKING_INVENTORY.row * 24 - 70);
		this.attemptedCenterY = this.centeredY;
		this.targetCenterY = this.centeredY;
	}

	@Override
	public void init() {
		this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
		this.buttons.clear();
		this.buttons.add(new OptionButtonWidget(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.translate("gui.done")));
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (!this.downloadingStats) {
			if (button.id == 1) {
				this.client.setScreen(this.parent);
			}
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == this.client.options.inventoryKey.getCode()) {
			this.client.setScreen(null);
			this.client.closeScreen();
		} else {
			super.keyPressed(id, code);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.downloadingStats) {
			this.renderBackground();
			this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				this.textRenderer,
				PROGRESS_BAR_STAGES[(int)(MinecraftClient.getTime() / 150L % (long)PROGRESS_BAR_STAGES.length)],
				this.width / 2,
				this.height / 2 + this.textRenderer.fontHeight * 2,
				16777215
			);
		} else {
			if (Mouse.isButtonDown(0)) {
				int i = (this.width - this.originX) / 2;
				int j = (this.height - this.originY) / 2;
				int k = i + 8;
				int l = j + 17;
				if ((this.isDragging == 0 || this.isDragging == 1) && mouseX >= k && mouseX < k + 224 && mouseY >= l && mouseY < l + 155) {
					if (this.isDragging == 0) {
						this.isDragging = 1;
					} else {
						this.targetCenterX = this.targetCenterX - (double)((float)(mouseX - this.targetMouseX) * this.fov);
						this.targetCenterY = this.targetCenterY - (double)((float)(mouseY - this.targetMouseY) * this.fov);
						this.attemptedCenterX = this.targetCenterX;
						this.attemptedCenterY = this.targetCenterY;
						this.centeredX = this.targetCenterX;
						this.centeredY = this.targetCenterY;
					}

					this.targetMouseX = mouseX;
					this.targetMouseY = mouseY;
				}
			} else {
				this.isDragging = 0;
			}

			int m = Mouse.getDWheel();
			float f = this.fov;
			if (m < 0) {
				this.fov += 0.25F;
			} else if (m > 0) {
				this.fov -= 0.25F;
			}

			this.fov = MathHelper.clamp(this.fov, 1.0F, 2.0F);
			if (this.fov != f) {
				float g = f * (float)this.originX;
				float h = f * (float)this.originY;
				float n = this.fov * (float)this.originX;
				float o = this.fov * (float)this.originY;
				this.targetCenterX -= (double)((n - g) * 0.5F);
				this.targetCenterY -= (double)((o - h) * 0.5F);
				this.attemptedCenterX = this.targetCenterX;
				this.attemptedCenterY = this.targetCenterY;
				this.centeredX = this.targetCenterX;
				this.centeredY = this.targetCenterY;
			}

			if (this.centeredX < (double)MIN_PAN_X) {
				this.centeredX = (double)MIN_PAN_X;
			}

			if (this.centeredY < (double)MIN_PAN_Y) {
				this.centeredY = (double)MIN_PAN_Y;
			}

			if (this.centeredX >= (double)MAX_PAN_X) {
				this.centeredX = (double)(MAX_PAN_X - 1);
			}

			if (this.centeredY >= (double)MAX_PAN_Y) {
				this.centeredY = (double)(MAX_PAN_Y - 1);
			}

			this.renderBackground();
			this.renderIcons(mouseX, mouseY, tickDelta);
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			this.renderTitle();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
		}
	}

	@Override
	public void onStatsReady() {
		if (this.downloadingStats) {
			this.downloadingStats = false;
		}
	}

	@Override
	public void tick() {
		if (!this.downloadingStats) {
			this.attemptedCenterX = this.targetCenterX;
			this.attemptedCenterY = this.targetCenterY;
			double d = this.centeredX - this.targetCenterX;
			double e = this.centeredY - this.targetCenterY;
			if (d * d + e * e < 4.0) {
				this.targetCenterX += d;
				this.targetCenterY += e;
			} else {
				this.targetCenterX += d * 0.85;
				this.targetCenterY += e * 0.85;
			}
		}
	}

	protected void renderTitle() {
		int i = (this.width - this.originX) / 2;
		int j = (this.height - this.originY) / 2;
		this.textRenderer.draw(I18n.translate("gui.achievements"), i + 15, j + 5, 4210752);
	}

	protected void renderIcons(int mouseX, int mouseY, float tickDelta) {
		int i = MathHelper.floor(this.attemptedCenterX + (this.targetCenterX - this.attemptedCenterX) * (double)tickDelta);
		int j = MathHelper.floor(this.attemptedCenterY + (this.targetCenterY - this.attemptedCenterY) * (double)tickDelta);
		if (i < MIN_PAN_X) {
			i = MIN_PAN_X;
		}

		if (j < MIN_PAN_Y) {
			j = MIN_PAN_Y;
		}

		if (i >= MAX_PAN_X) {
			i = MAX_PAN_X - 1;
		}

		if (j >= MAX_PAN_Y) {
			j = MAX_PAN_Y - 1;
		}

		int k = (this.width - this.originX) / 2;
		int l = (this.height - this.originY) / 2;
		int m = k + 16;
		int n = l + 17;
		this.zOffset = 0.0F;
		GlStateManager.depthFunc(518);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)m, (float)n, -200.0F);
		GlStateManager.scale(1.0F / this.fov, 1.0F / this.fov, 0.0F);
		GlStateManager.enableTexture();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		int o = i + 288 >> 4;
		int p = j + 288 >> 4;
		int q = (i + 288) % 16;
		int r = (j + 288) % 16;
		int s = 4;
		int t = 8;
		int u = 10;
		int v = 22;
		int w = 37;
		Random random = new Random();
		float f = 16.0F / this.fov;
		float g = 16.0F / this.fov;

		for (int x = 0; (float)x * f - (float)r < 155.0F; x++) {
			float h = 0.6F - (float)(p + x) / 25.0F * 0.3F;
			GlStateManager.color(h, h, h, 1.0F);

			for (int y = 0; (float)y * g - (float)q < 224.0F; y++) {
				random.setSeed((long)(this.client.getSession().getUuid().hashCode() + o + y + (p + x) * 16));
				int z = random.nextInt(1 + p + x) + (p + x) / 2;
				Sprite sprite = this.getSprite(Blocks.SAND);
				if (z > 37 || p + x == 35) {
					Block block = Blocks.BEDROCK;
					sprite = this.getSprite(block);
				} else if (z == 22) {
					if (random.nextInt(2) == 0) {
						sprite = this.getSprite(Blocks.DIAMOND_ORE);
					} else {
						sprite = this.getSprite(Blocks.REDSTONE_ORE);
					}
				} else if (z == 10) {
					sprite = this.getSprite(Blocks.IRON_ORE);
				} else if (z == 8) {
					sprite = this.getSprite(Blocks.COAL_ORE);
				} else if (z > 4) {
					sprite = this.getSprite(Blocks.STONE);
				} else if (z > 0) {
					sprite = this.getSprite(Blocks.DIRT);
				}

				this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				this.drawSprite(y * 16 - q, x * 16 - r, sprite, 16, 16);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		this.client.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

		for (int aa = 0; aa < AchievementsAndCriterions.ACHIEVEMENTS.size(); aa++) {
			Achievement achievement = (Achievement)AchievementsAndCriterions.ACHIEVEMENTS.get(aa);
			if (achievement.parent != null) {
				int ab = achievement.column * 24 - i + 11;
				int ac = achievement.row * 24 - j + 11;
				int ad = achievement.parent.column * 24 - i + 11;
				int ae = achievement.parent.row * 24 - j + 11;
				boolean bl = this.handler.hasAchievement(achievement);
				boolean bl2 = this.handler.hasParentAchievement(achievement);
				int af = this.handler.getAchievementDepth(achievement);
				if (af <= 4) {
					int ag = -16777216;
					if (bl) {
						ag = -6250336;
					} else if (bl2) {
						ag = -16711936;
					}

					this.drawHorizontalLine(ab, ad, ac, ag);
					this.drawVerticalLine(ad, ac, ae, ag);
					if (ab > ad) {
						this.drawTexture(ab - 11 - 7, ac - 5, 114, 234, 7, 11);
					} else if (ab < ad) {
						this.drawTexture(ab + 11, ac - 5, 107, 234, 7, 11);
					} else if (ac > ae) {
						this.drawTexture(ab - 5, ac - 11 - 7, 96, 234, 11, 7);
					} else if (ac < ae) {
						this.drawTexture(ab - 5, ac + 11, 96, 241, 11, 7);
					}
				}
			}
		}

		Achievement achievement2 = null;
		float ah = (float)(mouseX - m) * this.fov;
		float ai = (float)(mouseY - n) * this.fov;
		DiffuseLighting.enable();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();

		for (int aj = 0; aj < AchievementsAndCriterions.ACHIEVEMENTS.size(); aj++) {
			Achievement achievement3 = (Achievement)AchievementsAndCriterions.ACHIEVEMENTS.get(aj);
			int ak = achievement3.column * 24 - i;
			int al = achievement3.row * 24 - j;
			if (ak >= -24 && al >= -24 && (float)ak <= 224.0F * this.fov && (float)al <= 155.0F * this.fov) {
				int am = this.handler.getAchievementDepth(achievement3);
				if (this.handler.hasAchievement(achievement3)) {
					float an = 0.75F;
					GlStateManager.color(0.75F, 0.75F, 0.75F, 1.0F);
				} else if (this.handler.hasParentAchievement(achievement3)) {
					float ao = 1.0F;
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				} else if (am < 3) {
					float ap = 0.3F;
					GlStateManager.color(0.3F, 0.3F, 0.3F, 1.0F);
				} else if (am == 3) {
					float aq = 0.2F;
					GlStateManager.color(0.2F, 0.2F, 0.2F, 1.0F);
				} else {
					if (am != 4) {
						continue;
					}

					float ar = 0.1F;
					GlStateManager.color(0.1F, 0.1F, 0.1F, 1.0F);
				}

				this.client.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
				if (achievement3.isChallenge()) {
					this.drawTexture(ak - 2, al - 2, 26, 202, 26, 26);
				} else {
					this.drawTexture(ak - 2, al - 2, 0, 202, 26, 26);
				}

				if (!this.handler.hasParentAchievement(achievement3)) {
					float as = 0.1F;
					GlStateManager.color(0.1F, 0.1F, 0.1F, 1.0F);
					this.itemRenderer.setRenderingAchievement(false);
				}

				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				this.itemRenderer.method_12461(achievement3.logo, ak + 3, al + 3);
				GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableLighting();
				if (!this.handler.hasParentAchievement(achievement3)) {
					this.itemRenderer.setRenderingAchievement(true);
				}

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				if (ah >= (float)ak && ah <= (float)(ak + 22) && ai >= (float)al && ai <= (float)(al + 22)) {
					achievement2 = achievement3;
				}
			}
		}

		GlStateManager.disableDepthTest();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
		this.drawTexture(k, l, 0, 0, this.originX, this.originY);
		this.zOffset = 0.0F;
		GlStateManager.depthFunc(515);
		GlStateManager.disableDepthTest();
		GlStateManager.enableTexture();
		super.render(mouseX, mouseY, tickDelta);
		if (achievement2 != null) {
			String string = achievement2.getText().asUnformattedString();
			String string2 = achievement2.getDescription();
			int at = mouseX + 12;
			int au = mouseY - 4;
			int av = this.handler.getAchievementDepth(achievement2);
			if (this.handler.hasParentAchievement(achievement2)) {
				int aw = Math.max(this.textRenderer.getStringWidth(string), 120);
				int ax = this.textRenderer.getHeightSplit(string2, aw);
				if (this.handler.hasAchievement(achievement2)) {
					ax += 12;
				}

				this.fillGradient(at - 3, au - 3, at + aw + 3, au + ax + 3 + 12, -1073741824, -1073741824);
				this.textRenderer.drawTrimmed(string2, at, au + 12, aw, -6250336);
				if (this.handler.hasAchievement(achievement2)) {
					this.textRenderer.drawWithShadow(I18n.translate("achievement.taken"), (float)at, (float)(au + ax + 4), -7302913);
				}
			} else if (av == 3) {
				string = I18n.translate("achievement.unknown");
				int ay = Math.max(this.textRenderer.getStringWidth(string), 120);
				String string3 = new TranslatableText("achievement.requires", achievement2.parent.getText()).asUnformattedString();
				int az = this.textRenderer.getHeightSplit(string3, ay);
				this.fillGradient(at - 3, au - 3, at + ay + 3, au + az + 12 + 3, -1073741824, -1073741824);
				this.textRenderer.drawTrimmed(string3, at, au + 12, ay, -9416624);
			} else if (av < 3) {
				int ba = Math.max(this.textRenderer.getStringWidth(string), 120);
				String string4 = new TranslatableText("achievement.requires", achievement2.parent.getText()).asUnformattedString();
				int bb = this.textRenderer.getHeightSplit(string4, ba);
				this.fillGradient(at - 3, au - 3, at + ba + 3, au + bb + 12 + 3, -1073741824, -1073741824);
				this.textRenderer.drawTrimmed(string4, at, au + 12, ba, -9416624);
			} else {
				string = null;
			}

			if (string != null) {
				this.textRenderer
					.drawWithShadow(
						string,
						(float)at,
						(float)au,
						this.handler.hasParentAchievement(achievement2) ? (achievement2.isChallenge() ? -128 : -1) : (achievement2.isChallenge() ? -8355776 : -8355712)
					);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.enableLighting();
		DiffuseLighting.disable();
	}

	private Sprite getSprite(Block block) {
		return MinecraftClient.getInstance().getBlockRenderManager().getModels().getParticleSprite(block.getDefaultState());
	}

	@Override
	public boolean shouldPauseGame() {
		return !this.downloadingStats;
	}
}
