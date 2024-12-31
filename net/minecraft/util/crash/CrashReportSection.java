package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CrashReportSection {
	private final CrashReport report;
	private final String title;
	private final List<CrashReportSection.Element> elements = Lists.newArrayList();
	private StackTraceElement[] stackTrace = new StackTraceElement[0];

	public CrashReportSection(CrashReport crashReport, String string) {
		this.report = crashReport;
		this.title = string;
	}

	public static String createPositionString(double d, double e, double f) {
		return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d, e, f, createPositionString(new BlockPos(d, e, f)));
	}

	public static String createPositionString(BlockPos blockPos) {
		return createPositionString(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	public static String createPositionString(int i, int j, int k) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
		} catch (Throwable var16) {
			stringBuilder.append("(Error finding world loc)");
		}

		stringBuilder.append(", ");

		try {
			int l = i >> 4;
			int m = k >> 4;
			int n = i & 15;
			int o = j >> 4;
			int p = k & 15;
			int q = l << 4;
			int r = m << 4;
			int s = (l + 1 << 4) - 1;
			int t = (m + 1 << 4) - 1;
			stringBuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", n, o, p, l, m, q, r, s, t));
		} catch (Throwable var15) {
			stringBuilder.append("(Error finding chunk loc)");
		}

		stringBuilder.append(", ");

		try {
			int u = i >> 9;
			int v = k >> 9;
			int w = u << 5;
			int x = v << 5;
			int y = (u + 1 << 5) - 1;
			int z = (v + 1 << 5) - 1;
			int aa = u << 9;
			int ab = v << 9;
			int ac = (u + 1 << 9) - 1;
			int ad = (v + 1 << 9) - 1;
			stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", u, v, w, x, y, z, aa, ab, ac, ad));
		} catch (Throwable var14) {
			stringBuilder.append("(Error finding world loc)");
		}

		return stringBuilder.toString();
	}

	public CrashReportSection add(String string, CrashCallable<String> crashCallable) {
		try {
			this.add(string, crashCallable.call());
		} catch (Throwable var4) {
			this.add(string, var4);
		}

		return this;
	}

	public CrashReportSection add(String string, Object object) {
		this.elements.add(new CrashReportSection.Element(string, object));
		return this;
	}

	public void add(String string, Throwable throwable) {
		this.add(string, (Object)throwable);
	}

	public int trimStackTrace(int i) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length <= 0) {
			return 0;
		} else {
			this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - i];
			System.arraycopy(stackTraceElements, 3 + i, this.stackTrace, 0, this.stackTrace.length);
			return this.stackTrace.length;
		}
	}

	public boolean method_584(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
		if (this.stackTrace.length != 0 && stackTraceElement != null) {
			StackTraceElement stackTraceElement3 = this.stackTrace[0];
			if (stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod()
				&& stackTraceElement3.getClassName().equals(stackTraceElement.getClassName())
				&& stackTraceElement3.getFileName().equals(stackTraceElement.getFileName())
				&& stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName())) {
				if (stackTraceElement2 != null != this.stackTrace.length > 1) {
					return false;
				} else if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
					return false;
				} else {
					this.stackTrace[0] = stackTraceElement;
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void method_580(int i) {
		StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - i];
		System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
		this.stackTrace = stackTraceElements;
	}

	public void addStackTrace(StringBuilder stringBuilder) {
		stringBuilder.append("-- ").append(this.title).append(" --\n");
		stringBuilder.append("Details:");

		for (CrashReportSection.Element element : this.elements) {
			stringBuilder.append("\n\t");
			stringBuilder.append(element.getName());
			stringBuilder.append(": ");
			stringBuilder.append(element.getDetail());
		}

		if (this.stackTrace != null && this.stackTrace.length > 0) {
			stringBuilder.append("\nStacktrace:");

			for (StackTraceElement stackTraceElement : this.stackTrace) {
				stringBuilder.append("\n\tat ");
				stringBuilder.append(stackTraceElement);
			}
		}
	}

	public StackTraceElement[] getStackTrace() {
		return this.stackTrace;
	}

	public static void addBlockInfo(CrashReportSection crashReportSection, BlockPos blockPos, @Nullable BlockState blockState) {
		if (blockState != null) {
			crashReportSection.add("Block", blockState::toString);
		}

		crashReportSection.add("Block location", (CrashCallable<String>)(() -> createPositionString(blockPos)));
	}

	static class Element {
		private final String name;
		private final String detail;

		public Element(String string, Object object) {
			this.name = string;
			if (object == null) {
				this.detail = "~~NULL~~";
			} else if (object instanceof Throwable) {
				Throwable throwable = (Throwable)object;
				this.detail = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
			} else {
				this.detail = object.toString();
			}
		}

		public String getName() {
			return this.name;
		}

		public String getDetail() {
			return this.detail;
		}
	}
}
