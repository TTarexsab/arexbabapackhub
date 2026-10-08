package com.arex.arexhub;

/** One tab. "folder" = folder under assets/arexhub/textures/packs/, "targets" = vanilla item textures that get replaced. */
public enum Category {
    SWORD("Sword", "sword", "diamond_sword", "netherite_sword"),
    TOTEM("Totem", "totem", "totem_of_undying"),
    BOW("Bow", "bow", "bow"),
    AXE("Axe", "axe", "diamond_axe", "netherite_axe");

    public final String label;
    public final String folder;
    public final String[] targets;

    Category(String label, String folder, String... targets) {
        this.label = label;
        this.folder = folder;
        this.targets = targets;
    }
}
