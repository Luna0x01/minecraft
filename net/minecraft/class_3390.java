package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.palette.PaletteData;

public class class_3390 extends DataFix {
	private static final int[][] field_16559 = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
	private static final Object2IntMap<String> field_16560 = (Object2IntMap<String>)DataFixUtils.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.put("minecraft:acacia_leaves", 0);
		object2IntOpenHashMap.put("minecraft:birch_leaves", 1);
		object2IntOpenHashMap.put("minecraft:dark_oak_leaves", 2);
		object2IntOpenHashMap.put("minecraft:jungle_leaves", 3);
		object2IntOpenHashMap.put("minecraft:oak_leaves", 4);
		object2IntOpenHashMap.put("minecraft:spruce_leaves", 5);
	});
	private static final Set<String> field_16561 = ImmutableSet.of(
		"minecraft:acacia_bark",
		"minecraft:birch_bark",
		"minecraft:dark_oak_bark",
		"minecraft:jungle_bark",
		"minecraft:oak_bark",
		"minecraft:spruce_bark",
		new String[]{
			"minecraft:acacia_log",
			"minecraft:birch_log",
			"minecraft:dark_oak_log",
			"minecraft:jungle_log",
			"minecraft:oak_log",
			"minecraft:spruce_log",
			"minecraft:stripped_acacia_log",
			"minecraft:stripped_birch_log",
			"minecraft:stripped_dark_oak_log",
			"minecraft:stripped_jungle_log",
			"minecraft:stripped_oak_log",
			"minecraft:stripped_spruce_log"
		}
	);

	public class_3390(Schema schema, boolean bl) {
		super(schema, bl);
	}

	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16584);
		OpticFinder<?> opticFinder = type.findField("Level");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("Sections");
		Type<?> type2 = opticFinder2.type();
		if (!(type2 instanceof ListType)) {
			throw new IllegalStateException("Expecting sections to be a list.");
		} else {
			Type<?> type3 = ((ListType)type2).getElement();
			OpticFinder<?> opticFinder3 = DSL.typeFinder(type3);
			return this.fixTypeEverywhereTyped(
				"Leaves fix",
				type,
				typed -> typed.updateTyped(
						opticFinder,
						typedx -> {
							int[] is = new int[]{0};
							Typed<?> typed2 = typedx.updateTyped(
								opticFinder2,
								typedxx -> {
									Int2ObjectMap<class_3390.class_3391> int2ObjectMap = new Int2ObjectOpenHashMap(
										(Map)typedxx.getAllTyped(opticFinder3)
											.stream()
											.map(typedxxx -> new class_3390.class_3391(typedxxx, this.getInputSchema()))
											.collect(Collectors.toMap(class_3390.class_3392::method_15179, arg -> arg))
									);
									if (int2ObjectMap.values().stream().allMatch(class_3390.class_3392::method_15177)) {
										return typedxx;
									} else {
										List<IntSet> list = Lists.newArrayList();

										for (int i = 0; i < 7; i++) {
											list.add(new IntOpenHashSet());
										}

										ObjectIterator var25 = int2ObjectMap.values().iterator();

										while (var25.hasNext()) {
											class_3390.class_3391 lv = (class_3390.class_3391)var25.next();
											if (!lv.method_15177()) {
												for (int j = 0; j < 4096; j++) {
													int k = lv.method_15180(j);
													if (lv.method_15162(k)) {
														((IntSet)list.get(0)).add(lv.method_15179() << 12 | j);
													} else if (lv.method_15167(k)) {
														int l = this.method_15148(j);
														int m = this.method_15161(j);
														is[0] |= method_15157(l == 0, l == 15, m == 0, m == 15);
													}
												}
											}
										}

										for (int n = 1; n < 7; n++) {
											IntSet intSet = (IntSet)list.get(n - 1);
											IntSet intSet2 = (IntSet)list.get(n);
											IntIterator intIterator = intSet.iterator();

											while (intIterator.hasNext()) {
												int o = intIterator.nextInt();
												int p = this.method_15148(o);
												int q = this.method_15160(o);
												int r = this.method_15161(o);

												for (int[] js : field_16559) {
													int s = p + js[0];
													int t = q + js[1];
													int u = r + js[2];
													if (s >= 0 && s <= 15 && u >= 0 && u <= 15 && t >= 0 && t <= 255) {
														class_3390.class_3391 lv2 = (class_3390.class_3391)int2ObjectMap.get(t >> 4);
														if (lv2 != null && !lv2.method_15177()) {
															int v = method_15149(s, t & 15, u);
															int w = lv2.method_15180(v);
															if (lv2.method_15167(w)) {
																int x = lv2.method_15170(w);
																if (x > n) {
																	lv2.method_15163(v, w, n);
																	intSet2.add(method_15149(s, t, u));
																}
															}
														}
													}
												}
											}
										}

										return typedxx.updateTyped(
											opticFinder3,
											typedxxx -> ((class_3390.class_3391)int2ObjectMap.get(((Dynamic)typedxxx.get(DSL.remainderFinder())).getInt("Y"))).method_15174(typedxxx)
										);
									}
								}
							);
							if (is[0] != 0) {
								typed2 = typed2.update(DSL.remainderFinder(), dynamic -> {
									Dynamic<?> dynamic2 = (Dynamic<?>)DataFixUtils.orElse(dynamic.get("UpgradeData"), dynamic.emptyMap());
									return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.getByte("Sides") | is[0]))));
								});
							}

							return typed2;
						}
					)
			);
		}
	}

	public static int method_15149(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private int method_15148(int i) {
		return i & 15;
	}

	private int method_15160(int i) {
		return i >> 8 & 0xFF;
	}

	private int method_15161(int i) {
		return i >> 4 & 15;
	}

	public static int method_15157(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		int i = 0;
		if (bl3) {
			if (bl2) {
				i |= 2;
			} else if (bl) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (bl4) {
			if (bl) {
				i |= 32;
			} else if (bl2) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (bl2) {
			i |= 4;
		} else if (bl) {
			i |= 64;
		}

		return i;
	}

	public static final class class_3391 extends class_3390.class_3392 {
		@Nullable
		private IntSet field_16562;
		@Nullable
		private IntSet field_16563;
		@Nullable
		private Int2IntMap field_16564;

		public class_3391(Typed<?> typed, Schema schema) {
			super(typed, schema);
		}

		@Override
		protected boolean method_15172() {
			this.field_16562 = new IntOpenHashSet();
			this.field_16563 = new IntOpenHashSet();
			this.field_16564 = new Int2IntOpenHashMap();

			for (int i = 0; i < this.field_16567.size(); i++) {
				Dynamic<?> dynamic = (Dynamic<?>)this.field_16567.get(i);
				String string = dynamic.getString("Name");
				if (class_3390.field_16560.containsKey(string)) {
					boolean bl = Objects.equals(dynamic.get("Properties").flatMap(dynamicx -> dynamicx.get("decayable")).flatMap(Dynamic::getStringValue).orElse(""), "false");
					this.field_16562.add(i);
					this.field_16564.put(this.method_15175(string, bl, 7), i);
					this.field_16567.set(i, this.method_15166(dynamic, string, bl, 7));
				}

				if (class_3390.field_16561.contains(string)) {
					this.field_16563.add(i);
				}
			}

			return this.field_16562.isEmpty() && this.field_16563.isEmpty();
		}

		private Dynamic<?> method_15166(Dynamic<?> dynamic, String string, boolean bl, int i) {
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			dynamic2 = dynamic2.set("persistent", dynamic2.createString(bl ? "true" : "false"));
			dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(i)));
			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Properties", dynamic2);
			return dynamic3.set("Name", dynamic3.createString(string));
		}

		public boolean method_15162(int i) {
			return this.field_16563.contains(i);
		}

		public boolean method_15167(int i) {
			return this.field_16562.contains(i);
		}

		private int method_15170(int i) {
			return this.method_15162(i)
				? 0
				: Integer.parseInt(
					(String)((Dynamic)this.field_16567.get(i)).get("Properties").flatMap(dynamic -> dynamic.get("distance")).flatMap(Dynamic::getStringValue).orElse("")
				);
		}

		private void method_15163(int i, int j, int k) {
			Dynamic<?> dynamic = (Dynamic<?>)this.field_16567.get(j);
			String string = dynamic.getString("Name");
			boolean bl = Objects.equals(dynamic.get("Properties").flatMap(dynamicx -> dynamicx.get("persistent")).flatMap(Dynamic::getStringValue).orElse(""), "true");
			int l = this.method_15175(string, bl, k);
			if (!this.field_16564.containsKey(l)) {
				int m = this.field_16567.size();
				this.field_16562.add(m);
				this.field_16564.put(l, m);
				this.field_16567.add(this.method_15166(dynamic, string, bl, k));
			}

			int n = this.field_16564.get(l);
			if (1 << this.field_16569.method_21498() <= n) {
				PaletteData paletteData = new PaletteData(this.field_16569.method_21498() + 1, 4096);

				for (int o = 0; o < 4096; o++) {
					paletteData.set(o, this.field_16569.get(o));
				}

				this.field_16569 = paletteData;
			}

			this.field_16569.set(i, n);
		}
	}

	public abstract static class class_3392 {
		final Type<Pair<String, Dynamic<?>>> field_16565 = DSL.named(class_3402.field_16593.typeName(), DSL.remainderType());
		protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_16566 = DSL.fieldFinder("Palette", DSL.list(this.field_16565));
		protected final List<Dynamic<?>> field_16567;
		protected final int field_16568;
		@Nullable
		protected PaletteData field_16569;

		public class_3392(Typed<?> typed, Schema schema) {
			if (!Objects.equals(schema.getType(class_3402.field_16593), this.field_16565)) {
				throw new IllegalStateException("Block state type is not what was expected.");
			} else {
				Optional<List<Pair<String, Dynamic<?>>>> optional = typed.getOptional(this.field_16566);
				this.field_16567 = (List<Dynamic<?>>)optional.map(list -> (List)list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
				Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
				this.field_16568 = dynamic.getInt("Y");
				this.method_15173(dynamic);
			}
		}

		protected void method_15173(Dynamic<?> dynamic) {
			if (this.method_15172()) {
				this.field_16569 = null;
			} else {
				long[] ls = ((LongStream)dynamic.get("BlockStates").flatMap(Dynamic::getLongStream).get()).toArray();
				int i = Math.max(4, DataFixUtils.ceillog2(this.field_16567.size()));
				this.field_16569 = new PaletteData(i, 4096, ls);
			}
		}

		public Typed<?> method_15174(Typed<?> typed) {
			return this.method_15177()
				? typed
				: typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.field_16569.getBlockStateIds()))))
					.set(this.field_16566, this.field_16567.stream().map(dynamic -> Pair.of(class_3402.field_16593.typeName(), dynamic)).collect(Collectors.toList()));
		}

		public boolean method_15177() {
			return this.field_16569 == null;
		}

		public int method_15180(int i) {
			return this.field_16569.get(i);
		}

		protected int method_15175(String string, boolean bl, int i) {
			return class_3390.field_16560.get(string) << 5 | (bl ? 16 : 0) | i;
		}

		int method_15179() {
			return this.field_16568;
		}

		protected abstract boolean method_15172();
	}
}
