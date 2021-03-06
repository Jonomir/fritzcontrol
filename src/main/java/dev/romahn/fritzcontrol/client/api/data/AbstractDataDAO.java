package dev.romahn.fritzcontrol.client.api.data;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.FritzBoxClient;
import dev.romahn.fritzcontrol.client.util.CallUtil;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractDataDAO<T> {

    private final FritzBoxClient client;

    public AbstractDataDAO(FritzControl fritzControl) {
        Objects.requireNonNull(fritzControl, "fritzControl must not be null");
        this.client = fritzControl.getClient();
    }

    protected abstract String getPageId();

    protected abstract String getPagePath();

    protected abstract T parseResponse(ResponseBody response) throws IOException;

    protected abstract Map<String, String> createValuesToSend(T data);


    public T fetch() throws IOException {
        ResponseBody response = CallUtil.executeAndCheck(client.fetchData(getPageId()));
        return parseResponse(response);
    }

    public T send(T data) throws IOException {
        Map<String, String> valuesToSend = createValuesToSend(data);
        valuesToSend.put("oldpage", getPagePath());

        ResponseBody response = CallUtil.executeAndCheck(client.sendData(valuesToSend));
        return parseResponse(response);
    }

}
