package br.com.vidroforte.catalog.model.catalog;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "catalog_variant",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_catalog_variant_kit_id", 
        columnNames = {"kit_id", "variant_id"}
    )
)
public class CatalogVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kit_id", nullable = false)
    private CatalogKit kit;

    @Column(name = "variant_id", nullable = false, length = 64)
    private String variantId;

    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName;

    @Column(name = "presentation_image", length = 500)
    private String presentationImage;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CatalogVariantView> views = new ArrayList<>();

    // Getters e Setters
    public Long getId() { return id; }

    public CatalogKit getKit() { return kit; }
    public void setKit(CatalogKit kit) { this.kit = kit; }

    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getPresentationImage() { return presentationImage; }
    public void setPresentationImage(String presentationImage) { this.presentationImage = presentationImage; }

    public List<CatalogVariantView> getViews() { return views; }
    public void setViews(List<CatalogVariantView> views) { this.views = views; }
}
