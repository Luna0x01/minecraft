package net.minecraft.realms;

import net.minecraft.client.gui.Element;

public class RealmsLabel extends RealmsGuiEventListener {
	private final RealmsLabelProxy proxy = new RealmsLabelProxy(this);
	private final String text;
	private final int x;
	private final int y;
	private final int color;

	public RealmsLabel(String string, int i, int j, int k) {
		this.text = string;
		this.x = i;
		this.y = j;
		this.color = k;
	}

	public void render(RealmsScreen realmsScreen) {
		realmsScreen.drawCenteredString(this.text, this.x, this.y, this.color);
	}

	@Override
	public Element getProxy() {
		return this.proxy;
	}

	public String getText() {
		return this.text;
	}
}
