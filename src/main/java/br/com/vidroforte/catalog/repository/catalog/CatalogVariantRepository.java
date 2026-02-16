package br.com.vidroforte.catalog.repository.catalog;

import br.com.vidroforte.catalog.model.catalog.CatalogVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogVariantRepository extends JpaRepository<CatalogVariant, Long> {

    List<CatalogVariant> findByKit_Id(String kitId);

    Optional<CatalogVariant> findByKit_IdAndVariantId(String kitId, String variantId);

    @Transactional
    void deleteByKit_Id(String kitId);
}
