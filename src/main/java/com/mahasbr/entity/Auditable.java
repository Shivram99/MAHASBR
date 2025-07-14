package com.mahasbr.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;



import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
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

    @PrePersist
    protected void onCreate() {
        this.createdDateTime = new Date();
        this.updatedDateTime = new Date(); // optional: initialize both on creation
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDateTime = new Date();
    }
}
