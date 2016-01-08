package org.hertsig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeckBoard {
    private UUID id;
    private UUID deckid;
    private String name;
    private int order;
    private List<DeckEntry> cards;
}
