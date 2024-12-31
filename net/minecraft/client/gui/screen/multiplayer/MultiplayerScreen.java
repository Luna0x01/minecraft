package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class MultiplayerScreen extends Screen implements IdentifiableBooleanConsumer {
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
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
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
				LOGGER.warn("Unable to start LAN server detection: {}", new Object[]{var2.getMessage()});
			}

			this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
			this.serverListWidget.setServers(this.serverList);
		}

		this.initButtons();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.serverListWidget.handleMouse();
	}

	public void initButtons() {
		this.editButton = this.addButton(new ButtonWidget(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit")));
		this.deleteButton = this.addButton(new ButtonWidget(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete")));
		this.joinButton = this.addButton(new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select")));
		this.buttons.add(new ButtonWidget(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct")));
		this.buttons.add(new ButtonWidget(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add")));
		this.buttons.add(new ButtonWidget(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh")));
		this.buttons.add(new ButtonWidget(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel")));
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
		Keyboard.enableRepeatEvents(false);
		if (this.lanServerDetector != null) {
			this.lanServerDetector.interrupt();
			this.lanServerDetector = null;
		}

		this.pinger.cancel();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			EntryListWidget.Entry entry = this.serverListWidget.getSelected() < 0 ? null : this.serverListWidget.getEntry(this.serverListWidget.getSelected());
			if (button.id == 2 && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
				String string = ((net.minecraft.client.gui.widget.ServerEntry)entry).getServer().name;
				if (string != null) {
					this.deleteSelected = true;
					String string2 = I18n.translate("selectServer.deleteQuestion");
					String string3 = "'" + string + "' " + I18n.translate("selectServer.deleteWarning");
					String string4 = I18n.translate("selectServer.deleteButton");
					String string5 = I18n.translate("gui.cancel");
					ConfirmScreen confirmScreen = new ConfirmScreen(this, string2, string3, string4, string5, this.serverListWidget.getSelected());
					this.client.setScreen(confirmScreen);
				}
			} else if (button.id == 1) {
				this.connect();
			} else if (button.id == 4) {
				this.directSelected = true;
				this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
				this.client.setScreen(new DirectConnectScreen(this, this.selectedEntry));
			} else if (button.id == 3) {
				this.addSelected = true;
				this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName"), "", false);
				this.client.setScreen(new AddServerScreen(this, this.selectedEntry));
			} else if (button.id == 7 && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
				this.editSelected = true;
				ServerInfo serverInfo = ((net.minecraft.client.gui.widget.ServerEntry)entry).getServer();
				this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
				this.selectedEntry.copyFrom(serverInfo);
				this.client.setScreen(new AddServerScreen(this, this.selectedEntry));
			} else if (button.id == 0) {
				this.client.setScreen(this.parent);
			} else if (button.id == 8) {
				this.refresh();
			}
		}
	}

	private void refresh() {
		this.client.setScreen(new MultiplayerScreen(this.parent));
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		EntryListWidget.Entry entry = this.serverListWidget.getSelected() < 0 ? null : this.serverListWidget.getEntry(this.serverListWidget.getSelected());
		if (this.deleteSelected) {
			this.deleteSelected = false;
			if (confirmed && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
				this.serverList.remove(this.serverListWidget.getSelected());
				this.serverList.saveFile();
				this.serverListWidget.setSelected(-1);
				this.serverListWidget.setServers(this.serverList);
			}

			this.client.setScreen(this);
		} else if (this.directSelected) {
			this.directSelected = false;
			if (confirmed) {
				this.connect(this.selectedEntry);
			} else {
				this.client.setScreen(this);
			}
		} else if (this.addSelected) {
			this.addSelected = false;
			if (confirmed) {
				this.serverList.add(this.selectedEntry);
				this.serverList.saveFile();
				this.serverListWidget.setSelected(-1);
				this.serverListWidget.setServers(this.serverList);
			}

			this.client.setScreen(this);
		} else if (this.editSelected) {
			this.editSelected = false;
			if (confirmed && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
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
	protected void keyPressed(char id, int code) {
		int i = this.serverListWidget.getSelected();
		EntryListWidget.Entry entry = i < 0 ? null : this.serverListWidget.getEntry(i);
		if (code == 63) {
			this.refresh();
		} else {
			if (i >= 0) {
				if (code == 200) {
					if (hasShiftDown()) {
						if (i > 0 && entry instanceof net.minecraft.client.gui.widget.ServerEntry) {
							this.serverList.swapEntries(i, i - 1);
							this.selectEntry(this.serverListWidget.getSelected() - 1);
							this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
							this.serverListWidget.setServers(this.serverList);
						}
					} else if (i > 0) {
						this.selectEntry(this.serverListWidget.getSelected() - 1);
						this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
						if (this.serverListWidget.getEntry(this.serverListWidget.getSelected()) instanceof LanScanWidget) {
							if (this.serverListWidget.getSelected() > 0) {
								this.selectEntry(this.serverListWidget.getEntryCount() - 1);
								this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
							} else {
								this.selectEntry(-1);
							}
						}
					} else {
						this.selectEntry(-1);
					}
				} else if (code == 208) {
					if (hasShiftDown()) {
						if (i < this.serverList.size() - 1) {
							this.serverList.swapEntries(i, i + 1);
							this.selectEntry(i + 1);
							this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
							this.serverListWidget.setServers(this.serverList);
						}
					} else if (i < this.serverListWidget.getEntryCount()) {
						this.selectEntry(this.serverListWidget.getSelected() + 1);
						this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
						if (this.serverListWidget.getEntry(this.serverListWidget.getSelected()) instanceof LanScanWidget) {
							if (this.serverListWidget.getSelected() < this.serverListWidget.getEntryCount() - 1) {
								this.selectEntry(this.serverListWidget.getEntryCount() + 1);
								this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
							} else {
								this.selectEntry(-1);
							}
						}
					} else {
						this.selectEntry(-1);
					}
				} else if (code != 28 && code != 156) {
					super.keyPressed(id, code);
				} else {
					this.buttonClicked((ButtonWidget)this.buttons.get(2));
				}
			} else {
				super.keyPressed(id, code);
			}
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
		EntryListWidget.Entry entry = this.serverListWidget.getSelected() < 0 ? null : this.serverListWidget.getEntry(this.serverListWidget.getSelected());
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
		EntryListWidget.Entry entry = index < 0 ? null : this.serverListWidget.getEntry(index);
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

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.serverListWidget.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		this.serverListWidget.mouseReleased(mouseX, mouseY, button);
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
