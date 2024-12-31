package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.class_3251;
import net.minecraft.class_3297;
import net.minecraft.class_3306;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.gui.CreativeInventoryListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class CreativeInventoryScreen extends InventoryScreen {
	private static final Identifier ITEM_GROUPS = new Identifier("textures/gui/container/creative_inventory/tabs.png");
	private static final SimpleInventory inventory = new SimpleInventory(new LiteralText("tmp"), 45);
	private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
	private float scrollPosition;
	private boolean hasScrollbar;
	private TextFieldWidget searchField;
	private List<Slot> slots;
	private Slot deleteItemSlot;
	private CreativeInventoryListener listener;
	private boolean field_20401;
	private boolean field_20402;

	public CreativeInventoryScreen(PlayerEntity playerEntity) {
		super(new CreativeInventoryScreen.CreativeScreenHandler(playerEntity));
		playerEntity.openScreenHandler = this.screenHandler;
		this.passEvents = true;
		this.backgroundHeight = 136;
		this.backgroundWidth = 195;
	}

	@Override
	public void tick() {
		if (!this.client.interactionManager.hasCreativeInventory()) {
			this.client.setScreen(new SurvivalInventoryScreen(this.client.player));
		}
	}

	@Override
	protected void method_1131(@Nullable Slot slot, int i, int j, ItemAction itemAction) {
		if (this.method_18732(slot)) {
			this.searchField.setCursorToEnd();
			this.searchField.setSelectionEnd(0);
		}

		boolean bl = itemAction == ItemAction.QUICK_MOVE;
		itemAction = i == -999 && itemAction == ItemAction.PICKUP ? ItemAction.THROW : itemAction;
		if (slot == null && selectedTab != ItemGroup.INVENTORY.getIndex() && itemAction != ItemAction.QUICK_CRAFT) {
			PlayerInventory playerInventory2 = this.client.player.inventory;
			if (!playerInventory2.getCursorStack().isEmpty() && this.field_20402) {
				if (j == 0) {
					this.client.player.dropItem(playerInventory2.getCursorStack(), true);
					this.client.interactionManager.dropCreativeStack(playerInventory2.getCursorStack());
					playerInventory2.setCursorStack(ItemStack.EMPTY);
				}

				if (j == 1) {
					ItemStack itemStack11 = playerInventory2.getCursorStack().split(1);
					this.client.player.dropItem(itemStack11, true);
					this.client.interactionManager.dropCreativeStack(itemStack11);
				}
			}
		} else {
			if (slot != null && !slot.canTakeItems(this.client.player)) {
				return;
			}

			if (slot == this.deleteItemSlot && bl) {
				for (int k = 0; k < this.client.player.playerScreenHandler.method_13641().size(); k++) {
					this.client.interactionManager.clickCreativeStack(ItemStack.EMPTY, k);
				}
			} else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
				if (slot == this.deleteItemSlot) {
					this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else if (itemAction == ItemAction.THROW && slot != null && slot.hasStack()) {
					ItemStack itemStack = slot.takeStack(j == 0 ? 1 : slot.getStack().getMaxCount());
					ItemStack itemStack2 = slot.getStack();
					this.client.player.dropItem(itemStack, true);
					this.client.interactionManager.dropCreativeStack(itemStack);
					this.client.interactionManager.clickCreativeStack(itemStack2, ((CreativeInventoryScreen.CreativeInventorySlot)slot).slot.id);
				} else if (itemAction == ItemAction.THROW && !this.client.player.inventory.getCursorStack().isEmpty()) {
					this.client.player.dropItem(this.client.player.inventory.getCursorStack(), true);
					this.client.interactionManager.dropCreativeStack(this.client.player.inventory.getCursorStack());
					this.client.player.inventory.setCursorStack(ItemStack.EMPTY);
				} else {
					this.client
						.player
						.playerScreenHandler
						.method_3252(slot == null ? i : ((CreativeInventoryScreen.CreativeInventorySlot)slot).slot.id, j, itemAction, this.client.player);
					this.client.player.playerScreenHandler.sendContentUpdates();
				}
			} else if (itemAction != ItemAction.QUICK_CRAFT && slot.inventory == inventory) {
				PlayerInventory playerInventory = this.client.player.inventory;
				ItemStack itemStack3 = playerInventory.getCursorStack();
				ItemStack itemStack4 = slot.getStack();
				if (itemAction == ItemAction.SWAP) {
					if (!itemStack4.isEmpty() && j >= 0 && j < 9) {
						ItemStack itemStack5 = itemStack4.copy();
						itemStack5.setCount(itemStack5.getMaxCount());
						this.client.player.inventory.setInvStack(j, itemStack5);
						this.client.player.playerScreenHandler.sendContentUpdates();
					}

					return;
				}

				if (itemAction == ItemAction.CLONE) {
					if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
						ItemStack itemStack6 = slot.getStack().copy();
						itemStack6.setCount(itemStack6.getMaxCount());
						playerInventory.setCursorStack(itemStack6);
					}

					return;
				}

				if (itemAction == ItemAction.THROW) {
					if (!itemStack4.isEmpty()) {
						ItemStack itemStack7 = itemStack4.copy();
						itemStack7.setCount(j == 0 ? 1 : itemStack7.getMaxCount());
						this.client.player.dropItem(itemStack7, true);
						this.client.interactionManager.dropCreativeStack(itemStack7);
					}

					return;
				}

				if (!itemStack3.isEmpty() && !itemStack4.isEmpty() && itemStack3.equalsIgnoreNbt(itemStack4) && ItemStack.equalsIgnoreDamage(itemStack3, itemStack4)) {
					if (j == 0) {
						if (bl) {
							itemStack3.setCount(itemStack3.getMaxCount());
						} else if (itemStack3.getCount() < itemStack3.getMaxCount()) {
							itemStack3.increment(1);
						}
					} else {
						itemStack3.decrement(1);
					}
				} else if (!itemStack4.isEmpty() && itemStack3.isEmpty()) {
					playerInventory.setCursorStack(itemStack4.copy());
					itemStack3 = playerInventory.getCursorStack();
					if (bl) {
						itemStack3.setCount(itemStack3.getMaxCount());
					}
				} else if (j == 0) {
					playerInventory.setCursorStack(ItemStack.EMPTY);
				} else {
					playerInventory.getCursorStack().decrement(1);
				}
			} else if (this.screenHandler != null) {
				ItemStack itemStack8 = slot == null ? ItemStack.EMPTY : this.screenHandler.getSlot(slot.id).getStack();
				this.screenHandler.method_3252(slot == null ? i : slot.id, j, itemAction, this.client.player);
				if (ScreenHandler.unpackButtonId(j) == 2) {
					for (int l = 0; l < 9; l++) {
						this.client.interactionManager.clickCreativeStack(this.screenHandler.getSlot(45 + l).getStack(), 36 + l);
					}
				} else if (slot != null) {
					ItemStack itemStack9 = this.screenHandler.getSlot(slot.id).getStack();
					this.client.interactionManager.clickCreativeStack(itemStack9, slot.id - this.screenHandler.slots.size() + 9 + 36);
					int m = 45 + j;
					if (itemAction == ItemAction.SWAP) {
						this.client.interactionManager.clickCreativeStack(itemStack8, m - this.screenHandler.slots.size() + 9 + 36);
					} else if (itemAction == ItemAction.THROW && !itemStack8.isEmpty()) {
						ItemStack itemStack10 = itemStack8.copy();
						itemStack10.setCount(j == 0 ? 1 : itemStack10.getMaxCount());
						this.client.player.dropItem(itemStack10, true);
						this.client.interactionManager.dropCreativeStack(itemStack10);
					}

					this.client.player.playerScreenHandler.sendContentUpdates();
				}
			}
		}
	}

	private boolean method_18732(@Nullable Slot slot) {
		return slot != null && slot.inventory == inventory;
	}

	@Override
	protected void applyStatusEffectOffset() {
		int i = this.x;
		super.applyStatusEffectOffset();
		if (this.searchField != null && this.x != i) {
			this.searchField.x = this.x + 82;
		}
	}

	@Override
	protected void init() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			super.init();
			this.client.field_19946.method_18191(true);
			this.searchField = new TextFieldWidget(0, this.textRenderer, this.x + 82, this.y + 6, 80, this.textRenderer.fontHeight);
			this.searchField.setMaxLength(50);
			this.searchField.setHasBorder(false);
			this.searchField.setVisible(false);
			this.searchField.setEditableColor(16777215);
			this.field_20307.add(this.searchField);
			int i = selectedTab;
			selectedTab = -1;
			this.setSelectedTab(ItemGroup.itemGroups[i]);
			this.listener = new CreativeInventoryListener(this.client);
			this.client.player.playerScreenHandler.addListener(this.listener);
		} else {
			this.client.setScreen(new SurvivalInventoryScreen(this.client.player));
		}
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.searchField.getText();
		this.init(client, width, height);
		this.searchField.setText(string);
		if (!this.searchField.getText().isEmpty()) {
			this.search();
		}
	}

	@Override
	public void removed() {
		super.removed();
		if (this.client.player != null && this.client.player.inventory != null) {
			this.client.player.playerScreenHandler.removeListener(this.listener);
		}

		this.client.field_19946.method_18191(false);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_20401) {
			return false;
		} else if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			return false;
		} else {
			String string = this.searchField.getText();
			if (this.searchField.charTyped(c, i)) {
				if (!Objects.equals(string, this.searchField.getText())) {
					this.search();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		this.field_20401 = false;
		if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			if (this.client.options.chatKey.method_18166(i, j)) {
				this.field_20401 = true;
				this.setSelectedTab(ItemGroup.SEARCH);
				return true;
			} else {
				return super.keyPressed(i, j, k);
			}
		} else {
			boolean bl = !this.method_18732(this.focusedSlot) || this.focusedSlot != null && this.focusedSlot.hasStack();
			if (bl && this.method_4261(i, j)) {
				this.field_20401 = true;
				return true;
			} else {
				String string = this.searchField.getText();
				if (this.searchField.keyPressed(i, j, k)) {
					if (!Objects.equals(string, this.searchField.getText())) {
						this.search();
					}

					return true;
				} else {
					return super.keyPressed(i, j, k);
				}
			}
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k) {
		this.field_20401 = false;
		return super.keyReleased(i, j, k);
	}

	private void search() {
		CreativeInventoryScreen.CreativeScreenHandler creativeScreenHandler = (CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler;
		creativeScreenHandler.field_15251.clear();
		if (this.searchField.getText().isEmpty()) {
			for (Item item : Registry.ITEM) {
				item.appendToItemGroup(ItemGroup.SEARCH, creativeScreenHandler.field_15251);
			}
		} else {
			creativeScreenHandler.field_15251.addAll(this.client.method_14460(class_3306.field_16177).method_14707(this.searchField.getText().toLowerCase(Locale.ROOT)));
		}

		this.scrollPosition = 0.0F;
		creativeScreenHandler.scrollItems(0.0F);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		ItemGroup itemGroup = ItemGroup.itemGroups[selectedTab];
		if (itemGroup.hasTooltip()) {
			GlStateManager.disableBlend();
			this.textRenderer.method_18355(I18n.translate(itemGroup.getTranslationKey()), 8.0F, 6.0F, 4210752);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			double f = d - (double)this.x;
			double g = e - (double)this.y;

			for (ItemGroup itemGroup : ItemGroup.itemGroups) {
				if (this.method_18733(itemGroup, f, g)) {
					return true;
				}
			}

			if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.method_18731(d, e)) {
				this.hasScrollbar = this.hasScrollbar();
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0) {
			double f = d - (double)this.x;
			double g = e - (double)this.y;
			this.hasScrollbar = false;

			for (ItemGroup itemGroup : ItemGroup.itemGroups) {
				if (this.method_18733(itemGroup, f, g)) {
					this.setSelectedTab(itemGroup);
					return true;
				}
			}
		}

		return super.mouseReleased(d, e, i);
	}

	private boolean hasScrollbar() {
		return selectedTab != ItemGroup.INVENTORY.getIndex()
			&& ItemGroup.itemGroups[selectedTab].hasScrollbar()
			&& ((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).isFull();
	}

	private void setSelectedTab(ItemGroup group) {
		int i = selectedTab;
		selectedTab = group.getIndex();
		CreativeInventoryScreen.CreativeScreenHandler creativeScreenHandler = (CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler;
		this.cursorDragSlots.clear();
		creativeScreenHandler.field_15251.clear();
		if (group == ItemGroup.field_15657) {
			class_3251 lv = this.client.method_18221();

			for (int j = 0; j < 9; j++) {
				class_3297 lv2 = lv.method_14450(j);
				if (lv2.isEmpty()) {
					for (int k = 0; k < 9; k++) {
						if (k == j) {
							ItemStack itemStack = new ItemStack(Items.PAPER);
							itemStack.getOrCreateNbtCompound("CustomCreativeLock");
							String string = this.client.options.hotbarKeys[j].method_18174();
							String string2 = this.client.options.field_15881.method_18174();
							itemStack.setCustomName(new TranslatableText("inventory.hotbarInfo", string2, string));
							creativeScreenHandler.field_15251.add(itemStack);
						} else {
							creativeScreenHandler.field_15251.add(ItemStack.EMPTY);
						}
					}
				} else {
					creativeScreenHandler.field_15251.addAll(lv2);
				}
			}
		} else if (group != ItemGroup.SEARCH) {
			group.method_13646(creativeScreenHandler.field_15251);
		}

		if (group == ItemGroup.INVENTORY) {
			ScreenHandler screenHandler = this.client.player.playerScreenHandler;
			if (this.slots == null) {
				this.slots = creativeScreenHandler.slots;
			}

			creativeScreenHandler.slots = Lists.newArrayList();

			for (int l = 0; l < screenHandler.slots.size(); l++) {
				Slot slot = new CreativeInventoryScreen.CreativeInventorySlot((Slot)screenHandler.slots.get(l), l);
				creativeScreenHandler.slots.add(slot);
				if (l >= 5 && l < 9) {
					int m = l - 5;
					int n = m / 2;
					int o = m % 2;
					slot.x = 54 + n * 54;
					slot.y = 6 + o * 27;
				} else if (l >= 0 && l < 5) {
					slot.x = -2000;
					slot.y = -2000;
				} else if (l == 45) {
					slot.x = 35;
					slot.y = 20;
				} else if (l < screenHandler.slots.size()) {
					int p = l - 9;
					int q = p % 9;
					int r = p / 9;
					slot.x = 9 + q * 18;
					if (l >= 36) {
						slot.y = 112;
					} else {
						slot.y = 54 + r * 18;
					}
				}
			}

			this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
			creativeScreenHandler.slots.add(this.deleteItemSlot);
		} else if (i == ItemGroup.INVENTORY.getIndex()) {
			creativeScreenHandler.slots = this.slots;
			this.slots = null;
		}

		if (this.searchField != null) {
			if (group == ItemGroup.SEARCH) {
				this.searchField.setVisible(true);
				this.searchField.setFocusUnlocked(false);
				this.searchField.setFocused(true);
				if (i != group.getIndex()) {
					this.searchField.setText("");
				}

				this.search();
			} else {
				this.searchField.setVisible(false);
				this.searchField.setFocusUnlocked(true);
				this.searchField.setFocused(false);
				this.searchField.setText("");
			}
		}

		this.scrollPosition = 0.0F;
		creativeScreenHandler.scrollItems(0.0F);
	}

	@Override
	public boolean mouseScrolled(double d) {
		if (!this.hasScrollbar()) {
			return false;
		} else {
			int i = (((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).field_15251.size() + 9 - 1) / 9 - 5;
			this.scrollPosition = (float)((double)this.scrollPosition - d / (double)i);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).scrollItems(this.scrollPosition);
			return true;
		}
	}

	@Override
	protected boolean method_14549(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
		this.field_20402 = bl && !this.method_18733(ItemGroup.itemGroups[selectedTab], d, e);
		return this.field_20402;
	}

	protected boolean method_18731(double d, double e) {
		int i = this.x;
		int j = this.y;
		int k = i + 175;
		int l = j + 18;
		int m = k + 14;
		int n = l + 112;
		return d >= (double)k && e >= (double)l && d < (double)m && e < (double)n;
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (this.hasScrollbar) {
			int j = this.y + 18;
			int k = j + 112;
			this.scrollPosition = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).scrollItems(this.scrollPosition);
			return true;
		} else {
			return super.mouseDragged(d, e, i, f, g);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		super.render(mouseX, mouseY, tickDelta);

		for (ItemGroup itemGroup : ItemGroup.itemGroups) {
			if (this.renderTabTooltipIfHovered(itemGroup, mouseX, mouseY)) {
				break;
			}
		}

		if (this.deleteItemSlot != null
			&& selectedTab == ItemGroup.INVENTORY.getIndex()
			&& this.method_1134(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, (double)mouseX, (double)mouseY)) {
			this.renderTooltip(I18n.translate("inventory.binSlot"), mouseX, mouseY);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		this.renderTooltip(mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(ItemStack stack, int x, int y) {
		if (selectedTab == ItemGroup.SEARCH.getIndex()) {
			List<Text> list = stack.getTooltip(
				this.client.player, this.client.options.field_19992 ? TooltipContext.TooltipType.ADVANCED : TooltipContext.TooltipType.NORMAL
			);
			List<String> list2 = Lists.newArrayListWithCapacity(list.size());

			for (Text text : list) {
				list2.add(text.asFormattedString());
			}

			ItemGroup itemGroup = stack.getItem().getItemGroup();
			if (itemGroup == null && stack.getItem() == Items.ENCHANTED_BOOK) {
				Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
				if (map.size() == 1) {
					Enchantment enchantment = (Enchantment)map.keySet().iterator().next();

					for (ItemGroup itemGroup2 : ItemGroup.itemGroups) {
						if (itemGroup2.containsEnchantments(enchantment.target)) {
							itemGroup = itemGroup2;
							break;
						}
					}
				}
			}

			if (itemGroup != null) {
				list2.add(1, "" + Formatting.BOLD + Formatting.BLUE + I18n.translate(itemGroup.getTranslationKey()));
			}

			for (int i = 0; i < list2.size(); i++) {
				if (i == 0) {
					list2.set(i, stack.getRarity().formatting + (String)list2.get(i));
				} else {
					list2.set(i, Formatting.GRAY + (String)list2.get(i));
				}
			}

			this.renderTooltip(list2, x, y);
		} else {
			super.renderTooltip(stack, x, y);
		}
	}

	@Override
	protected void drawBackground(float delta, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		DiffuseLighting.enable();
		ItemGroup itemGroup = ItemGroup.itemGroups[selectedTab];

		for (ItemGroup itemGroup2 : ItemGroup.itemGroups) {
			this.client.getTextureManager().bindTexture(ITEM_GROUPS);
			if (itemGroup2.getIndex() != selectedTab) {
				this.renderTabIcon(itemGroup2);
			}
		}

		this.client.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
		this.drawTexture(this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
		this.searchField.method_18385(mouseX, mouseY, delta);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.x + 175;
		int j = this.y + 18;
		int k = j + 112;
		this.client.getTextureManager().bindTexture(ITEM_GROUPS);
		if (itemGroup.hasScrollbar()) {
			this.drawTexture(i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
		}

		this.renderTabIcon(itemGroup);
		if (itemGroup == ItemGroup.INVENTORY) {
			SurvivalInventoryScreen.renderEntity(this.x + 88, this.y + 45, 20, (float)(this.x + 88 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.client.player);
		}
	}

	protected boolean method_18733(ItemGroup itemGroup, double d, double e) {
		int i = itemGroup.getColumn();
		int j = 28 * i;
		int k = 0;
		if (itemGroup.method_14220()) {
			j = this.backgroundWidth - 28 * (6 - i) + 2;
		} else if (i > 0) {
			j += i;
		}

		if (itemGroup.isTopRow()) {
			k -= 32;
		} else {
			k += this.backgroundHeight;
		}

		return d >= (double)j && d <= (double)(j + 28) && e >= (double)k && e <= (double)(k + 32);
	}

	protected boolean renderTabTooltipIfHovered(ItemGroup group, int mouseX, int mouseY) {
		int i = group.getColumn();
		int j = 28 * i;
		int k = 0;
		if (group.method_14220()) {
			j = this.backgroundWidth - 28 * (6 - i) + 2;
		} else if (i > 0) {
			j += i;
		}

		if (group.isTopRow()) {
			k -= 32;
		} else {
			k += this.backgroundHeight;
		}

		if (this.method_1134(j + 3, k + 3, 23, 27, (double)mouseX, (double)mouseY)) {
			this.renderTooltip(I18n.translate(group.getTranslationKey()), mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	protected void renderTabIcon(ItemGroup group) {
		boolean bl = group.getIndex() == selectedTab;
		boolean bl2 = group.isTopRow();
		int i = group.getColumn();
		int j = i * 28;
		int k = 0;
		int l = this.x + 28 * i;
		int m = this.y;
		int n = 32;
		if (bl) {
			k += 32;
		}

		if (group.method_14220()) {
			l = this.x + this.backgroundWidth - 28 * (6 - i);
		} else if (i > 0) {
			l += i;
		}

		if (bl2) {
			m -= 28;
		} else {
			k += 64;
			m += this.backgroundHeight - 4;
		}

		GlStateManager.disableLighting();
		this.drawTexture(l, m, j, k, 28, 32);
		this.zOffset = 100.0F;
		this.field_20308.field_20932 = 100.0F;
		l += 6;
		m += 8 + (bl2 ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		ItemStack itemStack = group.getIcon();
		this.field_20308.method_19397(itemStack, l, m);
		this.field_20308.method_19383(this.textRenderer, itemStack, l, m);
		GlStateManager.disableLighting();
		this.field_20308.field_20932 = 0.0F;
		this.zOffset = 0.0F;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public static void method_14550(MinecraftClient minecraftClient, int i, boolean bl, boolean bl2) {
		ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
		class_3251 lv = minecraftClient.method_18221();
		class_3297 lv2 = lv.method_14450(i);
		if (bl) {
			for (int j = 0; j < PlayerInventory.getHotbarSize(); j++) {
				ItemStack itemStack = ((ItemStack)lv2.get(j)).copy();
				clientPlayerEntity.inventory.setInvStack(j, itemStack);
				minecraftClient.interactionManager.clickCreativeStack(itemStack, 36 + j);
			}

			clientPlayerEntity.playerScreenHandler.sendContentUpdates();
		} else if (bl2) {
			for (int k = 0; k < PlayerInventory.getHotbarSize(); k++) {
				lv2.set(k, clientPlayerEntity.inventory.getInvStack(k).copy());
			}

			String string = minecraftClient.options.hotbarKeys[i].method_18174();
			String string2 = minecraftClient.options.field_15882.method_18174();
			minecraftClient.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", string2, string), false);
			lv.method_14451();
		}
	}

	class CreativeInventorySlot extends Slot {
		private final Slot slot;

		public CreativeInventorySlot(Slot slot, int i) {
			super(slot.inventory, i, 0, 0);
			this.slot = slot;
		}

		@Override
		public ItemStack method_3298(PlayerEntity playerEntity, ItemStack itemStack) {
			this.slot.method_3298(playerEntity, itemStack);
			return itemStack;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return this.slot.canInsert(stack);
		}

		@Override
		public ItemStack getStack() {
			return this.slot.getStack();
		}

		@Override
		public boolean hasStack() {
			return this.slot.hasStack();
		}

		@Override
		public void setStack(ItemStack stack) {
			this.slot.setStack(stack);
		}

		@Override
		public void markDirty() {
			this.slot.markDirty();
		}

		@Override
		public int getMaxStackAmount() {
			return this.slot.getMaxStackAmount();
		}

		@Override
		public int getMaxStackAmount(ItemStack stack) {
			return this.slot.getMaxStackAmount(stack);
		}

		@Nullable
		@Override
		public String getBackgroundSprite() {
			return this.slot.getBackgroundSprite();
		}

		@Override
		public ItemStack takeStack(int amount) {
			return this.slot.takeStack(amount);
		}

		@Override
		public boolean equals(Inventory inventory, int slot) {
			return this.slot.equals(inventory, slot);
		}

		@Override
		public boolean doDrawHoveringEffect() {
			return this.slot.doDrawHoveringEffect();
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return this.slot.canTakeItems(playerEntity);
		}
	}

	public static class CreativeScreenHandler extends ScreenHandler {
		public DefaultedList<ItemStack> field_15251 = DefaultedList.of();

		public CreativeScreenHandler(PlayerEntity playerEntity) {
			PlayerInventory playerInventory = playerEntity.inventory;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 9; j++) {
					this.addSlot(new CreativeInventoryScreen.class_3277(CreativeInventoryScreen.inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
				}
			}

			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k, 9 + k * 18, 112));
			}

			this.scrollItems(0.0F);
		}

		@Override
		public boolean canUse(PlayerEntity player) {
			return true;
		}

		public void scrollItems(float position) {
			int i = (this.field_15251.size() + 9 - 1) / 9 - 5;
			int j = (int)((double)(position * (float)i) + 0.5);
			if (j < 0) {
				j = 0;
			}

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					int m = l + (k + j) * 9;
					if (m >= 0 && m < this.field_15251.size()) {
						CreativeInventoryScreen.inventory.setInvStack(l + k * 9, this.field_15251.get(m));
					} else {
						CreativeInventoryScreen.inventory.setInvStack(l + k * 9, ItemStack.EMPTY);
					}
				}
			}
		}

		public boolean isFull() {
			return this.field_15251.size() > 45;
		}

		@Override
		public ItemStack transferSlot(PlayerEntity player, int invSlot) {
			if (invSlot >= this.slots.size() - 9 && invSlot < this.slots.size()) {
				Slot slot = (Slot)this.slots.get(invSlot);
				if (slot != null && slot.hasStack()) {
					slot.setStack(ItemStack.EMPTY);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
			return slot.y > 90;
		}

		@Override
		public boolean canInsertIntoSlot(Slot slot) {
			return slot.inventory instanceof PlayerInventory || slot.y > 90 && slot.x <= 162;
		}
	}

	static class class_3277 extends Slot {
		public class_3277(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canTakeItems(PlayerEntity playerEntity) {
			return super.canTakeItems(playerEntity) && this.hasStack() ? this.getStack().getNbtCompound("CustomCreativeLock") == null : !this.hasStack();
		}
	}
}
