package org.hertsig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Tag {
    private UUID id;
    private UUID parentid;
    private String name;
    private UUID userid;
}
