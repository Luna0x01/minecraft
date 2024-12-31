package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class StatsScreen extends Screen implements StatsListener {
	protected final Screen parent;
	private StatsScreen.CustomStatsListWidget generalButton;
	private StatsScreen.ItemStatsListWidget itemsButton;
	private StatsScreen.EntityStatsListWidget mobsButton;
	private final StatHandler statHandler;
	@Nullable
	private AlwaysSelectedEntryListWidget<?> listWidget;
	private boolean field_2645 = true;

	public StatsScreen(Screen screen, StatHandler statHandler) {
		super(new TranslatableText("gui.stats"));
		this.parent = screen;
		this.statHandler = statHandler;
	}

	@Override
	protected void init() {
		this.field_2645 = true;
		this.minecraft.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.field_12775));
	}

	public void method_2270() {
		this.generalButton = new StatsScreen.CustomStatsListWidget(this.minecraft);
		this.itemsButton = new StatsScreen.ItemStatsListWidget(this.minecraft);
		this.mobsButton = new StatsScreen.EntityStatsListWidget(this.minecraft);
	}

	public void method_2267() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton"), buttonWidgetx -> this.method_19390(this.generalButton)
			)
		);
		ButtonWidget buttonWidget = this.addButton(
			new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton"), buttonWidgetx -> this.method_19390(this.itemsButton))
		);
		ButtonWidget buttonWidget2 = this.addButton(
			new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton"), buttonWidgetx -> this.method_19390(this.mobsButton))
		);
		this.addButton(
			new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, I18n.translate("gui.done"), buttonWidgetx -> this.minecraft.openScreen(this.parent))
		);
		if (this.itemsButton.children().isEmpty()) {
			buttonWidget.active = false;
		}

		if (this.mobsButton.children().isEmpty()) {
			buttonWidget2.active = false;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.field_2645) {
			this.renderBackground();
			this.drawCenteredString(this.font, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				this.font,
				PROGRESS_BAR_STAGES[(int)(SystemUtil.getMeasuringTimeMs() / 150L % (long)PROGRESS_BAR_STAGES.length)],
				this.width / 2,
				this.height / 2 + 9 * 2,
				16777215
			);
		} else {
			this.method_19399().render(i, j, f);
			this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
			super.render(i, j, f);
		}
	}

	@Override
	public void onStatsReady() {
		if (this.field_2645) {
			this.method_2270();
			this.method_2267();
			this.method_19390(this.generalButton);
			this.field_2645 = false;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return !this.field_2645;
	}

	@Nullable
	public AlwaysSelectedEntryListWidget<?> method_19399() {
		return this.listWidget;
	}

	public void method_19390(@Nullable AlwaysSelectedEntryListWidget<?> alwaysSelectedEntryListWidget) {
		this.children.remove(this.generalButton);
		this.children.remove(this.itemsButton);
		this.children.remove(this.mobsButton);
		if (alwaysSelectedEntryListWidget != null) {
			this.children.add(0, alwaysSelectedEntryListWidget);
			this.listWidget = alwaysSelectedEntryListWidget;
		}
	}

	private int method_2285(int i) {
		return 115 + 40 * i;
	}

	private void method_2289(int i, int j, Item item) {
		this.method_2282(i + 1, j + 1, 0, 0);
		GlStateManager.enableRescaleNormal();
		GuiLighting.enableForItems();
		this.itemRenderer.renderGuiItemIcon(item.getStackForRender(), i + 2, j + 2);
		GuiLighting.disable();
		GlStateManager.disableRescaleNormal();
	}

	private void method_2282(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
		blit(i, j, this.blitOffset, (float)k, (float)l, 18, 18, 128, 128);
	}

	class CustomStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.CustomStatsListWidget.CustomStatItem> {
		public CustomStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);

			for (Stat<Identifier> stat : Stats.field_15419) {
				this.addEntry(new StatsScreen.CustomStatsListWidget.CustomStatItem(stat));
			}
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		class CustomStatItem extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.CustomStatsListWidget.CustomStatItem> {
			private final Stat<Identifier> field_18749;

			private CustomStatItem(Stat<Identifier> stat) {
				this.field_18749 = stat;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				Text text = new TranslatableText("stat." + this.field_18749.getValue().toString().replace(':', '.')).formatted(Formatting.field_1080);
				CustomStatsListWidget.this.drawString(StatsScreen.this.font, text.getString(), k + 2, j + 1, i % 2 == 0 ? 16777215 : 9474192);
				String string = this.field_18749.format(StatsScreen.this.statHandler.getStat(this.field_18749));
				CustomStatsListWidget.this.drawString(
					StatsScreen.this.font, string, k + 2 + 213 - StatsScreen.this.font.getStringWidth(string), j + 1, i % 2 == 0 ? 16777215 : 9474192
				);
			}
		}
	}

	class EntityStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.EntityStatsListWidget.EntityStatItem> {
		public EntityStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);

			for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
				if (StatsScreen.this.statHandler.getStat(Stats.field_15403.getOrCreateStat(entityType)) > 0
					|| StatsScreen.this.statHandler.getStat(Stats.field_15411.getOrCreateStat(entityType)) > 0) {
					this.addEntry(new StatsScreen.EntityStatsListWidget.EntityStatItem(entityType));
				}
			}
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		class EntityStatItem extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.EntityStatsListWidget.EntityStatItem> {
			private final EntityType<?> field_18762;

			public EntityStatItem(EntityType<?> entityType) {
				this.field_18762 = entityType;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				String string = I18n.translate(SystemUtil.createTranslationKey("entity", EntityType.getId(this.field_18762)));
				int p = StatsScreen.this.statHandler.getStat(Stats.field_15403.getOrCreateStat(this.field_18762));
				int q = StatsScreen.this.statHandler.getStat(Stats.field_15411.getOrCreateStat(this.field_18762));
				EntityStatsListWidget.this.drawString(StatsScreen.this.font, string, k + 2, j + 1, 16777215);
				EntityStatsListWidget.this.drawString(StatsScreen.this.font, this.method_19411(string, p), k + 2 + 10, j + 1 + 9, p == 0 ? 6316128 : 9474192);
				EntityStatsListWidget.this.drawString(StatsScreen.this.font, this.method_19412(string, q), k + 2 + 10, j + 1 + 9 * 2, q == 0 ? 6316128 : 9474192);
			}

			private String method_19411(String string, int i) {
				String string2 = Stats.field_15403.getTranslationKey();
				return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, i, string);
			}

			private String method_19412(String string, int i) {
				String string2 = Stats.field_15411.getTranslationKey();
				return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, string, i);
			}
		}
	}

	class ItemStatsListWidget extends AlwaysSelectedEntryListWidget<StatsScreen.ItemStatsListWidget.ItemStatItem> {
		protected final List<StatType<Block>> field_18754;
		protected final List<StatType<Item>> field_18755;
		private final int[] field_18753 = new int[]{3, 4, 1, 2, 5, 6};
		protected int field_18756 = -1;
		protected final List<Item> field_18757;
		protected final Comparator<Item> field_18758 = new StatsScreen.ItemStatsListWidget.class_450();
		@Nullable
		protected StatType<?> field_18759;
		protected int field_18760;

		public ItemStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
			this.field_18754 = Lists.newArrayList();
			this.field_18754.add(Stats.field_15427);
			this.field_18755 = Lists.newArrayList(new StatType[]{Stats.field_15383, Stats.field_15370, Stats.field_15372, Stats.field_15392, Stats.field_15405});
			this.setRenderHeader(true, 20);
			Set<Item> set = Sets.newIdentityHashSet();

			for (Item item : Registry.ITEM) {
				boolean bl = false;

				for (StatType<Item> statType : this.field_18755) {
					if (statType.hasStat(item) && StatsScreen.this.statHandler.getStat(statType.getOrCreateStat(item)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(item);
				}
			}

			for (Block block : Registry.BLOCK) {
				boolean bl2 = false;

				for (StatType<Block> statType2 : this.field_18754) {
					if (statType2.hasStat(block) && StatsScreen.this.statHandler.getStat(statType2.getOrCreateStat(block)) > 0) {
						bl2 = true;
					}
				}

				if (bl2) {
					set.add(block.asItem());
				}
			}

			set.remove(Items.AIR);
			this.field_18757 = Lists.newArrayList(set);

			for (int i = 0; i < this.field_18757.size(); i++) {
				this.addEntry(new StatsScreen.ItemStatsListWidget.ItemStatItem());
			}
		}

		@Override
		protected void renderHeader(int i, int j, Tessellator tessellator) {
			if (!this.minecraft.mouse.wasLeftButtonClicked()) {
				this.field_18756 = -1;
			}

			for (int k = 0; k < this.field_18753.length; k++) {
				StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(k) - 18, j + 1, 0, this.field_18756 == k ? 0 : 18);
			}

			if (this.field_18759 != null) {
				int l = StatsScreen.this.method_2285(this.method_19409(this.field_18759)) - 36;
				int m = this.field_18760 == 1 ? 2 : 1;
				StatsScreen.this.method_2282(i + l, j + 1, 18 * m, 0);
			}

			for (int n = 0; n < this.field_18753.length; n++) {
				int o = this.field_18756 == n ? 1 : 0;
				StatsScreen.this.method_2282(i + StatsScreen.this.method_2285(n) - 18 + o, j + 1 + o, 18 * this.field_18753[n], 18);
			}
		}

		@Override
		public int getRowWidth() {
			return 375;
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width / 2 + 140;
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		@Override
		protected void clickedHeader(int i, int j) {
			this.field_18756 = -1;

			for (int k = 0; k < this.field_18753.length; k++) {
				int l = i - StatsScreen.this.method_2285(k);
				if (l >= -36 && l <= 0) {
					this.field_18756 = k;
					break;
				}
			}

			if (this.field_18756 >= 0) {
				this.method_19408(this.method_19410(this.field_18756));
				this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.field_15015, 1.0F));
			}
		}

		private StatType<?> method_19410(int i) {
			return i < this.field_18754.size() ? (StatType)this.field_18754.get(i) : (StatType)this.field_18755.get(i - this.field_18754.size());
		}

		private int method_19409(StatType<?> statType) {
			int i = this.field_18754.indexOf(statType);
			if (i >= 0) {
				return i;
			} else {
				int j = this.field_18755.indexOf(statType);
				return j >= 0 ? j + this.field_18754.size() : -1;
			}
		}

		@Override
		protected void renderDecorations(int i, int j) {
			if (j >= this.top && j <= this.bottom) {
				StatsScreen.ItemStatsListWidget.ItemStatItem itemStatItem = this.getEntryAtPosition((double)i, (double)j);
				int k = (this.width - this.getRowWidth()) / 2;
				if (itemStatItem != null) {
					if (i < k + 40 || i > k + 40 + 20) {
						return;
					}

					Item item = (Item)this.field_18757.get(this.children().indexOf(itemStatItem));
					this.method_19407(this.method_19406(item), i, j);
				} else {
					Text text = null;
					int l = i - k;

					for (int m = 0; m < this.field_18753.length; m++) {
						int n = StatsScreen.this.method_2285(m);
						if (l >= n - 18 && l <= n) {
							text = new TranslatableText(this.method_19410(m).getTranslationKey());
							break;
						}
					}

					this.method_19407(text, i, j);
				}
			}
		}

		protected void method_19407(@Nullable Text text, int i, int j) {
			if (text != null) {
				String string = text.asFormattedString();
				int k = i + 12;
				int l = j - 12;
				int m = StatsScreen.this.font.getStringWidth(string);
				this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				StatsScreen.this.font.drawWithShadow(string, (float)k, (float)l, -1);
			}
		}

		protected Text method_19406(Item item) {
			return item.getName();
		}

		protected void method_19408(StatType<?> statType) {
			if (statType != this.field_18759) {
				this.field_18759 = statType;
				this.field_18760 = -1;
			} else if (this.field_18760 == -1) {
				this.field_18760 = 1;
			} else {
				this.field_18759 = null;
				this.field_18760 = 0;
			}

			this.field_18757.sort(this.field_18758);
		}

		class ItemStatItem extends AlwaysSelectedEntryListWidget.Entry<StatsScreen.ItemStatsListWidget.ItemStatItem> {
			private ItemStatItem() {
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				Item item = (Item)StatsScreen.this.itemsButton.field_18757.get(i);
				StatsScreen.this.method_2289(k + 40, j, item);

				for (int p = 0; p < StatsScreen.this.itemsButton.field_18754.size(); p++) {
					Stat<Block> stat;
					if (item instanceof BlockItem) {
						stat = ((StatType)StatsScreen.this.itemsButton.field_18754.get(p)).getOrCreateStat(((BlockItem)item).getBlock());
					} else {
						stat = null;
					}

					this.method_19405(stat, k + StatsScreen.this.method_2285(p), j, i % 2 == 0);
				}

				for (int q = 0; q < StatsScreen.this.itemsButton.field_18755.size(); q++) {
					this.method_19405(
						((StatType)StatsScreen.this.itemsButton.field_18755.get(q)).getOrCreateStat(item),
						k + StatsScreen.this.method_2285(q + StatsScreen.this.itemsButton.field_18754.size()),
						j,
						i % 2 == 0
					);
				}
			}

			protected void method_19405(@Nullable Stat<?> stat, int i, int j, boolean bl) {
				String string = stat == null ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
				ItemStatsListWidget.this.drawString(StatsScreen.this.font, string, i - StatsScreen.this.font.getStringWidth(string), j + 5, bl ? 16777215 : 9474192);
			}
		}

		class class_450 implements Comparator<Item> {
			private class_450() {
			}

			public int method_2297(Item item, Item item2) {
				int i;
				int j;
				if (ItemStatsListWidget.this.field_18759 == null) {
					i = 0;
					j = 0;
				} else if (ItemStatsListWidget.this.field_18754.contains(ItemStatsListWidget.this.field_18759)) {
					StatType<Block> statType = (StatType<Block>)ItemStatsListWidget.this.field_18759;
					i = item instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item).getBlock()) : -1;
					j = item2 instanceof BlockItem ? StatsScreen.this.statHandler.getStat(statType, ((BlockItem)item2).getBlock()) : -1;
				} else {
					StatType<Item> statType2 = (StatType<Item>)ItemStatsListWidget.this.field_18759;
					i = StatsScreen.this.statHandler.getStat(statType2, item);
					j = StatsScreen.this.statHandler.getStat(statType2, item2);
				}

				return i == j
					? ItemStatsListWidget.this.field_18760 * Integer.compare(Item.getRawId(item), Item.getRawId(item2))
					: ItemStatsListWidget.this.field_18760 * Integer.compare(i, j);
			}
		}
	}
}
