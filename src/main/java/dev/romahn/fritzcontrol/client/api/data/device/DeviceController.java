package dev.romahn.fritzcontrol.client.api.data.device;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.data.device.dao.DeviceDAO;
import dev.romahn.fritzcontrol.client.api.data.device.dao.dto.Device;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DeviceController {

    private final DeviceDAO deviceDAO;

    public DeviceController(FritzControl fritzControl) {
        this.deviceDAO = new DeviceDAO(fritzControl);
    }

    public void setProfilesForDevices(Map<String, String> deviceProfiles) throws IOException {
        List<Device> devices = deviceDAO.fetch();
        deviceProfiles.forEach((deviceName, profileName) -> setProfileForDevice(devices, deviceName, profileName));
        deviceDAO.send(devices);
    }

    private void setProfileForDevice(List<Device> devices, String deviceName, String profileName) {
        devices.stream().filter(
                d -> d.getName().equals(deviceName)).forEach(
                d -> d.getProfiles().stream().filter(
                        p -> p.getName().equals(profileName))
                        .findFirst().ifPresent(d::setCurrentProfile));

        System.out.println("Set " + deviceName + " to " + profileName);
    }

}
