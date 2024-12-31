package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_4393;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureBlockScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final StructureBlockEntity field_14929;
	private BlockMirror mirror = BlockMirror.NONE;
	private BlockRotation rotation = BlockRotation.NONE;
	private StructureBlockMode field_14932 = StructureBlockMode.DATA;
	private boolean field_14933;
	private boolean field_14934;
	private boolean field_14935;
	private TextFieldWidget field_14936;
	private TextFieldWidget field_14937;
	private TextFieldWidget field_14938;
	private TextFieldWidget field_14939;
	private TextFieldWidget field_14940;
	private TextFieldWidget field_14906;
	private TextFieldWidget field_14907;
	private TextFieldWidget field_14908;
	private TextFieldWidget field_14909;
	private TextFieldWidget field_14910;
	private ButtonWidget doneButton;
	private ButtonWidget cancelButton;
	private ButtonWidget saveButton;
	private ButtonWidget loadButton;
	private ButtonWidget field_14915;
	private ButtonWidget field_14916;
	private ButtonWidget field_14917;
	private ButtonWidget field_14918;
	private ButtonWidget modeButton;
	private ButtonWidget detectSizeButton;
	private ButtonWidget entitiesButton;
	private ButtonWidget mirrorButton;
	private ButtonWidget showAirButton;
	private ButtonWidget showBoundingBoxButton;
	private final List<TextFieldWidget> field_14925 = Lists.newArrayList();
	private final DecimalFormat field_14926 = new DecimalFormat("0.0###");

	public StructureBlockScreen(StructureBlockEntity structureBlockEntity) {
		this.field_14929 = structureBlockEntity;
		this.field_14926.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
	}

	@Override
	public void tick() {
		this.field_14936.tick();
		this.field_14937.tick();
		this.field_14938.tick();
		this.field_14939.tick();
		this.field_14940.tick();
		this.field_14906.tick();
		this.field_14907.tick();
		this.field_14908.tick();
		this.field_14909.tick();
		this.field_14910.tick();
	}

	private void method_18759() {
		if (this.method_18748(StructureBlockEntity.class_3745.UPDATE_DATA)) {
			this.client.setScreen(null);
		}
	}

	private void method_18761() {
		this.field_14929.method_11667(this.mirror);
		this.field_14929.method_11668(this.rotation);
		this.field_14929.method_11669(this.field_14932);
		this.field_14929.method_11675(this.field_14933);
		this.field_14929.method_13348(this.field_14934);
		this.field_14929.method_13349(this.field_14935);
		this.client.setScreen(null);
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.method_18759();
			}
		});
		this.cancelButton = this.addButton(new ButtonWidget(1, this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.method_18761();
			}
		});
		this.saveButton = this.addButton(new ButtonWidget(9, this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.save")) {
			@Override
			public void method_18374(double d, double e) {
				if (StructureBlockScreen.this.field_14929.method_13354() == StructureBlockMode.SAVE) {
					StructureBlockScreen.this.method_18748(StructureBlockEntity.class_3745.SAVE_AREA);
					StructureBlockScreen.this.client.setScreen(null);
				}
			}
		});
		this.loadButton = this.addButton(new ButtonWidget(10, this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.load")) {
			@Override
			public void method_18374(double d, double e) {
				if (StructureBlockScreen.this.field_14929.method_13354() == StructureBlockMode.LOAD) {
					StructureBlockScreen.this.method_18748(StructureBlockEntity.class_3745.LOAD_AREA);
					StructureBlockScreen.this.client.setScreen(null);
				}
			}
		});
		this.modeButton = this.addButton(new ButtonWidget(18, this.width / 2 - 4 - 150, 185, 50, 20, "MODE") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_13355();
				StructureBlockScreen.this.method_13422();
			}
		});
		this.detectSizeButton = this.addButton(new ButtonWidget(19, this.width / 2 + 4 + 100, 120, 50, 20, I18n.translate("structure_block.button.detect_size")) {
			@Override
			public void method_18374(double d, double e) {
				if (StructureBlockScreen.this.field_14929.method_13354() == StructureBlockMode.SAVE) {
					StructureBlockScreen.this.method_18748(StructureBlockEntity.class_3745.SCAN_AREA);
					StructureBlockScreen.this.client.setScreen(null);
				}
			}
		});
		this.entitiesButton = this.addButton(new ButtonWidget(20, this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_11675(!StructureBlockScreen.this.field_14929.method_13356());
				StructureBlockScreen.this.method_13412();
			}
		});
		this.mirrorButton = this.addButton(new ButtonWidget(21, this.width / 2 - 20, 185, 40, 20, "MIRROR") {
			@Override
			public void method_18374(double d, double e) {
				switch (StructureBlockScreen.this.field_14929.method_13351()) {
					case NONE:
						StructureBlockScreen.this.field_14929.method_11667(BlockMirror.LEFT_RIGHT);
						break;
					case LEFT_RIGHT:
						StructureBlockScreen.this.field_14929.method_11667(BlockMirror.FRONT_BACK);
						break;
					case FRONT_BACK:
						StructureBlockScreen.this.field_14929.method_11667(BlockMirror.NONE);
				}

				StructureBlockScreen.this.method_13420();
			}
		});
		this.showAirButton = this.addButton(new ButtonWidget(22, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_13348(!StructureBlockScreen.this.field_14929.method_13335());
				StructureBlockScreen.this.method_13418();
			}
		});
		this.showBoundingBoxButton = this.addButton(new ButtonWidget(23, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_13349(!StructureBlockScreen.this.field_14929.method_13336());
				StructureBlockScreen.this.method_13419();
			}
		});
		this.field_14915 = this.addButton(new ButtonWidget(11, this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_11668(BlockRotation.NONE);
				StructureBlockScreen.this.method_13421();
			}
		});
		this.field_14916 = this.addButton(new ButtonWidget(12, this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_11668(BlockRotation.CLOCKWISE_90);
				StructureBlockScreen.this.method_13421();
			}
		});
		this.field_14917 = this.addButton(new ButtonWidget(13, this.width / 2 + 1 + 20, 185, 40, 20, "180") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_11668(BlockRotation.CLOCKWISE_180);
				StructureBlockScreen.this.method_13421();
			}
		});
		this.field_14918 = this.addButton(new ButtonWidget(14, this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270") {
			@Override
			public void method_18374(double d, double e) {
				StructureBlockScreen.this.field_14929.method_11668(BlockRotation.COUNTERCLOCKWISE_90);
				StructureBlockScreen.this.method_13421();
			}
		});
		this.field_14925.clear();
		this.field_14936 = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 152, 40, 300, 20) {
			@Override
			public boolean charTyped(char c, int i) {
				return !StructureBlockScreen.method_18753(this.getText(), c, this.getCursor()) ? false : super.charTyped(c, i);
			}
		};
		this.field_14936.setMaxLength(64);
		this.field_14936.setText(this.field_14929.method_13345());
		this.field_14925.add(this.field_14936);
		BlockPos blockPos = this.field_14929.method_13347();
		this.field_14937 = new TextFieldWidget(3, this.textRenderer, this.width / 2 - 152, 80, 80, 20);
		this.field_14937.setMaxLength(15);
		this.field_14937.setText(Integer.toString(blockPos.getX()));
		this.field_14925.add(this.field_14937);
		this.field_14938 = new TextFieldWidget(4, this.textRenderer, this.width / 2 - 72, 80, 80, 20);
		this.field_14938.setMaxLength(15);
		this.field_14938.setText(Integer.toString(blockPos.getY()));
		this.field_14925.add(this.field_14938);
		this.field_14939 = new TextFieldWidget(5, this.textRenderer, this.width / 2 + 8, 80, 80, 20);
		this.field_14939.setMaxLength(15);
		this.field_14939.setText(Integer.toString(blockPos.getZ()));
		this.field_14925.add(this.field_14939);
		BlockPos blockPos2 = this.field_14929.method_13350();
		this.field_14940 = new TextFieldWidget(6, this.textRenderer, this.width / 2 - 152, 120, 80, 20);
		this.field_14940.setMaxLength(15);
		this.field_14940.setText(Integer.toString(blockPos2.getX()));
		this.field_14925.add(this.field_14940);
		this.field_14906 = new TextFieldWidget(7, this.textRenderer, this.width / 2 - 72, 120, 80, 20);
		this.field_14906.setMaxLength(15);
		this.field_14906.setText(Integer.toString(blockPos2.getY()));
		this.field_14925.add(this.field_14906);
		this.field_14907 = new TextFieldWidget(8, this.textRenderer, this.width / 2 + 8, 120, 80, 20);
		this.field_14907.setMaxLength(15);
		this.field_14907.setText(Integer.toString(blockPos2.getZ()));
		this.field_14925.add(this.field_14907);
		this.field_14908 = new TextFieldWidget(15, this.textRenderer, this.width / 2 - 152, 120, 80, 20);
		this.field_14908.setMaxLength(15);
		this.field_14908.setText(this.field_14926.format((double)this.field_14929.method_13357()));
		this.field_14925.add(this.field_14908);
		this.field_14909 = new TextFieldWidget(16, this.textRenderer, this.width / 2 - 72, 120, 80, 20);
		this.field_14909.setMaxLength(31);
		this.field_14909.setText(Long.toString(this.field_14929.method_13358()));
		this.field_14925.add(this.field_14909);
		this.field_14910 = new TextFieldWidget(17, this.textRenderer, this.width / 2 - 152, 120, 240, 20);
		this.field_14910.setMaxLength(128);
		this.field_14910.setText(this.field_14929.method_13353());
		this.field_14925.add(this.field_14910);
		this.field_20307.addAll(this.field_14925);
		this.mirror = this.field_14929.method_13351();
		this.method_13420();
		this.rotation = this.field_14929.method_13352();
		this.method_13421();
		this.field_14932 = this.field_14929.method_13354();
		this.method_13422();
		this.field_14933 = this.field_14929.method_13356();
		this.method_13412();
		this.field_14934 = this.field_14929.method_13335();
		this.method_13418();
		this.field_14935 = this.field_14929.method_13336();
		this.method_13419();
		this.field_14936.setFocused(true);
		this.method_18421(this.field_14936);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.field_14936.getText();
		String string2 = this.field_14937.getText();
		String string3 = this.field_14938.getText();
		String string4 = this.field_14939.getText();
		String string5 = this.field_14940.getText();
		String string6 = this.field_14906.getText();
		String string7 = this.field_14907.getText();
		String string8 = this.field_14908.getText();
		String string9 = this.field_14909.getText();
		String string10 = this.field_14910.getText();
		this.init(client, width, height);
		this.field_14936.setText(string);
		this.field_14937.setText(string2);
		this.field_14938.setText(string3);
		this.field_14939.setText(string4);
		this.field_14940.setText(string5);
		this.field_14906.setText(string6);
		this.field_14907.setText(string7);
		this.field_14908.setText(string8);
		this.field_14909.setText(string9);
		this.field_14910.setText(string10);
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
	}

	private void method_13412() {
		boolean bl = !this.field_14929.method_13356();
		if (bl) {
			this.entitiesButton.message = I18n.translate("options.on");
		} else {
			this.entitiesButton.message = I18n.translate("options.off");
		}
	}

	private void method_13418() {
		boolean bl = this.field_14929.method_13335();
		if (bl) {
			this.showAirButton.message = I18n.translate("options.on");
		} else {
			this.showAirButton.message = I18n.translate("options.off");
		}
	}

	private void method_13419() {
		boolean bl = this.field_14929.method_13336();
		if (bl) {
			this.showBoundingBoxButton.message = I18n.translate("options.on");
		} else {
			this.showBoundingBoxButton.message = I18n.translate("options.off");
		}
	}

	private void method_13420() {
		BlockMirror blockMirror = this.field_14929.method_13351();
		switch (blockMirror) {
			case NONE:
				this.mirrorButton.message = "|";
				break;
			case LEFT_RIGHT:
				this.mirrorButton.message = "< >";
				break;
			case FRONT_BACK:
				this.mirrorButton.message = "^ v";
		}
	}

	private void method_13421() {
		this.field_14915.active = true;
		this.field_14916.active = true;
		this.field_14917.active = true;
		this.field_14918.active = true;
		switch (this.field_14929.method_13352()) {
			case NONE:
				this.field_14915.active = false;
				break;
			case CLOCKWISE_180:
				this.field_14917.active = false;
				break;
			case COUNTERCLOCKWISE_90:
				this.field_14918.active = false;
				break;
			case CLOCKWISE_90:
				this.field_14916.active = false;
		}
	}

	private void method_13422() {
		this.field_14936.setFocused(false);
		this.field_14937.setFocused(false);
		this.field_14938.setFocused(false);
		this.field_14939.setFocused(false);
		this.field_14940.setFocused(false);
		this.field_14906.setFocused(false);
		this.field_14907.setFocused(false);
		this.field_14908.setFocused(false);
		this.field_14909.setFocused(false);
		this.field_14910.setFocused(false);
		this.field_14936.setVisible(false);
		this.field_14936.setFocused(false);
		this.field_14937.setVisible(false);
		this.field_14938.setVisible(false);
		this.field_14939.setVisible(false);
		this.field_14940.setVisible(false);
		this.field_14906.setVisible(false);
		this.field_14907.setVisible(false);
		this.field_14908.setVisible(false);
		this.field_14909.setVisible(false);
		this.field_14910.setVisible(false);
		this.saveButton.visible = false;
		this.loadButton.visible = false;
		this.detectSizeButton.visible = false;
		this.entitiesButton.visible = false;
		this.mirrorButton.visible = false;
		this.field_14915.visible = false;
		this.field_14916.visible = false;
		this.field_14917.visible = false;
		this.field_14918.visible = false;
		this.showAirButton.visible = false;
		this.showBoundingBoxButton.visible = false;
		switch (this.field_14929.method_13354()) {
			case SAVE:
				this.field_14936.setVisible(true);
				this.field_14937.setVisible(true);
				this.field_14938.setVisible(true);
				this.field_14939.setVisible(true);
				this.field_14940.setVisible(true);
				this.field_14906.setVisible(true);
				this.field_14907.setVisible(true);
				this.saveButton.visible = true;
				this.detectSizeButton.visible = true;
				this.entitiesButton.visible = true;
				this.showAirButton.visible = true;
				break;
			case LOAD:
				this.field_14936.setVisible(true);
				this.field_14937.setVisible(true);
				this.field_14938.setVisible(true);
				this.field_14939.setVisible(true);
				this.field_14908.setVisible(true);
				this.field_14909.setVisible(true);
				this.loadButton.visible = true;
				this.entitiesButton.visible = true;
				this.mirrorButton.visible = true;
				this.field_14915.visible = true;
				this.field_14916.visible = true;
				this.field_14917.visible = true;
				this.field_14918.visible = true;
				this.showBoundingBoxButton.visible = true;
				this.method_13421();
				break;
			case CORNER:
				this.field_14936.setVisible(true);
				break;
			case DATA:
				this.field_14910.setVisible(true);
		}

		this.modeButton.message = I18n.translate("structure_block.mode." + this.field_14929.method_13354().asString());
	}

	private boolean method_18748(StructureBlockEntity.class_3745 arg) {
		BlockPos blockPos = new BlockPos(
			this.method_13417(this.field_14937.getText()), this.method_13417(this.field_14938.getText()), this.method_13417(this.field_14939.getText())
		);
		BlockPos blockPos2 = new BlockPos(
			this.method_13417(this.field_14940.getText()), this.method_13417(this.field_14906.getText()), this.method_13417(this.field_14907.getText())
		);
		float f = this.method_13416(this.field_14908.getText());
		long l = this.method_13413(this.field_14909.getText());
		this.client
			.getNetworkHandler()
			.sendPacket(
				new class_4393(
					this.field_14929.getPos(),
					arg,
					this.field_14929.method_13354(),
					this.field_14936.getText(),
					blockPos,
					blockPos2,
					this.field_14929.method_13351(),
					this.field_14929.method_13352(),
					this.field_14910.getText(),
					this.field_14929.method_13356(),
					this.field_14929.method_13335(),
					this.field_14929.method_13336(),
					f,
					l
				)
			);
		return true;
	}

	private long method_13413(String string) {
		try {
			return Long.valueOf(string);
		} catch (NumberFormatException var3) {
			return 0L;
		}
	}

	private float method_13416(String string) {
		try {
			return Float.valueOf(string);
		} catch (NumberFormatException var3) {
			return 1.0F;
		}
	}

	private int method_13417(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException var3) {
			return 0;
		}
	}

	@Override
	public void method_18608() {
		this.method_18761();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (super.mouseClicked(d, e, i)) {
			for (TextFieldWidget textFieldWidget : this.field_14925) {
				textFieldWidget.setFocused(this.getFocused() == textFieldWidget);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i != 258) {
			if (i != 257 && i != 335) {
				return super.keyPressed(i, j, k);
			} else {
				this.method_18759();
				return true;
			}
		} else {
			TextFieldWidget textFieldWidget = null;
			TextFieldWidget textFieldWidget2 = null;

			for (TextFieldWidget textFieldWidget3 : this.field_14925) {
				if (textFieldWidget != null && textFieldWidget3.isVisible()) {
					textFieldWidget2 = textFieldWidget3;
					break;
				}

				if (textFieldWidget3.isFocused() && textFieldWidget3.isVisible()) {
					textFieldWidget = textFieldWidget3;
				}
			}

			if (textFieldWidget != null && textFieldWidget2 == null) {
				for (TextFieldWidget textFieldWidget4 : this.field_14925) {
					if (textFieldWidget4.isVisible() && textFieldWidget4 != textFieldWidget) {
						textFieldWidget2 = textFieldWidget4;
						break;
					}
				}
			}

			if (textFieldWidget2 != null && textFieldWidget2 != textFieldWidget) {
				textFieldWidget.setFocused(false);
				textFieldWidget2.setFocused(true);
				this.method_18421(textFieldWidget2);
			}

			return true;
		}
	}

	private static boolean method_18753(String string, char c, int i) {
		int j = string.indexOf(58);
		int k = string.indexOf(47);
		if (c == ':') {
			return (k == -1 || i <= k) && j == -1;
		} else {
			return c == '/' ? i > j : c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		StructureBlockMode structureBlockMode = this.field_14929.method_13354();
		this.drawCenteredString(this.textRenderer, I18n.translate(Blocks.STRUCTURE_BLOCK.getTranslationKey()), this.width / 2, 10, 16777215);
		if (structureBlockMode != StructureBlockMode.DATA) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
			this.field_14936.method_18385(mouseX, mouseY, tickDelta);
		}

		if (structureBlockMode == StructureBlockMode.LOAD || structureBlockMode == StructureBlockMode.SAVE) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.position"), this.width / 2 - 153, 70, 10526880);
			this.field_14937.method_18385(mouseX, mouseY, tickDelta);
			this.field_14938.method_18385(mouseX, mouseY, tickDelta);
			this.field_14939.method_18385(mouseX, mouseY, tickDelta);
			String string = I18n.translate("structure_block.include_entities");
			int i = this.textRenderer.getStringWidth(string);
			this.drawWithShadow(this.textRenderer, string, this.width / 2 + 154 - i, 150, 10526880);
		}

		if (structureBlockMode == StructureBlockMode.SAVE) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.size"), this.width / 2 - 153, 110, 10526880);
			this.field_14940.method_18385(mouseX, mouseY, tickDelta);
			this.field_14906.method_18385(mouseX, mouseY, tickDelta);
			this.field_14907.method_18385(mouseX, mouseY, tickDelta);
			String string2 = I18n.translate("structure_block.detect_size");
			int j = this.textRenderer.getStringWidth(string2);
			this.drawWithShadow(this.textRenderer, string2, this.width / 2 + 154 - j, 110, 10526880);
			String string3 = I18n.translate("structure_block.show_air");
			int k = this.textRenderer.getStringWidth(string3);
			this.drawWithShadow(this.textRenderer, string3, this.width / 2 + 154 - k, 70, 10526880);
		}

		if (structureBlockMode == StructureBlockMode.LOAD) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
			this.field_14908.method_18385(mouseX, mouseY, tickDelta);
			this.field_14909.method_18385(mouseX, mouseY, tickDelta);
			String string4 = I18n.translate("structure_block.show_boundingbox");
			int l = this.textRenderer.getStringWidth(string4);
			this.drawWithShadow(this.textRenderer, string4, this.width / 2 + 154 - l, 70, 10526880);
		}

		if (structureBlockMode == StructureBlockMode.DATA) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
			this.field_14910.method_18385(mouseX, mouseY, tickDelta);
		}

		String string5 = "structure_block.mode_info." + structureBlockMode.asString();
		this.drawWithShadow(this.textRenderer, I18n.translate(string5), this.width / 2 - 153, 174, 10526880);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}
}
