package net.minecraft.client.gui.screen.world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.class_3260;
import net.minecraft.class_3264;
import net.minecraft.class_4153;
import net.minecraft.class_4179;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.commons.io.FileUtils;

public class EditWorldScreen extends Screen {
	private ButtonWidget field_20482;
	private final IdentifiableBooleanConsumer field_20483;
	private TextFieldWidget searchField;
	private final String levelName;

	public EditWorldScreen(IdentifiableBooleanConsumer identifiableBooleanConsumer, String string) {
		this.field_20483 = identifiableBooleanConsumer;
		this.levelName = string;
	}

	@Override
	public void tick() {
		this.searchField.tick();
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(3, this.width / 2 - 100, this.height / 4 + 24 + 5, I18n.translate("selectWorld.edit.resetIcon")) {
				@Override
				public void method_18374(double d, double e) {
					LevelStorageAccess levelStorageAccess = EditWorldScreen.this.client.getCurrentSave();
					FileUtils.deleteQuietly(levelStorageAccess.method_11957(EditWorldScreen.this.levelName, "icon.png"));
					this.active = false;
				}
			}
		);
		this.addButton(new ButtonWidget(4, this.width / 2 - 100, this.height / 4 + 48 + 5, I18n.translate("selectWorld.edit.openFolder")) {
			@Override
			public void method_18374(double d, double e) {
				LevelStorageAccess levelStorageAccess = EditWorldScreen.this.client.getCurrentSave();
				Util.getOperatingSystem().method_20235(levelStorageAccess.method_11957(EditWorldScreen.this.levelName, "icon.png").getParentFile());
			}
		});
		this.addButton(new ButtonWidget(5, this.width / 2 - 100, this.height / 4 + 72 + 5, I18n.translate("selectWorld.edit.backup")) {
			@Override
			public void method_18374(double d, double e) {
				LevelStorageAccess levelStorageAccess = EditWorldScreen.this.client.getCurrentSave();
				EditWorldScreen.method_18857(levelStorageAccess, EditWorldScreen.this.levelName);
				EditWorldScreen.this.field_20483.confirmResult(false, 0);
			}
		});
		this.addButton(new ButtonWidget(6, this.width / 2 - 100, this.height / 4 + 96 + 5, I18n.translate("selectWorld.edit.backupFolder")) {
			@Override
			public void method_18374(double d, double e) {
				LevelStorageAccess levelStorageAccess = EditWorldScreen.this.client.getCurrentSave();
				Path path = levelStorageAccess.method_17968();

				try {
					Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
				} catch (IOException var8) {
					throw new RuntimeException(var8);
				}

				Util.getOperatingSystem().method_20235(path.toFile());
			}
		});
		this.addButton(
			new ButtonWidget(7, this.width / 2 - 100, this.height / 4 + 120 + 5, I18n.translate("selectWorld.edit.optimize")) {
				@Override
				public void method_18374(double d, double e) {
					EditWorldScreen.this.client
						.setScreen(
							new class_4153(
								EditWorldScreen.this,
								bl -> {
									if (bl) {
										EditWorldScreen.method_18857(EditWorldScreen.this.client.getCurrentSave(), EditWorldScreen.this.levelName);
									}

									EditWorldScreen.this.client
										.setScreen(new class_4179(EditWorldScreen.this.field_20483, EditWorldScreen.this.levelName, EditWorldScreen.this.client.getCurrentSave()));
								},
								I18n.translate("optimizeWorld.confirm.title"),
								I18n.translate("optimizeWorld.confirm.description")
							)
						);
				}
			}
		);
		this.field_20482 = this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.translate("selectWorld.edit.save")) {
			@Override
			public void method_18374(double d, double e) {
				EditWorldScreen.this.method_18865();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				EditWorldScreen.this.field_20483.confirmResult(false, 0);
			}
		});
		buttonWidget.active = this.client.getCurrentSave().method_11957(this.levelName, "icon.png").isFile();
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		LevelProperties levelProperties = levelStorageAccess.getLevelProperties(this.levelName);
		String string = levelProperties == null ? "" : levelProperties.getLevelName();
		this.searchField = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 100, 53, 200, 20);
		this.searchField.setFocused(true);
		this.searchField.setText(string);
		this.field_20307.add(this.searchField);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.searchField.getText();
		this.init(client, width, height);
		this.searchField.setText(string);
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
	}

	private void method_18865() {
		LevelStorageAccess levelStorageAccess = this.client.getCurrentSave();
		levelStorageAccess.renameLevel(this.levelName, this.searchField.getText().trim());
		this.field_20483.confirmResult(true, 0);
	}

	public static void method_18857(LevelStorageAccess levelStorageAccess, String string) {
		class_3264 lv = MinecraftClient.getInstance().method_14462();
		long l = 0L;
		IOException iOException = null;

		try {
			l = levelStorageAccess.method_17970(string);
		} catch (IOException var8) {
			iOException = var8;
		}

		Text text;
		Text text2;
		if (iOException != null) {
			text = new TranslatableText("selectWorld.edit.backupFailed");
			text2 = new LiteralText(iOException.getMessage());
		} else {
			text = new TranslatableText("selectWorld.edit.backupCreated", string);
			text2 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
		}

		lv.method_14491(new class_3260(class_3260.class_3261.WORLD_BACKUP, text, text2));
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.searchField.charTyped(c, i)) {
			this.field_20482.active = !this.searchField.getText().trim().isEmpty();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.searchField.keyPressed(i, j, k)) {
			this.field_20482.active = !this.searchField.getText().trim().isEmpty();
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.method_18865();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("selectWorld.edit.title"), this.width / 2, 20, 16777215);
		this.drawWithShadow(this.textRenderer, I18n.translate("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
		this.searchField.method_18385(mouseX, mouseY, tickDelta);
		super.render(mouseX, mouseY, tickDelta);
	}
}
