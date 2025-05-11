package tech.rket.shared.infrastructure.agent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import ua_parser.Parser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserAgentParser {
    private static final Parser uaParser = new Parser();
    private static final UserAgentAnalyzer USER_AGENT_ANALYZER = UserAgentAnalyzer
            .newBuilder()
            .showFullVersion()
            .withCache(10000)
            .build();

    public static ParsedUserAgent parse(String userAgentString) {
        var yauaa = USER_AGENT_ANALYZER.parse(userAgentString);
        var osVersionField = yauaa.get("OperatingSystemVersion");
        var agentVersionField = yauaa.get("AgentVersion");
        String agentVersion = agentVersionField.getValue();
        if (agentVersionField.isDefaultValue() || agentVersionField.getValue().contains("?")) {
            var ag = uaParser.parseUserAgent(userAgentString);
            agentVersion = ag.patch == null || ag.patch.isBlank() ? String.format("%s.%s", ag.major, ag.minor) : String.format("%s.%s.%s", ag.major, ag.minor, ag.minor);
        }
        String osVersion = osVersionField.getValue();
        if (osVersionField.isDefaultValue() || osVersionField.getValue().contains("?")) {
            var os = uaParser.parseOS(userAgentString);
            osVersion = os.patchMinor == null || os.patchMinor.isBlank() ? String.format("%s.%s", os.major, os.minor) : String.format("%s.%s.%s", os.major, os.minor, os.patchMinor);
        }
        return new ParsedUserAgent(
                new Device(yauaa.get("DeviceClass").getValue(),
                        yauaa.get("DeviceName").getValue(),
                        yauaa.get("DeviceBrand").getValue()
                ),
                new Agent(yauaa.get("AgentClass").getValue(),
                        yauaa.get("AgentName").getValue(),
                        agentVersion
                ),
                new OperatingSystem(
                        yauaa.get("OperatingSystemClass").getValue(),
                        yauaa.get("OperatingSystemName").getValue(),
                        osVersion
                )
        );
    }
}
