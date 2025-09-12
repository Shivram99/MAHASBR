package com.mahasbr.entity;

import java.util.Date;

import com.mahasbr.config.AuditEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public abstract class Auditable {

    @Column(name = "created_user_id", updatable = false)
    private Long createdUserId;

    @Column(name = "updated_user_id")
    private Long updatedUserId;

    @Column(name = "created_date_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDateTime;

    @Column(name = "updated_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDateTime;
 
    @Column(name = "created_ip", updatable = false)
    private String createdIp;

    @Column(name = "updated_ip")
    private String updatedIp;

    @Column(name = "created_user_agent", updatable = false)
    private String createdUserAgent;

    @Column(name = "updated_user_agent")
    private String updatedUserAgent;
    
//    @PrePersist
//    protected void onCreate() {
//        this.createdDateTime = new Date();
//        this.updatedDateTime = new Date(); // optional: initialize both on creation
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedDateTime = new Date();
//    }
}

