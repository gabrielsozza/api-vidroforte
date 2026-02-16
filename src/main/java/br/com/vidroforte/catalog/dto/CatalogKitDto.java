package br.com.vidroforte.catalog.dto;

import java.util.List;
import java.util.Map;

@Deprecated // legado (mock in-memory). Prefira CatalogDtos.KitDto no fluxo com banco.
public class CatalogKitDto {
  public String id;
  public String vehicleLabel;
  public String defaultImage;
  public KitInfo kit;
  public List<Glass> glasses;
  public Map<String, View> views;

  public static class KitInfo {
    public String code;
    public String desc;
  }

  public static class Glass {
    public String id;
    public String model;
    public String code;
    public String desc;
    public String position;
    public String side;
    public String dim;
    public List<String> options;
  }

  public static class View {
    public String image;
    public Map<String, Box> coordsPxByGlassId;
  }

  public static class Box {
    public int x;
    public int y;
    public int w;
    public int h;

    public Box() {}
    public Box(int x, int y, int w, int h) { this.x=x; this.y=y; this.w=w; this.h=h; }
  }
}
