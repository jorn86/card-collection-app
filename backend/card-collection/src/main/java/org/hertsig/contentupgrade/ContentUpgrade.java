package org.hertsig.contentupgrade;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hertsig.dao.ContentUpgradeDao;
import org.hertsig.dto.Card;
import org.hertsig.dto.Color;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.DBIException;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.util.Types;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ContentUpgrade {
    @Inject
    public ContentUpgrade(DBI dbi) {
        try (ContentUpgradeDao dao = dbi.open(ContentUpgradeDao.class); Reader sets = ensureSetFile()) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            Map<String, FullSet> map = gson.fromJson(sets, Types.mapOf(String.class, FullSet.class));
            for (FullSet fullSet : map.values()) {
                UUID setId = ensureSet(dao, fullSet);
                for (FullSet.Card card : fullSet.cards) {
                    UUID cardId = ensureCard(dao, card);
                    ensurePrinting(dao, cardId, setId, card);
                }
            }
        }
        catch (IOException e) {
            log.error("Exception during content upgrade", e);
        }
    }

    private void ensurePrinting(ContentUpgradeDao dao, UUID cardId, UUID setId, FullSet.Card card) {
        
    }

    private UUID ensureCard(ContentUpgradeDao dao, FullSet.Card card) {
        Card existingCard = dao.getCard(card.getName());
        if (existingCard == null) {
            try {
                return dao.createCard(new Card(null, card.getName(), card.getType(), card.getSupertypes(),
                        card.getSubtypes(), ImmutableList.of(Color.W), card.getLayout()));
            }
            catch (DBIException e) {
                log.debug("Inserting card {} failed", card, e);
                return null;
            }
        }
        return existingCard.getId();
    }

    private UUID ensureSet(ContentUpgradeDao dao, FullSet fullSet) {
        Set set = dao.getSet(fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode);
        if (set == null) {
            return dao.createSet(new Set(null, fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode, fullSet.code, fullSet.name, fullSet.releaseDate));
        }
        if (!fullSet.code.equals(set.getCode()) || !fullSet.name.equals(set.getName()) || !fullSet.releaseDate.equals(set.getReleasedate())) {
            log.warn("Inconsistency: database {}; external {} {}", set, fullSet.code, fullSet.name, fullSet.releaseDate);
        }
        return set.getId();
    }

    private Reader ensureSetFile() throws IOException {
        Path folder = Paths.get("json");
        if (!Files.isDirectory(folder)) {
            Files.createDirectory(folder);
        }

        Path file = folder.resolve("AllSets-x.json");
        if (!Files.isRegularFile(file)) {
            log.debug("Downloading sets file");
            try (ZipInputStream zipInputStream = new ZipInputStream(new URL("http", "mtgjson.com", "/json/AllSets-x.json.zip").openStream());
                    FileOutputStream outputStream = new FileOutputStream(file.toFile())) {
                Preconditions.checkState(zipInputStream.getNextEntry().getName().equals("AllSets-x.json"), "Invalid zip file contents");
                ByteStreams.copy(zipInputStream, outputStream);
            }
        }

        return new InputStreamReader(new FileInputStream(file.toFile()), Charsets.UTF_8);
    }
}
