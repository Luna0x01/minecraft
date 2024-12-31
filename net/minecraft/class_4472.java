package net.minecraft;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.GenericScoreboardCriteria;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4472<T> extends GenericScoreboardCriteria {
	private final class_4473 field_22069;
	private final T field_22070;
	private final StatType<T> field_22071;

	protected class_4472(StatType<T> statType, T object, class_4473 arg) {
		super(method_21422(statType, object));
		this.field_22071 = statType;
		this.field_22069 = arg;
		this.field_22070 = object;
	}

	public static <T> String method_21422(StatType<T> statType, T object) {
		return method_21421(Registry.STATS.getId(statType)) + ":" + method_21421(statType.method_21424().getId(object));
	}

	private static <T> String method_21421(@Nullable Identifier identifier) {
		return identifier.toString().replace(':', '.');
	}

	public StatType<T> method_21419() {
		return this.field_22071;
	}

	public T method_21423() {
		return this.field_22070;
	}

	public String method_21420(int i) {
		return this.field_22069.format(i);
	}

	public boolean equals(Object object) {
		return this == object || object instanceof class_4472 && Objects.equals(this.method_4917(), ((class_4472)object).method_4917());
	}

	public int hashCode() {
		return this.method_4917().hashCode();
	}

	public String toString() {
		return "Stat{name=" + this.method_4917() + ", formatter=" + this.field_22069 + '}';
	}
}
