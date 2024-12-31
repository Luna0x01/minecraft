package net.minecraft.client.realms.gui.screen;

public class UploadResult {
	public final int statusCode;
	public final String errorMessage;

	private UploadResult(int statusCode, String errorMessage) {
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}

	public static class Builder {
		private int statusCode = -1;
		private String errorMessage;

		public UploadResult.Builder withStatusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		public UploadResult.Builder withErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}

		public UploadResult build() {
			return new UploadResult(this.statusCode, this.errorMessage);
		}
	}
}
