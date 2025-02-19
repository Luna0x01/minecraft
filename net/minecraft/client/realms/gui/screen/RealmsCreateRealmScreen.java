package net.minecraft.client.realms.gui.screen;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class RealmsCreateRealmScreen extends RealmsScreen {
	private static final Text WORLD_NAME_TEXT = new TranslatableText("mco.configure.world.name");
	private static final Text WORLD_DESCRIPTION_TEXT = new TranslatableText("mco.configure.world.description");
	private final RealmsServer server;
	private final RealmsMainScreen parent;
	private TextFieldWidget nameBox;
	private TextFieldWidget descriptionBox;
	private ButtonWidget createButton;

	public RealmsCreateRealmScreen(RealmsServer server, RealmsMainScreen parent) {
		super(new TranslatableText("mco.selectServer.create"));
		this.server = server;
		this.parent = parent;
	}

	@Override
	public void tick() {
		if (this.nameBox != null) {
			this.nameBox.tick();
		}

		if (this.descriptionBox != null) {
			this.descriptionBox.tick();
		}
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.createButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, new TranslatableText("mco.create.world"), button -> this.createWorld())
		);
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, ScreenTexts.CANCEL, button -> this.client.openScreen(this.parent))
		);
		this.createButton.active = false;
		this.nameBox = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, 65, 200, 20, null, new TranslatableText("mco.configure.world.name"));
		this.addSelectableChild(this.nameBox);
		this.setInitialFocus(this.nameBox);
		this.descriptionBox = new TextFieldWidget(
			this.client.textRenderer, this.width / 2 - 100, 115, 200, 20, null, new TranslatableText("mco.configure.world.description")
		);
		this.addSelectableChild(this.descriptionBox);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		boolean bl = super.charTyped(chr, modifiers);
		this.createButton.active = this.valid();
		return bl;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			boolean bl = super.keyPressed(keyCode, scanCode, modifiers);
			this.createButton.active = this.valid();
			return bl;
		}
	}

	private void createWorld() {
		if (this.valid()) {
			RealmsResetWorldScreen realmsResetWorldScreen = new RealmsResetWorldScreen(
				this.parent,
				this.server,
				new TranslatableText("mco.selectServer.create"),
				new TranslatableText("mco.create.world.subtitle"),
				10526880,
				new TranslatableText("mco.create.world.skip"),
				() -> this.client.execute(() -> this.client.openScreen(this.parent.newScreen())),
				() -> this.client.openScreen(this.parent.newScreen())
			);
			realmsResetWorldScreen.setResetTitle(new TranslatableText("mco.create.world.reset.title"));
			this.client
				.openScreen(
					new RealmsLongRunningMcoTaskScreen(
						this.parent, new WorldCreationTask(this.server.id, this.nameBox.getText(), this.descriptionBox.getText(), realmsResetWorldScreen)
					)
				);
		}
	}

	private boolean valid() {
		return !this.nameBox.getText().trim().isEmpty();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 11, 16777215);
		this.textRenderer.draw(matrices, WORLD_NAME_TEXT, (float)(this.width / 2 - 100), 52.0F, 10526880);
		this.textRenderer.draw(matrices, WORLD_DESCRIPTION_TEXT, (float)(this.width / 2 - 100), 102.0F, 10526880);
		if (this.nameBox != null) {
			this.nameBox.render(matrices, mouseX, mouseY, delta);
		}

		if (this.descriptionBox != null) {
			this.descriptionBox.render(matrices, mouseX, mouseY, delta);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
