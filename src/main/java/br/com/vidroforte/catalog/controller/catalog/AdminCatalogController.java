package br.com.vidroforte.catalog.controller;

import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.KitDto;
import br.com.vidroforte.catalog.service.catalog.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/catalog")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminCatalogController {

  private final CatalogService catalogService;

  public AdminCatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/kits")
  public ResponseEntity<List<KitDto>> getAllKits() {
    return ResponseEntity.ok(catalogService.getAllKits());
  }

  @GetMapping("/kits/{kitId}")
  public ResponseEntity<KitDto> getKitById(@PathVariable String kitId) {
    return ResponseEntity.ok(catalogService.getKitById(kitId));
  }

  @PostMapping("/kits")
  public ResponseEntity<KitDto> createKit(@RequestBody KitDto body) {
    return ResponseEntity.ok(catalogService.createKit(body));
  }

  @PutMapping("/kits/{kitId}")
  public ResponseEntity<KitDto> updateKit(@PathVariable String kitId, @RequestBody KitDto body) {
    return ResponseEntity.ok(catalogService.upsertKitFull(kitId, body));
  }

  @DeleteMapping("/kits/{kitId}")
  public ResponseEntity<Void> deleteKit(@PathVariable String kitId) {
    catalogService.deleteKit(kitId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/upload")
  public Map<String, Object> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "kitId", required = false) String kitId,
      @RequestParam(value = "kind", required = false) String kind
  ) throws Exception {

    String ext = Optional.ofNullable(file.getOriginalFilename())
        .filter(n -> n.contains("."))
        .map(n -> n.substring(n.lastIndexOf(".")))
        .orElse(".jpg");

    // (Opcional) normalize extensões estranhas
    if (ext.length() > 8) ext = ".jpg";

    String name = UUID.randomUUID() + ext.toLowerCase();

    Path dir = Paths.get("uploads", "catalog");
    Files.createDirectories(dir);

    Path target = dir.resolve(name);
    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

    String url = "/uploads/catalog/" + name;

    return Map.of(
        "url", url,
        "absoluteUrl", "http://localhost:8080" + url,
        "filename", name,
        "kitId", kitId == null ? "" : kitId,
        "kind", kind == null ? "" : kind
    );
  }
}

