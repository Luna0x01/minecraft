package net.minecraft.client.gui.hud;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class InGameHud extends DrawableHelper {
	private static final Identifier VIGNETTE = new Identifier("textures/misc/vignette.png");
	private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");
	private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
	private final Random random = new Random();
	private final MinecraftClient client;
	private final ItemRenderer itemRenderer;
	private final ChatHud chatHud;
	private final StreamIndicatorHud streamIndicatorHud;
	private int ticks;
	private String overlayMessage = "";
	private int overlayRemaining;
	private boolean overlayTinted;
	public float vignetteDarkness = 1.0F;
	private int heldItemTooltipFade;
	private ItemStack heldItem;
	private final DebugHud debugHud;
	private final SpectatorHud spectatorHud;
	private final PlayerListHud playerListHud;
	private int titleTotalTicks;
	private String subtitle = "";
	private String title = "";
	private int titleFadeInTicks;
	private int titleRemainTicks;
	private int titleFadeOutTicks;
	private int renderHealthValue = 0;
	private int lastHealthValue = 0;
	private long lastHealthCheckTime = 0L;
	private long heartJumpEndTick = 0L;

	public InGameHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
		this.debugHud = new DebugHud(minecraftClient);
		this.spectatorHud = new SpectatorHud(minecraftClient);
		this.chatHud = new ChatHud(minecraftClient);
		this.streamIndicatorHud = new StreamIndicatorHud(minecraftClient);
		this.playerListHud = new PlayerListHud(minecraftClient, this);
		this.setDefaultTitleFade();
	}

	public void setDefaultTitleFade() {
		this.titleFadeInTicks = 10;
		this.titleRemainTicks = 70;
		this.titleFadeOutTicks = 20;
	}

	public void render(float tickDelta) {
		Window window = new Window(this.client);
		int i = window.getWidth();
		int j = window.getHeight();
		this.client.gameRenderer.setupHudMatrixMode();
		GlStateManager.enableBlend();
		if (MinecraftClient.isFancyGraphicsEnabled()) {
			this.renderVignetteOverlay(this.client.player.getBrightnessAtEyes(tickDelta), window);
		} else {
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		}

		ItemStack itemStack = this.client.player.inventory.getArmor(3);
		if (this.client.options.perspective == 0 && itemStack != null && itemStack.getItem() == Item.fromBlock(Blocks.PUMPKIN)) {
			this.renderPumpkinBlur(window);
		}

		if (!this.client.player.hasStatusEffect(StatusEffect.NAUSEA)) {
			float f = this.client.player.lastTimeInPortal + (this.client.player.timeInPortal - this.client.player.lastTimeInPortal) * tickDelta;
			if (f > 0.0F) {
				this.renderNausea(f, window);
			}
		}

		if (this.client.interactionManager.isSpectator()) {
			this.spectatorHud.render(window, tickDelta);
		} else {
			this.renderHotbar(window, tickDelta);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
		GlStateManager.enableBlend();
		if (this.showCrosshair()) {
			GlStateManager.blendFuncSeparate(775, 769, 1, 0);
			GlStateManager.enableAlphaTest();
			this.drawTexture(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
		}

		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		this.client.profiler.push("bossHealth");
		this.renderBossBar();
		this.client.profiler.pop();
		if (this.client.interactionManager.hasStatusBars()) {
			this.renderStatusBars(window);
		}

		GlStateManager.disableBlend();
		if (this.client.player.getSleepTimer() > 0) {
			this.client.profiler.push("sleep");
			GlStateManager.disableDepthTest();
			GlStateManager.disableAlphaTest();
			int k = this.client.player.getSleepTimer();
			float g = (float)k / 100.0F;
			if (g > 1.0F) {
				g = 1.0F - (float)(k - 100) / 10.0F;
			}

			int l = (int)(220.0F * g) << 24 | 1052704;
			fill(0, 0, i, j, l);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableDepthTest();
			this.client.profiler.pop();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int m = i / 2 - 91;
		if (this.client.player.isRidingHorse()) {
			this.renderHorseHealth(window, m);
		} else if (this.client.interactionManager.hasExperienceBar()) {
			this.renderExperienceBar(window, m);
		}

		if (this.client.options.heldItemTooltips && !this.client.interactionManager.isSpectator()) {
			this.renderHeldItemName(window);
		} else if (this.client.player.isSpectator()) {
			this.spectatorHud.render(window);
		}

		if (this.client.isDemo()) {
			this.renderDemoTime(window);
		}

		if (this.client.options.debugEnabled) {
			this.debugHud.render(window);
		}

		if (this.overlayRemaining > 0) {
			this.client.profiler.push("overlayMessage");
			float h = (float)this.overlayRemaining - tickDelta;
			int n = (int)(h * 255.0F / 20.0F);
			if (n > 255) {
				n = 255;
			}

			if (n > 8) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(i / 2), (float)(j - 68), 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
				int o = 16777215;
				if (this.overlayTinted) {
					o = MathHelper.hsvToRgb(h / 50.0F, 0.7F, 0.6F) & 16777215;
				}

				this.getFontRenderer().draw(this.overlayMessage, -this.getFontRenderer().getStringWidth(this.overlayMessage) / 2, -4, o + (n << 24 & 0xFF000000));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			this.client.profiler.pop();
		}

		if (this.titleTotalTicks > 0) {
			this.client.profiler.push("titleAndSubtitle");
			float p = (float)this.titleTotalTicks - tickDelta;
			int q = 255;
			if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
				float r = (float)(this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks) - p;
				q = (int)(r * 255.0F / (float)this.titleFadeInTicks);
			}

			if (this.titleTotalTicks <= this.titleFadeOutTicks) {
				q = (int)(p * 255.0F / (float)this.titleFadeOutTicks);
			}

			q = MathHelper.clamp(q, 0, 255);
			if (q > 8) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(i / 2), (float)(j / 2), 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
				GlStateManager.pushMatrix();
				GlStateManager.scale(4.0F, 4.0F, 4.0F);
				int t = q << 24 & 0xFF000000;
				this.getFontRenderer().draw(this.subtitle, (float)(-this.getFontRenderer().getStringWidth(this.subtitle) / 2), -10.0F, 16777215 | t, true);
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				this.getFontRenderer().draw(this.title, (float)(-this.getFontRenderer().getStringWidth(this.title) / 2), 5.0F, 16777215 | t, true);
				GlStateManager.popMatrix();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			this.client.profiler.pop();
		}

		Scoreboard scoreboard = this.client.world.getScoreboard();
		ScoreboardObjective scoreboardObjective = null;
		Team team = scoreboard.getPlayerTeam(this.client.player.getTranslationKey());
		if (team != null) {
			int u = team.getFormatting().getColorIndex();
			if (u >= 0) {
				scoreboardObjective = scoreboard.getObjectiveForSlot(3 + u);
			}
		}

		ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
		if (scoreboardObjective2 != null) {
			this.renderScoreboardObjective(scoreboardObjective2, window);
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.disableAlphaTest();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, (float)(j - 48), 0.0F);
		this.client.profiler.push("chat");
		this.chatHud.render(this.ticks);
		this.client.profiler.pop();
		GlStateManager.popMatrix();
		scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
		if (!this.client.options.playerListKey.isPressed()
			|| this.client.isIntegratedServerRunning() && this.client.player.networkHandler.getPlayerList().size() <= 1 && scoreboardObjective2 == null) {
			this.playerListHud.tick(false);
		} else {
			this.playerListHud.tick(true);
			this.playerListHud.render(i, scoreboard, scoreboardObjective2);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlphaTest();
	}

	protected void renderHotbar(Window window, float tickDelta) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(WIDGETS);
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			int i = window.getWidth() / 2;
			float f = this.zOffset;
			this.zOffset = -90.0F;
			this.drawTexture(i - 91, window.getHeight() - 22, 0, 0, 182, 22);
			this.drawTexture(i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, window.getHeight() - 22 - 1, 0, 22, 24, 22);
			this.zOffset = f;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);
			DiffuseLighting.enable();

			for (int j = 0; j < 9; j++) {
				int k = window.getWidth() / 2 - 90 + j * 20 + 2;
				int l = window.getHeight() - 16 - 3;
				this.renderHotbarItem(j, k, l, tickDelta, playerEntity);
			}

			DiffuseLighting.disable();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
		}
	}

	public void renderHorseHealth(Window window, int x) {
		this.client.profiler.push("jumpBar");
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		float f = this.client.player.getMountJumpStrength();
		int i = 182;
		int j = (int)(f * (float)(i + 1));
		int k = window.getHeight() - 32 + 3;
		this.drawTexture(x, k, 0, 84, i, 5);
		if (j > 0) {
			this.drawTexture(x, k, 0, 89, j, 5);
		}

		this.client.profiler.pop();
	}

	public void renderExperienceBar(Window window, int x) {
		this.client.profiler.push("expBar");
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		int i = this.client.player.getNextLevelExperience();
		if (i > 0) {
			int j = 182;
			int k = (int)(this.client.player.experienceProgress * (float)(j + 1));
			int l = window.getHeight() - 32 + 3;
			this.drawTexture(x, l, 0, 64, j, 5);
			if (k > 0) {
				this.drawTexture(x, l, 0, 69, k, 5);
			}
		}

		this.client.profiler.pop();
		if (this.client.player.experienceLevel > 0) {
			this.client.profiler.push("expLevel");
			int m = 8453920;
			String string = "" + this.client.player.experienceLevel;
			int n = (window.getWidth() - this.getFontRenderer().getStringWidth(string)) / 2;
			int o = window.getHeight() - 31 - 4;
			int p = 0;
			this.getFontRenderer().draw(string, n + 1, o, 0);
			this.getFontRenderer().draw(string, n - 1, o, 0);
			this.getFontRenderer().draw(string, n, o + 1, 0);
			this.getFontRenderer().draw(string, n, o - 1, 0);
			this.getFontRenderer().draw(string, n, o, m);
			this.client.profiler.pop();
		}
	}

	public void renderHeldItemName(Window window) {
		this.client.profiler.push("selectedItemName");
		if (this.heldItemTooltipFade > 0 && this.heldItem != null) {
			String string = this.heldItem.getCustomName();
			if (this.heldItem.hasCustomName()) {
				string = Formatting.ITALIC + string;
			}

			int i = (window.getWidth() - this.getFontRenderer().getStringWidth(string)) / 2;
			int j = window.getHeight() - 59;
			if (!this.client.interactionManager.hasStatusBars()) {
				j += 14;
			}

			int k = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(770, 771, 1, 0);
				this.getFontRenderer().drawWithShadow(string, (float)i, (float)j, 16777215 + (k << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}

		this.client.profiler.pop();
	}

	public void renderDemoTime(Window window) {
		this.client.profiler.push("demo");
		String string = "";
		if (this.client.world.getLastUpdateTime() >= 120500L) {
			string = I18n.translate("demo.demoExpired");
		} else {
			string = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getLastUpdateTime())));
		}

		int i = this.getFontRenderer().getStringWidth(string);
		this.getFontRenderer().drawWithShadow(string, (float)(window.getWidth() - i - 10), 5.0F, 16777215);
		this.client.profiler.pop();
	}

	protected boolean showCrosshair() {
		if (this.client.options.debugEnabled && !this.client.player.getReducedDebugInfo() && !this.client.options.reducedDebugInfo) {
			return false;
		} else if (this.client.interactionManager.isSpectator()) {
			if (this.client.targetedEntity != null) {
				return true;
			} else {
				if (this.client.result != null && this.client.result.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = this.client.result.getBlockPos();
					if (this.client.world.getBlockEntity(blockPos) instanceof Inventory) {
						return true;
					}
				}

				return false;
			}
		} else {
			return true;
		}
	}

	public void renderStreamIndicator(Window window) {
		this.streamIndicatorHud.render(window.getWidth() - 10, 10);
	}

	private void renderScoreboardObjective(ScoreboardObjective objective, Window window) {
		Scoreboard scoreboard = objective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
		List<ScoreboardPlayerScore> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<ScoreboardPlayerScore>() {
			public boolean apply(ScoreboardPlayerScore scoreboardPlayerScore) {
				return scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#");
			}
		}));
		if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}

		int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

		for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			String string = Team.decorateName(team, scoreboardPlayerScore.getPlayerName()) + ": " + Formatting.RED + scoreboardPlayerScore.getScore();
			i = Math.max(i, this.getFontRenderer().getStringWidth(string));
		}

		int j = collection.size() * this.getFontRenderer().fontHeight;
		int k = window.getHeight() / 2 + j / 3;
		int l = 3;
		int m = window.getWidth() - i - l;
		int n = 0;

		for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
			n++;
			Team team2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
			String string2 = Team.decorateName(team2, scoreboardPlayerScore2.getPlayerName());
			String string3 = Formatting.RED + "" + scoreboardPlayerScore2.getScore();
			int p = k - n * this.getFontRenderer().fontHeight;
			int q = window.getWidth() - l + 2;
			fill(m - 2, p, q, p + this.getFontRenderer().fontHeight, 1342177280);
			this.getFontRenderer().draw(string2, m, p, 553648127);
			this.getFontRenderer().draw(string3, q - this.getFontRenderer().getStringWidth(string3), p, 553648127);
			if (n == collection.size()) {
				String string4 = objective.getDisplayName();
				fill(m - 2, p - this.getFontRenderer().fontHeight - 1, q, p - 1, 1610612736);
				fill(m - 2, p - 1, q, p, 1342177280);
				this.getFontRenderer().draw(string4, m + i / 2 - this.getFontRenderer().getStringWidth(string4) / 2, p - this.getFontRenderer().fontHeight, 553648127);
			}
		}
	}

	private void renderStatusBars(Window window) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			int i = MathHelper.ceil(playerEntity.getHealth());
			boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
			if (i < this.renderHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = MinecraftClient.getTime();
				this.heartJumpEndTick = (long)(this.ticks + 20);
			} else if (i > this.renderHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = MinecraftClient.getTime();
				this.heartJumpEndTick = (long)(this.ticks + 10);
			}

			if (MinecraftClient.getTime() - this.lastHealthCheckTime > 1000L) {
				this.renderHealthValue = i;
				this.lastHealthValue = i;
				this.lastHealthCheckTime = MinecraftClient.getTime();
			}

			this.renderHealthValue = i;
			int j = this.lastHealthValue;
			this.random.setSeed((long)(this.ticks * 312871));
			boolean bl2 = false;
			HungerManager hungerManager = playerEntity.getHungerManager();
			int k = hungerManager.getFoodLevel();
			int l = hungerManager.getPrevFoodLevel();
			EntityAttributeInstance entityAttributeInstance = playerEntity.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH);
			int m = window.getWidth() / 2 - 91;
			int n = window.getWidth() / 2 + 91;
			int o = window.getHeight() - 39;
			float f = (float)entityAttributeInstance.getValue();
			float g = playerEntity.getAbsorption();
			int p = MathHelper.ceil((f + g) / 2.0F / 10.0F);
			int q = Math.max(10 - (p - 2), 3);
			int r = o - (p - 1) * q - 10;
			float h = g;
			int s = playerEntity.getArmorProtectionValue();
			int t = -1;
			if (playerEntity.hasStatusEffect(StatusEffect.REGENERATION)) {
				t = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.profiler.push("armor");

			for (int u = 0; u < 10; u++) {
				if (s > 0) {
					int v = m + u * 8;
					if (u * 2 + 1 < s) {
						this.drawTexture(v, r, 34, 9, 9, 9);
					}

					if (u * 2 + 1 == s) {
						this.drawTexture(v, r, 25, 9, 9, 9);
					}

					if (u * 2 + 1 > s) {
						this.drawTexture(v, r, 16, 9, 9, 9);
					}
				}
			}

			this.client.profiler.swap("health");

			for (int w = MathHelper.ceil((f + g) / 2.0F) - 1; w >= 0; w--) {
				int x = 16;
				if (playerEntity.hasStatusEffect(StatusEffect.POISON)) {
					x += 36;
				} else if (playerEntity.hasStatusEffect(StatusEffect.WITHER)) {
					x += 72;
				}

				int y = 0;
				if (bl) {
					y = 1;
				}

				int z = MathHelper.ceil((float)(w + 1) / 10.0F) - 1;
				int aa = m + w % 10 * 8;
				int ab = o - z * q;
				if (i <= 4) {
					ab += this.random.nextInt(2);
				}

				if (w == t) {
					ab -= 2;
				}

				int ac = 0;
				if (playerEntity.world.getLevelProperties().isHardcore()) {
					ac = 5;
				}

				this.drawTexture(aa, ab, 16 + y * 9, 9 * ac, 9, 9);
				if (bl) {
					if (w * 2 + 1 < j) {
						this.drawTexture(aa, ab, x + 54, 9 * ac, 9, 9);
					}

					if (w * 2 + 1 == j) {
						this.drawTexture(aa, ab, x + 63, 9 * ac, 9, 9);
					}
				}

				if (h > 0.0F) {
					if (h == g && g % 2.0F == 1.0F) {
						this.drawTexture(aa, ab, x + 153, 9 * ac, 9, 9);
					} else {
						this.drawTexture(aa, ab, x + 144, 9 * ac, 9, 9);
					}

					h -= 2.0F;
				} else {
					if (w * 2 + 1 < i) {
						this.drawTexture(aa, ab, x + 36, 9 * ac, 9, 9);
					}

					if (w * 2 + 1 == i) {
						this.drawTexture(aa, ab, x + 45, 9 * ac, 9, 9);
					}
				}
			}

			Entity entity = playerEntity.vehicle;
			if (entity == null) {
				this.client.profiler.swap("food");

				for (int ad = 0; ad < 10; ad++) {
					int ae = o;
					int af = 16;
					int ag = 0;
					if (playerEntity.hasStatusEffect(StatusEffect.HUNGER)) {
						af += 36;
						ag = 13;
					}

					if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
						ae = o + (this.random.nextInt(3) - 1);
					}

					if (bl2) {
						ag = 1;
					}

					int ah = n - ad * 8 - 9;
					this.drawTexture(ah, ae, 16 + ag * 9, 27, 9, 9);
					if (bl2) {
						if (ad * 2 + 1 < l) {
							this.drawTexture(ah, ae, af + 54, 27, 9, 9);
						}

						if (ad * 2 + 1 == l) {
							this.drawTexture(ah, ae, af + 63, 27, 9, 9);
						}
					}

					if (ad * 2 + 1 < k) {
						this.drawTexture(ah, ae, af + 36, 27, 9, 9);
					}

					if (ad * 2 + 1 == k) {
						this.drawTexture(ah, ae, af + 45, 27, 9, 9);
					}
				}
			} else if (entity instanceof LivingEntity) {
				this.client.profiler.swap("mountHealth");
				LivingEntity livingEntity = (LivingEntity)entity;
				int ai = (int)Math.ceil((double)livingEntity.getHealth());
				float aj = livingEntity.getMaxHealth();
				int ak = (int)(aj + 0.5F) / 2;
				if (ak > 30) {
					ak = 30;
				}

				int al = o;

				for (int am = 0; ak > 0; am += 20) {
					int an = Math.min(ak, 10);
					ak -= an;

					for (int ao = 0; ao < an; ao++) {
						int ap = 52;
						int aq = 0;
						if (bl2) {
							aq = 1;
						}

						int ar = n - ao * 8 - 9;
						this.drawTexture(ar, al, ap + aq * 9, 9, 9, 9);
						if (ao * 2 + 1 + am < ai) {
							this.drawTexture(ar, al, ap + 36, 9, 9, 9);
						}

						if (ao * 2 + 1 + am == ai) {
							this.drawTexture(ar, al, ap + 45, 9, 9, 9);
						}
					}

					al -= 10;
				}
			}

			this.client.profiler.swap("air");
			if (playerEntity.isSubmergedIn(Material.WATER)) {
				int as = this.client.player.getAir();
				int at = MathHelper.ceil((double)(as - 2) * 10.0 / 300.0);
				int au = MathHelper.ceil((double)as * 10.0 / 300.0) - at;

				for (int av = 0; av < at + au; av++) {
					if (av < at) {
						this.drawTexture(n - av * 8 - 9, r, 16, 18, 9, 9);
					} else {
						this.drawTexture(n - av * 8 - 9, r, 25, 18, 9, 9);
					}
				}
			}

			this.client.profiler.pop();
		}
	}

	private void renderBossBar() {
		if (BossBar.name != null && BossBar.framesToLive > 0) {
			BossBar.framesToLive--;
			TextRenderer textRenderer = this.client.textRenderer;
			Window window = new Window(this.client);
			int i = window.getWidth();
			int j = 182;
			int k = i / 2 - j / 2;
			int l = (int)(BossBar.percent * (float)(j + 1));
			int m = 12;
			this.drawTexture(k, m, 0, 74, j, 5);
			this.drawTexture(k, m, 0, 74, j, 5);
			if (l > 0) {
				this.drawTexture(k, m, 0, 79, l, 5);
			}

			String string = BossBar.name;
			this.getFontRenderer().drawWithShadow(string, (float)(i / 2 - this.getFontRenderer().getStringWidth(string) / 2), (float)(m - 10), 16777215);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
		}
	}

	private void renderPumpkinBlur(Window window) {
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableAlphaTest();
		this.client.getTextureManager().bindTexture(PUMPKIN_BLUR);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)window.getHeight(), -90.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex((double)window.getWidth(), (double)window.getHeight(), -90.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex((double)window.getWidth(), 0.0, -90.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderVignetteOverlay(float tickDelta, Window window) {
		tickDelta = 1.0F - tickDelta;
		tickDelta = MathHelper.clamp(tickDelta, 0.0F, 1.0F);
		WorldBorder worldBorder = this.client.world.getWorldBorder();
		float f = (float)worldBorder.getDistanceInsideBorder(this.client.player);
		double d = Math.min(
			worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getOldSize())
		);
		double e = Math.max((double)worldBorder.getWarningBlocks(), d);
		if ((double)f < e) {
			f = 1.0F - (float)((double)f / e);
		} else {
			f = 0.0F;
		}

		this.vignetteDarkness = (float)((double)this.vignetteDarkness + (double)(tickDelta - this.vignetteDarkness) * 0.01);
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(0, 769, 1, 0);
		if (f > 0.0F) {
			GlStateManager.color(0.0F, f, f, 1.0F);
		} else {
			GlStateManager.color(this.vignetteDarkness, this.vignetteDarkness, this.vignetteDarkness, 1.0F);
		}

		this.client.getTextureManager().bindTexture(VIGNETTE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)window.getHeight(), -90.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex((double)window.getWidth(), (double)window.getHeight(), -90.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex((double)window.getWidth(), 0.0, -90.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
	}

	private void renderNausea(float amplifier, Window window) {
		if (amplifier < 1.0F) {
			amplifier *= amplifier;
			amplifier *= amplifier;
			amplifier = amplifier * 0.8F + 0.2F;
		}

		GlStateManager.disableAlphaTest();
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, amplifier);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Sprite sprite = this.client.getBlockRenderManager().getModels().getParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
		float f = sprite.getMinU();
		float g = sprite.getMinV();
		float h = sprite.getMaxU();
		float i = sprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)window.getHeight(), -90.0).texture((double)f, (double)i).next();
		bufferBuilder.vertex((double)window.getWidth(), (double)window.getHeight(), -90.0).texture((double)h, (double)i).next();
		bufferBuilder.vertex((double)window.getWidth(), 0.0, -90.0).texture((double)h, (double)g).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture((double)f, (double)g).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHotbarItem(int slot, int x, int y, float tickDelta, PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.main[slot];
		if (itemStack != null) {
			float f = (float)itemStack.pickupTick - tickDelta;
			if (f > 0.0F) {
				GlStateManager.pushMatrix();
				float g = 1.0F + f / 5.0F;
				GlStateManager.translate((float)(x + 8), (float)(y + 12), 0.0F);
				GlStateManager.scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
			}

			this.itemRenderer.renderInGuiWithOverrides(itemStack, x, y);
			if (f > 0.0F) {
				GlStateManager.popMatrix();
			}

			this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, itemStack, x, y);
		}
	}

	public void tick() {
		if (this.overlayRemaining > 0) {
			this.overlayRemaining--;
		}

		if (this.titleTotalTicks > 0) {
			this.titleTotalTicks--;
			if (this.titleTotalTicks <= 0) {
				this.subtitle = "";
				this.title = "";
			}
		}

		this.ticks++;
		this.streamIndicatorHud.tick();
		if (this.client.player != null) {
			ItemStack itemStack = this.client.player.inventory.getMainHandStack();
			if (itemStack == null) {
				this.heldItemTooltipFade = 0;
			} else if (this.heldItem != null
				&& itemStack.getItem() == this.heldItem.getItem()
				&& ItemStack.equalsIgnoreDamage(itemStack, this.heldItem)
				&& (itemStack.isDamageable() || itemStack.getData() == this.heldItem.getData())) {
				if (this.heldItemTooltipFade > 0) {
					this.heldItemTooltipFade--;
				}
			} else {
				this.heldItemTooltipFade = 40;
			}

			this.heldItem = itemStack;
		}
	}

	public void setRecordPlayingOverlay(String name) {
		this.setOverlayMessage(I18n.translate("record.nowPlaying", name), true);
	}

	public void setOverlayMessage(String message, boolean tinted) {
		this.overlayMessage = message;
		this.overlayRemaining = 60;
		this.overlayTinted = tinted;
	}

	public void setTitles(String subtitle, String title, int titleFadeInTicks, int titleRemainTicks, int titleFadeOutTicks) {
		if (subtitle == null && title == null && titleFadeInTicks < 0 && titleRemainTicks < 0 && titleFadeOutTicks < 0) {
			this.subtitle = "";
			this.title = "";
			this.titleTotalTicks = 0;
		} else if (subtitle != null) {
			this.subtitle = subtitle;
			this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
		} else if (title != null) {
			this.title = title;
		} else {
			if (titleFadeInTicks >= 0) {
				this.titleFadeInTicks = titleFadeInTicks;
			}

			if (titleRemainTicks >= 0) {
				this.titleRemainTicks = titleRemainTicks;
			}

			if (titleFadeOutTicks >= 0) {
				this.titleFadeOutTicks = titleFadeOutTicks;
			}

			if (this.titleTotalTicks > 0) {
				this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
			}
		}
	}

	public void setOverlayMessage(Text text, boolean tinted) {
		this.setOverlayMessage(text.asUnformattedString(), tinted);
	}

	public ChatHud getChatHud() {
		return this.chatHud;
	}

	public int getTicks() {
		return this.ticks;
	}

	public TextRenderer getFontRenderer() {
		return this.client.textRenderer;
	}

	public SpectatorHud getSpectatorHud() {
		return this.spectatorHud;
	}

	public PlayerListHud getPlayerListWidget() {
		return this.playerListHud;
	}

	public void resetDebugHudChunk() {
		this.playerListHud.clear();
	}
}
