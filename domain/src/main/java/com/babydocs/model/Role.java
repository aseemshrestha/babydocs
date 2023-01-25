package com.babydocs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role;

    public Role() {
    }
}
