package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
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
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.WordPackedArray;

public class LeavesFix extends DataFix {
	private static final int[][] field_5687 = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
	private static final Object2IntMap<String> LEAVES_MAP = (Object2IntMap<String>)DataFixUtils.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.put("minecraft:acacia_leaves", 0);
		object2IntOpenHashMap.put("minecraft:birch_leaves", 1);
		object2IntOpenHashMap.put("minecraft:dark_oak_leaves", 2);
		object2IntOpenHashMap.put("minecraft:jungle_leaves", 3);
		object2IntOpenHashMap.put("minecraft:oak_leaves", 4);
		object2IntOpenHashMap.put("minecraft:spruce_leaves", 5);
	});
	private static final Set<String> LOGS_MAP = ImmutableSet.of(
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

	public LeavesFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
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
									Int2ObjectMap<LeavesFix.LeavesLogFixer> int2ObjectMap = new Int2ObjectOpenHashMap(
										(Map)typedxx.getAllTyped(opticFinder3)
											.stream()
											.map(typedxxx -> new LeavesFix.LeavesLogFixer(typedxxx, this.getInputSchema()))
											.collect(Collectors.toMap(LeavesFix.ListFixer::method_5077, leavesLogFixer -> leavesLogFixer))
									);
									if (int2ObjectMap.values().stream().allMatch(LeavesFix.ListFixer::isFixed)) {
										return typedxx;
									} else {
										List<IntSet> list = Lists.newArrayList();

										for (int i = 0; i < 7; i++) {
											list.add(new IntOpenHashSet());
										}

										ObjectIterator var25 = int2ObjectMap.values().iterator();

										while (var25.hasNext()) {
											LeavesFix.LeavesLogFixer leavesLogFixer = (LeavesFix.LeavesLogFixer)var25.next();
											if (!leavesLogFixer.isFixed()) {
												for (int j = 0; j < 4096; j++) {
													int k = leavesLogFixer.needsFix(j);
													if (leavesLogFixer.isLog(k)) {
														((IntSet)list.get(0)).add(leavesLogFixer.method_5077() << 12 | j);
													} else if (leavesLogFixer.isLeaf(k)) {
														int l = this.method_5052(j);
														int m = this.method_5050(j);
														is[0] |= method_5061(l == 0, l == 15, m == 0, m == 15);
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
												int p = this.method_5052(o);
												int q = this.method_5062(o);
												int r = this.method_5050(o);

												for (int[] js : field_5687) {
													int s = p + js[0];
													int t = q + js[1];
													int u = r + js[2];
													if (s >= 0 && s <= 15 && u >= 0 && u <= 15 && t >= 0 && t <= 255) {
														LeavesFix.LeavesLogFixer leavesLogFixer2 = (LeavesFix.LeavesLogFixer)int2ObjectMap.get(t >> 4);
														if (leavesLogFixer2 != null && !leavesLogFixer2.isFixed()) {
															int v = method_5051(s, t & 15, u);
															int w = leavesLogFixer2.needsFix(v);
															if (leavesLogFixer2.isLeaf(w)) {
																int x = leavesLogFixer2.getDistanceToLog(w);
																if (x > n) {
																	leavesLogFixer2.computeLeafStates(v, w, n);
																	intSet2.add(method_5051(s, t, u));
																}
															}
														}
													}
												}
											}
										}

										return typedxx.updateTyped(
											opticFinder3,
											typedxxx -> ((LeavesFix.LeavesLogFixer)int2ObjectMap.get(((Dynamic)typedxxx.get(DSL.remainderFinder())).get("Y").asInt(0))).method_5083(typedxxx)
										);
									}
								}
							);
							if (is[0] != 0) {
								typed2 = typed2.update(DSL.remainderFinder(), dynamic -> {
									Dynamic<?> dynamic2 = (Dynamic<?>)DataFixUtils.orElse(dynamic.get("UpgradeData").result(), dynamic.emptyMap());
									return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | is[0]))));
								});
							}

							return typed2;
						}
					)
			);
		}
	}

	public static int method_5051(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private int method_5052(int i) {
		return i & 15;
	}

	private int method_5062(int i) {
		return i >> 8 & 0xFF;
	}

	private int method_5050(int i) {
		return i >> 4 & 15;
	}

	public static int method_5061(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
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

	public static final class LeavesLogFixer extends LeavesFix.ListFixer {
		@Nullable
		private IntSet leafIndices;
		@Nullable
		private IntSet logIndices;
		@Nullable
		private Int2IntMap leafStates;

		public LeavesLogFixer(Typed<?> typed, Schema schema) {
			super(typed, schema);
		}

		@Override
		protected boolean needsFix() {
			this.leafIndices = new IntOpenHashSet();
			this.logIndices = new IntOpenHashSet();
			this.leafStates = new Int2IntOpenHashMap();

			for (int i = 0; i < this.properties.size(); i++) {
				Dynamic<?> dynamic = (Dynamic<?>)this.properties.get(i);
				String string = dynamic.get("Name").asString("");
				if (LeavesFix.LEAVES_MAP.containsKey(string)) {
					boolean bl = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
					this.leafIndices.add(i);
					this.leafStates.put(this.computeFlags(string, bl, 7), i);
					this.properties.set(i, this.createLeafProperties(dynamic, string, bl, 7));
				}

				if (LeavesFix.LOGS_MAP.contains(string)) {
					this.logIndices.add(i);
				}
			}

			return this.leafIndices.isEmpty() && this.logIndices.isEmpty();
		}

		private Dynamic<?> createLeafProperties(Dynamic<?> dynamic, String string, boolean bl, int i) {
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			dynamic2 = dynamic2.set("persistent", dynamic2.createString(bl ? "true" : "false"));
			dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(i)));
			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Properties", dynamic2);
			return dynamic3.set("Name", dynamic3.createString(string));
		}

		public boolean isLog(int i) {
			return this.logIndices.contains(i);
		}

		public boolean isLeaf(int i) {
			return this.leafIndices.contains(i);
		}

		private int getDistanceToLog(int i) {
			return this.isLog(i) ? 0 : Integer.parseInt(((Dynamic)this.properties.get(i)).get("Properties").get("distance").asString(""));
		}

		private void computeLeafStates(int i, int j, int k) {
			Dynamic<?> dynamic = (Dynamic<?>)this.properties.get(j);
			String string = dynamic.get("Name").asString("");
			boolean bl = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true");
			int l = this.computeFlags(string, bl, k);
			if (!this.leafStates.containsKey(l)) {
				int m = this.properties.size();
				this.leafIndices.add(m);
				this.leafStates.put(l, m);
				this.properties.add(this.createLeafProperties(dynamic, string, bl, k));
			}

			int n = this.leafStates.get(l);
			if (1 << this.blockStateMap.getUnitSize() <= n) {
				WordPackedArray wordPackedArray = new WordPackedArray(this.blockStateMap.getUnitSize() + 1, 4096);

				for (int o = 0; o < 4096; o++) {
					wordPackedArray.set(o, this.blockStateMap.get(o));
				}

				this.blockStateMap = wordPackedArray;
			}

			this.blockStateMap.set(i, n);
		}
	}

	public abstract static class ListFixer {
		private final Type<Pair<String, Dynamic<?>>> field_5695 = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
		protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_5693 = DSL.fieldFinder("Palette", DSL.list(this.field_5695));
		protected final List<Dynamic<?>> properties;
		protected final int field_5694;
		@Nullable
		protected WordPackedArray blockStateMap;

		public ListFixer(Typed<?> typed, Schema schema) {
			if (!Objects.equals(schema.getType(TypeReferences.BLOCK_STATE), this.field_5695)) {
				throw new IllegalStateException("Block state type is not what was expected.");
			} else {
				Optional<List<Pair<String, Dynamic<?>>>> optional = typed.getOptional(this.field_5693);
				this.properties = (List<Dynamic<?>>)optional.map(list -> (List)list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
				Dynamic<?> dynamic = (Dynamic<?>)typed.get(DSL.remainderFinder());
				this.field_5694 = dynamic.get("Y").asInt(0);
				this.computeFixableBlockStates(dynamic);
			}
		}

		protected void computeFixableBlockStates(Dynamic<?> dynamic) {
			if (this.needsFix()) {
				this.blockStateMap = null;
			} else {
				long[] ls = dynamic.get("BlockStates").asLongStream().toArray();
				int i = Math.max(4, DataFixUtils.ceillog2(this.properties.size()));
				this.blockStateMap = new WordPackedArray(i, 4096, ls);
			}
		}

		public Typed<?> method_5083(Typed<?> typed) {
			return this.isFixed()
				? typed
				: typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.blockStateMap.getAlignedArray()))))
					.set(this.field_5693, this.properties.stream().map(dynamic -> Pair.of(TypeReferences.BLOCK_STATE.typeName(), dynamic)).collect(Collectors.toList()));
		}

		public boolean isFixed() {
			return this.blockStateMap == null;
		}

		public int needsFix(int i) {
			return this.blockStateMap.get(i);
		}

		protected int computeFlags(String leafBlockName, boolean persistent, int i) {
			return LeavesFix.LEAVES_MAP.get(leafBlockName) << 5 | (persistent ? 16 : 0) | i;
		}

		int method_5077() {
			return this.field_5694;
		}

		protected abstract boolean needsFix();
	}
}
