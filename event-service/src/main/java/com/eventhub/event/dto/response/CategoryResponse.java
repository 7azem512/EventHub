package com.eventhub.event.dto.response;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
}
