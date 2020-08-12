package fr.pulsedev.jarvis.modules;

import fr.pulsedev.jarvis.items.ItemsType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis.modules
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 18:36.
 */
public class ErrorModule extends Module {

    List<String> answers = Arrays.asList("Je ne comprend pas ce que vous voulez dire !", "Monsieur, excusez moi mais je ne comprends pas!", "Je n'ai pas compris ce que vous vouliez que je fasse!", "Monsieur, je ne trouve pas le module correspondant à votre requete.");

    public ErrorModule(HashMap<ItemsType, String> arguments) {
        super(arguments);
    }

    @Override
    public List<String> getKeywords() {
        return null;
    }

    @Override
    public List<String> getAnswers() {
        return answers;
    }
}
