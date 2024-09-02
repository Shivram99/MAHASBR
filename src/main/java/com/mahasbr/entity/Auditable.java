package com.mahasbr.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Column(name = "created_date_time", updatable = false)
    private Date createdDateTime;

    @Column(name = "updated_date_time")
    private Date updatedDateTime;
    
//    @PrePersist
//    protected void onCreate() {
//        this.createdDateTime = new Date();
//        this.createdUserId = getCurrentUserId(); // Set the created user ID
//    }
//
//    // Method to be called before updating the entity
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedDateTime = new Date();
//        this.updatedUserId = getCurrentUserId(); // Set the updated user ID
//    }
//
//    // Method to get the current user ID (needs to be implemented)
//    private Long getCurrentUserId() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof UserDetails) {
//            // Assuming your UserDetails implementation has a method to get the user ID
//            return ((User) principal).getId(); // Replace with your method to get user ID
//        }
//        return 1L; // Or handle accordingly if the user is not authenticated
//    }
    
}