package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.class_3252;
import net.minecraft.class_3253;
import net.minecraft.class_3254;
import net.minecraft.class_3255;
import net.minecraft.block.Blocks;
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
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
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
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ChatMessageType;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;

public class InGameHud extends DrawableHelper {
	private static final Identifier VIGNETTE = new Identifier("textures/misc/vignette.png");
	private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");
	private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
	private final Random random = new Random();
	private final MinecraftClient client;
	private final HeldItemRenderer field_20063;
	private final ChatHud chatHud;
	private int ticks;
	private String overlayMessage = "";
	private int overlayRemaining;
	private boolean overlayTinted;
	public float vignetteDarkness = 1.0F;
	private int heldItemTooltipFade;
	private ItemStack heldItem = ItemStack.EMPTY;
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
	private int field_20061;
	private int field_20062;
	private final Map<ChatMessageType, List<class_3252>> field_15886 = Maps.newHashMap();

	public InGameHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.field_20063 = minecraftClient.getHeldItemRenderer();
		this.debugHud = new DebugHud(minecraftClient);
		this.spectatorHud = new SpectatorHud(minecraftClient);
		this.chatHud = new ChatHud(minecraftClient);
		this.playerListHud = new PlayerListHud(minecraftClient, this);
		this.bossbar = new BossBarHud(minecraftClient);
		this.field_13302 = new class_2841(minecraftClient);

		for (ChatMessageType chatMessageType : ChatMessageType.values()) {
			this.field_15886.put(chatMessageType, Lists.newArrayList());
		}

		class_3252 lv = class_3253.field_15887;
		((List)this.field_15886.get(ChatMessageType.CHAT)).add(new class_3255(minecraftClient));
		((List)this.field_15886.get(ChatMessageType.CHAT)).add(lv);
		((List)this.field_15886.get(ChatMessageType.SYSTEM)).add(new class_3255(minecraftClient));
		((List)this.field_15886.get(ChatMessageType.SYSTEM)).add(lv);
		((List)this.field_15886.get(ChatMessageType.GAME_INFO)).add(new class_3254(minecraftClient));
		this.setDefaultTitleFade();
	}

	public void setDefaultTitleFade() {
		this.titleFadeInTicks = 10;
		this.titleRemainTicks = 70;
		this.titleFadeOutTicks = 20;
	}

	public void render(float tickDelta) {
		this.field_20061 = this.client.field_19944.method_18321();
		this.field_20062 = this.client.field_19944.method_18322();
		TextRenderer textRenderer = this.getFontRenderer();
		GlStateManager.enableBlend();
		if (MinecraftClient.isFancyGraphicsEnabled()) {
			this.method_18364(this.client.getCameraEntity());
		} else {
			GlStateManager.enableDepthTest();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
		}

		ItemStack itemStack = this.client.player.inventory.getArmor(3);
		if (this.client.options.perspective == 0 && itemStack.getItem() == Blocks.CARVED_PUMPKIN.getItem()) {
			this.method_18373();
		}

		if (!this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
			float f = this.client.player.lastTimeInPortal + (this.client.player.timeInPortal - this.client.player.lastTimeInPortal) * tickDelta;
			if (f > 0.0F) {
				this.method_9430(f);
			}
		}

		if (this.client.interactionManager.method_9667() == GameMode.SPECTATOR) {
			this.spectatorHud.method_9534(tickDelta);
		} else if (!this.client.options.field_19987) {
			this.method_9425(tickDelta);
		}

		if (!this.client.options.field_19987) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
			GlStateManager.enableBlend();
			GlStateManager.enableAlphaTest();
			this.method_18366(tickDelta);
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			this.client.profiler.push("bossHealth");
			this.bossbar.render();
			this.client.profiler.pop();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
			if (this.client.interactionManager.hasStatusBars()) {
				this.method_18371();
			}

			this.method_18372();
			GlStateManager.disableBlend();
			int i = this.field_20061 / 2 - 91;
			if (this.client.player.isRidingHorse()) {
				this.method_9426(i);
			} else if (this.client.interactionManager.hasExperienceBar()) {
				this.method_9432(i);
			}

			if (this.client.options.heldItemTooltips && this.client.interactionManager.method_9667() != GameMode.SPECTATOR) {
				this.method_18365();
			} else if (this.client.player.isSpectator()) {
				this.spectatorHud.method_18429();
			}
		}

		if (this.client.player.getSleepTimer() > 0) {
			this.client.profiler.push("sleep");
			GlStateManager.disableDepthTest();
			GlStateManager.disableAlphaTest();
			float g = (float)this.client.player.getSleepTimer();
			float h = g / 100.0F;
			if (h > 1.0F) {
				h = 1.0F - (g - 100.0F) / 10.0F;
			}

			int j = (int)(220.0F * h) << 24 | 1052704;
			fill(0, 0, this.field_20061, this.field_20062, j);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableDepthTest();
			this.client.profiler.pop();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.client.isDemo()) {
			this.method_18368();
		}

		this.method_18363();
		if (this.client.options.debugEnabled) {
			this.debugHud.method_18382();
		}

		if (!this.client.options.field_19987) {
			if (this.overlayRemaining > 0) {
				this.client.profiler.push("overlayMessage");
				float k = (float)this.overlayRemaining - tickDelta;
				int l = (int)(k * 255.0F / 20.0F);
				if (l > 255) {
					l = 255;
				}

				if (l > 8) {
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)(this.field_20061 / 2), (float)(this.field_20062 - 68), 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.method_12288(
						GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
					);
					int m = 16777215;
					if (this.overlayTinted) {
						m = MathHelper.hsvToRgb(k / 50.0F, 0.7F, 0.6F) & 16777215;
					}

					textRenderer.method_18355(this.overlayMessage, (float)(-textRenderer.getStringWidth(this.overlayMessage) / 2), -4.0F, m + (l << 24 & 0xFF000000));
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}

				this.client.profiler.pop();
			}

			if (this.titleTotalTicks > 0) {
				this.client.profiler.push("titleAndSubtitle");
				float n = (float)this.titleTotalTicks - tickDelta;
				int o = 255;
				if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
					float p = (float)(this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks) - n;
					o = (int)(p * 255.0F / (float)this.titleFadeInTicks);
				}

				if (this.titleTotalTicks <= this.titleFadeOutTicks) {
					o = (int)(n * 255.0F / (float)this.titleFadeOutTicks);
				}

				o = MathHelper.clamp(o, 0, 255);
				if (o > 8) {
					GlStateManager.pushMatrix();
					GlStateManager.translate((float)(this.field_20061 / 2), (float)(this.field_20062 / 2), 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.method_12288(
						GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
					);
					GlStateManager.pushMatrix();
					GlStateManager.scale(4.0F, 4.0F, 4.0F);
					int q = o << 24 & 0xFF000000;
					textRenderer.drawWithShadow(this.subtitle, (float)(-textRenderer.getStringWidth(this.subtitle) / 2), -10.0F, 16777215 | q);
					GlStateManager.popMatrix();
					GlStateManager.pushMatrix();
					GlStateManager.scale(2.0F, 2.0F, 2.0F);
					textRenderer.drawWithShadow(this.title, (float)(-textRenderer.getStringWidth(this.title) / 2), 5.0F, 16777215 | q);
					GlStateManager.popMatrix();
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}

				this.client.profiler.pop();
			}

			this.field_13302.method_18418();
			Scoreboard scoreboard = this.client.world.getScoreboard();
			ScoreboardObjective scoreboardObjective = null;
			Team team = scoreboard.getPlayerTeam(this.client.player.method_15586());
			if (team != null) {
				int r = team.method_12130().getColorIndex();
				if (r >= 0) {
					scoreboardObjective = scoreboard.getObjectiveForSlot(3 + r);
				}
			}

			ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
			if (scoreboardObjective2 != null) {
				this.method_18361(scoreboardObjective2);
			}

			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			GlStateManager.disableAlphaTest();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, (float)(this.field_20062 - 48), 0.0F);
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
				this.playerListHud.render(this.field_20061, scoreboard, scoreboardObjective2);
			}
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableAlphaTest();
	}

	private void method_18366(float f) {
		GameOptions gameOptions = this.client.options;
		if (gameOptions.perspective == 0) {
			if (this.client.interactionManager.method_9667() == GameMode.SPECTATOR && this.client.targetedEntity == null) {
				BlockHitResult blockHitResult = this.client.result;
				if (blockHitResult == null || blockHitResult.type != BlockHitResult.Type.BLOCK) {
					return;
				}

				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!this.client.world.getBlockState(blockPos).getBlock().hasBlockEntity() || !(this.client.world.getBlockEntity(blockPos) instanceof Inventory)) {
					return;
				}
			}

			if (gameOptions.debugEnabled && !gameOptions.field_19987 && !this.client.player.getReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)(this.field_20061 / 2), (float)(this.field_20062 / 2), this.zOffset);
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
				int i = 15;
				this.drawTexture((float)this.field_20061 / 2.0F - 7.5F, (float)this.field_20062 / 2.0F - 7.5F, 0, 0, 15, 15);
				if (this.client.options.field_13290 == 1) {
					float g = this.client.player.method_13275(0.0F);
					boolean bl = false;
					if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && g >= 1.0F) {
						bl = this.client.player.method_13268() > 5.0F;
						bl &= this.client.targetedEntity.isAlive();
					}

					int j = this.field_20062 / 2 - 7 + 16;
					int k = this.field_20061 / 2 - 8;
					if (bl) {
						this.drawTexture(k, j, 68, 94, 16, 16);
					} else if (g < 1.0F) {
						int l = (int)(g * 17.0F);
						this.drawTexture(k, j, 36, 94, 16, 4);
						this.drawTexture(k, j, 52, 94, l, 4);
					}
				}
			}
		}
	}

	protected void method_18363() {
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffectInstances();
		if (!collection.isEmpty()) {
			this.client.getTextureManager().bindTexture(HandledScreen.INVENTORY_TEXTURE);
			GlStateManager.enableBlend();
			int i = 0;
			int j = 0;

			for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getStatusEffect();
				if (statusEffect.hasIcon() && statusEffectInstance.method_15552()) {
					int k = this.field_20061;
					int l = 1;
					if (this.client.isDemo()) {
						l += 15;
					}

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
					int o = m % 12;
					int p = m / 12;
					this.drawTexture(k + 3, l + 3, o * 18, 198 + p * 18, 18, 18);
				}
			}
		}
	}

	protected void method_9425(float f) {
		PlayerEntity playerEntity = this.method_18369();
		if (playerEntity != null) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(WIDGETS);
			ItemStack itemStack = playerEntity.getOffHandStack();
			HandOption handOption = playerEntity.getDurability().method_13037();
			int i = this.field_20061 / 2;
			float g = this.zOffset;
			int j = 182;
			int k = 91;
			this.zOffset = -90.0F;
			this.drawTexture(i - 91, this.field_20062 - 22, 0, 0, 182, 22);
			this.drawTexture(i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, this.field_20062 - 22 - 1, 0, 22, 24, 22);
			if (!itemStack.isEmpty()) {
				if (handOption == HandOption.LEFT) {
					this.drawTexture(i - 91 - 29, this.field_20062 - 23, 24, 22, 29, 24);
				} else {
					this.drawTexture(i + 91, this.field_20062 - 23, 53, 22, 29, 24);
				}
			}

			this.zOffset = g;
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			DiffuseLighting.enable();

			for (int l = 0; l < 9; l++) {
				int m = i - 90 + l * 20 + 2;
				int n = this.field_20062 - 16 - 3;
				this.method_9422(m, n, f, playerEntity, playerEntity.inventory.field_15082.get(l));
			}

			if (!itemStack.isEmpty()) {
				int o = this.field_20062 - 16 - 3;
				if (handOption == HandOption.LEFT) {
					this.method_9422(i - 91 - 26, o, f, playerEntity, itemStack);
				} else {
					this.method_9422(i + 91 + 10, o, f, playerEntity, itemStack);
				}
			}

			if (this.client.options.field_13290 == 2) {
				float h = this.client.player.method_13275(0.0F);
				if (h < 1.0F) {
					int p = this.field_20062 - 20;
					int q = i + 91 + 6;
					if (handOption == HandOption.RIGHT) {
						q = i - 91 - 22;
					}

					this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
					int r = (int)(h * 19.0F);
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

	public void method_9426(int i) {
		this.client.profiler.push("jumpBar");
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		float f = this.client.player.getMountJumpStrength();
		int j = 182;
		int k = (int)(f * 183.0F);
		int l = this.field_20062 - 32 + 3;
		this.drawTexture(i, l, 0, 84, 182, 5);
		if (k > 0) {
			this.drawTexture(i, l, 0, 89, k, 5);
		}

		this.client.profiler.pop();
	}

	public void method_9432(int i) {
		this.client.profiler.push("expBar");
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
		int j = this.client.player.getNextLevelExperience();
		if (j > 0) {
			int k = 182;
			int l = (int)(this.client.player.experienceProgress * 183.0F);
			int m = this.field_20062 - 32 + 3;
			this.drawTexture(i, m, 0, 64, 182, 5);
			if (l > 0) {
				this.drawTexture(i, m, 0, 69, l, 5);
			}
		}

		this.client.profiler.pop();
		if (this.client.player.experienceLevel > 0) {
			this.client.profiler.push("expLevel");
			String string = "" + this.client.player.experienceLevel;
			int n = (this.field_20061 - this.getFontRenderer().getStringWidth(string)) / 2;
			int o = this.field_20062 - 31 - 4;
			this.getFontRenderer().method_18355(string, (float)(n + 1), (float)o, 0);
			this.getFontRenderer().method_18355(string, (float)(n - 1), (float)o, 0);
			this.getFontRenderer().method_18355(string, (float)n, (float)(o + 1), 0);
			this.getFontRenderer().method_18355(string, (float)n, (float)(o - 1), 0);
			this.getFontRenderer().method_18355(string, (float)n, (float)o, 8453920);
			this.client.profiler.pop();
		}
	}

	public void method_18365() {
		this.client.profiler.push("selectedItemName");
		if (this.heldItemTooltipFade > 0 && !this.heldItem.isEmpty()) {
			Text text = new LiteralText("").append(this.heldItem.getName()).formatted(this.heldItem.getRarity().formatting);
			if (this.heldItem.hasCustomName()) {
				text.formatted(Formatting.ITALIC);
			}

			String string = text.asFormattedString();
			int i = (this.field_20061 - this.getFontRenderer().getStringWidth(string)) / 2;
			int j = this.field_20062 - 59;
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

	public void method_18368() {
		this.client.profiler.push("demo");
		String string;
		if (this.client.world.getLastUpdateTime() >= 120500L) {
			string = I18n.translate("demo.demoExpired");
		} else {
			string = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getLastUpdateTime())));
		}

		int i = this.getFontRenderer().getStringWidth(string);
		this.getFontRenderer().drawWithShadow(string, (float)(this.field_20061 - i - 10), 5.0F, 16777215);
		this.client.profiler.pop();
	}

	private void method_18361(ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = scoreboardObjective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(scoreboardObjective);
		List<ScoreboardPlayerScore> list = (List<ScoreboardPlayerScore>)collection.stream()
			.filter(scoreboardPlayerScore -> scoreboardPlayerScore.getPlayerName() != null && !scoreboardPlayerScore.getPlayerName().startsWith("#"))
			.collect(Collectors.toList());
		if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}

		String string = scoreboardObjective.method_4849().asFormattedString();
		int i = this.getFontRenderer().getStringWidth(string);
		int j = i;

		for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			String string2 = Team.method_18097(team, new LiteralText(scoreboardPlayerScore.getPlayerName())).asFormattedString()
				+ ": "
				+ Formatting.RED
				+ scoreboardPlayerScore.getScore();
			j = Math.max(j, this.getFontRenderer().getStringWidth(string2));
		}

		int k = collection.size() * this.getFontRenderer().fontHeight;
		int l = this.field_20062 / 2 + k / 3;
		int m = 3;
		int n = this.field_20061 - j - 3;
		int o = 0;

		for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
			o++;
			Team team2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
			String string3 = Team.method_18097(team2, new LiteralText(scoreboardPlayerScore2.getPlayerName())).asFormattedString();
			String string4 = Formatting.RED + "" + scoreboardPlayerScore2.getScore();
			int q = l - o * this.getFontRenderer().fontHeight;
			int r = this.field_20061 - 3 + 2;
			fill(n - 2, q, r, q + this.getFontRenderer().fontHeight, 1342177280);
			this.getFontRenderer().method_18355(string3, (float)n, (float)q, 553648127);
			this.getFontRenderer().method_18355(string4, (float)(r - this.getFontRenderer().getStringWidth(string4)), (float)q, 553648127);
			if (o == collection.size()) {
				fill(n - 2, q - this.getFontRenderer().fontHeight - 1, r, q - 1, 1610612736);
				fill(n - 2, q - 1, r, q, 1342177280);
				this.getFontRenderer().method_18355(string, (float)(n + j / 2 - i / 2), (float)(q - this.getFontRenderer().fontHeight), 553648127);
			}
		}
	}

	private PlayerEntity method_18369() {
		return !(this.client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.client.getCameraEntity();
	}

	private LivingEntity method_18370() {
		PlayerEntity playerEntity = this.method_18369();
		if (playerEntity != null) {
			Entity entity = playerEntity.getVehicle();
			if (entity == null) {
				return null;
			}

			if (entity instanceof LivingEntity) {
				return (LivingEntity)entity;
			}
		}

		return null;
	}

	private int method_18360(LivingEntity livingEntity) {
		if (livingEntity != null && livingEntity.method_15569()) {
			float f = livingEntity.getMaxHealth();
			int i = (int)(f + 0.5F) / 2;
			if (i > 30) {
				i = 30;
			}

			return i;
		} else {
			return 0;
		}
	}

	private int method_18367(int i) {
		return (int)Math.ceil((double)i / 10.0);
	}

	private void method_18371() {
		PlayerEntity playerEntity = this.method_18369();
		if (playerEntity != null) {
			int i = MathHelper.ceil(playerEntity.getHealth());
			boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
			long l = Util.method_20227();
			if (i < this.renderHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = l;
				this.heartJumpEndTick = (long)(this.ticks + 20);
			} else if (i > this.renderHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = l;
				this.heartJumpEndTick = (long)(this.ticks + 10);
			}

			if (l - this.lastHealthCheckTime > 1000L) {
				this.renderHealthValue = i;
				this.lastHealthValue = i;
				this.lastHealthCheckTime = l;
			}

			this.renderHealthValue = i;
			int j = this.lastHealthValue;
			this.random.setSeed((long)(this.ticks * 312871));
			HungerManager hungerManager = playerEntity.getHungerManager();
			int k = hungerManager.getFoodLevel();
			EntityAttributeInstance entityAttributeInstance = playerEntity.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH);
			int m = this.field_20061 / 2 - 91;
			int n = this.field_20061 / 2 + 91;
			int o = this.field_20062 - 39;
			float f = (float)entityAttributeInstance.getValue();
			int p = MathHelper.ceil(playerEntity.getAbsorption());
			int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
			int r = Math.max(10 - (q - 2), 3);
			int s = o - (q - 1) * r - 10;
			int t = o - 10;
			int u = p;
			int v = playerEntity.getArmorProtectionValue();
			int w = -1;
			if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
				w = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.profiler.push("armor");

			for (int x = 0; x < 10; x++) {
				if (v > 0) {
					int y = m + x * 8;
					if (x * 2 + 1 < v) {
						this.drawTexture(y, s, 34, 9, 9, 9);
					}

					if (x * 2 + 1 == v) {
						this.drawTexture(y, s, 25, 9, 9, 9);
					}

					if (x * 2 + 1 > v) {
						this.drawTexture(y, s, 16, 9, 9, 9);
					}
				}
			}

			this.client.profiler.swap("health");

			for (int z = MathHelper.ceil((f + (float)p) / 2.0F) - 1; z >= 0; z--) {
				int aa = 16;
				if (playerEntity.hasStatusEffect(StatusEffects.POISON)) {
					aa += 36;
				} else if (playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
					aa += 72;
				}

				int ab = 0;
				if (bl) {
					ab = 1;
				}

				int ac = MathHelper.ceil((float)(z + 1) / 10.0F) - 1;
				int ad = m + z % 10 * 8;
				int ae = o - ac * r;
				if (i <= 4) {
					ae += this.random.nextInt(2);
				}

				if (u <= 0 && z == w) {
					ae -= 2;
				}

				int af = 0;
				if (playerEntity.world.method_3588().isHardcore()) {
					af = 5;
				}

				this.drawTexture(ad, ae, 16 + ab * 9, 9 * af, 9, 9);
				if (bl) {
					if (z * 2 + 1 < j) {
						this.drawTexture(ad, ae, aa + 54, 9 * af, 9, 9);
					}

					if (z * 2 + 1 == j) {
						this.drawTexture(ad, ae, aa + 63, 9 * af, 9, 9);
					}
				}

				if (u > 0) {
					if (u == p && p % 2 == 1) {
						this.drawTexture(ad, ae, aa + 153, 9 * af, 9, 9);
						u--;
					} else {
						this.drawTexture(ad, ae, aa + 144, 9 * af, 9, 9);
						u -= 2;
					}
				} else {
					if (z * 2 + 1 < i) {
						this.drawTexture(ad, ae, aa + 36, 9 * af, 9, 9);
					}

					if (z * 2 + 1 == i) {
						this.drawTexture(ad, ae, aa + 45, 9 * af, 9, 9);
					}
				}
			}

			LivingEntity livingEntity = this.method_18370();
			int ag = this.method_18360(livingEntity);
			if (ag == 0) {
				this.client.profiler.swap("food");

				for (int ah = 0; ah < 10; ah++) {
					int ai = o;
					int aj = 16;
					int ak = 0;
					if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
						aj += 36;
						ak = 13;
					}

					if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
						ai = o + (this.random.nextInt(3) - 1);
					}

					int al = n - ah * 8 - 9;
					this.drawTexture(al, ai, 16 + ak * 9, 27, 9, 9);
					if (ah * 2 + 1 < k) {
						this.drawTexture(al, ai, aj + 36, 27, 9, 9);
					}

					if (ah * 2 + 1 == k) {
						this.drawTexture(al, ai, aj + 45, 27, 9, 9);
					}
				}

				t -= 10;
			}

			this.client.profiler.swap("air");
			int am = playerEntity.getAir();
			int an = playerEntity.method_15585();
			if (playerEntity.method_15567(FluidTags.WATER) || am < an) {
				int ao = this.method_18367(ag) - 1;
				t -= ao * 10;
				int ap = MathHelper.ceil((double)(am - 2) * 10.0 / (double)an);
				int aq = MathHelper.ceil((double)am * 10.0 / (double)an) - ap;

				for (int ar = 0; ar < ap + aq; ar++) {
					if (ar < ap) {
						this.drawTexture(n - ar * 8 - 9, t, 16, 18, 9, 9);
					} else {
						this.drawTexture(n - ar * 8 - 9, t, 25, 18, 9, 9);
					}
				}
			}

			this.client.profiler.pop();
		}
	}

	private void method_18372() {
		LivingEntity livingEntity = this.method_18370();
		if (livingEntity != null) {
			int i = this.method_18360(livingEntity);
			if (i != 0) {
				int j = (int)Math.ceil((double)livingEntity.getHealth());
				this.client.profiler.swap("mountHealth");
				int k = this.field_20062 - 39;
				int l = this.field_20061 / 2 + 91;
				int m = k;
				int n = 0;

				for (boolean bl = false; i > 0; n += 20) {
					int o = Math.min(i, 10);
					i -= o;

					for (int p = 0; p < o; p++) {
						int q = 52;
						int r = 0;
						int s = l - p * 8 - 9;
						this.drawTexture(s, m, 52 + r * 9, 9, 9, 9);
						if (p * 2 + 1 + n < j) {
							this.drawTexture(s, m, 88, 9, 9, 9);
						}

						if (p * 2 + 1 + n == j) {
							this.drawTexture(s, m, 97, 9, 9, 9);
						}
					}

					m -= 10;
				}
			}
		}
	}

	private void method_18373() {
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
		bufferBuilder.vertex(0.0, (double)this.field_20062, -90.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex((double)this.field_20061, (double)this.field_20062, -90.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex((double)this.field_20061, 0.0, -90.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_18359(Entity entity) {
		if (entity != null) {
			float f = MathHelper.clamp(1.0F - entity.getBrightnessAtEyes(), 0.0F, 1.0F);
			this.vignetteDarkness = (float)((double)this.vignetteDarkness + (double)(f - this.vignetteDarkness) * 0.01);
		}
	}

	private void method_18364(Entity entity) {
		WorldBorder worldBorder = this.client.world.method_8524();
		float f = (float)worldBorder.getDistanceInsideBorder(entity);
		double d = Math.min(
			worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getOldSize())
		);
		double e = Math.max((double)worldBorder.getWarningBlocks(), d);
		if ((double)f < e) {
			f = 1.0F - (float)((double)f / e);
		} else {
			f = 0.0F;
		}

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
		bufferBuilder.vertex(0.0, (double)this.field_20062, -90.0).texture(0.0, 1.0).next();
		bufferBuilder.vertex((double)this.field_20061, (double)this.field_20062, -90.0).texture(1.0, 1.0).next();
		bufferBuilder.vertex((double)this.field_20061, 0.0, -90.0).texture(1.0, 0.0).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0, 0.0).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
	}

	private void method_9430(float f) {
		if (f < 1.0F) {
			f *= f;
			f *= f;
			f = f * 0.8F + 0.2F;
		}

		GlStateManager.disableAlphaTest();
		GlStateManager.disableDepthTest();
		GlStateManager.depthMask(false);
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		GlStateManager.color(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Sprite sprite = this.client.getBlockRenderManager().getModels().getParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
		float g = sprite.getMinU();
		float h = sprite.getMinV();
		float i = sprite.getMaxU();
		float j = sprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.field_20062, -90.0).texture((double)g, (double)j).next();
		bufferBuilder.vertex((double)this.field_20061, (double)this.field_20062, -90.0).texture((double)i, (double)j).next();
		bufferBuilder.vertex((double)this.field_20061, 0.0, -90.0).texture((double)i, (double)h).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture((double)g, (double)h).next();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepthTest();
		GlStateManager.enableAlphaTest();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void method_9422(int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			float g = (float)itemStack.getPickupTick() - f;
			if (g > 0.0F) {
				GlStateManager.pushMatrix();
				float h = 1.0F + g / 5.0F;
				GlStateManager.translate((float)(i + 8), (float)(j + 12), 0.0F);
				GlStateManager.scale(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				GlStateManager.translate((float)(-(i + 8)), (float)(-(j + 12)), 0.0F);
			}

			this.field_20063.method_19374(playerEntity, itemStack, i, j);
			if (g > 0.0F) {
				GlStateManager.popMatrix();
			}

			this.field_20063.method_19383(this.client.textRenderer, itemStack, i, j);
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
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			this.method_18359(entity);
		}

		if (this.client.player != null) {
			ItemStack itemStack = this.client.player.inventory.getMainHandStack();
			if (itemStack.isEmpty()) {
				this.heldItemTooltipFade = 0;
			} else if (this.heldItem.isEmpty() || itemStack.getItem() != this.heldItem.getItem() || !itemStack.getName().equals(this.heldItem.getName())) {
				this.heldItemTooltipFade = 40;
			} else if (this.heldItemTooltipFade > 0) {
				this.heldItemTooltipFade--;
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
		this.setOverlayMessage(text.getString(), tinted);
	}

	public void method_14471(ChatMessageType chatMessageType, Text text) {
		for (class_3252 lv : (List)this.field_15886.get(chatMessageType)) {
			lv.method_14472(chatMessageType, text);
		}
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
		this.client.method_14462().method_14489();
	}

	public BossBarHud method_12167() {
		return this.bossbar;
	}
}
