package br.com.vidroforte.catalog.repository.catalog;

import br.com.vidroforte.catalog.model.catalog.CatalogView;
import br.com.vidroforte.catalog.model.catalog.CatalogViewKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogViewRepository extends JpaRepository<CatalogView, Long> {

  // Para listar todas as views de um kit (admin e/ou montagem do JSON).
  List<CatalogView> findByKit_Id(String kitId);

  // Para carregar uma view específica (A/B/C).
  Optional<CatalogView> findByKit_IdAndViewKey(String kitId, CatalogViewKey viewKey);

  // Ajuda no admin (trocar imagem da view) e também para “resetar” uma view.
  @Transactional
  void deleteByKit_IdAndViewKey(String kitId, CatalogViewKey viewKey);

  // Útil quando for recriar todas as views de um kit no admin.
  @Transactional
  void deleteByKit_Id(String kitId);
}
