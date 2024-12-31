package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.CustomizedWorldProperties;
import org.lwjgl.input.Keyboard;

public class CustomizedWorldPresetsScreen extends Screen {
	private static final List<CustomizedWorldPresetsScreen.PresetEntry> entries = Lists.newArrayList();
	private CustomizedWorldPresetsScreen.PresetsListWidget presetList;
	private ButtonWidget selectButton;
	private TextFieldWidget searchField;
	private final CustomizeWorldScreen parent;
	protected String title = "Customize World Presets";
	private String shareText;
	private String listText;

	public CustomizedWorldPresetsScreen(CustomizeWorldScreen customizeWorldScreen) {
		this.parent = customizeWorldScreen;
	}

	@Override
	public void init() {
		this.buttons.clear();
		Keyboard.enableRepeatEvents(true);
		this.title = I18n.translate("createWorld.customize.custom.presets.title");
		this.shareText = I18n.translate("createWorld.customize.presets.share");
		this.listText = I18n.translate("createWorld.customize.presets.list");
		this.searchField = new TextFieldWidget(2, this.textRenderer, 50, 40, this.width - 100, 20);
		this.presetList = new CustomizedWorldPresetsScreen.PresetsListWidget();
		this.searchField.setMaxLength(2000);
		this.searchField.setText(this.parent.getPropsAsString());
		this.selectButton = this.addButton(
			new ButtonWidget(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.translate("createWorld.customize.presets.select"))
		);
		this.buttons.add(new ButtonWidget(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.translate("gui.cancel")));
		this.updateSelectButton();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.presetList.handleMouse();
	}

	@Override
	public void removed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		this.searchField.method_920(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (!this.searchField.keyPressed(id, code)) {
			super.keyPressed(id, code);
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		switch (button.id) {
			case 0:
				this.parent.loadProps(this.searchField.getText());
				this.client.setScreen(this.parent);
				break;
			case 1:
				this.client.setScreen(this.parent);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.presetList.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawWithShadow(this.textRenderer, this.shareText, 50, 30, 10526880);
		this.drawWithShadow(this.textRenderer, this.listText, 50, 70, 10526880);
		this.searchField.render();
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public void tick() {
		this.searchField.tick();
		super.tick();
	}

	public void updateSelectButton() {
		this.selectButton.active = this.isEntrySelected();
	}

	private boolean isEntrySelected() {
		return this.presetList.selectedEntryIndex > -1 && this.presetList.selectedEntryIndex < entries.size() || this.searchField.getText().length() > 1;
	}

	static {
		CustomizedWorldProperties.Builder builder = CustomizedWorldProperties.Builder.fromJson(
			"{ \"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":8.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":0.5, \"biomeScaleWeight\":2.0, \"biomeScaleOffset\":0.375, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":255 }"
		);
		Identifier identifier = new Identifier("textures/gui/presets/water.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.waterWorld"), identifier, builder));
		builder = CustomizedWorldProperties.Builder.fromJson(
			"{\"coordinateScale\":3000.0, \"heightScale\":6000.0, \"upperLimitScale\":250.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
		);
		identifier = new Identifier("textures/gui/presets/isles.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.isleLand"), identifier, builder));
		builder = CustomizedWorldProperties.Builder.fromJson(
			"{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":5.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":1.0, \"biomeScaleWeight\":4.0, \"biomeScaleOffset\":1.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
		);
		identifier = new Identifier("textures/gui/presets/delight.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.caveDelight"), identifier, builder));
		builder = CustomizedWorldProperties.Builder.fromJson(
			"{\"coordinateScale\":738.41864, \"heightScale\":157.69133, \"upperLimitScale\":801.4267, \"lowerLimitScale\":1254.1643, \"depthNoiseScaleX\":374.93652, \"depthNoiseScaleZ\":288.65228, \"depthNoiseScaleExponent\":1.2092624, \"mainNoiseScaleX\":1355.9908, \"mainNoiseScaleY\":745.5343, \"mainNoiseScaleZ\":1183.464, \"baseSize\":1.8758626, \"stretchY\":1.7137525, \"biomeDepthWeight\":1.7553768, \"biomeDepthOffset\":3.4701107, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":2.535211, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
		);
		identifier = new Identifier("textures/gui/presets/madness.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.mountains"), identifier, builder));
		builder = CustomizedWorldProperties.Builder.fromJson(
			"{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":1000.0, \"mainNoiseScaleY\":3000.0, \"mainNoiseScaleZ\":1000.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":20 }"
		);
		identifier = new Identifier("textures/gui/presets/drought.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.drought"), identifier, builder));
		builder = CustomizedWorldProperties.Builder.fromJson(
			"{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":2.0, \"lowerLimitScale\":64.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":6 }"
		);
		identifier = new Identifier("textures/gui/presets/chaos.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.caveChaos"), identifier, builder));
		builder = CustomizedWorldProperties.Builder.fromJson(
			"{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":true, \"seaLevel\":40 }"
		);
		identifier = new Identifier("textures/gui/presets/luck.png");
		entries.add(new CustomizedWorldPresetsScreen.PresetEntry(I18n.translate("createWorld.customize.custom.preset.goodLuck"), identifier, builder));
	}

	static class PresetEntry {
		public String name;
		public Identifier texture;
		public CustomizedWorldProperties.Builder utilities;

		public PresetEntry(String string, Identifier identifier, CustomizedWorldProperties.Builder builder) {
			this.name = string;
			this.texture = identifier;
			this.utilities = builder;
		}
	}

	class PresetsListWidget extends ListWidget {
		public int selectedEntryIndex = -1;

		public PresetsListWidget() {
			super(
				CustomizedWorldPresetsScreen.this.client,
				CustomizedWorldPresetsScreen.this.width,
				CustomizedWorldPresetsScreen.this.height,
				80,
				CustomizedWorldPresetsScreen.this.height - 32,
				38
			);
		}

		@Override
		protected int getEntryCount() {
			return CustomizedWorldPresetsScreen.entries.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
			this.selectedEntryIndex = index;
			CustomizedWorldPresetsScreen.this.updateSelectButton();
			CustomizedWorldPresetsScreen.this.searchField
				.setText(
					((CustomizedWorldPresetsScreen.PresetEntry)CustomizedWorldPresetsScreen.entries.get(CustomizedWorldPresetsScreen.this.presetList.selectedEntryIndex))
						.utilities
						.toString()
				);
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return index == this.selectedEntryIndex;
		}

		@Override
		protected void renderBackground() {
		}

		private void renderEntryBackground(int x, int y, Identifier texture) {
			int i = x + 5;
			CustomizedWorldPresetsScreen.this.drawHorizontalLine(i - 1, i + 32, y - 1, -2039584);
			CustomizedWorldPresetsScreen.this.drawHorizontalLine(i - 1, i + 32, y + 32, -6250336);
			CustomizedWorldPresetsScreen.this.drawVerticalLine(i - 1, y - 1, y + 32, -2039584);
			CustomizedWorldPresetsScreen.this.drawVerticalLine(i + 32, y - 1, y + 32, -6250336);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(texture);
			int k = 32;
			int l = 32;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
			bufferBuilder.vertex((double)(i + 0), (double)(y + 32), 0.0).texture(0.0, 1.0).next();
			bufferBuilder.vertex((double)(i + 32), (double)(y + 32), 0.0).texture(1.0, 1.0).next();
			bufferBuilder.vertex((double)(i + 32), (double)(y + 0), 0.0).texture(1.0, 0.0).next();
			bufferBuilder.vertex((double)(i + 0), (double)(y + 0), 0.0).texture(0.0, 0.0).next();
			tessellator.draw();
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			CustomizedWorldPresetsScreen.PresetEntry presetEntry = (CustomizedWorldPresetsScreen.PresetEntry)CustomizedWorldPresetsScreen.entries.get(i);
			this.renderEntryBackground(j, k, presetEntry.texture);
			CustomizedWorldPresetsScreen.this.textRenderer.draw(presetEntry.name, j + 32 + 10, k + 14, 16777215);
		}
	}
}
