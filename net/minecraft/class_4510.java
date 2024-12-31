package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_4510 extends DataFix {
	public class_4510(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_3402.field_16584);
		Type<?> type2 = this.getOutputSchema().getType(class_3402.field_16584);
		Type<?> type3 = type.findFieldType("Level");
		Type<?> type4 = type2.findFieldType("Level");
		Type<?> type5 = type3.findFieldType("TileTicks");
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type3);
		OpticFinder<?> opticFinder2 = DSL.fieldFinder("TileTicks", type5);
		return TypeRewriteRule.seq(
			this.fixTypeEverywhereTyped(
				"ChunkToProtoChunkFix",
				type,
				this.getOutputSchema().getType(class_3402.field_16584),
				typed -> typed.updateTyped(
						opticFinder,
						type4,
						typedx -> {
							Optional<? extends Stream<? extends Dynamic<?>>> optional = typedx.getOptionalTyped(opticFinder2).map(Typed::write).flatMap(Dynamic::getStream);
							Dynamic<?> dynamic = (Dynamic<?>)typedx.get(DSL.remainderFinder());
							boolean bl = dynamic.getBoolean("TerrainPopulated")
								&& (!dynamic.get("LightPopulated").flatMap(Dynamic::getNumberValue).isPresent() || dynamic.getBoolean("LightPopulated"));
							dynamic = dynamic.set("Status", dynamic.createString(bl ? "mobs_spawned" : "empty"));
							dynamic = dynamic.set("hasLegacyStructureData", dynamic.createBoolean(true));
							Dynamic<?> dynamic3;
							if (bl) {
								Optional<ByteBuffer> optional2 = dynamic.get("Biomes").flatMap(Dynamic::getByteBuffer);
								if (optional2.isPresent()) {
									ByteBuffer byteBuffer = (ByteBuffer)optional2.get();
									int[] is = new int[256];

									for (int i = 0; i < is.length; i++) {
										if (i < byteBuffer.capacity()) {
											is[i] = byteBuffer.get(i) & 255;
										}
									}

									dynamic = dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(is)));
								}

								Dynamic<?> dynamic2 = dynamic;
								List<Dynamic<?>> list = (List<Dynamic<?>>)IntStream.range(0, 16).mapToObj(ix -> dynamic2.createList(Stream.empty())).collect(Collectors.toList());
								if (optional.isPresent()) {
									((Stream)optional.get()).forEach(dynamic2x -> {
										int ix = dynamic2x.getInt("x");
										int j = dynamic2x.getInt("y");
										int k = dynamic2x.getInt("z");
										short s = method_21677(ix, j, k);
										list.set(j >> 4, ((Dynamic)list.get(j >> 4)).merge(dynamic2.createShort(s)));
									});
									dynamic = dynamic.set("ToBeTicked", dynamic.createList(list.stream()));
								}

								dynamic3 = typedx.set(DSL.remainderFinder(), dynamic).write();
							} else {
								dynamic3 = dynamic;
							}

							return (Typed)((Optional)type4.readTyped(dynamic3).getSecond()).orElseThrow(() -> new IllegalStateException("Could not read the new chunk"));
						}
					)
			),
			this.writeAndRead("Structure biome inject", this.getInputSchema().getType(class_3402.field_16600), this.getOutputSchema().getType(class_3402.field_16600))
		);
	}

	private static short method_21677(int i, int j, int k) {
		return (short)(i & 15 | (j & 15) << 4 | (k & 15) << 8);
	}
}
