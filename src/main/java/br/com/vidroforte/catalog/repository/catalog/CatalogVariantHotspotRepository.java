package br.com.vidroforte.catalog.repository.catalog;

import br.com.vidroforte.catalog.model.catalog.CatalogVariantHotspot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CatalogVariantHotspotRepository extends JpaRepository<CatalogVariantHotspot, Long> {

    // Usado no MERGE: quando vier coordsPxByGlassId != null, limpa só os hotspots dessa view e recria.
    @Transactional
    void deleteByVariantView_Id(Long variantViewId);

    // Usado no deleteKit(): limpa tudo do kit antes de apagar views/variants (ordem evita FK).
    @Modifying
    @Transactional
    @Query("DELETE FROM CatalogVariantHotspot h WHERE h.variantView.variant.kit.id = ?1")
    void deleteByKit_Id(String kitId);
}
