package net.minecraft.client.gui.screen.ingame;

import net.minecraft.container.Container;

public interface ContainerProvider<T extends Container> {
	T getContainer();
}
