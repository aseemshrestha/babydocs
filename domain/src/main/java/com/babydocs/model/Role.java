package com.babydocs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Role implements Serializable {
    @Id
    private int id;
    private String role;

    public Role() {
    }
}
