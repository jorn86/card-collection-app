package org.hertsig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeckEntry {
    private UUID id;
    private int amount;
    private String name;
    private String type;
    private String subtype;
    private String cost;
    private double cmc;
    private long multiverseid;
    private String setcode;
    private String rarity;
}
