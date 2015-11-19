package org.hertsig.contentupgrade;

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
    String magicCardsInfoCode;
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
        String layout;
        int multiverseid;
        List<Integer> variations;
        String originalText;
        String originalType;
        String imageName;
        String watermark;
        Border border;
        boolean timeshifted;

        // vanguard only
        int hand;
        int life;
    }

    public enum Border {
        black, white, silver;
    }
}
