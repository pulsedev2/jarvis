package fr.pulsedev.jarvis.decoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This file is a part of jarvis, located on fr.pulsedev.jarvis.modules
 * Copyright (c) Niout - All rights reserved
 *
 * @author Niout
 * Created the 12/08/2020 at 23:37.
 */
public class ModuleTemp {

    private String name;
    private List<String> keywords = new ArrayList<>();
    private List<String> imports = new ArrayList<>();
    private HashMap<Integer, String> answers = new HashMap<>();
    private final ModuleReader reader;
    private HashMap<Object, Object> variables = new HashMap<>();


    public ModuleTemp(ModuleReader reader){
        this.reader = reader;
        reader.loadConfiguration();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public void setAnswers(HashMap<Integer, String> answers) {
        this.answers = answers;
    }

    public void addAnswer(Integer answerName, String answerValue){this.answers.put(answerName, answerValue);}

    public void setVariables(HashMap<Object, Object> variables){this.variables = variables;}

    public void addVariables(Object variableName, Object variableValue){this.variables.put(variableName, variableValue);}

    public ModuleReader getReader(){
        return reader;
    }

    public String getName() {
        return name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getImports() {
        return imports;
    }

    public HashMap<Integer, String> getAnswers() {
        return answers;
    }

    public HashMap<Object, Object> getVariables(){return variables;}
}
