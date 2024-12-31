package net.minecraft.client.gui.hud;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2841;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.option.GameOptions;
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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
	private int ticks;
	private String overlayMessage = "";
	private int overlayRemaining;
	private boolean overlayTinted;
	public float vignetteDarkness = 1.0F;
	private int heldItemTooltipFade;
	private ItemStack heldItem;
	private final DebugHud debugHud;
	private final class_2841 field_13302;
	private final SpectatorHud spectatorHud;
	private final PlayerListHud playerListHud;
	private final BossBarHud bossbar;
	private int titleTotalTicks;
	private String subtitle = "";
	private String title = "";
	private int titleFadeInTicks;
	private int titleRemainTicks;
	private int titleFadeOutTicks;
	private int renderHealthValue;
	private int lastHealthValue;
	private long lastHealthCheckTime;
	private long heartJumpEndTick;

	public InGameHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
		this.debugHud = new DebugHud(minecraftClient);
		this.spectatorHud = new SpectatorHud(minecraftClient);
		this.chatHud = new ChatHud(minecraftClient);
		this.playerListHud = new PlayerListHud(minecraftClient, this);
		this.bossbar = new BossBarHud(minecraftClient);
		this.field_13302 = new class_2841(minecraftClient);
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
		TextRenderer textRenderer = this.getFontRenderer();
		this.client.gameRenderer.setupHudMatrixMode();
		GlStateManager.enableBlend();
		if (MinecraftClient.isFancyGraphicsEnabled()) {
			this.renderVignetteOverlay(this.client.player.getBrightnessAtEyes(tickDelta), window);
		} else {
			GlStateManager.enableDepthTest();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
		}

		ItemStack itemStack = this.client.player.inventory.getArmor(3);
		if (this.client.options.perspective == 0 && itemStack != null && itemStack.getItem() == Item.fromBlock(Blocks.PUMPKIN)) {
			this.renderPumpkinBlur(window);
		}

		if (!this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
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
		this.method_12164(tickDelta, window);
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		this.client.profiler.push("bossHealth");
		this.bossbar.render();
		this.client.profiler.pop();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
		if (this.client.interactionManager.hasStatusBars()) {
			this.renderStatusBars(window);
		}

		this.method_12166(window);
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

		this.method_12165(window);
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
				GlStateManager.method_12288(
					GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
				);
				int o = 16777215;
				if (this.overlayTinted) {
					o = MathHelper.hsvToRgb(h / 50.0F, 0.7F, 0.6F) & 16777215;
				}

				textRenderer.draw(this.overlayMessage, -textRenderer.getStringWidth(this.overlayMessage) / 2, -4, o + (n << 24 & 0xFF000000));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			this.client.profiler.pop();
		}

		this.field_13302.method_12176(window);
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
				GlStateManager.method_12288(
					GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
				);
				GlStateManager.pushMatrix();
				GlStateManager.scale(4.0F, 4.0F, 4.0F);
				int s = q << 24 & 0xFF000000;
				textRenderer.draw(this.subtitle, (float)(-textRenderer.getStringWidth(this.subtitle) / 2), -10.0F, 16777215 | s, true);
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				textRenderer.draw(this.title, (float)(-textRenderer.getStringWidth(this.title) / 2), 5.0F, 16777215 | s, true);
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
			int t = team.method_12130().getColorIndex();
			if (t >= 0) {
				scoreboardObjective = scoreboard.getObjectiveForSlot(3 + t);
			}
		}

		ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
		if (scoreboardObjective2 != null) {
			this.renderScoreboardObjective(scoreboardObjective2, window);
		}

		GlStateManager.enableBlend();
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
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

	private void method_12164(float f, Window window) {
		GameOptions gameOptions = this.client.options;
		if (gameOptions.perspective == 0) {
			if (this.client.interactionManager.isSpectator() && this.client.targetedEntity == null) {
				BlockHitResult blockHitResult = this.client.result;
				if (blockHitResult == null || blockHitResult.type != BlockHitResult.Type.BLOCK) {
					return;
				}

				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.client.world.getBlockState(blockPos).getBlock().hasBlockEntity() || !(this.client.world.getBlockEntity(blockPos) instanceof Inventory)) {
					return;
				}
			}

			int i = window.getWidth();
			int j = window.getHeight();
			if (gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.getReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(i / 2), (float)(j / 2), this.zOffset);
				Entity entity = this.client.getCameraEntity();
				GlStateManager.rotate(entity.prevPitch + (entity.pitch - entity.prevPitch) * f, -1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.prevYaw + (entity.yaw - entity.prevYaw) * f, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(-1.0F, -1.0F, -1.0F);
				GLX.method_12554(10);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.method_12288(
					GlStateManager.class_2870.ONE_MINUS_DST_COLOR,
					GlStateManager.class_2866.ONE_MINUS_SRC_COLOR,
					GlStateManager.class_2870.ONE,
					GlStateManager.class_2866.ZERO
				);
				GlStateManager.enableAlphaTest();
				this.drawTexture(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
				if (this.client.options.field_13290 == 1) {
					float g = this.client.player.method_13275(0.0F);
					if (g < 1.0F) {
						int k = j / 2 - 7 + 16;
						int l = i / 2 - 7;
						int m = (int)(g * 17.0F);
						this.drawTexture(l, k, 36, 94, 16, 4);
						this.drawTexture(l, k, 52, 94, m, 4);
					}
				}
			}
		}
	}

	protected void method_12165(Window window) {
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffectInstances();
		if (!collection.isEmpty()) {
			this.client.getTextureManager().bindTexture(HandledScreen.INVENTORY_TEXTURE);
			GlStateManager.enableBlend();
			int i = 0;
			int j = 0;

			for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getStatusEffect();
				if (statusEffect.hasIcon() && statusEffectInstance.shouldShowParticles()) {
					int k = window.getWidth();
					int l = 1;
					int m = statusEffect.getIconLevel();
					if (statusEffect.method_2448()) {
						i++;
						k -= 25 * i;
					} else {
						j++;
						k -= 25 * j;
						l += 26;
					}

					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					float f = 1.0F;
					if (statusEffectInstance.isAmbient()) {
						this.drawTexture(k, l, 165, 166, 24, 24);
					} else {
						this.drawTexture(k, l, 141, 166, 24, 24);
						if (statusEffectInstance.getDuration() <= 200) {
							int n = 10 - statusEffectInstance.getDuration() / 20;
							f = MathHelper.clamp((float)statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
								+ MathHelper.cos((float)statusEffectInstance.getDuration() * (float) Math.PI / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
						}
					}

					GlStateManager.color(1.0F, 1.0F, 1.0F, f);
					this.drawTexture(k + 3, l + 3, m % 8 * 18, 198 + m / 8 * 18, 18, 18);
				}
			}
		}
	}

	protected void renderHotbar(Window window, float tickDelta) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(WIDGETS);
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			ItemStack itemStack = playerEntity.getOffHandStack();
			HandOption handOption = playerEntity.getDurability().method_13037();
			int i = window.getWidth() / 2;
			float f = this.zOffset;
			int j = 182;
			int k = 91;
			this.zOffset = -90.0F;
			this.drawTexture(i - 91, window.getHeight() - 22, 0, 0, 182, 22);
			this.drawTexture(i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, window.getHeight() - 22 - 1, 0, 22, 24, 22);
			if (itemStack != null) {
				if (handOption == HandOption.LEFT) {
					this.drawTexture(i - 91 - 29, window.getHeight() - 23, 24, 22, 29, 24);
				} else {
					this.drawTexture(i + 91, window.getHeight() - 23, 53, 22, 29, 24);
				}
			}

			this.zOffset = f;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			DiffuseLighting.enable();

			for (int l = 0; l < 9; l++) {
				int m = i - 90 + l * 20 + 2;
				int n = window.getHeight() - 16 - 3;
				this.method_9422(m, n, tickDelta, playerEntity, playerEntity.inventory.main[l]);
			}

			if (itemStack != null) {
				int o = window.getHeight() - 16 - 3;
				if (handOption == HandOption.LEFT) {
					this.method_9422(i - 91 - 26, o, tickDelta, playerEntity, itemStack);
				} else {
					this.method_9422(i + 91 + 10, o, tickDelta, playerEntity, itemStack);
				}
			}

			if (this.client.options.field_13290 == 2) {
				float g = this.client.player.method_13275(0.0F);
				if (g < 1.0F) {
					int p = window.getHeight() - 20;
					int q = i + 91 + 6;
					if (handOption == HandOption.RIGHT) {
						q = i - 91 - 22;
					}

					this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
					int r = (int)(g * 19.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					this.drawTexture(q, p, 0, 94, 18, 18);
					this.drawTexture(q, p + 18 - r, 18, 112 - r, 18, r);
				}
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
		int j = (int)(f * 183.0F);
		int k = window.getHeight() - 32 + 3;
		this.drawTexture(x, k, 0, 84, 182, 5);
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
			int k = (int)(this.client.player.experienceProgress * 183.0F);
			int l = window.getHeight() - 32 + 3;
			this.drawTexture(x, l, 0, 64, 182, 5);
			if (k > 0) {
				this.drawTexture(x, l, 0, 69, k, 5);
			}
		}

		this.client.profiler.pop();
		if (this.client.player.experienceLevel > 0) {
			this.client.profiler.push("expLevel");
			String string = "" + this.client.player.experienceLevel;
			int m = (window.getWidth() - this.getFontRenderer().getStringWidth(string)) / 2;
			int n = window.getHeight() - 31 - 4;
			this.getFontRenderer().draw(string, m + 1, n, 0);
			this.getFontRenderer().draw(string, m - 1, n, 0);
			this.getFontRenderer().draw(string, m, n + 1, 0);
			this.getFontRenderer().draw(string, m, n - 1, 0);
			this.getFontRenderer().draw(string, m, n, 8453920);
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
				GlStateManager.method_12288(
					GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
				);
				this.getFontRenderer().drawWithShadow(string, (float)i, (float)j, 16777215 + (k << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}

		this.client.profiler.pop();
	}

	public void renderDemoTime(Window window) {
		this.client.profiler.push("demo");
		String string;
		if (this.client.world.getLastUpdateTime() >= 120500L) {
			string = I18n.translate("demo.demoExpired");
		} else {
			string = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getLastUpdateTime())));
		}

		int i = this.getFontRenderer().getStringWidth(string);
		this.getFontRenderer().drawWithShadow(string, (float)(window.getWidth() - i - 10), 5.0F, 16777215);
		this.client.profiler.pop();
	}

	private void renderScoreboardObjective(ScoreboardObjective objective, Window window) {
		Scoreboard scoreboard = objective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
		List<ScoreboardPlayerScore> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<ScoreboardPlayerScore>() {
			public boolean apply(@Nullable ScoreboardPlayerScore scoreboardPlayerScore) {
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
		int m = window.getWidth() - i - 3;
		int n = 0;

		for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
			n++;
			Team team2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
			String string2 = Team.decorateName(team2, scoreboardPlayerScore2.getPlayerName());
			String string3 = Formatting.RED + "" + scoreboardPlayerScore2.getScore();
			int p = k - n * this.getFontRenderer().fontHeight;
			int q = window.getWidth() - 3 + 2;
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
			HungerManager hungerManager = playerEntity.getHungerManager();
			int k = hungerManager.getFoodLevel();
			EntityAttributeInstance entityAttributeInstance = playerEntity.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH);
			int l = window.getWidth() / 2 - 91;
			int m = window.getWidth() / 2 + 91;
			int n = window.getHeight() - 39;
			float f = (float)entityAttributeInstance.getValue();
			int o = MathHelper.ceil(playerEntity.getAbsorption());
			int p = MathHelper.ceil((f + (float)o) / 2.0F / 10.0F);
			int q = Math.max(10 - (p - 2), 3);
			int r = n - (p - 1) * q - 10;
			int s = n - 10;
			int t = o;
			int u = playerEntity.getArmorProtectionValue();
			int v = -1;
			if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
				v = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.profiler.push("armor");

			for (int w = 0; w < 10; w++) {
				if (u > 0) {
					int x = l + w * 8;
					if (w * 2 + 1 < u) {
						this.drawTexture(x, r, 34, 9, 9, 9);
					}

					if (w * 2 + 1 == u) {
						this.drawTexture(x, r, 25, 9, 9, 9);
					}

					if (w * 2 + 1 > u) {
						this.drawTexture(x, r, 16, 9, 9, 9);
					}
				}
			}

			this.client.profiler.swap("health");

			for (int y = MathHelper.ceil((f + (float)o) / 2.0F) - 1; y >= 0; y--) {
				int z = 16;
				if (playerEntity.hasStatusEffect(StatusEffects.POISON)) {
					z += 36;
				} else if (playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
					z += 72;
				}

				int aa = 0;
				if (bl) {
					aa = 1;
				}

				int ab = MathHelper.ceil((float)(y + 1) / 10.0F) - 1;
				int ac = l + y % 10 * 8;
				int ad = n - ab * q;
				if (i <= 4) {
					ad += this.random.nextInt(2);
				}

				if (t <= 0 && y == v) {
					ad -= 2;
				}

				int ae = 0;
				if (playerEntity.world.getLevelProperties().isHardcore()) {
					ae = 5;
				}

				this.drawTexture(ac, ad, 16 + aa * 9, 9 * ae, 9, 9);
				if (bl) {
					if (y * 2 + 1 < j) {
						this.drawTexture(ac, ad, z + 54, 9 * ae, 9, 9);
					}

					if (y * 2 + 1 == j) {
						this.drawTexture(ac, ad, z + 63, 9 * ae, 9, 9);
					}
				}

				if (t > 0) {
					if (t == o && o % 2 == 1) {
						this.drawTexture(ac, ad, z + 153, 9 * ae, 9, 9);
						t--;
					} else {
						this.drawTexture(ac, ad, z + 144, 9 * ae, 9, 9);
						t -= 2;
					}
				} else {
					if (y * 2 + 1 < i) {
						this.drawTexture(ac, ad, z + 36, 9 * ae, 9, 9);
					}

					if (y * 2 + 1 == i) {
						this.drawTexture(ac, ad, z + 45, 9 * ae, 9, 9);
					}
				}
			}

			Entity entity = playerEntity.getVehicle();
			if (entity == null) {
				this.client.profiler.swap("food");

				for (int af = 0; af < 10; af++) {
					int ag = n;
					int ah = 16;
					int ai = 0;
					if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
						ah += 36;
						ai = 13;
					}

					if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
						ag = n + (this.random.nextInt(3) - 1);
					}

					int aj = m - af * 8 - 9;
					this.drawTexture(aj, ag, 16 + ai * 9, 27, 9, 9);
					if (af * 2 + 1 < k) {
						this.drawTexture(aj, ag, ah + 36, 27, 9, 9);
					}

					if (af * 2 + 1 == k) {
						this.drawTexture(aj, ag, ah + 45, 27, 9, 9);
					}
				}
			}

			this.client.profiler.swap("air");
			if (playerEntity.isSubmergedIn(Material.WATER)) {
				int ak = this.client.player.getAir();
				int al = MathHelper.ceil((double)(ak - 2) * 10.0 / 300.0);
				int am = MathHelper.ceil((double)ak * 10.0 / 300.0) - al;

				for (int an = 0; an < al + am; an++) {
					if (an < al) {
						this.drawTexture(m - an * 8 - 9, s, 16, 18, 9, 9);
					} else {
						this.drawTexture(m - an * 8 - 9, s, 25, 18, 9, 9);
					}
				}
			}

			this.client.profiler.pop();
		}
	}

	private void method_12166(Window window) {
		if (this.client.getCameraEntity() instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
			Entity entity = playerEntity.getVehicle();
			if (entity instanceof LivingEntity) {
				this.client.profiler.swap("mountHealth");
				LivingEntity livingEntity = (LivingEntity)entity;
				int i = (int)Math.ceil((double)livingEntity.getHealth());
				float f = livingEntity.getMaxHealth();
				int j = (int)(f + 0.5F) / 2;
				if (j > 30) {
					j = 30;
				}

				int k = window.getHeight() - 39;
				int l = window.getWidth() / 2 + 91;
				int m = k;
				int n = 0;

				for (boolean bl = false; j > 0; n += 20) {
					int o = Math.min(j, 10);
					j -= o;

					for (int p = 0; p < o; p++) {
						int q = 52;
						int r = 0;
						int s = l - p * 8 - 9;
						this.drawTexture(s, m, 52 + r * 9, 9, 9, 9);
						if (p * 2 + 1 + n < i) {
							this.drawTexture(s, m, 88, 9, 9, 9);
						}

						if (p * 2 + 1 + n == i) {
							this.drawTexture(s, m, 97, 9, 9, 9);
						}
					}

					m -= 10;
				}
			}
		}
	}

	private void renderPumpkinBlur(Window window) {
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
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
		GlStateManager.method_12288(
			GlStateManager.class_2870.ZERO, GlStateManager.class_2866.ONE_MINUS_SRC_COLOR, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
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
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
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
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
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

	private void method_9422(int i, int j, float f, PlayerEntity playerEntity, @Nullable ItemStack itemStack) {
		if (itemStack != null) {
			float g = (float)itemStack.pickupTick - f;
			if (g > 0.0F) {
				GlStateManager.pushMatrix();
				float h = 1.0F + g / 5.0F;
				GlStateManager.translate((float)(i + 8), (float)(j + 12), 0.0F);
				GlStateManager.scale(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float)(-(i + 8)), (float)(-(j + 12)), 0.0F);
			}

			this.itemRenderer.method_10249(playerEntity, itemStack, i, j);
			if (g > 0.0F) {
				GlStateManager.popMatrix();
			}

			this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, itemStack, i, j);
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
		this.bossbar.method_12171();
	}

	public BossBarHud method_12167() {
		return this.bossbar;
	}
}
