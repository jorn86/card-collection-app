package org.hertsig.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeckRow {
    private UUID id;
    private UUID boardid;
    private int cardid;
    private Integer printingid;
    private int amount;
}
