package org.hertsig.logic;

import com.google.common.base.Objects;
import org.hertsig.dao.DeckDao;
import org.hertsig.dto.DeckRow;
import org.skife.jdbi.v2.IDBI;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DeckManager {
    private final IDBI dbi;

    @Inject
    public DeckManager(IDBI dbi) {
        this.dbi = dbi;
    }

    public void addCard(UUID boardId, int count, int card, Integer printing) {
        try (DeckDao dao = dbi.open(DeckDao.class)) {
            addCard(dao, boardId, count, card, printing);
        }
    }

    public void addCard(DeckDao dao, UUID boardId, int count, int card, Integer printing) {
        List<DeckRow> rows = dao.getBoardRows(boardId, card);
        Optional<DeckRow> existingPrinting = rows.stream().filter(row -> Objects.equal(row.getPrintingid(), printing)).findAny();
        if (existingPrinting.isPresent()) {
            DeckRow existing = existingPrinting.get();
            dao.updateRow(new DeckRow(existing.getId(), null, 0, null, existing.getAmount() + count));
        }
        else if (rows.size() > 0 && printing == null) {
            DeckRow existing = rows.get(0);
            dao.updateRow(new DeckRow(existing.getId(), null, 0, null, existing.getAmount() + count));
        }
        else {
            dao.addCardToDeck(new DeckRow(null, boardId, card, printing, count));
        }

    }
}
