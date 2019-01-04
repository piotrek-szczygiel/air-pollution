package air.pollution;

import java.util.LinkedList;
import java.util.List;

class OptionStrategy {
    private boolean listStations;
    private boolean airIndex;
    private boolean measurement;
    private boolean average;
    private boolean fluctuation;
    private boolean findMinMaxParameter;
    private boolean findMinMaxValue;
    private boolean findAboveNorm;
    private boolean worstStations;
    private boolean graph;

    private Logger logger = Logger.getLogger(this);

    OptionStrategy(boolean listStations, boolean airIndex, boolean measurement, boolean average,
                   boolean fluctuation, boolean findMinMaxParameter, boolean findMinMaxValue,
                   boolean findAboveNorm, boolean worstStations, boolean graph) {

        this.listStations = listStations;
        this.airIndex = airIndex;
        this.measurement = measurement;
        this.average = average;
        this.fluctuation = fluctuation;
        this.findMinMaxParameter = findMinMaxParameter;
        this.findMinMaxValue = findMinMaxValue;
        this.findAboveNorm = findAboveNorm;
        this.worstStations = worstStations;
        this.graph = graph;
    }

    void invoke(Cache cache, Options options) {
        List<Command> commands = new LinkedList<>();

        // --list
        if (listStations) {
            commands.add(new CommandListAllStations());
        }

        // --air-index
        if (airIndex) {
            commands.add(new CommandAirIndex());
        }

        // --measurement
        if (measurement) {
            commands.add(new CommandMeasurement());
        }

        // --average
        if (average) {
            commands.add(new CommandAveragePollution());
        }

        // --fluctuation
        if (fluctuation) {
            commands.add(new CommandHighestFluctuation());
        }

        // --find-min-max-parameter
        if (findMinMaxParameter) {
            commands.add(new CommandFindMinMaxParameter());
        }

        // --find-min-max-value
        if (findMinMaxValue) {
            commands.add(new CommandFindMinMaxValue());
        }

        // --find-above-norm
        if (findAboveNorm) {
            commands.add(new CommandFindAboveNorm());
        }

        // --worst-stations
        if (worstStations) {
            commands.add(new CommandWorstStations());
        }

        // --graph
        if (graph) {
            commands.add(new CommandGraph());
        }

        // Execute all the requested commands with supplied options
        for (Command command : commands) {
            logger.debug("executing command %s", command.getClass().getSimpleName());
            command.execute(cache, options);
        }
    }
}
