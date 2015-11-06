package org.hertsig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Set {
    private UUID id;
    private String gatherercode;
    private String code;
    private String name;
}
