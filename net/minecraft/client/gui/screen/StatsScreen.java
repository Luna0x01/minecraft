package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.CraftingStat;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import org.lwjgl.input.Mouse;

public class StatsScreen extends Screen implements StatsListener {
	protected Screen parent;
	protected String title = "Select world";
	private StatsScreen.GeneralStatsListWidget generalStats;
	private StatsScreen.ItemStatsListWidget itemStats;
	private StatsScreen.BlockStatsListWidget blockStats;
	private StatsScreen.EntityStatsListWidget mobStats;
	private final StatHandler statHandler;
	private ListWidget activeList;
	private boolean downloadingStats = true;

	public StatsScreen(Screen screen, StatHandler statHandler) {
		this.parent = screen;
		this.statHandler = statHandler;
	}

	@Override
	public void init() {
		this.title = I18n.translate("gui.stats");
		this.downloadingStats = true;
		this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		if (this.activeList != null) {
			this.activeList.handleMouse();
		}
	}

	public void createLists() {
		this.generalStats = new StatsScreen.GeneralStatsListWidget(this.client);
		this.generalStats.setButtonIds(1, 1);
		this.itemStats = new StatsScreen.ItemStatsListWidget(this.client);
		this.itemStats.setButtonIds(1, 1);
		this.blockStats = new StatsScreen.BlockStatsListWidget(this.client);
		this.blockStats.setButtonIds(1, 1);
		this.mobStats = new StatsScreen.EntityStatsListWidget(this.client);
		this.mobStats.setButtonIds(1, 1);
	}

	public void initButtons() {
		this.buttons.add(new ButtonWidget(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.translate("gui.done")));
		this.buttons.add(new ButtonWidget(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.translate("stat.generalButton")));
		ButtonWidget buttonWidget = this.addButton(new ButtonWidget(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.translate("stat.blocksButton")));
		ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(3, this.width / 2, this.height - 52, 80, 20, I18n.translate("stat.itemsButton")));
		ButtonWidget buttonWidget3 = this.addButton(new ButtonWidget(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.translate("stat.mobsButton")));
		if (this.blockStats.getEntryCount() == 0) {
			buttonWidget.active = false;
		}

		if (this.itemStats.getEntryCount() == 0) {
			buttonWidget2.active = false;
		}

		if (this.mobStats.getEntryCount() == 0) {
			buttonWidget3.active = false;
		}
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.active) {
			if (button.id == 0) {
				this.client.setScreen(this.parent);
			} else if (button.id == 1) {
				this.activeList = this.generalStats;
			} else if (button.id == 3) {
				this.activeList = this.itemStats;
			} else if (button.id == 2) {
				this.activeList = this.blockStats;
			} else if (button.id == 4) {
				this.activeList = this.mobStats;
			} else {
				this.activeList.buttonClicked(button);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (this.downloadingStats) {
			this.renderBackground();
			this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				this.textRenderer,
				PROGRESS_BAR_STAGES[(int)(MinecraftClient.getTime() / 150L % (long)PROGRESS_BAR_STAGES.length)],
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

	private void renderStatItem(int x, int y, Item item) {
		this.renderIcon(x + 1, y + 1);
		GlStateManager.enableRescaleNormal();
		DiffuseLighting.enable();
		this.itemRenderer.method_12455(item.getDefaultStack(), x + 2, y + 2);
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

	class BlockStatsListWidget extends StatsScreen.CustomStatsListWidget {
		public BlockStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient);
			this.entries = Lists.newArrayList();

			for (CraftingStat craftingStat : Stats.MINE) {
				boolean bl = false;
				Item item = craftingStat.getItem();
				if (StatsScreen.this.statHandler.getStatLevel(craftingStat) > 0) {
					bl = true;
				} else if (Stats.used(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.used(item)) > 0) {
					bl = true;
				} else if (Stats.crafted(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.crafted(item)) > 0) {
					bl = true;
				} else if (Stats.picked(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.picked(item)) > 0) {
					bl = true;
				} else if (Stats.dropped(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.dropped(item)) > 0) {
					bl = true;
				}

				if (bl) {
					this.entries.add(craftingStat);
				}
			}

			this.statComparator = new Comparator<CraftingStat>() {
				public int compare(CraftingStat craftingStat, CraftingStat craftingStat2) {
					Item item = craftingStat.getItem();
					Item item2 = craftingStat2.getItem();
					Stat stat = null;
					Stat stat2 = null;
					if (BlockStatsListWidget.this.sortColumnMode == 2) {
						stat = Stats.mined(Block.getBlockFromItem(item));
						stat2 = Stats.mined(Block.getBlockFromItem(item2));
					} else if (BlockStatsListWidget.this.sortColumnMode == 0) {
						stat = Stats.crafted(item);
						stat2 = Stats.crafted(item2);
					} else if (BlockStatsListWidget.this.sortColumnMode == 1) {
						stat = Stats.used(item);
						stat2 = Stats.used(item2);
					} else if (BlockStatsListWidget.this.sortColumnMode == 3) {
						stat = Stats.picked(item);
						stat2 = Stats.picked(item2);
					} else if (BlockStatsListWidget.this.sortColumnMode == 4) {
						stat = Stats.dropped(item);
						stat2 = Stats.dropped(item2);
					}

					if (stat != null || stat2 != null) {
						if (stat == null) {
							return 1;
						}

						if (stat2 == null) {
							return -1;
						}

						int i = StatsScreen.this.statHandler.getStatLevel(stat);
						int j = StatsScreen.this.statHandler.getStatLevel(stat2);
						if (i != j) {
							return (i - j) * BlockStatsListWidget.this.sortOrder;
						}
					}

					return Item.getRawId(item) - Item.getRawId(item2);
				}
			};
		}

		@Override
		protected void renderHeader(int x, int y, Tessellator tessellator) {
			super.renderHeader(x, y, tessellator);
			if (this.headerMode == 0) {
				StatsScreen.this.renderIcon(x + 115 - 18 + 1, y + 1 + 1, 18, 18);
			} else {
				StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 18, 18);
			}

			if (this.headerMode == 1) {
				StatsScreen.this.renderIcon(x + 165 - 18 + 1, y + 1 + 1, 36, 18);
			} else {
				StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 36, 18);
			}

			if (this.headerMode == 2) {
				StatsScreen.this.renderIcon(x + 215 - 18 + 1, y + 1 + 1, 54, 18);
			} else {
				StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 54, 18);
			}

			if (this.headerMode == 3) {
				StatsScreen.this.renderIcon(x + 265 - 18 + 1, y + 1 + 1, 90, 18);
			} else {
				StatsScreen.this.renderIcon(x + 265 - 18, y + 1, 90, 18);
			}

			if (this.headerMode == 4) {
				StatsScreen.this.renderIcon(x + 315 - 18 + 1, y + 1 + 1, 108, 18);
			} else {
				StatsScreen.this.renderIcon(x + 315 - 18, y + 1, 108, 18);
			}
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			CraftingStat craftingStat = this.getEntry(i);
			Item item = craftingStat.getItem();
			StatsScreen.this.renderStatItem(j + 40, k, item);
			this.renderStat(Stats.crafted(item), j + 115, k, i % 2 == 0);
			this.renderStat(Stats.used(item), j + 165, k, i % 2 == 0);
			this.renderStat(craftingStat, j + 215, k, i % 2 == 0);
			this.renderStat(Stats.picked(item), j + 265, k, i % 2 == 0);
			this.renderStat(Stats.dropped(item), j + 315, k, i % 2 == 0);
		}

		@Override
		protected String getText(int index) {
			if (index == 0) {
				return "stat.crafted";
			} else if (index == 1) {
				return "stat.used";
			} else if (index == 3) {
				return "stat.pickup";
			} else {
				return index == 4 ? "stat.dropped" : "stat.mined";
			}
		}
	}

	abstract class CustomStatsListWidget extends ListWidget {
		protected int headerMode = -1;
		protected List<CraftingStat> entries;
		protected Comparator<CraftingStat> statComparator;
		protected int sortColumnMode = -1;
		protected int sortOrder;

		protected CustomStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
			this.setRenderSelection(false);
			this.setHeader(true, 20);
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
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
		protected void renderHeader(int x, int y, Tessellator tessellator) {
			if (!Mouse.isButtonDown(0)) {
				this.headerMode = -1;
			}

			if (this.headerMode == 0) {
				StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 0, 0);
			} else {
				StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 0, 18);
			}

			if (this.headerMode == 1) {
				StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 0, 0);
			} else {
				StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 0, 18);
			}

			if (this.headerMode == 2) {
				StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 0, 0);
			} else {
				StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 0, 18);
			}

			if (this.headerMode == 3) {
				StatsScreen.this.renderIcon(x + 265 - 18, y + 1, 0, 0);
			} else {
				StatsScreen.this.renderIcon(x + 265 - 18, y + 1, 0, 18);
			}

			if (this.headerMode == 4) {
				StatsScreen.this.renderIcon(x + 315 - 18, y + 1, 0, 0);
			} else {
				StatsScreen.this.renderIcon(x + 315 - 18, y + 1, 0, 18);
			}

			if (this.sortColumnMode != -1) {
				int i = 79;
				int j = 18;
				if (this.sortColumnMode == 1) {
					i = 129;
				} else if (this.sortColumnMode == 2) {
					i = 179;
				} else if (this.sortColumnMode == 3) {
					i = 229;
				} else if (this.sortColumnMode == 4) {
					i = 279;
				}

				if (this.sortOrder == 1) {
					j = 36;
				}

				StatsScreen.this.renderIcon(x + i, y + 1, j, 0);
			}
		}

		@Override
		protected void clickedHeader(int mouseX, int mouseY) {
			this.headerMode = -1;
			if (mouseX >= 79 && mouseX < 115) {
				this.headerMode = 0;
			} else if (mouseX >= 129 && mouseX < 165) {
				this.headerMode = 1;
			} else if (mouseX >= 179 && mouseX < 215) {
				this.headerMode = 2;
			} else if (mouseX >= 229 && mouseX < 265) {
				this.headerMode = 3;
			} else if (mouseX >= 279 && mouseX < 315) {
				this.headerMode = 4;
			}

			if (this.headerMode >= 0) {
				this.changeSortOrder(this.headerMode);
				this.client.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.UI_BUTTON_CLICK, 1.0F));
			}
		}

		@Override
		protected final int getEntryCount() {
			return this.entries.size();
		}

		protected final CraftingStat getEntry(int index) {
			return (CraftingStat)this.entries.get(index);
		}

		protected abstract String getText(int index);

		protected void renderStat(Stat stat, int x, int y, boolean oddNumber) {
			if (stat != null) {
				String string = stat.formatValue(StatsScreen.this.statHandler.getStatLevel(stat));
				StatsScreen.this.drawWithShadow(
					StatsScreen.this.textRenderer, string, x - StatsScreen.this.textRenderer.getStringWidth(string), y + 5, oddNumber ? 16777215 : 9474192
				);
			} else {
				String string2 = "-";
				StatsScreen.this.drawWithShadow(
					StatsScreen.this.textRenderer, "-", x - StatsScreen.this.textRenderer.getStringWidth("-"), y + 5, oddNumber ? 16777215 : 9474192
				);
			}
		}

		@Override
		protected void renderDecorations(int mouseX, int mouseY) {
			if (mouseY >= this.yStart && mouseY <= this.yEnd) {
				int i = this.getEntryAt(mouseX, mouseY);
				int j = (this.width - this.getRowWidth()) / 2;
				if (i >= 0) {
					if (mouseX < j + 40 || mouseX > j + 40 + 20) {
						return;
					}

					CraftingStat craftingStat = this.getEntry(i);
					this.render(craftingStat, mouseX, mouseY);
				} else {
					String string;
					if (mouseX >= j + 115 - 18 && mouseX <= j + 115) {
						string = this.getText(0);
					} else if (mouseX >= j + 165 - 18 && mouseX <= j + 165) {
						string = this.getText(1);
					} else if (mouseX >= j + 215 - 18 && mouseX <= j + 215) {
						string = this.getText(2);
					} else if (mouseX >= j + 265 - 18 && mouseX <= j + 265) {
						string = this.getText(3);
					} else {
						if (mouseX < j + 315 - 18 || mouseX > j + 315) {
							return;
						}

						string = this.getText(4);
					}

					string = ("" + I18n.translate(string)).trim();
					if (!string.isEmpty()) {
						int k = mouseX + 12;
						int l = mouseY - 12;
						int m = StatsScreen.this.textRenderer.getStringWidth(string);
						StatsScreen.this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
						StatsScreen.this.textRenderer.drawWithShadow(string, (float)k, (float)l, -1);
					}
				}
			}
		}

		protected void render(CraftingStat stat, int x, int y) {
			if (stat != null) {
				Item item = stat.getItem();
				ItemStack itemStack = new ItemStack(item);
				String string = itemStack.getTranslationKey();
				String string2 = ("" + I18n.translate(string + ".name")).trim();
				if (!string2.isEmpty()) {
					int i = x + 12;
					int j = y - 12;
					int k = StatsScreen.this.textRenderer.getStringWidth(string2);
					StatsScreen.this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
					StatsScreen.this.textRenderer.drawWithShadow(string2, (float)i, (float)j, -1);
				}
			}
		}

		protected void changeSortOrder(int headerMode) {
			if (headerMode != this.sortColumnMode) {
				this.sortColumnMode = headerMode;
				this.sortOrder = -1;
			} else if (this.sortOrder == -1) {
				this.sortOrder = 1;
			} else {
				this.sortColumnMode = -1;
				this.sortOrder = 0;
			}

			Collections.sort(this.entries, this.statComparator);
		}
	}

	class EntityStatsListWidget extends ListWidget {
		private final List<EntityType.SpawnEggData> entries = Lists.newArrayList();

		public EntityStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, StatsScreen.this.textRenderer.fontHeight * 4);
			this.setRenderSelection(false);

			for (EntityType.SpawnEggData spawnEggData : EntityType.SPAWN_EGGS.values()) {
				if (StatsScreen.this.statHandler.getStatLevel(spawnEggData.killEntityStat) > 0
					|| StatsScreen.this.statHandler.getStatLevel(spawnEggData.killedByEntityStat) > 0) {
					this.entries.add(spawnEggData);
				}
			}
		}

		@Override
		protected int getEntryCount() {
			return this.entries.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
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
			EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)this.entries.get(i);
			String string = I18n.translate("entity." + EntityType.getEntityName(spawnEggData.identifier) + ".name");
			int o = StatsScreen.this.statHandler.getStatLevel(spawnEggData.killEntityStat);
			int p = StatsScreen.this.statHandler.getStatLevel(spawnEggData.killedByEntityStat);
			String string2 = I18n.translate("stat.entityKills", o, string);
			String string3 = I18n.translate("stat.entityKilledBy", string, p);
			if (o == 0) {
				string2 = I18n.translate("stat.entityKills.none", string);
			}

			if (p == 0) {
				string3 = I18n.translate("stat.entityKilledBy.none", string);
			}

			StatsScreen.this.drawWithShadow(StatsScreen.this.textRenderer, string, j + 2 - 10, k + 1, 16777215);
			StatsScreen.this.drawWithShadow(StatsScreen.this.textRenderer, string2, j + 2, k + 1 + StatsScreen.this.textRenderer.fontHeight, o == 0 ? 6316128 : 9474192);
			StatsScreen.this.drawWithShadow(
				StatsScreen.this.textRenderer, string3, j + 2, k + 1 + StatsScreen.this.textRenderer.fontHeight * 2, p == 0 ? 6316128 : 9474192
			);
		}
	}

	class GeneralStatsListWidget extends ListWidget {
		public GeneralStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
			this.setRenderSelection(false);
		}

		@Override
		protected int getEntryCount() {
			return Stats.GENERAL.size();
		}

		@Override
		protected void selectEntry(int index, boolean doubleClick, int lastMouseX, int lastMouseY) {
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
			Stat stat = (Stat)Stats.GENERAL.get(i);
			StatsScreen.this.drawWithShadow(StatsScreen.this.textRenderer, stat.getText().asUnformattedString(), j + 2, k + 1, i % 2 == 0 ? 16777215 : 9474192);
			String string = stat.formatValue(StatsScreen.this.statHandler.getStatLevel(stat));
			StatsScreen.this.drawWithShadow(
				StatsScreen.this.textRenderer, string, j + 2 + 213 - StatsScreen.this.textRenderer.getStringWidth(string), k + 1, i % 2 == 0 ? 16777215 : 9474192
			);
		}
	}

	class ItemStatsListWidget extends StatsScreen.CustomStatsListWidget {
		public ItemStatsListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient);
			this.entries = Lists.newArrayList();

			for (CraftingStat craftingStat : Stats.ITEM) {
				boolean bl = false;
				Item item = craftingStat.getItem();
				if (StatsScreen.this.statHandler.getStatLevel(craftingStat) > 0) {
					bl = true;
				} else if (Stats.broke(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.broke(item)) > 0) {
					bl = true;
				} else if (Stats.crafted(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.crafted(item)) > 0) {
					bl = true;
				} else if (Stats.picked(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.picked(item)) > 0) {
					bl = true;
				} else if (Stats.dropped(item) != null && StatsScreen.this.statHandler.getStatLevel(Stats.dropped(item)) > 0) {
					bl = true;
				}

				if (bl) {
					this.entries.add(craftingStat);
				}
			}

			this.statComparator = new Comparator<CraftingStat>() {
				public int compare(CraftingStat craftingStat, CraftingStat craftingStat2) {
					Item item = craftingStat.getItem();
					Item item2 = craftingStat2.getItem();
					int i = Item.getRawId(item);
					int j = Item.getRawId(item2);
					Stat stat = null;
					Stat stat2 = null;
					if (ItemStatsListWidget.this.sortColumnMode == 0) {
						stat = Stats.broke(item);
						stat2 = Stats.broke(item2);
					} else if (ItemStatsListWidget.this.sortColumnMode == 1) {
						stat = Stats.crafted(item);
						stat2 = Stats.crafted(item2);
					} else if (ItemStatsListWidget.this.sortColumnMode == 2) {
						stat = Stats.used(item);
						stat2 = Stats.used(item2);
					} else if (ItemStatsListWidget.this.sortColumnMode == 3) {
						stat = Stats.picked(item);
						stat2 = Stats.picked(item2);
					} else if (ItemStatsListWidget.this.sortColumnMode == 4) {
						stat = Stats.dropped(item);
						stat2 = Stats.dropped(item2);
					}

					if (stat != null || stat2 != null) {
						if (stat == null) {
							return 1;
						}

						if (stat2 == null) {
							return -1;
						}

						int k = StatsScreen.this.statHandler.getStatLevel(stat);
						int l = StatsScreen.this.statHandler.getStatLevel(stat2);
						if (k != l) {
							return (k - l) * ItemStatsListWidget.this.sortOrder;
						}
					}

					return i - j;
				}
			};
		}

		@Override
		protected void renderHeader(int x, int y, Tessellator tessellator) {
			super.renderHeader(x, y, tessellator);
			if (this.headerMode == 0) {
				StatsScreen.this.renderIcon(x + 115 - 18 + 1, y + 1 + 1, 72, 18);
			} else {
				StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 72, 18);
			}

			if (this.headerMode == 1) {
				StatsScreen.this.renderIcon(x + 165 - 18 + 1, y + 1 + 1, 18, 18);
			} else {
				StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 18, 18);
			}

			if (this.headerMode == 2) {
				StatsScreen.this.renderIcon(x + 215 - 18 + 1, y + 1 + 1, 36, 18);
			} else {
				StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 36, 18);
			}

			if (this.headerMode == 3) {
				StatsScreen.this.renderIcon(x + 265 - 18 + 1, y + 1 + 1, 90, 18);
			} else {
				StatsScreen.this.renderIcon(x + 265 - 18, y + 1, 90, 18);
			}

			if (this.headerMode == 4) {
				StatsScreen.this.renderIcon(x + 315 - 18 + 1, y + 1 + 1, 108, 18);
			} else {
				StatsScreen.this.renderIcon(x + 315 - 18, y + 1, 108, 18);
			}
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			CraftingStat craftingStat = this.getEntry(i);
			Item item = craftingStat.getItem();
			StatsScreen.this.renderStatItem(j + 40, k, item);
			this.renderStat(Stats.broke(item), j + 115, k, i % 2 == 0);
			this.renderStat(Stats.crafted(item), j + 165, k, i % 2 == 0);
			this.renderStat(craftingStat, j + 215, k, i % 2 == 0);
			this.renderStat(Stats.picked(item), j + 265, k, i % 2 == 0);
			this.renderStat(Stats.dropped(item), j + 315, k, i % 2 == 0);
		}

		@Override
		protected String getText(int index) {
			if (index == 1) {
				return "stat.crafted";
			} else if (index == 2) {
				return "stat.used";
			} else if (index == 3) {
				return "stat.pickup";
			} else {
				return index == 4 ? "stat.dropped" : "stat.depleted";
			}
		}
	}
}
