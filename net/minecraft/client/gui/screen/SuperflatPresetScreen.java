package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
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
import net.minecraft.item.Items;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatWorldHelper;
import net.minecraft.world.gen.layer.FlatWorldLayer;
import org.lwjgl.input.Keyboard;

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
	public void init() {
		this.buttons.clear();
		Keyboard.enableRepeatEvents(true);
		this.title = I18n.translate("createWorld.customize.presets.title");
		this.shareText = I18n.translate("createWorld.customize.presets.share");
		this.listText = I18n.translate("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(2, this.textRenderer, 50, 40, this.width - 100, 20);
		this.listWidget = new SuperflatPresetScreen.SuperflatPresetsListWidget();
		this.customPresetField.setMaxLength(1230);
		this.customPresetField.setText(this.parent.getConfigString());
		this.buttons
			.add(this.selectButton = new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
		this.updateSelectButton();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.listWidget.handleMouse();
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		this.customPresetField.mouseClicked(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (!this.customPresetField.keyPressed(id, code)) {
			super.keyPressed(id, code);
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0 && this.isEntrySelected()) {
			this.parent.setConfigHelper(this.customPresetField.getText());
			this.client.setScreen(this.parent);
		} else if (button.id == 1) {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.listWidget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawWithShadow(this.textRenderer, this.shareText, 50, 30, 10526880);
		this.drawWithShadow(this.textRenderer, this.listText, 50, 70, 10526880);
		this.customPresetField.render();
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public void tick() {
		this.customPresetField.tick();
		super.tick();
	}

	public void updateSelectButton() {
		boolean bl = this.isEntrySelected();
		this.selectButton.active = bl;
	}

	private boolean isEntrySelected() {
		return this.listWidget.selectedEntryIndex > -1 && this.listWidget.selectedEntryIndex < PRESETS.size() || this.customPresetField.getText().length() > 1;
	}

	private static void addRedstonePreset(String name, Item icon, Biome iconDamage, FlatWorldLayer... layers) {
		addOverworldPreset(name, icon, 0, iconDamage, null, layers);
	}

	private static void addPreset(String name, Item icon, Biome biome, List<String> structures, FlatWorldLayer... layers) {
		addOverworldPreset(name, icon, 0, biome, structures, layers);
	}

	private static void addOverworldPreset(String name, Item icon, int iconDamage, Biome biome, List<String> structures, FlatWorldLayer... layers) {
		FlatWorldHelper flatWorldHelper = new FlatWorldHelper();

		for (int i = layers.length - 1; i >= 0; i--) {
			flatWorldHelper.getLayers().add(layers[i]);
		}

		flatWorldHelper.setBiomeId(biome.id);
		flatWorldHelper.updateLayerLevel();
		if (structures != null) {
			for (String string : structures) {
				flatWorldHelper.getStructures().put(string, Maps.newHashMap());
			}
		}

		PRESETS.add(new SuperflatPresetScreen.PresetEntry(icon, iconDamage, name, flatWorldHelper.toString()));
	}

	static {
		addPreset(
			"Classic Flat",
			Item.fromBlock(Blocks.GRASS),
			Biome.PLAINS,
			Arrays.asList("village"),
			new FlatWorldLayer(1, Blocks.GRASS),
			new FlatWorldLayer(2, Blocks.DIRT),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			"Tunnelers' Dream",
			Item.fromBlock(Blocks.STONE),
			Biome.EXTREME_HILLS,
			Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
			new FlatWorldLayer(1, Blocks.GRASS),
			new FlatWorldLayer(5, Blocks.DIRT),
			new FlatWorldLayer(230, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			"Water World",
			Items.WATER_BUCKET,
			Biome.DEEP_OCEAN,
			Arrays.asList("biome_1", "oceanmonument"),
			new FlatWorldLayer(90, Blocks.WATER),
			new FlatWorldLayer(5, Blocks.SAND),
			new FlatWorldLayer(5, Blocks.DIRT),
			new FlatWorldLayer(5, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		addOverworldPreset(
			"Overworld",
			Item.fromBlock(Blocks.TALLGRASS),
			TallPlantBlock.GrassType.GRASS.getId(),
			Biome.PLAINS,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
			new FlatWorldLayer(1, Blocks.GRASS),
			new FlatWorldLayer(3, Blocks.DIRT),
			new FlatWorldLayer(59, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			"Snowy Kingdom",
			Item.fromBlock(Blocks.SNOW_LAYER),
			Biome.ICE_PLAINS,
			Arrays.asList("village", "biome_1"),
			new FlatWorldLayer(1, Blocks.SNOW_LAYER),
			new FlatWorldLayer(1, Blocks.GRASS),
			new FlatWorldLayer(3, Blocks.DIRT),
			new FlatWorldLayer(59, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			"Bottomless Pit",
			Items.FEATHER,
			Biome.PLAINS,
			Arrays.asList("village", "biome_1"),
			new FlatWorldLayer(1, Blocks.GRASS),
			new FlatWorldLayer(3, Blocks.DIRT),
			new FlatWorldLayer(2, Blocks.COBBLESTONE)
		);
		addPreset(
			"Desert",
			Item.fromBlock(Blocks.SAND),
			Biome.DESERT,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
			new FlatWorldLayer(8, Blocks.SAND),
			new FlatWorldLayer(52, Blocks.SANDSTONE),
			new FlatWorldLayer(3, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
		addRedstonePreset(
			"Redstone Ready",
			Items.REDSTONE,
			Biome.DESERT,
			new FlatWorldLayer(52, Blocks.SANDSTONE),
			new FlatWorldLayer(3, Blocks.STONE),
			new FlatWorldLayer(1, Blocks.BEDROCK)
		);
	}

	static class PresetEntry {
		public Item icon;
		public int iconDamage;
		public String name;
		public String config;

		public PresetEntry(Item item, int i, String string, String string2) {
			this.icon = item;
			this.iconDamage = i;
			this.name = string;
			this.config = string2;
		}
	}

	class SuperflatPresetsListWidget extends ListWidget {
		public int selectedEntryIndex = -1;

		public SuperflatPresetsListWidget() {
			super(SuperflatPresetScreen.this.client, SuperflatPresetScreen.this.width, SuperflatPresetScreen.this.height, 80, SuperflatPresetScreen.this.height - 37, 24);
		}

		private void renderEntry(int x, int y, Item item, int damage) {
			this.renderIconBackground(x + 1, y + 1);
			GlStateManager.enableRescaleNormal();
			DiffuseLighting.enable();
			SuperflatPresetScreen.this.itemRenderer.renderGuiItemModel(new ItemStack(item, 1, damage), x + 2, y + 2);
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
			bufferBuilder.vertex((double)(x + 0), (double)(y + 18), (double)SuperflatPresetScreen.this.zOffset)
				.texture((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 18), (double)(y + 18), (double)SuperflatPresetScreen.this.zOffset)
				.texture((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 18), (double)(y + 0), (double)SuperflatPresetScreen.this.zOffset)
				.texture((double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(x + 0), (double)(y + 0), (double)SuperflatPresetScreen.this.zOffset)
				.texture((double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F))
				.next();
			tessellator.draw();
		}

		@Override
		protected int getEntryCount() {
			return SuperflatPresetScreen.PRESETS.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
			this.selectedEntryIndex = index;
			SuperflatPresetScreen.this.updateSelectButton();
			SuperflatPresetScreen.this.customPresetField
				.setText(((SuperflatPresetScreen.PresetEntry)SuperflatPresetScreen.PRESETS.get(SuperflatPresetScreen.this.listWidget.selectedEntryIndex)).config);
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return index == this.selectedEntryIndex;
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
			SuperflatPresetScreen.PresetEntry presetEntry = (SuperflatPresetScreen.PresetEntry)SuperflatPresetScreen.PRESETS.get(index);
			this.renderEntry(x, y, presetEntry.icon, presetEntry.iconDamage);
			SuperflatPresetScreen.this.textRenderer.draw(presetEntry.name, x + 18 + 5, y + 6, 16777215);
		}
	}
}
