package br.com.vidroforte.catalog.model.catalog;

import jakarta.persistence.*;

@Entity
@Table(
    name = "catalog_view",
    uniqueConstraints = @UniqueConstraint(name = "uk_catalog_view_kit_key", columnNames = {"kit_id", "view_key"})
)
public class CatalogView {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "kit_id", nullable = false)
  private CatalogKit kit;

  @Enumerated(EnumType.STRING)
  @Column(name = "view_key", nullable = false, length = 1)
  private CatalogViewKey viewKey;

  @Column(name = "image_path", nullable = false, length = 255)
  private String imagePath;

  public Long getId() { return id; }

  public CatalogKit getKit() { return kit; }
  public void setKit(CatalogKit kit) { this.kit = kit; }

  public CatalogViewKey getViewKey() { return viewKey; }
  public void setViewKey(CatalogViewKey viewKey) { this.viewKey = viewKey; }

  public String getImagePath() { return imagePath; }
  public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
