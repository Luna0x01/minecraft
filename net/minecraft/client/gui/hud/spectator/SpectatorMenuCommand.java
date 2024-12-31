package net.minecraft.client.gui.hud.spectator;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface SpectatorMenuCommand {
	void use(SpectatorMenu menu);

	Text getName();

	void renderIcon(MatrixStack matrices, float f, int i);

	boolean isEnabled();
}
