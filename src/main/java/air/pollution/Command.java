package air.pollution;

/**
 * Interface for air pollution commands like graphing, showing measurements, etc.
 */
interface Command {

    /**
     * Execute the command.
     *
     * @param cache   cache for object fetching
     * @param options options for controlling command execution
     */
    void execute(Cache cache, Options options);
}

