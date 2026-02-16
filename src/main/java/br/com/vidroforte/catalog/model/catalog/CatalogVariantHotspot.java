package br.com.vidroforte.catalog.model.catalog;

import jakarta.persistence.*;

@Entity
@Table(name = "catalog_variant_hotspot")
public class CatalogVariantHotspot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_view_id", nullable = false)
    private CatalogVariantView variantView;

    @Column(name = "glass_id", nullable = false, length = 64)
    private String glassId;

    @Column(nullable = false) private int x;
    @Column(nullable = false) private int y;
    @Column(nullable = false) private int w;
    @Column(nullable = false) private int h;

    // Getters e Setters
    public Long getId() { return id; }

    public CatalogVariantView getVariantView() { return variantView; }
    public void setVariantView(CatalogVariantView variantView) { this.variantView = variantView; }

    public String getGlassId() { return glassId; }
    public void setGlassId(String glassId) { this.glassId = glassId; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getW() { return w; }
    public void setW(int w) { this.w = w; }

    public int getH() { return h; }
    public void setH(int h) { this.h = h; }
}
