package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
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

	public static String createPositionString(double x, double y, double z) {
		return String.format("%.2f,%.2f,%.2f - %s", x, y, z, addBlockData(new BlockPos(x, y, z)));
	}

	public static String addBlockData(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		StringBuilder stringBuilder = new StringBuilder();

		try {
			stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
		} catch (Throwable var17) {
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
		} catch (Throwable var16) {
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
		} catch (Throwable var15) {
			stringBuilder.append("(Error finding world loc)");
		}

		return stringBuilder.toString();
	}

	public void add(String name, Callable<String> value) {
		try {
			this.add(name, value.call());
		} catch (Throwable var4) {
			this.add(name, var4);
		}
	}

	public void add(String name, Object detail) {
		this.elements.add(new CrashReportSection.Element(name, detail));
	}

	public void add(String name, Throwable throwable) {
		this.add(name, (Object)throwable);
	}

	public int initStackTrace(int ignoredCallCount) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		if (stackTraceElements.length <= 0) {
			return 0;
		} else {
			this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - ignoredCallCount];
			System.arraycopy(stackTraceElements, 3 + ignoredCallCount, this.stackTrace, 0, this.stackTrace.length);
			return this.stackTrace.length;
		}
	}

	public boolean method_4426(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
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

	public void trimStackTraceEnd(int callCount) {
		StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - callCount];
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
				stringBuilder.append(stackTraceElement.toString());
			}
		}
	}

	public StackTraceElement[] getStackTrace() {
		return this.stackTrace;
	}

	public static void addBlockData(CrashReportSection section, BlockPos pos, Block block, int i) {
		final int j = Block.getIdByBlock(block);
		section.add("Block type", new Callable<String>() {
			public String call() throws Exception {
				try {
					return String.format("ID #%d (%s // %s)", j, block.getTranslationKey(), block.getClass().getCanonicalName());
				} catch (Throwable var2) {
					return "ID #" + j;
				}
			}
		});
		section.add("Block data value", new Callable<String>() {
			public String call() throws Exception {
				if (i < 0) {
					return "Unknown? (Got " + i + ")";
				} else {
					String string = String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
					return String.format("%1$d / 0x%1$X / 0b%2$s", i, string);
				}
			}
		});
		section.add("Block location", new Callable<String>() {
			public String call() throws Exception {
				return CrashReportSection.addBlockData(pos);
			}
		});
	}

	public static void addBlockInfo(CrashReportSection element, BlockPos pos, BlockState state) {
		element.add("Block", new Callable<String>() {
			public String call() throws Exception {
				return state.toString();
			}
		});
		element.add("Block location", new Callable<String>() {
			public String call() throws Exception {
				return CrashReportSection.addBlockData(pos);
			}
		});
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
