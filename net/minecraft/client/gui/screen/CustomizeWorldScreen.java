package net.minecraft.client.gui.screen;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PagedEntryListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.SwitchWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.CustomizedWorldProperties;

public class CustomizeWorldScreen extends Screen implements SliderWidget.LabelSupplier, PagedEntryListWidget.Listener {
	private CreateWorldScreen parent;
	protected String title = "Customize World Settings";
	protected String page = "Page 1 of 3";
	protected String subtitle = "Basic Settings";
	protected String[] pageNames = new String[4];
	private PagedEntryListWidget settingsWidget;
	private ButtonWidget doneButton;
	private ButtonWidget randomizeButton;
	private ButtonWidget resetToDefaultsButton;
	private ButtonWidget previousButton;
	private ButtonWidget nextButton;
	private ButtonWidget yesButton;
	private ButtonWidget noButton;
	private ButtonWidget presetsButton;
	private boolean modified = false;
	private int buttonLastPushed = 0;
	private boolean cancelled = false;
	private Predicate<String> floatVerifier = new Predicate<String>() {
		public boolean apply(@Nullable String string) {
			Float float_ = Floats.tryParse(string);
			return string.isEmpty() || float_ != null && Floats.isFinite(float_) && float_ >= 0.0F;
		}
	};
	private CustomizedWorldProperties.Builder defaultProps = new CustomizedWorldProperties.Builder();
	private CustomizedWorldProperties.Builder props;
	private Random random = new Random();

	public CustomizeWorldScreen(Screen screen, String string) {
		this.parent = (CreateWorldScreen)screen;
		this.loadProps(string);
	}

	@Override
	public void init() {
		int i = 0;
		int j = 0;
		if (this.settingsWidget != null) {
			i = this.settingsWidget.getCurrentPageId();
			j = this.settingsWidget.getScrollAmount();
		}

		this.title = I18n.translate("options.customizeTitle");
		this.buttons.clear();
		this.buttons.add(this.previousButton = new ButtonWidget(302, 20, 5, 80, 20, I18n.translate("createWorld.customize.custom.prev")));
		this.buttons.add(this.nextButton = new ButtonWidget(303, this.width - 100, 5, 80, 20, I18n.translate("createWorld.customize.custom.next")));
		this.buttons
			.add(
				this.resetToDefaultsButton = new ButtonWidget(304, this.width / 2 - 187, this.height - 27, 90, 20, I18n.translate("createWorld.customize.custom.defaults"))
			);
		this.buttons
			.add(this.randomizeButton = new ButtonWidget(301, this.width / 2 - 92, this.height - 27, 90, 20, I18n.translate("createWorld.customize.custom.randomize")));
		this.buttons
			.add(this.presetsButton = new ButtonWidget(305, this.width / 2 + 3, this.height - 27, 90, 20, I18n.translate("createWorld.customize.custom.presets")));
		this.buttons.add(this.doneButton = new ButtonWidget(300, this.width / 2 + 98, this.height - 27, 90, 20, I18n.translate("gui.done")));
		this.resetToDefaultsButton.active = this.modified;
		this.yesButton = new ButtonWidget(306, this.width / 2 - 55, 160, 50, 20, I18n.translate("gui.yes"));
		this.yesButton.visible = false;
		this.buttons.add(this.yesButton);
		this.noButton = new ButtonWidget(307, this.width / 2 + 5, 160, 50, 20, I18n.translate("gui.no"));
		this.noButton.visible = false;
		this.buttons.add(this.noButton);
		if (this.buttonLastPushed != 0) {
			this.yesButton.visible = true;
			this.noButton.visible = true;
		}

		this.initPages();
		if (i != 0) {
			this.settingsWidget.setCurrentPage(i);
			this.settingsWidget.scroll(j);
			this.initButtons();
		}
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.settingsWidget.handleMouse();
	}

	private void initPages() {
		PagedEntryListWidget.ListEntry[] listEntrys = new PagedEntryListWidget.ListEntry[]{
			new PagedEntryListWidget.LabelSupplierEntry(
				160, I18n.translate("createWorld.customize.custom.seaLevel"), true, this, 1.0F, 255.0F, (float)this.props.seaLevel
			),
			new PagedEntryListWidget.ButtonEntry(148, I18n.translate("createWorld.customize.custom.useCaves"), true, this.props.useCaves),
			new PagedEntryListWidget.ButtonEntry(150, I18n.translate("createWorld.customize.custom.useStrongholds"), true, this.props.useStrongholds),
			new PagedEntryListWidget.ButtonEntry(151, I18n.translate("createWorld.customize.custom.useVillages"), true, this.props.useVillages),
			new PagedEntryListWidget.ButtonEntry(152, I18n.translate("createWorld.customize.custom.useMineShafts"), true, this.props.useMineshafts),
			new PagedEntryListWidget.ButtonEntry(153, I18n.translate("createWorld.customize.custom.useTemples"), true, this.props.useTemples),
			new PagedEntryListWidget.ButtonEntry(210, I18n.translate("createWorld.customize.custom.useMonuments"), true, this.props.useMonuments),
			new PagedEntryListWidget.ButtonEntry(154, I18n.translate("createWorld.customize.custom.useRavines"), true, this.props.useRavines),
			new PagedEntryListWidget.ButtonEntry(149, I18n.translate("createWorld.customize.custom.useDungeons"), true, this.props.useDungeons),
			new PagedEntryListWidget.LabelSupplierEntry(
				157, I18n.translate("createWorld.customize.custom.dungeonChance"), true, this, 1.0F, 100.0F, (float)this.props.dungeonChance
			),
			new PagedEntryListWidget.ButtonEntry(155, I18n.translate("createWorld.customize.custom.useWaterLakes"), true, this.props.useWaterLakes),
			new PagedEntryListWidget.LabelSupplierEntry(
				158, I18n.translate("createWorld.customize.custom.waterLakeChance"), true, this, 1.0F, 100.0F, (float)this.props.waterLakeChance
			),
			new PagedEntryListWidget.ButtonEntry(156, I18n.translate("createWorld.customize.custom.useLavaLakes"), true, this.props.useLavaLakes),
			new PagedEntryListWidget.LabelSupplierEntry(
				159, I18n.translate("createWorld.customize.custom.lavaLakeChance"), true, this, 10.0F, 100.0F, (float)this.props.lavaLakeChance
			),
			new PagedEntryListWidget.ButtonEntry(161, I18n.translate("createWorld.customize.custom.useLavaOceans"), true, this.props.useLavaOceans),
			new PagedEntryListWidget.LabelSupplierEntry(
				162, I18n.translate("createWorld.customize.custom.fixedBiome"), true, this, -1.0F, 37.0F, (float)this.props.fixedBiome
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				163, I18n.translate("createWorld.customize.custom.biomeSize"), true, this, 1.0F, 8.0F, (float)this.props.biomeSize
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				164, I18n.translate("createWorld.customize.custom.riverSize"), true, this, 1.0F, 5.0F, (float)this.props.riverSize
			)
		};
		PagedEntryListWidget.ListEntry[] listEntrys2 = new PagedEntryListWidget.ListEntry[]{
			new PagedEntryListWidget.TextFieldLabelEntry(416, I18n.translate("tile.dirt.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(165, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.dirtSize),
			new PagedEntryListWidget.LabelSupplierEntry(166, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.dirtCount),
			new PagedEntryListWidget.LabelSupplierEntry(
				167, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.dirtMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				168, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.dirtMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(417, I18n.translate("tile.gravel.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(169, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.gravelSize),
			new PagedEntryListWidget.LabelSupplierEntry(
				170, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.gravelCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				171, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.gravelMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				172, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.gravelMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(418, I18n.translate("tile.stone.granite.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(
				173, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.graniteSize
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				174, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.graniteCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				175, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.graniteMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				176, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.graniteMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(419, I18n.translate("tile.stone.diorite.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(
				177, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.dioriteSize
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				178, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.dioriteCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				179, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.dioriteMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				180, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.dioriteMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(420, I18n.translate("tile.stone.andesite.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(
				181, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.andesiteSize
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				182, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.andesiteCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				183, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.andesiteMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				184, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.andesiteMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(421, I18n.translate("tile.oreCoal.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(185, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.coalSize),
			new PagedEntryListWidget.LabelSupplierEntry(186, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.coalCount),
			new PagedEntryListWidget.LabelSupplierEntry(
				187, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.coalMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				189, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.coalMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(422, I18n.translate("tile.oreIron.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(190, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.ironSize),
			new PagedEntryListWidget.LabelSupplierEntry(191, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.ironCount),
			new PagedEntryListWidget.LabelSupplierEntry(
				192, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.ironMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				193, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.ironMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(423, I18n.translate("tile.oreGold.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(194, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.goldSize),
			new PagedEntryListWidget.LabelSupplierEntry(195, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.goldCount),
			new PagedEntryListWidget.LabelSupplierEntry(
				196, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.goldMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				197, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.goldMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(424, I18n.translate("tile.oreRedstone.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(
				198, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.redstoneSize
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				199, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.redstoneCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				200, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.redstoneMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				201, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.redstoneMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(425, I18n.translate("tile.oreDiamond.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(
				202, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.diamondSize
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				203, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.diamondCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				204, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.props.diamondMinHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				205, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.props.diamondMaxHeight
			),
			new PagedEntryListWidget.TextFieldLabelEntry(426, I18n.translate("tile.oreLapis.name"), false),
			null,
			new PagedEntryListWidget.LabelSupplierEntry(206, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.props.lapisSize),
			new PagedEntryListWidget.LabelSupplierEntry(
				207, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.props.lapisCount
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				208, I18n.translate("createWorld.customize.custom.center"), false, this, 0.0F, 255.0F, (float)this.props.lapisCenterHeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				209, I18n.translate("createWorld.customize.custom.spread"), false, this, 0.0F, 255.0F, (float)this.props.lapisSpread
			)
		};
		PagedEntryListWidget.ListEntry[] listEntrys3 = new PagedEntryListWidget.ListEntry[]{
			new PagedEntryListWidget.LabelSupplierEntry(
				100, I18n.translate("createWorld.customize.custom.mainNoiseScaleX"), false, this, 1.0F, 5000.0F, this.props.mainNoiseScaleX
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				101, I18n.translate("createWorld.customize.custom.mainNoiseScaleY"), false, this, 1.0F, 5000.0F, this.props.mainNoiseScaleY
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				102, I18n.translate("createWorld.customize.custom.mainNoiseScaleZ"), false, this, 1.0F, 5000.0F, this.props.mainNoiseScaleZ
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				103, I18n.translate("createWorld.customize.custom.depthNoiseScaleX"), false, this, 1.0F, 2000.0F, this.props.depthNoiseScaleX
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				104, I18n.translate("createWorld.customize.custom.depthNoiseScaleZ"), false, this, 1.0F, 2000.0F, this.props.depthNoiseScaleZ
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				105, I18n.translate("createWorld.customize.custom.depthNoiseScaleExponent"), false, this, 0.01F, 20.0F, this.props.depthNoiseScaleExponent
			),
			new PagedEntryListWidget.LabelSupplierEntry(106, I18n.translate("createWorld.customize.custom.baseSize"), false, this, 1.0F, 25.0F, this.props.baseSize),
			new PagedEntryListWidget.LabelSupplierEntry(
				107, I18n.translate("createWorld.customize.custom.coordinateScale"), false, this, 1.0F, 6000.0F, this.props.coordinateScale
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				108, I18n.translate("createWorld.customize.custom.heightScale"), false, this, 1.0F, 6000.0F, this.props.heightScale
			),
			new PagedEntryListWidget.LabelSupplierEntry(109, I18n.translate("createWorld.customize.custom.stretchY"), false, this, 0.01F, 50.0F, this.props.stretchY),
			new PagedEntryListWidget.LabelSupplierEntry(
				110, I18n.translate("createWorld.customize.custom.upperLimitScale"), false, this, 1.0F, 5000.0F, this.props.upperLimitScale
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				111, I18n.translate("createWorld.customize.custom.lowerLimitScale"), false, this, 1.0F, 5000.0F, this.props.lowerLimitScale
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				112, I18n.translate("createWorld.customize.custom.biomeDepthWeight"), false, this, 1.0F, 20.0F, this.props.biomeDepthWeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				113, I18n.translate("createWorld.customize.custom.biomeDepthOffset"), false, this, 0.0F, 20.0F, this.props.biomeDepthOffset
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				114, I18n.translate("createWorld.customize.custom.biomeScaleWeight"), false, this, 1.0F, 20.0F, this.props.biomeScaleWeight
			),
			new PagedEntryListWidget.LabelSupplierEntry(
				115, I18n.translate("createWorld.customize.custom.biomeScaleOffset"), false, this, 0.0F, 20.0F, this.props.biomeScaleOffset
			)
		};
		PagedEntryListWidget.ListEntry[] listEntrys4 = new PagedEntryListWidget.ListEntry[]{
			new PagedEntryListWidget.TextFieldLabelEntry(400, I18n.translate("createWorld.customize.custom.mainNoiseScaleX") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(132, String.format("%5.3f", this.props.mainNoiseScaleX), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(401, I18n.translate("createWorld.customize.custom.mainNoiseScaleY") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(133, String.format("%5.3f", this.props.mainNoiseScaleY), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(402, I18n.translate("createWorld.customize.custom.mainNoiseScaleZ") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(134, String.format("%5.3f", this.props.mainNoiseScaleZ), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(403, I18n.translate("createWorld.customize.custom.depthNoiseScaleX") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(135, String.format("%5.3f", this.props.depthNoiseScaleX), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(404, I18n.translate("createWorld.customize.custom.depthNoiseScaleZ") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(136, String.format("%5.3f", this.props.depthNoiseScaleZ), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(405, I18n.translate("createWorld.customize.custom.depthNoiseScaleExponent") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(137, String.format("%2.3f", this.props.depthNoiseScaleExponent), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(406, I18n.translate("createWorld.customize.custom.baseSize") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(138, String.format("%2.3f", this.props.baseSize), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(407, I18n.translate("createWorld.customize.custom.coordinateScale") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(139, String.format("%5.3f", this.props.coordinateScale), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(408, I18n.translate("createWorld.customize.custom.heightScale") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(140, String.format("%5.3f", this.props.heightScale), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(409, I18n.translate("createWorld.customize.custom.stretchY") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(141, String.format("%2.3f", this.props.stretchY), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(410, I18n.translate("createWorld.customize.custom.upperLimitScale") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(142, String.format("%5.3f", this.props.upperLimitScale), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(411, I18n.translate("createWorld.customize.custom.lowerLimitScale") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(143, String.format("%5.3f", this.props.lowerLimitScale), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(412, I18n.translate("createWorld.customize.custom.biomeDepthWeight") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(144, String.format("%2.3f", this.props.biomeDepthWeight), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(413, I18n.translate("createWorld.customize.custom.biomeDepthOffset") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(145, String.format("%2.3f", this.props.biomeDepthOffset), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(414, I18n.translate("createWorld.customize.custom.biomeScaleWeight") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(146, String.format("%2.3f", this.props.biomeScaleWeight), false, this.floatVerifier),
			new PagedEntryListWidget.TextFieldLabelEntry(415, I18n.translate("createWorld.customize.custom.biomeScaleOffset") + ":", false),
			new PagedEntryListWidget.TextFieldEntry(147, String.format("%2.3f", this.props.biomeScaleOffset), false, this.floatVerifier)
		};
		this.settingsWidget = new PagedEntryListWidget(
			this.client, this.width, this.height, 32, this.height - 32, 25, this, listEntrys, listEntrys2, listEntrys3, listEntrys4
		);

		for (int i = 0; i < 4; i++) {
			this.pageNames[i] = I18n.translate("createWorld.customize.custom.page" + i);
		}

		this.initButtons();
	}

	public String getPropsAsString() {
		return this.props.toString().replace("\n", "");
	}

	public void loadProps(String generatorOptions) {
		if (generatorOptions != null && !generatorOptions.isEmpty()) {
			this.props = CustomizedWorldProperties.Builder.fromJson(generatorOptions);
		} else {
			this.props = new CustomizedWorldProperties.Builder();
		}
	}

	@Override
	public void setStringValue(int id, String text) {
		float f = 0.0F;

		try {
			f = Float.parseFloat(text);
		} catch (NumberFormatException var5) {
		}

		float g = 0.0F;
		switch (id) {
			case 132:
				g = this.props.mainNoiseScaleX = MathHelper.clamp(f, 1.0F, 5000.0F);
				break;
			case 133:
				g = this.props.mainNoiseScaleY = MathHelper.clamp(f, 1.0F, 5000.0F);
				break;
			case 134:
				g = this.props.mainNoiseScaleZ = MathHelper.clamp(f, 1.0F, 5000.0F);
				break;
			case 135:
				g = this.props.depthNoiseScaleX = MathHelper.clamp(f, 1.0F, 2000.0F);
				break;
			case 136:
				g = this.props.depthNoiseScaleZ = MathHelper.clamp(f, 1.0F, 2000.0F);
				break;
			case 137:
				g = this.props.depthNoiseScaleExponent = MathHelper.clamp(f, 0.01F, 20.0F);
				break;
			case 138:
				g = this.props.baseSize = MathHelper.clamp(f, 1.0F, 25.0F);
				break;
			case 139:
				g = this.props.coordinateScale = MathHelper.clamp(f, 1.0F, 6000.0F);
				break;
			case 140:
				g = this.props.heightScale = MathHelper.clamp(f, 1.0F, 6000.0F);
				break;
			case 141:
				g = this.props.stretchY = MathHelper.clamp(f, 0.01F, 50.0F);
				break;
			case 142:
				g = this.props.upperLimitScale = MathHelper.clamp(f, 1.0F, 5000.0F);
				break;
			case 143:
				g = this.props.lowerLimitScale = MathHelper.clamp(f, 1.0F, 5000.0F);
				break;
			case 144:
				g = this.props.biomeDepthWeight = MathHelper.clamp(f, 1.0F, 20.0F);
				break;
			case 145:
				g = this.props.biomeDepthOffset = MathHelper.clamp(f, 0.0F, 20.0F);
				break;
			case 146:
				g = this.props.biomeScaleWeight = MathHelper.clamp(f, 1.0F, 20.0F);
				break;
			case 147:
				g = this.props.biomeScaleOffset = MathHelper.clamp(f, 0.0F, 20.0F);
		}

		if (g != f && f != 0.0F) {
			((TextFieldWidget)this.settingsWidget.getWidget(id)).setText(this.getFormattedValue(id, g));
		}

		((SliderWidget)this.settingsWidget.getWidget(id - 132 + 100)).setSliderValue(g, false);
		if (!this.props.equals(this.defaultProps)) {
			this.setModified(true);
		}
	}

	private void setModified(boolean modified) {
		this.modified = modified;
		this.resetToDefaultsButton.active = modified;
	}

	@Override
	public String getLabel(int id, String label, float sliderValue) {
		return label + ": " + this.getFormattedValue(id, sliderValue);
	}

	private String getFormattedValue(int id, float sliderValue) {
		switch (id) {
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 107:
			case 108:
			case 110:
			case 111:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 139:
			case 140:
			case 142:
			case 143:
				return String.format("%5.3f", sliderValue);
			case 105:
			case 106:
			case 109:
			case 112:
			case 113:
			case 114:
			case 115:
			case 137:
			case 138:
			case 141:
			case 144:
			case 145:
			case 146:
			case 147:
				return String.format("%2.3f", sliderValue);
			case 116:
			case 117:
			case 118:
			case 119:
			case 120:
			case 121:
			case 122:
			case 123:
			case 124:
			case 125:
			case 126:
			case 127:
			case 128:
			case 129:
			case 130:
			case 131:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			default:
				return String.format("%d", (int)sliderValue);
			case 162:
				if (sliderValue < 0.0F) {
					return I18n.translate("gui.all");
				} else if ((int)sliderValue >= Biome.getBiomeIndex(Biomes.NETHER)) {
					Biome biome = Biome.getBiomeFromIndex((int)sliderValue + 2);
					return biome != null ? biome.getName() : "?";
				} else {
					Biome biome2 = Biome.getBiomeFromIndex((int)sliderValue);
					return biome2 != null ? biome2.getName() : "?";
				}
		}
	}

	@Override
	public void setBooleanValue(int id, boolean value) {
		switch (id) {
			case 148:
				this.props.useCaves = value;
				break;
			case 149:
				this.props.useDungeons = value;
				break;
			case 150:
				this.props.useStrongholds = value;
				break;
			case 151:
				this.props.useVillages = value;
				break;
			case 152:
				this.props.useMineshafts = value;
				break;
			case 153:
				this.props.useTemples = value;
				break;
			case 154:
				this.props.useRavines = value;
				break;
			case 155:
				this.props.useWaterLakes = value;
				break;
			case 156:
				this.props.useLavaLakes = value;
				break;
			case 161:
				this.props.useLavaOceans = value;
				break;
			case 210:
				this.props.useMonuments = value;
		}

		if (!this.props.equals(this.defaultProps)) {
			this.setModified(true);
		}
	}

	@Override
	public void setFloatValue(int id, float value) {
		switch (id) {
			case 100:
				this.props.mainNoiseScaleX = value;
				break;
			case 101:
				this.props.mainNoiseScaleY = value;
				break;
			case 102:
				this.props.mainNoiseScaleZ = value;
				break;
			case 103:
				this.props.depthNoiseScaleX = value;
				break;
			case 104:
				this.props.depthNoiseScaleZ = value;
				break;
			case 105:
				this.props.depthNoiseScaleExponent = value;
				break;
			case 106:
				this.props.baseSize = value;
				break;
			case 107:
				this.props.coordinateScale = value;
				break;
			case 108:
				this.props.heightScale = value;
				break;
			case 109:
				this.props.stretchY = value;
				break;
			case 110:
				this.props.upperLimitScale = value;
				break;
			case 111:
				this.props.lowerLimitScale = value;
				break;
			case 112:
				this.props.biomeDepthWeight = value;
				break;
			case 113:
				this.props.biomeDepthOffset = value;
				break;
			case 114:
				this.props.biomeScaleWeight = value;
				break;
			case 115:
				this.props.biomeScaleOffset = value;
			case 116:
			case 117:
			case 118:
			case 119:
			case 120:
			case 121:
			case 122:
			case 123:
			case 124:
			case 125:
			case 126:
			case 127:
			case 128:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 142:
			case 143:
			case 144:
			case 145:
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
			case 156:
			case 161:
			case 188:
			default:
				break;
			case 157:
				this.props.dungeonChance = (int)value;
				break;
			case 158:
				this.props.waterLakeChance = (int)value;
				break;
			case 159:
				this.props.lavaLakeChance = (int)value;
				break;
			case 160:
				this.props.seaLevel = (int)value;
				break;
			case 162:
				this.props.fixedBiome = (int)value;
				break;
			case 163:
				this.props.biomeSize = (int)value;
				break;
			case 164:
				this.props.riverSize = (int)value;
				break;
			case 165:
				this.props.dirtSize = (int)value;
				break;
			case 166:
				this.props.dirtCount = (int)value;
				break;
			case 167:
				this.props.dirtMinHeight = (int)value;
				break;
			case 168:
				this.props.dirtMaxHeight = (int)value;
				break;
			case 169:
				this.props.gravelSize = (int)value;
				break;
			case 170:
				this.props.gravelCount = (int)value;
				break;
			case 171:
				this.props.gravelMinHeight = (int)value;
				break;
			case 172:
				this.props.gravelMaxHeight = (int)value;
				break;
			case 173:
				this.props.graniteSize = (int)value;
				break;
			case 174:
				this.props.graniteCount = (int)value;
				break;
			case 175:
				this.props.graniteMinHeight = (int)value;
				break;
			case 176:
				this.props.graniteMaxHeight = (int)value;
				break;
			case 177:
				this.props.dioriteSize = (int)value;
				break;
			case 178:
				this.props.dioriteCount = (int)value;
				break;
			case 179:
				this.props.dioriteMinHeight = (int)value;
				break;
			case 180:
				this.props.dioriteMaxHeight = (int)value;
				break;
			case 181:
				this.props.andesiteSize = (int)value;
				break;
			case 182:
				this.props.andesiteCount = (int)value;
				break;
			case 183:
				this.props.andesiteMinHeight = (int)value;
				break;
			case 184:
				this.props.andesiteMaxHeight = (int)value;
				break;
			case 185:
				this.props.coalSize = (int)value;
				break;
			case 186:
				this.props.coalCount = (int)value;
				break;
			case 187:
				this.props.coalMinHeight = (int)value;
				break;
			case 189:
				this.props.coalMaxHeight = (int)value;
				break;
			case 190:
				this.props.ironSize = (int)value;
				break;
			case 191:
				this.props.ironCount = (int)value;
				break;
			case 192:
				this.props.ironMinHeight = (int)value;
				break;
			case 193:
				this.props.ironMaxHeight = (int)value;
				break;
			case 194:
				this.props.goldSize = (int)value;
				break;
			case 195:
				this.props.goldCount = (int)value;
				break;
			case 196:
				this.props.goldMinHeight = (int)value;
				break;
			case 197:
				this.props.goldMaxHeight = (int)value;
				break;
			case 198:
				this.props.redstoneSize = (int)value;
				break;
			case 199:
				this.props.redstoneCount = (int)value;
				break;
			case 200:
				this.props.redstoneMinHeight = (int)value;
				break;
			case 201:
				this.props.redstoneMaxHeight = (int)value;
				break;
			case 202:
				this.props.diamondSize = (int)value;
				break;
			case 203:
				this.props.diamondCount = (int)value;
				break;
			case 204:
				this.props.diamondMinHeight = (int)value;
				break;
			case 205:
				this.props.diamondMaxHeight = (int)value;
				break;
			case 206:
				this.props.lapisSize = (int)value;
				break;
			case 207:
				this.props.lapisCount = (int)value;
				break;
			case 208:
				this.props.lapisCenterHeight = (int)value;
				break;
			case 209:
				this.props.lapisSpread = (int)value;
		}

		if (id >= 100 && id < 116) {
			DrawableHelper drawableHelper = this.settingsWidget.getWidget(id - 100 + 132);
			if (drawableHelper != null) {
				((TextFieldWidget)drawableHelper).setText(this.getFormattedValue(id, value));
			}
		}

		if (!this.props.equals(this.defaultProps)) {
			this.setModified(true);
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			switch (button.id) {
				case 300:
					this.parent.generatorOptions = this.props.toString();
					this.client.setScreen(this.parent);
					break;
				case 301:
					for (int i = 0; i < this.settingsWidget.getEntryCount(); i++) {
						PagedEntryListWidget.DualDrawableEntry dualDrawableEntry = this.settingsWidget.getEntry(i);
						DrawableHelper drawableHelper = dualDrawableEntry.getFirst();
						if (drawableHelper instanceof ButtonWidget) {
							ButtonWidget buttonWidget = (ButtonWidget)drawableHelper;
							if (buttonWidget instanceof SliderWidget) {
								float f = ((SliderWidget)buttonWidget).getProgress() * (0.75F + this.random.nextFloat() * 0.5F) + (this.random.nextFloat() * 0.1F - 0.05F);
								((SliderWidget)buttonWidget).setSliderProgress(MathHelper.clamp(f, 0.0F, 1.0F));
							} else if (buttonWidget instanceof SwitchWidget) {
								((SwitchWidget)buttonWidget).setValue(this.random.nextBoolean());
							}
						}

						DrawableHelper drawableHelper2 = dualDrawableEntry.getSecond();
						if (drawableHelper2 instanceof ButtonWidget) {
							ButtonWidget buttonWidget2 = (ButtonWidget)drawableHelper2;
							if (buttonWidget2 instanceof SliderWidget) {
								float g = ((SliderWidget)buttonWidget2).getProgress() * (0.75F + this.random.nextFloat() * 0.5F) + (this.random.nextFloat() * 0.1F - 0.05F);
								((SliderWidget)buttonWidget2).setSliderProgress(MathHelper.clamp(g, 0.0F, 1.0F));
							} else if (buttonWidget2 instanceof SwitchWidget) {
								((SwitchWidget)buttonWidget2).setValue(this.random.nextBoolean());
							}
						}
					}
					break;
				case 302:
					this.settingsWidget.previousPage();
					this.initButtons();
					break;
				case 303:
					this.settingsWidget.nextPage();
					this.initButtons();
					break;
				case 304:
					if (this.modified) {
						this.resetToDefaults(304);
					}
					break;
				case 305:
					this.client.setScreen(new CustomizedWorldPresetsScreen(this));
					break;
				case 306:
					this.checkConfirmation();
					break;
				case 307:
					this.buttonLastPushed = 0;
					this.checkConfirmation();
			}
		}
	}

	private void resetToDefaults() {
		this.props.resetToDefault();
		this.initPages();
		this.setModified(false);
	}

	private void resetToDefaults(int buttonId) {
		this.buttonLastPushed = buttonId;
		this.setConfirmationButtons(true);
	}

	private void checkConfirmation() {
		switch (this.buttonLastPushed) {
			case 300:
				this.buttonClicked((SwitchWidget)this.settingsWidget.getWidget(300));
				break;
			case 304:
				this.resetToDefaults();
		}

		this.buttonLastPushed = 0;
		this.cancelled = true;
		this.setConfirmationButtons(false);
	}

	private void setConfirmationButtons(boolean visible) {
		this.yesButton.visible = visible;
		this.noButton.visible = visible;
		this.randomizeButton.active = !visible;
		this.doneButton.active = !visible;
		this.previousButton.active = !visible;
		this.nextButton.active = !visible;
		this.resetToDefaultsButton.active = this.modified && !visible;
		this.presetsButton.active = !visible;
		this.settingsWidget.setActive(!visible);
	}

	private void initButtons() {
		this.previousButton.active = this.settingsWidget.getCurrentPageId() != 0;
		this.nextButton.active = this.settingsWidget.getCurrentPageId() != this.settingsWidget.getMaxPages() - 1;
		this.page = I18n.translate("book.pageIndicator", this.settingsWidget.getCurrentPageId() + 1, this.settingsWidget.getMaxPages());
		this.subtitle = this.pageNames[this.settingsWidget.getCurrentPageId()];
		this.randomizeButton.active = this.settingsWidget.getCurrentPageId() != this.settingsWidget.getMaxPages() - 1;
	}

	@Override
	protected void keyPressed(char id, int code) {
		super.keyPressed(id, code);
		if (this.buttonLastPushed == 0) {
			switch (code) {
				case 200:
					this.updateValue(1.0F);
					break;
				case 208:
					this.updateValue(-1.0F);
					break;
				default:
					this.settingsWidget.updateText(id, code);
			}
		}
	}

	private void updateValue(float baseAmount) {
		DrawableHelper drawableHelper = this.settingsWidget.getCurrentWidget();
		if (drawableHelper instanceof TextFieldWidget) {
			float f = baseAmount;
			if (Screen.hasShiftDown()) {
				f = baseAmount * 0.1F;
				if (Screen.hasControlDown()) {
					f *= 0.1F;
				}
			} else if (Screen.hasControlDown()) {
				f = baseAmount * 10.0F;
				if (Screen.hasAltDown()) {
					f *= 10.0F;
				}
			}

			TextFieldWidget textFieldWidget = (TextFieldWidget)drawableHelper;
			Float float_ = Floats.tryParse(textFieldWidget.getText());
			if (float_ != null) {
				float_ = float_ + f;
				int i = textFieldWidget.getId();
				String string = this.getFormattedValue(textFieldWidget.getId(), float_);
				textFieldWidget.setText(string);
				this.setStringValue(i, string);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		if (this.buttonLastPushed == 0 && !this.cancelled) {
			this.settingsWidget.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		if (this.cancelled) {
			this.cancelled = false;
		} else if (this.buttonLastPushed == 0) {
			this.settingsWidget.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.settingsWidget.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 2, 16777215);
		this.drawCenteredString(this.textRenderer, this.page, this.width / 2, 12, 16777215);
		this.drawCenteredString(this.textRenderer, this.subtitle, this.width / 2, 22, 16777215);
		super.render(mouseX, mouseY, tickDelta);
		if (this.buttonLastPushed != 0) {
			fill(0, 0, this.width, this.height, Integer.MIN_VALUE);
			this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
			this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
			this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
			this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
			float f = 85.0F;
			float g = 180.0F;
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			this.client.getTextureManager().bindTexture(OPTIONS_BACKGROUND_TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			float h = 32.0F;
			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex((double)(this.width / 2 - 90), 185.0, 0.0).texture(0.0, 2.65625).color(64, 64, 64, 64).next();
			bufferBuilder.vertex((double)(this.width / 2 + 90), 185.0, 0.0).texture(5.625, 2.65625).color(64, 64, 64, 64).next();
			bufferBuilder.vertex((double)(this.width / 2 + 90), 100.0, 0.0).texture(5.625, 0.0).color(64, 64, 64, 64).next();
			bufferBuilder.vertex((double)(this.width / 2 - 90), 100.0, 0.0).texture(0.0, 0.0).color(64, 64, 64, 64).next();
			tessellator.draw();
			this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.custom.confirmTitle"), this.width / 2, 105, 16777215);
			this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.custom.confirm1"), this.width / 2, 125, 16777215);
			this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.custom.confirm2"), this.width / 2, 135, 16777215);
			this.yesButton.render(this.client, mouseX, mouseY);
			this.noButton.render(this.client, mouseX, mouseY);
		}
	}
}
