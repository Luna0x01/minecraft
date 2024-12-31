package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_3917;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.layer.FlatWorldLayer;

public class SuperflatPresetScreen extends Screen {
	private static final List<SuperflatPresetScreen.PresetEntry> PRESETS = Lists.newArrayList();
	private final CustomizeFlatLevelScreen parent;
	private String title;
	private String shareText;
	private String listText;
	private SuperflatPresetScreen.SuperflatPresetsListWidget listWidget;
	private ButtonWidget selectButton;
	private TextFieldWidget customPresetField;

	public SuperflatPresetScreen(CustomizeFlatLevelScreen customizeFlatLevelScreen) {
		this.parent = customizeFlatLevelScreen;
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.title = I18n.translate("createWorld.customize.presets.title");
		this.shareText = I18n.translate("createWorld.customize.presets.share");
		this.listText = I18n.translate("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(2, this.textRenderer, 50, 40, this.width - 100, 20);
		this.listWidget = new SuperflatPresetScreen.SuperflatPresetsListWidget();
		this.field_20307.add(this.listWidget);
		this.customPresetField.setMaxLength(1230);
		this.customPresetField.setText(this.parent.getConfigString());
		this.field_20307.add(this.customPresetField);
		this.selectButton = this.addButton(
			new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select")) {
				@Override
				public void method_18374(double d, double e) {
					SuperflatPresetScreen.this.parent.setConfigHelper(SuperflatPresetScreen.this.customPresetField.getText());
					SuperflatPresetScreen.this.client.setScreen(SuperflatPresetScreen.this.parent);
				}
			}
		);
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				SuperflatPresetScreen.this.client.setScreen(SuperflatPresetScreen.this.parent);
			}
		});
		this.updateSelectButton();
		this.method_18421(this.listWidget);
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.listWidget.mouseScrolled(d);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.customPresetField.getText();
		this.init(client, width, height);
		this.customPresetField.setText(string);
	}

	@Override
	public void removed() {
		this.client.field_19946.method_18191(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.listWidget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawWithShadow(this.textRenderer, this.shareText, 50, 30, 10526880);
		this.drawWithShadow(this.textRenderer, this.listText, 50, 70, 10526880);
		this.customPresetField.method_18385(mouseX, mouseY, tickDelta);
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public void tick() {
		this.customPresetField.tick();
		super.tick();
	}

	public void updateSelectButton() {
		this.selectButton.active = this.isEntrySelected();
	}

	private boolean isEntrySelected() {
		return this.listWidget.selectedEntryIndex > -1 && this.listWidget.selectedEntryIndex < PRESETS.size() || this.customPresetField.getText().length() > 1;
	}

	private static void method_18603(String string, Itemable itemable, Biome biome, List<String> list, FlatWorldLayer... flatWorldLayers) {
		class_3917 lv = ChunkGeneratorType.FLAT.method_17040();

		for (int i = flatWorldLayers.length - 1; i >= 0; i--) {
			lv.method_17499().add(flatWorldLayers[i]);
		}

		lv.method_17476(biome);
		lv.method_17500();

		for (String string2 : list) {
			lv.method_17498().put(string2, Maps.newHashMap());
		}

		PRESETS.add(new SuperflatPresetScreen.PresetEntry(itemable.getItem(), string, lv.toString()));
	}

	static {
		method_18603(
			I18n.translate("createWorld.customize.preset.classic_flat"),
			Blocks.GRASS_BLOCK,
			Biomes.PLAINS,
			Arrays.asList("village"),
			new FlatWorldLayer(1, Blocks.GRASS_BLOCK),
			new FlatWorldLayer(2, Blocks.DIRT),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.tunnelers_dream"),
			Blocks.STONE,
			Biomes.EXTREME_HILLS,
			Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
			new FlatWorldLayer(1, Blocks.GRASS_BLOCK),
			new FlatWorldLayer(5, Blocks.DIRT),
			new FlatWorldLayer(230, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.water_world"),
			Items.WATER_BUCKET,
			Biomes.DEEP_OCEAN,
			Arrays.asList("biome_1", "oceanmonument"),
			new FlatWorldLayer(90, Blocks.WATER),
			new FlatWorldLayer(5, Blocks.SAND),
			new FlatWorldLayer(5, Blocks.DIRT),
			new FlatWorldLayer(5, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.overworld"),
			Blocks.GRASS,
			Biomes.PLAINS,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
			new FlatWorldLayer(1, Blocks.GRASS_BLOCK),
			new FlatWorldLayer(3, Blocks.DIRT),
			new FlatWorldLayer(59, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.snowy_kingdom"),
			Blocks.SNOW,
			Biomes.ICE_FLATS,
			Arrays.asList("village", "biome_1"),
			new FlatWorldLayer(1, Blocks.SNOW),
			new FlatWorldLayer(1, Blocks.GRASS_BLOCK),
			new FlatWorldLayer(3, Blocks.DIRT),
			new FlatWorldLayer(59, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.bottomless_pit"),
			Items.FEATHER,
			Biomes.PLAINS,
			Arrays.asList("village", "biome_1"),
			new FlatWorldLayer(1, Blocks.GRASS_BLOCK),
			new FlatWorldLayer(3, Blocks.DIRT),
			new FlatWorldLayer(2, Blocks.COBBLESTONE)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.desert"),
			Blocks.SAND,
			Biomes.DESERT,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
			new FlatWorldLayer(8, Blocks.SAND),
			new FlatWorldLayer(52, Blocks.SANDSTONE),
			new FlatWorldLayer(3, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.redstone_ready"),
			Items.REDSTONE,
			Biomes.DESERT,
			Collections.emptyList(),
			new FlatWorldLayer(52, Blocks.SANDSTONE),
			new FlatWorldLayer(3, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		method_18603(
			I18n.translate("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.VOID, Arrays.asList("decoration"), new FlatWorldLayer(1, Blocks.AIR)
		);
	}

	static class PresetEntry {
		public Item icon;
		public String name;
		public String config;

		public PresetEntry(Item item, String string, String string2) {
			this.icon = item;
			this.name = string;
			this.config = string2;
		}
	}

	class SuperflatPresetsListWidget extends ListWidget {
		public int selectedEntryIndex = -1;

		public SuperflatPresetsListWidget() {
			super(SuperflatPresetScreen.this.client, SuperflatPresetScreen.this.width, SuperflatPresetScreen.this.height, 80, SuperflatPresetScreen.this.height - 37, 24);
		}

		private void method_9576(int i, int j, Item item) {
			this.renderIconBackground(i + 1, j + 1);
			GlStateManager.enableRescaleNormal();
			DiffuseLighting.enable();
			SuperflatPresetScreen.this.field_20308.method_19376(new ItemStack(item), i + 2, j + 2);
			DiffuseLighting.disable();
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
			return SuperflatPresetScreen.PRESETS.size();
		}

		@Override
		protected boolean method_18414(int i, int j, double d, double e) {
			this.selectedEntryIndex = i;
			SuperflatPresetScreen.this.updateSelectButton();
			SuperflatPresetScreen.this.customPresetField
				.setText(((SuperflatPresetScreen.PresetEntry)SuperflatPresetScreen.PRESETS.get(SuperflatPresetScreen.this.listWidget.selectedEntryIndex)).config);
			SuperflatPresetScreen.this.customPresetField.setCursorToStart();
			return true;
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return index == this.selectedEntryIndex;
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			SuperflatPresetScreen.PresetEntry presetEntry = (SuperflatPresetScreen.PresetEntry)SuperflatPresetScreen.PRESETS.get(i);
			this.method_9576(j, k, presetEntry.icon);
			SuperflatPresetScreen.this.textRenderer.method_18355(presetEntry.name, (float)(j + 18 + 5), (float)(k + 6), 16777215);
		}
	}
}
