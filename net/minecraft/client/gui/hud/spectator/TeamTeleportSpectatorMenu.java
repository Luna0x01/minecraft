package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TeamTeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private final List<SpectatorMenuCommand> commands = Lists.newArrayList();

	public TeamTeleportSpectatorMenu() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();

		for (Team team : minecraftClient.world.getScoreboard().getTeams()) {
			this.commands.add(new TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand(team));
		}
	}

	@Override
	public List<SpectatorMenuCommand> getCommands() {
		return this.commands;
	}

	@Override
	public Text getPrompt() {
		return new LiteralText("Select a team to teleport to");
	}

	@Override
	public void use(SpectatorMenu menu) {
		menu.selectElement(this);
	}

	@Override
	public Text getName() {
		return new LiteralText("Teleport to team member");
	}

	@Override
	public void renderIcon(float brightness, int alpha) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
		DrawableHelper.drawTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
	}

	@Override
	public boolean isEnabled() {
		for (SpectatorMenuCommand spectatorMenuCommand : this.commands) {
			if (spectatorMenuCommand.isEnabled()) {
				return true;
			}
		}

		return false;
	}

	class TeleportToSpecificTeamCommand implements SpectatorMenuCommand {
		private final Team team;
		private final Identifier skinId;
		private final List<PlayerListEntry> scoreboardEntries;

		public TeleportToSpecificTeamCommand(Team team) {
			this.team = team;
			this.scoreboardEntries = Lists.newArrayList();

			for (String string : team.getPlayerList()) {
				PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(string);
				if (playerListEntry != null) {
					this.scoreboardEntries.add(playerListEntry);
				}
			}

			if (!this.scoreboardEntries.isEmpty()) {
				String string2 = ((PlayerListEntry)this.scoreboardEntries.get(new Random().nextInt(this.scoreboardEntries.size()))).getProfile().getName();
				this.skinId = AbstractClientPlayerEntity.getSkinId(string2);
				AbstractClientPlayerEntity.loadSkin(this.skinId, string2);
			} else {
				this.skinId = DefaultSkinHelper.getTexture();
			}
		}

		@Override
		public void use(SpectatorMenu menu) {
			menu.selectElement(new TeleportSpectatorMenu(this.scoreboardEntries));
		}

		@Override
		public Text getName() {
			return new LiteralText(this.team.getDisplayName());
		}

		@Override
		public void renderIcon(float brightness, int alpha) {
			int i = -1;
			String string = TextRenderer.getFormattingOnly(this.team.getPrefix());
			if (string.length() >= 2) {
				i = MinecraftClient.getInstance().textRenderer.getColor(string.charAt(1));
			}

			if (i >= 0) {
				float f = (float)(i >> 16 & 0xFF) / 255.0F;
				float g = (float)(i >> 8 & 0xFF) / 255.0F;
				float h = (float)(i & 0xFF) / 255.0F;
				DrawableHelper.fill(1, 1, 15, 15, MathHelper.packRgb(f * brightness, g * brightness, h * brightness) | alpha << 24);
			}

			MinecraftClient.getInstance().getTextureManager().bindTexture(this.skinId);
			GlStateManager.color(brightness, brightness, brightness, (float)alpha / 255.0F);
			DrawableHelper.drawTexture(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
			DrawableHelper.drawTexture(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		}

		@Override
		public boolean isEnabled() {
			return !this.scoreboardEntries.isEmpty();
		}
	}
}
