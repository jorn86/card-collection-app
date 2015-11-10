package org.hertsig.dto;

public enum Color {
    W, U, B, R, G;

    public static Color forName(String name) {
        switch (name) {
            case "White": return W;
            case "Blue": return U;
            case "Black": return B;
            case "Red": return R;
            case "Green": return G;
        }
        throw new IllegalArgumentException("Unrecognized color " + name);
    }
}
