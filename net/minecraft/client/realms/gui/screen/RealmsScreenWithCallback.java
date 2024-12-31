package net.minecraft.client.realms.gui.screen;

import javax.annotation.Nullable;
import net.minecraft.client.realms.dto.WorldTemplate;

public abstract class RealmsScreenWithCallback extends RealmsScreen {
	protected abstract void callback(@Nullable WorldTemplate template);
}
