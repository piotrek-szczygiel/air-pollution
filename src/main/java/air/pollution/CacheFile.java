package air.pollution;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class CacheFile {
    Logger logger = Logger.getLogger(this);
    private String fileName;

    CacheFile(String fileName) {
        this.fileName = fileName;
    }

    Cache load() {
        Cache cache;
        Stopwatch stopwatch;

        try (Reader reader = new FileReader(fileName)) {
            logger.debug("loading cache from " + Format.file(fileName) + "~...");

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
        logger.debug("loaded cache update minutes difference since this hour is "
                + Format.size(minutesDifferenceSinceThisHour));

        if (minutesDifferenceSinceThisHour < 0) {
            logger.debug("loaded cache is not up-to-date, refreshing cache...");
            return null;
        }

        return cache;
    }

    void save(Cache cache) {
        Stopwatch stopwatch;

        try (Writer writer = new FileWriter(fileName)) {
            logger.debug("saving cache to " + Format.file(fileName) + "~...");

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
