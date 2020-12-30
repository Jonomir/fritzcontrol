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
    private Map<String, String> profiles;

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

        Response<ResponseBody> response = fritzBoxClient.getData("kidLis").execute();

        if (response.isSuccessful()) {
            return parseDeviceTable(response.body().string());
        } else {
            throw new HttpException(response);
        }
    }

    private List<Device> parseDeviceTable(String in) {
        Document document = Jsoup.parse(in);
        Elements deviceTable = document.select("[id=uiDevices]");

        profiles = parseProfiles(deviceTable);

        return deviceTable.select("[class=block]").stream().map(e -> {
            Element device = e.parent();

            String id = device.selectFirst("[data-uid]").attr("data-uid");
            String name = device.selectFirst("[class=name]").attr("title");
            String profile = device.selectFirst("[selected]").attr("value");

            return new Device(id, name, profile);
        }).collect(Collectors.toList());
    }

    private Map<String, String> parseProfiles(Elements deviceTable) {
        Element profileData = deviceTable.select("select").first();
        return profileData.children().stream().collect(Collectors.toMap(Element::text, p -> p.attr("value")));
    }

    public Map<String, String> getProfiles() {
        return profiles;
    }

    public List<Device> saveDevices(List<Device> devices) throws Exception {

        Map<String, String> deviceData = devices.stream().collect(Collectors.toMap(d -> "profile:" + d.getId(), Device::getProfile));
        deviceData.put("apply", "");
        deviceData.put("oldpage", "/internet/kids_userlist.lua");

        Response<ResponseBody> response = fritzBoxClient.sendData(deviceData).execute();

        if (response.isSuccessful()) {
            return parseDeviceTable(response.body().string());
        } else {
            throw new HttpException(response);
        }
    }
}
