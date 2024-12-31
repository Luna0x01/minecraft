package net.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resource.AssetsIndex;
import net.minecraft.util.Identifier;

public class class_4285 extends class_4456 {
	private final AssetsIndex field_21040;

	public class_4285(AssetsIndex assetsIndex) {
		super("minecraft", "realms");
		this.field_21040 = assetsIndex;
	}

	@Nullable
	@Override
	protected InputStream method_21334(class_4455 arg, Identifier identifier) {
		if (arg == class_4455.CLIENT_RESOURCES) {
			File file = this.field_21040.method_12496(identifier);
			if (file != null && file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException var5) {
				}
			}
		}

		return super.method_21334(arg, identifier);
	}

	@Nullable
	@Override
	protected InputStream method_21333(String string) {
		File file = this.field_21040.method_19535(string);
		if (file != null && file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException var4) {
			}
		}

		return super.method_21333(string);
	}

	@Override
	public Collection<Identifier> method_21328(class_4455 arg, String string, int i, Predicate<String> predicate) {
		Collection<Identifier> collection = super.method_21328(arg, string, i, predicate);
		collection.addAll((Collection)this.field_21040.method_19536(string, i, predicate).stream().map(Identifier::new).collect(Collectors.toList()));
		return collection;
	}
}
