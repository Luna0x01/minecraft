package net.minecraft.client.gui.screen.world;

import java.util.Random;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.CustomizeWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

public class CreateWorldScreen extends Screen {
	private Screen parent;
	private TextFieldWidget levelNameField;
	private TextFieldWidget seedField;
	private String saveDirectoryName;
	private String gamemodeName = "survival";
	private String lastGamemode;
	private boolean structures = true;
	private boolean tweakedCheats;
	private boolean cheatsEnabled;
	private boolean bonusChest;
	private boolean hardcore;
	private boolean creatingLevel;
	private boolean moreOptionsOpen;
	private ButtonWidget gameModeButton;
	private ButtonWidget moreWorldOptionsButton;
	private ButtonWidget generateStructuresButton;
	private ButtonWidget bonusChestButton;
	private ButtonWidget mapTypeSwitchButton;
	private ButtonWidget allowCommandsButton;
	private ButtonWidget customizeButton;
	private String firstGameModeDescriptionLine;
	private String secondGameModeDescriptionLine;
	private String seed;
	private String levelName;
	private int generatorType;
	public String generatorOptions = "";
	private static final String[] ILLEGAL_FOLDER_NAMES = new String[]{
		"CON",
		"COM",
		"PRN",
		"AUX",
		"CLOCK$",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9"
	};

	public CreateWorldScreen(Screen screen) {
		this.parent = screen;
		this.seed = "";
		this.levelName = I18n.translate("selectWorld.newWorld");
	}

	@Override
	public void tick() {
		this.levelNameField.tick();
		this.seedField.tick();
	}

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
		this.buttons.add(this.gameModeButton = new ButtonWidget(2, this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode")));
		this.buttons.add(this.moreWorldOptionsButton = new ButtonWidget(3, this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions")));
		this.buttons.add(this.generateStructuresButton = new ButtonWidget(4, this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures")));
		this.generateStructuresButton.visible = false;
		this.buttons.add(this.bonusChestButton = new ButtonWidget(7, this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems")));
		this.bonusChestButton.visible = false;
		this.buttons.add(this.mapTypeSwitchButton = new ButtonWidget(5, this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType")));
		this.mapTypeSwitchButton.visible = false;
		this.buttons.add(this.allowCommandsButton = new ButtonWidget(6, this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands")));
		this.allowCommandsButton.visible = false;
		this.buttons.add(this.customizeButton = new ButtonWidget(8, this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType")));
		this.customizeButton.visible = false;
		this.levelNameField = new TextFieldWidget(9, this.textRenderer, this.width / 2 - 100, 60, 200, 20);
		this.levelNameField.setFocused(true);
		this.levelNameField.setText(this.levelName);
		this.seedField = new TextFieldWidget(10, this.textRenderer, this.width / 2 - 100, 60, 200, 20);
		this.seedField.setText(this.seed);
		this.setMoreOptionsOpen(this.moreOptionsOpen);
		this.updateSaveFolderName();
		this.updateSettingsLabels();
	}

	private void updateSaveFolderName() {
		this.saveDirectoryName = this.levelNameField.getText().trim();

		for (char c : SharedConstants.INVALID_LEVEL_NAME_CHARS) {
			this.saveDirectoryName = this.saveDirectoryName.replace(c, '_');
		}

		if (StringUtils.isEmpty(this.saveDirectoryName)) {
			this.saveDirectoryName = "World";
		}

		this.saveDirectoryName = checkDirectoryName(this.client.getCurrentSave(), this.saveDirectoryName);
	}

	private void updateSettingsLabels() {
		this.gameModeButton.message = I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gamemodeName);
		this.firstGameModeDescriptionLine = I18n.translate("selectWorld.gameMode." + this.gamemodeName + ".line1");
		this.secondGameModeDescriptionLine = I18n.translate("selectWorld.gameMode." + this.gamemodeName + ".line2");
		this.generateStructuresButton.message = I18n.translate("selectWorld.mapFeatures") + " ";
		if (this.structures) {
			this.generateStructuresButton.message = this.generateStructuresButton.message + I18n.translate("options.on");
		} else {
			this.generateStructuresButton.message = this.generateStructuresButton.message + I18n.translate("options.off");
		}

		this.bonusChestButton.message = I18n.translate("selectWorld.bonusItems") + " ";
		if (this.bonusChest && !this.hardcore) {
			this.bonusChestButton.message = this.bonusChestButton.message + I18n.translate("options.on");
		} else {
			this.bonusChestButton.message = this.bonusChestButton.message + I18n.translate("options.off");
		}

		this.mapTypeSwitchButton.message = I18n.translate("selectWorld.mapType")
			+ " "
			+ I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getTranslationKey());
		this.allowCommandsButton.message = I18n.translate("selectWorld.allowCommands") + " ";
		if (this.tweakedCheats && !this.hardcore) {
			this.allowCommandsButton.message = this.allowCommandsButton.message + I18n.translate("options.on");
		} else {
			this.allowCommandsButton.message = this.allowCommandsButton.message + I18n.translate("options.off");
		}
	}

	public static String checkDirectoryName(LevelStorageAccess access, String directoryName) {
		directoryName = directoryName.replaceAll("[\\./\"]", "_");

		for (String string : ILLEGAL_FOLDER_NAMES) {
			if (directoryName.equalsIgnoreCase(string)) {
				directoryName = "_" + directoryName + "_";
			}
		}

		while (access.getLevelProperties(directoryName) != null) {
			directoryName = directoryName + "-";
		}

		return directoryName;
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.client.setScreen(this.parent);
			} else if (button.id == 0) {
				this.client.setScreen(null);
				if (this.creatingLevel) {
					return;
				}

				this.creatingLevel = true;
				long l = new Random().nextLong();
				String string = this.seedField.getText();
				if (!StringUtils.isEmpty(string)) {
					try {
						long m = Long.parseLong(string);
						if (m != 0L) {
							l = m;
						}
					} catch (NumberFormatException var7) {
						l = (long)string.hashCode();
					}
				}

				LevelInfo levelInfo = new LevelInfo(
					l, LevelInfo.GameMode.byName(this.gamemodeName), this.structures, this.hardcore, LevelGeneratorType.TYPES[this.generatorType]
				);
				levelInfo.setGeneratorOptions(this.generatorOptions);
				if (this.bonusChest && !this.hardcore) {
					levelInfo.setBonusChest();
				}

				if (this.tweakedCheats && !this.hardcore) {
					levelInfo.enableCommands();
				}

				this.client.startIntegratedServer(this.saveDirectoryName, this.levelNameField.getText().trim(), levelInfo);
			} else if (button.id == 3) {
				this.toggleMoreOptions();
			} else if (button.id == 2) {
				if (this.gamemodeName.equals("survival")) {
					if (!this.cheatsEnabled) {
						this.tweakedCheats = false;
					}

					this.hardcore = false;
					this.gamemodeName = "hardcore";
					this.hardcore = true;
					this.allowCommandsButton.active = false;
					this.bonusChestButton.active = false;
					this.updateSettingsLabels();
				} else if (this.gamemodeName.equals("hardcore")) {
					if (!this.cheatsEnabled) {
						this.tweakedCheats = true;
					}

					this.hardcore = false;
					this.gamemodeName = "creative";
					this.updateSettingsLabels();
					this.hardcore = false;
					this.allowCommandsButton.active = true;
					this.bonusChestButton.active = true;
				} else {
					if (!this.cheatsEnabled) {
						this.tweakedCheats = false;
					}

					this.gamemodeName = "survival";
					this.updateSettingsLabels();
					this.allowCommandsButton.active = true;
					this.bonusChestButton.active = true;
					this.hardcore = false;
				}

				this.updateSettingsLabels();
			} else if (button.id == 4) {
				this.structures = !this.structures;
				this.updateSettingsLabels();
			} else if (button.id == 7) {
				this.bonusChest = !this.bonusChest;
				this.updateSettingsLabels();
			} else if (button.id == 5) {
				this.generatorType++;
				if (this.generatorType >= LevelGeneratorType.TYPES.length) {
					this.generatorType = 0;
				}

				while (!this.isGeneratorTypeValid()) {
					this.generatorType++;
					if (this.generatorType >= LevelGeneratorType.TYPES.length) {
						this.generatorType = 0;
					}
				}

				this.generatorOptions = "";
				this.updateSettingsLabels();
				this.setMoreOptionsOpen(this.moreOptionsOpen);
			} else if (button.id == 6) {
				this.cheatsEnabled = true;
				this.tweakedCheats = !this.tweakedCheats;
				this.updateSettingsLabels();
			} else if (button.id == 8) {
				if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT) {
					this.client.setScreen(new CustomizeFlatLevelScreen(this, this.generatorOptions));
				} else {
					this.client.setScreen(new CustomizeWorldScreen(this, this.generatorOptions));
				}
			}
		}
	}

	private boolean isGeneratorTypeValid() {
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[this.generatorType];
		if (levelGeneratorType == null || !levelGeneratorType.isVisible()) {
			return false;
		} else {
			return levelGeneratorType == LevelGeneratorType.DEBUG ? hasShiftDown() : true;
		}
	}

	private void toggleMoreOptions() {
		this.setMoreOptionsOpen(!this.moreOptionsOpen);
	}

	private void setMoreOptionsOpen(boolean moreOptionsOpen) {
		this.moreOptionsOpen = moreOptionsOpen;
		if (LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.DEBUG) {
			this.gameModeButton.visible = !this.moreOptionsOpen;
			this.gameModeButton.active = false;
			if (this.lastGamemode == null) {
				this.lastGamemode = this.gamemodeName;
			}

			this.gamemodeName = "spectator";
			this.generateStructuresButton.visible = false;
			this.bonusChestButton.visible = false;
			this.mapTypeSwitchButton.visible = this.moreOptionsOpen;
			this.allowCommandsButton.visible = false;
			this.customizeButton.visible = false;
		} else {
			this.gameModeButton.visible = !this.moreOptionsOpen;
			this.gameModeButton.active = true;
			if (this.lastGamemode != null) {
				this.gamemodeName = this.lastGamemode;
				this.lastGamemode = null;
			}

			this.generateStructuresButton.visible = this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType] != LevelGeneratorType.CUSTOMIZED;
			this.bonusChestButton.visible = this.moreOptionsOpen;
			this.mapTypeSwitchButton.visible = this.moreOptionsOpen;
			this.allowCommandsButton.visible = this.moreOptionsOpen;
			this.customizeButton.visible = this.moreOptionsOpen
				&& (
					LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.FLAT || LevelGeneratorType.TYPES[this.generatorType] == LevelGeneratorType.CUSTOMIZED
				);
		}

		this.updateSettingsLabels();
		if (this.moreOptionsOpen) {
			this.moreWorldOptionsButton.message = I18n.translate("gui.done");
		} else {
			this.moreWorldOptionsButton.message = I18n.translate("selectWorld.moreWorldOptions");
		}
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (this.levelNameField.isFocused() && !this.moreOptionsOpen) {
			this.levelNameField.keyPressed(id, code);
			this.levelName = this.levelNameField.getText();
		} else if (this.seedField.isFocused() && this.moreOptionsOpen) {
			this.seedField.keyPressed(id, code);
			this.seed = this.seedField.getText();
		}

		if (code == 28 || code == 156) {
			this.buttonClicked((ButtonWidget)this.buttons.get(0));
		}

		((ButtonWidget)this.buttons.get(0)).active = !this.levelNameField.getText().isEmpty();
		this.updateSaveFolderName();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		if (this.moreOptionsOpen) {
			this.seedField.mouseClicked(mouseX, mouseY, button);
		} else {
			this.levelNameField.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("selectWorld.create"), this.width / 2, 20, -1);
		if (this.moreOptionsOpen) {
			this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
			if (this.generateStructuresButton.visible) {
				this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.allowCommandsButton.visible) {
				this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.seedField.render();
			if (LevelGeneratorType.TYPES[this.generatorType].hasInfo()) {
				this.textRenderer
					.drawTrimmed(
						I18n.translate(LevelGeneratorType.TYPES[this.generatorType].getInfoTranslationKey()),
						this.mapTypeSwitchButton.x + 2,
						this.mapTypeSwitchButton.y + 22,
						this.mapTypeSwitchButton.getWidth(),
						10526880
					);
			}
		} else {
			this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.resultFolder") + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336);
			this.levelNameField.render();
			this.drawWithShadow(this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 100, 137, -6250336);
			this.drawWithShadow(this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 100, 149, -6250336);
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	public void copyWorld(LevelProperties properties) {
		this.levelName = I18n.translate("selectWorld.newWorld.copyOf", properties.getLevelName());
		this.seed = properties.getSeed() + "";
		this.generatorType = properties.getGeneratorType().getId();
		this.generatorOptions = properties.getGeneratorOptions();
		this.structures = properties.hasStructures();
		this.tweakedCheats = properties.areCheatsEnabled();
		if (properties.isHardcore()) {
			this.gamemodeName = "hardcore";
		} else if (properties.getGameMode().isSurvivalLike()) {
			this.gamemodeName = "survival";
		} else if (properties.getGameMode().isCreative()) {
			this.gamemodeName = "creative";
		}
	}
}
