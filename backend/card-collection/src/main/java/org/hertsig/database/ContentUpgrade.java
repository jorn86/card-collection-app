package org.hertsig.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hertsig.dao.SetDao;
import org.hertsig.dto.Set;
import org.skife.jdbi.v2.DBI;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
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
            log.debug("parsed json {}", map);
            for (FullSet fullSet : map.values()) {
                Set set = setDao.get(fullSet.gathererCode);
                if (set == null) {
                    setDao.create(new Set(null, fullSet.gathererCode, fullSet.code, fullSet.name));
                }
            }
        }
        catch (IOException e) {
            log.error("Exception during content upgrade", e);
        }
    }

    private Reader ensureSetFile() throws IOException {
        if (!Files.isDirectory(Paths.get("json"))) {
            Files.createDirectory(Paths.get("json"));
        }
        if (!Files.exists(Paths.get("json", "AllSets.json.zip"))) {
            log.info("Downloading sets file");
            try (InputStream in = new URL("http", "mtgjson.com", "/json/AllSets-x.json.zip").openStream();
                    FileOutputStream out = new FileOutputStream("json/AllSets.json.zip")) {
                CharStreams.copy(new InputStreamReader(in), new OutputStreamWriter(out));
            }
        }
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream("json/AllSets.json.zip"));
        Preconditions.checkState(zipInputStream.getNextEntry().getName().equals("AllSets.json"));
        return new InputStreamReader(zipInputStream, Charsets.UTF_8);
    }
}
