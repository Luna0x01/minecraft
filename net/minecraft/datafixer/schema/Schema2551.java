package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema2551 extends IdentifierNormalizingSchema {
	public Schema2551(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(
			false,
			TypeReferences.CHUNK_GENERATOR_SETTINGS,
			() -> DSL.fields(
					"dimensions",
					DSL.compoundList(
						DSL.constType(getIdentifierType()),
						DSL.fields(
							"generator",
							DSL.taggedChoiceLazy(
								"type",
								DSL.string(),
								ImmutableMap.of(
									"minecraft:debug",
									DSL::remainder,
									"minecraft:flat",
									(Supplier)() -> DSL.optionalFields(
											"settings",
											DSL.optionalFields("biome", TypeReferences.BIOME.in(schema), "layers", DSL.list(DSL.optionalFields("block", TypeReferences.BLOCK_NAME.in(schema))))
										),
									"minecraft:noise",
									(Supplier)() -> DSL.optionalFields(
											"biome_source",
											DSL.taggedChoiceLazy(
												"type",
												DSL.string(),
												ImmutableMap.of(
													"minecraft:fixed",
													(Supplier)() -> DSL.fields("biome", TypeReferences.BIOME.in(schema)),
													"minecraft:multi_noise",
													(Supplier)() -> DSL.list(DSL.fields("biome", TypeReferences.BIOME.in(schema))),
													"minecraft:checkerboard",
													(Supplier)() -> DSL.fields("biomes", DSL.list(TypeReferences.BIOME.in(schema))),
													"minecraft:vanilla_layered",
													DSL::remainder,
													"minecraft:the_end",
													DSL::remainder
												)
											),
											"settings",
											DSL.or(
												DSL.constType(DSL.string()),
												DSL.optionalFields("default_block", TypeReferences.BLOCK_NAME.in(schema), "default_fluid", TypeReferences.BLOCK_NAME.in(schema))
											)
										)
								)
							)
						)
					)
				)
		);
	}
}
