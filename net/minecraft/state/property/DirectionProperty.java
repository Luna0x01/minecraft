package net.minecraft.state.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.util.math.Direction;

public class DirectionProperty extends EnumProperty<Direction> {
	protected DirectionProperty(String string, Collection<Direction> collection) {
		super(string, Direction.class, collection);
	}

	public static DirectionProperty of(String name) {
		return of(name, Predicates.alwaysTrue());
	}

	public static DirectionProperty of(String name, Predicate<Direction> directionPredicate) {
		return of(name, Collections2.filter(Lists.newArrayList(Direction.values()), directionPredicate));
	}

	public static DirectionProperty of(String name, Collection<Direction> values) {
		return new DirectionProperty(name, values);
	}
}
