package org.openmrs.module.ebolaexample.domain;

import org.openmrs.DrugOrder;
import org.openmrs.User;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ebola_scheduled_dose")
public class ScheduledDose {

    public static enum DoseStatus {
        FULL, PARTIAL, NOT_GIVEN
    }

    @Id
    @GeneratedValue
    @Column(name = "scheduled_dose_id")
    private Integer scheduledDoseId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private DrugOrder order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_datetime" )
    private Date scheduledDatetime;

    @Column(name = "reason_not_administered_non_coded")
    private String reasonNotAdministeredNonCoded;

    @ManyToOne
    @JoinColumn(name = "creator")
    protected User creator;

    @Column(name = "voided", nullable = false)
    private Boolean voided;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_voided" )
    private Date dateVoided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    private User voidedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created" )
    private Date dateCreated;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_changed" )
    private Date dateChanged;

    @Basic
    @Column(name = "uuid", length = 38, unique = true)
    private String uuid  = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DoseStatus status;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getVoidedBy() { return voidedBy; }

    public void setVoidedBy(User voidedBy) { this.voidedBy = voidedBy; }

    public Boolean getVoided() { return voided; }

    public void setVoided(Boolean voided) { this.voided = voided; }

    public Date getDateVoided() { return dateVoided; }

    public void setDateVoided(Date dateVoided) { this.dateVoided = dateVoided; }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public void setStatus(DoseStatus status) {
        this.status = status;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getId() {
        return getScheduledDoseId();
    }

    public void setId(Integer id) {
        setScheduledDoseId(id);
    }

    public Integer getScheduledDoseId() {
        return scheduledDoseId;
    }

    public void setScheduledDoseId(Integer scheduledDoseId) {
        this.scheduledDoseId = scheduledDoseId;
    }

    public DrugOrder getOrder() {
        return order;
    }

    public void setOrder(DrugOrder order) {
        this.order = order;
    }

    public Date getScheduledDatetime() {
        return scheduledDatetime;
    }

    public void setScheduledDatetime(Date scheduledDatetime) {
        this.scheduledDatetime = scheduledDatetime;
    }

    public String getReasonNotAdministeredNonCoded() {
        return reasonNotAdministeredNonCoded;
    }

    public void setReasonNotAdministeredNonCoded(String reasonNotAdministeredNonCoded) {
        this.reasonNotAdministeredNonCoded = reasonNotAdministeredNonCoded;
    }

    public String getUuid() {
        return this.uuid;
    }

    public DoseStatus getStatus() {
        return status;
    }
}
