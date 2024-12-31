package net.minecraft.client;

public interface TooltipContext {
	boolean isAdvanced();

	public static enum TooltipType implements TooltipContext {
		NORMAL(false),
		ADVANCED(true);

		final boolean advanced;

		private TooltipType(boolean bl) {
			this.advanced = bl;
		}

		@Override
		public boolean isAdvanced() {
			return this.advanced;
		}
	}
}
