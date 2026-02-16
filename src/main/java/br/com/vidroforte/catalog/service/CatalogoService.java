package br.com.vidroforte.catalog.service;

import br.com.vidroforte.catalog.dto.CatalogKitDto;
import br.com.vidroforte.catalog.dto.CatalogKitSummaryDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

  private final Map<String, CatalogKitDto> db = new LinkedHashMap<>();

  public CatalogoService() {
    seed();
  }

  public List<CatalogKitSummaryDto> listKits() {
    return db.values().stream()
      .map(k -> new CatalogKitSummaryDto(k.id, k.vehicleLabel, k.defaultImage, k.kit))
      .collect(Collectors.toList());
  }

  public Optional<CatalogKitDto> getKitFull(String id) {
    return Optional.ofNullable(db.get(id));
  }

  private void seed() {
    // --- 1 kit exemplo (copia o resto do teu KITS pra cá depois) ---
    CatalogKitDto k = new CatalogKitDto();
    k.id = "masterl1h1semabertura";
    k.vehicleLabel = "Renault Master L1H1";
    k.defaultImage = "./img/vanl1h1.png";

    CatalogKitDto.KitInfo kitInfo = new CatalogKitDto.KitInfo();
    kitInfo.code = "04.19-7706-HW";
    kitInfo.desc = "Kit de Vidros Fixos Master L1H1 Verde";
    k.kit = kitInfo;

    CatalogKitDto.Glass g1 = new CatalogKitDto.Glass();
    g1.id = "L1H1LE1";
    g1.model = "Vidro fixo Master L1H1 1 vão LE 1210x686";
    g1.code = "04.15-2982-HW";
    g1.desc = "Vidro fixo Master L1H1 1 vão LE 1210x686";
    g1.position = "1 vão";
    g1.side = "LE";
    g1.dim = "1210 x 686";
    g1.options = List.of("Verde","Fixo");

    k.glasses = List.of(g1);

    CatalogKitDto.View viewA = new CatalogKitDto.View();
    viewA.image = "./img/l1h1-SA.png";
    viewA.coordsPxByGlassId = Map.of(
      "L1H1LE1", new CatalogKitDto.Box(60, 310, 175, 195)
    );

    k.views = Map.of("a", viewA);

    db.put(k.id, k);
  }
}
