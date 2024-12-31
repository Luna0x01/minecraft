package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

public class PlayerListHud extends DrawableHelper {
	private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from(new PlayerListHud.EntryOrderComparator());
	private final MinecraftClient client;
	private final InGameHud inGameHud;
	private Text footer;
	private Text header;
	private long showTime;
	private boolean visible;

	public PlayerListHud(MinecraftClient minecraftClient, InGameHud inGameHud) {
		this.client = minecraftClient;
		this.inGameHud = inGameHud;
	}

	public String getPlayerName(PlayerListEntry playerEntry) {
		return playerEntry.getDisplayName() != null
			? playerEntry.getDisplayName().asFormattedString()
			: Team.decorateName(playerEntry.getScoreboardTeam(), playerEntry.getProfile().getName());
	}

	public void tick(boolean visible) {
		if (visible && !this.visible) {
			this.showTime = MinecraftClient.getTime();
		}

		this.visible = visible;
	}

	public void render(int width, Scoreboard scoreboard, @Nullable ScoreboardObjective playerListScoreboardObjective) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
		List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
		int i = 0;
		int j = 0;

		for (PlayerListEntry playerListEntry : list) {
			int k = this.client.textRenderer.getStringWidth(this.getPlayerName(playerListEntry));
			i = Math.max(i, k);
			if (playerListScoreboardObjective != null && playerListScoreboardObjective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
				k = this.client
					.textRenderer
					.getStringWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), playerListScoreboardObjective).getScore());
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

		boolean bl = this.client.isIntegratedServerRunning() || this.client.getNetworkHandler().getClientConnection().isEncrypted();
		int o;
		if (playerListScoreboardObjective != null) {
			if (playerListScoreboardObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
				o = 90;
			} else {
				o = j;
			}
		} else {
			o = 0;
		}

		int r = Math.min(n * ((bl ? 9 : 0) + i + o + 13), width - 50) / n;
		int s = width / 2 - (r * n + (n - 1) * 5) / 2;
		int t = 10;
		int u = r * n + (n - 1) * 5;
		List<String> list2 = null;
		if (this.header != null) {
			list2 = this.client.textRenderer.wrapLines(this.header.asFormattedString(), width - 50);

			for (String string : list2) {
				u = Math.max(u, this.client.textRenderer.getStringWidth(string));
			}
		}

		List<String> list3 = null;
		if (this.footer != null) {
			list3 = this.client.textRenderer.wrapLines(this.footer.asFormattedString(), width - 50);

			for (String string2 : list3) {
				u = Math.max(u, this.client.textRenderer.getStringWidth(string2));
			}
		}

		if (list2 != null) {
			fill(width / 2 - u / 2 - 1, t - 1, width / 2 + u / 2 + 1, t + list2.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);

			for (String string3 : list2) {
				int v = this.client.textRenderer.getStringWidth(string3);
				this.client.textRenderer.drawWithShadow(string3, (float)(width / 2 - v / 2), (float)t, -1);
				t += this.client.textRenderer.fontHeight;
			}

			t++;
		}

		fill(width / 2 - u / 2 - 1, t - 1, width / 2 + u / 2 + 1, t + m * 9, Integer.MIN_VALUE);

		for (int w = 0; w < l; w++) {
			int x = w / m;
			int y = w % m;
			int z = s + x * r + x * 5;
			int aa = t + y * 9;
			fill(z, aa, z + r, aa + 8, 553648127);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableAlphaTest();
			GlStateManager.enableBlend();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			if (w < list.size()) {
				PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(w);
				GameProfile gameProfile = playerListEntry2.getProfile();
				if (bl) {
					PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
					boolean bl2 = playerEntity != null
						&& playerEntity.isPartVisible(PlayerModelPart.CAPE)
						&& ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
					this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
					int ab = 8 + (bl2 ? 8 : 0);
					int ac = 8 * (bl2 ? -1 : 1);
					DrawableHelper.drawTexture(z, aa, 8.0F, (float)ab, 8, ac, 8, 8, 64.0F, 64.0F);
					if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
						int ad = 8 + (bl2 ? 8 : 0);
						int ae = 8 * (bl2 ? -1 : 1);
						DrawableHelper.drawTexture(z, aa, 40.0F, (float)ad, 8, ae, 8, 8, 64.0F, 64.0F);
					}

					z += 9;
				}

				String string4 = this.getPlayerName(playerListEntry2);
				if (playerListEntry2.getGameMode() == GameMode.SPECTATOR) {
					this.client.textRenderer.drawWithShadow(Formatting.ITALIC + string4, (float)z, (float)aa, -1862270977);
				} else {
					this.client.textRenderer.drawWithShadow(string4, (float)z, (float)aa, -1);
				}

				if (playerListScoreboardObjective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
					int af = z + i + 1;
					int ag = af + o;
					if (ag - af > 5) {
						this.renderScoreboardObjective(playerListScoreboardObjective, aa, gameProfile.getName(), af, ag, playerListEntry2);
					}
				}

				this.renderLatencyIcon(r, z - (bl ? 9 : 0), aa, playerListEntry2);
			}
		}

		if (list3 != null) {
			t += m * 9 + 1;
			fill(width / 2 - u / 2 - 1, t - 1, width / 2 + u / 2 + 1, t + list3.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);

			for (String string5 : list3) {
				int ah = this.client.textRenderer.getStringWidth(string5);
				this.client.textRenderer.drawWithShadow(string5, (float)(width / 2 - ah / 2), (float)t, -1);
				t += this.client.textRenderer.fontHeight;
			}
		}
	}

	protected void renderLatencyIcon(int width, int x, int y, PlayerListEntry playerEntry) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
		int i = 0;
		int j;
		if (playerEntry.getLatency() < 0) {
			j = 5;
		} else if (playerEntry.getLatency() < 150) {
			j = 0;
		} else if (playerEntry.getLatency() < 300) {
			j = 1;
		} else if (playerEntry.getLatency() < 600) {
			j = 2;
		} else if (playerEntry.getLatency() < 1000) {
			j = 3;
		} else {
			j = 4;
		}

		this.zOffset += 100.0F;
		this.drawTexture(x + width - 11, y, 0, 176 + j * 8, 10, 8);
		this.zOffset -= 100.0F;
	}

	private void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int startX, int endX, PlayerListEntry playerEntry) {
		int i = objective.getScoreboard().getPlayerScore(player, objective).getScore();
		if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
			this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
			if (this.showTime == playerEntry.getShowTime()) {
				if (i < playerEntry.getLastHealth()) {
					playerEntry.setLastHealthTime(MinecraftClient.getTime());
					playerEntry.setBlinkingHeartTime((long)(this.inGameHud.getTicks() + 20));
				} else if (i > playerEntry.getLastHealth()) {
					playerEntry.setLastHealthTime(MinecraftClient.getTime());
					playerEntry.setBlinkingHeartTime((long)(this.inGameHud.getTicks() + 10));
				}
			}

			if (MinecraftClient.getTime() - playerEntry.getLastHealthTime() > 1000L || this.showTime != playerEntry.getShowTime()) {
				playerEntry.setLastHealth(i);
				playerEntry.setHealth(i);
				playerEntry.setLastHealthTime(MinecraftClient.getTime());
			}

			playerEntry.setShowTime(this.showTime);
			playerEntry.setLastHealth(i);
			int j = MathHelper.ceil((float)Math.max(i, playerEntry.getHealth()) / 2.0F);
			int k = Math.max(MathHelper.ceil((float)(i / 2)), Math.max(MathHelper.ceil((float)(playerEntry.getHealth() / 2)), 10));
			boolean bl = playerEntry.getBlinkingHeartTime() > (long)this.inGameHud.getTicks()
				&& (playerEntry.getBlinkingHeartTime() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
			if (j > 0) {
				float f = Math.min((float)(endX - startX - 4) / (float)k, 9.0F);
				if (f > 3.0F) {
					for (int l = j; l < k; l++) {
						this.drawTexture((float)startX + (float)l * f, (float)y, bl ? 25 : 16, 0, 9, 9);
					}

					for (int m = 0; m < j; m++) {
						this.drawTexture((float)startX + (float)m * f, (float)y, bl ? 25 : 16, 0, 9, 9);
						if (bl) {
							if (m * 2 + 1 < playerEntry.getHealth()) {
								this.drawTexture((float)startX + (float)m * f, (float)y, 70, 0, 9, 9);
							}

							if (m * 2 + 1 == playerEntry.getHealth()) {
								this.drawTexture((float)startX + (float)m * f, (float)y, 79, 0, 9, 9);
							}
						}

						if (m * 2 + 1 < i) {
							this.drawTexture((float)startX + (float)m * f, (float)y, m >= 10 ? 160 : 52, 0, 9, 9);
						}

						if (m * 2 + 1 == i) {
							this.drawTexture((float)startX + (float)m * f, (float)y, m >= 10 ? 169 : 61, 0, 9, 9);
						}
					}
				} else {
					float g = MathHelper.clamp((float)i / 20.0F, 0.0F, 1.0F);
					int n = (int)((1.0F - g) * 255.0F) << 16 | (int)(g * 255.0F) << 8;
					String string = "" + (float)i / 2.0F;
					if (endX - this.client.textRenderer.getStringWidth(string + "hp") >= startX) {
						string = string + "hp";
					}

					this.client.textRenderer.drawWithShadow(string, (float)((endX + startX) / 2 - this.client.textRenderer.getStringWidth(string) / 2), (float)y, n);
				}
			}
		} else {
			String string2 = Formatting.YELLOW + "" + i;
			this.client.textRenderer.drawWithShadow(string2, (float)(endX - this.client.textRenderer.getStringWidth(string2)), (float)y, 16777215);
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
		private EntryOrderComparator() {
		}

		public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
			Team team = playerListEntry.getScoreboardTeam();
			Team team2 = playerListEntry2.getScoreboardTeam();
			return ComparisonChain.start()
				.compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR)
				.compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "")
				.compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName())
				.result();
		}
	}
}
