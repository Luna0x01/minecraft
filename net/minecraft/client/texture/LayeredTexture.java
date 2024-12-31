package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_4277;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public final List<String> locations;

	public LayeredTexture(String... strings) {
		this.locations = Lists.newArrayList(strings);
		if (this.locations.isEmpty()) {
			throw new IllegalStateException("Layered texture with no layers.");
		}
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		Iterator<String> iterator = this.locations.iterator();
		String string = (String)iterator.next();

		try {
			Resource resource = manager.getResource(new Identifier(string));
			Throwable var5 = null;

			try (class_4277 lv = class_4277.method_19472(resource.getInputStream())) {
				while (true) {
					if (!iterator.hasNext()) {
						TextureUtil.prepareImage(this.getGlId(), lv.method_19458(), lv.method_19478());
						lv.method_19466(0, 0, 0, false);
						break;
					}

					String string2 = (String)iterator.next();
					if (string2 != null) {
						Resource resource2 = manager.getResource(new Identifier(string2));
						Throwable var10 = null;

						try (class_4277 lv2 = class_4277.method_19472(resource2.getInputStream())) {
							for (int i = 0; i < lv2.method_19478(); i++) {
								for (int j = 0; j < lv2.method_19458(); j++) {
									lv.method_19479(j, i, lv2.method_19459(j, i));
								}
							}
						} catch (Throwable var91) {
							var10 = var91;
							throw var91;
						} finally {
							if (resource2 != null) {
								if (var10 != null) {
									try {
										resource2.close();
									} catch (Throwable var87) {
										var10.addSuppressed(var87);
									}
								} else {
									resource2.close();
								}
							}
						}
					}
				}
			} catch (Throwable var95) {
				var5 = var95;
				throw var95;
			} finally {
				if (resource != null) {
					if (var5 != null) {
						try {
							resource.close();
						} catch (Throwable var85) {
							var5.addSuppressed(var85);
						}
					} else {
						resource.close();
					}
				}
			}
		} catch (IOException var97) {
			LOGGER.error("Couldn't load layered image", var97);
		}
	}
}
