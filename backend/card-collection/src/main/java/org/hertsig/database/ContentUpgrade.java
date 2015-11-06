package org.hertsig.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import org.hertsig.dao.SetDao;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.DBI;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
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
        try (SetDao setDao = dbi.open(SetDao.class);
                Reader sets = ensureSetFile()) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            Map<String, FullSet> map = gson.fromJson(sets, Types.mapOf(String.class, FullSet.class));
            for (FullSet fullSet : map.values()) {
                UUID setId = ensureSet(setDao, fullSet);
                for (FullSet.Card card : fullSet.cards) {
                    UUID cardId = ensureCard(card);
                    ensurePrinting(cardId, setId, card);
                }
            }
        }
        catch (IOException e) {
            log.error("Exception during content upgrade", e);
        }
    }

    private void ensurePrinting(UUID cardId, UUID setId, FullSet.Card card) {
        
    }

    private UUID ensureCard(FullSet.Card card) {
        return null;
    }

    private UUID ensureSet(SetDao setDao, FullSet fullSet) {
        Set set = setDao.get(fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode);
        if (set == null) {
            return setDao.create(new Set(null, fullSet.gathererCode == null ? fullSet.code : fullSet.gathererCode, fullSet.code, fullSet.name, fullSet.releaseDate));
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
        if (!Files.isRegularFile(folder.resolve("AllSets-x.json"))) {
            log.debug("Downloading sets file");
            try (ZipInputStream zipInputStream = new ZipInputStream(new URL("http", "mtgjson.com", "/json/AllSets-x.json.zip").openStream());
                    FileOutputStream outputStream = new FileOutputStream("json/AllSets-x.json")) {
                Preconditions.checkState(zipInputStream.getNextEntry().getName().equals("AllSets-x.json"));
                ByteStreams.copy(zipInputStream, outputStream);
            }
        }

        return new InputStreamReader(new FileInputStream("json/AllSets-x.json"), Charsets.UTF_8);
    }
}
