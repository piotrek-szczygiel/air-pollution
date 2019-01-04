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

import static air.pollution.Format.format;

/**
 * Handles reading and writing cache to file archive.
 */
class CacheFile {
    private Logger logger = Logger.getLogger(this);
    private File file;

    CacheFile(File file) {
        this.file = file;
    }

    /**
     * Load cache from file archive.
     *
     * @param disableRefresh if true, don't refresh if cache isn't up-to-date
     * @return cache
     * @see Cache
     */
    Cache load(boolean disableRefresh) {
        if (file == null) {
            return null;
        }

        Cache cache;
        Stopwatch stopwatch;

        // Open GZIP File input stream
        try (FileInputStream fileInputStream = new FileInputStream(file);
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             Reader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {

            logger.debug("loading cache from compressed archive %s...", format(file));

            Gson gson = new GsonBuilder().create();

            stopwatch = Stopwatch.createStarted();
            cache = gson.fromJson(reader, Cache.class);
            stopwatch.stop();
        } catch (Exception e) {
            logger.warn("error while loading cache: %s", e.toString());
            return null;
        }

        logger.info("loaded cache from %s in %s", format(file), format(stopwatch));

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastUpdatedDate = cache.getCacheDate();

        long minutesDifference = ChronoUnit.MINUTES.between(lastUpdatedDate, currentDate);
        logger.debug("loaded cache was last updated at %s (%s minutes ago)",
                format(lastUpdatedDate), format(minutesDifference));

        // Check if last update was done earlier than an hour ago
        // Check also if it wasn't done at the previous hour (17:58 last, 18:03 current => UPDATE)
        if (minutesDifference >= 60 || currentDate.getHour() != lastUpdatedDate.getHour()) {
            logger.warn("loaded cache is not up-to-date");

            if (!disableRefresh) {
                return null;
            }
        }

        return cache;
    }

    /**
     * Save cache to file archive.
     *
     * @param cache cache
     * @see Cache
     */
    void save(Cache cache) {
        Stopwatch stopwatch;

        // Open GZIP File output stream
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             Writer writer = new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8)) {

            logger.debug("saving cache to compressed archive %s...", format(file));

            Gson gson = new GsonBuilder().create();

            stopwatch = Stopwatch.createStarted();
            gson.toJson(cache, writer);
            stopwatch.stop();
        } catch (Exception e) {
            logger.warn("error while saving cache: %s", e.toString());
            return;
        }

        logger.info("saved cache to %s in %s", format(file), format(stopwatch));
    }
}
