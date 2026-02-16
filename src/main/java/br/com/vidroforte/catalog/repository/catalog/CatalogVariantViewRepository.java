package br.com.vidroforte.catalog.repository.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.vidroforte.catalog.model.catalog.CatalogVariantView;

@Repository
public interface CatalogVariantViewRepository extends JpaRepository<CatalogVariantView, Long> {

    Optional<CatalogVariantView> findByVariant_IdAndViewId(Long variantPkId, String viewId);

    // ADICIONE ISTO:
    List<CatalogVariantView> findByVariant_Id(Long variantPkId);

    @Transactional
    @Modifying
    void deleteByVariant_Kit_Id(String kitId);
}
