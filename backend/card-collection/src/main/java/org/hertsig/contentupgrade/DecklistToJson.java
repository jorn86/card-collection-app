package org.hertsig.contentupgrade;

import java.util.Scanner;

public class DecklistToJson {
    public static void main(String... args) {
        try (Scanner s = new Scanner(System.in)) {
            while (true) {
                String line = s.nextLine();
                if (line.trim().isEmpty()) {
                    System.out.println();
                    continue;
                }

                int index = line.indexOf(' ');
                if (index <= 0) continue;
                System.out.printf("\t\t{ \"amount\": %s, \"name\": \"%s\" },%n", line.substring(0, index), line.substring(index + 1).replace("Ae", "Æ"));
            }
        }
    }
}
