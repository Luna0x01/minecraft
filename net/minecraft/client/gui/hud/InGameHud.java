package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.GameInfoChatListener;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.apache.commons.lang3.StringUtils;

public class InGameHud extends DrawableHelper {
	private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
	private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
	private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
	private static final Identifier SPYGLASS_SCOPE = new Identifier("textures/misc/spyglass_scope.png");
	private static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");
	private static final Text DEMO_EXPIRED_MESSAGE = new TranslatableText("demo.demoExpired");
	private static final int WHITE = 16777215;
	private static final float field_32168 = 5.0F;
	private static final int field_32169 = 10;
	private static final int field_32170 = 10;
	private static final String field_32171 = ": ";
	private static final float field_32172 = 0.2F;
	private static final int field_33942 = 9;
	private static final int field_33943 = 8;
	private final Random random = new Random();
	private final MinecraftClient client;
	private final ItemRenderer itemRenderer;
	private final ChatHud chatHud;
	private int ticks;
	@Nullable
	private Text overlayMessage;
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
	@Nullable
	private Text title;
	@Nullable
	private Text subtitle;
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
	private float spyglassScale;

	public InGameHud(MinecraftClient client) {
		this.client = client;
		this.itemRenderer = client.getItemRenderer();
		this.debugHud = new DebugHud(client);
		this.spectatorHud = new SpectatorHud(client);
		this.chatHud = new ChatHud(client);
		this.playerListHud = new PlayerListHud(client, this);
		this.bossBarHud = new BossBarHud(client);
		this.subtitlesHud = new SubtitlesHud(client);

		for (MessageType messageType : MessageType.values()) {
			this.listeners.put(messageType, Lists.newArrayList());
		}

		ClientChatListener clientChatListener = NarratorManager.INSTANCE;
		((List)this.listeners.get(MessageType.CHAT)).add(new ChatHudListener(client));
		((List)this.listeners.get(MessageType.CHAT)).add(clientChatListener);
		((List)this.listeners.get(MessageType.SYSTEM)).add(new ChatHudListener(client));
		((List)this.listeners.get(MessageType.SYSTEM)).add(clientChatListener);
		((List)this.listeners.get(MessageType.GAME_INFO)).add(new GameInfoChatListener(client));
		this.setDefaultTitleFade();
	}

	public void setDefaultTitleFade() {
		this.titleFadeInTicks = 10;
		this.titleRemainTicks = 70;
		this.titleFadeOutTicks = 20;
	}

	public void render(MatrixStack matrices, float tickDelta) {
		this.scaledWidth = this.client.getWindow().getScaledWidth();
		this.scaledHeight = this.client.getWindow().getScaledHeight();
		TextRenderer textRenderer = this.getTextRenderer();
		RenderSystem.enableBlend();
		if (MinecraftClient.isFancyGraphicsOrBetter()) {
			this.renderVignetteOverlay(this.client.getCameraEntity());
		} else {
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.defaultBlendFunc();
		}

		float f = this.client.getLastFrameDuration();
		this.spyglassScale = MathHelper.lerp(0.5F * f, this.spyglassScale, 1.125F);
		if (this.client.options.getPerspective().isFirstPerson()) {
			if (this.client.player.isUsingSpyglass()) {
				this.renderSpyglassOverlay(this.spyglassScale);
			} else {
				this.spyglassScale = 0.5F;
				ItemStack itemStack = this.client.player.getInventory().getArmorStack(3);
				if (itemStack.isOf(Blocks.CARVED_PUMPKIN.asItem())) {
					this.renderOverlay(PUMPKIN_BLUR, 1.0F);
				}
			}
		}

		if (this.client.player.getFrozenTicks() > 0) {
			this.renderOverlay(POWDER_SNOW_OUTLINE, this.client.player.getFreezingScale());
		}

		float g = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
		if (g > 0.0F && !this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
			this.renderPortalOverlay(g);
		}

		if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
			this.spectatorHud.render(matrices, tickDelta);
		} else if (!this.client.options.hudHidden) {
			this.renderHotbar(tickDelta, matrices);
		}

		if (!this.client.options.hudHidden) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
			RenderSystem.enableBlend();
			this.renderCrosshair(matrices);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.defaultBlendFunc();
			this.client.getProfiler().push("bossHealth");
			this.bossBarHud.render(matrices);
			this.client.getProfiler().pop();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
			if (this.client.interactionManager.hasStatusBars()) {
				this.renderStatusBars(matrices);
			}

			this.renderMountHealth(matrices);
			RenderSystem.disableBlend();
			int i = this.scaledWidth / 2 - 91;
			if (this.client.player.hasJumpingMount()) {
				this.renderMountJumpBar(matrices, i);
			} else if (this.client.interactionManager.hasExperienceBar()) {
				this.renderExperienceBar(matrices, i);
			}

			if (this.client.options.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
				this.renderHeldItemTooltip(matrices);
			} else if (this.client.player.isSpectator()) {
				this.spectatorHud.render(matrices);
			}
		}

		if (this.client.player.getSleepTimer() > 0) {
			this.client.getProfiler().push("sleep");
			RenderSystem.disableDepthTest();
			float h = (float)this.client.player.getSleepTimer();
			float j = h / 100.0F;
			if (j > 1.0F) {
				j = 1.0F - (h - 100.0F) / 10.0F;
			}

			int k = (int)(220.0F * j) << 24 | 1052704;
			fill(matrices, 0, 0, this.scaledWidth, this.scaledHeight, k);
			RenderSystem.enableDepthTest();
			this.client.getProfiler().pop();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.client.isDemo()) {
			this.renderDemoTimer(matrices);
		}

		this.renderStatusEffectOverlay(matrices);
		if (this.client.options.debugEnabled) {
			this.debugHud.render(matrices);
		}

		if (!this.client.options.hudHidden) {
			if (this.overlayMessage != null && this.overlayRemaining > 0) {
				this.client.getProfiler().push("overlayMessage");
				float l = (float)this.overlayRemaining - tickDelta;
				int m = (int)(l * 255.0F / 20.0F);
				if (m > 255) {
					m = 255;
				}

				if (m > 8) {
					matrices.push();
					matrices.translate((double)(this.scaledWidth / 2), (double)(this.scaledHeight - 68), 0.0);
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					int n = 16777215;
					if (this.overlayTinted) {
						n = MathHelper.hsvToRgb(l / 50.0F, 0.7F, 0.6F) & 16777215;
					}

					int o = m << 24 & 0xFF000000;
					int p = textRenderer.getWidth(this.overlayMessage);
					this.drawTextBackground(matrices, textRenderer, -4, p, 16777215 | o);
					textRenderer.draw(matrices, this.overlayMessage, (float)(-p / 2), -4.0F, n | o);
					RenderSystem.disableBlend();
					matrices.pop();
				}

				this.client.getProfiler().pop();
			}

			if (this.title != null && this.titleTotalTicks > 0) {
				this.client.getProfiler().push("titleAndSubtitle");
				float q = (float)this.titleTotalTicks - tickDelta;
				int r = 255;
				if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
					float s = (float)(this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks) - q;
					r = (int)(s * 255.0F / (float)this.titleFadeInTicks);
				}

				if (this.titleTotalTicks <= this.titleFadeOutTicks) {
					r = (int)(q * 255.0F / (float)this.titleFadeOutTicks);
				}

				r = MathHelper.clamp(r, 0, 255);
				if (r > 8) {
					matrices.push();
					matrices.translate((double)(this.scaledWidth / 2), (double)(this.scaledHeight / 2), 0.0);
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					matrices.push();
					matrices.scale(4.0F, 4.0F, 4.0F);
					int t = r << 24 & 0xFF000000;
					int u = textRenderer.getWidth(this.title);
					this.drawTextBackground(matrices, textRenderer, -10, u, 16777215 | t);
					textRenderer.drawWithShadow(matrices, this.title, (float)(-u / 2), -10.0F, 16777215 | t);
					matrices.pop();
					if (this.subtitle != null) {
						matrices.push();
						matrices.scale(2.0F, 2.0F, 2.0F);
						int v = textRenderer.getWidth(this.subtitle);
						this.drawTextBackground(matrices, textRenderer, 5, v, 16777215 | t);
						textRenderer.drawWithShadow(matrices, this.subtitle, (float)(-v / 2), 5.0F, 16777215 | t);
						matrices.pop();
					}

					RenderSystem.disableBlend();
					matrices.pop();
				}

				this.client.getProfiler().pop();
			}

			this.subtitlesHud.render(matrices);
			Scoreboard scoreboard = this.client.world.getScoreboard();
			ScoreboardObjective scoreboardObjective = null;
			Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
			if (team != null) {
				int w = team.getColor().getColorIndex();
				if (w >= 0) {
					scoreboardObjective = scoreboard.getObjectiveForSlot(3 + w);
				}
			}

			ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
			if (scoreboardObjective2 != null) {
				this.renderScoreboardSidebar(matrices, scoreboardObjective2);
			}

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			matrices.push();
			matrices.translate(0.0, (double)(this.scaledHeight - 48), 0.0);
			this.client.getProfiler().push("chat");
			this.chatHud.render(matrices, this.ticks);
			this.client.getProfiler().pop();
			matrices.pop();
			scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
			if (!this.client.options.keyPlayerList.isPressed()
				|| this.client.isInSingleplayer() && this.client.player.networkHandler.getPlayerList().size() <= 1 && scoreboardObjective2 == null) {
				this.playerListHud.setVisible(false);
			} else {
				this.playerListHud.setVisible(true);
				this.playerListHud.render(matrices, this.scaledWidth, scoreboard, scoreboardObjective2);
			}
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawTextBackground(MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color) {
		int i = this.client.options.getTextBackgroundColor(0.0F);
		if (i != 0) {
			int j = -width / 2;
			fill(matrices, j - 2, yOffset - 2, j + width + 2, yOffset + 9 + 2, BackgroundHelper.ColorMixer.mixColor(i, color));
		}
	}

	private void renderCrosshair(MatrixStack matrices) {
		GameOptions gameOptions = this.client.options;
		if (gameOptions.getPerspective().isFirstPerson()) {
			if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
				if (gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.hasReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
					Camera camera = this.client.gameRenderer.getCamera();
					MatrixStack matrixStack = RenderSystem.getModelViewStack();
					matrixStack.push();
					matrixStack.translate((double)(this.scaledWidth / 2), (double)(this.scaledHeight / 2), (double)this.getZOffset());
					matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(camera.getPitch()));
					matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
					matrixStack.scale(-1.0F, -1.0F, -1.0F);
					RenderSystem.applyModelViewMatrix();
					RenderSystem.renderCrosshair(10);
					matrixStack.pop();
					RenderSystem.applyModelViewMatrix();
				} else {
					RenderSystem.blendFuncSeparate(
						GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
					);
					int i = 15;
					this.drawTexture(matrices, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
					if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
						float f = this.client.player.getAttackCooldownProgress(0.0F);
						boolean bl = false;
						if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0F) {
							bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
							bl &= this.client.targetedEntity.isAlive();
						}

						int j = this.scaledHeight / 2 - 7 + 16;
						int k = this.scaledWidth / 2 - 8;
						if (bl) {
							this.drawTexture(matrices, k, j, 68, 94, 16, 16);
						} else if (f < 1.0F) {
							int l = (int)(f * 17.0F);
							this.drawTexture(matrices, k, j, 36, 94, 16, 4);
							this.drawTexture(matrices, k, j, 52, 94, l, 4);
						}
					}
				}
			}
		}
	}

	private boolean shouldRenderSpectatorCrosshair(HitResult hitResult) {
		if (hitResult == null) {
			return false;
		} else if (hitResult.getType() == HitResult.Type.ENTITY) {
			return ((EntityHitResult)hitResult).getEntity() instanceof NamedScreenHandlerFactory;
		} else if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			World world = this.client.world;
			return world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null;
		} else {
			return false;
		}
	}

	protected void renderStatusEffectOverlay(MatrixStack matrices) {
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty()) {
			RenderSystem.enableBlend();
			int i = 0;
			int j = 0;
			StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
			List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
			RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);

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

					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					float f = 1.0F;
					if (statusEffectInstance.isAmbient()) {
						this.drawTexture(matrices, k, l, 165, 166, 24, 24);
					} else {
						this.drawTexture(matrices, k, l, 141, 166, 24, 24);
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
						RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
						drawSprite(matrices, n + 3, o + 3, this.getZOffset(), 18, 18, sprite);
					});
				}
			}

			list.forEach(Runnable::run);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private void renderHotbar(float tickDelta, MatrixStack matrices) {
		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
			ItemStack itemStack = playerEntity.getOffHandStack();
			Arm arm = playerEntity.getMainArm().getOpposite();
			int i = this.scaledWidth / 2;
			int j = this.getZOffset();
			int k = 182;
			int l = 91;
			this.setZOffset(-90);
			this.drawTexture(matrices, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
			this.drawTexture(matrices, i - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
			if (!itemStack.isEmpty()) {
				if (arm == Arm.LEFT) {
					this.drawTexture(matrices, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
				} else {
					this.drawTexture(matrices, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
				}
			}

			this.setZOffset(j);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			int m = 1;

			for (int n = 0; n < 9; n++) {
				int o = i - 90 + n * 20 + 2;
				int p = this.scaledHeight - 16 - 3;
				this.renderHotbarItem(o, p, tickDelta, playerEntity, playerEntity.getInventory().main.get(n), m++);
			}

			if (!itemStack.isEmpty()) {
				int q = this.scaledHeight - 16 - 3;
				if (arm == Arm.LEFT) {
					this.renderHotbarItem(i - 91 - 26, q, tickDelta, playerEntity, itemStack, m++);
				} else {
					this.renderHotbarItem(i + 91 + 10, q, tickDelta, playerEntity, itemStack, m++);
				}
			}

			if (this.client.options.attackIndicator == AttackIndicator.HOTBAR) {
				float f = this.client.player.getAttackCooldownProgress(0.0F);
				if (f < 1.0F) {
					int r = this.scaledHeight - 20;
					int s = i + 91 + 6;
					if (arm == Arm.RIGHT) {
						s = i - 91 - 22;
					}

					RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
					int t = (int)(f * 19.0F);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					this.drawTexture(matrices, s, r, 0, 94, 18, 18);
					this.drawTexture(matrices, s, r + 18 - t, 18, 112 - t, 18, t);
				}
			}

			RenderSystem.disableBlend();
		}
	}

	public void renderMountJumpBar(MatrixStack matrices, int x) {
		this.client.getProfiler().push("jumpBar");
		RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
		float f = this.client.player.getMountJumpStrength();
		int i = 182;
		int j = (int)(f * 183.0F);
		int k = this.scaledHeight - 32 + 3;
		this.drawTexture(matrices, x, k, 0, 84, 182, 5);
		if (j > 0) {
			this.drawTexture(matrices, x, k, 0, 89, j, 5);
		}

		this.client.getProfiler().pop();
	}

	public void renderExperienceBar(MatrixStack matrices, int x) {
		this.client.getProfiler().push("expBar");
		RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
		int i = this.client.player.getNextLevelExperience();
		if (i > 0) {
			int j = 182;
			int k = (int)(this.client.player.experienceProgress * 183.0F);
			int l = this.scaledHeight - 32 + 3;
			this.drawTexture(matrices, x, l, 0, 64, 182, 5);
			if (k > 0) {
				this.drawTexture(matrices, x, l, 0, 69, k, 5);
			}
		}

		this.client.getProfiler().pop();
		if (this.client.player.experienceLevel > 0) {
			this.client.getProfiler().push("expLevel");
			String string = this.client.player.experienceLevel + "";
			int m = (this.scaledWidth - this.getTextRenderer().getWidth(string)) / 2;
			int n = this.scaledHeight - 31 - 4;
			this.getTextRenderer().draw(matrices, string, (float)(m + 1), (float)n, 0);
			this.getTextRenderer().draw(matrices, string, (float)(m - 1), (float)n, 0);
			this.getTextRenderer().draw(matrices, string, (float)m, (float)(n + 1), 0);
			this.getTextRenderer().draw(matrices, string, (float)m, (float)(n - 1), 0);
			this.getTextRenderer().draw(matrices, string, (float)m, (float)n, 8453920);
			this.client.getProfiler().pop();
		}
	}

	public void renderHeldItemTooltip(MatrixStack matrices) {
		this.client.getProfiler().push("selectedItemName");
		if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
			MutableText mutableText = new LiteralText("").append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
			if (this.currentStack.hasCustomName()) {
				mutableText.formatted(Formatting.ITALIC);
			}

			int i = this.getTextRenderer().getWidth(mutableText);
			int j = (this.scaledWidth - i) / 2;
			int k = this.scaledHeight - 59;
			if (!this.client.interactionManager.hasStatusBars()) {
				k += 14;
			}

			int l = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
			if (l > 255) {
				l = 255;
			}

			if (l > 0) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				fill(matrices, j - 2, k - 2, j + i + 2, k + 9 + 2, this.client.options.getTextBackgroundColor(0));
				this.getTextRenderer().drawWithShadow(matrices, mutableText, (float)j, (float)k, 16777215 + (l << 24));
				RenderSystem.disableBlend();
			}
		}

		this.client.getProfiler().pop();
	}

	public void renderDemoTimer(MatrixStack matrices) {
		this.client.getProfiler().push("demo");
		Text text;
		if (this.client.world.getTime() >= 120500L) {
			text = DEMO_EXPIRED_MESSAGE;
		} else {
			text = new TranslatableText("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
		}

		int i = this.getTextRenderer().getWidth(text);
		this.getTextRenderer().drawWithShadow(matrices, text, (float)(this.scaledWidth - i - 10), 5.0F, 16777215);
		this.client.getProfiler().pop();
	}

	private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective) {
		Scoreboard scoreboard = objective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
		List<ScoreboardPlayerScore> list = (List<ScoreboardPlayerScore>)collection.stream()
			.filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
			.collect(Collectors.toList());
		if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}

		List<Pair<ScoreboardPlayerScore, Text>> list2 = Lists.newArrayListWithCapacity(collection.size());
		Text text = objective.getDisplayName();
		int i = this.getTextRenderer().getWidth(text);
		int j = i;
		int k = this.getTextRenderer().getWidth(": ");

		for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			Text text2 = Team.decorateName(team, new LiteralText(scoreboardPlayerScore.getPlayerName()));
			list2.add(Pair.of(scoreboardPlayerScore, text2));
			j = Math.max(j, this.getTextRenderer().getWidth(text2) + k + this.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())));
		}

		int l = collection.size() * 9;
		int m = this.scaledHeight / 2 + l / 3;
		int n = 3;
		int o = this.scaledWidth - j - 3;
		int p = 0;
		int q = this.client.options.getTextBackgroundColor(0.3F);
		int r = this.client.options.getTextBackgroundColor(0.4F);

		for (Pair<ScoreboardPlayerScore, Text> pair : list2) {
			p++;
			ScoreboardPlayerScore scoreboardPlayerScore2 = (ScoreboardPlayerScore)pair.getFirst();
			Text text3 = (Text)pair.getSecond();
			String string = "" + Formatting.RED + scoreboardPlayerScore2.getScore();
			int t = m - p * 9;
			int u = this.scaledWidth - 3 + 2;
			fill(matrices, o - 2, t, u, t + 9, q);
			this.getTextRenderer().draw(matrices, text3, (float)o, (float)t, -1);
			this.getTextRenderer().draw(matrices, string, (float)(u - this.getTextRenderer().getWidth(string)), (float)t, -1);
			if (p == collection.size()) {
				fill(matrices, o - 2, t - 9 - 1, u, t - 1, r);
				fill(matrices, o - 2, t - 1, u, t, q);
				this.getTextRenderer().draw(matrices, text, (float)(o + j / 2 - i / 2), (float)(t - 9), -1);
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

	private int getHeartCount(LivingEntity entity) {
		if (entity != null && entity.isLiving()) {
			float f = entity.getMaxHealth();
			int i = (int)(f + 0.5F) / 2;
			if (i > 30) {
				i = 30;
			}

			return i;
		} else {
			return 0;
		}
	}

	private int getHeartRows(int heartCount) {
		return (int)Math.ceil((double)heartCount / 10.0);
	}

	private void renderStatusBars(MatrixStack matrices) {
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
			int m = this.scaledWidth / 2 - 91;
			int n = this.scaledWidth / 2 + 91;
			int o = this.scaledHeight - 39;
			float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
			int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
			int q = MathHelper.ceil((f + (float)p) / 2.0F / 10.0F);
			int r = Math.max(10 - (q - 2), 3);
			int s = o - (q - 1) * r - 10;
			int t = o - 10;
			int u = playerEntity.getArmor();
			int v = -1;
			if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
				v = this.ticks % MathHelper.ceil(f + 5.0F);
			}

			this.client.getProfiler().push("armor");

			for (int w = 0; w < 10; w++) {
				if (u > 0) {
					int x = m + w * 8;
					if (w * 2 + 1 < u) {
						this.drawTexture(matrices, x, s, 34, 9, 9, 9);
					}

					if (w * 2 + 1 == u) {
						this.drawTexture(matrices, x, s, 25, 9, 9, 9);
					}

					if (w * 2 + 1 > u) {
						this.drawTexture(matrices, x, s, 16, 9, 9, 9);
					}
				}
			}

			this.client.getProfiler().swap("health");
			this.renderHealthBar(matrices, playerEntity, m, o, r, v, f, i, j, p, bl);
			LivingEntity livingEntity = this.getRiddenEntity();
			int y = this.getHeartCount(livingEntity);
			if (y == 0) {
				this.client.getProfiler().swap("food");

				for (int z = 0; z < 10; z++) {
					int aa = o;
					int ab = 16;
					int ac = 0;
					if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
						ab += 36;
						ac = 13;
					}

					if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
						aa = o + (this.random.nextInt(3) - 1);
					}

					int ad = n - z * 8 - 9;
					this.drawTexture(matrices, ad, aa, 16 + ac * 9, 27, 9, 9);
					if (z * 2 + 1 < k) {
						this.drawTexture(matrices, ad, aa, ab + 36, 27, 9, 9);
					}

					if (z * 2 + 1 == k) {
						this.drawTexture(matrices, ad, aa, ab + 45, 27, 9, 9);
					}
				}

				t -= 10;
			}

			this.client.getProfiler().swap("air");
			int ae = playerEntity.getMaxAir();
			int af = Math.min(playerEntity.getAir(), ae);
			if (playerEntity.isSubmergedIn(FluidTags.WATER) || af < ae) {
				int ag = this.getHeartRows(y) - 1;
				t -= ag * 10;
				int ah = MathHelper.ceil((double)(af - 2) * 10.0 / (double)ae);
				int ai = MathHelper.ceil((double)af * 10.0 / (double)ae) - ah;

				for (int aj = 0; aj < ah + ai; aj++) {
					if (aj < ah) {
						this.drawTexture(matrices, n - aj * 8 - 9, t, 16, 18, 9, 9);
					} else {
						this.drawTexture(matrices, n - aj * 8 - 9, t, 25, 18, 9, 9);
					}
				}
			}

			this.client.getProfiler().pop();
		}
	}

	private void renderHealthBar(
		MatrixStack matrices,
		PlayerEntity player,
		int x,
		int y,
		int lines,
		int regeneratingHeartIndex,
		float maxHealth,
		int lastHealth,
		int health,
		int absorption,
		boolean blinking
	) {
		InGameHud.HeartType heartType = InGameHud.HeartType.fromPlayerState(player);
		int i = 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0);
		int j = MathHelper.ceil((double)maxHealth / 2.0);
		int k = MathHelper.ceil((double)absorption / 2.0);
		int l = j * 2;

		for (int m = j + k - 1; m >= 0; m--) {
			int n = m / 10;
			int o = m % 10;
			int p = x + o * 8;
			int q = y - n * lines;
			if (lastHealth + absorption <= 4) {
				q += this.random.nextInt(2);
			}

			if (m < j && m == regeneratingHeartIndex) {
				q -= 2;
			}

			this.drawHeart(matrices, InGameHud.HeartType.CONTAINER, p, q, i, blinking, false);
			int r = m * 2;
			boolean bl = m >= j;
			if (bl) {
				int s = r - l;
				if (s < absorption) {
					boolean bl2 = s + 1 == absorption;
					this.drawHeart(matrices, heartType == InGameHud.HeartType.WITHERED ? heartType : InGameHud.HeartType.ABSORBING, p, q, i, false, bl2);
				}
			}

			if (blinking && r < health) {
				boolean bl3 = r + 1 == health;
				this.drawHeart(matrices, heartType, p, q, i, true, bl3);
			}

			if (r < lastHealth) {
				boolean bl4 = r + 1 == lastHealth;
				this.drawHeart(matrices, heartType, p, q, i, false, bl4);
			}
		}
	}

	private void drawHeart(MatrixStack matrices, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
		this.drawTexture(matrices, x, y, type.getU(halfHeart, blinking), v, 9, 9);
	}

	private void renderMountHealth(MatrixStack matrices) {
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
						this.drawTexture(matrices, s, m, 52 + r * 9, 9, 9, 9);
						if (p * 2 + 1 + n < j) {
							this.drawTexture(matrices, s, m, 88, 9, 9, 9);
						}

						if (p * 2 + 1 + n == j) {
							this.drawTexture(matrices, s, m, 97, 9, 9, 9);
						}
					}

					m -= 10;
				}
			}
		}
	}

	private void renderOverlay(Identifier texture, float opacity) {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
		RenderSystem.setShaderTexture(0, texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderSpyglassOverlay(float scale) {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, SPYGLASS_SCOPE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float f = (float)Math.min(this.scaledWidth, this.scaledHeight);
		float h = Math.min((float)this.scaledWidth / f, (float)this.scaledHeight / f) * scale;
		float i = f * h;
		float j = f * h;
		float k = ((float)this.scaledWidth - i) / 2.0F;
		float l = ((float)this.scaledHeight - j) / 2.0F;
		float m = k + i;
		float n = l + j;
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)k, (double)n, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)m, (double)n, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)m, (double)l, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex((double)k, (double)l, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.disableTexture();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)n, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0.0, (double)n, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0.0, (double)l, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)l, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0.0, (double)n, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)k, (double)n, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)k, (double)l, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0.0, (double)l, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)m, (double)n, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)n, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)l, -90.0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex((double)m, (double)l, -90.0).color(0, 0, 0, 255).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
			worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getSizeLerpTarget() - worldBorder.getSize())
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
			f = MathHelper.clamp(f, 0.0F, 1.0F);
			RenderSystem.setShaderColor(0.0F, f, f, 1.0F);
		} else {
			float g = this.vignetteDarkness;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			RenderSystem.setShaderColor(g, g, g, 1.0F);
		}

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
	}

	private void renderPortalOverlay(float nauseaStrength) {
		if (nauseaStrength < 1.0F) {
			nauseaStrength *= nauseaStrength;
			nauseaStrength *= nauseaStrength;
			nauseaStrength = nauseaStrength * 0.8F + 0.2F;
		}

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, nauseaStrength);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Sprite sprite = this.client.getBlockRenderManager().getModels().getSprite(Blocks.NETHER_PORTAL.getDefaultState());
		float f = sprite.getMinU();
		float g = sprite.getMinV();
		float h = sprite.getMaxU();
		float i = sprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0, (double)this.scaledHeight, -90.0).texture(f, i).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0).texture(h, i).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0, -90.0).texture(h, g).next();
		bufferBuilder.vertex(0.0, 0.0, -90.0).texture(f, g).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
		if (!stack.isEmpty()) {
			MatrixStack matrixStack = RenderSystem.getModelViewStack();
			float f = (float)stack.getCooldown() - tickDelta;
			if (f > 0.0F) {
				float g = 1.0F + f / 5.0F;
				matrixStack.push();
				matrixStack.translate((double)(x + 8), (double)(y + 12), 0.0);
				matrixStack.scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
				matrixStack.translate((double)(-(x + 8)), (double)(-(y + 12)), 0.0);
				RenderSystem.applyModelViewMatrix();
			}

			this.itemRenderer.renderInGuiWithOverrides(player, stack, x, y, seed);
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			if (f > 0.0F) {
				matrixStack.pop();
				RenderSystem.applyModelViewMatrix();
			}

			this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, stack, x, y);
		}
	}

	public void tick() {
		if (this.overlayRemaining > 0) {
			this.overlayRemaining--;
		}

		if (this.titleTotalTicks > 0) {
			this.titleTotalTicks--;
			if (this.titleTotalTicks <= 0) {
				this.title = null;
				this.subtitle = null;
			}
		}

		this.ticks++;
		Entity entity = this.client.getCameraEntity();
		if (entity != null) {
			this.updateVignetteDarkness(entity);
		}

		if (this.client.player != null) {
			ItemStack itemStack = this.client.player.getInventory().getMainHandStack();
			if (itemStack.isEmpty()) {
				this.heldItemTooltipFade = 0;
			} else if (this.currentStack.isEmpty() || !itemStack.isOf(this.currentStack.getItem()) || !itemStack.getName().equals(this.currentStack.getName())) {
				this.heldItemTooltipFade = 40;
			} else if (this.heldItemTooltipFade > 0) {
				this.heldItemTooltipFade--;
			}

			this.currentStack = itemStack;
		}
	}

	public void setRecordPlayingOverlay(Text description) {
		this.setOverlayMessage(new TranslatableText("record.nowPlaying", description), true);
	}

	public void setOverlayMessage(Text message, boolean tinted) {
		this.overlayMessage = message;
		this.overlayRemaining = 60;
		this.overlayTinted = tinted;
	}

	public void setTitleTicks(int fadeInTicks, int remainTicks, int fadeOutTicks) {
		if (fadeInTicks >= 0) {
			this.titleFadeInTicks = fadeInTicks;
		}

		if (remainTicks >= 0) {
			this.titleRemainTicks = remainTicks;
		}

		if (fadeOutTicks >= 0) {
			this.titleFadeOutTicks = fadeOutTicks;
		}

		if (this.titleTotalTicks > 0) {
			this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
		}
	}

	public void setSubtitle(Text subtitle) {
		this.subtitle = subtitle;
	}

	public void setTitle(Text title) {
		this.title = title;
		this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
	}

	public void clearTitle() {
		this.title = null;
		this.subtitle = null;
		this.titleTotalTicks = 0;
	}

	public UUID extractSender(Text message) {
		String string = TextVisitFactory.removeFormattingCodes(message);
		String string2 = StringUtils.substringBetween(string, "<", ">");
		return string2 == null ? Util.NIL_UUID : this.client.getSocialInteractionsManager().getUuid(string2);
	}

	public void addChatMessage(MessageType type, Text message, UUID sender) {
		if (!this.client.shouldBlockMessages(sender)) {
			if (!this.client.options.hideMatchedNames || !this.client.shouldBlockMessages(this.extractSender(message))) {
				for (ClientChatListener clientChatListener : (List)this.listeners.get(type)) {
					clientChatListener.onChatMessage(type, message, sender);
				}
			}
		}
	}

	public ChatHud getChatHud() {
		return this.chatHud;
	}

	public int getTicks() {
		return this.ticks;
	}

	public TextRenderer getTextRenderer() {
		return this.client.textRenderer;
	}

	public SpectatorHud getSpectatorHud() {
		return this.spectatorHud;
	}

	public PlayerListHud getPlayerListHud() {
		return this.playerListHud;
	}

	public void clear() {
		this.playerListHud.clear();
		this.bossBarHud.clear();
		this.client.getToastManager().clear();
		this.client.options.debugEnabled = false;
		this.chatHud.clear(true);
	}

	public BossBarHud getBossBarHud() {
		return this.bossBarHud;
	}

	public void resetDebugHudChunk() {
		this.debugHud.resetChunk();
	}

	static enum HeartType {
		CONTAINER(0, false),
		NORMAL(2, true),
		POISIONED(4, true),
		WITHERED(6, true),
		ABSORBING(8, false),
		FROZEN(9, false);

		private final int textureIndex;
		private final boolean hasBlinkingTexture;

		private HeartType(int textureIndex, boolean hasBlinkingTexture) {
			this.textureIndex = textureIndex;
			this.hasBlinkingTexture = hasBlinkingTexture;
		}

		public int getU(boolean halfHeart, boolean blinking) {
			int i;
			if (this == CONTAINER) {
				i = blinking ? 1 : 0;
			} else {
				int j = halfHeart ? 1 : 0;
				int k = this.hasBlinkingTexture && blinking ? 2 : 0;
				i = j + k;
			}

			return 16 + (this.textureIndex * 2 + i) * 9;
		}

		static InGameHud.HeartType fromPlayerState(PlayerEntity player) {
			InGameHud.HeartType heartType;
			if (player.hasStatusEffect(StatusEffects.POISON)) {
				heartType = POISIONED;
			} else if (player.hasStatusEffect(StatusEffects.WITHER)) {
				heartType = WITHERED;
			} else if (player.isFreezing()) {
				heartType = FROZEN;
			} else {
				heartType = NORMAL;
			}

			return heartType;
		}
	}
}
