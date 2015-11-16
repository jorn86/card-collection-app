package org.hertsig.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Deck {
    private UUID id;
    private UUID userid;
    private String name;
    private List<DeckEntry> cards;
}
