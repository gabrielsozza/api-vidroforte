package br.com.vidroforte.catalog.repository.catalog;

import br.com.vidroforte.catalog.model.catalog.CatalogHotspot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogHotspotRepository extends JpaRepository<CatalogHotspot, String> {

  // view.id = Long
  List<CatalogHotspot> findByView_Id(Long viewId);

  // kit.id = String (ex.: "masterl1h1semabertura")
  void deleteByView_Kit_Id(String kitId);

  // glass.id = String (ex.: "L1H1LE1")
  void deleteByGlass_Id(String glassId);
}
