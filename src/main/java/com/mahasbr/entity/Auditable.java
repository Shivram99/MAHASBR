package com.mahasbr.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;



@Data
@MappedSuperclass
public abstract class Auditable {
	
	
	
	
    @Column(name = "created_user_id")
    private Long createdUserId;

    @Column(name = "updated_user_id")
    private Long updatedUserId;

    @Column(name = "created_date_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdDateTime;

    @Column(name = "updated_date_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedDateTime;
    
    
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        if (this.createdDateTime == null) {
            this.createdDateTime = now;
        }
        if (this.updatedDateTime == null) {
            this.updatedDateTime = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDateTime = new Date();
    }

}