package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.minecraft.class_4156;
import net.minecraft.class_4372;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.lang3.StringUtils;

public class CreateWorldScreen extends Screen {
	private final Screen parent;
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
	private ButtonWidget field_20471;
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
	public NbtCompound field_20472 = new NbtCompound();
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
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.field_20471 = this.addButton(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("selectWorld.create")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.method_18847();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.client.setScreen(CreateWorldScreen.this.parent);
			}
		});
		this.gameModeButton = this.addButton(new ButtonWidget(2, this.width / 2 - 75, 115, 150, 20, I18n.translate("selectWorld.gameMode")) {
			@Override
			public void method_18374(double d, double e) {
				if ("survival".equals(CreateWorldScreen.this.gamemodeName)) {
					if (!CreateWorldScreen.this.cheatsEnabled) {
						CreateWorldScreen.this.tweakedCheats = false;
					}

					CreateWorldScreen.this.hardcore = false;
					CreateWorldScreen.this.gamemodeName = "hardcore";
					CreateWorldScreen.this.hardcore = true;
					CreateWorldScreen.this.allowCommandsButton.active = false;
					CreateWorldScreen.this.bonusChestButton.active = false;
					CreateWorldScreen.this.updateSettingsLabels();
				} else if ("hardcore".equals(CreateWorldScreen.this.gamemodeName)) {
					if (!CreateWorldScreen.this.cheatsEnabled) {
						CreateWorldScreen.this.tweakedCheats = true;
					}

					CreateWorldScreen.this.hardcore = false;
					CreateWorldScreen.this.gamemodeName = "creative";
					CreateWorldScreen.this.updateSettingsLabels();
					CreateWorldScreen.this.hardcore = false;
					CreateWorldScreen.this.allowCommandsButton.active = true;
					CreateWorldScreen.this.bonusChestButton.active = true;
				} else {
					if (!CreateWorldScreen.this.cheatsEnabled) {
						CreateWorldScreen.this.tweakedCheats = false;
					}

					CreateWorldScreen.this.gamemodeName = "survival";
					CreateWorldScreen.this.updateSettingsLabels();
					CreateWorldScreen.this.allowCommandsButton.active = true;
					CreateWorldScreen.this.bonusChestButton.active = true;
					CreateWorldScreen.this.hardcore = false;
				}

				CreateWorldScreen.this.updateSettingsLabels();
			}
		});
		this.moreWorldOptionsButton = this.addButton(new ButtonWidget(3, this.width / 2 - 75, 187, 150, 20, I18n.translate("selectWorld.moreWorldOptions")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.toggleMoreOptions();
			}
		});
		this.generateStructuresButton = this.addButton(new ButtonWidget(4, this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.mapFeatures")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.structures = !CreateWorldScreen.this.structures;
				CreateWorldScreen.this.updateSettingsLabels();
			}
		});
		this.generateStructuresButton.visible = false;
		this.bonusChestButton = this.addButton(new ButtonWidget(7, this.width / 2 + 5, 151, 150, 20, I18n.translate("selectWorld.bonusItems")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.bonusChest = !CreateWorldScreen.this.bonusChest;
				CreateWorldScreen.this.updateSettingsLabels();
			}
		});
		this.bonusChestButton.visible = false;
		this.mapTypeSwitchButton = this.addButton(new ButtonWidget(5, this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.mapType")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.generatorType++;
				if (CreateWorldScreen.this.generatorType >= LevelGeneratorType.TYPES.length) {
					CreateWorldScreen.this.generatorType = 0;
				}

				while (!CreateWorldScreen.this.isGeneratorTypeValid()) {
					CreateWorldScreen.this.generatorType++;
					if (CreateWorldScreen.this.generatorType >= LevelGeneratorType.TYPES.length) {
						CreateWorldScreen.this.generatorType = 0;
					}
				}

				CreateWorldScreen.this.field_20472 = new NbtCompound();
				CreateWorldScreen.this.updateSettingsLabels();
				CreateWorldScreen.this.setMoreOptionsOpen(CreateWorldScreen.this.moreOptionsOpen);
			}
		});
		this.mapTypeSwitchButton.visible = false;
		this.allowCommandsButton = this.addButton(new ButtonWidget(6, this.width / 2 - 155, 151, 150, 20, I18n.translate("selectWorld.allowCommands")) {
			@Override
			public void method_18374(double d, double e) {
				CreateWorldScreen.this.cheatsEnabled = true;
				CreateWorldScreen.this.tweakedCheats = !CreateWorldScreen.this.tweakedCheats;
				CreateWorldScreen.this.updateSettingsLabels();
			}
		});
		this.allowCommandsButton.visible = false;
		this.customizeButton = this.addButton(new ButtonWidget(8, this.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType")) {
			@Override
			public void method_18374(double d, double e) {
				if (LevelGeneratorType.TYPES[CreateWorldScreen.this.generatorType] == LevelGeneratorType.FLAT) {
					CreateWorldScreen.this.client.setScreen(new CustomizeFlatLevelScreen(CreateWorldScreen.this, CreateWorldScreen.this.field_20472));
				}

				if (LevelGeneratorType.TYPES[CreateWorldScreen.this.generatorType] == LevelGeneratorType.field_17505) {
					CreateWorldScreen.this.client.setScreen(new class_4156(CreateWorldScreen.this, CreateWorldScreen.this.field_20472));
				}
			}
		});
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
		this.client.field_19946.method_18191(false);
	}

	private void method_18847() {
		this.client.setScreen(null);
		if (!this.creatingLevel) {
			this.creatingLevel = true;
			long l = new Random().nextLong();
			String string = this.seedField.getText();
			if (!StringUtils.isEmpty(string)) {
				try {
					long m = Long.parseLong(string);
					if (m != 0L) {
						l = m;
					}
				} catch (NumberFormatException var6) {
					l = (long)string.hashCode();
				}
			}

			LevelInfo levelInfo = new LevelInfo(
				l, GameMode.setGameModeWithString(this.gamemodeName), this.structures, this.hardcore, LevelGeneratorType.TYPES[this.generatorType]
			);
			levelInfo.method_16395((JsonElement)Dynamic.convert(class_4372.field_21487, JsonOps.INSTANCE, this.field_20472));
			if (this.bonusChest && !this.hardcore) {
				levelInfo.setBonusChest();
			}

			if (this.tweakedCheats && !this.hardcore) {
				levelInfo.enableCommands();
			}

			this.client.startIntegratedServer(this.saveDirectoryName, this.levelNameField.getText().trim(), levelInfo);
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
			this.customizeButton.visible = this.moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType].method_16402();
		}

		this.updateSettingsLabels();
		if (this.moreOptionsOpen) {
			this.moreWorldOptionsButton.message = I18n.translate("gui.done");
		} else {
			this.moreWorldOptionsButton.message = I18n.translate("selectWorld.moreWorldOptions");
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.levelNameField.isFocused() && !this.moreOptionsOpen) {
			this.levelNameField.charTyped(c, i);
			this.levelName = this.levelNameField.getText();
			this.field_20471.active = !this.levelNameField.getText().isEmpty();
			this.updateSaveFolderName();
			return true;
		} else if (this.seedField.isFocused() && this.moreOptionsOpen) {
			this.seedField.charTyped(c, i);
			this.seed = this.seedField.getText();
			return true;
		} else {
			return super.charTyped(c, i);
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.levelNameField.isFocused() && !this.moreOptionsOpen) {
			this.levelNameField.keyPressed(i, j, k);
			this.levelName = this.levelNameField.getText();
			this.field_20471.active = !this.levelNameField.getText().isEmpty();
			this.updateSaveFolderName();
		} else if (this.seedField.isFocused() && this.moreOptionsOpen) {
			this.seedField.keyPressed(i, j, k);
			this.seed = this.seedField.getText();
		}

		if (this.field_20471.active && (i == 257 || i == 335)) {
			this.method_18847();
		}

		return true;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.moreOptionsOpen ? this.seedField.mouseClicked(d, e, i) : this.levelNameField.mouseClicked(d, e, i);
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

			this.seedField.method_18385(mouseX, mouseY, tickDelta);
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
			this.levelNameField.method_18385(mouseX, mouseY, tickDelta);
			this.drawCenteredString(this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2, 137, -6250336);
			this.drawCenteredString(this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2, 149, -6250336);
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	public void copyWorld(LevelProperties properties) {
		this.levelName = I18n.translate("selectWorld.newWorld.copyOf", properties.getLevelName());
		this.seed = properties.getSeed() + "";
		LevelGeneratorType levelGeneratorType = properties.getGeneratorType() == LevelGeneratorType.CUSTOMIZED
			? LevelGeneratorType.DEFAULT
			: properties.getGeneratorType();
		this.generatorType = levelGeneratorType.getId();
		this.field_20472 = properties.method_17950();
		this.structures = properties.hasStructures();
		this.tweakedCheats = properties.areCheatsEnabled();
		if (properties.isHardcore()) {
			this.gamemodeName = "hardcore";
		} else if (properties.getGamemode().canBeDamaged()) {
			this.gamemodeName = "survival";
		} else if (properties.getGamemode().isCreative()) {
			this.gamemodeName = "creative";
		}
	}
}
