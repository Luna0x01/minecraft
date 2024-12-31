package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.MathHelper;

public class SoundsScreen extends Screen {
	private final Screen parent;
	private final GameOptions options;
	protected String name = "Options";
	private String off;

	public SoundsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	public void init() {
		this.name = I18n.translate("options.sounds.title");
		this.off = I18n.translate("options.off");
		int i = 0;
		this.buttons
			.add(
				new SoundsScreen.SoundButtonWidget(
					SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true
				)
			);
		i += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.MASTER) {
				this.buttons
					.add(
						new SoundsScreen.SoundButtonWidget(
							soundCategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, false
						)
					);
				i++;
			}
		}

		this.buttons
			.add(
				new OptionButtonWidget(
					201,
					this.width / 2 - 75,
					this.height / 6 - 12 + 24 * (++i >> 1),
					GameOptions.Option.SHOW_SUBTITLES,
					this.options.getValueMessage(GameOptions.Option.SHOW_SUBTITLES)
				)
			);
		this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")));
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == 1) {
			this.client.options.save();
		}

		super.keyPressed(id, code);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 200) {
				this.client.options.save();
				this.client.setScreen(this.parent);
			} else if (button.id == 201) {
				this.client.options.getBooleanValue(GameOptions.Option.SHOW_SUBTITLES, 1);
				button.message = this.client.options.getValueMessage(GameOptions.Option.SHOW_SUBTITLES);
				this.client.options.save();
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.name, this.width / 2, 15, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	protected String getVolume(SoundCategory soundCategory) {
		float f = this.options.getSoundVolume(soundCategory);
		return f == 0.0F ? this.off : (int)(f * 100.0F) + "%";
	}

	class SoundButtonWidget extends ButtonWidget {
		private final SoundCategory category;
		private final String categoryName;
		public float volume = 1.0F;
		public boolean mouseButtonPressed;

		public SoundButtonWidget(int i, int j, int k, SoundCategory soundCategory, boolean bl) {
			super(i, j, k, bl ? 310 : 150, 20, "");
			this.category = soundCategory;
			this.categoryName = I18n.translate("soundCategory." + soundCategory.getName());
			this.message = this.categoryName + ": " + SoundsScreen.this.getVolume(soundCategory);
			this.volume = SoundsScreen.this.options.getSoundVolume(soundCategory);
		}

		@Override
		protected int getYImage(boolean isHovered) {
			return 0;
		}

		@Override
		protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
			if (this.visible) {
				if (this.mouseButtonPressed) {
					this.volume = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
					this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
					client.options.setSoundVolume(this.category, this.volume);
					client.options.save();
					this.message = this.categoryName + ": " + SoundsScreen.this.getVolume(this.category);
				}

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.drawTexture(this.x + (int)(this.volume * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
				this.drawTexture(this.x + (int)(this.volume * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
			}
		}

		@Override
		public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
			if (super.isMouseOver(client, mouseX, mouseY)) {
				this.volume = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
				this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
				client.options.setSoundVolume(this.category, this.volume);
				client.options.save();
				this.message = this.categoryName + ": " + SoundsScreen.this.getVolume(this.category);
				this.mouseButtonPressed = true;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void playDownSound(SoundManager soundManager) {
		}

		@Override
		public void mouseReleased(int mouseX, int mouseY) {
			if (this.mouseButtonPressed) {
				SoundsScreen.this.client.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
			}

			this.mouseButtonPressed = false;
		}
	}
}
