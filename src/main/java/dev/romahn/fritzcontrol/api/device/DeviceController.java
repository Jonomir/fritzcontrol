package dev.romahn.fritzcontrol.api.device;

import dev.romahn.fritzcontrol.api.CallUtil;
import dev.romahn.fritzcontrol.api.FritzBoxClient;
import dev.romahn.fritzcontrol.api.device.dto.Device;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeviceController {

    private FritzBoxClient fritzBoxClient;
    private Map<String, String> profiles;

    public DeviceController(FritzBoxClient fritzBoxClient) {
        this.fritzBoxClient = fritzBoxClient;
    }

    public List<Device> getDevices() throws IOException {
        ResponseBody response = CallUtil.executeAndCheck(fritzBoxClient.getData("kidLis"));
        return parseDeviceTable(response);
    }

    public List<Device> saveDevices(List<Device> devices) throws IOException {

        Map<String, String> deviceData = devices.stream().collect(Collectors.toMap(d -> "profile:" + d.getId(), Device::getProfile));
        deviceData.put("apply", "");
        deviceData.put("oldpage", "/internet/kids_userlist.lua");

        ResponseBody response = CallUtil.executeAndCheck(fritzBoxClient.sendData(deviceData));
        return parseDeviceTable(response);
    }

    private List<Device> parseDeviceTable(ResponseBody in) throws IOException {
        Document document = Jsoup.parse(in.string());
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

}
