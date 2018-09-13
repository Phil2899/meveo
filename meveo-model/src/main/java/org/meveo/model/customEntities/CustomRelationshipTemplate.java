package org.meveo.model.customEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.billing.RelationshipDirectionEnum;

@Entity
@ModuleItem
@ExportIdentifier({ "code"})
@Table(name = "CUST_CRT", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE"}))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CUST_CRT_SEQ")
@NamedQueries({ @NamedQuery(name = "CustomRelationshipTemplate.getCRTForCache", query = "SELECT crt from CustomRelationshipTemplate crt where crt.disabled=false  ") })
public class CustomRelationshipTemplate extends BusinessEntity implements Comparable<CustomRelationshipTemplate> {

    private static final long serialVersionUID = 8281478284763353310L;

    public static String CRT_PREFIX = "CRT";

    @Column(name = "name", length = 100, nullable = false)
    @Size(max = 100)
    @NotNull
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "START_NODE_ID")
    private CustomEntityTemplate startNode;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "END_NODE_ID")
    private CustomEntityTemplate  endNode;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "DIRECTION", length = 100)
    private RelationshipDirectionEnum direction = RelationshipDirectionEnum.OUTGOING;

    /**
     * Json list type. ex : ["firstName","lastName","birthDate"]
     */
    @Column(name = "START_NODE_KEYS", length = 100)
    private String startNodeKeys; 

    /**
     * Json list type. ex : ["firstName","lastName","birthDate"]
     */
    @Column(name = "END_NODE_KEYS", length = 100)
    private String endNodeKeys; 
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomEntityTemplate getStartNode() {
		return startNode;
	}

	public void setStartNode(CustomEntityTemplate startNode) {
		this.startNode = startNode;
	}

	public CustomEntityTemplate getEndNode() {
		return endNode;
	}

	public void setEndNode(CustomEntityTemplate endNode) {
		this.endNode = endNode;
	}

	public RelationshipDirectionEnum getDirection() {
		return direction;
	}

	public void setDirection(RelationshipDirectionEnum direction) {
		if(direction!=null){
			this.direction = direction;
		}
		
	}

	public String getStartNodeKeys() {
		return startNodeKeys;
	}

	public void setStartNodeKey(String startNodeKeys) {
		this.startNodeKeys = startNodeKeys;
	}

	public String getEndNodeKeys() {
		return endNodeKeys;
	}

	public void setEndNodeKey(String endNodeKeys) {
		this.endNodeKeys = endNodeKeys;
	}

	public String getAppliesTo() {
        return CRT_PREFIX + "_" + getCode();
    }
    
    public static String getAppliesTo(String code) {
        return CRT_PREFIX + "_" + code;
    }

    public String getPermissionResourceName() {
        return CustomRelationshipTemplate.getPermissionResourceName(code);
    }

    @Override
    public int compareTo(CustomRelationshipTemplate cet1) {
        return StringUtils.compare(name, cet1.getName());
    }

    public static String getPermissionResourceName(String code) {
        return "CRT_" + code;
    }
}