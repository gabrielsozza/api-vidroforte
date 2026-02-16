package br.com.vidroforte.catalog.controller;

import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.KitDto;
import br.com.vidroforte.catalog.model.catalog.CatalogSegment;
import br.com.vidroforte.catalog.service.catalog.CatalogService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@CrossOrigin(origins = "*")
public class CatalogController {

  private final CatalogService catalogService;

  public CatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  // Lista resumida (para select/filtro)
  @GetMapping("/kits")
  public List<KitDto> listKits(
      @RequestParam(value = "segment", required = false) CatalogSegment segment
  ) {
    return catalogService.listKits(segment);
  }

  // Kit completo (glasses + views + hotspots)
  @GetMapping("/kits/{kitId}")
  public KitDto getKitFull(@PathVariable String kitId) {
    return catalogService.getKitFull(kitId);
  }

  // Upsert completo (painel admin)
  @PutMapping(value = "/kits/{kitId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public KitDto upsertKitFull(
      @PathVariable String kitId,
      @RequestBody KitDto body
  ) {
    return catalogService.upsertKitFull(kitId, body);
  }

  // Download PDF de 1 vidro
  @GetMapping(
      value = "/kits/{kitId}/glasses/{glassId}/pdf",
      produces = MediaType.APPLICATION_PDF_VALUE
  )
  public ResponseEntity<byte[]> downloadGlassPdf(
      @PathVariable String kitId,
      @PathVariable String glassId
  ) {
    byte[] pdf = catalogService.generateGlassPdf(kitId, glassId);

    String filename = kitId + "-" + glassId + ".pdf";

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdf);
  }
}



