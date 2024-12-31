package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3411 extends DataFix {
	private static final Logger field_16613 = LogManager.getLogger();

	public class_3411(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(class_3402.field_16584);
		Type<?> type2 = type.findFieldType("Level");
		Type<?> type3 = type2.findFieldType("TileEntities");
		if (!(type3 instanceof ListType)) {
			throw new IllegalStateException("Tile entity type is not a list type.");
		} else {
			ListType<?> listType = (ListType<?>)type3;
			OpticFinder<? extends List<?>> opticFinder = DSL.fieldFinder("TileEntities", listType);
			Type<?> type4 = this.getInputSchema().getType(class_3402.field_16584);
			OpticFinder<?> opticFinder2 = type4.findField("Level");
			OpticFinder<?> opticFinder3 = opticFinder2.type().findField("Sections");
			Type<?> type5 = opticFinder3.type();
			if (!(type5 instanceof ListType)) {
				throw new IllegalStateException("Expecting sections to be a list.");
			} else {
				Type<?> type6 = ((ListType)type5).getElement();
				OpticFinder<?> opticFinder4 = DSL.typeFinder(type6);
				return TypeRewriteRule.seq(
					new class_4490(this.getOutputSchema(), "AddTrappedChestFix", class_3402.field_16591).makeRule(),
					this.fixTypeEverywhereTyped("Trapped Chest fix", type4, typed -> typed.updateTyped(opticFinder2, typedx -> {
							Optional<? extends Typed<?>> optional = typedx.getOptionalTyped(opticFinder3);
							if (!optional.isPresent()) {
								return typedx;
							} else {
								List<? extends Typed<?>> list = ((Typed)optional.get()).getAllTyped(opticFinder4);
								IntSet intSet = new IntOpenHashSet();

								for (Typed<?> typed2 : list) {
									class_3411.class_3412 lv = new class_3411.class_3412(typed2, this.getInputSchema());
									if (!lv.method_15177()) {
										for (int i = 0; i < 4096; i++) {
											int j = lv.method_15180(i);
											if (lv.method_15278(j)) {
												intSet.add(lv.method_15179() << 12 | i);
											}
										}
									}
								}

								Dynamic<?> dynamic = (Dynamic<?>)typedx.get(DSL.remainderFinder());
								int k = dynamic.getInt("xPos");
								int l = dynamic.getInt("zPos");
								TaggedChoiceType<String> taggedChoiceType = this.getInputSchema().findChoiceType(class_3402.field_16591);
								return typedx.updateTyped(opticFinder, typedxx -> typedxx.updateTyped(taggedChoiceType.finder(), typedxxx -> {
										Dynamic<?> dynamicx = (Dynamic<?>)typedxxx.getOrCreate(DSL.remainderFinder());
										int kx = dynamicx.getInt("x") - (k << 4);
										int lx = dynamicx.getInt("y");
										int m = dynamicx.getInt("z") - (l << 4);
										return intSet.contains(class_3390.method_15149(kx, lx, m)) ? typedxxx.update(taggedChoiceType.finder(), pair -> pair.mapFirst(string -> {
												if (!Objects.equals(string, "minecraft:chest")) {
													field_16613.warn("Block Entity was expected to be a chest");
												}

												return "minecraft:trapped_chest";
											})) : typedxxx;
									}));
							}
						}))
				);
			}
		}
	}

	public static final class class_3412 extends class_3390.class_3392 {
		@Nullable
		private IntSet field_16614;

		public class_3412(Typed<?> typed, Schema schema) {
			super(typed, schema);
		}

		@Override
		protected boolean method_15172() {
			this.field_16614 = new IntOpenHashSet();

			for (int i = 0; i < this.field_16567.size(); i++) {
				Dynamic<?> dynamic = (Dynamic<?>)this.field_16567.get(i);
				String string = dynamic.getString("Name");
				if (Objects.equals(string, "minecraft:trapped_chest")) {
					this.field_16614.add(i);
				}
			}

			return this.field_16614.isEmpty();
		}

		public boolean method_15278(int i) {
			return this.field_16614.contains(i);
		}
	}
}
