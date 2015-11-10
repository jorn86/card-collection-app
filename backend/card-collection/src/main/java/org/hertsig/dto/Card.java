package org.hertsig.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Card {
    private UUID id;
    private String name;
    private String fulltype;
    private List<String> supertypes;
    private List<String> subtypes;
    private String cost;
    private double cmc;
    private List<Color> colors;
    private String text;
    private String power;
    private String toughness;
    private Integer loyalty;
    private String layout;
    private UUID splitcardparent;
    private UUID doublefacefront;
}
