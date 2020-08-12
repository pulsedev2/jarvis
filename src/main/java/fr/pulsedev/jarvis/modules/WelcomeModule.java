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
 * Created the 26/07/2020 at 18:06.
 */
public class WelcomeModule extends Module {

    public List<String> keywords = Arrays.asList("bonjour","salut","hello");
    public List<String> answers = Arrays.asList("Bonjour monsieur !", "Bien le bonjour, monsieur ! ", "Je suis heureux de vous voir monsieur !");

    public WelcomeModule(HashMap<ItemsType, String> arguments) {
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
