package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> {
	public static final TreeDecoratorType<TrunkVineTreeDecorator> field_21320 = register("trunk_vine", TrunkVineTreeDecorator::new);
	public static final TreeDecoratorType<LeaveVineTreeDecorator> field_21321 = register("leave_vine", LeaveVineTreeDecorator::new);
	public static final TreeDecoratorType<CocoaBeansTreeDecorator> field_21322 = register("cocoa", CocoaBeansTreeDecorator::new);
	public static final TreeDecoratorType<BeehiveTreeDecorator> field_21323 = register("beehive", BeehiveTreeDecorator::new);
	public static final TreeDecoratorType<AlterGroundTreeDecorator> field_21324 = register("alter_ground", AlterGroundTreeDecorator::new);
	private final Function<Dynamic<?>, P> field_21325;

	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.field_21448, string, new TreeDecoratorType<>(function));
	}

	private TreeDecoratorType(Function<Dynamic<?>, P> function) {
		this.field_21325 = function;
	}

	public P method_23472(Dynamic<?> dynamic) {
		return (P)this.field_21325.apply(dynamic);
	}
}
