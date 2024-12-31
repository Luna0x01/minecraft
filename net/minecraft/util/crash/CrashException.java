package net.minecraft.util.crash;

public class CrashException extends RuntimeException {
	private final CrashReport crashReport;

	public CrashException(CrashReport crashReport) {
		this.crashReport = crashReport;
	}

	public CrashReport getReport() {
		return this.crashReport;
	}

	public Throwable getCause() {
		return this.crashReport.getCause();
	}

	public String getMessage() {
		return this.crashReport.getMessage();
	}
}
