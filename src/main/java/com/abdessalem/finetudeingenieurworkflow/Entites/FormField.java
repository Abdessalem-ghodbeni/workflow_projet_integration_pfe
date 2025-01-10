package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Getter
@Setter
public class FormField implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String type;
    private String label;
    private String icon;
    private String placeholder;
    private boolean required;

    @ElementCollection
    private List<String> options = new ArrayList<>();

    private Integer index;

    @ManyToOne
    @JoinColumn(name = "form_id")
    @JsonIgnore
//    @JsonBackReference
    private Form form;

}
