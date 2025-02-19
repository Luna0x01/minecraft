package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Matrix4f;

public class SignEditScreen extends Screen {
	private final SignBlockEntity sign;
	private int ticksSinceOpened;
	private int currentRow;
	private SelectionManager selectionManager;
	private SignType signType;
	private SignBlockEntityRenderer.SignModel model;
	private final String[] text;

	public SignEditScreen(SignBlockEntity sign, boolean filtered) {
		super(new TranslatableText("sign.edit"));
		this.text = (String[])IntStream.range(0, 4).mapToObj(row -> sign.getTextOnRow(row, filtered)).map(Text::getString).toArray(String[]::new);
		this.sign = sign;
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, ScreenTexts.DONE, button -> this.finishEditing()));
		this.sign.setEditable(false);
		this.selectionManager = new SelectionManager(() -> this.text[this.currentRow], text -> {
			this.text[this.currentRow] = text;
			this.sign.setTextOnRow(this.currentRow, new LiteralText(text));
		}, SelectionManager.makeClipboardGetter(this.client), SelectionManager.makeClipboardSetter(this.client), text -> this.client.textRenderer.getWidth(text)
				<= 90);
		BlockState blockState = this.sign.getCachedState();
		this.signType = SignBlockEntityRenderer.getSignType(blockState.getBlock());
		this.model = SignBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType);
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.text[0], this.text[1], this.text[2], this.text[3]));
		}

		this.sign.setEditable(true);
	}

	@Override
	public void tick() {
		this.ticksSinceOpened++;
		if (!this.sign.getType().supports(this.sign.getCachedState())) {
			this.finishEditing();
		}
	}

	private void finishEditing() {
		this.sign.markDirty();
		this.client.openScreen(null);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		this.selectionManager.insert(chr);
		return true;
	}

	@Override
	public void onClose() {
		this.finishEditing();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 265) {
			this.currentRow = this.currentRow - 1 & 3;
			this.selectionManager.putCursorAtEnd();
			return true;
		} else if (keyCode == 264 || keyCode == 257 || keyCode == 335) {
			this.currentRow = this.currentRow + 1 & 3;
			this.selectionManager.putCursorAtEnd();
			return true;
		} else {
			return this.selectionManager.handleSpecialKey(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		DiffuseLighting.disableGuiDepthLighting();
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 16777215);
		matrices.push();
		matrices.translate((double)(this.width / 2), 0.0, 50.0);
		float f = 93.75F;
		matrices.scale(93.75F, -93.75F, 93.75F);
		matrices.translate(0.0, -1.3125, 0.0);
		BlockState blockState = this.sign.getCachedState();
		boolean bl = blockState.getBlock() instanceof SignBlock;
		if (!bl) {
			matrices.translate(0.0, -0.3125, 0.0);
		}

		boolean bl2 = this.ticksSinceOpened / 6 % 2 == 0;
		float g = 0.6666667F;
		matrices.push();
		matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
		VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
		SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(this.signType);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(immediate, this.model::getLayer);
		this.model.stick.visible = bl;
		this.model.root.render(matrices, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
		matrices.pop();
		float h = 0.010416667F;
		matrices.translate(0.0, 0.33333334F, 0.046666667F);
		matrices.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = this.sign.getTextColor().getSignColor();
		int j = this.selectionManager.getSelectionStart();
		int k = this.selectionManager.getSelectionEnd();
		int l = this.currentRow * 10 - this.text.length * 5;
		Matrix4f matrix4f = matrices.peek().getModel();

		for (int m = 0; m < this.text.length; m++) {
			String string = this.text[m];
			if (string != null) {
				if (this.textRenderer.isRightToLeft()) {
					string = this.textRenderer.mirror(string);
				}

				float n = (float)(-this.client.textRenderer.getWidth(string) / 2);
				this.client.textRenderer.draw(string, n, (float)(m * 10 - this.text.length * 5), i, false, matrix4f, immediate, false, 0, 15728880, false);
				if (m == this.currentRow && j >= 0 && bl2) {
					int o = this.client.textRenderer.getWidth(string.substring(0, Math.max(Math.min(j, string.length()), 0)));
					int p = o - this.client.textRenderer.getWidth(string) / 2;
					if (j >= string.length()) {
						this.client.textRenderer.draw("_", (float)p, (float)l, i, false, matrix4f, immediate, false, 0, 15728880, false);
					}
				}
			}
		}

		immediate.draw();

		for (int q = 0; q < this.text.length; q++) {
			String string2 = this.text[q];
			if (string2 != null && q == this.currentRow && j >= 0) {
				int r = this.client.textRenderer.getWidth(string2.substring(0, Math.max(Math.min(j, string2.length()), 0)));
				int s = r - this.client.textRenderer.getWidth(string2) / 2;
				if (bl2 && j < string2.length()) {
					fill(matrices, s, l - 1, s + 1, l + 9, 0xFF000000 | i);
				}

				if (k != j) {
					int t = Math.min(j, k);
					int u = Math.max(j, k);
					int v = this.client.textRenderer.getWidth(string2.substring(0, t)) - this.client.textRenderer.getWidth(string2) / 2;
					int w = this.client.textRenderer.getWidth(string2.substring(0, u)) - this.client.textRenderer.getWidth(string2) / 2;
					int x = Math.min(v, w);
					int y = Math.max(v, w);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					RenderSystem.setShader(GameRenderer::getPositionColorShader);
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex(matrix4f, (float)x, (float)(l + 9), 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, (float)y, (float)(l + 9), 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, (float)y, (float)l, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, (float)x, (float)l, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.end();
					BufferRenderer.draw(bufferBuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrices.pop();
		DiffuseLighting.enableGuiDepthLighting();
		super.render(matrices, mouseX, mouseY, delta);
	}
}
