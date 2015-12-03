package org.hertsig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class SetInfo {
    private String name;
    private int cards;
    private int prints;
}
