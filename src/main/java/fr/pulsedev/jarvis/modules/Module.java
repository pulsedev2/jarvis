package fr.pulsedev.jarvis.modules;


import fr.pulsedev.jarvis.items.ItemsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis.modules
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 18:03.
 */
public abstract class Module {

    String name;
    List<String> imports = new ArrayList<>();
    List<String> keywords = new ArrayList<>();
    List<String> answers = new ArrayList<>();


    public Module(HashMap<ItemsType, String> arguments) {

    }

    public void setName(String name){this.name = name;}
    public void setImports(List<String> imports){this.imports = imports;}
    public void setKeywords(List<String> keywords){this.keywords = keywords;}
    public void setAnswers(List<String> answers){this.answers = answers;}
    public String getName(){return name;}
    public List<String> getImports(){return imports; }
    public List<String> getKeywords() { return keywords; }
    public List<String> getAnswers() {
        return answers;
    }

}
