package dev.romahn.fritzcontrol;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.data.device.DeviceController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class App {

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = parseArgs(args);

        App app = new App();
        app.execute(createFritzControl(commandLine), commandLine.getArgList());
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addRequiredOption("u", "username", true, "Username for FritzBox login");
        options.addRequiredOption("p", "password", true, "Password for FritzBox login");
        options.addOption("url", "url", true, "FritzBox Url, default is http://fritz.box");
        options.addOption("a", "auth", true, "Authentication Strategy, possible values MD5 & PBKDF2, default is MD5");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args, false);
    }

    private static FritzControl createFritzControl(CommandLine commandLine) {

        FritzControl.Builder builder = new FritzControl.Builder();

        builder.username(commandLine.getOptionValue("username"));
        builder.password(commandLine.getOptionValue("password"));

        String url = commandLine.getOptionValue("url");
        if (url != null) {
            builder.url(url);
        }

        String auth = commandLine.getOptionValue("auth");
        if (auth != null) {
            builder.authenticationStrategy(auth);
        }

        return builder.build();
    }

    private void execute(FritzControl fritzControl, List<String> args) throws Exception {

        Map<String, String> deviceProfiles = new HashMap<>();

        for (String arg : args) {
            String[] parts = arg.split("=");

            if (parts.length != 2) {
                throw new InvalidParameterException("try device=profile");
            }

            deviceProfiles.put(parts[0], parts[1]);
        }

        DeviceController deviceController = new DeviceController(fritzControl);
        deviceController.setProfilesForDevices(deviceProfiles);
    }

}
