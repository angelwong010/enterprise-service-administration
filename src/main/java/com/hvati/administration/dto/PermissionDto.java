package com.hvati.administration.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    private String id;
    private String label;
    private String category;
}
