package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class ClickableWidget extends DrawableHelper implements Drawable, Element, Selectable {
	public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
	protected int width;
	protected int height;
	public int x;
	public int y;
	private Text message;
	protected boolean hovered;
	public boolean active = true;
	public boolean visible = true;
	protected float alpha = 1.0F;
	private boolean focused;

	public ClickableWidget(int x, int y, int width, int height, Text message) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.message = message;
	}

	public int getHeight() {
		return this.height;
	}

	protected int getYImage(boolean hovered) {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (hovered) {
			i = 2;
		}

		return i;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			this.renderButton(matrices, mouseX, mouseY, delta);
		}
	}

	protected MutableText getNarrationMessage() {
		return getNarrationMessage(this.getMessage());
	}

	public static MutableText getNarrationMessage(Text message) {
		return new TranslatableText("gui.narrate.button", message);
	}

	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int i = this.getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
		this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
		int j = this.active ? 16777215 : 10526880;
		drawCenteredText(
			matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24
		);
	}

	protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
	}

	public void onClick(double mouseX, double mouseY) {
	}

	public void onRelease(double mouseX, double mouseY) {
	}

	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean bl = this.clicked(mouseX, mouseY);
				if (bl) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					this.onClick(mouseX, mouseY);
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.isValidClickButton(button)) {
			this.onRelease(mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	protected boolean isValidClickButton(int button) {
		return button == 0;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isValidClickButton(button)) {
			this.onDrag(mouseX, mouseY, deltaX, deltaY);
			return true;
		} else {
			return false;
		}
	}

	protected boolean clicked(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.x
			&& mouseY >= (double)this.y
			&& mouseX < (double)(this.x + this.width)
			&& mouseY < (double)(this.y + this.height);
	}

	public boolean isHovered() {
		return this.hovered || this.focused;
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		if (this.active && this.visible) {
			this.focused = !this.focused;
			this.onFocusedChanged(this.focused);
			return this.focused;
		} else {
			return false;
		}
	}

	protected void onFocusedChanged(boolean newFocused) {
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active
			&& this.visible
			&& mouseX >= (double)this.x
			&& mouseY >= (double)this.y
			&& mouseX < (double)(this.x + this.width)
			&& mouseY < (double)(this.y + this.height);
	}

	public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int value) {
		this.width = value;
	}

	public void setAlpha(float value) {
		this.alpha = value;
	}

	public void setMessage(Text message) {
		this.message = message;
	}

	public Text getMessage() {
		return this.message;
	}

	public boolean isFocused() {
		return this.focused;
	}

	@Override
	public boolean isNarratable() {
		return this.visible && this.active;
	}

	protected void setFocused(boolean focused) {
		this.focused = focused;
	}

	@Override
	public Selectable.SelectionType getType() {
		if (this.focused) {
			return Selectable.SelectionType.FOCUSED;
		} else {
			return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
		}
	}

	protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				builder.put(NarrationPart.USAGE, new TranslatableText("narration.button.usage.focused"));
			} else {
				builder.put(NarrationPart.USAGE, new TranslatableText("narration.button.usage.hovered"));
			}
		}
	}
}
