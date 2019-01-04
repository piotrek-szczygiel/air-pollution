package air.pollution;

import java.util.*;

import static air.pollution.Format.MEASUREMENT_DATE_FORMATTER;
import static air.pollution.Format.format;
import static air.pollution.Parameter.*;
import static org.fusesource.jansi.Ansi.ansi;

class CommandFindAboveNorm implements Command {
    private Map<Parameter, Float> norms = new HashMap<>() {{
        put(PM10, 50.0f);
        put(PM25, 25.0f);
        put(NO2, 200.0f);
        put(SO2, 350.0f);
        put(C6H6, 5.0f);
        put(CO, 10000.0f);
    }};

    private Logger logger = Logger.getLogger(this);

    @Override
    @SuppressWarnings("Duplicates")
    public void execute(Cache cache, Options options) {

        logger.debug("displaying measurements above norm for %s stations",
                format(options.stations.size()));

        for (Station station : options.stations) {
            List<RatioAndOutput> averageValuesAndOutputs = new ArrayList<>();

            for (Parameter parameter : options.parameters) {

                // Skip if there are no defined norms
                if (!norms.containsKey(parameter)) {
                    continue;
                }

                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(
                                cache,
                                station,
                                parameter,
                                options.since,
                                options.until);

                if (measurements == null || measurements.size() < 1) {
                    continue;
                }

                List<SensorMeasurement> measurementsAboveNorm = new ArrayList<>();

                for (SensorMeasurement measurement : measurements) {

                    if (measurement.getValue() > norms.get(parameter)) {
                        measurementsAboveNorm.add(measurement);
                    }
                }

                // Skip if there were no measurements above the norm
                if (measurementsAboveNorm.size() < 1) {
                    continue;
                }

                StringBuilder output =
                        new StringBuilder(String.format(
                                "%nShowing %s measurement%s above norm of %s (%s) for %s%n",
                                format(measurementsAboveNorm.size()),
                                (measurementsAboveNorm.size() > 1 ? "s" : ""),
                                format(parameter),
                                format(norms.get(parameter), parameter, false),
                                format(station)));

                float sum = 0.0f;
                int count = 0;

                for (SensorMeasurement measurement : measurementsAboveNorm) {

                    output.append(String.format("%s\t%s%s%s%n",
                            format(measurement.getValue(), measurement.getParameter(), true),
                            ansi().fgBrightBlack(),
                            measurement.getDate().format(MEASUREMENT_DATE_FORMATTER),
                            ansi().reset()));

                    sum += measurement.getValue();
                    count++;
                }

                float average = sum / count;
                float ratio = average / norms.get(parameter);

                RatioAndOutput ratioAndOutput = new RatioAndOutput();
                ratioAndOutput.ratio = ratio;
                ratioAndOutput.output = output.toString();
                averageValuesAndOutputs.add(ratioAndOutput);
            }

            averageValuesAndOutputs.sort(Comparator.comparing(o -> o.ratio));

            for (RatioAndOutput ratioAndOutput : averageValuesAndOutputs) {
                System.out.print(ratioAndOutput.output);
            }
        }
    }

    private class RatioAndOutput {
        Float ratio;
        String output;
    }
}
