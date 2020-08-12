package fr.pulsedev.jarvis.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis.enums
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 15:21.
 */
public enum AssistantName {
    NAME("jarvis"),
    NAME_CALL("et jarvis"),
    NAME_ASK("dis jarvis");

    String text;

    AssistantName(String text) {
        this.text = text;
    }

    public static List<String> getNames(){
        List<String> result = new ArrayList<>();
        result.add(NAME.text);
        result.add(NAME_ASK.text);
        result.add(NAME_CALL.text);
        return result;
    }
}
