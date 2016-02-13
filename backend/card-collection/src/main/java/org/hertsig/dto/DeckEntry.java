package org.hertsig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeckEntry {
    private UUID id;
    private int amount;
    private String name;
    private String fulltype;
    private String supertype;
    private String type;
    private String subtype;
    private String cost;
    private double cmc;
    private long multiverseid;
    private long multiverseidBack;
    private String setcode;
    private String rarity;
    private boolean setisfallback;
    private boolean split;
    private int inventorycount;
}
