package net.minecraft.client.gui.hud.spectator;

import net.minecraft.text.Text;

public interface SpectatorMenuCommand {
	void use(SpectatorMenu menu);

	Text getName();

	void renderIcon(float brightness, int alpha);

	boolean isEnabled();
}
