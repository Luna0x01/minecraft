package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

public class PlayerListHud extends DrawableHelper {
	private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new PlayerListHud.EntryOrderComparator());
	public static final int MAX_ROWS = 20;
	public static final int HEART_OUTLINE_U = 16;
	public static final int BLINKING_HEART_OUTLINE_U = 25;
	public static final int HEART_U = 52;
	public static final int HALF_HEART_U = 61;
	public static final int GOLDEN_HEART_U = 160;
	public static final int HALF_GOLDEN_HEART_U = 169;
	public static final int BLINKING_HEART_U = 70;
	public static final int BLINKING_HALF_HEART_U = 79;
	private final MinecraftClient client;
	private final InGameHud inGameHud;
	@Nullable
	private Text footer;
	@Nullable
	private Text header;
	private long showTime;
	private boolean visible;

	public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
		this.client = client;
		this.inGameHud = inGameHud;
	}

	public Text getPlayerName(PlayerListEntry entry) {
		return entry.getDisplayName() != null
			? this.applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy())
			: this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
	}

	private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
		return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
	}

	public void setVisible(boolean visible) {
		if (visible && !this.visible) {
			this.showTime = Util.getMeasuringTimeMs();
		}

		this.visible = visible;
	}

	public void render(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
		int i = 0;
		int j = 0;

		for (PlayerListEntry playerListEntry : list) {
			int k = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
			i = Math.max(i, k);
			if (objective != null && objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
				k = this.client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective).getScore());
				j = Math.max(j, k);
			}
		}

		list = list.subList(0, Math.min(list.size(), 80));
		int l = list.size();
		int m = l;

		int n;
		for (n = 1; m > 20; m = (l + n - 1) / n) {
			n++;
		}

		boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
		int o;
		if (objective != null) {
			if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
				o = 90;
			} else {
				o = j;
			}
		} else {
			o = 0;
		}

		int r = Math.min(n * ((bl ? 9 : 0) + i + o + 13), scaledWindowWidth - 50) / n;
		int s = scaledWindowWidth / 2 - (r * n + (n - 1) * 5) / 2;
		int t = 10;
		int u = r * n + (n - 1) * 5;
		List<OrderedText> list2 = null;
		if (this.header != null) {
			list2 = this.client.textRenderer.wrapLines(this.header, scaledWindowWidth - 50);

			for (OrderedText orderedText : list2) {
				u = Math.max(u, this.client.textRenderer.getWidth(orderedText));
			}
		}

		List<OrderedText> list3 = null;
		if (this.footer != null) {
			list3 = this.client.textRenderer.wrapLines(this.footer, scaledWindowWidth - 50);

			for (OrderedText orderedText2 : list3) {
				u = Math.max(u, this.client.textRenderer.getWidth(orderedText2));
			}
		}

		if (list2 != null) {
			fill(matrices, scaledWindowWidth / 2 - u / 2 - 1, t - 1, scaledWindowWidth / 2 + u / 2 + 1, t + list2.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText3 : list2) {
				int v = this.client.textRenderer.getWidth(orderedText3);
				this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float)(scaledWindowWidth / 2 - v / 2), (float)t, -1);
				t += 9;
			}

			t++;
		}

		fill(matrices, scaledWindowWidth / 2 - u / 2 - 1, t - 1, scaledWindowWidth / 2 + u / 2 + 1, t + m * 9, Integer.MIN_VALUE);
		int w = this.client.options.getTextBackgroundColor(553648127);

		for (int x = 0; x < l; x++) {
			int y = x / m;
			int z = x % m;
			int aa = s + y * r + y * 5;
			int ab = t + z * 9;
			fill(matrices, aa, ab, aa + r, ab + 8, w);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			if (x < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(x);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null
						&& playerEntity.isPartVisible(PlayerModelPart.CAPE)
						&& ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					RenderSystem.setShaderTexture(0, playerListEntry2.getSkinTexture());
					int ac = 8 + (bl2 ? 8 : 0);
					int ad = 8 * (bl2 ? -1 : 1);
					DrawableHelper.drawTexture(matrices, aa, ab, 8, 8, 8.0F, (float)ac, 8, ad, 64, 64);
					if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
						int ae = 8 + (bl2 ? 8 : 0);
						int af = 8 * (bl2 ? -1 : 1);
						DrawableHelper.drawTexture(matrices, aa, ab, 8, 8, 40.0F, (float)ae, 8, af, 64, 64);
					}

					aa += 9;
				}

				this.client
					.textRenderer
					.drawWithShadow(
						matrices, this.getPlayerName(playerListEntry2), (float)aa, (float)ab, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
					);
				if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int ag = aa + i + 1;
					int ah = ag + o;
					if (ah - ag > 5) {
						this.renderScoreboardObjective(objective, ab, gameProfile.getName(), ag, ah, playerListEntry2, matrices);
					}
				}

				this.renderLatencyIcon(matrices, r, aa - (bl ? 9 : 0), ab, playerListEntry2);
			}
		}

		if (list3 != null) {
			t += m * 9 + 1;
			fill(matrices, scaledWindowWidth / 2 - u / 2 - 1, t - 1, scaledWindowWidth / 2 + u / 2 + 1, t + list3.size() * 9, Integer.MIN_VALUE);

			for (OrderedText orderedText4 : list3) {
				int ai = this.client.textRenderer.getWidth(orderedText4);
				this.client.textRenderer.drawWithShadow(matrices, orderedText4, (float)(scaledWindowWidth / 2 - ai / 2), (float)t, -1);
				t += 9;
			}
		}
	}

	protected void renderLatencyIcon(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
		int i = 0;
		int j;
		if (entry.getLatency() < 0) {
			j = 5;
		} else if (entry.getLatency() < 150) {
			j = 0;
		} else if (entry.getLatency() < 300) {
			j = 1;
		} else if (entry.getLatency() < 600) {
			j = 2;
		} else if (entry.getLatency() < 1000) {
			j = 3;
		} else {
			j = 4;
		}

		this.setZOffset(this.getZOffset() + 100);
		this.drawTexture(matrices, x + width - 11, y, 0, 176 + j * 8, 10, 8);
		this.setZOffset(this.getZOffset() - 100);
	}

	private void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int startX, int endX, PlayerListEntry entry, MatrixStack matrices) {
		int i = objective.getScoreboard().getPlayerScore(player, objective).getScore();
		if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
			long l = Util.getMeasuringTimeMs();
			if (this.showTime == entry.getShowTime()) {
				if (i < entry.getLastHealth()) {
					entry.setLastHealthTime(l);
					entry.setBlinkingHeartTime((long)(this.inGameHud.getTicks() + 20));
				} else if (i > entry.getLastHealth()) {
					entry.setLastHealthTime(l);
					entry.setBlinkingHeartTime((long)(this.inGameHud.getTicks() + 10));
				}
			}

			if (l - entry.getLastHealthTime() > 1000L || this.showTime != entry.getShowTime()) {
				entry.setLastHealth(i);
				entry.setHealth(i);
				entry.setLastHealthTime(l);
			}

			entry.setShowTime(this.showTime);
			entry.setLastHealth(i);
			int j = MathHelper.ceil((float)Math.max(i, entry.getHealth()) / 2.0F);
			int k = Math.max(MathHelper.ceil((float)(i / 2)), Math.max(MathHelper.ceil((float)(entry.getHealth() / 2)), 10));
			boolean bl = entry.getBlinkingHeartTime() > (long)this.inGameHud.getTicks()
				&& (entry.getBlinkingHeartTime() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
			if (j > 0) {
				int m = MathHelper.floor(Math.min((float)(endX - startX - 4) / (float)k, 9.0F));
				if (m > 3) {
					for (int n = j; n < k; n++) {
						this.drawTexture(matrices, startX + n * m, y, bl ? 25 : 16, 0, 9, 9);
					}

					for (int o = 0; o < j; o++) {
						this.drawTexture(matrices, startX + o * m, y, bl ? 25 : 16, 0, 9, 9);
						if (bl) {
							if (o * 2 + 1 < entry.getHealth()) {
								this.drawTexture(matrices, startX + o * m, y, 70, 0, 9, 9);
							}

							if (o * 2 + 1 == entry.getHealth()) {
								this.drawTexture(matrices, startX + o * m, y, 79, 0, 9, 9);
							}
						}

						if (o * 2 + 1 < i) {
							this.drawTexture(matrices, startX + o * m, y, o >= 10 ? 160 : 52, 0, 9, 9);
						}

						if (o * 2 + 1 == i) {
							this.drawTexture(matrices, startX + o * m, y, o >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float f = MathHelper.clamp((float)i / 20.0F, 0.0F, 1.0F);
					int p = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
					String string = (float)i / 2.0F + "";
					if (endX - this.client.textRenderer.getWidth(string + "hp") >= startX) {
						string = string + "hp";
					}

					this.client.textRenderer.drawWithShadow(matrices, string, (float)((endX + startX) / 2 - this.client.textRenderer.getWidth(string) / 2), (float)y, p);
				}
			}
		} else {
			String string2 = "" + Formatting.YELLOW + i;
			this.client.textRenderer.drawWithShadow(matrices, string2, (float)(endX - this.client.textRenderer.getWidth(string2)), (float)y, 16777215);
		}
	}

	public void setFooter(@Nullable Text footer) {
		this.footer = footer;
	}

	public void setHeader(@Nullable Text header) {
		this.header = header;
	}

	public void clear() {
		this.header = null;
		this.footer = null;
	}

	static class EntryOrderComparator implements Comparator<PlayerListEntry> {
		public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
			Team team = playerListEntry.getScoreboardTeam();
			Team team2 = playerListEntry2.getScoreboardTeam();
			return ComparisonChain.start()
				.compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR)
				.compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "")
				.compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase)
				.result();
		}
	}
}
