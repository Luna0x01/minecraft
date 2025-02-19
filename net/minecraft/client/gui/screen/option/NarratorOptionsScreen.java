package net.minecraft.client.gui.screen.option;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public abstract class NarratorOptionsScreen extends GameOptionsScreen {
	private final Option[] options;
	@Nullable
	private ClickableWidget narratorButton;
	private ButtonListWidget buttonList;

	public NarratorOptionsScreen(Screen parent, GameOptions gameOptions, Text title, Option[] options) {
		super(parent, gameOptions, title);
		this.options = options;
	}

	@Override
	protected void init() {
		this.buttonList = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.buttonList.addAll(this.options);
		this.addSelectableChild(this.buttonList);
		this.initFooter();
		this.narratorButton = this.buttonList.getButtonFor(Option.NARRATOR);
		if (this.narratorButton != null) {
			this.narratorButton.active = NarratorManager.INSTANCE.isActive();
		}
	}

	protected void initFooter() {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.buttonList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
		List<OrderedText> list = getHoveredButtonTooltip(this.buttonList, mouseX, mouseY);
		this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
	}

	public void updateNarratorButtonText() {
		if (this.narratorButton instanceof CyclingButtonWidget) {
			((CyclingButtonWidget)this.narratorButton).setValue(this.gameOptions.narrator);
		}
	}
}
