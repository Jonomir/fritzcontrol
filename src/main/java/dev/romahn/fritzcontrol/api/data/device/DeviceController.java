package dev.romahn.fritzcontrol.api.data.device;

import dev.romahn.fritzcontrol.api.FritzBoxClient;
import dev.romahn.fritzcontrol.api.data.device.dao.DeviceDAO;
import dev.romahn.fritzcontrol.api.data.device.dao.dto.Device;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DeviceController {

    private DeviceDAO deviceDAO;

    public DeviceController(FritzBoxClient client) {
        this.deviceDAO = new DeviceDAO(client);
    }

    public void setProfilesForDevices(Map<String, String> deviceProfiles) throws IOException {
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
