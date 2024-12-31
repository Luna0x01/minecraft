package net.minecraft.realms;

import net.minecraft.client.gui.Element;

public class RealmsLabelProxy implements Element {
	private final RealmsLabel label;

	public RealmsLabelProxy(RealmsLabel realmsLabel) {
		this.label = realmsLabel;
	}

	public RealmsLabel getLabel() {
		return this.label;
	}
}
