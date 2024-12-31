package net.minecraft.client.item;

public interface TooltipContext {
	boolean isAdvanced();

	public static enum Default implements TooltipContext {
		field_8934(false),
		field_8935(true);

		private final boolean advanced;

		private Default(boolean bl) {
			this.advanced = bl;
		}

		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}
	}
}
