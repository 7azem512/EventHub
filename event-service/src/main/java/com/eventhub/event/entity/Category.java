package com.eventhub.event.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name", nullable = false,unique = true,length = 100)
    @Size(max = 100)
    private String name;

    @Column(name = "slug", nullable = false,unique = true,length = 120)
    @Size(max = 120)
    private String slug;

    @Column(name = "description",length = 500)
    @Size(max = 500)
    private String description;
}
