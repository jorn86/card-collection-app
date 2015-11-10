package org.hertsig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Printing {
    private UUID id;
    private UUID setid;
    private UUID cardid;
    private int multiverseid;
    private String number;
    private String rarity;
    private String originaltext;
    private String originaltype;
    private String flavortext;
}
