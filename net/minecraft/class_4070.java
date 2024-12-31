package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.dimension.DimensionType;

public class class_4070 {
	private final Map<DimensionType, class_4068> field_19776;
	@Nullable
	private final SaveHandler field_19777;

	public class_4070(@Nullable SaveHandler saveHandler) {
		this.field_19777 = saveHandler;
		Builder<DimensionType, class_4068> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.method_17200()) {
			class_4068 lv = new class_4068(dimensionType, saveHandler);
			builder.put(dimensionType, lv);
			lv.method_17937();
		}

		this.field_19776 = builder.build();
	}

	@Nullable
	public <T extends PersistentState> T method_17977(DimensionType dimensionType, Function<String, T> function, String string) {
		return ((class_4068)this.field_19776.get(dimensionType)).method_17942(function, string);
	}

	public void method_17976(DimensionType dimensionType, String string, PersistentState persistentState) {
		((class_4068)this.field_19776.get(dimensionType)).method_17941(string, persistentState);
	}

	public void method_17975() {
		this.field_19776.values().forEach(class_4068::method_17943);
	}

	public int method_270(DimensionType dimensionType, String string) {
		return ((class_4068)this.field_19776.get(dimensionType)).method_17940(string);
	}

	public NbtCompound method_17978(String string, int i) throws IOException {
		return class_4068.method_17939(this.field_19777, DimensionType.OVERWORLD, string, i);
	}
}
