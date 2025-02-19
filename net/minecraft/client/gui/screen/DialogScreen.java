package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class DialogScreen extends Screen {
	private static final int field_32260 = 20;
	private static final int field_32261 = 5;
	private static final int field_32262 = 20;
	private final Text narrationMessage;
	private final StringVisitable message;
	private final ImmutableList<DialogScreen.ChoiceButton> choiceButtons;
	private MultilineText lines = MultilineText.EMPTY;
	private int linesY;
	private int buttonWidth;

	protected DialogScreen(Text title, List<Text> messages, ImmutableList<DialogScreen.ChoiceButton> choiceButtons) {
		super(title);
		this.message = StringVisitable.concat(messages);
		this.narrationMessage = ScreenTexts.joinSentences(title, Texts.join(messages, LiteralText.EMPTY));
		this.choiceButtons = choiceButtons;
	}

	@Override
	public Text getNarratedTitle() {
		return this.narrationMessage;
	}

	@Override
	public void init() {
		UnmodifiableIterator i = this.choiceButtons.iterator();

		while (i.hasNext()) {
			DialogScreen.ChoiceButton choiceButton = (DialogScreen.ChoiceButton)i.next();
			this.buttonWidth = Math.max(this.buttonWidth, 20 + this.textRenderer.getWidth(choiceButton.message) + 20);
		}

		int ix = 5 + this.buttonWidth + 5;
		int j = ix * this.choiceButtons.size();
		this.lines = MultilineText.create(this.textRenderer, this.message, j);
		int k = this.lines.count() * 9;
		this.linesY = (int)((double)this.height / 2.0 - (double)k / 2.0);
		int l = this.linesY + k + 9 * 2;
		int m = (int)((double)this.width / 2.0 - (double)j / 2.0);

		for (UnmodifiableIterator var6 = this.choiceButtons.iterator(); var6.hasNext(); m += ix) {
			DialogScreen.ChoiceButton choiceButton2 = (DialogScreen.ChoiceButton)var6.next();
			this.addDrawableChild(new ButtonWidget(m, l, this.buttonWidth, 20, choiceButton2.message, choiceButton2.pressAction));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, this.linesY - 9 * 2, -1);
		this.lines.drawCenterWithShadow(matrices, this.width / 2, this.linesY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	public static final class ChoiceButton {
		final Text message;
		final ButtonWidget.PressAction pressAction;

		public ChoiceButton(Text message, ButtonWidget.PressAction pressAction) {
			this.message = message;
			this.pressAction = pressAction;
		}
	}
}
