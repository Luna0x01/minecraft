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
	protected void init() {
		this.name = I18n.translate("options.sounds.title");
		this.off = I18n.translate("options.off");
		int i = 0;
		this.addButton(
			new SoundsScreen.SoundButtonWidget(
				SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true
			)
		);
		i += 2;

		for (SoundCategory soundCategory : SoundCategory.values()) {
			if (soundCategory != SoundCategory.MASTER) {
				this.addButton(
					new SoundsScreen.SoundButtonWidget(soundCategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, false)
				);
				i++;
			}
		}

		this.addButton(
			new OptionButtonWidget(
				201,
				this.width / 2 - 75,
				this.height / 6 - 12 + 24 * (++i >> 1),
				GameOptions.Option.SHOW_SUBTITLES,
				this.options.method_18260(GameOptions.Option.SHOW_SUBTITLES)
			) {
				@Override
				public void method_18374(double d, double e) {
					SoundsScreen.this.client.options.method_18258(GameOptions.Option.SHOW_SUBTITLES, 1);
					this.message = SoundsScreen.this.client.options.method_18260(GameOptions.Option.SHOW_SUBTITLES);
					SoundsScreen.this.client.options.save();
				}
			}
		);
		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 168, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				SoundsScreen.this.client.options.save();
				SoundsScreen.this.client.setScreen(SoundsScreen.this.parent);
			}
		});
	}

	@Override
	public void method_18608() {
		this.client.options.save();
		super.method_18608();
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
		public double field_20319;
		public boolean mouseButtonPressed;

		public SoundButtonWidget(int i, int j, int k, SoundCategory soundCategory, boolean bl) {
			super(i, j, k, bl ? 310 : 150, 20, "");
			this.category = soundCategory;
			this.categoryName = I18n.translate("soundCategory." + soundCategory.getName());
			this.message = this.categoryName + ": " + SoundsScreen.this.getVolume(soundCategory);
			this.field_20319 = (double)SoundsScreen.this.options.getSoundVolume(soundCategory);
		}

		@Override
		protected int getYImage(boolean isHovered) {
			return 0;
		}

		@Override
		protected void mouseDragged(MinecraftClient client, int mouseX, int mouseY) {
			if (this.visible) {
				if (this.mouseButtonPressed) {
					this.field_20319 = (double)((float)(mouseX - (this.x + 4)) / (float)(this.width - 8));
					this.field_20319 = MathHelper.clamp(this.field_20319, 0.0, 1.0);
					client.options.setSoundVolume(this.category, (float)this.field_20319);
					client.options.save();
					this.message = this.categoryName + ": " + SoundsScreen.this.getVolume(this.category);
				}

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.drawTexture(this.x + (int)(this.field_20319 * (double)(this.width - 8)), this.y, 0, 66, 4, 20);
				this.drawTexture(this.x + (int)(this.field_20319 * (double)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
			}
		}

		@Override
		public void method_18374(double d, double e) {
			this.field_20319 = (d - (double)(this.x + 4)) / (double)(this.width - 8);
			this.field_20319 = MathHelper.clamp(this.field_20319, 0.0, 1.0);
			SoundsScreen.this.client.options.setSoundVolume(this.category, (float)this.field_20319);
			SoundsScreen.this.client.options.save();
			this.message = this.categoryName + ": " + SoundsScreen.this.getVolume(this.category);
			this.mouseButtonPressed = true;
		}

		@Override
		public void playDownSound(SoundManager soundManager) {
		}

		@Override
		public void method_18376(double d, double e) {
			if (this.mouseButtonPressed) {
				SoundsScreen.this.client.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
			}

			this.mouseButtonPressed = false;
		}
	}
}
