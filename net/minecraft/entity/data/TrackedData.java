package net.minecraft.entity.data;

public class TrackedData<T> {
	private final int field_13816;
	private final TrackedDataHandler<T> field_13817;

	public TrackedData(int i, TrackedDataHandler<T> trackedDataHandler) {
		this.field_13816 = i;
		this.field_13817 = trackedDataHandler;
	}

	public int method_12713() {
		return this.field_13816;
	}

	public TrackedDataHandler<T> method_12714() {
		return this.field_13817;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			TrackedData<?> trackedData = (TrackedData<?>)object;
			return this.field_13816 == trackedData.field_13816;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_13816;
	}
}
