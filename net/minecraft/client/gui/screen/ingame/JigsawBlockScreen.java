package net.minecraft.client.gui.screen.ingame;

import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class JigsawBlockScreen extends Screen {
	private static final int field_32344 = 7;
	private static final Text JOINT_LABEL_TEXT = new TranslatableText("jigsaw_block.joint_label");
	private static final Text POOL_TEXT = new TranslatableText("jigsaw_block.pool");
	private static final Text NAME_TEXT = new TranslatableText("jigsaw_block.name");
	private static final Text TARGET_TEXT = new TranslatableText("jigsaw_block.target");
	private static final Text FINAL_STATE_TEXT = new TranslatableText("jigsaw_block.final_state");
	private final JigsawBlockEntity jigsaw;
	private TextFieldWidget nameField;
	private TextFieldWidget targetField;
	private TextFieldWidget poolField;
	private TextFieldWidget finalStateField;
	int generationDepth;
	private boolean keepJigsaws = true;
	private CyclingButtonWidget<JigsawBlockEntity.Joint> jointRotationButton;
	private ButtonWidget doneButton;
	private ButtonWidget generateButton;
	private JigsawBlockEntity.Joint joint;

	public JigsawBlockScreen(JigsawBlockEntity jigsaw) {
		super(NarratorManager.EMPTY);
		this.jigsaw = jigsaw;
	}

	@Override
	public void tick() {
		this.nameField.tick();
		this.targetField.tick();
		this.poolField.tick();
		this.finalStateField.tick();
	}

	private void onDone() {
		this.updateServer();
		this.client.openScreen(null);
	}

	private void onCancel() {
		this.client.openScreen(null);
	}

	private void updateServer() {
		this.client
			.getNetworkHandler()
			.sendPacket(
				new UpdateJigsawC2SPacket(
					this.jigsaw.getPos(),
					new Identifier(this.nameField.getText()),
					new Identifier(this.targetField.getText()),
					new Identifier(this.poolField.getText()),
					this.finalStateField.getText(),
					this.joint
				)
			);
	}

	private void generate() {
		this.client.getNetworkHandler().sendPacket(new JigsawGeneratingC2SPacket(this.jigsaw.getPos(), this.generationDepth, this.keepJigsaws));
	}

	@Override
	public void onClose() {
		this.onCancel();
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.poolField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 20, 300, 20, new TranslatableText("jigsaw_block.pool"));
		this.poolField.setMaxLength(128);
		this.poolField.setText(this.jigsaw.getPool().toString());
		this.poolField.setChangedListener(pool -> this.updateDoneButtonState());
		this.addSelectableChild(this.poolField);
		this.nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 55, 300, 20, new TranslatableText("jigsaw_block.name"));
		this.nameField.setMaxLength(128);
		this.nameField.setText(this.jigsaw.getName().toString());
		this.nameField.setChangedListener(name -> this.updateDoneButtonState());
		this.addSelectableChild(this.nameField);
		this.targetField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 90, 300, 20, new TranslatableText("jigsaw_block.target"));
		this.targetField.setMaxLength(128);
		this.targetField.setText(this.jigsaw.getTarget().toString());
		this.targetField.setChangedListener(target -> this.updateDoneButtonState());
		this.addSelectableChild(this.targetField);
		this.finalStateField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 125, 300, 20, new TranslatableText("jigsaw_block.final_state"));
		this.finalStateField.setMaxLength(256);
		this.finalStateField.setText(this.jigsaw.getFinalState());
		this.addSelectableChild(this.finalStateField);
		this.joint = this.jigsaw.getJoint();
		int i = this.textRenderer.getWidth(JOINT_LABEL_TEXT) + 10;
		this.jointRotationButton = this.addDrawableChild(
			CyclingButtonWidget.<JigsawBlockEntity.Joint>builder(JigsawBlockEntity.Joint::asText)
				.values(JigsawBlockEntity.Joint.values())
				.initially(this.joint)
				.omitKeyText()
				.build(this.width / 2 - 152 + i, 150, 300 - i, 20, JOINT_LABEL_TEXT, (button, joint) -> this.joint = joint)
		);
		boolean bl = JigsawBlock.getFacing(this.jigsaw.getCachedState()).getAxis().isVertical();
		this.jointRotationButton.active = bl;
		this.jointRotationButton.visible = bl;
		this.addDrawableChild(new SliderWidget(this.width / 2 - 154, 180, 100, 20, LiteralText.EMPTY, 0.0) {
			{
				this.updateMessage();
			}

			@Override
			protected void updateMessage() {
				this.setMessage(new TranslatableText("jigsaw_block.levels", JigsawBlockScreen.this.generationDepth));
			}

			@Override
			protected void applyValue() {
				JigsawBlockScreen.this.generationDepth = MathHelper.floor(MathHelper.clampedLerp(0.0, 7.0, this.value));
			}
		});
		this.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.keepJigsaws)
				.build(this.width / 2 - 50, 180, 100, 20, new TranslatableText("jigsaw_block.keep_jigsaws"), (button, keepJigsaws) -> this.keepJigsaws = keepJigsaws)
		);
		this.generateButton = this.addDrawableChild(new ButtonWidget(this.width / 2 + 54, 180, 100, 20, new TranslatableText("jigsaw_block.generate"), button -> {
			this.onDone();
			this.generate();
		}));
		this.doneButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, ScreenTexts.DONE, button -> this.onDone()));
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, ScreenTexts.CANCEL, button -> this.onCancel()));
		this.setInitialFocus(this.poolField);
		this.updateDoneButtonState();
	}

	private void updateDoneButtonState() {
		boolean bl = Identifier.isValid(this.nameField.getText()) && Identifier.isValid(this.targetField.getText()) && Identifier.isValid(this.poolField.getText());
		this.doneButton.active = bl;
		this.generateButton.active = bl;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.nameField.getText();
		String string2 = this.targetField.getText();
		String string3 = this.poolField.getText();
		String string4 = this.finalStateField.getText();
		int i = this.generationDepth;
		JigsawBlockEntity.Joint joint = this.joint;
		this.init(client, width, height);
		this.nameField.setText(string);
		this.targetField.setText(string2);
		this.poolField.setText(string3);
		this.finalStateField.setText(string4);
		this.generationDepth = i;
		this.joint = joint;
		this.jointRotationButton.setValue(joint);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (!this.doneButton.active || keyCode != 257 && keyCode != 335) {
			return false;
		} else {
			this.onDone();
			return true;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawTextWithShadow(matrices, this.textRenderer, POOL_TEXT, this.width / 2 - 153, 10, 10526880);
		this.poolField.render(matrices, mouseX, mouseY, delta);
		drawTextWithShadow(matrices, this.textRenderer, NAME_TEXT, this.width / 2 - 153, 45, 10526880);
		this.nameField.render(matrices, mouseX, mouseY, delta);
		drawTextWithShadow(matrices, this.textRenderer, TARGET_TEXT, this.width / 2 - 153, 80, 10526880);
		this.targetField.render(matrices, mouseX, mouseY, delta);
		drawTextWithShadow(matrices, this.textRenderer, FINAL_STATE_TEXT, this.width / 2 - 153, 115, 10526880);
		this.finalStateField.render(matrices, mouseX, mouseY, delta);
		if (JigsawBlock.getFacing(this.jigsaw.getCachedState()).getAxis().isVertical()) {
			drawTextWithShadow(matrices, this.textRenderer, JOINT_LABEL_TEXT, this.width / 2 - 153, 156, 16777215);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
