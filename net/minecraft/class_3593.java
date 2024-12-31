package net.minecraft;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.util.math.Box;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;

public interface class_3593 {
	List<Entity> method_16288(@Nullable Entity entity, Box box, @Nullable Predicate<? super Entity> predicate);

	default List<Entity> getEntities(@Nullable Entity entity, Box box) {
		return this.method_16288(entity, box, EntityPredicate.field_16705);
	}

	default Stream<VoxelShape> method_16289(@Nullable Entity entity, VoxelShape voxelShape, Set<Entity> set) {
		if (voxelShape.isEmpty()) {
			return Stream.empty();
		} else {
			Box box = voxelShape.getBoundingBox();
			return this.getEntities(entity, box.expand(0.25))
				.stream()
				.filter(entity2 -> !set.contains(entity2) && (entity == null || !entity.isConnectedThroughVehicle(entity2)))
				.flatMap(
					entity2 -> Stream.of(entity2.getBox(), entity == null ? null : entity.getHardCollisionBox(entity2))
							.filter(Objects::nonNull)
							.filter(box2 -> box2.intersects(box))
							.map(VoxelShapes::method_18049)
				);
		}
	}
}
