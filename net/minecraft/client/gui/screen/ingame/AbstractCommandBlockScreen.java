package net.minecraft.client.gui.screen.ingame;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.world.CommandBlockExecutor;

public abstract class AbstractCommandBlockScreen extends Screen {
	protected TextFieldWidget consoleCommandTextField;
	protected TextFieldWidget previousOutputTextField;
	protected ButtonWidget doneButton;
	protected ButtonWidget cancelButton;
	protected ButtonWidget toggleTrackingOutputButton;
	protected boolean trackingOutput;
	private CommandSuggestor commandSuggestor;

	public AbstractCommandBlockScreen() {
		super(NarratorManager.EMPTY);
	}

	@Override
	public void tick() {
		this.consoleCommandTextField.tick();
	}

	abstract CommandBlockExecutor getCommandExecutor();

	abstract int getTrackOutputButtonHeight();

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.doneButton = this.addButton(
			new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.commitAndClose())
		);
		this.cancelButton = this.addButton(
			new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.onClose())
		);
		this.toggleTrackingOutputButton = this.addButton(
			new ButtonWidget(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, "O", buttonWidget -> {
				CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
				commandBlockExecutor.shouldTrackOutput(!commandBlockExecutor.isTrackingOutput());
				this.updateTrackedOutput();
			})
		);
		this.consoleCommandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.translate("advMode.command")) {
			@Override
			protected String getNarrationMessage() {
				return super.getNarrationMessage() + AbstractCommandBlockScreen.this.commandSuggestor.method_23958();
			}
		};
		this.consoleCommandTextField.setMaxLength(32500);
		this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
		this.children.add(this.consoleCommandTextField);
		this.previousOutputTextField = new TextFieldWidget(
			this.font, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, I18n.translate("advMode.previousOutput")
		);
		this.previousOutputTextField.setMaxLength(32500);
		this.previousOutputTextField.setEditable(false);
		this.previousOutputTextField.setText("-");
		this.children.add(this.previousOutputTextField);
		this.setInitialFocus(this.consoleCommandTextField);
		this.consoleCommandTextField.setSelected(true);
		this.commandSuggestor = new CommandSuggestor(this.minecraft, this, this.consoleCommandTextField, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
		this.commandSuggestor.setWindowActive(true);
		this.commandSuggestor.refresh();
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.consoleCommandTextField.getText();
		this.init(minecraftClient, i, j);
		this.consoleCommandTextField.setText(string);
		this.commandSuggestor.refresh();
	}

	protected void updateTrackedOutput() {
		if (this.getCommandExecutor().isTrackingOutput()) {
			this.toggleTrackingOutputButton.setMessage("O");
			this.previousOutputTextField.setText(this.getCommandExecutor().getLastOutput().getString());
		} else {
			this.toggleTrackingOutputButton.setMessage("X");
			this.previousOutputTextField.setText("-");
		}
	}

	protected void commitAndClose() {
		CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
		this.syncSettingsToServer(commandBlockExecutor);
		if (!commandBlockExecutor.isTrackingOutput()) {
			commandBlockExecutor.setLastOutput(null);
		}

		this.minecraft.openScreen(null);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	protected abstract void syncSettingsToServer(CommandBlockExecutor commandBlockExecutor);

	@Override
	public void onClose() {
		this.getCommandExecutor().shouldTrackOutput(this.trackingOutput);
		this.minecraft.openScreen(null);
	}

	private void onCommandChanged(String string) {
		this.commandSuggestor.refresh();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.commandSuggestor.keyPressed(i, j, k)) {
			return true;
		} else if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			return false;
		} else {
			this.commitAndClose();
			return true;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.commandSuggestor.mouseScrolled(f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.commandSuggestor.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, I18n.translate("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawString(this.font, I18n.translate("advMode.command"), this.width / 2 - 150, 40, 10526880);
		this.consoleCommandTextField.render(i, j, f);
		int k = 75;
		if (!this.previousOutputTextField.getText().isEmpty()) {
			k += 5 * 9 + 1 + this.getTrackOutputButtonHeight() - 135;
			this.drawString(this.font, I18n.translate("advMode.previousOutput"), this.width / 2 - 150, k + 4, 10526880);
			this.previousOutputTextField.render(i, j, f);
		}

		super.render(i, j, f);
		this.commandSuggestor.render(i, j);
	}
}
