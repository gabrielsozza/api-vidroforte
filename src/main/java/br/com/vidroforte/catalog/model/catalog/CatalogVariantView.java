package br.com.vidroforte.catalog.model.catalog;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "catalog_variant_view",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_catalog_variant_view", 
        columnNames = {"variant_id", "view_id"}
    )
)
public class CatalogVariantView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private CatalogVariant variant;

    @Column(name = "view_id", nullable = false, length = 64)
    private String viewId;

    @Column(name = "view_name", nullable = false, length = 100)
    private String viewName;

    @Column(name = "view_image", length = 500)
    private String viewImage;

    @OneToMany(mappedBy = "variantView", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CatalogVariantHotspot> hotspots = new ArrayList<>();

    // Getters e Setters
    public Long getId() { return id; }

    public CatalogVariant getVariant() { return variant; }
    public void setVariant(CatalogVariant variant) { this.variant = variant; }

    public String getViewId() { return viewId; }
    public void setViewId(String viewId) { this.viewId = viewId; }

    public String getViewName() { return viewName; }
    public void setViewName(String viewName) { this.viewName = viewName; }

    public String getViewImage() { return viewImage; }
    public void setViewImage(String viewImage) { this.viewImage = viewImage; }

    public List<CatalogVariantHotspot> getHotspots() { return hotspots; }
    public void setHotspots(List<CatalogVariantHotspot> hotspots) { this.hotspots = hotspots; }
}
