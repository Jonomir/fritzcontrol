package dev.romahn.fritzcontrol.client.api.data.device.dao;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.data.AbstractDataDAO;
import dev.romahn.fritzcontrol.client.api.data.device.dao.dto.Device;
import dev.romahn.fritzcontrol.client.api.data.device.dao.dto.Profile;
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

public class DeviceDAO extends AbstractDataDAO<List<Device>> {

    private final static String DEVICE_PAGE_ID = "kidLis";
    private final static String DEVICE_PAGE_PATH = "/internet/kids_userlist.lua";

    public DeviceDAO(FritzControl fritzControl) {
        super(fritzControl);
    }

    @Override
    protected String getPageId() {
        return DEVICE_PAGE_ID;
    }

    @Override
    protected String getPagePath() {
        return DEVICE_PAGE_PATH;
    }

    @Override
    protected Map<String, String> createValuesToSend(List<Device> devices) {
        Map<String, String> deviceData = devices.stream().collect(Collectors.toMap(Device::getId, d -> d.getCurrentProfile().getId()));
        deviceData.put("apply", "");
        return deviceData;
    }

    @Override
    protected List<Device> parseResponse(ResponseBody response) throws IOException {
        Document document = Jsoup.parse(response.string());
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
