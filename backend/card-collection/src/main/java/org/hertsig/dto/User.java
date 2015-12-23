package org.hertsig.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String email;
    private UUID inventoryid;
    private List<AuthenticationOption> authenticationOptions;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class AuthenticationOption {
        private String id;
        private String type;
    }
}
