package net.minecraft.entity;

import java.util.UUID;
import javax.annotation.Nullable;

public interface Tameable {
	@Nullable
	UUID method_2719();

	@Nullable
	Entity getOwner();
}
