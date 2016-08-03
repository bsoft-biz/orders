package biz.bsoft.orders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by vbabin on 28.11.2015.
 */
@Entity
@Table(name = "t_client_pos")
public class ClientPOS {
    @GeneratedValue
    @Id
    private Integer id;

    @Basic
    private String posName;

    @Basic
    @JsonIgnore
    private Integer extid;

    private String posAddress;

    private String posPhone;

    private String managerName;

    private String managerPhone;

    @Column(name = "deny_count2")
    private Integer denyCount2;

    @Override
    public String toString() {
        return "ClientPOS{" +
                "id=" + id +
                ", posName='" + posName + '\'' +
                ", extid=" + extid +
                ", posAddress='" + posAddress + '\'' +
                ", posPhone='" + posPhone + '\'' +
                ", managerName='" + managerName + '\'' +
                ", managerPhone='" + managerPhone + '\'' +
                ", denyCount2='" + denyCount2 + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }

    public Integer getExtid() {
        return extid;
    }

    public void setExtid(Integer extid) {
        this.extid = extid;
    }

    public String getPosAddress() {
        return posAddress;
    }

    public void setPosAddress(String posAddress) {
        this.posAddress = posAddress;
    }

    public String getPosPhone() {
        return posPhone;
    }

    public void setPosPhone(String posPhone) {
        this.posPhone = posPhone;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public Integer getDenyCount2() {
        return denyCount2;
    }

    public void setDenyCount2(Integer denyCount2) {
        this.denyCount2 = denyCount2;
    }
}
