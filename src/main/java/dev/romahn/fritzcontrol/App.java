package dev.romahn.fritzcontrol;

import dev.romahn.fritzcontrol.api.FritzBoxClient;
import dev.romahn.fritzcontrol.api.auth.AuthenticationInterceptor;
import dev.romahn.fritzcontrol.api.auth.challenge.impl.Md5AuthenticationStrategy;
import dev.romahn.fritzcontrol.api.data.device.DeviceDAO;
import dev.romahn.fritzcontrol.api.data.device.dto.Device;
import okhttp3.OkHttpClient;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        String fritzBoxUrl = commandLine.getOptionValue("url", "http://fritz.box");
        String username = commandLine.getOptionValue("u");
        String password = commandLine.getOptionValue("p");

        return new Configuration(fritzBoxUrl, username, password);
    }

    private FritzBoxClient createAuthenticatingFritzBoxClient(Configuration configuration) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(configuration, new Md5AuthenticationStrategy()))
                .build();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(configuration.getFritzBoxUrl())
                .build().create(FritzBoxClient.class);
    }

    private void execute(Configuration configuration) throws Exception {

        FritzBoxClient fritzBoxClient = createAuthenticatingFritzBoxClient(configuration);
        DeviceDAO deviceDAO = new DeviceDAO(fritzBoxClient);

        Map<String, String> deviceProfiles = new HashMap<>();

        deviceProfiles.put("Jonathan-PC", "Fight The Addiction");

        setProfilesForDevices(deviceDAO, deviceProfiles);
    }


    private void setProfilesForDevices(DeviceDAO deviceDAO, Map<String, String> deviceProfiles) throws IOException {
        List<Device> devices = deviceDAO.fetch();
        deviceProfiles.forEach((deviceName, profileName) -> setProfileForDevice(devices, deviceName, profileName));
        deviceDAO.send(devices);
    }

    private void setProfileForDevice(List<Device> devices, String deviceName, String profileName) {
        devices.stream().filter(d -> d.getName().equals(deviceName)).forEach(device ->
            device.getProfiles().stream().filter(p -> p.getName().equals(profileName)).findFirst()
                    .ifPresent(device::setCurrentProfile));
    }

}
