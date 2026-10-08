package com.arex.arexhub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Finds every PNG under assets/arexhub/textures/packs/<folder>/ . Drop new PNGs there and they show up. */
public class Catalog {
    public record Entry(Identifier id, String name, int size) {}

    public static List<Entry> list(Category cat) {
        List<Entry> out = new ArrayList<>();
        Map<Identifier, Resource> found = MinecraftClient.getInstance().getResourceManager().findResources(
                "textures/packs/" + cat.folder,
                id -> id.getNamespace().equals("arexhub") && id.getPath().endsWith(".png"));

        for (Map.Entry<Identifier, Resource> e : found.entrySet()) {
            int size = 16;
            try (InputStream in = e.getValue().getInputStream(); NativeImage img = NativeImage.read(in)) {
                size = img.getWidth();
            } catch (Exception ignored) {
            }
            String path = e.getKey().getPath();
            String name = path.substring(path.lastIndexOf('/') + 1).replace(".png", "");
            out.add(new Entry(e.getKey(), name, size));
        }
        out.sort(Comparator.comparing(en -> en.id().getPath()));
        return out;
    }
}
