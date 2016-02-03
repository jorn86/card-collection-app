package org.hertsig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Printing {
    private int id;
    private int setid;
    private int cardid;
    private int multiverseid;
    private String number;
    private String rarity;
    private String originaltext;
    private String originaltype;
    private String flavortext;
}
