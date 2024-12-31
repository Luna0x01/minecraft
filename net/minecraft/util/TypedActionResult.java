package net.minecraft.util;

public class TypedActionResult<T> {
	private final ActionResult result;
	private final T value;

	public TypedActionResult(ActionResult actionResult, T object) {
		this.result = actionResult;
		this.value = object;
	}

	public ActionResult getResult() {
		return this.result;
	}

	public T getValue() {
		return this.value;
	}

	public static <T> TypedActionResult<T> success(T object) {
		return new TypedActionResult<>(ActionResult.field_5812, object);
	}

	public static <T> TypedActionResult<T> consume(T object) {
		return new TypedActionResult<>(ActionResult.field_21466, object);
	}

	public static <T> TypedActionResult<T> pass(T object) {
		return new TypedActionResult<>(ActionResult.field_5811, object);
	}

	public static <T> TypedActionResult<T> fail(T object) {
		return new TypedActionResult<>(ActionResult.field_5814, object);
	}
}
