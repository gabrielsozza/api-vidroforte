package br.com.vidroforte.catalog.model.catalog;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalog_glass")
public class CatalogGlass {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kit_id", nullable = false)
    private CatalogKit kit;

    @Column(nullable = false, length = 255)
    private String model;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(name = "glass_desc", nullable = false, length = 255)
    private String glassDesc;

    @Column(nullable = false, length = 40)
    private String position;

    @Column(nullable = false, length = 4)
    private String side;

    @Column(nullable = false, length = 40)
    private String dim;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @ElementCollection
    @CollectionTable(
            name = "catalog_glass_option",
            joinColumns = @JoinColumn(name = "glass_id")
    )
    @Column(name = "opt", nullable = false, length = 40)
    private Set<String> options = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CatalogKit getKit() {
        return kit;
    }

    public void setKit(CatalogKit kit) {
        this.kit = kit;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGlassDesc() {
        return glassDesc;
    }

    public void setGlassDesc(String glassDesc) {
        this.glassDesc = glassDesc;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getDim() {
        return dim;
    }

    public void setDim(String dim) {
        this.dim = dim;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Set<String> getOptions() {
        return options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }

    @Column(name = "pdf_url", length = 255)
    private String pdfUrl;

    @Column(name = "parasolid_xt_url", length = 255)
    private String parasolidXtUrl;

    @Column(name = "parasolid_xb_url", length = 255)
    private String parasolidXbUrl;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getParasolidXtUrl() {
        return parasolidXtUrl;
    }

    public void setParasolidXtUrl(String parasolidXtUrl) {
        this.parasolidXtUrl = parasolidXtUrl;
    }

    public String getParasolidXbUrl() {
        return parasolidXbUrl;
    }

    public void setParasolidXbUrl(String parasolidXbUrl) {
        this.parasolidXbUrl = parasolidXbUrl;
    }

}
