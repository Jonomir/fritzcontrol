package dev.romahn.fritzcontrol;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.data.device.DeviceController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashMap;
import java.util.Map;


public class App {

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.execute(createConfig(args));
    }

    private static FritzControl createConfig(String[] args) throws ParseException {

        Options options = new Options();
        options.addRequiredOption("u", "username", true, "Username for FritzBox login");
        options.addRequiredOption("p", "password", true, "Password for FritzBox login");
        options.addOption("url", "url", true, "FritzBox Url, default is http://fritz.box");
        options.addOption("a", "auth", true, "Authentication Strategy, possible values MD5 & PBKDF2, default is MD5");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args, true);

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

    private void execute(FritzControl fritzControl) throws Exception {

        DeviceController deviceController = new DeviceController(fritzControl.getClient());

        Map<String, String> deviceProfiles = new HashMap<>();

        deviceProfiles.put("Jonathan-PC", "Fight The Addiction");

        deviceController.setProfilesForDevices(deviceProfiles);
    }

}
