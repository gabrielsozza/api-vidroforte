package br.com.vidroforte.catalog.repository.catalog;

import br.com.vidroforte.catalog.model.catalog.CatalogKit;
import br.com.vidroforte.catalog.model.catalog.CatalogSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogKitRepository extends JpaRepository<CatalogKit, String> {

  // Catálogo público: só ativos.
  List<CatalogKit> findByActiveTrueOrderByVehicleLabelAsc();

  // Catálogo público filtrado por segmento (convencional / ambulância / escolar / etc.).
  List<CatalogKit> findBySegmentAndActiveTrueOrderByVehicleLabelAsc(CatalogSegment segment);

  // Conveniência: quando quiser abrir o kit no front/admin garantindo ativo.
  Optional<CatalogKit> findByIdAndActiveTrue(String id);

  // Admin: listar tudo (ativos e inativos).
  List<CatalogKit> findAllByOrderByVehicleLabelAsc();
}
