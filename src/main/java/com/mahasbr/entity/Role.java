
package com.mahasbr.entity;

import java.util.HashSet;
import java.util.Set;

import com.mahasbr.model.ERole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Role extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_generator")
    @SequenceGenerator(name = "role_seq_generator", sequenceName = "roles_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
    
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//        name = "role_menu",
//        joinColumns = @JoinColumn(name = "role_id"),
//        inverseJoinColumns = @JoinColumn(name = "menu_id")
//    )
//    private Set<Menu> menus = new HashSet<>();
}

