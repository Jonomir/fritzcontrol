package dev.romahn.fritzcontrol.api.device;

import dev.romahn.fritzcontrol.api.CallUtil;
import dev.romahn.fritzcontrol.api.FritzBoxClient;
import dev.romahn.fritzcontrol.api.device.dto.Device;
import dev.romahn.fritzcontrol.api.device.dto.Profile;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DeviceController {

    private final static String DEVICE_PAGE_ID = "kidLis";
    private final static String DEVICE_PAGE_PATH = "/internet/kids_userlist.lua";


    private FritzBoxClient fritzBoxClient;
    private Map<String, String> profiles;

    public DeviceController(FritzBoxClient fritzBoxClient) {
        this.fritzBoxClient = fritzBoxClient;
    }

    public List<Device> getDevices() throws IOException {
        ResponseBody response = CallUtil.executeAndCheck(fritzBoxClient.getData(DEVICE_PAGE_ID));
        return parseDeviceTable(response);
    }

    public List<Device> saveDevices(List<Device> devices) throws IOException {

        Map<String, String> deviceData = devices.stream().collect(Collectors.toMap(Device::getId, d -> d.getCurrentProfile().getId()));
        deviceData.put("apply", "");
        deviceData.put("oldpage", DEVICE_PAGE_PATH);

        ResponseBody response = CallUtil.executeAndCheck(fritzBoxClient.sendData(deviceData));
        return parseDeviceTable(response);
    }

    private List<Device> parseDeviceTable(ResponseBody in) throws IOException {
        Document document = Jsoup.parse(in.string());
        Elements deviceTableData = document.select("[id=uiDevices]");

        return deviceTableData.select("select").stream().map(profileListData -> {
            String id = profileListData.attr("name");

            Element deviceData = profileListData.parent().parent();
            String name = deviceData.selectFirst("[class=name]").attr("title");

            AtomicReference<Profile> currentProfile = new AtomicReference<>();

            List<Profile> profiles = profileListData.children().stream().map(profileData -> {
                Profile profile = new Profile(profileData.attr("value"), profileData.text());

                if (profileData.hasAttr("selected")) {
                    currentProfile.set(profile);
                }

                return profile;
            }).collect(Collectors.toList());

            return new Device(id, name, currentProfile.get(), profiles);
        }).collect(Collectors.toList());
    }

}
