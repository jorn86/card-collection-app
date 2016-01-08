package org.hertsig.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
public class SetInfo extends Set {
    private int cards;
    private int prints;
    private int newcards;
    private int reprints;
}
