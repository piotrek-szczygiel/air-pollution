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
    private String fileName;

    CacheFile(String fileName) {
        this.fileName = fileName;
    }

    Cache load() {
        Cache cache;
        Stopwatch stopwatch;

        // Open GZIP input stream
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             Reader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {

            logger.debug("loading cache from " + Format.file(fileName) + "~ compressed archive...");

            Gson gson = new GsonBuilder().create();

            stopwatch = Stopwatch.createStarted();
            cache = gson.fromJson(reader, Cache.class);
            stopwatch.stop();
        } catch (Exception e) {
            logger.warn("error while loading cache: " + e);
            return null;
        }

        logger.info("loaded cache from " + Format.file(fileName) + "~ in " + Format.size(stopwatch));

        LocalDateTime currentTime = LocalDateTime.now();
        long minutesDifference = ChronoUnit.MINUTES.between(cache.getLastUpdated(), currentTime);
        logger.debug("loaded cache was updated " + Format.size(minutesDifference) + "~ minutes ago");

        long minutesDifferenceSinceThisHour = currentTime.getMinute() - minutesDifference;

        if (minutesDifferenceSinceThisHour < 0) {
            logger.info("loaded cache is not up-to-date, refreshing cache...");
            return null;
        }

        return cache;
    }

    void save(Cache cache) {
        Stopwatch stopwatch;

        // Open GZIP output stream
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             Writer writer = new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8)) {

            logger.debug("saving cache to " + Format.file(fileName) + "~ compressed archive...");

            Gson gson = new GsonBuilder().create();

            stopwatch = Stopwatch.createStarted();
            gson.toJson(cache, writer);
            stopwatch.stop();
        } catch (Exception e) {
            logger.warn("error while saving cache: " + e);
            return;
        }

        logger.info("saved cache to " + Format.file(fileName) + "~ in " + Format.size(stopwatch));
    }
}
