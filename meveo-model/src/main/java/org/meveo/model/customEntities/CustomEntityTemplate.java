package org.meveo.model.customEntities;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.crm.custom.AssetTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code"})
@Table(name = "cust_cet", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "cust_cet_seq"), })
@NamedQueries({ @NamedQuery(name = "CustomEntityTemplate.getCETForCache", query = "SELECT cet from CustomEntityTemplate cet where cet.disabled=false order by cet.name ") })
public class CustomEntityTemplate extends BusinessEntity implements Comparable<CustomEntityTemplate> {

    private static final long serialVersionUID = 8281478284763353310L;

    public static String CFT_PREFIX = "CE";

    @Column(name = "name", length = 100, nullable = false)
    @Size(max = 100)
    @NotNull
    private String name;

    /**
     * Labels to apply to the template.
     */
    @ElementCollection
    @CollectionTable(name = "cet_labels", joinColumns = { @JoinColumn(name = "cet_id") })
	@Column(name = "label")
    private List<String> labels;
    
    /**
     * Whether the CET is an asset.
     * An asset is an entity containing only one property named "value"
     */
    @Column(name = "asset", updatable = false)
    @Type(type = "numeric_boolean")
    private boolean asset;
    
    /**
     * The type of the asset, if entity is an asset.
     */
    @Column(name = "asset_type", nullable = true, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AssetTypeEnum assetType;

    public List<String> getLabels() {
        return labels;
    }

    public boolean isAsset() {
		return asset;
	}

	public void setAsset(boolean asset) {
		this.asset = asset;
	}

	public AssetTypeEnum getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetTypeEnum assetType) {
		this.assetType = assetType;
	}

	public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppliesTo() {
        return CFT_PREFIX + "_" + getCode();
    }
    
    public static String getAppliesTo(String code) {
        return CFT_PREFIX + "_" + code;
    }

    public String getReadPermission() {
        return CustomEntityTemplate.getReadPermission(code);
    }

    public String getModifyPermission() {
        return CustomEntityTemplate.getModifyPermission(code);
    }

    @Override
    public int compareTo(CustomEntityTemplate cet1) {
        return StringUtils.compare(name, cet1.getName());
    }

    public static String getReadPermission(String code) {
        return "CE_" + code + "-read";
    }

    public static String getModifyPermission(String code) {
        return "CE_" + code + "-modify";
    } 
}