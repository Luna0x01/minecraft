package net.minecraft.client.gui.screen.options;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;

public class SkinOptionsScreen extends Screen {
	private final Screen parent;
	private String title;

	public SkinOptionsScreen(Screen screen) {
		this.parent = screen;
	}

	@Override
	protected void init() {
		int i = 0;
		this.title = I18n.translate("options.skinCustomisation.title");

		for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
			this.addButton(
				new SkinOptionsScreen.SkinOptionButton(
					playerModelPart.getId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, playerModelPart
				)
			);
			i++;
		}

		this.addButton(
			new OptionButtonWidget(
				199,
				this.width / 2 - 155 + i % 2 * 160,
				this.height / 6 + 24 * (i >> 1),
				GameOptions.Option.MAIN_HAND,
				this.client.options.method_18260(GameOptions.Option.MAIN_HAND)
			) {
				@Override
				public void method_18374(double d, double e) {
					SkinOptionsScreen.this.client.options.method_18258(GameOptions.Option.MAIN_HAND, 1);
					this.message = SkinOptionsScreen.this.client.options.method_18260(GameOptions.Option.MAIN_HAND);
					SkinOptionsScreen.this.client.options.onPlayerModelPartChange();
				}
			}
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(new ButtonWidget(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				SkinOptionsScreen.this.client.options.save();
				SkinOptionsScreen.this.client.setScreen(SkinOptionsScreen.this.parent);
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
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	private String getPlayerModelPartDisplayString(PlayerModelPart part) {
		String string;
		if (this.client.options.getEnabledPlayerModelParts().contains(part)) {
			string = I18n.translate("options.on");
		} else {
			string = I18n.translate("options.off");
		}

		return part.getOptionName().asFormattedString() + ": " + string;
	}

	class SkinOptionButton extends ButtonWidget {
		private final PlayerModelPart part;

		private SkinOptionButton(int i, int j, int k, int l, int m, PlayerModelPart playerModelPart) {
			super(i, j, k, l, m, SkinOptionsScreen.this.getPlayerModelPartDisplayString(playerModelPart));
			this.part = playerModelPart;
		}

		@Override
		public void method_18374(double d, double e) {
			SkinOptionsScreen.this.client.options.togglePlayerModelPart(this.part);
			this.message = SkinOptionsScreen.this.getPlayerModelPartDisplayString(this.part);
		}
	}
}
