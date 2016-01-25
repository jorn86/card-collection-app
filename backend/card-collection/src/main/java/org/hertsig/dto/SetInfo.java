package org.hertsig.dto;

import lombok.*;

@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
public class SetInfo extends Set {
    private int cards;
    private int prints;
    private int newcards;
    private int reprints;
}
