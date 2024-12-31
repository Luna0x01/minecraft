package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_4122;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiplayerScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
	private final Screen parent;
	private MultiplayerServerListWidget serverListWidget;
	private ServerList serverList;
	private ButtonWidget editButton;
	private ButtonWidget joinButton;
	private ButtonWidget deleteButton;
	private boolean deleteSelected;
	private boolean addSelected;
	private boolean editSelected;
	private boolean directSelected;
	private String tooltipText;
	private ServerInfo selectedEntry;
	private LanServerQueryManager.LanServerEntryList lanServers;
	private LanServerQueryManager.LanServerDetector lanServerDetector;
	private boolean initialized;

	public MultiplayerScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public class_4122 getFocused() {
		return this.serverListWidget;
	}

	@Override
	protected void init() {
		super.init();
		this.client.field_19946.method_18191(true);
		if (this.initialized) {
			this.serverListWidget.updateBounds(this.width, this.height, 32, this.height - 64);
		} else {
			this.initialized = true;
			this.serverList = new ServerList(this.client);
			this.serverList.loadFile();
			this.lanServers = new LanServerQueryManager.LanServerEntryList();

			try {
				this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
				this.lanServerDetector.start();
			} catch (Exception var2) {
				LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
			}

			this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
			this.serverListWidget.setServers(this.serverList);
		}

		this.initButtons();
	}

	public void initButtons() {
		this.editButton = this.addButton(
			new ButtonWidget(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit")) {
				@Override
				public void method_18374(double d, double e) {
					EntryListWidget.Entry<?> entry = MultiplayerScreen.this.serverListWidget.getSelected() < 0
						? null
						: (EntryListWidget.Entry)MultiplayerScreen.this.serverListWidget.method_18423().get(MultiplayerScreen.this.serverListWidget.getSelected());
					MultiplayerScreen.this.editSelected = true;
					if (entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
						ServerInfo serverInfo = ((net.minecraft.client.gui.widget.ServerEntry)entry).getServer();
						MultiplayerScreen.this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
						MultiplayerScreen.this.selectedEntry.copyFrom(serverInfo);
						MultiplayerScreen.this.client.setScreen(new AddServerScreen(MultiplayerScreen.this, MultiplayerScreen.this.selectedEntry));
					}
				}
			}
		);
		this.deleteButton = this.addButton(
			new ButtonWidget(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete")) {
				@Override
				public void method_18374(double d, double e) {
					EntryListWidget.Entry<?> entry = MultiplayerScreen.this.serverListWidget.getSelected() < 0
						? null
						: (EntryListWidget.Entry)MultiplayerScreen.this.serverListWidget.method_18423().get(MultiplayerScreen.this.serverListWidget.getSelected());
					if (entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
						String string = ((net.minecraft.client.gui.widget.ServerEntry)entry).getServer().name;
						if (string != null) {
							MultiplayerScreen.this.deleteSelected = true;
							String string2 = I18n.translate("selectServer.deleteQuestion");
							String string3 = I18n.translate("selectServer.deleteWarning", string);
							String string4 = I18n.translate("selectServer.deleteButton");
							String string5 = I18n.translate("gui.cancel");
							ConfirmScreen confirmScreen = new ConfirmScreen(
								MultiplayerScreen.this, string2, string3, string4, string5, MultiplayerScreen.this.serverListWidget.getSelected()
							);
							MultiplayerScreen.this.client.setScreen(confirmScreen);
						}
					}
				}
			}
		);
		this.joinButton = this.addButton(new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select")) {
			@Override
			public void method_18374(double d, double e) {
				MultiplayerScreen.this.connect();
			}
		});
		this.addButton(new ButtonWidget(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct")) {
			@Override
			public void method_18374(double d, double e) {
				MultiplayerScreen.this.directSelected = true;
				MultiplayerScreen.this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
				MultiplayerScreen.this.client.setScreen(new DirectConnectScreen(MultiplayerScreen.this, MultiplayerScreen.this.selectedEntry));
			}
		});
		this.addButton(new ButtonWidget(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add")) {
			@Override
			public void method_18374(double d, double e) {
				MultiplayerScreen.this.addSelected = true;
				MultiplayerScreen.this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
				MultiplayerScreen.this.client.setScreen(new AddServerScreen(MultiplayerScreen.this, MultiplayerScreen.this.selectedEntry));
			}
		});
		this.addButton(new ButtonWidget(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh")) {
			@Override
			public void method_18374(double d, double e) {
				MultiplayerScreen.this.refresh();
			}
		});
		this.addButton(new ButtonWidget(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				MultiplayerScreen.this.client.setScreen(MultiplayerScreen.this.parent);
			}
		});
		this.field_20307.add(this.serverListWidget);
		this.selectEntry(this.serverListWidget.getSelected());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.lanServers.needsUpdate()) {
			List<ServerEntry> list = this.lanServers.getServers();
			this.lanServers.markClean();
			this.serverListWidget.addServers(list);
		}

		this.pinger.tick();
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
		if (this.lanServerDetector != null) {
			this.lanServerDetector.interrupt();
			this.lanServerDetector = null;
		}

		this.pinger.cancel();
	}

	private void refresh() {
		this.client.setScreen(new MultiplayerScreen(this.parent));
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		EntryListWidget.Entry<?> entry = this.serverListWidget.getSelected() < 0
			? null
			: (EntryListWidget.Entry)this.serverListWidget.method_18423().get(this.serverListWidget.getSelected());
		if (this.deleteSelected) {
			this.deleteSelected = false;
			if (bl && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
				this.serverList.remove(this.serverListWidget.getSelected());
				this.serverList.saveFile();
				this.serverListWidget.setSelected(-1);
				this.serverListWidget.setServers(this.serverList);
			}

			this.client.setScreen(this);
		} else if (this.directSelected) {
			this.directSelected = false;
			if (bl) {
				this.connect(this.selectedEntry);
			} else {
				this.client.setScreen(this);
			}
		} else if (this.addSelected) {
			this.addSelected = false;
			if (bl) {
				this.serverList.add(this.selectedEntry);
				this.serverList.saveFile();
				this.serverListWidget.setSelected(-1);
				this.serverListWidget.setServers(this.serverList);
			}

			this.client.setScreen(this);
		} else if (this.editSelected) {
			this.editSelected = false;
			if (bl && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
				ServerInfo serverInfo = ((net.minecraft.client.gui.widget.ServerEntry)entry).getServer();
				serverInfo.name = this.selectedEntry.name;
				serverInfo.address = this.selectedEntry.address;
				serverInfo.copyFrom(this.selectedEntry);
				this.serverList.saveFile();
				this.serverListWidget.setServers(this.serverList);
			}

			this.client.setScreen(this);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		int l = this.serverListWidget.getSelected();
		EntryListWidget.Entry<?> entry = l < 0 ? null : (EntryListWidget.Entry)this.serverListWidget.method_18423().get(l);
		if (i == 294) {
			this.refresh();
			return true;
		} else {
			if (l >= 0) {
				if (i == 265) {
					if (hasShiftDown()) {
						if (l > 0 && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
							this.serverList.swapEntries(l, l - 1);
							this.selectEntry(this.serverListWidget.getSelected() - 1);
							this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
							this.serverListWidget.setServers(this.serverList);
						}
					} else if (l > 0) {
						this.selectEntry(this.serverListWidget.getSelected() - 1);
						this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
						if (this.serverListWidget.method_18423().get(this.serverListWidget.getSelected()) instanceof LanScanWidget) {
							if (this.serverListWidget.getSelected() > 0) {
								this.selectEntry(this.serverListWidget.method_18423().size() - 1);
								this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
							} else {
								this.selectEntry(-1);
							}
						}
					} else {
						this.selectEntry(-1);
					}

					return true;
				}

				if (i == 264) {
					if (hasShiftDown()) {
						if (l < this.serverList.size() - 1) {
							this.serverList.swapEntries(l, l + 1);
							this.selectEntry(l + 1);
							this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
							this.serverListWidget.setServers(this.serverList);
						}
					} else if (l < this.serverListWidget.method_18423().size()) {
						this.selectEntry(this.serverListWidget.getSelected() + 1);
						this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
						if (this.serverListWidget.method_18423().get(this.serverListWidget.getSelected()) instanceof LanScanWidget) {
							if (this.serverListWidget.getSelected() < this.serverListWidget.method_18423().size() - 1) {
								this.selectEntry(this.serverListWidget.method_18423().size() + 1);
								this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
							} else {
								this.selectEntry(-1);
							}
						}
					} else {
						this.selectEntry(-1);
					}

					return true;
				}

				if (i == 257 || i == 335) {
					this.connect();
					return true;
				}
			}

			return super.keyPressed(i, j, k);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.tooltipText = null;
		this.renderBackground();
		this.serverListWidget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.title"), this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
		if (this.tooltipText != null) {
			this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.tooltipText)), mouseX, mouseY);
		}
	}

	public void connect() {
		EntryListWidget.Entry<?> entry = this.serverListWidget.getSelected() < 0
			? null
			: (EntryListWidget.Entry)this.serverListWidget.method_18423().get(this.serverListWidget.getSelected());
		if (entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
			this.connect(((net.minecraft.client.gui.widget.ServerEntry)entry).getServer());
		} else if (entry instanceof LanServerEntry) {
			ServerEntry serverEntry = ((LanServerEntry)entry).method_6786();
			this.connect(new ServerInfo(serverEntry.getName(), serverEntry.getAddress(), true));
		}
	}

	private void connect(ServerInfo entry) {
		this.client.setScreen(new ConnectScreen(this, this.client, entry));
	}

	public void selectEntry(int index) {
		this.serverListWidget.setSelected(index);
		EntryListWidget.Entry<?> entry = index < 0 ? null : (EntryListWidget.Entry)this.serverListWidget.method_18423().get(index);
		this.joinButton.active = false;
		this.editButton.active = false;
		this.deleteButton.active = false;
		if (entry != null && !(entry instanceof LanScanWidget)) {
			this.joinButton.active = true;
			if (entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
				this.editButton.active = true;
				this.deleteButton.active = true;
			}
		}
	}

	public MultiplayerServerListPinger getServerListPinger() {
		return this.pinger;
	}

	public void setTooltip(String text) {
		this.tooltipText = text;
	}

	public ServerList getServerList() {
		return this.serverList;
	}

	public boolean canSortUp(net.minecraft.client.gui.widget.ServerEntry entry, int index) {
		return index > 0;
	}

	public boolean canSortDown(net.minecraft.client.gui.widget.ServerEntry entry, int index) {
		return index < this.serverList.size() - 1;
	}

	public void sortUp(net.minecraft.client.gui.widget.ServerEntry entry, int index, boolean shiftPressed) {
		int i = shiftPressed ? 0 : index - 1;
		this.serverList.swapEntries(index, i);
		if (this.serverListWidget.getSelected() == index) {
			this.selectEntry(i);
		}

		this.serverListWidget.setServers(this.serverList);
	}

	public void sortDown(net.minecraft.client.gui.widget.ServerEntry entry, int index, boolean shiftPressed) {
		int i = shiftPressed ? this.serverList.size() - 1 : index + 1;
		this.serverList.swapEntries(index, i);
		if (this.serverListWidget.getSelected() == index) {
			this.selectEntry(i);
		}

		this.serverListWidget.setServers(this.serverList);
	}
}
