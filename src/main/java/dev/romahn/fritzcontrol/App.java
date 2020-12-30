package dev.romahn.fritzcontrol;

import dev.romahn.fritzcontrol.api.Device;
import dev.romahn.fritzcontrol.api.FritzBoxController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class App {

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.execute(createConfig(args));
    }

    private static Configuration createConfig(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption("url", "url", true, "FritzBox Url, default is http://fritz.box");
        options.addRequiredOption("u", "username", true, "Username for FritzBox login");
        options.addRequiredOption("p", "password", true, "Password for FritzBox login");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args, true);

        Configuration configuration = new Configuration();
        configuration.setUsername(commandLine.getOptionValue("u"));
        configuration.setPassword(commandLine.getOptionValue("p"));
        configuration.setFritzBoxUrl(commandLine.getOptionValue("url", "http://fritz.box"));

        return configuration;
    }


    private void execute(Configuration configuration) throws Exception {
        FritzBoxController controller = new FritzBoxController(configuration);

        List<Device> devices = controller.getDevices();
        Map<String, String> profiles = controller.getProfiles();

        System.out.println(profiles);
        devices.forEach(System.out::println);

        Optional<Device> deviceToChange = devices.stream().filter(d -> d.getName().equals("Jonathan-PC")).findFirst();
        deviceToChange.ifPresent(device -> {
            device.setProfile(profiles.get("Fight The Addiction"));
        });

        System.out.println("========================= Device Changed =====================");

        List<Device> changedDevices = controller.saveDevices(devices);
        changedDevices.forEach(System.out::println);

    }


}
