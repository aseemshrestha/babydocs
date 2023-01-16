package com.babydocs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ImageDeleteDTO {
    @NotNull
    private Long mediaId;
    @NotNull
    private String mediaLocation;
}
