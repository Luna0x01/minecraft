package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GLX;
import javax.annotation.Nullable;
import net.minecraft.class_4122;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionPairWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;

public class VideoOptionsScreen extends Screen {
	private final Screen parent;
	protected String title = "Video Settings";
	private final GameOptions options;
	private OptionPairWidget field_20330;
	private static final GameOptions.Option[] OPTIONS = new GameOptions.Option[]{
		GameOptions.Option.GRAPHICS,
		GameOptions.Option.RENDER_DISTANCE,
		GameOptions.Option.AMBIENT_OCCLUSION,
		GameOptions.Option.MAX_FPS,
		GameOptions.Option.ENABLE_VSYNC,
		GameOptions.Option.VIEW_BOBBING,
		GameOptions.Option.GUI_SCALE,
		GameOptions.Option.ATTACK_INDICATOR,
		GameOptions.Option.BRIGHTNESS,
		GameOptions.Option.SHOW_CLOUDS,
		GameOptions.Option.USE_FULLSCREEN,
		GameOptions.Option.PARTICLES,
		GameOptions.Option.MIPMAP_LEVELS,
		GameOptions.Option.USE_VBO,
		GameOptions.Option.ENTITY_SHADOWS,
		GameOptions.Option.BIOME_BLEND_RADIUS
	};

	public VideoOptionsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.field_20330;
	}

	@Override
	protected void init() {
		this.title = I18n.translate("options.videoTitle");
		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height - 27, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				VideoOptionsScreen.this.client.options.save();
				VideoOptionsScreen.this.client.field_19944.method_18312();
				VideoOptionsScreen.this.client.setScreen(VideoOptionsScreen.this.parent);
			}
		});
		if (GLX.vboSupported) {
			this.field_20330 = new OptionPairWidget(this.client, this.width, this.height, 32, this.height - 32, 25, OPTIONS);
		} else {
			GameOptions.Option[] options = new GameOptions.Option[OPTIONS.length - 1];
			int i = 0;

			for (GameOptions.Option option : OPTIONS) {
				if (option == GameOptions.Option.USE_VBO) {
					break;
				}

				options[i] = option;
				i++;
			}

			this.field_20330 = new OptionPairWidget(this.client, this.width, this.height, 32, this.height - 32, 25, options);
		}

		this.field_20307.add(this.field_20330);
	}

	@Override
	public void method_18608() {
		this.client.options.save();
		super.method_18608();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		int j = this.options.guiScale;
		if (super.mouseClicked(d, e, i)) {
			if (this.options.guiScale != j) {
				this.client.field_19944.method_18314();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		int j = this.options.guiScale;
		if (super.mouseReleased(d, e, i)) {
			return true;
		} else if (this.field_20330.mouseReleased(d, e, i)) {
			if (this.options.guiScale != j) {
				this.client.field_19944.method_18314();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.field_20330.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 5, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
