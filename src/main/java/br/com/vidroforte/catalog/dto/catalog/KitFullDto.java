package br.com.vidroforte.catalog.dto.catalog;

import java.util.List;
import java.util.Map;

public record KitFullDto(
        String id,
        String vehicleLabel,
        String defaultImage,
        KitMetaDto kit,
        List<GlassDto> glasses,
        Map<String, ViewDto> views
) {}
