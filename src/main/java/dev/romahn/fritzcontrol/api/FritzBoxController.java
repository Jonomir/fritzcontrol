package dev.romahn.fritzcontrol.api;

import dev.romahn.fritzcontrol.Configuration;
import dev.romahn.fritzcontrol.api.auth.AuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FritzBoxController {

    private FritzBoxClient fritzBoxClient;

    public FritzBoxController(Configuration configuration) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(configuration))
                .build();

        this.fritzBoxClient = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(configuration.getFritzBoxUrl())
                .build().create(FritzBoxClient.class);
    }

    public List<Device> getDevices() throws Exception {

        Response<ResponseBody> kidLisResponse = fritzBoxClient.getData("kidLis").execute();

        if (kidLisResponse.isSuccessful()) {
            return parseDeviceTable(kidLisResponse.body().string());
        } else {
            throw new HttpException(kidLisResponse);
        }
    }

    private List<Device> parseDeviceTable(String in) {
        Document document = Jsoup.parse(in);
        Elements deviceTable = document.select("[id=uiDevices]");

        Map<String, Profile> profiles = parseProfiles(deviceTable);

        return deviceTable.select("[class=block]").stream().map(e -> {
            Element device = e.parent();

            String id = device.selectFirst("[data-uid]").attr("data-uid");
            String name = device.selectFirst("[class=name]").attr("title");
            Profile profile = profiles.get(device.selectFirst("[selected]").attr("value"));

            return new Device(id, name, profile);
        }).collect(Collectors.toList());
    }

    private Map<String, Profile> parseProfiles(Elements deviceTable) {
        Element profileData = deviceTable.select("select").first();
        return profileData.children().stream().map(profile -> {
            String id = profile.attr("value");
            String name = profile.text();
            return new Profile(id, name);
        }).collect(Collectors.toMap(Profile::getId, p -> p));
    }
}
