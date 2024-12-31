package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.GameInfoChatListener;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class InGameHud extends DrawableHelper {
	private static final Identifier VIGNETTE_TEX = new Identifier("textures/misc/vignette.png");
	private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
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
	private ItemStack currentStack = ItemStack.EMPTY;
	private final DebugHud debugHud;
	private final SubtitlesHud subtitlesHud;
	private final SpectatorHud spectatorHud;
	private final PlayerListHud playerListHud;
	private final BossBarHud bossBarHud;
	private int titleTotalTicks;
	private String title = "";
	private String subtitle = "";
	private int titleFadeInTicks;
	private int titleRemainTicks;
	private int titleFadeOutTicks;
	private int lastHealthValue;
	private int renderHealthValue;
	private long lastHealthCheckTime;
	private long heartJumpEndTick;
	private int scaledWidth;
	private int scaledHeight;
	private final Map<MessageType, List<ClientChatListener>> listeners = Maps.newHashMap();

	public InGameHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.itemRenderer = minecraftClient.getItemRenderer();
		this.debugHud = new DebugHud(minecraftClient);
		this.spectatorHud = new SpectatorHud(minecraftClient);
		this.chatHud = new ChatHud(minecraftClient);
		this.playerListHud = new PlayerListHud(minecraftClient, this);
		this.bossBarHud = new BossBarHud(minecraftClient);
		this.subtitlesHud = new SubtitlesHud(minecraftClient);

		for (MessageType messageType : MessageType.values()) {
			this.listeners.put(messageType, Lists.newArrayList());
		}

		ClientChatListener clientChatListener = NarratorManager.INSTANCE;
		((List)this.listeners.get(MessageType.field_11737)).add(new ChatListenerHud(minecraftClient));
		((List)this.listeners.get(MessageType.field_11737)).add(clientChatListener);
		((List)this.listeners.get(MessageType.field_11735)).add(new ChatListenerHud(minecraftClient));
		((List)this.listeners.get(MessageType.field_11735)).add(clientChatListener);
		((List)this.listeners.get(MessageType.field_11733)).add(new GameInfoChatListener(minecraftClient));
		this.setDefaultTitleFade();
	}

	public void setDefaultTitleFade() {
		this.titleFadeInTicks = 10;
		this.titleRemainTicks = 70;
		this.titleFadeOutTicks = 20;
	}

	public void render(float f) {
		this.scaledWidth = this.client.getWindow().getScaledWidth();
		this.scaledHeight = this.client.getWindow().getScaledHeight();
		TextRenderer textRenderer = this.getFontRenderer();
		RenderSystem.enableBlend();
		if (MinecraftClient.isFancyGraphicsEnabled()) {
			this.renderVignetteOverlay(this.client.getCameraEntity());
		} else {
			RenderSystem.enableDepthTest();
			RenderSystem.defaultBlendFunc();
		}

		ItemStack itemStack = this.client.player.inventory.getArmorStack(3);
		if (this.client.options.perspective == 0 && itemStack.getItem() == Blocks.field_10147.asItem()) {
			this.renderPumpkinOverlay();
		}

		if (!this.client.player.hasStatusEffect(StatusEffects.field_5916)) {
			float g = MathHelper.lerp(f, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
			if (g > 0.0F) {
				this.renderPortalOverlay(g);
			}
		}

		if (this.client.interactionManager.getCurrentGameMode() == GameMode.field_9219) {
			this.spectatorHud.render(f);
		} else if (!this.client.options.hudHidden) {
			this.renderHotbar(f);
		}

		if (!this.client.options.hudHidden) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			this.renderCrosshair();
			RenderSystem.defaultBlendFunc();
			this.client.getProfiler().push("bossHealth");
			this.bossBarHud.render();
			this.client.getProfiler().pop();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
			if (this.client.interactionManager.hasStatusBars()) {
				this.renderStatusBars();
			}

			this.renderMountHealth();
			RenderSystem.disableBlend();
			int i = this.scaledWidth / 2 - 91;
			if (this.client.player.hasJumpingMount()) {
				this.renderMountJumpBar(i);
			} else if (this.client.interactionManager.hasExperienceBar()) {
				this.renderExperienceBar(i);
			}

			if (this.client.options.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.field_9219) {
				this.renderHeldItemTooltip();
			} else if (this.client.player.isSpectator()) {
				this.spectatorHud.render();
			}
		}

		if (this.client.player.getSleepTimer() > 0) {
			this.client.getProfiler().push("sleep");
			RenderSystem.disableDepthTest();
			RenderSystem.disableAlphaTest();
			float h = (float)this.client.player.getSleepTimer();
			float j = h / 100.0F;
			if (j > 1.0F) {
				j = 1.0F - (h - 100.0F) / 10.0F;
			}

			int k = (int)(220.0F * j) << 24 | 1052704;
			fill(0, 0, this.scaledWidth, this.scaledHeight, k);
			RenderSystem.enableAlphaTest();
			RenderSystem.enableDepthTest();
			this.client.getProfiler().pop();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.client.isDemo()) {
			this.renderDemoTimer();
		}

		this.renderStatusEffectOverlay();
		if (this.client.options.debugEnabled) {
			this.debugHud.render();
		}

		if (!this.client.options.hudHidden) {
			if (this.overlayRemaining > 0) {
				this.client.getProfiler().push("overlayMessage");
				float l = (float)this.overlayRemaining - f;
				int m = (int)(l * 255.0F / 20.0F);
				if (m > 255) {
					m = 255;
				}

				if (m > 8) {
					RenderSystem.pushMatrix();
					RenderSystem.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight - 68), 0.0F);
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					int n = 16777215;
					if (this.overlayTinted) {
						n = MathHelper.hsvToRgb(l / 50.0F, 0.7F, 0.6F) & 16777215;
					}

					int o = m << 24 & 0xFF000000;
					this.drawTextBackground(textRenderer, -4, textRenderer.getStringWidth(this.overlayMessage));
					textRenderer.draw(this.overlayMessage, (float)(-textRenderer.getStringWidth(this.overlayMessage) / 2), -4.0F, n | o);
					RenderSystem.disableBlend();
					RenderSystem.popMatrix();
				}

				this.client.getProfiler().pop();
			}

			if (this.titleTotalTicks > 0) {
				this.client.getProfiler().push("titleAndSubtitle");
				float p = (float)this.titleTotalTicks - f;
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
					RenderSystem.pushMatrix();
					RenderSystem.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), 0.0F);
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					RenderSystem.pushMatrix();
					RenderSystem.scalef(4.0F, 4.0F, 4.0F);
					int s = q << 24 & 0xFF000000;
					int t = textRenderer.getStringWidth(this.title);
					this.drawTextBackground(textRenderer, -10, t);
					textRenderer.drawWithShadow(this.title, (float)(-t / 2), -10.0F, 16777215 | s);
					RenderSystem.popMatrix();
					if (!this.subtitle.isEmpty()) {
						RenderSystem.pushMatrix();
						RenderSystem.scalef(2.0F, 2.0F, 2.0F);
						int u = textRenderer.getStringWidth(this.subtitle);
						this.drawTextBackground(textRenderer, 5, u);
						textRenderer.drawWithShadow(this.subtitle, (float)(-u / 2), 5.0F, 16777215 | s);
						RenderSystem.popMatrix();
					}

					RenderSystem.disableBlend();
					RenderSystem.popMatrix();
				}

				this.client.getProfiler().pop();
			}

			this.subtitlesHud.render();
			Scoreboard scoreboard = this.client.world.getScoreboard();
			ScoreboardObjective scoreboardObjective = null;
			Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
			if (team != null) {
				int v = team.getColor().getColorIndex();
				if (v >= 0) {
					scoreboardObjective = scoreboard.getObjectiveForSlot(3 + v);
				}
			}

			ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
			if (scoreboardObjective2 != null) {
				this.renderScoreboardSidebar(scoreboardObjective2);
			}

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableAlphaTest();
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, (float)(this.scaledHeight - 48), 0.0F);
			this.client.getProfiler().push("chat");
			this.chatHud.render(this.ticks);
			this.client.getProfiler().pop();
			RenderSystem.popMatrix();
			scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
			if (!this.client.options.keyPlayerList.isPressed()
				|| this.client.isInSingleplayer() && this.client.player.networkHandler.getPlayerList().size() <= 1 && scoreboardObjective2 == null) {
				this.playerListHud.tick(false);
			} else {
				this.playerListHud.tick(true);
				this.playerListHud.render(this.scaledWidth, scoreboard, scoreboardObjective2);
			}
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableAlphaTest();
	}

	private void drawTextBackground(TextRenderer textRenderer, int i, int j) {
		int k = this.client.options.getTextBackgroundColor(0.0F);
		if (k != 0) {
			int l = -j / 2;
			fill(l - 2, i - 2, l + j + 2, i + 9 + 2, k);
		}
	}

	private void renderCrosshair() {
		GameOptions gameOptions = this.client.options;
		if (gameOptions.perspective == 0) {
			if (this.client.interactionManager.getCurrentGameMode() != GameMode.field_9219 || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
				if (gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.getReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
					RenderSystem.pushMatrix();
					RenderSystem.translatef((float)(this.scaledWidth / 2), (float)(this.scaledHeight / 2), (float)this.getBlitOffset());
					Camera camera = this.client.gameRenderer.getCamera();
					RenderSystem.rotatef(camera.getPitch(), -1.0F, 0.0F, 0.0F);
					RenderSystem.rotatef(camera.getYaw(), 0.0F, 1.0F, 0.0F);
					RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
					RenderSystem.renderCrosshair(10);
					RenderSystem.popMatrix();
				} else {
					RenderSystem.blendFuncSeparate(
						GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
					);
					int i = 15;
					this.blit((this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
					if (this.client.options.attackIndicator == AttackIndicator.field_18152) {
						float f = this.client.player.getAttackCooldownProgress(0.0F);
						boolean bl = false;
						if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0F) {
							bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
							bl &= this.client.targetedEntity.isAlive();
						}

						int j = this.scaledHeight / 2 - 7 + 16;
						int k = this.scaledWidth / 2 - 8;
						if (bl) {
							this.blit(k, j, 68, 94, 16, 16);
						} else if (f < 1.0F) {
							int l = (int)(f * 17.0F);
							this.blit(k, j, 36, 94, 16, 4);
							this.blit(k, j, 52, 94, l, 4);
						}
					}
				}
			}
		}
	}

	private boolean shouldRenderSpectatorCrosshair(HitResult hitResult) {
		if (hitResult == null) {
			return false;
		} else if (hitResult.getType() == HitResult.Type.field_1331) {
			return ((EntityHitResult)hitResult).getEntity() instanceof NameableContainerFactory;
		} else if (hitResult.getType() == HitResult.Type.field_1332) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			World world = this.client.world;
			return world.getBlockState(blockPos).createContainerFactory(world, blockPos) != null;
		} else {
			return false;
		}
	}

	protected void renderStatusEffectOverlay() {
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty()) {
			RenderSystem.enableBlend();
			int i = 0;
			int j = 0;
			StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
			List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
			this.client.getTextureManager().bindTexture(ContainerScreen.BACKGROUND_TEXTURE);

			for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				if (statusEffectInstance.shouldShowIcon()) {
					int k = this.scaledWidth;
					int l = 1;
					if (this.client.isDemo()) {
						l += 15;
					}

					if (statusEffect.isBeneficial()) {
						i++;
						k -= 25 * i;
					} else {
						j++;
						k -= 25 * j;
						l += 26;
					}

					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					float f = 1.0F;
					if (statusEffectInstance.isAmbient()) {
						this.blit(k, l, 165, 166, 24, 24);
					} else {
						this.blit(k, l, 141, 166, 24, 24);
						if (statusEffectInstance.getDuration() <= 200) {
							int m = 10 - statusEffectInstance.getDuration() / 20;
							f = MathHelper.clamp((float)statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
								+ MathHelper.cos((float)statusEffectInstance.getDuration() * (float) Math.PI / 5.0F) * MathHelper.clamp((float)m / 10.0F * 0.25F, 0.0F, 0.25F);
						}
					}

					Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
					int n = k;
					int o = l;
					float g = f;
					list.add((Runnable)() -> {
						this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, g);
						blit(n + 3, o + 3, this.getBlitOffset(), 18, 18, sprite);
					});
				}
			}

			list.forEach(Runnable::run);
		}
	}

	protected void renderHotbar(float f) {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(WIDGETS_TEX);
			ItemStack itemStack = playerEntity.getOffHandStack();
			Arm arm = playerEntity.getMainArm().getOpposite();
			int i = this.scaledWidth / 2;
			int j = this.getBlitOffset();
			int k = 182;
			int l = 91;
			this.setBlitOffset(-90);
			this.blit(i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
			this.blit(i - 91 - 1 + playerEntity.inventory.selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
			if (!itemStack.isEmpty()) {
				if (arm == Arm.field_6182) {
					this.blit(i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
				} else {
					this.blit(i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
				}
			}

			this.setBlitOffset(j);
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();

			for (int m = 0; m < 9; m++) {
				int n = i - 90 + m * 20 + 2;
				int o = this.scaledHeight - 16 - 3;
				this.renderHotbarItem(n, o, f, playerEntity, playerEntity.inventory.main.get(m));
			}

			if (!itemStack.isEmpty()) {
				int p = this.scaledHeight - 16 - 3;
				if (arm == Arm.field_6182) {
					this.renderHotbarItem(i - 91 - 26, p, f, playerEntity, itemStack);
				} else {
					this.renderHotbarItem(i + 91 + 10, p, f, playerEntity, itemStack);
				}
			}

			if (this.client.options.attackIndicator == AttackIndicator.field_18153) {
				float g = this.client.player.getAttackCooldownProgress(0.0F);
				if (g < 1.0F) {
					int q = this.scaledHeight - 20;
					int r = i + 91 + 6;
					if (arm == Arm.field_6183) {
						r = i - 91 - 22;
					}

					this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
					int s = (int)(g * 19.0F);
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					this.blit(r, q, 0, 94, 18, 18);
					this.blit(r, q + 18 - s, 18, 112 - s, 18, s);
				}
			}

			RenderSystem.disableRescaleNormal();
			RenderSystem.disableBlend();
		}
	}

	public void renderMountJumpBar(int i) {
		this.client.getProfiler().push("jumpBar");
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
		float f = this.client.player.method_3151();
		int j = 182;
		int k = (int)(f * 183.0F);
		int l = this.scaledHeight - 32 + 3;
		this.blit(i, l, 0, 84, 182, 5);
		if (k > 0) {
			this.blit(i, l, 0, 89, k, 5);
		}

		this.client.getProfiler().pop();
	}

	public void renderExperienceBar(int i) {
		this.client.getProfiler().push("expBar");
		this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
		int j = this.client.player.getNextLevelExperience();
		if (j > 0) {
			int k = 182;
			int l = (int)(this.client.player.experienceProgress * 183.0F);
			int m = this.scaledHeight - 32 + 3;
			this.blit(i, m, 0, 64, 182, 5);
			if (l > 0) {
				this.blit(i, m, 0, 69, l, 5);
			}
		}

		this.client.getProfiler().pop();
		if (this.client.player.experienceLevel > 0) {
			this.client.getProfiler().push("expLevel");
			String string = "" + this.client.player.experienceLevel;
			int n = (this.scaledWidth - this.getFontRenderer().getStringWidth(string)) / 2;
			int o = this.scaledHeight - 31 - 4;
			this.getFontRenderer().draw(string, (float)(n + 1), (float)o, 0);
			this.getFontRenderer().draw(string, (float)(n - 1), (float)o, 0);
			this.getFontRenderer().draw(string, (float)n, (float)(o + 1), 0);
			this.getFontRenderer().draw(string, (float)n, (float)(o - 1), 0);
			this.getFontRenderer().draw(string, (float)n, (float)o, 8453920);
			this.client.getProfiler().pop();
		}
	}

	public void renderHeldItemTooltip() {
		this.client.getProfiler().push("selectedItemName");
		if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
			Text text = new LiteralText("").append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
			if (this.currentStack.hasCustomName()) {
				text.formatted(Formatting.field_1056);
			}

			String string = text.asFormattedString();
			int i = (this.scaledWidth - this.getFontRenderer().getStringWidth(string)) / 2;
			int j = this.scaledHeight - 59;
			if (!this.client.interactionManager.hasStatusBars()) {
				j += 14;
			}

			int k = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
			if (k > 255) {
				k = 255;
			}

			if (k > 0) {
				RenderSystem.pushMatrix();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				fill(i - 2, j - 2, i + this.getFontRenderer().getStringWidth(string) + 2, j + 9 + 2, this.client.options.getTextBackgroundColor(0));
				this.getFontRenderer().drawWithShadow(string, (float)i, (float)j, 16777215 + (k << 24));
				RenderSystem.disableBlend();
				RenderSystem.popMatrix();
			}
		}

		this.client.getProfiler().pop();
	}

	public void renderDemoTimer() {
		this.client.getProfiler().push("demo");
		String string;
		if (this.client.world.getTime() >= 120500L) {
			string = I18n.translate("demo.demoExpired");
		} else {
			string = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
		}

		int i = this.getFontRenderer().getStringWidth(string);
		this.getFontRenderer().drawWithShadow(string, (float)(this.scaledWidth - i - 10), 5.0F, 16777215);
		this.client.getProfiler().pop();
	}

	private void renderScoreboardSidebar(ScoreboardObjective scoreboardObjective) {
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

		String string = scoreboardObjective.getDisplayName().asFormattedString();
		int i = this.getFontRenderer().getStringWidth(string);
		int j = i;

		for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			String string2 = Team.modifyText(team, new LiteralText(scoreboardPlayerScore.getPlayerName())).asFormattedString()
				+ ": "
				+ Formatting.field_1061
				+ scoreboardPlayerScore.getScore();
			j = Math.max(j, this.getFontRenderer().getStringWidth(string2));
		}

		int k = collection.size() * 9;
		int l = this.scaledHeight / 2 + k / 3;
		int m = 3;
		int n = this.scaledWidth - j - 3;
		int o = 0;
		int p = this.client.options.getTextBackgroundColor(0.3F);
		int q = this.client.options.getTextBackgroundColor(0.4F);

		for (ScoreboardPlayerScore scoreboardPlayerScore2 : collection) {
			o++;
			Team team2 = scoreboard.getPlayerTeam(scoreboardPlayerScore2.getPlayerName());
			String string3 = Team.modifyText(team2, new LiteralText(scoreboardPlayerScore2.getPlayerName())).asFormattedString();
			String string4 = Formatting.field_1061 + "" + scoreboardPlayerScore2.getScore();
			int s = l - o * 9;
			int t = this.scaledWidth - 3 + 2;
			fill(n - 2, s, t, s + 9, p);
			this.getFontRenderer().draw(string3, (float)n, (float)s, -1);
			this.getFontRenderer().draw(string4, (float)(t - this.getFontRenderer().getStringWidth(string4)), (float)s, -1);
			if (o == collection.size()) {
				fill(n - 2, s - 9 - 1, t, s - 1, q);
				fill(n - 2, s - 1, t, s, p);
				this.getFontRenderer().draw(string, (float)(n + j / 2 - i / 2), (float)(s - 9), -1);
			}
		}
	}

	private PlayerEntity getCameraPlayer() {
		return !(this.client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.client.getCameraEntity();
	}

	private LivingEntity getRiddenEntity() {
		PlayerEntity playerEntity = this.getCameraPlayer();
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

	private int getHeartCount(LivingEntity livingEntity) {
		if (livingEntity != null && livingEntity.isLiving()) {
			float f = livingEntity.getMaximumHealth();
			int i = (int)(f + 0.5F) / 2;
			if (i > 30) {
				i = 30;
			}

			return i;
		} else {
			return 0;
		}
	}

	private int getHeartRows(int i) {
		return (int)Math.ceil((double)i / 10.0);
	}

	private void renderStatusBars() {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			int i = MathHelper.ceil(playerEntity.getHealth());
			boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
			long l = Util.getMeasuringTimeMs();
			if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = l;
				this.heartJumpEndTick = (long)(this.ticks + 20);
			} else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
				this.lastHealthCheckTime = l;
				this.heartJumpEndTick = (long)(this.ticks + 10);
			}

			if (l - this.lastHealthCheckTime > 1000L) {
				this.lastHealthValue = i;
				this.renderHealthValue = i;
				this.lastHealthCheckTime = l;
			}

			this.lastHealthValue = i;
			int j = this.renderHealthValue;
			this.random.setSeed((long)(this.ticks * 312871));
			HungerManager hungerManager = playerEntity.getHungerManager();
			int k = hungerManager.getFoodLevel();
			EntityAttributeInstance entityAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);
			int m = this.scaledWidth / 2 - 91;
			int n = this.scaledWidth / 2 + 91;
			int o = this.scaledHeight - 39;
			float f = (float)entityAttributeInstance.getValue();
			int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
			int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
			int r = Math.max(10 - (q - 2), 3);
			int s = o - (q - 1) * r - 10;
			int t = o - 10;
			int u = p;
			int v = playerEntity.getArmor();
			int w = -1;
			if (playerEntity.hasStatusEffect(StatusEffects.field_5924)) {
				w = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.getProfiler().push("armor");

			for (int x = 0; x < 10; x++) {
				if (v > 0) {
					int y = m + x * 8;
					if (x * 2 + 1 < v) {
						this.blit(y, s, 34, 9, 9, 9);
					}

					if (x * 2 + 1 == v) {
						this.blit(y, s, 25, 9, 9, 9);
					}

					if (x * 2 + 1 > v) {
						this.blit(y, s, 16, 9, 9, 9);
					}
				}
			}

			this.client.getProfiler().swap("health");

			for (int z = MathHelper.ceil((f + (float)p) / 2.0F) - 1; z >= 0; z--) {
				int aa = 16;
				if (playerEntity.hasStatusEffect(StatusEffects.field_5899)) {
					aa += 36;
				} else if (playerEntity.hasStatusEffect(StatusEffects.field_5920)) {
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
				if (playerEntity.world.getLevelProperties().isHardcore()) {
					af = 5;
				}

				this.blit(ad, ae, 16 + ab * 9, 9 * af, 9, 9);
				if (bl) {
					if (z * 2 + 1 < j) {
						this.blit(ad, ae, aa + 54, 9 * af, 9, 9);
					}

					if (z * 2 + 1 == j) {
						this.blit(ad, ae, aa + 63, 9 * af, 9, 9);
					}
				}

				if (u > 0) {
					if (u == p && p % 2 == 1) {
						this.blit(ad, ae, aa + 153, 9 * af, 9, 9);
						u--;
					} else {
						this.blit(ad, ae, aa + 144, 9 * af, 9, 9);
						u -= 2;
					}
				} else {
					if (z * 2 + 1 < i) {
						this.blit(ad, ae, aa + 36, 9 * af, 9, 9);
					}

					if (z * 2 + 1 == i) {
						this.blit(ad, ae, aa + 45, 9 * af, 9, 9);
					}
				}
			}

			LivingEntity livingEntity = this.getRiddenEntity();
			int ag = this.getHeartCount(livingEntity);
			if (ag == 0) {
				this.client.getProfiler().swap("food");

				for (int ah = 0; ah < 10; ah++) {
					int ai = o;
					int aj = 16;
					int ak = 0;
					if (playerEntity.hasStatusEffect(StatusEffects.field_5903)) {
						aj += 36;
						ak = 13;
					}

					if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
						ai = o + (this.random.nextInt(3) - 1);
					}

					int al = n - ah * 8 - 9;
					this.blit(al, ai, 16 + ak * 9, 27, 9, 9);
					if (ah * 2 + 1 < k) {
						this.blit(al, ai, aj + 36, 27, 9, 9);
					}

					if (ah * 2 + 1 == k) {
						this.blit(al, ai, aj + 45, 27, 9, 9);
					}
				}

				t -= 10;
			}

			this.client.getProfiler().swap("air");
			int am = playerEntity.getAir();
			int an = playerEntity.getMaxAir();
			if (playerEntity.isInFluid(FluidTags.field_15517) || am < an) {
				int ao = this.getHeartRows(ag) - 1;
				t -= ao * 10;
				int ap = MathHelper.ceil((double)(am - 2) * 10.0 / (double)an);
				int aq = MathHelper.ceil((double)am * 10.0 / (double)an) - ap;

				for (int ar = 0; ar < ap + aq; ar++) {
					if (ar < ap) {
						this.blit(n - ar * 8 - 9, t, 16, 18, 9, 9);
					} else {
						this.blit(n - ar * 8 - 9, t, 25, 18, 9, 9);
					}
				}
			}

			this.client.getProfiler().pop();
		}
	}

	private void renderMountHealth() {
		LivingEntity livingEntity = this.getRiddenEntity();
		if (livingEntity != null) {
			int i = this.getHeartCount(livingEntity);
			if (i != 0) {
				int j = (int)Math.ceil((double)livingEntity.getHealth());
				this.client.getProfiler().swap("mountHealth");
				int k = this.scaledHeight - 39;
				int l = this.scaledWidth / 2 + 91;
				int m = k;
				int n = 0;

				for (boolean bl = false; i > 0; n += 20) {
					int o = Math.min(i, 10);
					i -= o;

					for (int p = 0; p < o; p++) {
						int q = 52;
						int r = 0;
						int s = l - p * 8 - 9;
						this.blit(s, m, 52 + r * 9, 9, 9, 9);
						if (p * 2 + 1 + n < j) {
							this.blit(s, m, 88, 9, 9, 9);
						}

						if (p * 2 + 1 + n == j) {
							this.blit(s, m, 97, 9, 9, 9);
						}
					}

					m -= 10;
				}
			}
		}
	}

	private void renderPumpkinOverlay() {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableAlphaTest();
		this.client.getTextureManager().bindTexture(PUMPKIN_BLUR);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void updateVignetteDarkness(Entity entity) {
		if (entity != null) {
			float f = MathHelper.clamp(1.0F - entity.getBrightnessAtEyes(), 0.0F, 1.0F);
			this.vignetteDarkness = (float)((double)this.vignetteDarkness + (double)(f - this.vignetteDarkness) * 0.01);
		}
	}

	private void renderVignetteOverlay(Entity entity) {
		WorldBorder worldBorder = this.client.world.getWorldBorder();
		float f = (float)worldBorder.getDistanceInsideBorder(entity);
		double d = Math.min(
			worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getSize())
		);
		double e = Math.max((double)worldBorder.getWarningBlocks(), d);
		if ((double)f < e) {
			f = 1.0F - (float)((double)f / e);
		} else {
			f = 0.0F;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
		if (f > 0.0F) {
			RenderSystem.color4f(0.0F, f, f, 1.0F);
		} else {
			RenderSystem.color4f(this.vignetteDarkness, this.vignetteDarkness, this.vignetteDarkness, 1.0F);
		}

		this.client.getTextureManager().bindTexture(VIGNETTE_TEX);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
	}

	private void renderPortalOverlay(float f) {
		if (f < 1.0F) {
			f *= f;
			f *= f;
			f = f * 0.8F + 0.2F;
		}

		RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Sprite sprite = this.client.getBlockRenderManager().getModels().getSprite(Blocks.field_10316.getDefaultState());
		float g = sprite.getMinU();
		float h = sprite.getMinV();
		float i = sprite.getMaxU();
		float j = sprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(g, j).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(i, j).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(i, h).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(g, h).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHotbarItem(int i, int j, float f, PlayerEntity playerEntity, ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			float g = (float)itemStack.getCooldown() - f;
			if (g > 0.0F) {
				RenderSystem.pushMatrix();
				float h = 1.0F + g / 5.0F;
				RenderSystem.translatef((float)(i + 8), (float)(j + 12), 0.0F);
				RenderSystem.scalef(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				RenderSystem.translatef((float)(-(i + 8)), (float)(-(j + 12)), 0.0F);
			}

			this.itemRenderer.renderGuiItem(playerEntity, itemStack, i, j);
			if (g > 0.0F) {
				RenderSystem.popMatrix();
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
				this.title = "";
				this.subtitle = "";
			}
		}

		this.ticks++;
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			this.updateVignetteDarkness(entity);
		}

		if (this.client.player != null) {
			ItemStack itemStack = this.client.player.inventory.getMainHandStack();
			if (itemStack.isEmpty()) {
				this.heldItemTooltipFade = 0;
			} else if (this.currentStack.isEmpty() || itemStack.getItem() != this.currentStack.getItem() || !itemStack.getName().equals(this.currentStack.getName())) {
				this.heldItemTooltipFade = 40;
			} else if (this.heldItemTooltipFade > 0) {
				this.heldItemTooltipFade--;
			}

			this.currentStack = itemStack;
		}
	}

	public void setRecordPlayingOverlay(String string) {
		this.setOverlayMessage(I18n.translate("record.nowPlaying", string), true);
	}

	public void setOverlayMessage(String string, boolean bl) {
		this.overlayMessage = string;
		this.overlayRemaining = 60;
		this.overlayTinted = bl;
	}

	public void setTitles(String string, String string2, int i, int j, int k) {
		if (string == null && string2 == null && i < 0 && j < 0 && k < 0) {
			this.title = "";
			this.subtitle = "";
			this.titleTotalTicks = 0;
		} else if (string != null) {
			this.title = string;
			this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
		} else if (string2 != null) {
			this.subtitle = string2;
		} else {
			if (i >= 0) {
				this.titleFadeInTicks = i;
			}

			if (j >= 0) {
				this.titleRemainTicks = j;
			}

			if (k >= 0) {
				this.titleFadeOutTicks = k;
			}

			if (this.titleTotalTicks > 0) {
				this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
			}
		}
	}

	public void setOverlayMessage(Text text, boolean bl) {
		this.setOverlayMessage(text.getString(), bl);
	}

	public void addChatMessage(MessageType messageType, Text text) {
		for (ClientChatListener clientChatListener : (List)this.listeners.get(messageType)) {
			clientChatListener.onChatMessage(messageType, text);
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

	public void clear() {
		this.playerListHud.clear();
		this.bossBarHud.clear();
		this.client.getToastManager().clear();
	}

	public BossBarHud getBossBarHud() {
		return this.bossBarHud;
	}

	public void resetDebugHudChunk() {
		this.debugHud.resetChunk();
	}
}
