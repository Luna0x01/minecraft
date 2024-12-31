package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.gui.widget.ServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.option.ServerList;

public class MultiplayerServerListWidget extends EntryListWidget {
	private final MultiplayerScreen parent;
	private final List<ServerEntry> servers = Lists.newArrayList();
	private final List<LanServerEntry> lanServers = Lists.newArrayList();
	private final EntryListWidget.Entry scanningWidget = new LanScanWidget();
	private int selectedEntry = -1;

	public MultiplayerServerListWidget(MultiplayerScreen multiplayerScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.parent = multiplayerScreen;
	}

	@Override
	public EntryListWidget.Entry getEntry(int index) {
		if (index < this.servers.size()) {
			return (EntryListWidget.Entry)this.servers.get(index);
		} else {
			index -= this.servers.size();
			return index == 0 ? this.scanningWidget : (EntryListWidget.Entry)this.lanServers.get(--index);
		}
	}

	@Override
	protected int getEntryCount() {
		return this.servers.size() + 1 + this.lanServers.size();
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
			this.servers.add(new ServerEntry(this.parent, servers.get(i)));
		}
	}

	public void addServers(List<LanServerQueryManager.LanServerInfo> servers) {
		this.lanServers.clear();

		for (LanServerQueryManager.LanServerInfo lanServerInfo : servers) {
			this.lanServers.add(new LanServerEntry(this.parent, lanServerInfo));
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 30;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 85;
	}
}
