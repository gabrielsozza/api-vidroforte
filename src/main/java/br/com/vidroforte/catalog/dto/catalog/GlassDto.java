package br.com.vidroforte.catalog.dto.catalog;

import java.util.List;

public record GlassDto(
        String id,
        String model,
        String code,
        String desc,
        String position,
        String side,
        String dim,
        List<String> options
) {}
