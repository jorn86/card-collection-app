package org.hertsig.logic;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharSet;
import org.hertsig.dao.DeckDao;
import org.hertsig.dao.ImportDao;
import org.hertsig.user.HttpRequestException;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Singleton
public class DeckboxImport {
    private static final Escaper LIKE_ESCAPER = Escapers.builder().addEscape('_', "\\_").addEscape('%', "\\%").build();

    private final IDBI dbi;
    private final DeckManager deckManager;

    @Inject
    public DeckboxImport(IDBI dbi, DeckManager deckManager) {
        this.dbi = dbi;
        this.deckManager = deckManager;
    }

    public List<String> importCsv(UUID collectionId, InputStream file) throws IOException {
        List<String> messages = Lists.newArrayList();
        try (CSVReader csv = new CSVReader(new InputStreamReader(file, Charsets.UTF_8));
             ImportDao dao = dbi.open(ImportDao.class);
             DeckDao deckDao = dbi.open(DeckDao.class)) {

            Iterator<String[]> iterator = csv.iterator();
            List<String> titles = Lists.newArrayList(iterator.next());
            int count = titles.indexOf("Count");
            int name = titles.indexOf("Name");
            int edition = titles.indexOf("Edition");
            int section = titles.indexOf("Section");
            log.info("Reading from Deckbox csv columns Count ({}), Name ({}), Edition ({}), Section ({})", count, name, edition, section);
            if (count < 0 || name < 0) {
                throw new HttpRequestException(Response.Status.BAD_REQUEST, "Missing column in CSV");
            }

            Map<String, UUID> boards = Maps.newHashMap();
            if (section == -1) {
                UUID board = dao.createBoard(collectionId, "Deckbox CSV import");
                boards.put(null, board);
            }
            iterator.forEachRemaining(line -> readLine(line, count, name, edition, section, boards, collectionId, dao, deckDao, messages));
        }
        catch (DBIException e) {
            log.error("Error in deckbox import", e);
            throw new HttpRequestException(Response.Status.INTERNAL_SERVER_ERROR, "Error while importing. Partial import may have happened.");
        }
        return messages;
    }

    private void readLine(String[] line, int countI, int nameI, int editionI, int sectionI, Map<String, UUID> boards,
                          UUID collectionId, ImportDao dao, DeckDao deckDao, List<String> messages) {
        if (line[countI].isEmpty()) return;

        int count = Integer.parseInt(line[countI]);
        String name = line[nameI].replace(" // ", " / ");
        String editionName = editionI < 0 ? null : line[editionI];
        String section = sectionI < 0 ? null : line[sectionI];
        UUID board = ensureBoard(dao, collectionId, section, boards);
        Integer card = dao.getCardByName(name);
        log.trace("Importing line {}, {}, {} (card {})", count, name, editionName, card);
        if (card == null) {
            messages.add(String.format("Cannot find card %s (%s)", name, editionName));
            return;
        }

        if (Strings.isNullOrEmpty(editionName)) {
            deckManager.addCard(deckDao, board, count, card, null);
        }
        else {
            Integer edition = dao.getEditionByName(editionName);
            if (edition == null) {
                edition = dao.getEditionFallback('%' + LIKE_ESCAPER.escape(editionName) + '%', editionName);
            }
            if (edition == null) {
                messages.add(String.format("Cannot find edition %s for card %s", editionName, name));
                return;
            }
            Integer printing = dao.getPrinting(card, edition);
            if (printing == null) {
                messages.add(String.format("Card %s was not printed in edition %s, importing without specific edition", name, editionName));
            }
            deckManager.addCard(deckDao, board, count, card, printing);
        }
    }

    private UUID ensureBoard(ImportDao dao, UUID collectionId, String section, Map<String, UUID> boards) {
        if (!boards.containsKey(section)) {
            UUID board = dao.createBoard(collectionId, section);
            boards.put(section, board);
        }
        return boards.get(section);
    }
}
