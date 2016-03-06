package org.hertsig.preconstructed;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class DecklistToJson {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String... args) throws IOException {
        try (Scanner s = new Scanner(System.in)) {
            while (true) {
                readFile(s);
            }
        }
    }

    private static void readFile(Scanner s) throws IOException {
        PreconstructedDeck deck = new PreconstructedDeck();

        System.out.println("Name:");
        deck.setName(s.nextLine());
        System.out.println("Set code:");
        deck.setSet(s.nextLine());
        System.out.println("Tags (comma separated):");
        deck.setTag(Lists.newArrayList(s.nextLine().split(",")));

        List<PreconstructedDeck.Card> cards = Lists.newArrayList();
        deck.setMainboard(cards);
        System.out.println("Mainboard:");
        readCards(s, cards);

        cards = Lists.newArrayList();
        System.out.println("Sideboard:");
        readCards(s, cards);
        if (!cards.isEmpty()) {
            deck.setSideboard(cards);
        }

        Path dir = Paths.get("backend", "card-collection", "json", "preconstructed", deck.getTag().get(0).toLowerCase(), deck.getSet().toLowerCase());
        Files.createDirectories(dir);
        Path file = dir.resolve(name(deck.getName()));
        Files.createFile(file);
        try (Writer w = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
            gson.toJson(deck, w);
            w.write("\n");
        }
    }

    private static void readCards(Scanner s, List<PreconstructedDeck.Card> cards) {
        while (s.hasNext()) {
            String line = s.nextLine();
            try {
                if (line.equals("n")) break;
                int index = line.indexOf(' ');
                if (index <= 0) continue;

                PreconstructedDeck.Card card = new PreconstructedDeck.Card();
                card.setAmount(Integer.parseInt(line.substring(0, index)));
                card.setName(line.substring(index + 1).replace("Ae", "Æ"));
                cards.add(card);
            }
            catch (RuntimeException e) {
                System.err.printf("For line %s%n%s: %s%n", line, e.getClass().getName(), e.getMessage());
            }
        }
    }

    private static String name(String name) {
        return name.replace(" ", "").replace("'", "").toLowerCase() + ".json";
    }
}
