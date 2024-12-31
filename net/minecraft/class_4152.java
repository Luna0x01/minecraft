package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class class_4152 extends Screen {
	private final Runnable field_20199;
	protected final Text field_20196;
	protected final Text field_20197;
	private final List<String> field_20200 = Lists.newArrayList();
	protected String field_20198;
	private int field_20201;

	public class_4152(Runnable runnable, Text text, Text text2) {
		this(runnable, text, text2, "gui.back");
	}

	public class_4152(Runnable runnable, Text text, Text text2, String string) {
		this.field_20199 = runnable;
		this.field_20196 = text;
		this.field_20197 = text2;
		this.field_20198 = I18n.translate(string);
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 6 + 168, this.field_20198) {
			@Override
			public void method_18374(double d, double e) {
				class_4152.this.field_20199.run();
			}
		});
		this.field_20200.clear();
		this.field_20200.addAll(this.textRenderer.wrapLines(this.field_20197.asFormattedString(), this.width - 50));
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.field_20196.asFormattedString(), this.width / 2, 70, 16777215);
		int i = 90;

		for (String string : this.field_20200) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
			i += this.textRenderer.fontHeight;
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.field_20201 == 0) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.active = true;
			}
		}
	}
}
