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
 * Created the 28/07/2020 at 14:36.
 */
public class ThanksModule extends Module {

    List<String> keywords = Arrays.asList("merci");
    List<String> answers = Arrays.asList("Je vous en prie monsieur!", "Le plaisir était pour moi monsieur!", "C'est bien normale!");

    public ThanksModule(HashMap<ItemsType, String> arguments) {
        super(arguments);
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
