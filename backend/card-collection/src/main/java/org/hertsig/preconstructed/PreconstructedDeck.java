package org.hertsig.preconstructed;

import lombok.Data;

import java.util.List;

@Data
class PreconstructedDeck {
    private List<String> tag;
    private String name;
    private String set;
    private List<Card> mainboard;
    private List<Card> sideboard;

    @Data
    static class Card {
        private int amount;
        private String name;
        private String edition;
    }
}
