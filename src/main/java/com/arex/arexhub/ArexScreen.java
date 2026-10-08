package com.arex.arexhub;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.List;

public class ArexScreen extends Screen {
    static final int BG = 0xFF150826, PANEL = 0xFF241040, PANEL2 = 0xFF3A1D68, HOVER = 0xFF5B2E9E;
    static final int ACCENT = 0xFF9D4EDD, LIGHT = 0xFFC77DFF, TEXT = 0xFFEDE0FF, DIM = 0xFF9A86B8;
    static final int COLS = 6, CELL = 36, ICON = 28;

    private Category cat = Category.SWORD;
    private List<Catalog.Entry> entries = List.of();
    private int scroll = 0;
    private String status = "";

    private int gridX, gridY, gridH, visibleRows;

    public ArexScreen() {
        super(Text.literal("Arexhub"));
    }

    @Override
    protected void init() {
        switchTab(cat);
    }

    private void switchTab(Category c) {
        cat = c;
        scroll = 0;
        entries = Catalog.list(c);
        Preview.update(Selection.ALL.get(c));
    }

    private void layout() {
        gridX = width - COLS * CELL - 16;
        gridY = 40;
        gridH = height - 40 - 54;
        visibleRows = Math.max(1, gridH / CELL);
    }

    private int totalRows() {
        return (entries.size() + COLS - 1) / COLS;
    }

    private int tabX(int i) { return 12 + i * 76; }

    private int dlX() { return (width - 120) / 2; }
    private int dlY() { return height - 30; }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        // no vanilla blur
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        layout();
        ctx.fill(0, 0, width, height, BG);

        // title + tabs
        ctx.drawText(textRenderer, "AREXHUB", width - 12 - textRenderer.getWidth("AREXHUB"), 14, LIGHT, true);
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            int x = tabX(i);
            boolean sel = cats[i] == cat;
            boolean hov = mx >= x && mx < x + 72 && my >= 8 && my < 30;
            ctx.fill(x, 8, x + 72, 30, sel ? ACCENT : (hov ? HOVER : PANEL2));
            if (sel) ctx.fill(x, 28, x + 72, 30, LIGHT);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(cats[i].label), x + 36, 15, TEXT);
        }

        // left preview panel
        int px1 = 12, px2 = gridX - 12, py1 = 40, py2 = height - 54;
        ctx.fill(px1, py1, px2, py2, PANEL);
        Selection sel = Selection.ALL.get(cat);
        int cx = (px1 + px2) / 2;
        if (sel != null && Preview.ready) {
            int s = Math.min(128, Math.min(px2 - px1 - 24, py2 - py1 - 60));
            s = Math.max(32, s);
            ctx.fill(cx - s / 2 - 4, py1 + 16, cx + s / 2 + 4, py1 + 16 + s + 8, 0xFF1A0B2E);
            ctx.drawTexture(RenderLayer::getGuiTextured, Preview.ID, cx - s / 2, py1 + 20, 0f, 0f, s, s,
                    Preview.size, Preview.size, Preview.size, Preview.size);
            String name = sel.texture.getPath().substring(sel.texture.getPath().lastIndexOf('/') + 1).replace(".png", "");
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(name), cx, py1 + s + 36, TEXT);
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Replaces: " + String.join(", ", cat.targets)), cx, py1 + s + 50, DIM);
        } else {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Select a texture"), cx, (py1 + py2) / 2, DIM);
        }
        if (!status.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(status), cx, py2 - 14, LIGHT);
        }

        // grid
        int maxScroll = Math.max(0, totalRows() - visibleRows);
        scroll = Math.max(0, Math.min(scroll, maxScroll));
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = (scroll + row) * COLS + col;
                if (idx >= entries.size()) continue;
                Catalog.Entry en = entries.get(idx);
                int x = gridX + col * CELL;
                int y = gridY + row * CELL;
                boolean hov = mx >= x && mx < x + CELL && my >= y && my < y + CELL;
                boolean chosen = sel != null && sel.texture.equals(en.id());
                ctx.fill(x + 1, y + 1, x + CELL - 1, y + CELL - 1, chosen ? PANEL2 : (hov ? HOVER : PANEL));
                if (chosen) {
                    ctx.fill(x + 1, y + 1, x + CELL - 1, y + 2, LIGHT);
                    ctx.fill(x + 1, y + CELL - 2, x + CELL - 1, y + CELL - 1, LIGHT);
                    ctx.fill(x + 1, y + 1, x + 2, y + CELL - 1, LIGHT);
                    ctx.fill(x + CELL - 2, y + 1, x + CELL - 1, y + CELL - 1, LIGHT);
                }
                ctx.drawTexture(RenderLayer::getGuiTextured, en.id(), x + (CELL - ICON) / 2, y + (CELL - ICON) / 2,
                        0f, 0f, ICON, ICON, en.size(), en.size(), en.size(), en.size());
            }
        }
        if (entries.isEmpty()) {
            ctx.drawText(textRenderer, "No textures in this tab", gridX, gridY + 4, DIM, false);
        }
        if (maxScroll > 0) {
            int trackX = gridX + COLS * CELL + 4;
            int trackH = visibleRows * CELL;
            ctx.fill(trackX, gridY, trackX + 4, gridY + trackH, PANEL);
            int thumbH = Math.max(12, trackH * visibleRows / totalRows());
            int thumbY = gridY + (trackH - thumbH) * scroll / maxScroll;
            ctx.fill(trackX, thumbY, trackX + 4, thumbY + thumbH, ACCENT);
        }

        // hint + download
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Click LMB to select, RMB to edit, then DOWNLOAD"), width / 2, height - 44, DIM);
        boolean dh = mx >= dlX() && mx < dlX() + 120 && my >= dlY() && my < dlY() + 20;
        ctx.fill(dlX(), dlY(), dlX() + 120, dlY() + 20, dh ? LIGHT : ACCENT);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("DOWNLOAD"), dlX() + 60, dlY() + 6, 0xFFFFFFFF);

        super.render(ctx, mx, my, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        layout();
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            int x = tabX(i);
            if (mx >= x && mx < x + 72 && my >= 8 && my < 30) {
                switchTab(cats[i]);
                return true;
            }
        }

        if (mx >= dlX() && mx < dlX() + 120 && my >= dlY() && my < dlY() + 20) {
            if (Selection.ALL.isEmpty()) {
                status = "Pick at least one texture first";
            } else {
                try {
                    Path p = PackBuilder.build();
                    status = "Saved: resourcepacks/" + p.getFileName() + " (enable it in Resource Packs)";
                } catch (Exception e) {
                    e.printStackTrace();
                    status = "Error: " + e.getMessage();
                }
            }
            return true;
        }

        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = (scroll + row) * COLS + col;
                if (idx >= entries.size()) continue;
                int x = gridX + col * CELL;
                int y = gridY + row * CELL;
                if (mx >= x && mx < x + CELL && my >= y && my < y + CELL) {
                    Catalog.Entry en = entries.get(idx);
                    Selection cur = Selection.ALL.get(cat);
                    if (cur == null || !cur.texture.equals(en.id())) {
                        Selection.ALL.put(cat, new Selection(en.id()));
                    }
                    Preview.update(Selection.ALL.get(cat));
                    status = "";
                    if (button == 1) {
                        client.setScreen(new EditScreen(this, cat));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        scroll -= (int) Math.signum(vertical);
        return true;
    }

    /** Called when coming back from the edit screen. */
    void refreshPreview() {
        Preview.update(Selection.ALL.get(cat));
    }
}
