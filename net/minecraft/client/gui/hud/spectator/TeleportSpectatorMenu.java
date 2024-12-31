package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class TeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
	private static final Ordering<PlayerListEntry> ORDERING = Ordering.from(new Comparator<PlayerListEntry>() {
		public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
			return ComparisonChain.start().compare(playerListEntry.getProfile().getId(), playerListEntry2.getProfile().getId()).result();
		}
	});
	private final List<SpectatorMenuCommand> elements = Lists.newArrayList();

	public TeleportSpectatorMenu() {
		this(ORDERING.sortedCopy(MinecraftClient.getInstance().getNetworkHandler().getPlayerList()));
	}

	public TeleportSpectatorMenu(Collection<PlayerListEntry> collection) {
		for (PlayerListEntry playerListEntry : ORDERING.sortedCopy(collection)) {
			if (playerListEntry.getGameMode() != GameMode.SPECTATOR) {
				this.elements.add(new TeleportToSpecificPlayerSpectatorCommand(playerListEntry.getProfile()));
			}
		}
	}

	@Override
	public List<SpectatorMenuCommand> getCommands() {
		return this.elements;
	}

	@Override
	public Text getPrompt() {
		return new TranslatableText("spectatorMenu.teleport.prompt");
	}

	@Override
	public void use(SpectatorMenu menu) {
		menu.selectElement(this);
	}

	@Override
	public Text getName() {
		return new TranslatableText("spectatorMenu.teleport");
	}

	@Override
	public void renderIcon(float brightness, int alpha) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEXTURE);
		DrawableHelper.drawTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
	}

	@Override
	public boolean isEnabled() {
		return !this.elements.isEmpty();
	}
}
