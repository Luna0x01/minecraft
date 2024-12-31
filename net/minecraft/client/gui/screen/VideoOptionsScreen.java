package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionPairWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;

public class VideoOptionsScreen extends Screen {
	private Screen parent;
	protected String title = "Video Settings";
	private GameOptions options;
	private EntryListWidget list;
	private static final GameOptions.Option[] OPTIONS = new GameOptions.Option[]{
		GameOptions.Option.GRAPHICS,
		GameOptions.Option.RENDER_DISTANCE,
		GameOptions.Option.AMBIENT_OCCLUSION,
		GameOptions.Option.MAX_FPS,
		GameOptions.Option.ANAGLYPH,
		GameOptions.Option.VIEW_BOBBING,
		GameOptions.Option.GUI_SCALE,
		GameOptions.Option.BRIGHTNESS,
		GameOptions.Option.SHOW_CLOUDS,
		GameOptions.Option.PARTICLES,
		GameOptions.Option.USE_FULLSCREEN,
		GameOptions.Option.ENABLE_VSYNC,
		GameOptions.Option.MIPMAP_LEVELS,
		GameOptions.Option.BLOCK_ALTERNATIVES,
		GameOptions.Option.USE_VBO,
		GameOptions.Option.ENTITY_SHADOWS
	};

	public VideoOptionsScreen(Screen screen, GameOptions gameOptions) {
		this.parent = screen;
		this.options = gameOptions;
	}

	@Override
	public void init() {
		this.title = I18n.translate("options.videoTitle");
		this.buttons.clear();
		this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height - 27, I18n.translate("gui.done")));
		if (!GLX.vboSupported) {
			GameOptions.Option[] options = new GameOptions.Option[OPTIONS.length - 1];
			int i = 0;

			for (GameOptions.Option option : OPTIONS) {
				if (option == GameOptions.Option.USE_VBO) {
					break;
				}

				options[i] = option;
				i++;
			}

			this.list = new OptionPairWidget(this.client, this.width, this.height, 32, this.height - 32, 25, options);
		} else {
			this.list = new OptionPairWidget(this.client, this.width, this.height, 32, this.height - 32, 25, OPTIONS);
		}
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.list.handleMouse();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 200) {
				this.client.options.save();
				this.client.setScreen(this.parent);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		int i = this.options.guiScale;
		super.mouseClicked(mouseX, mouseY, button);
		this.list.mouseClicked(mouseX, mouseY, button);
		if (this.options.guiScale != i) {
			Window window = new Window(this.client);
			int j = window.getWidth();
			int k = window.getHeight();
			this.init(this.client, j, k);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		int i = this.options.guiScale;
		super.mouseReleased(mouseX, mouseY, button);
		this.list.mouseReleased(mouseX, mouseY, button);
		if (this.options.guiScale != i) {
			Window window = new Window(this.client);
			int j = window.getWidth();
			int k = window.getHeight();
			this.init(this.client, j, k);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.list.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 5, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}
}
