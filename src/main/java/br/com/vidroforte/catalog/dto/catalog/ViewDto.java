package br.com.vidroforte.catalog.dto.catalog;

import java.util.Map;

public record ViewDto(
        String image,
        Map<String, PxBoxDto> coordsPxByGlassId
) {}
