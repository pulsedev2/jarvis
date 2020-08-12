package fr.pulsedev.jarvis.modules;


import fr.pulsedev.jarvis.items.ItemsType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis.modules
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 18:40.
 */
public class HourAskModule extends Module {

    public List<String> keywords = Arrays.asList("heure");
    public List<String> answers = new ArrayList<>();

    public HourAskModule(HashMap<ItemsType, String> arguments) {
        super(arguments);
        answers.add("Il est actuelement, " + java.time.LocalTime.now().getHour() + " heure et " + java.time.LocalTime.now().getMinute() + " minutes.");
        answers.add("Monsieur, il est, " + java.time.LocalTime.now().getHour() + " heure et " + java.time.LocalTime.now().getMinute() + " minutes.");
        answers.add("Actuelement en france il est " + java.time.LocalTime.now().getHour() + " heure et " + java.time.LocalTime.now().getMinute() + " minutes.");
    }

    @Override
    public List<String> getKeywords() {
        return keywords;
    }

    @Override
    public List<String> getAnswers() {
        return answers;
    }
}
