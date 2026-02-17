package br.com.vidroforte.catalog.dto.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import br.com.vidroforte.catalog.model.catalog.CatalogSegment;

public final class CatalogDtos {

    public record CatalogResponse(List<KitDto> kits) {

    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KitInfoDto {

        public String code;
        public String desc;

        public KitInfoDto() {
        }

        public KitInfoDto(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KitDto {

        public String id;
        public String vehicleLabel;
        public CatalogSegment segment;
        public String defaultImage;
        public String presentationImage;
        public KitInfoDto kit;
        public boolean active = true;
        public List<GlassDto> glasses = new ArrayList<>();
        public List<VariantDto> variants = new ArrayList<>();
        public Map<String, ViewDto> views = new HashMap<>();

        public KitDto() {
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GlassDto {

        public String id;
        public String model;
        public String code;
        public String desc;
        public String position;
        public String side;
        public String dim;
        public int sortOrder;

        public String pdfUrl;
        public String parasolidXtUrl;
        public String parasolidXbUrl;

        public List<String> options = new ArrayList<>();

        public GlassDto() {
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ViewDto {

        public String image;
        public Map<String, HotspotDto> coordsPxByGlassId = new HashMap<>();

        public ViewDto() {
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HotspotDto {

        public int x;
        public int y;
        public int w;
        public int h;

        public HotspotDto() {
        }

        public HotspotDto(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxDto extends HotspotDto {

        public BoxDto() {
        }

        public BoxDto(int x, int y, int w, int h) {
            super(x, y, w, h);
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VariantDto {

        public String id;
        public String name;
        public String presentationImage;
        public List<ViewDtoSimple> views = new ArrayList<>();

        public VariantDto() {
        }
    }

    @JsonInclude(NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ViewDtoSimple {

        public String id;
        public String name;
        public String image;
        public LinkedHashMap<String, HotspotDto> coordsPxByGlassId = new LinkedHashMap<>();

        public ViewDtoSimple() {
        }
    }

    private CatalogDtos() {
    }
}
