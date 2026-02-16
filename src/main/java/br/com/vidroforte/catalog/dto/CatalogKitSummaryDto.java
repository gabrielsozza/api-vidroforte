package br.com.vidroforte.catalog.dto;

public class CatalogKitSummaryDto {
  public String id;
  public String vehicleLabel;
  public String defaultImage;
  public CatalogKitDto.KitInfo kit;

  public CatalogKitSummaryDto() {}
  public CatalogKitSummaryDto(String id, String vehicleLabel, String defaultImage, CatalogKitDto.KitInfo kit) {
    this.id = id;
    this.vehicleLabel = vehicleLabel;
    this.defaultImage = defaultImage;
    this.kit = kit;
  }
}
