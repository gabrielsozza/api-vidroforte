package br.com.vidroforte.catalog.repository.catalog;

import br.com.vidroforte.catalog.model.catalog.CatalogGlass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogGlassRepository extends JpaRepository<CatalogGlass, String> {

  // Front depende da ordenação (sortOrder).
  List<CatalogGlass> findByKit_IdOrderBySortOrderAsc(String kitId);

  // Útil no admin: pegar “um vidro do kit” de forma explícita.
  Optional<CatalogGlass> findByIdAndKit_Id(String id, String kitId);

  // Útil quando o admin recriar/atualizar todos os vidros do kit.
  @Transactional
  void deleteByKit_Id(String kitId);
}
