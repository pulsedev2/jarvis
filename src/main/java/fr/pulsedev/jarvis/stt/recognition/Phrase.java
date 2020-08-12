package fr.pulsedev.jarvis.stt.recognition;


import fr.pulsedev.jarvis.enums.AssistantName;

import java.util.Arrays;
import java.util.List;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 15:23.
 */
public class Phrase {
    String text;
    List<String> words;

    public Phrase(String text) {
        this.text = text;
        this.words = Arrays.asList(text.split(" "));
    }

    public String getText() {
        return text;
    }

    public boolean isACommand(){
        for(String assistantName : AssistantName.getNames()){
            if(text.startsWith(assistantName)) return true;
        }
        return false;
    }
}
