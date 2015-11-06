package org.hertsig.database;

import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
public class FullSet {
    String name;
    String code;
    String gathererCode;
    String oldCode;
    Date releaseDate;
    Border border;
    String type;
    String block;
    boolean onlineOnly;
    //	List<String> booster; // care
    List<Card> cards;

    @Data
    @FieldDefaults(level=AccessLevel.PRIVATE)
    public static class Card {
        String name;
        List<String> names;
        String manaCost;
        Double cmc;
        List<String> colors;
        String type;
        List<String> supertypes;
        List<String> types;
        List<String> subtypes;
        String rarity;
        String text;
        String flavor;
        String artist;
        String number;
        String power;
        String toughness;
        Integer loyalty;
        Layout layout;
        int multiverseid;
        List<Integer> variations;
        String imageName;
        String watermark;
        Border border;
        boolean timeshifted;

        // vanguard only
        int hand;
        int life;
    }

    public static enum Border {
        black, white, silver;
    }

    public static enum Layout {
        normal, split, flip, double_faced, token, plane, scheme, phenomenon, leveler, vanguard;
    }
}
