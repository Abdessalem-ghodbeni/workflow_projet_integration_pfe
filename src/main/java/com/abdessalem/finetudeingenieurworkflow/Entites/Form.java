package com.abdessalem.finetudeingenieurworkflow.Entites;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Getter
@Setter

public class Form implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    @Column(length = 512)
    private String description;
    private String color;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<FormField> formFields = new ArrayList<>();
@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuteur_id", nullable = false)
    private Tuteur tuteur;
}
