package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.FileNameUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

public abstract class GLImportProcessor {
	private static final String field_32036 = "/\\*(?:[^*]|\\*+[^*/])*\\*+/";
	private static final String field_33620 = "//[^\\v]*";
	private static final Pattern MOJ_IMPORT_PATTERN = Pattern.compile(
		"(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*moj_import(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(?:\"(.*)\"|<(.*)>))"
	);
	private static final Pattern IMPORT_VERSION_PATTERN = Pattern.compile(
		"(#(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*version(?:/\\*(?:[^*]|\\*+[^*/])*\\*+/|\\h)*(\\d+))\\b"
	);
	private static final Pattern field_33621 = Pattern.compile("(?:^|\\v)(?:\\s|/\\*(?:[^*]|\\*+[^*/])*\\*+/|(//[^\\v]*))*\\z");

	public List<String> readSource(String source) {
		GLImportProcessor.Context context = new GLImportProcessor.Context();
		List<String> list = this.parseImports(source, context, "");
		list.set(0, this.readImport((String)list.get(0), context.column));
		return list;
	}

	private List<String> parseImports(String source, GLImportProcessor.Context context, String path) {
		int i = context.line;
		int j = 0;
		String string = "";
		List<String> list = Lists.newArrayList();
		Matcher matcher = MOJ_IMPORT_PATTERN.matcher(source);

		while (matcher.find()) {
			if (!method_36424(source, matcher, j)) {
				String string2 = matcher.group(2);
				boolean bl = string2 != null;
				if (!bl) {
					string2 = matcher.group(3);
				}

				if (string2 != null) {
					String string3 = source.substring(j, matcher.start(1));
					String string4 = path + string2;
					String string5 = this.loadImport(bl, string4);
					if (!Strings.isEmpty(string5)) {
						if (!ChatUtil.endsWithLineBreak(string5)) {
							string5 = string5 + System.lineSeparator();
						}

						context.line++;
						int k = context.line;
						List<String> list2 = this.parseImports(string5, context, bl ? FileNameUtil.getPosixFullPath(string4) : "");
						list2.set(0, String.format(Locale.ROOT, "#line %d %d\n%s", 0, k, this.extractVersion((String)list2.get(0), context)));
						if (!StringUtils.isBlank(string3)) {
							list.add(string3);
						}

						list.addAll(list2);
					} else {
						String string6 = bl ? String.format("/*#moj_import \"%s\"*/", string2) : String.format("/*#moj_import <%s>*/", string2);
						list.add(string + string3 + string6);
					}

					int l = ChatUtil.countLines(source.substring(0, matcher.end(1)));
					string = String.format(Locale.ROOT, "#line %d %d", l, i);
					j = matcher.end(1);
				}
			}
		}

		String string7 = source.substring(j);
		if (!StringUtils.isBlank(string7)) {
			list.add(string + string7);
		}

		return list;
	}

	private String extractVersion(String line, GLImportProcessor.Context context) {
		Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
		if (matcher.find() && method_36423(line, matcher)) {
			context.column = Math.max(context.column, Integer.parseInt(matcher.group(2)));
			return line.substring(0, matcher.start(1)) + "/*" + line.substring(matcher.start(1), matcher.end(1)) + "*/" + line.substring(matcher.end(1));
		} else {
			return line;
		}
	}

	private String readImport(String line, int start) {
		Matcher matcher = IMPORT_VERSION_PATTERN.matcher(line);
		return matcher.find() && method_36423(line, matcher)
			? line.substring(0, matcher.start(2)) + Math.max(start, Integer.parseInt(matcher.group(2))) + line.substring(matcher.end(2))
			: line;
	}

	private static boolean method_36423(String string, Matcher matcher) {
		return !method_36424(string, matcher, 0);
	}

	private static boolean method_36424(String string, Matcher matcher, int i) {
		int j = matcher.start() - i;
		if (j == 0) {
			return false;
		} else {
			Matcher matcher2 = field_33621.matcher(string.substring(i, matcher.start()));
			if (!matcher2.find()) {
				return true;
			} else {
				int k = matcher2.end(1);
				return k == matcher.start();
			}
		}
	}

	@Nullable
	public abstract String loadImport(boolean inline, String name);

	static final class Context {
		int column;
		int line;
	}
}
