package org.hertsig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeckRow {
    private UUID id;
    private UUID deckid;
    private UUID cardid;
    private UUID printingid;
    private int amount;
}
