package org.hertsig.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Card {
    private int id;
    private String name;
    private String fulltype;
    private List<String> supertypes;
    private List<String> types;
    private List<String> subtypes;
    private String cost;
    private double cmc;
    @JsonIgnore  private List<Color> colors;
    private String text;
    private String power;
    private String toughness;
    private Integer loyalty;
    private String layout;
    private Integer splitcardparent;
    private Integer doublefacefront;
}
