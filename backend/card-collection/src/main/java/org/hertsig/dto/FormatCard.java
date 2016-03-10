package org.hertsig.dto;

import lombok.*;

@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
public class FormatCard extends Card {
    private Format format;
    private Legality legality;
}
