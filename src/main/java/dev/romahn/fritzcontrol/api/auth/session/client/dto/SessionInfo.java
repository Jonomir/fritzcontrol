package dev.romahn.fritzcontrol.api.auth.session.client.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SessionInfo")
public class SessionInfo {

    @XmlElement(name = "Challenge")
    private String challenge;

    @XmlElement(name = "SID")
    private String sid;

    @XmlElement(name = "BlockTime")
    private String blockTime;

    public String getChallenge() {
        return challenge;
    }

    public String getSid() {
        return sid;
    }

    public String getBlockTime() {
        return blockTime;
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "challenge='" + challenge + '\'' +
                ", sid='" + sid + '\'' +
                ", blockTime='" + blockTime + '\'' +
                '}';
    }
}
