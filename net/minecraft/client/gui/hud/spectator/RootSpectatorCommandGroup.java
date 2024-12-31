package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class RootSpectatorCommandGroup implements SpectatorMenuCommandGroup {
	private final List<SpectatorMenuCommand> elements = Lists.newArrayList();

	public RootSpectatorCommandGroup() {
		this.elements.add(new TeleportSpectatorMenu());
		this.elements.add(new TeamTeleportSpectatorMenu());
	}

	@Override
	public List<SpectatorMenuCommand> getCommands() {
		return this.elements;
	}

	@Override
	public Text getPrompt() {
		return new TranslatableText("spectatorMenu.root.prompt");
	}
}
