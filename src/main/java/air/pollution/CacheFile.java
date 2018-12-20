package air.pollution;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class CacheFile {
    Logger logger = Logger.getLogger(this);
    private File file;

    CacheFile(File file) {
        this.file = file;
    }

    Cache load() {
        if (file == null) {
            return null;
        }

        Cache cache;
        Stopwatch stopwatch;

        // Open GZIP File input stream
        try (FileInputStream fileInputStream = new FileInputStream(file);
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             Reader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {

            logger.debug("loading cache from " + Format.file(file.getPath()) + "~ compressed archive...");

            Gson gson = new GsonBuilder().create();

            stopwatch = Stopwatch.createStarted();
            cache = gson.fromJson(reader, Cache.class);
            stopwatch.stop();
        } catch (Exception e) {
            logger.warn("error while loading cache: " + e);
            return null;
        }

        logger.info("loaded cache from " + Format.file(file.getPath()) + "~ in " + Format.size(stopwatch));

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastUpdatedDate = cache.getCacheDate();

        long minutesDifference = ChronoUnit.MINUTES.between(lastUpdatedDate, currentDate);
        logger.debug("loaded cache was last updated at " + Format.timestampDate(lastUpdatedDate)
                + "~ (" + Format.size(minutesDifference) + "~ minutes ago)");

        // Check if last update was done earlier than an hour ago
        // Check also if it wasn't done at the previous hour (17:58 last, 18:03 current => UPDATE)
        if (minutesDifference >= 60 || currentDate.getHour() != lastUpdatedDate.getHour()) {
            logger.info("loaded cache is not up-to-date, refreshing cache...");
            return null;
        }

        return cache;
    }

    void save(Cache cache) {
        Stopwatch stopwatch;

        // Open GZIP File output stream
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             Writer writer = new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8)) {

            logger.debug("saving cache to " + Format.file(file.getPath()) + "~ compressed archive...");

            Gson gson = new GsonBuilder().create();

            stopwatch = Stopwatch.createStarted();
            gson.toJson(cache, writer);
            stopwatch.stop();
        } catch (Exception e) {
            logger.warn("error while saving cache: " + e);
            return;
        }

        logger.info("saved cache to " + Format.file(file.getPath()) + "~ in " + Format.size(stopwatch));
    }
}
