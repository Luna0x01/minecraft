package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.network.ServerEntry;
import net.minecraft.client.option.ServerList;

public class MultiplayerServerListWidget extends EntryListWidget<MultiplayerServerListWidget.class_4169> {
	private final MultiplayerScreen parent;
	private final List<net.minecraft.client.gui.widget.ServerEntry> servers = Lists.newArrayList();
	private final MultiplayerServerListWidget.class_4169 field_20442 = new LanScanWidget();
	private final List<LanServerEntry> lanServers = Lists.newArrayList();
	private int selectedEntry = -1;

	private void method_18787() {
		this.method_18399();
		this.servers.forEach(this::method_18398);
		this.method_18398(this.field_20442);
		this.lanServers.forEach(this::method_18398);
	}

	public MultiplayerServerListWidget(MultiplayerScreen multiplayerScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.parent = multiplayerScreen;
	}

	public void setSelected(int index) {
		this.selectedEntry = index;
	}

	@Override
	protected boolean isEntrySelected(int index) {
		return index == this.selectedEntry;
	}

	public int getSelected() {
		return this.selectedEntry;
	}

	public void setServers(ServerList servers) {
		this.servers.clear();

		for (int i = 0; i < servers.size(); i++) {
			this.servers.add(new net.minecraft.client.gui.widget.ServerEntry(this.parent, servers.get(i)));
		}

		this.method_18787();
	}

	public void addServers(List<ServerEntry> servers) {
		this.lanServers.clear();

		for (ServerEntry serverEntry : servers) {
			this.lanServers.add(new LanServerEntry(this.parent, serverEntry));
		}

		this.method_18787();
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 30;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}

	public abstract static class class_4169 extends EntryListWidget.Entry<MultiplayerServerListWidget.class_4169> {
	}
}
