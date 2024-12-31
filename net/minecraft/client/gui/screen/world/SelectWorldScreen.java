package net.minecraft.client.gui.screen.world;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.client.ClientException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Formatting;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectWorldScreen extends Screen implements IdentifiableBooleanConsumer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DateFormat dateFormat = new SimpleDateFormat();
	protected Screen parent;
	protected String title = "Select world";
	private boolean joinedWorld;
	private int selectedWorld;
	private List<LevelSummary> worlds;
	private SelectWorldScreen.WorldListWidget worldList;
	private String defaultWorldName;
	private String mustConvertText;
	private String[] gameModeTexts = new String[4];
	private boolean confimingDelete;
	private ButtonWidget deleteButton;
	private ButtonWidget selectButton;
	private ButtonWidget renameButton;
	private ButtonWidget recreateButton;

	public SelectWorldScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	public void init() {
		this.title = I18n.translate("selectWorld.title");

		try {
			this.loadWorlds();
		} catch (ClientException var2) {
			LOGGER.error("Couldn't load level list", var2);
			this.client.setScreen(new FatalErrorScreen("Unable to load worlds", var2.getMessage()));
			return;
		}

		this.defaultWorldName = I18n.translate("selectWorld.world");
		this.mustConvertText = I18n.translate("selectWorld.conversion");
		this.gameModeTexts[LevelInfo.GameMode.SURVIVAL.getId()] = I18n.translate("gameMode.survival");
		this.gameModeTexts[LevelInfo.GameMode.CREATIVE.getId()] = I18n.translate("gameMode.creative");
		this.gameModeTexts[LevelInfo.GameMode.ADVENTURE.getId()] = I18n.translate("gameMode.adventure");
		this.gameModeTexts[LevelInfo.GameMode.SPECTATOR.getId()] = I18n.translate("gameMode.spectator");
		this.worldList = new SelectWorldScreen.WorldListWidget(this.client);
		this.worldList.setButtonIds(4, 5);
		this.initButtons();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.worldList.handleMouse();
	}

	private void loadWorlds() throws ClientException {
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		this.worlds = levelStorageAccess.getLevelList();
		Collections.sort(this.worlds);
		this.selectedWorld = -1;
	}

	protected String getWorldFileName(int index) {
		return ((LevelSummary)this.worlds.get(index)).getFileName();
	}

	protected String getWorldName(int index) {
		String string = ((LevelSummary)this.worlds.get(index)).getDisplayName();
		if (StringUtils.isEmpty(string)) {
			string = I18n.translate("selectWorld.world") + " " + (index + 1);
		}

		return string;
	}

	public void initButtons() {
		this.buttons.add(this.selectButton = new ButtonWidget(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select")));
		this.buttons.add(new ButtonWidget(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create")));
		this.buttons.add(this.renameButton = new ButtonWidget(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.rename")));
		this.buttons.add(this.deleteButton = new ButtonWidget(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete")));
		this.buttons.add(this.recreateButton = new ButtonWidget(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate")));
		this.buttons.add(new ButtonWidget(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel")));
		this.selectButton.active = false;
		this.deleteButton.active = false;
		this.renameButton.active = false;
		this.recreateButton.active = false;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 2) {
				String string = this.getWorldName(this.selectedWorld);
				if (string != null) {
					this.confimingDelete = true;
					ConfirmScreen confirmScreen = createDeleteWarningScreen(this, string, this.selectedWorld);
					this.client.setScreen(confirmScreen);
				}
			} else if (button.id == 1) {
				this.joinWorld(this.selectedWorld);
			} else if (button.id == 3) {
				this.client.setScreen(new CreateWorldScreen(this));
			} else if (button.id == 6) {
				this.client.setScreen(new EditWorldScreen(this, this.getWorldFileName(this.selectedWorld)));
			} else if (button.id == 0) {
				this.client.setScreen(this.parent);
			} else if (button.id == 7) {
				CreateWorldScreen createWorldScreen = new CreateWorldScreen(this);
				SaveHandler saveHandler = this.client.getCurrentSave().createSaveHandler(this.getWorldFileName(this.selectedWorld), false);
				LevelProperties levelProperties = saveHandler.getLevelProperties();
				saveHandler.clear();
				createWorldScreen.copyWorld(levelProperties);
				this.client.setScreen(createWorldScreen);
			} else {
				this.worldList.buttonClicked(button);
			}
		}
	}

	public void joinWorld(int index) {
		this.client.setScreen(null);
		if (!this.joinedWorld) {
			this.joinedWorld = true;
			String string = this.getWorldFileName(index);
			if (string == null) {
				string = "World" + index;
			}

			String string2 = this.getWorldName(index);
			if (string2 == null) {
				string2 = "World" + index;
			}

			if (this.client.getCurrentSave().levelExists(string)) {
				this.client.startIntegratedServer(string, string2, null);
			}
		}
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		if (this.confimingDelete) {
			this.confimingDelete = false;
			if (confirmed) {
				LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
				levelStorageAccess.clearAll();
				levelStorageAccess.deleteLevel(this.getWorldFileName(id));

				try {
					this.loadWorlds();
				} catch (ClientException var5) {
					LOGGER.error("Couldn't load level list", var5);
				}
			}

			this.client.setScreen(this);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.worldList.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	public static ConfirmScreen createDeleteWarningScreen(IdentifiableBooleanConsumer consumer, String worldName, int id) {
		String string = I18n.translate("selectWorld.deleteQuestion");
		String string2 = "'" + worldName + "' " + I18n.translate("selectWorld.deleteWarning");
		String string3 = I18n.translate("selectWorld.deleteButton");
		String string4 = I18n.translate("gui.cancel");
		return new ConfirmScreen(consumer, string, string2, string3, string4, id);
	}

	class WorldListWidget extends ListWidget {
		public WorldListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, SelectWorldScreen.this.width, SelectWorldScreen.this.height, 32, SelectWorldScreen.this.height - 64, 36);
		}

		@Override
		protected int getEntryCount() {
			return SelectWorldScreen.this.worlds.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
			SelectWorldScreen.this.selectedWorld = index;
			boolean bl = SelectWorldScreen.this.selectedWorld >= 0 && SelectWorldScreen.this.selectedWorld < this.getEntryCount();
			SelectWorldScreen.this.selectButton.active = bl;
			SelectWorldScreen.this.deleteButton.active = bl;
			SelectWorldScreen.this.renameButton.active = bl;
			SelectWorldScreen.this.recreateButton.active = bl;
			if (doubleClick && bl) {
				SelectWorldScreen.this.joinWorld(index);
			}
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return index == SelectWorldScreen.this.selectedWorld;
		}

		@Override
		protected int getMaxPosition() {
			return SelectWorldScreen.this.worlds.size() * 36;
		}

		@Override
		protected void renderBackground() {
			SelectWorldScreen.this.renderBackground();
		}

		@Override
		protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
			LevelSummary levelSummary = (LevelSummary)SelectWorldScreen.this.worlds.get(index);
			String string = levelSummary.getDisplayName();
			if (StringUtils.isEmpty(string)) {
				string = SelectWorldScreen.this.defaultWorldName + " " + (index + 1);
			}

			String string2 = levelSummary.getFileName();
			string2 = string2 + " (" + SelectWorldScreen.this.dateFormat.format(new Date(levelSummary.getLastPlayed()));
			string2 = string2 + ")";
			String string3 = "";
			if (levelSummary.requiresConversion()) {
				string3 = SelectWorldScreen.this.mustConvertText + " " + string3;
			} else {
				string3 = SelectWorldScreen.this.gameModeTexts[levelSummary.getGameMode().getId()];
				if (levelSummary.isHardcore()) {
					string3 = Formatting.DARK_RED + I18n.translate("gameMode.hardcore") + Formatting.RESET;
				}

				if (levelSummary.cheatsEnabled()) {
					string3 = string3 + ", " + I18n.translate("selectWorld.cheats");
				}
			}

			SelectWorldScreen.this.drawWithShadow(SelectWorldScreen.this.textRenderer, string, x + 2, y + 1, 16777215);
			SelectWorldScreen.this.drawWithShadow(SelectWorldScreen.this.textRenderer, string2, x + 2, y + 12, 8421504);
			SelectWorldScreen.this.drawWithShadow(SelectWorldScreen.this.textRenderer, string3, x + 2, y + 12 + 10, 8421504);
		}
	}
}
