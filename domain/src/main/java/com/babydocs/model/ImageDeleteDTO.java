package com.babydocs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ImageDeleteDTO {
    @NotNull ( message = "Media Id is required")
    private Long mediaId;

    @NotNull ( message = "Media path is required")
    private String mediaLocation;
}
