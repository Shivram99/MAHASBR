package com.mahasbr.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menus")
@Getter @Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String nameMr;

    private String route;
    private String icon;
    private Integer sequence;

    private Boolean active = true;
    
    private String menuType;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("sequence ASC")
    private List<Menu> children = new ArrayList<>();
}

