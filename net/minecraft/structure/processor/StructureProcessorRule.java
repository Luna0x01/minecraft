package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.structure.rule.AbstractRuleTest;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public class StructureProcessorRule {
	private final AbstractRuleTest inputPredicate;
	private final AbstractRuleTest locationPredicate;
	private final BlockState outputState;
	@Nullable
	private final CompoundTag tag;

	public StructureProcessorRule(AbstractRuleTest abstractRuleTest, AbstractRuleTest abstractRuleTest2, BlockState blockState) {
		this(abstractRuleTest, abstractRuleTest2, blockState, null);
	}

	public StructureProcessorRule(AbstractRuleTest abstractRuleTest, AbstractRuleTest abstractRuleTest2, BlockState blockState, @Nullable CompoundTag compoundTag) {
		this.inputPredicate = abstractRuleTest;
		this.locationPredicate = abstractRuleTest2;
		this.outputState = blockState;
		this.tag = compoundTag;
	}

	public boolean test(BlockState blockState, BlockState blockState2, Random random) {
		return this.inputPredicate.test(blockState, random) && this.locationPredicate.test(blockState2, random);
	}

	public BlockState getOutputState() {
		return this.outputState;
	}

	@Nullable
	public CompoundTag getTag() {
		return this.tag;
	}

	public <T> Dynamic<T> method_16764(DynamicOps<T> dynamicOps) {
		T object = (T)dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("input_predicate"),
				this.inputPredicate.method_16767(dynamicOps).getValue(),
				dynamicOps.createString("location_predicate"),
				this.locationPredicate.method_16767(dynamicOps).getValue(),
				dynamicOps.createString("output_state"),
				BlockState.serialize(dynamicOps, this.outputState).getValue()
			)
		);
		return this.tag == null
			? new Dynamic(dynamicOps, object)
			: new Dynamic(
				dynamicOps, dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic(NbtOps.INSTANCE, this.tag).convert(dynamicOps).getValue())
			);
	}

	public static <T> StructureProcessorRule method_16765(Dynamic<T> dynamic) {
		Dynamic<T> dynamic2 = dynamic.get("input_predicate").orElseEmptyMap();
		Dynamic<T> dynamic3 = dynamic.get("location_predicate").orElseEmptyMap();
		AbstractRuleTest abstractRuleTest = DynamicDeserializer.deserialize(dynamic2, Registry.field_16792, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
		AbstractRuleTest abstractRuleTest2 = DynamicDeserializer.deserialize(dynamic3, Registry.field_16792, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
		BlockState blockState = BlockState.deserialize(dynamic.get("output_state").orElseEmptyMap());
		CompoundTag compoundTag = (CompoundTag)dynamic.get("output_nbt").map(dynamicx -> (Tag)dynamicx.convert(NbtOps.INSTANCE).getValue()).orElse(null);
		return new StructureProcessorRule(abstractRuleTest, abstractRuleTest2, blockState, compoundTag);
	}
}
