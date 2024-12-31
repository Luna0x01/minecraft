package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class StructureBlockScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int[] field_14927 = new int[]{203, 205, 14, 211, 199, 207};
	private final StructureBlockEntity field_14929;
	private BlockMirror mirror = BlockMirror.NONE;
	private BlockRotation rotation = BlockRotation.NONE;
	private StructureBlockEntity.class_2739 field_14932 = StructureBlockEntity.class_2739.DATA;
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
		this.field_14926.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
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

	@Override
	public void init() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.doneButton = this.addButton(new ButtonWidget(0, this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done")));
		this.cancelButton = this.addButton(new ButtonWidget(1, this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel")));
		this.saveButton = this.addButton(new ButtonWidget(9, this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.save")));
		this.loadButton = this.addButton(new ButtonWidget(10, this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.load")));
		this.modeButton = this.addButton(new ButtonWidget(18, this.width / 2 - 4 - 150, 185, 50, 20, "MODE"));
		this.detectSizeButton = this.addButton(new ButtonWidget(19, this.width / 2 + 4 + 100, 120, 50, 20, I18n.translate("structure_block.button.detect_size")));
		this.entitiesButton = this.addButton(new ButtonWidget(20, this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES"));
		this.mirrorButton = this.addButton(new ButtonWidget(21, this.width / 2 - 20, 185, 40, 20, "MIRROR"));
		this.showAirButton = this.addButton(new ButtonWidget(22, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR"));
		this.showBoundingBoxButton = this.addButton(new ButtonWidget(23, this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB"));
		this.field_14915 = this.addButton(new ButtonWidget(11, this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0"));
		this.field_14916 = this.addButton(new ButtonWidget(12, this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90"));
		this.field_14917 = this.addButton(new ButtonWidget(13, this.width / 2 + 1 + 20, 185, 40, 20, "180"));
		this.field_14918 = this.addButton(new ButtonWidget(14, this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270"));
		this.field_14936 = new TextFieldWidget(2, this.textRenderer, this.width / 2 - 152, 40, 300, 20);
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
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 1) {
				this.field_14929.method_11667(this.mirror);
				this.field_14929.method_11668(this.rotation);
				this.field_14929.method_11669(this.field_14932);
				this.field_14929.method_11675(this.field_14933);
				this.field_14929.method_13348(this.field_14934);
				this.field_14929.method_13349(this.field_14935);
				this.client.setScreen(null);
			} else if (button.id == 0) {
				if (this.method_13415(1)) {
					this.client.setScreen(null);
				}
			} else if (button.id == 9) {
				if (this.field_14929.method_13354() == StructureBlockEntity.class_2739.SAVE) {
					this.method_13415(2);
					this.client.setScreen(null);
				}
			} else if (button.id == 10) {
				if (this.field_14929.method_13354() == StructureBlockEntity.class_2739.LOAD) {
					this.method_13415(3);
					this.client.setScreen(null);
				}
			} else if (button.id == 11) {
				this.field_14929.method_11668(BlockRotation.NONE);
				this.method_13421();
			} else if (button.id == 12) {
				this.field_14929.method_11668(BlockRotation.CLOCKWISE_90);
				this.method_13421();
			} else if (button.id == 13) {
				this.field_14929.method_11668(BlockRotation.CLOCKWISE_180);
				this.method_13421();
			} else if (button.id == 14) {
				this.field_14929.method_11668(BlockRotation.COUNTERCLOCKWISE_90);
				this.method_13421();
			} else if (button.id == 18) {
				this.field_14929.method_13355();
				this.method_13422();
			} else if (button.id == 19) {
				if (this.field_14929.method_13354() == StructureBlockEntity.class_2739.SAVE) {
					this.method_13415(4);
					this.client.setScreen(null);
				}
			} else if (button.id == 20) {
				this.field_14929.method_11675(!this.field_14929.method_13356());
				this.method_13412();
			} else if (button.id == 22) {
				this.field_14929.method_13348(!this.field_14929.method_13335());
				this.method_13418();
			} else if (button.id == 23) {
				this.field_14929.method_13349(!this.field_14929.method_13336());
				this.method_13419();
			} else if (button.id == 21) {
				switch (this.field_14929.method_13351()) {
					case NONE:
						this.field_14929.method_11667(BlockMirror.LEFT_RIGHT);
						break;
					case LEFT_RIGHT:
						this.field_14929.method_11667(BlockMirror.FRONT_BACK);
						break;
					case FRONT_BACK:
						this.field_14929.method_11667(BlockMirror.NONE);
				}

				this.method_13420();
			}
		}
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
				this.field_14936.setFocused(true);
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
				this.field_14936.setFocused(true);
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
				this.field_14936.setFocused(true);
				break;
			case DATA:
				this.field_14910.setVisible(true);
				this.field_14910.setFocused(true);
		}

		this.modeButton.message = I18n.translate("structure_block.mode." + this.field_14929.method_13354().asString());
	}

	private boolean method_13415(int i) {
		try {
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			this.field_14929.method_13340(packetByteBuf);
			packetByteBuf.writeByte(i);
			packetByteBuf.writeString(this.field_14929.method_13354().toString());
			packetByteBuf.writeString(this.field_14936.getText());
			packetByteBuf.writeInt(this.method_13417(this.field_14937.getText()));
			packetByteBuf.writeInt(this.method_13417(this.field_14938.getText()));
			packetByteBuf.writeInt(this.method_13417(this.field_14939.getText()));
			packetByteBuf.writeInt(this.method_13417(this.field_14940.getText()));
			packetByteBuf.writeInt(this.method_13417(this.field_14906.getText()));
			packetByteBuf.writeInt(this.method_13417(this.field_14907.getText()));
			packetByteBuf.writeString(this.field_14929.method_13351().toString());
			packetByteBuf.writeString(this.field_14929.method_13352().toString());
			packetByteBuf.writeString(this.field_14910.getText());
			packetByteBuf.writeBoolean(this.field_14929.method_13356());
			packetByteBuf.writeBoolean(this.field_14929.method_13335());
			packetByteBuf.writeBoolean(this.field_14929.method_13336());
			packetByteBuf.writeFloat(this.method_13416(this.field_14908.getText()));
			packetByteBuf.method_10608(this.method_13413(this.field_14909.getText()));
			this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|Struct", packetByteBuf));
			return true;
		} catch (Exception var3) {
			LOGGER.warn("Could not send structure block info", var3);
			return false;
		}
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
	protected void keyPressed(char id, int code) {
		if (this.field_14936.isVisible() && method_13414(id, code)) {
			this.field_14936.keyPressed(id, code);
		}

		if (this.field_14937.isVisible()) {
			this.field_14937.keyPressed(id, code);
		}

		if (this.field_14938.isVisible()) {
			this.field_14938.keyPressed(id, code);
		}

		if (this.field_14939.isVisible()) {
			this.field_14939.keyPressed(id, code);
		}

		if (this.field_14940.isVisible()) {
			this.field_14940.keyPressed(id, code);
		}

		if (this.field_14906.isVisible()) {
			this.field_14906.keyPressed(id, code);
		}

		if (this.field_14907.isVisible()) {
			this.field_14907.keyPressed(id, code);
		}

		if (this.field_14908.isVisible()) {
			this.field_14908.keyPressed(id, code);
		}

		if (this.field_14909.isVisible()) {
			this.field_14909.keyPressed(id, code);
		}

		if (this.field_14910.isVisible()) {
			this.field_14910.keyPressed(id, code);
		}

		if (code == 15) {
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
			}
		}

		if (code == 28 || code == 156) {
			this.buttonClicked(this.doneButton);
		} else if (code == 1) {
			this.buttonClicked(this.cancelButton);
		}
	}

	private static boolean method_13414(char c, int i) {
		boolean bl = true;

		for (int j : field_14927) {
			if (j == i) {
				return true;
			}
		}

		for (char d : SharedConstants.field_14996) {
			if (d == c) {
				bl = false;
				break;
			}
		}

		return bl;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		if (this.field_14936.isVisible()) {
			this.field_14936.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14937.isVisible()) {
			this.field_14937.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14938.isVisible()) {
			this.field_14938.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14939.isVisible()) {
			this.field_14939.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14940.isVisible()) {
			this.field_14940.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14906.isVisible()) {
			this.field_14906.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14907.isVisible()) {
			this.field_14907.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14908.isVisible()) {
			this.field_14908.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14909.isVisible()) {
			this.field_14909.mouseClicked(mouseX, mouseY, button);
		}

		if (this.field_14910.isVisible()) {
			this.field_14910.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		StructureBlockEntity.class_2739 lv = this.field_14929.method_13354();
		this.drawCenteredString(this.textRenderer, I18n.translate("tile.structureBlock.name"), this.width / 2, 10, 16777215);
		if (lv != StructureBlockEntity.class_2739.DATA) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
			this.field_14936.render();
		}

		if (lv == StructureBlockEntity.class_2739.LOAD || lv == StructureBlockEntity.class_2739.SAVE) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.position"), this.width / 2 - 153, 70, 10526880);
			this.field_14937.render();
			this.field_14938.render();
			this.field_14939.render();
			String string = I18n.translate("structure_block.include_entities");
			int i = this.textRenderer.getStringWidth(string);
			this.drawWithShadow(this.textRenderer, string, this.width / 2 + 154 - i, 150, 10526880);
		}

		if (lv == StructureBlockEntity.class_2739.SAVE) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.size"), this.width / 2 - 153, 110, 10526880);
			this.field_14940.render();
			this.field_14906.render();
			this.field_14907.render();
			String string2 = I18n.translate("structure_block.detect_size");
			int j = this.textRenderer.getStringWidth(string2);
			this.drawWithShadow(this.textRenderer, string2, this.width / 2 + 154 - j, 110, 10526880);
			String string3 = I18n.translate("structure_block.show_air");
			int k = this.textRenderer.getStringWidth(string3);
			this.drawWithShadow(this.textRenderer, string3, this.width / 2 + 154 - k, 70, 10526880);
		}

		if (lv == StructureBlockEntity.class_2739.LOAD) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
			this.field_14908.render();
			this.field_14909.render();
			String string4 = I18n.translate("structure_block.show_boundingbox");
			int l = this.textRenderer.getStringWidth(string4);
			this.drawWithShadow(this.textRenderer, string4, this.width / 2 + 154 - l, 70, 10526880);
		}

		if (lv == StructureBlockEntity.class_2739.DATA) {
			this.drawWithShadow(this.textRenderer, I18n.translate("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
			this.field_14910.render();
		}

		String string5 = "structure_block.mode_info." + lv.asString();
		this.drawWithShadow(this.textRenderer, I18n.translate(string5), this.width / 2 - 153, 174, 10526880);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}
}
