package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.CreativeInventoryListener;
import net.minecraft.client.gui.screen.AchievementsScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class CreativeInventoryScreen extends InventoryScreen {
	private static final Identifier ITEM_GROUPS = new Identifier("textures/gui/container/creative_inventory/tabs.png");
	private static SimpleInventory inventory = new SimpleInventory("tmp", true, 45);
	private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
	private float scrollPosition;
	private boolean hasScrollbar;
	private boolean clicking;
	private TextFieldWidget searchField;
	private List<Slot> slots;
	private Slot deleteItemSlot;
	private boolean scrolling;
	private CreativeInventoryListener listener;

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

		this.applyStatusEffectOffset();
	}

	@Override
	protected void onMouseClick(Slot slot, int invSlot, int button, int slotAction) {
		this.scrolling = true;
		boolean bl = slotAction == 1;
		slotAction = invSlot == -999 && slotAction == 0 ? 4 : slotAction;
		if (slot == null && selectedTab != ItemGroup.INVENTORY.getIndex() && slotAction != 5) {
			PlayerInventory playerInventory2 = this.client.player.inventory;
			if (playerInventory2.getCursorStack() != null) {
				if (button == 0) {
					this.client.player.dropItem(playerInventory2.getCursorStack(), true);
					this.client.interactionManager.dropCreativeStack(playerInventory2.getCursorStack());
					playerInventory2.setCursorStack(null);
				}

				if (button == 1) {
					ItemStack itemStack8 = playerInventory2.getCursorStack().split(1);
					this.client.player.dropItem(itemStack8, true);
					this.client.interactionManager.dropCreativeStack(itemStack8);
					if (playerInventory2.getCursorStack().count == 0) {
						playerInventory2.setCursorStack(null);
					}
				}
			}
		} else if (slot == this.deleteItemSlot && bl) {
			for (int i = 0; i < this.client.player.playerScreenHandler.getStacks().size(); i++) {
				this.client.interactionManager.clickCreativeStack(null, i);
			}
		} else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
			if (slot == this.deleteItemSlot) {
				this.client.player.inventory.setCursorStack(null);
			} else if (slotAction == 4 && slot != null && slot.hasStack()) {
				ItemStack itemStack = slot.takeStack(button == 0 ? 1 : slot.getStack().getMaxCount());
				this.client.player.dropItem(itemStack, true);
				this.client.interactionManager.dropCreativeStack(itemStack);
			} else if (slotAction == 4 && this.client.player.inventory.getCursorStack() != null) {
				this.client.player.dropItem(this.client.player.inventory.getCursorStack(), true);
				this.client.interactionManager.dropCreativeStack(this.client.player.inventory.getCursorStack());
				this.client.player.inventory.setCursorStack(null);
			} else {
				this.client
					.player
					.playerScreenHandler
					.onSlotClick(slot == null ? invSlot : ((CreativeInventoryScreen.CreativeInventorySlot)slot).slot.id, button, slotAction, this.client.player);
				this.client.player.playerScreenHandler.sendContentUpdates();
			}
		} else if (slotAction != 5 && slot.inventory == inventory) {
			PlayerInventory playerInventory = this.client.player.inventory;
			ItemStack itemStack2 = playerInventory.getCursorStack();
			ItemStack itemStack3 = slot.getStack();
			if (slotAction == 2) {
				if (itemStack3 != null && button >= 0 && button < 9) {
					ItemStack itemStack4 = itemStack3.copy();
					itemStack4.count = itemStack4.getMaxCount();
					this.client.player.inventory.setInvStack(button, itemStack4);
					this.client.player.playerScreenHandler.sendContentUpdates();
				}

				return;
			}

			if (slotAction == 3) {
				if (playerInventory.getCursorStack() == null && slot.hasStack()) {
					ItemStack itemStack5 = slot.getStack().copy();
					itemStack5.count = itemStack5.getMaxCount();
					playerInventory.setCursorStack(itemStack5);
				}

				return;
			}

			if (slotAction == 4) {
				if (itemStack3 != null) {
					ItemStack itemStack6 = itemStack3.copy();
					itemStack6.count = button == 0 ? 1 : itemStack6.getMaxCount();
					this.client.player.dropItem(itemStack6, true);
					this.client.interactionManager.dropCreativeStack(itemStack6);
				}

				return;
			}

			if (itemStack2 != null && itemStack3 != null && itemStack2.equalsIgnoreNbt(itemStack3)) {
				if (button == 0) {
					if (bl) {
						itemStack2.count = itemStack2.getMaxCount();
					} else if (itemStack2.count < itemStack2.getMaxCount()) {
						itemStack2.count++;
					}
				} else if (itemStack2.count <= 1) {
					playerInventory.setCursorStack(null);
				} else {
					itemStack2.count--;
				}
			} else if (itemStack3 != null && itemStack2 == null) {
				playerInventory.setCursorStack(ItemStack.copyOf(itemStack3));
				itemStack2 = playerInventory.getCursorStack();
				if (bl) {
					itemStack2.count = itemStack2.getMaxCount();
				}
			} else {
				playerInventory.setCursorStack(null);
			}
		} else {
			this.screenHandler.onSlotClick(slot == null ? invSlot : slot.id, button, slotAction, this.client.player);
			if (ScreenHandler.unpackButtonId(button) == 2) {
				for (int j = 0; j < 9; j++) {
					this.client.interactionManager.clickCreativeStack(this.screenHandler.getSlot(45 + j).getStack(), 36 + j);
				}
			} else if (slot != null) {
				ItemStack itemStack7 = this.screenHandler.getSlot(slot.id).getStack();
				this.client.interactionManager.clickCreativeStack(itemStack7, slot.id - this.screenHandler.slots.size() + 9 + 36);
			}
		}
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
	public void init() {
		if (this.client.interactionManager.hasCreativeInventory()) {
			super.init();
			this.buttons.clear();
			Keyboard.enableRepeatEvents(true);
			this.searchField = new TextFieldWidget(0, this.textRenderer, this.x + 82, this.y + 6, 89, this.textRenderer.fontHeight);
			this.searchField.setMaxLength(15);
			this.searchField.setHasBorder(false);
			this.searchField.setVisible(false);
			this.searchField.setEditableColor(16777215);
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
	public void removed() {
		super.removed();
		if (this.client.player != null && this.client.player.inventory != null) {
			this.client.player.playerScreenHandler.removeListener(this.listener);
		}

		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (selectedTab != ItemGroup.SEARCH.getIndex()) {
			if (GameOptions.isPressed(this.client.options.chatKey)) {
				this.setSelectedTab(ItemGroup.SEARCH);
			} else {
				super.keyPressed(id, code);
			}
		} else {
			if (this.scrolling) {
				this.scrolling = false;
				this.searchField.setText("");
			}

			if (!this.handleHotbarKeyPressed(code)) {
				if (this.searchField.keyPressed(id, code)) {
					this.search();
				} else {
					super.keyPressed(id, code);
				}
			}
		}
	}

	private void search() {
		CreativeInventoryScreen.CreativeScreenHandler creativeScreenHandler = (CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler;
		creativeScreenHandler.itemList.clear();

		for (Item item : Item.REGISTRY) {
			if (item != null && item.getItemGroup() != null) {
				item.appendItemStacks(item, null, creativeScreenHandler.itemList);
			}
		}

		for (Enchantment enchantment : Enchantment.ALL_ENCHANTMENTS) {
			if (enchantment != null && enchantment.target != null) {
				Items.ENCHANTED_BOOK.getEnchantments(enchantment, creativeScreenHandler.itemList);
			}
		}

		Iterator<ItemStack> iterator2 = creativeScreenHandler.itemList.iterator();
		String string = this.searchField.getText().toLowerCase();

		while (iterator2.hasNext()) {
			ItemStack itemStack = (ItemStack)iterator2.next();
			boolean bl = false;

			for (String string2 : itemStack.getTooltip(this.client.player, this.client.options.advancedItemTooltips)) {
				if (Formatting.strip(string2).toLowerCase().contains(string)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				iterator2.remove();
			}
		}

		this.scrollPosition = 0.0F;
		creativeScreenHandler.scrollItems(0.0F);
	}

	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		ItemGroup itemGroup = ItemGroup.itemGroups[selectedTab];
		if (itemGroup.hasTooltip()) {
			GlStateManager.disableBlend();
			this.textRenderer.draw(I18n.translate(itemGroup.getTranslationKey()), 8, 6, 4210752);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			int i = mouseX - this.x;
			int j = mouseY - this.y;

			for (ItemGroup itemGroup : ItemGroup.itemGroups) {
				if (this.isClickInTab(itemGroup, i, j)) {
					return;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		if (button == 0) {
			int i = mouseX - this.x;
			int j = mouseY - this.y;

			for (ItemGroup itemGroup : ItemGroup.itemGroups) {
				if (this.isClickInTab(itemGroup, i, j)) {
					this.setSelectedTab(itemGroup);
					return;
				}
			}
		}

		super.mouseReleased(mouseX, mouseY, button);
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
		creativeScreenHandler.itemList.clear();
		group.showItems(creativeScreenHandler.itemList);
		if (group == ItemGroup.INVENTORY) {
			ScreenHandler screenHandler = this.client.player.playerScreenHandler;
			if (this.slots == null) {
				this.slots = creativeScreenHandler.slots;
			}

			creativeScreenHandler.slots = Lists.newArrayList();

			for (int j = 0; j < screenHandler.slots.size(); j++) {
				Slot slot = new CreativeInventoryScreen.CreativeInventorySlot((Slot)screenHandler.slots.get(j), j);
				creativeScreenHandler.slots.add(slot);
				if (j >= 5 && j < 9) {
					int k = j - 5;
					int l = k / 2;
					int m = k % 2;
					slot.x = 9 + l * 54;
					slot.y = 6 + m * 27;
				} else if (j >= 0 && j < 5) {
					slot.y = -2000;
					slot.x = -2000;
				} else if (j < screenHandler.slots.size()) {
					int n = j - 9;
					int o = n % 9;
					int p = n / 9;
					slot.x = 9 + o * 18;
					if (j >= 36) {
						slot.y = 112;
					} else {
						slot.y = 54 + p * 18;
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
				this.searchField.setText("");
				this.search();
			} else {
				this.searchField.setVisible(false);
				this.searchField.setFocusUnlocked(true);
				this.searchField.setFocused(false);
			}
		}

		this.scrollPosition = 0.0F;
		creativeScreenHandler.scrollItems(0.0F);
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		int i = Mouse.getEventDWheel();
		if (i != 0 && this.hasScrollbar()) {
			int j = ((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).itemList.size() / 9 - 5;
			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}

			this.scrollPosition = (float)((double)this.scrollPosition - (double)i / (double)j);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).scrollItems(this.scrollPosition);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		boolean bl = Mouse.isButtonDown(0);
		int i = this.x;
		int j = this.y;
		int k = i + 175;
		int l = j + 18;
		int m = k + 14;
		int n = l + 112;
		if (!this.clicking && bl && mouseX >= k && mouseY >= l && mouseX < m && mouseY < n) {
			this.hasScrollbar = this.hasScrollbar();
		}

		if (!bl) {
			this.hasScrollbar = false;
		}

		this.clicking = bl;
		if (this.hasScrollbar) {
			this.scrollPosition = ((float)(mouseY - l) - 7.5F) / ((float)(n - l) - 15.0F);
			this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
			((CreativeInventoryScreen.CreativeScreenHandler)this.screenHandler).scrollItems(this.scrollPosition);
		}

		super.render(mouseX, mouseY, tickDelta);

		for (ItemGroup itemGroup : ItemGroup.itemGroups) {
			if (this.renderTabTooltipIfHovered(itemGroup, mouseX, mouseY)) {
				break;
			}
		}

		if (this.deleteItemSlot != null
			&& selectedTab == ItemGroup.INVENTORY.getIndex()
			&& this.isPointWithinBounds(this.deleteItemSlot.x, this.deleteItemSlot.y, 16, 16, mouseX, mouseY)) {
			this.renderTooltip(I18n.translate("inventory.binSlot"), mouseX, mouseY);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
	}

	@Override
	protected void renderTooltip(ItemStack stack, int x, int y) {
		if (selectedTab == ItemGroup.SEARCH.getIndex()) {
			List<String> list = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips);
			ItemGroup itemGroup = stack.getItem().getItemGroup();
			if (itemGroup == null && stack.getItem() == Items.ENCHANTED_BOOK) {
				Map<Integer, Integer> map = EnchantmentHelper.get(stack);
				if (map.size() == 1) {
					Enchantment enchantment = Enchantment.byRawId((Integer)map.keySet().iterator().next());

					for (ItemGroup itemGroup2 : ItemGroup.itemGroups) {
						if (itemGroup2.containsEnchantments(enchantment.target)) {
							itemGroup = itemGroup2;
							break;
						}
					}
				}
			}

			if (itemGroup != null) {
				list.add(1, "" + Formatting.BOLD + Formatting.BLUE + I18n.translate(itemGroup.getTranslationKey()));
			}

			for (int k = 0; k < list.size(); k++) {
				if (k == 0) {
					list.set(k, stack.getRarity().formatting + (String)list.get(k));
				} else {
					list.set(k, Formatting.GRAY + (String)list.get(k));
				}
			}

			this.renderTooltip(list, x, y);
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
		this.searchField.render();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.x + 175;
		int l = this.y + 18;
		int m = l + 112;
		this.client.getTextureManager().bindTexture(ITEM_GROUPS);
		if (itemGroup.hasScrollbar()) {
			this.drawTexture(k, l + (int)((float)(m - l - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
		}

		this.renderTabIcon(itemGroup);
		if (itemGroup == ItemGroup.INVENTORY) {
			SurvivalInventoryScreen.renderEntity(this.x + 43, this.y + 45, 20, (float)(this.x + 43 - mouseX), (float)(this.y + 45 - 30 - mouseY), this.client.player);
		}
	}

	protected boolean isClickInTab(ItemGroup group, int mouseX, int mouseY) {
		int i = group.getColumn();
		int j = 28 * i;
		int k = 0;
		if (i == 5) {
			j = this.backgroundWidth - 28 + 2;
		} else if (i > 0) {
			j += i;
		}

		if (group.isTopRow()) {
			k -= 32;
		} else {
			k += this.backgroundHeight;
		}

		return mouseX >= j && mouseX <= j + 28 && mouseY >= k && mouseY <= k + 32;
	}

	protected boolean renderTabTooltipIfHovered(ItemGroup group, int mouseX, int mouseY) {
		int i = group.getColumn();
		int j = 28 * i;
		int k = 0;
		if (i == 5) {
			j = this.backgroundWidth - 28 + 2;
		} else if (i > 0) {
			j += i;
		}

		if (group.isTopRow()) {
			k -= 32;
		} else {
			k += this.backgroundHeight;
		}

		if (this.isPointWithinBounds(j + 3, k + 3, 23, 27, mouseX, mouseY)) {
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

		if (i == 5) {
			l = this.x + this.backgroundWidth - 28;
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
		this.drawTexture(l, m, j, k, 28, n);
		this.zOffset = 100.0F;
		this.itemRenderer.zOffset = 100.0F;
		l += 6;
		m += 8 + (bl2 ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		ItemStack itemStack = group.getIcon();
		this.itemRenderer.renderInGuiWithOverrides(itemStack, l, m);
		this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, l, m);
		GlStateManager.disableLighting();
		this.itemRenderer.zOffset = 0.0F;
		this.zOffset = 0.0F;
	}

	@Override
	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 0) {
			this.client.setScreen(new AchievementsScreen(this, this.client.player.getStatHandler()));
		}

		if (button.id == 1) {
			this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
		}
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	class CreativeInventorySlot extends Slot {
		private final Slot slot;

		public CreativeInventorySlot(Slot slot, int i) {
			super(slot.inventory, i, 0, 0);
			this.slot = slot;
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {
			this.slot.onTakeItem(player, stack);
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
	}

	static class CreativeScreenHandler extends ScreenHandler {
		public List<ItemStack> itemList = Lists.newArrayList();

		public CreativeScreenHandler(PlayerEntity playerEntity) {
			PlayerInventory playerInventory = playerEntity.inventory;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 9; j++) {
					this.addSlot(new Slot(CreativeInventoryScreen.inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
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
			int i = (this.itemList.size() + 9 - 1) / 9 - 5;
			int j = (int)((double)(position * (float)i) + 0.5);
			if (j < 0) {
				j = 0;
			}

			for (int k = 0; k < 5; k++) {
				for (int l = 0; l < 9; l++) {
					int m = l + (k + j) * 9;
					if (m >= 0 && m < this.itemList.size()) {
						CreativeInventoryScreen.inventory.setInvStack(l + k * 9, (ItemStack)this.itemList.get(m));
					} else {
						CreativeInventoryScreen.inventory.setInvStack(l + k * 9, null);
					}
				}
			}
		}

		public boolean isFull() {
			return this.itemList.size() > 45;
		}

		@Override
		protected void onSlotClick(int slotId, int clickData, boolean bl, PlayerEntity player) {
		}

		@Override
		public ItemStack transferSlot(PlayerEntity player, int invSlot) {
			if (invSlot >= this.slots.size() - 9 && invSlot < this.slots.size()) {
				Slot slot = (Slot)this.slots.get(invSlot);
				if (slot != null && slot.hasStack()) {
					slot.setStack(null);
				}
			}

			return null;
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
}
