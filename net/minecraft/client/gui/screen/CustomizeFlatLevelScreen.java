package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3917;
import net.minecraft.class_4122;
import net.minecraft.class_4372;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.gen.layer.FlatWorldLayer;

public class CustomizeFlatLevelScreen extends Screen {
	private final CreateWorldScreen parent;
	private class_3917 field_5062 = class_3917.method_17501();
	private String title;
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelScreen.CustomizeFlatLevelListWidget layerList;
	private ButtonWidget addLayer;
	private ButtonWidget editLayer;
	private ButtonWidget removeLayer;

	public CustomizeFlatLevelScreen(CreateWorldScreen createWorldScreen, NbtCompound nbtCompound) {
		this.parent = createWorldScreen;
		this.method_18573(nbtCompound);
	}

	public String getConfigString() {
		return this.field_5062.toString();
	}

	public NbtCompound method_18577() {
		return (NbtCompound)this.field_5062.method_17481(class_4372.field_21487).getValue();
	}

	public void setConfigHelper(String config) {
		this.field_5062 = class_3917.method_17492(config);
	}

	public void method_18573(NbtCompound nbtCompound) {
		this.field_5062 = class_3917.method_17480(new Dynamic(class_4372.field_21487, nbtCompound));
	}

	@Override
	protected void init() {
		this.title = I18n.translate("createWorld.customize.flat.title");
		this.tileText = I18n.translate("createWorld.customize.flat.tile");
		this.heightText = I18n.translate("createWorld.customize.flat.height");
		this.layerList = new CustomizeFlatLevelScreen.CustomizeFlatLevelListWidget();
		this.field_20307.add(this.layerList);
		this.addLayer = this.addButton(
			new ButtonWidget(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.addLayer") + " (NYI)") {
				@Override
				public void method_18374(double d, double e) {
					CustomizeFlatLevelScreen.this.field_5062.method_17500();
					CustomizeFlatLevelScreen.this.updateButtons();
				}
			}
		);
		this.editLayer = this.addButton(
			new ButtonWidget(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.editLayer") + " (NYI)") {
				@Override
				public void method_18374(double d, double e) {
					CustomizeFlatLevelScreen.this.field_5062.method_17500();
					CustomizeFlatLevelScreen.this.updateButtons();
				}
			}
		);
		this.removeLayer = this.addButton(
			new ButtonWidget(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer")) {
				@Override
				public void method_18374(double d, double e) {
					if (CustomizeFlatLevelScreen.this.hasLayerSelected()) {
						List<FlatWorldLayer> list = CustomizeFlatLevelScreen.this.field_5062.method_17499();
						int i = list.size() - CustomizeFlatLevelScreen.this.layerList.focusedEntry - 1;
						list.remove(i);
						CustomizeFlatLevelScreen.this.layerList.focusedEntry = Math.min(CustomizeFlatLevelScreen.this.layerList.focusedEntry, list.size() - 1);
						CustomizeFlatLevelScreen.this.field_5062.method_17500();
						CustomizeFlatLevelScreen.this.updateButtons();
					}
				}
			}
		);
		this.addButton(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				CustomizeFlatLevelScreen.this.parent.field_20472 = CustomizeFlatLevelScreen.this.method_18577();
				CustomizeFlatLevelScreen.this.client.setScreen(CustomizeFlatLevelScreen.this.parent);
				CustomizeFlatLevelScreen.this.field_5062.method_17500();
				CustomizeFlatLevelScreen.this.updateButtons();
			}
		});
		this.addButton(new ButtonWidget(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets")) {
			@Override
			public void method_18374(double d, double e) {
				CustomizeFlatLevelScreen.this.client.setScreen(new SuperflatPresetScreen(CustomizeFlatLevelScreen.this));
				CustomizeFlatLevelScreen.this.field_5062.method_17500();
				CustomizeFlatLevelScreen.this.updateButtons();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				CustomizeFlatLevelScreen.this.client.setScreen(CustomizeFlatLevelScreen.this.parent);
				CustomizeFlatLevelScreen.this.field_5062.method_17500();
				CustomizeFlatLevelScreen.this.updateButtons();
			}
		});
		this.addLayer.visible = false;
		this.editLayer.visible = false;
		this.field_5062.method_17500();
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
		return this.layerList.focusedEntry > -1 && this.layerList.focusedEntry < this.field_5062.method_17499().size();
	}

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.layerList;
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
				CustomizeFlatLevelScreen.this.field_20308.method_19376(iconItem, x + 2, y + 2);
				DiffuseLighting.disable();
			}

			GlStateManager.disableRescaleNormal();
		}

		private void renderIconBackground(int x, int y) {
			this.renderIconBackground(x, y, 0, 0);
		}

		private void renderIconBackground(int x, int y, int u, int v) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(STATS_ICON_TEXTURE);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int i = 18;
			int j = 18;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex((double)(x + 0), (double)(y + 18), (double)this.zOffset)
				.texture((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 18), (double)(y + 18), (double)this.zOffset)
				.texture((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 18), (double)(y + 0), (double)this.zOffset)
				.texture((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zOffset)
				.texture((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F))
				.next();
			tessellator.draw();
		}

		@Override
		protected int getEntryCount() {
			return CustomizeFlatLevelScreen.this.field_5062.method_17499().size();
		}

		@Override
		protected boolean method_18414(int i, int j, double d, double e) {
			this.focusedEntry = i;
			CustomizeFlatLevelScreen.this.updateButtons();
			return true;
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return index == this.focusedEntry;
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			FlatWorldLayer flatWorldLayer = (FlatWorldLayer)CustomizeFlatLevelScreen.this.field_5062
				.method_17499()
				.get(CustomizeFlatLevelScreen.this.field_5062.method_17499().size() - i - 1);
			BlockState blockState = flatWorldLayer.getBlockState();
			Block block = blockState.getBlock();
			Item item = block.getItem();
			if (item == Items.AIR) {
				if (block == Blocks.WATER) {
					item = Items.WATER_BUCKET;
				} else if (block == Blocks.LAVA) {
					item = Items.LAVA_BUCKET;
				}
			}

			ItemStack itemStack = new ItemStack(item);
			String string = item.getDisplayName(itemStack).asFormattedString();
			this.renderEntry(j, k, itemStack);
			CustomizeFlatLevelScreen.this.textRenderer.method_18355(string, (float)(j + 18 + 5), (float)(k + 3), 16777215);
			String string2;
			if (i == 0) {
				string2 = I18n.translate("createWorld.customize.flat.layer.top", flatWorldLayer.getThickness());
			} else if (i == CustomizeFlatLevelScreen.this.field_5062.method_17499().size() - 1) {
				string2 = I18n.translate("createWorld.customize.flat.layer.bottom", flatWorldLayer.getThickness());
			} else {
				string2 = I18n.translate("createWorld.customize.flat.layer", flatWorldLayer.getThickness());
			}

			CustomizeFlatLevelScreen.this.textRenderer
				.method_18355(string2, (float)(j + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getStringWidth(string2)), (float)(k + 3), 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}
	}
}
