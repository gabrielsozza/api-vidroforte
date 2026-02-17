package br.com.vidroforte.catalog.service.catalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.GlassDto;
import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.HotspotDto;
import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.KitDto;
import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.KitInfoDto;
import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.VariantDto;
import br.com.vidroforte.catalog.dto.catalog.CatalogDtos.ViewDtoSimple;
import br.com.vidroforte.catalog.model.catalog.CatalogGlass;
import br.com.vidroforte.catalog.model.catalog.CatalogKit;
import br.com.vidroforte.catalog.model.catalog.CatalogSegment;
import br.com.vidroforte.catalog.model.catalog.CatalogVariant;
import br.com.vidroforte.catalog.model.catalog.CatalogVariantHotspot;
import br.com.vidroforte.catalog.model.catalog.CatalogVariantView;
import br.com.vidroforte.catalog.repository.catalog.CatalogGlassRepository;
import br.com.vidroforte.catalog.repository.catalog.CatalogKitRepository;
import br.com.vidroforte.catalog.repository.catalog.CatalogVariantHotspotRepository;
import br.com.vidroforte.catalog.repository.catalog.CatalogVariantRepository;
import br.com.vidroforte.catalog.repository.catalog.CatalogVariantViewRepository;

@Service
public class CatalogService {

    private final CatalogKitRepository kitRepo;
    private final CatalogGlassRepository glassRepo;
    private final CatalogVariantRepository variantRepo;
    private final CatalogVariantViewRepository variantViewRepo;
    private final CatalogVariantHotspotRepository variantHotspotRepo;

    public CatalogService(
            CatalogKitRepository kitRepo,
            CatalogGlassRepository glassRepo,
            CatalogVariantRepository variantRepo,
            CatalogVariantViewRepository variantViewRepo,
            CatalogVariantHotspotRepository variantHotspotRepo
    ) {
        this.kitRepo = kitRepo;
        this.glassRepo = glassRepo;
        this.variantRepo = variantRepo;
        this.variantViewRepo = variantViewRepo;
        this.variantHotspotRepo = variantHotspotRepo;
    }

    @Transactional(readOnly = true)
    public List<KitDto> listKits(CatalogSegment segment) {
        List<CatalogKit> kits = (segment == null)
                ? kitRepo.findByActiveTrueOrderByVehicleLabelAsc()
                : kitRepo.findBySegmentAndActiveTrueOrderByVehicleLabelAsc(segment);

        return kits.stream().map(this::toKitDtoShallow).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KitDto> getAllKits() {
        return kitRepo.findAll().stream()
                .map(this::toKitDtoShallow)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public KitDto getKitById(String kitId) {
        return getKitFull(kitId);
    }

    @Transactional
    public KitDto createKit(KitDto kitDto) {
        if (kitDto == null || kitDto.id == null || kitDto.id.isBlank()) {
            throw new IllegalArgumentException("ID do kit é obrigatório");
        }
        return upsertKitFull(kitDto.id, kitDto);
    }

    @Transactional
    public void deleteKit(String kitId) {
        if (kitId == null || kitId.isBlank()) {
            throw new IllegalArgumentException("kitId obrigatório");
        }
        if (!kitRepo.existsById(kitId)) {
            throw new IllegalArgumentException("Kit não encontrado: " + kitId);
        }

        // Ordem correta pra não estourar FK e nem deixar lixo:
        variantHotspotRepo.deleteByKit_Id(kitId);
        variantViewRepo.deleteByVariant_Kit_Id(kitId);
        variantRepo.deleteByKit_Id(kitId);
        glassRepo.deleteByKit_Id(kitId);
        kitRepo.deleteById(kitId);
    }

    @Transactional(readOnly = true)
    public KitDto getKitFull(String kitId) {
        if (kitId == null || kitId.isBlank()) {
            throw new IllegalArgumentException("kitId obrigatório");
        }

        CatalogKit k = kitRepo.findById(kitId)
                .orElseThrow(() -> new IllegalArgumentException("Kit não encontrado: " + kitId));

        KitDto dto = new KitDto();
        dto.id = k.getId();
        dto.vehicleLabel = k.getVehicleLabel();
        dto.segment = k.getSegment();
        dto.presentationImage = k.getDefaultImagePath();
        dto.active = k.isActive();
        dto.kit = new KitInfoDto(k.getKitCode(), k.getKitDesc());

        List<CatalogGlass> glasses = glassRepo.findByKit_IdOrderBySortOrderAsc(kitId);
        dto.glasses = glasses.stream().map(this::toGlassDto).collect(Collectors.toList());

        List<CatalogVariant> variants = variantRepo.findByKit_Id(kitId);
        dto.variants = variants.stream().map(this::toVariantDto).collect(Collectors.toList());

        return dto;
    }

    @Transactional
    public KitDto upsertKitFull(String kitId, KitDto dto) {
        if (kitId == null || kitId.isBlank()) {
            throw new IllegalArgumentException("kitId obrigatório");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Body obrigatório");
        }

        CatalogKit kit = kitRepo.findById(kitId).orElseGet(() -> {
            CatalogKit k = new CatalogKit();
            k.setId(kitId);
            return k;
        });

        // 1) Merge do KIT (sempre atualiza)
        if (dto.vehicleLabel != null) {
            kit.setVehicleLabel(dto.vehicleLabel);
        }
        if (dto.segment != null) {
            kit.setSegment(dto.segment);
        }

        // Mantive como você está usando: dto.presentationImage -> defaultImagePath
        if (dto.presentationImage != null) {
            kit.setDefaultImagePath(dto.presentationImage);
        }

        if (dto.kit != null && dto.kit.code != null) {
            kit.setKitCode(dto.kit.code);
        }
        if (dto.kit != null && dto.kit.desc != null) {
            kit.setKitDesc(dto.kit.desc);
        }
        kit.setActive(dto.active);

        kitRepo.save(kit);

        // 2) Merge de GLASSES (cria/atualiza por id; DELETA ausentes)
        if (dto.glasses != null) {

            Set<String> incomingGlassIds = dto.glasses.stream()
                    .filter(Objects::nonNull)
                    .map(g -> g.id)
                    .filter(id -> id != null && !id.isBlank())
                    .collect(Collectors.toSet());

            // delete os que não vieram no payload
            List<CatalogGlass> existing = glassRepo.findByKit_IdOrderBySortOrderAsc(kitId);
            for (CatalogGlass old : existing) {
                if (old == null) {
                    continue;
                }
                if (!incomingGlassIds.contains(old.getId())) {
                    glassRepo.delete(old);
                }
            }

            int order = 0;
            for (GlassDto gDto : dto.glasses) {
                if (gDto == null || gDto.id == null || gDto.id.isBlank()) {
                    throw new IllegalArgumentException("Todo vidro precisa de id");
                }

                CatalogGlass g = glassRepo.findByIdAndKit_Id(gDto.id, kitId)
                        .orElseGet(CatalogGlass::new);

                g.setId(gDto.id);
                g.setKit(kit);

                if (gDto.model != null) {
                    g.setModel(gDto.model);
                }
                if (gDto.code != null) {
                    g.setCode(gDto.code);
                }
                if (gDto.desc != null) {
                    g.setGlassDesc(gDto.desc);
                }
                if (gDto.position != null) {
                    g.setPosition(gDto.position);
                }
                if (gDto.side != null) {
                    g.setSide(gDto.side);
                }
                if (gDto.dim != null) {
                    g.setDim(gDto.dim);
                }

                if (gDto.pdfUrl != null) {
                    g.setPdfUrl(gDto.pdfUrl);
                }
                if (gDto.parasolidXtUrl != null) {
                    g.setParasolidXtUrl(gDto.parasolidXtUrl);
                }
                if (gDto.parasolidXbUrl != null) {
                    g.setParasolidXbUrl(gDto.parasolidXbUrl);
                }

                g.setSortOrder(order++);

                if (gDto.options != null) {
                    g.setOptions(new LinkedHashSet<>(gDto.options));
                }

                glassRepo.save(g);
            }
        }

        // 3) Merge de VARIANTS / VIEWS / HOTSPOTS
        if (dto.variants != null) {
            // 3.0) DELETE de VARIANTS ausentes no payload (payload = verdade)
            Set<String> incomingVariantIds = dto.variants.stream()
                    .filter(Objects::nonNull)
                    .map(v -> v.id)
                    .filter(id -> id != null && !id.isBlank())
                    .collect(Collectors.toSet());

            List<CatalogVariant> existingVariants = variantRepo.findByKit_Id(kitId);

            for (CatalogVariant oldVariant : existingVariants) {
                if (oldVariant == null) {
                    continue;
                }

                if (!incomingVariantIds.contains(oldVariant.getVariantId())) {
                    // apaga filhos primeiro (views/hotspots) pra não estourar FK
                    List<CatalogVariantView> oldViews = variantViewRepo.findByVariant_Id(oldVariant.getId());
                    for (CatalogVariantView ov : oldViews) {
                        if (ov == null) {
                            continue;
                        }
                        variantHotspotRepo.deleteAllByVariantViewId(ov.getId());
                        variantViewRepo.delete(ov);
                    }
                    variantRepo.delete(oldVariant);
                }
            }

            for (VariantDto vDto : dto.variants) {
                if (vDto == null || vDto.id == null || vDto.id.isBlank()) {
                    throw new IllegalArgumentException("Toda variante precisa de id");
                }

                CatalogVariant variant = variantRepo.findByKit_IdAndVariantId(kitId, vDto.id)
                        .orElseGet(() -> {
                            CatalogVariant v = new CatalogVariant();
                            v.setKit(kit);
                            v.setVariantId(vDto.id);
                            return v;
                        });

                if (vDto.name != null) {
                    variant.setVariantName(vDto.name);
                }
                if (vDto.presentationImage != null) {
                    variant.setPresentationImage(vDto.presentationImage);
                }

                variantRepo.save(variant);

                // views null => não mexe em views/hotspots existentes
                if (vDto.views == null) {
                    continue;
                }

                // views [] => significa “delete todas as views” desta variante
                // views [..] => sincroniza: cria/atualiza as enviadas e deleta as ausentes
                Set<String> incomingViewIds = new HashSet<>();
                for (ViewDtoSimple viewDto : vDto.views) {
                    if (viewDto == null) {
                        continue;
                    }
                    if (viewDto.id == null || viewDto.id.isBlank()) {
                        throw new IllegalArgumentException("Toda view precisa de id");
                    }
                    incomingViewIds.add(viewDto.id);
                }

                // IMPORTANTE: este método precisa existir no seu repositório
                // List<CatalogVariantView> findByVariant_Id(Long variantId);
                List<CatalogVariantView> existingViews = variantViewRepo.findByVariant_Id(variant.getId());

                for (CatalogVariantView oldView : existingViews) {
                    if (oldView == null) {
                        continue;
                    }
                    if (!incomingViewIds.contains(oldView.getViewId())) {
                        variantHotspotRepo.deleteAllByVariantViewId(oldView.getId());
                        variantViewRepo.delete(oldView);
                    }
                }

                for (ViewDtoSimple viewDto : vDto.views) {
                    CatalogVariantView view = variantViewRepo
                            .findByVariant_IdAndViewId(variant.getId(), viewDto.id)
                            .orElseGet(() -> {
                                CatalogVariantView vv = new CatalogVariantView();
                                vv.setVariant(variant);
                                vv.setViewId(viewDto.id);
                                return vv;
                            });

                    if (viewDto.name != null) {
                        view.setViewName(viewDto.name);
                    }
                    if (viewDto.image != null) {
                        view.setViewImage(viewDto.image);
                    }

                    view = variantViewRepo.save(view);

                    // Hotspots: payload = verdade (sempre sincroniza)
                    // sempre sincroniza hotspots (payload = verdade)
                    variantHotspotRepo.deleteAllByVariantViewId(view.getId());

                    Map<String, HotspotDto> map
                            = (viewDto.coordsPxByGlassId == null) ? new LinkedHashMap<>() : viewDto.coordsPxByGlassId;

                    for (Map.Entry<String, HotspotDto> e : map.entrySet()) {
                        String glassId = e.getKey();
                        HotspotDto h = e.getValue();
                        if (glassId == null || glassId.isBlank() || h == null)
                            continue;
                        

                        CatalogVariantHotspot hs = new CatalogVariantHotspot();
                        hs.setVariantView(view);
                        hs.setGlassId(glassId);
                        hs.setX(h.x);
                        hs.setY(h.y);
                        hs.setW(h.w);
                        hs.setH(h.h);
                        variantHotspotRepo.save(hs);
                    }

                }
            }
        }

        return getKitFull(kitId);
    }

    private KitDto toKitDtoShallow(CatalogKit k) {
        KitDto dto = new KitDto();
        dto.id = k.getId();
        dto.vehicleLabel = k.getVehicleLabel();
        dto.segment = k.getSegment();
        dto.presentationImage = k.getDefaultImagePath();
        dto.kit = new KitInfoDto(k.getKitCode(), k.getKitDesc());
        dto.active = k.isActive();
        return dto;
    }

    private GlassDto toGlassDto(CatalogGlass g) {
        GlassDto dto = new GlassDto();
        dto.id = g.getId();
        dto.model = g.getModel();
        dto.code = g.getCode();
        dto.desc = g.getGlassDesc();
        dto.position = g.getPosition();
        dto.side = g.getSide();
        dto.dim = g.getDim();
        dto.sortOrder = g.getSortOrder();

        dto.pdfUrl = g.getPdfUrl();
        dto.parasolidXtUrl = g.getParasolidXtUrl();
        dto.parasolidXbUrl = g.getParasolidXbUrl();

        dto.options = (g.getOptions() == null) ? new ArrayList<>() : new ArrayList<>(g.getOptions());
        return dto;
    }

    private VariantDto toVariantDto(CatalogVariant v) {
        VariantDto dto = new VariantDto();
        dto.id = v.getVariantId();
        dto.name = v.getVariantName();
        dto.presentationImage = v.getPresentationImage();

        if (v.getViews() == null) {
            dto.views = new ArrayList<>();
        } else {
            dto.views = v.getViews().stream()
                    .filter(Objects::nonNull)
                    .map(this::toViewDtoSimple)
                    .collect(Collectors.toList());
        }

        return dto;
    }

    private ViewDtoSimple toViewDtoSimple(CatalogVariantView view) {
        ViewDtoSimple dto = new ViewDtoSimple();
        dto.id = view.getViewId();
        dto.name = view.getViewName();
        dto.image = view.getViewImage();
        dto.coordsPxByGlassId = new LinkedHashMap<>();

        if (view.getHotspots() != null) {
            for (CatalogVariantHotspot hs : view.getHotspots()) {
                if (hs == null) {
                    continue;
                }
                dto.coordsPxByGlassId.put(
                        hs.getGlassId(),
                        new HotspotDto(hs.getX(), hs.getY(), hs.getW(), hs.getH())
                );
            }
        }

        return dto;
    }

    public byte[] generateGlassPdf(String kitId, String glassId) {
        return new byte[0];
    }
}
