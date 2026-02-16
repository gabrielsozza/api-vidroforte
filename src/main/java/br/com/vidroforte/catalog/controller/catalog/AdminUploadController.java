package br.com.vidroforte.catalog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/catalog")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminUploadController {

  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  // Se quiser forçar uma base URL em produção (ex.: https://api.seudominio.com)
  @Value("${app.upload.publicBaseUrl:}")
  private String publicBaseUrl;

  private static final Set<String> ALLOWED = Set.of("image/png", "image/jpeg", "image/webp");
  private static final long MAX_BYTES = 10L * 1024L * 1024L; // 10MB

  @PostMapping(
      value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<?> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "kitId", required = false) String kitId,
      @RequestParam(value = "variantId", required = false) String variantId,
      @RequestParam(value = "viewId", required = false) String viewId,
      @RequestParam(value = "kind", required = false) String kind,
      HttpServletRequest request
  ) throws Exception {

    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Arquivo vazio."));
    }

    if (file.getSize() > MAX_BYTES) {
      return ResponseEntity.badRequest().body(Map.of("error", "Arquivo maior que 10MB."));
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED.contains(contentType)) {
      return ResponseEntity.badRequest().body(Map.of("error", "Tipo inválido (use PNG/JPG/WEBP)."));
    }

    String ext = switch (contentType) {
      case "image/png" -> ".png";
      case "image/jpeg" -> ".jpg";
      case "image/webp" -> ".webp";
      default -> ".jpg";
    };

    String safeKit = sanitize(kitId);
    String safeVar = sanitize(variantId);
    String safeView = sanitize(viewId);
    String safeKind = sanitize(kind);

    String baseName = String.join("_",
        "kit", emptyAsDash(safeKit),
        "var", emptyAsDash(safeVar),
        "view", emptyAsDash(safeView),
        "kind", emptyAsDash(safeKind),
        UUID.randomUUID().toString()
    );

    String filename = baseName + ext;

    // Pasta final: {uploadDir}/catalog/
    Path dir = Path.of(uploadDir, "catalog").toAbsolutePath().normalize();
    Files.createDirectories(dir);

    Path target = dir.resolve(filename).normalize();

    // Proteção contra path traversal
    if (!target.startsWith(dir)) {
      return ResponseEntity.badRequest().body(Map.of("error", "Nome de arquivo inválido."));
    }

    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

    String relativeUrl = "/uploads/catalog/" + filename;

    // Monta baseUrl: ou publicBaseUrl (se informado) ou pelo request atual
    String baseUrl = (publicBaseUrl != null && !publicBaseUrl.isBlank())
        ? publicBaseUrl.trim().replaceAll("/+$", "")
        : ServletUriComponentsBuilder.fromRequest(request)
            .replacePath(null)
            .build()
            .toUriString();

    String absoluteUrl = baseUrl + relativeUrl;

    return ResponseEntity.ok(Map.of(
        "url", relativeUrl,
        "absoluteUrl", absoluteUrl,
        "filename", filename,
        "contentType", contentType,
        "size", file.getSize(),
        "uploadedAt", LocalDateTime.now().toString()
    ));
  }

  private static String sanitize(String s) {
    if (!StringUtils.hasText(s)) return "";
    return s.trim()
        .toLowerCase()
        .replaceAll("[^a-z0-9\\-_.]", "-")
        .replaceAll("-{2,}", "-");
  }

  private static String emptyAsDash(String s) {
    return (s == null || s.isBlank()) ? "-" : s;
  }
}
