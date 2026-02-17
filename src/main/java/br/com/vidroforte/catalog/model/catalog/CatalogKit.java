package br.com.vidroforte.catalog.model.catalog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalog_kit")
public class CatalogKit {

  @Id
  @Column(length = 64)
  private String id;

  @Column(name = "vehicle_label", nullable = false, length = 120)
  private String vehicleLabel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private CatalogSegment segment = CatalogSegment.CONVENCIONAL;

  @Column(name = "default_image_path", length = 255)
  private String defaultImagePath;

  @Column(name = "kit_code", nullable = false, length = 40)
  private String kitCode;

  @Column(name = "kit_desc", nullable = false, length = 255)
  private String kitDesc;

  @Column(nullable = false)
  private boolean active = true;

  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "kit", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CatalogVariant> variants = new ArrayList<>();

  @Column(name = "updated_at", insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getVehicleLabel() { return vehicleLabel; }
  public void setVehicleLabel(String vehicleLabel) { this.vehicleLabel = vehicleLabel; }

  public CatalogSegment getSegment() { return segment; }
  public void setSegment(CatalogSegment segment) { this.segment = segment; }

  public String getDefaultImagePath() { return defaultImagePath; }
  public void setDefaultImagePath(String defaultImagePath) { this.defaultImagePath = defaultImagePath; }

  public String getKitCode() { return kitCode; }
  public void setKitCode(String kitCode) { this.kitCode = kitCode; }

  public String getKitDesc() { return kitDesc; }
  public void setKitDesc(String kitDesc) { this.kitDesc = kitDesc; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }

  public List<CatalogVariant> getVariants() { return variants; }

  // Importante: não reassigna a lista “do nada”; use clear/addAll no service.
  public void setVariants(List<CatalogVariant> variants) {
    this.variants = (variants != null) ? variants : new ArrayList<>();
  }

  public void addVariant(CatalogVariant v) {
    if (v == null) return;
    this.variants.add(v);
    v.setKit(this);
  }

  public void removeVariant(CatalogVariant v) {
    if (v == null) return;
    this.variants.remove(v);
    v.setKit(null);
  }
}

