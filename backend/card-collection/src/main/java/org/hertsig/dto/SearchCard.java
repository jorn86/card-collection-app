package org.hertsig.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class SearchCard extends Card {
    private long multiverseid;
    private long multiverseidBack;
    private String setcode;
    private String rarity;
    private boolean setisfallback;
    private boolean split;
    private int inventorycount;
}
