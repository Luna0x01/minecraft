package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.gen.FlatWorldHelper;
import net.minecraft.world.gen.layer.FlatWorldLayer;

public class CustomizeFlatLevelScreen extends Screen {
	private final CreateWorldScreen parent;
	private FlatWorldHelper helper = FlatWorldHelper.createDefault();
	private String title;
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelScreen.CustomizeFlatLevelListWidget layerList;
	private ButtonWidget addLayer;
	private ButtonWidget editLayer;
	private ButtonWidget removeLayer;

	public CustomizeFlatLevelScreen(CreateWorldScreen createWorldScreen, String string) {
		this.parent = createWorldScreen;
		this.setConfigHelper(string);
	}

	public String getConfigString() {
		return this.helper.toString();
	}

	public void setConfigHelper(String config) {
		this.helper = FlatWorldHelper.getHelper(config);
	}

	@Override
	public void init() {
		this.buttons.clear();
		this.title = I18n.translate("createWorld.customize.flat.title");
		this.tileText = I18n.translate("createWorld.customize.flat.tile");
		this.heightText = I18n.translate("createWorld.customize.flat.height");
		this.layerList = new CustomizeFlatLevelScreen.CustomizeFlatLevelListWidget();
		this.addLayer = this.addButton(
			new ButtonWidget(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.addLayer") + " (NYI)")
		);
		this.editLayer = this.addButton(
			new ButtonWidget(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.editLayer") + " (NYI)")
		);
		this.removeLayer = this.addButton(
			new ButtonWidget(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer"))
		);
		this.buttons.add(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")));
		this.buttons.add(new ButtonWidget(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
		this.addLayer.visible = false;
		this.editLayer.visible = false;
		this.helper.updateLayerLevel();
		this.updateButtons();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.layerList.handleMouse();
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		int i = this.helper.getLayers().size() - this.layerList.focusedEntry - 1;
		if (button.id == 1) {
			this.client.setScreen(this.parent);
		} else if (button.id == 0) {
			this.parent.generatorOptions = this.getConfigString();
			this.client.setScreen(this.parent);
		} else if (button.id == 5) {
			this.client.setScreen(new SuperflatPresetScreen(this));
		} else if (button.id == 4 && this.hasLayerSelected()) {
			this.helper.getLayers().remove(i);
			this.layerList.focusedEntry = Math.min(this.layerList.focusedEntry, this.helper.getLayers().size() - 1);
		}

		this.helper.updateLayerLevel();
		this.updateButtons();
	}

	public void updateButtons() {
		boolean bl = this.hasLayerSelected();
		this.removeLayer.active = bl;
		this.editLayer.active = bl;
		this.editLayer.active = false;
		this.addLayer.active = false;
	}

	private boolean hasLayerSelected() {
		return this.layerList.focusedEntry > -1 && this.layerList.focusedEntry < this.helper.getLayers().size();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.layerList.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		int i = this.width / 2 - 92 - 16;
		this.drawWithShadow(this.textRenderer, this.tileText, i, 32, 16777215);
		this.drawWithShadow(this.textRenderer, this.heightText, i + 2 + 213 - this.textRenderer.getStringWidth(this.heightText), 32, 16777215);
		super.render(mouseX, mouseY, tickDelta);
	}

	class CustomizeFlatLevelListWidget extends ListWidget {
		public int focusedEntry = -1;

		public CustomizeFlatLevelListWidget() {
			super(
				CustomizeFlatLevelScreen.this.client,
				CustomizeFlatLevelScreen.this.width,
				CustomizeFlatLevelScreen.this.height,
				43,
				CustomizeFlatLevelScreen.this.height - 60,
				24
			);
		}

		private void renderEntry(int x, int y, ItemStack iconItem) {
			this.renderIconBackground(x + 1, y + 1);
			GlStateManager.enableRescaleNormal();
			if (!iconItem.isEmpty()) {
				DiffuseLighting.enable();
				CustomizeFlatLevelScreen.this.itemRenderer.method_12455(iconItem, x + 2, y + 2);
				DiffuseLighting.disable();
			}

			GlStateManager.disableRescaleNormal();
		}

		private void renderIconBackground(int x, int y) {
			this.renderIconBackground(x, y, 0, 0);
		}

		private void renderIconBackground(int x, int y, int u, int v) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int i = 18;
			int j = 18;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex((double)(x + 0), (double)(y + 18), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 18), (double)(y + 18), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 18), (double)(y + 0), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 0), (double)(y + 0), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F))
				.next();
			tessellator.draw();
		}

		@Override
		protected int getEntryCount() {
			return CustomizeFlatLevelScreen.this.helper.getLayers().size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
			this.focusedEntry = index;
			CustomizeFlatLevelScreen.this.updateButtons();
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return index == this.focusedEntry;
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
			FlatWorldLayer flatWorldLayer = (FlatWorldLayer)CustomizeFlatLevelScreen.this.helper
				.getLayers()
				.get(CustomizeFlatLevelScreen.this.helper.getLayers().size() - index - 1);
			BlockState blockState = flatWorldLayer.getBlockState();
			Block block = blockState.getBlock();
			Item item = Item.fromBlock(block);
			if (item == Items.AIR) {
				if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
					item = Items.WATER_BUCKET;
				} else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
					item = Items.LAVA_BUCKET;
				}
			}

			ItemStack itemStack = new ItemStack(item, 1, item.isUnbreakable() ? block.getData(blockState) : 0);
			String string = item.getDisplayName(itemStack);
			this.renderEntry(x, y, itemStack);
			CustomizeFlatLevelScreen.this.textRenderer.draw(string, x + 18 + 5, y + 3, 16777215);
			String string2;
			if (index == 0) {
				string2 = I18n.translate("createWorld.customize.flat.layer.top", flatWorldLayer.getThickness());
			} else if (index == CustomizeFlatLevelScreen.this.helper.getLayers().size() - 1) {
				string2 = I18n.translate("createWorld.customize.flat.layer.bottom", flatWorldLayer.getThickness());
			} else {
				string2 = I18n.translate("createWorld.customize.flat.layer", flatWorldLayer.getThickness());
			}

			CustomizeFlatLevelScreen.this.textRenderer.draw(string2, x + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getStringWidth(string2), y + 3, 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}
	}
}
