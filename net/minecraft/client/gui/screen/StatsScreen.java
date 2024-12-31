package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4122;
import net.minecraft.class_4123;
import net.minecraft.class_4472;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.StatsListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class StatsScreen extends Screen implements StatsListener {
	protected Screen parent;
	protected String title = "Select world";
	private StatsScreen.GeneralStatsListWidget generalStats;
	private StatsScreen.ItemStatsListWidget itemStats;
	private StatsScreen.EntityStatsListWidget mobStats;
	private final StatHandler statHandler;
	private ListWidget activeList;
	private boolean downloadingStats = true;

	public StatsScreen(Screen screen, StatHandler statHandler) {
		this.parent = screen;
		this.statHandler = statHandler;
	}

	@Override
	public class_4122 getFocused() {
		return this.activeList;
	}

	@Override
	protected void init() {
		this.title = I18n.translate("gui.stats");
		this.downloadingStats = true;
		this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
	}

	public void createLists() {
		this.generalStats = new StatsScreen.GeneralStatsListWidget(this.client);
		this.itemStats = new StatsScreen.ItemStatsListWidget(this.client);
		this.mobStats = new StatsScreen.EntityStatsListWidget(this.client);
	}

	public void initButtons() {
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height - 28, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				StatsScreen.this.client.setScreen(StatsScreen.this.parent);
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton")) {
			@Override
			public void method_18374(double d, double e) {
				StatsScreen.this.activeList = StatsScreen.this.generalStats;
			}
		});
		ButtonWidget buttonWidget = this.addButton(new ButtonWidget(3, this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton")) {
			@Override
			public void method_18374(double d, double e) {
				StatsScreen.this.activeList = StatsScreen.this.itemStats;
			}
		});
		ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(4, this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton")) {
			@Override
			public void method_18374(double d, double e) {
				StatsScreen.this.activeList = StatsScreen.this.mobStats;
			}
		});
		if (this.itemStats.getEntryCount() == 0) {
			buttonWidget.active = false;
		}

		if (this.mobStats.getEntryCount() == 0) {
			buttonWidget2.active = false;
		}

		this.field_20307.add((class_4123)() -> this.activeList);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.downloadingStats) {
			this.renderBackground();
			this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				this.textRenderer,
				PROGRESS_BAR_STAGES[(int)(Util.method_20227() / 150L % (long)PROGRESS_BAR_STAGES.length)],
				this.width / 2,
				this.height / 2 + this.textRenderer.fontHeight * 2,
				16777215
			);
		} else {
			this.activeList.render(mouseX, mouseY, tickDelta);
			this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 20, 16777215);
			super.render(mouseX, mouseY, tickDelta);
		}
	}

	@Override
	public void onStatsReady() {
		if (this.downloadingStats) {
			this.createLists();
			this.initButtons();
			this.activeList = this.generalStats;
			this.downloadingStats = false;
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return !this.downloadingStats;
	}

	private int method_18625(int i) {
		return 115 + 40 * i;
	}

	private void renderStatItem(int x, int y, Item item) {
		this.renderIcon(x + 1, y + 1);
		GlStateManager.enableRescaleNormal();
		DiffuseLighting.enable();
		this.field_20308.method_19376(item.getDefaultStack(), x + 2, y + 2);
		DiffuseLighting.disable();
		GlStateManager.disableRescaleNormal();
	}

	private void renderIcon(int x, int y) {
		this.renderIcon(x, y, 0, 0);
	}

	private void renderIcon(int x, int y, int u, int v) {
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

	class EntityStatsListWidget extends ListWidget {
		private final List<EntityType<?>> entries = Lists.newArrayList();

		public EntityStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, StatsScreen.this.textRenderer.fontHeight * 4);
			this.setRenderSelection(false);

			for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
				if (StatsScreen.this.statHandler.method_21434(Stats.KILLED.method_21429(entityType)) > 0
					|| StatsScreen.this.statHandler.method_21434(Stats.KILLED_BY.method_21429(entityType)) > 0) {
					this.entries.add(entityType);
				}
			}
		}

		@Override
		protected int getEntryCount() {
			return this.entries.size();
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return false;
		}

		@Override
		protected int getMaxPosition() {
			return this.getEntryCount() * StatsScreen.this.textRenderer.fontHeight * 4;
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			EntityType<?> entityType = (EntityType<?>)this.entries.get(i);
			String string = I18n.translate(Util.createTranslationKey("entity", EntityType.getId(entityType)));
			int o = StatsScreen.this.statHandler.method_21434(Stats.KILLED.method_21429(entityType));
			int p = StatsScreen.this.statHandler.method_21434(Stats.KILLED_BY.method_21429(entityType));
			this.drawWithShadow(StatsScreen.this.textRenderer, string, j + 2 - 10, k + 1, 16777215);
			this.drawWithShadow(
				StatsScreen.this.textRenderer, this.method_18641(string, o), j + 2, k + 1 + StatsScreen.this.textRenderer.fontHeight, o == 0 ? 6316128 : 9474192
			);
			this.drawWithShadow(
				StatsScreen.this.textRenderer, this.method_18642(string, p), j + 2, k + 1 + StatsScreen.this.textRenderer.fontHeight * 2, p == 0 ? 6316128 : 9474192
			);
		}

		private String method_18641(String string, int i) {
			String string2 = Stats.KILLED.method_21430();
			return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, i, string);
		}

		private String method_18642(String string, int i) {
			String string2 = Stats.KILLED_BY.method_21430();
			return i == 0 ? I18n.translate(string2 + ".none", string) : I18n.translate(string2, string, i);
		}
	}

	class GeneralStatsListWidget extends ListWidget {
		private Iterator<class_4472<Identifier>> field_20336;

		public GeneralStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
			this.setRenderSelection(false);
		}

		@Override
		protected int getEntryCount() {
			return Stats.CUSTOM.method_21428();
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return false;
		}

		@Override
		protected int getMaxPosition() {
			return this.getEntryCount() * 10;
		}

		@Override
		protected void renderBackground() {
			StatsScreen.this.renderBackground();
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			if (i == 0) {
				this.field_20336 = Stats.CUSTOM.iterator();
			}

			class_4472<Identifier> lv = (class_4472<Identifier>)this.field_20336.next();
			Text text = new TranslatableText("stat." + lv.method_21423().toString().replace(':', '.')).formatted(Formatting.GRAY);
			this.drawWithShadow(StatsScreen.this.textRenderer, text.getString(), j + 2, k + 1, i % 2 == 0 ? 16777215 : 9474192);
			String string = lv.method_21420(StatsScreen.this.statHandler.method_21434(lv));
			this.drawWithShadow(
				StatsScreen.this.textRenderer, string, j + 2 + 213 - StatsScreen.this.textRenderer.getStringWidth(string), k + 1, i % 2 == 0 ? 16777215 : 9474192
			);
		}
	}

	class ItemStatsListWidget extends ListWidget {
		protected final List<StatType<Block>> field_20340;
		protected final List<StatType<Item>> field_20341;
		private final int[] field_20339 = new int[]{3, 4, 1, 2, 5, 6};
		protected int field_20342 = -1;
		protected final List<Item> field_20343;
		protected final Comparator<Item> field_20344 = new StatsScreen.ItemStatsListWidget.class_4160();
		@Nullable
		protected StatType<?> field_20337;
		protected int field_20338;

		public ItemStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
			this.field_20340 = Lists.newArrayList();
			this.field_20340.add(Stats.MINED);
			this.field_20341 = Lists.newArrayList(new StatType[]{Stats.BROKEN, Stats.CRAFTED, Stats.USED, Stats.PICKED_UP, Stats.DROPPED});
			this.setRenderSelection(false);
			this.setHeader(true, 20);
			Set<Item> set = Sets.newIdentityHashSet();

			for (Item item : Registry.ITEM) {
				boolean bl = false;

				for (StatType<Item> statType : this.field_20341) {
					if (statType.method_21425(item) && StatsScreen.this.statHandler.method_21434(statType.method_21429(item)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(item);
				}
			}

			for (Block block : Registry.BLOCK) {
				boolean bl2 = false;

				for (StatType<Block> statType2 : this.field_20340) {
					if (statType2.method_21425(block) && StatsScreen.this.statHandler.method_21434(statType2.method_21429(block)) > 0) {
						bl2 = true;
					}
				}

				if (bl2) {
					set.add(block.getItem());
				}
			}

			set.remove(Items.AIR);
			this.field_20343 = Lists.newArrayList(set);
		}

		@Override
		protected void renderHeader(int x, int y, Tessellator tessellator) {
			if (!this.client.field_19945.method_18245()) {
				this.field_20342 = -1;
			}

			for (int i = 0; i < this.field_20339.length; i++) {
				StatsScreen.this.renderIcon(x + StatsScreen.this.method_18625(i) - 18, y + 1, 0, this.field_20342 == i ? 0 : 18);
			}

			if (this.field_20337 != null) {
				int j = StatsScreen.this.method_18625(this.method_18637(this.field_20337)) - 36;
				int k = this.field_20338 == 1 ? 2 : 1;
				StatsScreen.this.renderIcon(x + j, y + 1, 18 * k, 0);
			}

			for (int l = 0; l < this.field_20339.length; l++) {
				int m = this.field_20342 != l ? 0 : 1;
				StatsScreen.this.renderIcon(x + StatsScreen.this.method_18625(l) - 18 + m, y + 1 + m, 18 * this.field_20339[l], 18);
			}
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			Item item = this.method_18638(i);
			StatsScreen.this.renderStatItem(j + 40, k, item);

			for (int o = 0; o < this.field_20340.size(); o++) {
				class_4472<Block> lv;
				if (item instanceof BlockItem) {
					lv = ((StatType)this.field_20340.get(o)).method_21429(((BlockItem)item).getBlock());
				} else {
					lv = null;
				}

				this.method_18635(lv, j + StatsScreen.this.method_18625(o), k, i % 2 == 0);
			}

			for (int p = 0; p < this.field_20341.size(); p++) {
				this.method_18635(((StatType)this.field_20341.get(p)).method_21429(item), j + StatsScreen.this.method_18625(p + this.field_20340.size()), k, i % 2 == 0);
			}
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return false;
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
		protected void clickedHeader(int mouseX, int mouseY) {
			this.field_20342 = -1;

			for (int i = 0; i < this.field_20339.length; i++) {
				int j = mouseX - StatsScreen.this.method_18625(i);
				if (j >= -36 && j <= 0) {
					this.field_20342 = i;
					break;
				}
			}

			if (this.field_20342 >= 0) {
				this.method_18636(this.method_18639(this.field_20342));
				this.client.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
			}
		}

		private StatType<?> method_18639(int i) {
			return i < this.field_20340.size() ? (StatType)this.field_20340.get(i) : (StatType)this.field_20341.get(i - this.field_20340.size());
		}

		private int method_18637(StatType<?> statType) {
			int i = this.field_20340.indexOf(statType);
			if (i >= 0) {
				return i;
			} else {
				int j = this.field_20341.indexOf(statType);
				return j >= 0 ? j + this.field_20340.size() : -1;
			}
		}

		@Override
		protected final int getEntryCount() {
			return this.field_20343.size();
		}

		protected final Item method_18638(int i) {
			return (Item)this.field_20343.get(i);
		}

		protected void method_18635(@Nullable class_4472<?> arg, int i, int j, boolean bl) {
			String string = arg == null ? "-" : arg.method_21420(StatsScreen.this.statHandler.method_21434(arg));
			this.drawWithShadow(StatsScreen.this.textRenderer, string, i - StatsScreen.this.textRenderer.getStringWidth(string), j + 5, bl ? 16777215 : 9474192);
		}

		@Override
		protected void renderDecorations(int mouseX, int mouseY) {
			if (mouseY >= this.yStart && mouseY <= this.yEnd) {
				int i = this.method_18411((double)mouseX, (double)mouseY);
				int j = (this.width - this.getRowWidth()) / 2;
				if (i >= 0) {
					if (mouseX < j + 40 || mouseX > j + 40 + 20) {
						return;
					}

					Item item = this.method_18638(i);
					this.method_18634(this.method_18633(item), mouseX, mouseY);
				} else {
					Text text = null;
					int k = mouseX - j;

					for (int l = 0; l < this.field_20339.length; l++) {
						int m = StatsScreen.this.method_18625(l);
						if (k >= m - 18 && k <= m) {
							text = new TranslatableText(this.method_18639(l).method_21430());
							break;
						}
					}

					this.method_18634(text, mouseX, mouseY);
				}
			}
		}

		protected void method_18634(@Nullable Text text, int i, int j) {
			if (text != null) {
				String string = text.asFormattedString();
				int k = i + 12;
				int l = j - 12;
				int m = StatsScreen.this.textRenderer.getStringWidth(string);
				this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				StatsScreen.this.textRenderer.drawWithShadow(string, (float)k, (float)l, -1);
			}
		}

		protected Text method_18633(Item item) {
			return item.method_16080();
		}

		protected void method_18636(StatType<?> statType) {
			if (statType != this.field_20337) {
				this.field_20337 = statType;
				this.field_20338 = -1;
			} else if (this.field_20338 == -1) {
				this.field_20338 = 1;
			} else {
				this.field_20337 = null;
				this.field_20338 = 0;
			}

			this.field_20343.sort(this.field_20344);
		}

		class class_4160 implements Comparator<Item> {
			private class_4160() {
			}

			public int compare(Item item, Item item2) {
				int i;
				int j;
				if (ItemStatsListWidget.this.field_20337 == null) {
					i = 0;
					j = 0;
				} else if (ItemStatsListWidget.this.field_20340.contains(ItemStatsListWidget.this.field_20337)) {
					StatType<Block> statType = (StatType<Block>)ItemStatsListWidget.this.field_20337;
					i = item instanceof BlockItem ? StatsScreen.this.statHandler.method_21435(statType, ((BlockItem)item).getBlock()) : -1;
					j = item2 instanceof BlockItem ? StatsScreen.this.statHandler.method_21435(statType, ((BlockItem)item2).getBlock()) : -1;
				} else {
					StatType<Item> statType2 = (StatType<Item>)ItemStatsListWidget.this.field_20337;
					i = StatsScreen.this.statHandler.method_21435(statType2, item);
					j = StatsScreen.this.statHandler.method_21435(statType2, item2);
				}

				return i == j
					? ItemStatsListWidget.this.field_20338 * Integer.compare(Item.getRawId(item), Item.getRawId(item2))
					: ItemStatsListWidget.this.field_20338 * Integer.compare(i, j);
			}
		}
	}
}
