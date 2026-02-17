package br.com.vidroforte.catalog.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.vidroforte.catalog.model.catalog.CatalogVariantHotspot;

@Repository
public interface CatalogVariantHotspotRepository extends JpaRepository<CatalogVariantHotspot, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from CatalogVariantHotspot h where h.variantView.id = :variantViewId")
    int deleteAllByVariantViewId(@Param("variantViewId") Long variantViewId);

    @Modifying
    @Transactional
    @Query("delete from CatalogVariantHotspot h where h.variantView.variant.kit.id = :kitId")
    void deleteByKit_Id(@Param("kitId") String kitId);
}
