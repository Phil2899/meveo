package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.RelationshipDirectionEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomRelationshipTemplate;

/**
 * @author Rachid AITYAAZZA
 **/

@XmlRootElement(name = "CustomRelationshipTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomRelationshipTemplateDto extends BaseDto {

    private static final long serialVersionUID = -6633504145323452803L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String name;

    @XmlAttribute()
    private boolean isUnique = false;

    @XmlAttribute()
    private String description;

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<CustomFieldTemplateDto> fields = new ArrayList<>();

    /**
     * Description of super template
     */
    @XmlAttribute()
    private CustomRelationshipTemplateDto superTemplate;

    @XmlAttribute(required = true)
    private String startNodeCode;

    @XmlAttribute(required = true)
    private String endNodeCode;

    @XmlAttribute()
    private RelationshipDirectionEnum direction;

    @XmlAttribute()
    private List<String> startNodeKeys = new ArrayList<>();

    @XmlAttribute()
    private List<String> endNodeKeys = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public CustomRelationshipTemplateDto getSuperTemplate() {
        return superTemplate;
    }

    public void setSuperTemplate(CustomRelationshipTemplateDto superTemplate) {
        this.superTemplate = superTemplate;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getName() {
        return name != null ? name : code;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description != null ? description : code;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<CustomFieldTemplateDto> getFields() {
        return fields;
    }


    public void setFields(List<CustomFieldTemplateDto> fields) {
        this.fields = fields;
    }


    public RelationshipDirectionEnum getDirection() {
        return direction;
    }


    public void setDirection(RelationshipDirectionEnum direction) {
        this.direction = direction;
    }


    public String getStartNodeCode() {
        return startNodeCode;
    }


    public void setStartNodeCode(String startNodeCode) {
        this.startNodeCode = startNodeCode;
    }


    public String getEndNodeCode() {
        return endNodeCode;
    }


    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }


    public List<String> getStartNodeKeys() {
        return startNodeKeys;
    }


    public void setStartNodeKeys(List<String> startNodeKeys) {
        this.startNodeKeys = startNodeKeys;
    }


    public List<String> getEndNodeKeys() {
        return endNodeKeys;
    }


    public void setEndNodeKeys(List<String> endNodeKeys) {
        this.endNodeKeys = endNodeKeys;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    /**
     * Convert CustomRelationshipTemplate instance to CustomRelationshipTemplateDto object including the fields and actions
     *
     * @param cet       CustomRelationshipTemplate object to convert
     * @param crtFields Fields (CustomFieldTemplate) that are part of CustomRelationshipTemplate
     * @return A CustomRelationshipTemplateDto object with fields set
     */
    public static CustomRelationshipTemplateDto toDTO(CustomRelationshipTemplate cet, Collection<CustomFieldTemplate> crtFields) {
        CustomRelationshipTemplateDto dto = new CustomRelationshipTemplateDto();
        dto.setCode(cet.getCode());
        dto.setName(cet.getName());
        dto.setDescription(cet.getDescription());
        dto.setUnique(cet.isUnique());
        if(cet.getSuperTemplate() != null){
            dto.setSuperTemplate(toDTO(cet.getSuperTemplate(), null));  //TODO: Find super template fields
        }
        if (crtFields != null) {
            List<CustomFieldTemplateDto> fields = new ArrayList<>();
            for (CustomFieldTemplate cft : crtFields) {
                fields.add(new CustomFieldTemplateDto(cft));
            }
            dto.setFields(fields);
        }


        return dto;
    }

    /**
     * Convert CustomRelationshipTemplateDto to a CustomRelationshipTemplate instance. Note: does not convert custom fields that are part of DTO
     *
     * @param dto         CustomRelationshipTemplateDto object to convert
     * @param crtToUpdate CustomRelationshipTemplate to update with values from dto, or if null create a new one
     * @return A new or updated CustomRelationshipTemplate instance
     */
    public static CustomRelationshipTemplate fromDTO(CustomRelationshipTemplateDto dto, CustomRelationshipTemplate crtToUpdate) {
        CustomRelationshipTemplate crt = new CustomRelationshipTemplate();
        if (crtToUpdate != null) {
            crt = crtToUpdate;
        }
        crt.setCode(dto.getCode());
        crt.setName(dto.getName());
        crt.setDescription(dto.getDescription());
        crt.setDirection(dto.getDirection());
        crt.setUnique(dto.isUnique());
        if(dto.getSuperTemplate() != null){
            crt.setSuperTemplate(fromDTO(dto.getSuperTemplate(), crt.getSuperTemplate()));
        }
        return crt;
    }

    @Override
    public String toString() {
        return "CustomRelationshipTemplateDto [code=" + code + ", name=" + name + ", description=" + description + ", fields=" + fields + "]";
    }

}