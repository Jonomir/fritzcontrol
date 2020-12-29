package dev.romahn.fritzcontrol;

import dev.romahn.fritzcontrol.api.FritzBoxClient;
import dev.romahn.fritzcontrol.api.auth.AuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;


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


    private void execute(Configuration configuration) throws IOException {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(configuration))
                .build();

        FritzBoxClient fritzBoxClient = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(configuration.getFritzBoxUrl())
                .build().create(FritzBoxClient.class);

        Response<ResponseBody> kidLisResponse = fritzBoxClient.getData("kidLis").execute();

        if (kidLisResponse.isSuccessful()) {
            Document document = Jsoup.parse(kidLisResponse.body().string());
            Elements deviceTable = document.select(new Evaluator.AttributeWithValue("id", "uiList"));

            System.out.println(deviceTable);

        }

    }

}
