package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;

public class class_3175 {
	public final Int2IntMap field_15628 = new Int2IntOpenHashMap();

	public void method_14170(ItemStack itemStack) {
		if (!itemStack.isEmpty() && !itemStack.isDamaged() && !itemStack.hasEnchantments() && !itemStack.hasCustomName()) {
			int i = method_14176(itemStack);
			int j = itemStack.getCount();
			this.method_14175(i, j);
		}
	}

	public static int method_14176(ItemStack itemStack) {
		Item item = itemStack.getItem();
		int i = item.isUnbreakable() ? itemStack.getData() : 0;
		return Item.REGISTRY.getRawId(item) << 16 | i & 65535;
	}

	public boolean method_14167(int i) {
		return this.field_15628.get(i) > 0;
	}

	public int method_14168(int i, int j) {
		int k = this.field_15628.get(i);
		if (k >= j) {
			this.field_15628.put(i, k - j);
			return i;
		} else {
			return 0;
		}
	}

	private void method_14175(int i, int j) {
		this.field_15628.put(i, this.field_15628.get(i) + j);
	}

	public boolean method_14172(RecipeType recipeType, @Nullable IntList intList) {
		return this.method_14173(recipeType, intList, 1);
	}

	public boolean method_14173(RecipeType recipeType, @Nullable IntList intList, int i) {
		return new class_3175.class_3176(recipeType).method_14180(i, intList);
	}

	public int method_14177(RecipeType recipeType, @Nullable IntList intList) {
		return this.method_14171(recipeType, Integer.MAX_VALUE, intList);
	}

	public int method_14171(RecipeType recipeType, int i, @Nullable IntList intList) {
		return new class_3175.class_3176(recipeType).method_14186(i, intList);
	}

	public static ItemStack method_14174(int i) {
		return i == 0 ? ItemStack.EMPTY : new ItemStack(Item.byRawId(i >> 16 & 65535), 1, i & 65535);
	}

	public void method_14166() {
		this.field_15628.clear();
	}

	class class_3176 {
		private final RecipeType field_15630;
		private final List<Ingredient> field_15631 = Lists.newArrayList();
		private final int field_15632;
		private final int[] field_15633;
		private final int field_15634;
		private final BitSet field_15635;
		private IntList field_15636 = new IntArrayList();

		public class_3176(RecipeType recipeType) {
			this.field_15630 = recipeType;
			this.field_15631.addAll(recipeType.method_14252());
			this.field_15631.removeIf(ingredient -> ingredient == Ingredient.field_15680);
			this.field_15632 = this.field_15631.size();
			this.field_15633 = this.method_14178();
			this.field_15634 = this.field_15633.length;
			this.field_15635 = new BitSet(this.field_15632 + this.field_15634 + this.field_15632 + this.field_15632 * this.field_15634);

			for (int i = 0; i < this.field_15631.size(); i++) {
				IntList intList = ((Ingredient)this.field_15631.get(i)).method_14249();

				for (int j = 0; j < this.field_15634; j++) {
					if (intList.contains(this.field_15633[j])) {
						this.field_15635.set(this.method_14193(true, j, i));
					}
				}
			}
		}

		public boolean method_14180(int i, @Nullable IntList intList) {
			if (i <= 0) {
				return true;
			} else {
				int j;
				for (j = 0; this.method_14179(i); j++) {
					class_3175.this.method_14168(this.field_15633[this.field_15636.getInt(0)], i);
					int k = this.field_15636.size() - 1;
					this.method_14189(this.field_15636.getInt(k));

					for (int l = 0; l < k; l++) {
						this.method_14191((l & 1) == 0, (Integer)this.field_15636.get(l), (Integer)this.field_15636.get(l + 1));
					}

					this.field_15636.clear();
					this.field_15635.clear(0, this.field_15632 + this.field_15634);
				}

				boolean bl = j == this.field_15632;
				boolean bl2 = bl && intList != null;
				if (bl2) {
					intList.clear();
				}

				this.field_15635.clear(0, this.field_15632 + this.field_15634 + this.field_15632);
				int m = 0;
				List<Ingredient> list = this.field_15630.method_14252();

				for (int n = 0; n < list.size(); n++) {
					if (bl2 && list.get(n) == Ingredient.field_15680) {
						intList.add(0);
					} else {
						for (int o = 0; o < this.field_15634; o++) {
							if (this.method_14188(false, m, o)) {
								this.method_14191(true, o, m);
								class_3175.this.method_14175(this.field_15633[o], i);
								if (bl2) {
									intList.add(this.field_15633[o]);
								}
							}
						}

						m++;
					}
				}

				return bl;
			}
		}

		private int[] method_14178() {
			IntCollection intCollection = new IntAVLTreeSet();

			for (Ingredient ingredient : this.field_15631) {
				intCollection.addAll(ingredient.method_14249());
			}

			IntIterator intIterator = intCollection.iterator();

			while (intIterator.hasNext()) {
				if (!class_3175.this.method_14167(intIterator.nextInt())) {
					intIterator.remove();
				}
			}

			return intCollection.toIntArray();
		}

		private boolean method_14179(int i) {
			int j = this.field_15634;

			for (int k = 0; k < j; k++) {
				if (class_3175.this.field_15628.get(this.field_15633[k]) >= i) {
					this.method_14182(false, k);

					while (!this.field_15636.isEmpty()) {
						int l = this.field_15636.size();
						boolean bl = (l & 1) == 1;
						int m = this.field_15636.getInt(l - 1);
						if (!bl && !this.method_14185(m)) {
							break;
						}

						int n = bl ? this.field_15632 : j;
						int o = 0;

						while (true) {
							if (o < n) {
								if (this.method_14187(bl, o) || !this.method_14183(bl, m, o) || !this.method_14188(bl, m, o)) {
									o++;
									continue;
								}

								this.method_14182(bl, o);
							}

							o = this.field_15636.size();
							if (o == l) {
								this.field_15636.removeInt(o - 1);
							}
							break;
						}
					}

					if (!this.field_15636.isEmpty()) {
						return true;
					}
				}
			}

			return false;
		}

		private boolean method_14185(int i) {
			return this.field_15635.get(this.method_14192(i));
		}

		private void method_14189(int i) {
			this.field_15635.set(this.method_14192(i));
		}

		private int method_14192(int i) {
			return this.field_15632 + this.field_15634 + i;
		}

		private boolean method_14183(boolean bl, int i, int j) {
			return this.field_15635.get(this.method_14193(bl, i, j));
		}

		private boolean method_14188(boolean bl, int i, int j) {
			return bl != this.field_15635.get(1 + this.method_14193(bl, i, j));
		}

		private void method_14191(boolean bl, int i, int j) {
			this.field_15635.flip(1 + this.method_14193(bl, i, j));
		}

		private int method_14193(boolean bl, int i, int j) {
			int k = bl ? i * this.field_15632 + j : j * this.field_15632 + i;
			return this.field_15632 + this.field_15634 + this.field_15632 + 2 * k;
		}

		private void method_14182(boolean bl, int i) {
			this.field_15635.set(this.method_14190(bl, i));
			this.field_15636.add(i);
		}

		private boolean method_14187(boolean bl, int i) {
			return this.field_15635.get(this.method_14190(bl, i));
		}

		private int method_14190(boolean bl, int i) {
			return (bl ? 0 : this.field_15632) + i;
		}

		public int method_14186(int i, @Nullable IntList intList) {
			int j = 0;
			int k = Math.min(i, this.method_14184()) + 1;

			while (true) {
				int l = (j + k) / 2;
				if (this.method_14180(l, null)) {
					if (k - j <= 1) {
						if (l > 0) {
							this.method_14180(l, intList);
						}

						return l;
					}

					j = l;
				} else {
					k = l;
				}
			}
		}

		private int method_14184() {
			int i = Integer.MAX_VALUE;

			for (Ingredient ingredient : this.field_15631) {
				int j = 0;
				IntListIterator var5 = ingredient.method_14249().iterator();

				while (var5.hasNext()) {
					int k = (Integer)var5.next();
					j = Math.max(j, class_3175.this.field_15628.get(k));
				}

				if (i > 0) {
					i = Math.min(i, j);
				}
			}

			return i;
		}
	}
}
