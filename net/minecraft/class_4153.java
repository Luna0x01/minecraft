package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class class_4153 extends Screen {
	private final Screen field_20208;
	protected class_4153.class_4154 field_20203;
	protected String field_20204;
	private final String field_20209;
	private final List<String> field_20210 = Lists.newArrayList();
	protected String field_20205;
	protected String field_20206;
	protected String field_20207;

	public class_4153(Screen screen, class_4153.class_4154 arg, String string, String string2) {
		this.field_20208 = screen;
		this.field_20203 = arg;
		this.field_20204 = string;
		this.field_20209 = string2;
		this.field_20205 = I18n.translate("selectWorld.backupJoinConfirmButton");
		this.field_20206 = I18n.translate("selectWorld.backupJoinSkipButton");
		this.field_20207 = I18n.translate("gui.cancel");
	}

	@Override
	protected void init() {
		super.init();
		this.field_20210.clear();
		this.field_20210.addAll(this.textRenderer.wrapLines(this.field_20209, this.width - 50));
		this.addButton(new OptionButtonWidget(0, this.width / 2 - 155, 100 + (this.field_20210.size() + 1) * this.textRenderer.fontHeight, this.field_20205) {
			@Override
			public void method_18374(double d, double e) {
				class_4153.this.field_20203.proceed(true);
			}
		});
		this.addButton(new OptionButtonWidget(1, this.width / 2 - 155 + 160, 100 + (this.field_20210.size() + 1) * this.textRenderer.fontHeight, this.field_20206) {
			@Override
			public void method_18374(double d, double e) {
				class_4153.this.field_20203.proceed(false);
			}
		});
		this.addButton(
			new ButtonWidget(1, this.width / 2 - 155 + 80, 124 + (this.field_20210.size() + 1) * this.textRenderer.fontHeight, 150, 20, this.field_20207) {
				@Override
				public void method_18374(double d, double e) {
					class_4153.this.client.setScreen(class_4153.this.field_20208);
				}
			}
		);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.field_20204, this.width / 2, 70, 16777215);
		int i = 90;

		for (String string : this.field_20210) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
			i += this.textRenderer.fontHeight;
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean method_18607() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.client.setScreen(this.field_20208);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	public interface class_4154 {
		void proceed(boolean bl);
	}
}
