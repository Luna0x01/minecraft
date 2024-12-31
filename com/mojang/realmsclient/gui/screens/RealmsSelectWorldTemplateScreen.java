package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreenWithCallback<WorldTemplate> lastScreen;
	private RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList field_20071;
	private int selectedTemplate = -1;
	private String title;
	private RealmsButton selectButton;
	private RealmsButton trailerButton;
	private RealmsButton publisherButton;
	private String toolTip;
	private String currentLink;
	private final RealmsServer.WorldType worldType;
	private int clicks;
	private String warning;
	private String warningURL;
	private boolean displayWarning;
	private boolean hoverWarning;
	private List<TextRenderingUtils.Line> noTemplatesMessage;

	public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> realmsScreenWithCallback, RealmsServer.WorldType worldType) {
		this(realmsScreenWithCallback, worldType, null);
	}

	public RealmsSelectWorldTemplateScreen(
		RealmsScreenWithCallback<WorldTemplate> realmsScreenWithCallback,
		RealmsServer.WorldType worldType,
		@Nullable WorldTemplatePaginatedList worldTemplatePaginatedList
	) {
		this.lastScreen = realmsScreenWithCallback;
		this.worldType = worldType;
		if (worldTemplatePaginatedList == null) {
			this.field_20071 = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList();
			this.method_21415(new WorldTemplatePaginatedList(10));
		} else {
			this.field_20071 = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(Lists.newArrayList(worldTemplatePaginatedList.templates));
			this.method_21415(worldTemplatePaginatedList);
		}

		this.title = getLocalizedString("mco.template.title");
	}

	public void setTitle(String string) {
		this.title = string;
	}

	public void setWarning(String string) {
		this.warning = string;
		this.displayWarning = true;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.hoverWarning && this.warningURL != null) {
			RealmsUtil.browseTo("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
			return true;
		} else {
			return super.mouseClicked(d, e, i);
		}
	}

	@Override
	public void init() {
		this.setKeyboardHandlerSendRepeatsToGui(true);
		this.field_20071 = new RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionList(this.field_20071.method_21450());
		this.buttonsAdd(
			this.trailerButton = new RealmsButton(2, this.width() / 2 - 206, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.trailer")) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen.this.onTrailer();
				}
			}
		);
		this.buttonsAdd(
			this.selectButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.select")) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen.this.selectTemplate();
				}
			}
		);
		this.buttonsAdd(
			new RealmsButton(
				0, this.width() / 2 + 6, this.height() - 32, 100, 20, getLocalizedString(this.worldType == RealmsServer.WorldType.MINIGAME ? "gui.cancel" : "gui.back")
			) {
				@Override
				public void onPress() {
					RealmsSelectWorldTemplateScreen.this.backButtonClicked();
				}
			}
		);
		this.publisherButton = new RealmsButton(3, this.width() / 2 + 112, this.height() - 32, 100, 20, getLocalizedString("mco.template.button.publisher")) {
			@Override
			public void onPress() {
				RealmsSelectWorldTemplateScreen.this.onPublish();
			}
		};
		this.buttonsAdd(this.publisherButton);
		this.selectButton.active(false);
		this.trailerButton.setVisible(false);
		this.publisherButton.setVisible(false);
		this.addWidget(this.field_20071);
		this.focusOn(this.field_20071);
		Realms.narrateNow((Iterable<String>)Stream.of(this.title, this.warning).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	private void updateButtonStates() {
		this.publisherButton.setVisible(this.shouldPublisherBeVisible());
		this.trailerButton.setVisible(this.shouldTrailerBeVisible());
		this.selectButton.active(this.shouldSelectButtonBeActive());
	}

	private boolean shouldSelectButtonBeActive() {
		return this.selectedTemplate != -1;
	}

	private boolean shouldPublisherBeVisible() {
		return this.selectedTemplate != -1 && !this.method_21434().link.isEmpty();
	}

	private WorldTemplate method_21434() {
		return this.field_20071.method_21447(this.selectedTemplate);
	}

	private boolean shouldTrailerBeVisible() {
		return this.selectedTemplate != -1 && !this.method_21434().trailer.isEmpty();
	}

	@Override
	public void tick() {
		super.tick();
		this.clicks--;
		if (this.clicks < 0) {
			this.clicks = 0;
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		switch (i) {
			case 256:
				this.backButtonClicked();
				return true;
			default:
				return super.keyPressed(i, j, k);
		}
	}

	private void backButtonClicked() {
		this.lastScreen.callback(null);
		Realms.setScreen(this.lastScreen);
	}

	private void selectTemplate() {
		if (this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount()) {
			WorldTemplate worldTemplate = this.method_21434();
			this.lastScreen.callback(worldTemplate);
		}
	}

	private void onTrailer() {
		if (this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount()) {
			WorldTemplate worldTemplate = this.method_21434();
			if (!"".equals(worldTemplate.trailer)) {
				RealmsUtil.browseTo(worldTemplate.trailer);
			}
		}
	}

	private void onPublish() {
		if (this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount()) {
			WorldTemplate worldTemplate = this.method_21434();
			if (!"".equals(worldTemplate.link)) {
				RealmsUtil.browseTo(worldTemplate.link);
			}
		}
	}

	private void method_21415(WorldTemplatePaginatedList worldTemplatePaginatedList) {
		(new Thread("realms-template-fetcher") {
				public void run() {
					WorldTemplatePaginatedList worldTemplatePaginatedList = worldTemplatePaginatedList;
					RealmsClient realmsClient = RealmsClient.createRealmsClient();

					while (worldTemplatePaginatedList != null) {
						Either<WorldTemplatePaginatedList, String> either = RealmsSelectWorldTemplateScreen.this.method_21416(worldTemplatePaginatedList, realmsClient);
						worldTemplatePaginatedList = (WorldTemplatePaginatedList)Realms.execute(
								(Supplier)(() -> {
									if (either.right().isPresent()) {
										RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", either.right().get());
										if (RealmsSelectWorldTemplateScreen.this.field_20071.method_21446()) {
											RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(
												RealmsScreen.getLocalizedString("mco.template.select.failure")
											);
										}

										return null;
									} else {
										assert either.left().isPresent();

										WorldTemplatePaginatedList worldTemplatePaginatedListxxx = (WorldTemplatePaginatedList)either.left().get();

										for (WorldTemplate worldTemplate : worldTemplatePaginatedListxxx.templates) {
											RealmsSelectWorldTemplateScreen.this.field_20071.addEntry(worldTemplate);
										}

										if (worldTemplatePaginatedListxxx.templates.isEmpty()) {
											if (RealmsSelectWorldTemplateScreen.this.field_20071.method_21446()) {
												String string = RealmsScreen.getLocalizedString("mco.template.select.none", "%link");
												TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(
													RealmsScreen.getLocalizedString("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/"
												);
												RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
											}

											return null;
										} else {
											return worldTemplatePaginatedListxxx;
										}
									}
								})
							)
							.join();
					}
				}
			})
			.start();
	}

	private Either<WorldTemplatePaginatedList, String> method_21416(WorldTemplatePaginatedList worldTemplatePaginatedList, RealmsClient realmsClient) {
		try {
			return Either.left(realmsClient.fetchWorldTemplates(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.worldType));
		} catch (RealmsServiceException var4) {
			return Either.right(var4.getMessage());
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.toolTip = null;
		this.currentLink = null;
		this.hoverWarning = false;
		this.renderBackground();
		this.field_20071.render(i, j, f);
		if (this.noTemplatesMessage != null) {
			this.method_21414(i, j, this.noTemplatesMessage);
		}

		this.drawCenteredString(this.title, this.width() / 2, 13, 16777215);
		if (this.displayWarning) {
			String[] strings = this.warning.split("\\\\n");

			for (int k = 0; k < strings.length; k++) {
				int l = this.fontWidth(strings[k]);
				int m = this.width() / 2 - l / 2;
				int n = RealmsConstants.row(-1 + k);
				if (i >= m && i <= m + l && j >= n && j <= n + this.fontLineHeight()) {
					this.hoverWarning = true;
				}
			}

			for (int o = 0; o < strings.length; o++) {
				String string = strings[o];
				int p = 10526880;
				if (this.warningURL != null) {
					if (this.hoverWarning) {
						p = 7107012;
						string = "§n" + string;
					} else {
						p = 3368635;
					}
				}

				this.drawCenteredString(string, this.width() / 2, RealmsConstants.row(-1 + o), p);
			}
		}

		super.render(i, j, f);
		if (this.toolTip != null) {
			this.renderMousehoverTooltip(this.toolTip, i, j);
		}
	}

	private void method_21414(int i, int j, List<TextRenderingUtils.Line> list) {
		for (int k = 0; k < list.size(); k++) {
			TextRenderingUtils.Line line = (TextRenderingUtils.Line)list.get(k);
			int l = RealmsConstants.row(4 + k);
			int m = line.segments.stream().mapToInt(lineSegmentx -> this.fontWidth(lineSegmentx.renderedText())).sum();
			int n = this.width() / 2 - m / 2;

			for (TextRenderingUtils.LineSegment lineSegment : line.segments) {
				int o = lineSegment.isLink() ? 3368635 : 16777215;
				int p = this.draw(lineSegment.renderedText(), n, l, o, true);
				if (lineSegment.isLink() && i > n && i < p && j > l - 3 && j < l + 8) {
					this.toolTip = lineSegment.getLinkUrl();
					this.currentLink = lineSegment.getLinkUrl();
				}

				n = p;
			}
		}
	}

	protected void renderMousehoverTooltip(String string, int i, int j) {
		if (string != null) {
			int k = i + 12;
			int l = j - 12;
			int m = this.fontWidth(string);
			this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
			this.fontDrawShadow(string, k, l, 16777215);
		}
	}

	class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry> {
		public WorldTemplateObjectSelectionList() {
			this(Collections.emptyList());
		}

		public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> iterable) {
			super(
				RealmsSelectWorldTemplateScreen.this.width(),
				RealmsSelectWorldTemplateScreen.this.height(),
				RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsConstants.row(1) : 32,
				RealmsSelectWorldTemplateScreen.this.height() - 40,
				46
			);
			iterable.forEach(this::addEntry);
		}

		public void addEntry(WorldTemplate worldTemplate) {
			this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateObjectSelectionListEntry(worldTemplate));
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0 && e >= (double)this.y0() && e <= (double)this.y1()) {
				int j = this.width() / 2 - 150;
				if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
					RealmsUtil.browseTo(RealmsSelectWorldTemplateScreen.this.currentLink);
				}

				int k = (int)Math.floor(e - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
				int l = k / this.itemHeight();
				if (d >= (double)j && d < (double)this.getScrollbarPosition() && l >= 0 && k >= 0 && l < this.getItemCount()) {
					this.selectItem(l);
					this.itemClicked(k, l, d, e, this.width());
					if (l >= RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount()) {
						return super.mouseClicked(d, e, i);
					}

					RealmsSelectWorldTemplateScreen.this.selectedTemplate = l;
					RealmsSelectWorldTemplateScreen.this.updateButtonStates();
					RealmsSelectWorldTemplateScreen.this.clicks = RealmsSelectWorldTemplateScreen.this.clicks + 7;
					if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
						RealmsSelectWorldTemplateScreen.this.selectTemplate();
					}

					return true;
				}
			}

			return super.mouseClicked(d, e, i);
		}

		@Override
		public void selectItem(int i) {
			RealmsSelectWorldTemplateScreen.this.selectedTemplate = i;
			this.setSelected(i);
			if (i != -1) {
				WorldTemplate worldTemplate = RealmsSelectWorldTemplateScreen.this.field_20071.method_21447(i);
				String string = RealmsScreen.getLocalizedString("narrator.select.list.position", i + 1, RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount());
				String string2 = RealmsScreen.getLocalizedString("mco.template.select.narrate.version", worldTemplate.version);
				String string3 = RealmsScreen.getLocalizedString("mco.template.select.narrate.authors", worldTemplate.author);
				String string4 = Realms.joinNarrations(Arrays.asList(worldTemplate.name, string3, worldTemplate.recommendedPlayers, string2, string));
				Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string4));
			}

			RealmsSelectWorldTemplateScreen.this.updateButtonStates();
		}

		@Override
		public void itemClicked(int i, int j, double d, double e, int k) {
			if (j < RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount()) {
				;
			}
		}

		@Override
		public int getMaxPosition() {
			return this.getItemCount() * 46;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}

		@Override
		public void renderBackground() {
			RealmsSelectWorldTemplateScreen.this.renderBackground();
		}

		@Override
		public boolean isFocused() {
			return RealmsSelectWorldTemplateScreen.this.isFocused(this);
		}

		public boolean method_21446() {
			return this.getItemCount() == 0;
		}

		public WorldTemplate method_21447(int i) {
			return ((RealmsSelectWorldTemplateScreen.WorldTemplateObjectSelectionListEntry)this.children().get(i)).mTemplate;
		}

		public List<WorldTemplate> method_21450() {
			return (List<WorldTemplate>)this.children()
				.stream()
				.map(worldTemplateObjectSelectionListEntry -> worldTemplateObjectSelectionListEntry.mTemplate)
				.collect(Collectors.toList());
		}
	}

	class WorldTemplateObjectSelectionListEntry extends RealmListEntry {
		final WorldTemplate mTemplate;

		public WorldTemplateObjectSelectionListEntry(WorldTemplate worldTemplate) {
			this.mTemplate = worldTemplate;
		}

		@Override
		public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			this.renderWorldTemplateItem(this.mTemplate, k, j, n, o);
		}

		private void renderWorldTemplateItem(WorldTemplate worldTemplate, int i, int j, int k, int l) {
			int m = i + 45 + 20;
			RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.name, m, j + 2, 16777215);
			RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.author, m, j + 15, 8421504);
			RealmsSelectWorldTemplateScreen.this.drawString(
				worldTemplate.version, m + 227 - RealmsSelectWorldTemplateScreen.this.fontWidth(worldTemplate.version), j + 1, 8421504
			);
			if (!"".equals(worldTemplate.link) || !"".equals(worldTemplate.trailer) || !"".equals(worldTemplate.recommendedPlayers)) {
				this.drawIcons(m - 1, j + 25, k, l, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
			}

			this.drawImage(i, j + 1, k, l, worldTemplate);
		}

		private void drawImage(int i, int j, int k, int l, WorldTemplate worldTemplate) {
			RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RealmsScreen.blit(i + 1, j + 1, 0.0F, 0.0F, 38, 38, 38, 38);
			RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RealmsScreen.blit(i, j, 0.0F, 0.0F, 40, 40, 40, 40);
		}

		private void drawIcons(int i, int j, int k, int l, String string, String string2, String string3) {
			if (!"".equals(string3)) {
				RealmsSelectWorldTemplateScreen.this.drawString(string3, i, j + 4, 8421504);
			}

			int m = "".equals(string3) ? 0 : RealmsSelectWorldTemplateScreen.this.fontWidth(string3) + 2;
			boolean bl = false;
			boolean bl2 = false;
			if (k >= i + m && k <= i + m + 32 && l >= j && l <= j + 15 && l < RealmsSelectWorldTemplateScreen.this.height() - 15 && l > 32) {
				if (k <= i + 15 + m && k > m) {
					if ("".equals(string)) {
						bl2 = true;
					} else {
						bl = true;
					}
				} else if (!"".equals(string)) {
					bl2 = true;
				}
			}

			if (!"".equals(string)) {
				RealmsScreen.bind("realms:textures/gui/realms/link_icons.png");
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(i + m, j, bl ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
				RenderSystem.popMatrix();
			}

			if (!"".equals(string2)) {
				RealmsScreen.bind("realms:textures/gui/realms/trailer_icons.png");
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.pushMatrix();
				RenderSystem.scalef(1.0F, 1.0F, 1.0F);
				RealmsScreen.blit(i + m + ("".equals(string) ? 0 : 17), j, bl2 ? 15.0F : 0.0F, 0.0F, 15, 15, 30, 15);
				RenderSystem.popMatrix();
			}

			if (bl && !"".equals(string)) {
				RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
				RealmsSelectWorldTemplateScreen.this.currentLink = string;
			} else if (bl2 && !"".equals(string2)) {
				RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
				RealmsSelectWorldTemplateScreen.this.currentLink = string2;
			}
		}
	}
}
