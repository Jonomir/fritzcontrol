package dev.romahn.fritzcontrol;

import dev.romahn.fritzcontrol.api.FritzBoxClient;
import dev.romahn.fritzcontrol.api.auth.AuthenticationInterceptor;
import dev.romahn.fritzcontrol.api.auth.challenge.impl.Md5AuthenticationStrategy;
import dev.romahn.fritzcontrol.api.device.DeviceController;
import dev.romahn.fritzcontrol.api.device.dto.Device;
import okhttp3.OkHttpClient;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import retrofit2.Retrofit;

import java.util.List;


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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(configuration, new Md5AuthenticationStrategy()))
                .build();

        FritzBoxClient fritzBoxClient = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(configuration.getFritzBoxUrl())
                .build().create(FritzBoxClient.class);

        DeviceController controller = new DeviceController(fritzBoxClient);

        List<Device> devices = controller.getDevices();
        devices.forEach(System.out::println);

        devices.stream().filter(d -> d.getName().equals("Jonathan-PC")).forEach(device ->
            device.getProfiles().stream().filter(p -> p.getName().equals("Fight The Addiction")).findFirst()
                    .ifPresent(device::setCurrentProfile));

        System.out.println("===================== Device Changed =====================");

        List<Device> changedDevices = controller.saveDevices(devices);
        changedDevices.forEach(System.out::println);
    }

}
