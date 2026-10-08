package com.arex.arexhub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Writes .minecraft/resourcepacks/Arexhub.zip with every selected texture. */
public class PackBuilder {
    public static Path build() throws IOException {
        Path dir = MinecraftClient.getInstance().runDirectory.toPath().resolve("resourcepacks");
        Files.createDirectories(dir);
        Path zip = dir.resolve("Arexhub.zip");

        try (OutputStream os = Files.newOutputStream(zip); ZipOutputStream z = new ZipOutputStream(os)) {
            z.putNextEntry(new ZipEntry("pack.mcmeta"));
            z.write("{\"pack\":{\"pack_format\":46,\"description\":\"Arexhub pack\"}}".getBytes(StandardCharsets.UTF_8));
            z.closeEntry();

            for (Map.Entry<Category, Selection> e : Selection.ALL.entrySet()) {
                Selection sel = e.getValue();
                byte[] png = toPng(sel);
                for (String target : e.getKey().targets) {
                    z.putNextEntry(new ZipEntry("assets/minecraft/textures/item/" + target + ".png"));
                    z.write(png);
                    z.closeEntry();
                }
            }
        }
        return zip;
    }

    private static byte[] toPng(Selection sel) throws IOException {
        Path tmp = Files.createTempFile("arexhub", ".png");
        try (NativeImage src = TextureUtil.load(sel.texture);
             NativeImage out = TextureUtil.tint(src, sel.r, sel.g, sel.b)) {
            out.writeTo(tmp);
            return Files.readAllBytes(tmp);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
