package air.pollution;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "air-pollution",
        mixinStandardHelpOptions = true,
        version = "Air Pollution Information v1.0 by Piotr Szczygieł 2018",
        header = "Display information about air pollution in Poland.",
        description = "Displays information about air pollution usign JSON API provided by the government.",
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class App implements Runnable {
    @Option(names = {"-t", "--town"}, required = true, paramLabel = "TOWN", description = "town")
    private String town;

    public static void main(String[] args) {
        CommandLine.run(new App(), args);
    }

    @Override
    public void run() {
        System.out.println("Town: " + town);
    }
}
