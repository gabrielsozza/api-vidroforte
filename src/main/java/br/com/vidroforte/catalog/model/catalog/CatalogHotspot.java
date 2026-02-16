package br.com.vidroforte.catalog.model.catalog;

import jakarta.persistence.*;

@Entity
@Table(
    name = "catalog_hotspot",
    uniqueConstraints = @UniqueConstraint(name = "uk_catalog_hotspot_view_glass", columnNames = {"view_id", "glass_id"})
)
public class CatalogHotspot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "view_id", nullable = false)
  private CatalogView view;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "glass_id", nullable = false)
  private CatalogGlass glass;

  @Column(nullable = false) private int x;
  @Column(nullable = false) private int y;
  @Column(nullable = false) private int w;
  @Column(nullable = false) private int h;

  public Long getId() { return id; }

  public CatalogView getView() { return view; }
  public void setView(CatalogView view) { this.view = view; }

  public CatalogGlass getGlass() { return glass; }
  public void setGlass(CatalogGlass glass) { this.glass = glass; }

  public int getX() { return x; }
  public void setX(int x) { this.x = x; }

  public int getY() { return y; }
  public void setY(int y) { this.y = y; }

  public int getW() { return w; }
  public void setW(int w) { this.w = w; }

  public int getH() { return h; }
  public void setH(int h) { this.h = h; }
}
