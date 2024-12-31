package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.class_3175;
import net.minecraft.class_3257;
import net.minecraft.class_3278;
import net.minecraft.class_3283;
import net.minecraft.class_3284;
import net.minecraft.class_3286;
import net.minecraft.class_3287;
import net.minecraft.class_3306;
import net.minecraft.class_3320;
import net.minecraft.class_3355;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.network.packet.c2s.play.CraftingBlockData;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipeType;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Keyboard;

public class RecipeBookScreen extends DrawableHelper implements class_3287 {
	protected static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private int field_16043;
	private int field_16044;
	private int field_16045;
	private final class_3278 field_16046 = new class_3278();
	private final List<class_3284> field_16047 = Lists.newArrayList(
		new class_3284[]{
			new class_3284(0, ItemGroup.SEARCH),
			new class_3284(0, ItemGroup.TOOLS),
			new class_3284(0, ItemGroup.BUILDING_BLOCKS),
			new class_3284(0, ItemGroup.MISC),
			new class_3284(0, ItemGroup.REDSTONE)
		}
	);
	private class_3284 field_16048;
	private class_3257 field_16049;
	private CraftingInventory field_16050;
	private MinecraftClient client;
	private TextFieldWidget field_16052;
	private String field_16053 = "";
	private class_3355 field_16054;
	private final class_3283 field_16055 = new class_3283();
	private class_3175 field_16056 = new class_3175();
	private int field_16057;

	public void method_14577(int i, int j, MinecraftClient client, boolean bl, CraftingInventory craftingInventory) {
		this.client = client;
		this.field_16044 = i;
		this.field_16045 = j;
		this.field_16050 = craftingInventory;
		this.field_16054 = client.player.method_14675();
		this.field_16057 = client.player.inventory.method_14153();
		this.field_16048 = (class_3284)this.field_16047.get(0);
		this.field_16048.method_14478(true);
		if (this.method_14590()) {
			this.method_14586(bl, craftingInventory);
		}

		Keyboard.enableRepeatEvents(true);
	}

	public void method_14586(boolean bl, CraftingInventory craftingInventory) {
		this.field_16043 = bl ? 0 : 86;
		int i = (this.field_16044 - 147) / 2 - this.field_16043;
		int j = (this.field_16045 - 166) / 2;
		this.field_16056.method_14166();
		this.client.player.inventory.method_14148(this.field_16056, false);
		craftingInventory.method_14206(this.field_16056);
		this.field_16052 = new TextFieldWidget(0, this.client.textRenderer, i + 25, j + 14, 80, this.client.textRenderer.fontHeight + 5);
		this.field_16052.setMaxLength(50);
		this.field_16052.setHasBorder(false);
		this.field_16052.setVisible(true);
		this.field_16052.setEditableColor(16777215);
		this.field_16055.method_14606(this.client, i, j);
		this.field_16055.method_14607(this);
		this.field_16049 = new class_3257(0, i + 110, j + 12, 26, 16, this.field_16054.method_14986());
		this.field_16049.method_14477(152, 41, 28, 18, TEXTURE);
		this.method_14589(false);
		this.method_14598();
	}

	public void method_14573() {
		Keyboard.enableRepeatEvents(false);
	}

	public int method_14585(boolean bl, int i, int j) {
		int k;
		if (this.method_14590() && !bl) {
			k = 177 + (i - j - 200) / 2;
		} else {
			k = (i - j) / 2;
		}

		return k;
	}

	public void method_14587() {
		this.method_14584(!this.method_14590());
	}

	public boolean method_14590() {
		return this.field_16054.method_14982();
	}

	private void method_14584(boolean bl) {
		this.field_16054.method_14985(bl);
		if (!bl) {
			this.field_16055.method_14611();
		}

		this.method_14601();
	}

	public void method_14579(@Nullable Slot slot) {
		if (slot != null && slot.id <= 9) {
			this.field_16046.method_14551();
			if (this.method_14590()) {
				this.method_14599();
			}
		}
	}

	private void method_14589(boolean bl) {
		List<class_3286> list = (List<class_3286>)class_3320.field_16242.get(this.field_16048.method_14616());
		list.forEach(arg -> arg.method_14626(this.field_16056, this.field_16050.getWidth(), this.field_16050.getHeight(), this.field_16054));
		List<class_3286> list2 = Lists.newArrayList(list);
		list2.removeIf(arg -> !arg.method_14625());
		list2.removeIf(arg -> !arg.method_14633());
		String string = this.field_16052.getText();
		if (!string.isEmpty()) {
			ObjectSet<class_3286> objectSet = new ObjectLinkedOpenHashSet(this.client.method_14460(class_3306.field_16178).method_14707(string.toLowerCase(Locale.ROOT)));
			list2.removeIf(arg -> !objectSet.contains(arg));
		}

		if (this.field_16054.method_14986()) {
			list2.removeIf(arg -> !arg.method_14630());
		}

		this.field_16055.method_14609(list2, bl);
	}

	private void method_14598() {
		int i = (this.field_16044 - 147) / 2 - this.field_16043 - 30;
		int j = (this.field_16045 - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (class_3284 lv : this.field_16047) {
			ItemGroup itemGroup = lv.method_14616();
			if (itemGroup == ItemGroup.SEARCH) {
				lv.visible = true;
				lv.method_14480(i, j + 27 * l++);
			} else if (lv.method_14617()) {
				lv.method_14480(i, j + 27 * l++);
				lv.method_14614(this.client);
			}
		}
	}

	public void method_14594() {
		if (this.method_14590()) {
			if (this.field_16057 != this.client.player.inventory.method_14153()) {
				this.method_14599();
				this.field_16057 = this.client.player.inventory.method_14153();
			}
		}
	}

	private void method_14599() {
		this.field_16056.method_14166();
		this.client.player.inventory.method_14148(this.field_16056, false);
		this.field_16050.method_14206(this.field_16056);
		this.method_14589(false);
	}

	public void method_14575(int i, int j, float f) {
		if (this.method_14590()) {
			DiffuseLighting.enable();
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 100.0F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int k = (this.field_16044 - 147) / 2 - this.field_16043;
			int l = (this.field_16045 - 166) / 2;
			this.drawTexture(k, l, 1, 1, 147, 166);
			this.field_16052.render();
			DiffuseLighting.disable();

			for (class_3284 lv : this.field_16047) {
				lv.method_891(this.client, i, j, f);
			}

			this.field_16049.method_891(this.client, i, j, f);
			this.field_16055.method_14604(k, l, i, j, f);
			GlStateManager.popMatrix();
		}
	}

	public void method_14591(int i, int j, int k, int l) {
		if (this.method_14590()) {
			this.field_16055.method_14603(k, l);
			if (this.field_16049.isHovered()) {
				String string = I18n.translate(this.field_16049.method_14479() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
				if (this.client.currentScreen != null) {
					this.client.currentScreen.renderTooltip(string, k, l);
				}
			}

			this.method_14595(i, j, k, l);
		}
	}

	private void method_14595(int i, int j, int k, int l) {
		ItemStack itemStack = null;

		for (int m = 0; m < this.field_16046.method_14557(); m++) {
			class_3278.class_3279 lv = this.field_16046.method_14552(m);
			int n = lv.method_14559() + i;
			int o = lv.method_14560() + j;
			if (k >= n && l >= o && k < n + 16 && l < o + 16) {
				itemStack = lv.method_14561();
			}
		}

		if (itemStack != null && this.client.currentScreen != null) {
			this.client.currentScreen.renderTooltip(this.client.currentScreen.method_14502(itemStack), k, l);
		}
	}

	public void method_14578(int i, int j, boolean bl, float f) {
		this.field_16046.method_14555(this.client, i, j, bl, f);
	}

	public boolean method_14576(int i, int j, int k) {
		if (this.method_14590() && !this.client.player.isSpectator()) {
			if (this.field_16055.method_14605(i, j, k, (this.field_16044 - 147) / 2 - this.field_16043, (this.field_16045 - 166) / 2, 147, 166)) {
				RecipeType recipeType = this.field_16055.method_14602();
				class_3286 lv = this.field_16055.method_14610();
				if (recipeType != null && lv != null) {
					if (!lv.method_14627(recipeType) && this.field_16046.method_14558() == recipeType) {
						return false;
					}

					this.field_16046.method_14551();
					this.client.interactionManager.method_14674(this.client.player.openScreenHandler.syncId, recipeType, Screen.hasShiftDown(), this.client.player);
					if (!this.method_14600() && k == 0) {
						this.method_14584(false);
					}
				}

				return true;
			} else if (k != 0) {
				return false;
			} else if (this.field_16052.method_920(i, j, k)) {
				return true;
			} else if (this.field_16049.isMouseOver(this.client, i, j)) {
				boolean bl = !this.field_16054.method_14986();
				this.field_16054.method_14988(bl);
				this.field_16049.method_14478(bl);
				this.field_16049.playDownSound(this.client.getSoundManager());
				this.method_14601();
				this.method_14589(false);
				return true;
			} else {
				for (class_3284 lv2 : this.field_16047) {
					if (lv2.isMouseOver(this.client, i, j)) {
						if (this.field_16048 != lv2) {
							lv2.playDownSound(this.client.getSoundManager());
							this.field_16048.method_14478(false);
							this.field_16048 = lv2;
							this.field_16048.method_14478(true);
							this.method_14589(true);
						}

						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public boolean method_14592(int i, int j, int k, int l, int m, int n) {
		if (!this.method_14590()) {
			return true;
		} else {
			boolean bl = i < k || j < l || i >= k + m || j >= l + n;
			boolean bl2 = k - 147 < i && i < k && l < j && j < l + n;
			return bl && !bl2 && !this.field_16048.isMouseOver(this.client, i, j);
		}
	}

	public boolean method_14574(char c, int i) {
		if (!this.method_14590() || this.client.player.isSpectator()) {
			return false;
		} else if (i == 1 && !this.method_14600()) {
			this.method_14584(false);
			return true;
		} else {
			if (GameOptions.isPressed(this.client.options.chatKey) && !this.field_16052.isFocused()) {
				this.field_16052.setFocused(true);
			} else if (this.field_16052.keyPressed(c, i)) {
				String string = this.field_16052.getText().toLowerCase(Locale.ROOT);
				this.method_14583(string);
				if (!string.equals(this.field_16053)) {
					this.method_14589(false);
					this.field_16053 = string;
				}

				return true;
			}

			return false;
		}
	}

	private void method_14583(String string) {
		if ("excitedze".equals(string)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			LanguageDefinition languageDefinition = languageManager.method_14698("en_pt");
			if (languageManager.getLanguage().compareTo(languageDefinition) == 0) {
				return;
			}

			languageManager.setLanguage(languageDefinition);
			this.client.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			this.client.textRenderer.setUnicode(this.client.getLanguageManager().forcesUnicodeFont() || this.client.options.forcesUnicodeFont);
			this.client.textRenderer.setRightToLeft(languageManager.isRightToLeft());
			this.client.options.save();
		}
	}

	private boolean method_14600() {
		return this.field_16043 == 86;
	}

	public void method_14597() {
		this.method_14598();
		if (this.method_14590()) {
			this.method_14589(false);
		}
	}

	@Override
	public void method_14636(List<RecipeType> list) {
		for (RecipeType recipeType : list) {
			this.client.player.method_14676(recipeType);
		}
	}

	public void method_14580(RecipeType recipeType, List<Slot> list) {
		ItemStack itemStack = recipeType.getOutput();
		this.field_16046.method_14554(recipeType);
		this.field_16046.method_14553(Ingredient.method_14248(itemStack), ((Slot)list.get(0)).x, ((Slot)list.get(0)).y);
		int i = this.field_16050.getWidth();
		int j = this.field_16050.getHeight();
		int k = recipeType instanceof ShapedRecipeType ? ((ShapedRecipeType)recipeType).method_14272() : i;
		int l = 1;
		Iterator<Ingredient> iterator = recipeType.method_14252().iterator();

		for (int m = 0; m < j; m++) {
			for (int n = 0; n < k; n++) {
				if (!iterator.hasNext()) {
					return;
				}

				Ingredient ingredient = (Ingredient)iterator.next();
				if (ingredient != Ingredient.field_15680) {
					Slot slot = (Slot)list.get(l);
					this.field_16046.method_14553(ingredient, slot.x, slot.y);
				}

				l++;
			}

			if (k < i) {
				l += i - k;
			}
		}
	}

	private void method_14601() {
		if (this.client.getNetworkHandler() != null) {
			this.client.getNetworkHandler().sendPacket(new CraftingBlockData(this.method_14590(), this.field_16054.method_14986()));
		}
	}
}
