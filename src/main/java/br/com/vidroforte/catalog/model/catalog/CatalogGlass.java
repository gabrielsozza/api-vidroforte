package br.com.vidroforte.catalog.model.catalog;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "catalog_glass")
public class CatalogGlass {

  @Id
  @Column(length = 64)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "kit_id", nullable = false)
  private CatalogKit kit;

  @Column(nullable = false, length = 255)
  private String model;

  @Column(nullable = false, length = 40)
  private String code;

  @Column(name = "glass_desc", nullable = false, length = 255)
  private String glassDesc;

  @Column(nullable = false, length = 40)
  private String position;

  @Column(nullable = false, length = 4)
  private String side;

  @Column(nullable = false, length = 40)
  private String dim;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder = 0;

  @ElementCollection
  @CollectionTable(
      name = "catalog_glass_option",
      joinColumns = @JoinColumn(name = "glass_id")
  )
  @Column(name = "opt", nullable = false, length = 40)
  private Set<String> options = new LinkedHashSet<>();

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public CatalogKit getKit() { return kit; }
  public void setKit(CatalogKit kit) { this.kit = kit; }

  public String getModel() { return model; }
  public void setModel(String model) { this.model = model; }

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public String getGlassDesc() { return glassDesc; }
  public void setGlassDesc(String glassDesc) { this.glassDesc = glassDesc; }

  public String getPosition() { return position; }
  public void setPosition(String position) { this.position = position; }

  public String getSide() { return side; }
  public void setSide(String side) { this.side = side; }

  public String getDim() { return dim; }
  public void setDim(String dim) { this.dim = dim; }

  public int getSortOrder() { return sortOrder; }
  public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

  public Set<String> getOptions() { return options; }
  public void setOptions(Set<String> options) { this.options = options; }
}
