package fr.pulsedev.jarvis.decoder;

import fr.pulsedev.jarvis.Main;
import fr.pulsedev.jarvis.calculator.Expression;

import java.io.*;
import java.util.*;

/**
 * This file is a part of jarvis, located on fr.pulsedev.jarvis.modules
 * Copyright (c) Niout - All rights reserved
 *
 * @author Niout
 * Created the 12/08/2020 at 22:54.
 */
public class ModulesDecoder {

    private final List<ModuleTemp> modules = new ArrayList<>();
    private final HashMap<String, Object> variables = new HashMap<>();

    public void setUpModules(){
        try{
            File folder = new File("modules");
            for(File file : Objects.requireNonNull(folder.listFiles())){
                ModuleReader reader = new ModuleReader(file);
                ModuleTemp module = new ModuleTemp(reader);
                module.setName(reader.getString("name"));
                module.setKeywords(reader.getStringList("keywords"));
                module.setImports(reader.getStringList("import"));
                for(String section : reader.getConfigurationSection("answers")){
                    for(String answer : reader.getStringList("answers." + section + ".command")){
                        if(answer.startsWith("set")){
                            try {
                                HashMap<Object, Object> variables = this.decodeVariable(answer);
                                for(Object name : variables.keySet()){
                                    module.addVariables(name, variables.get(name));
                                }
                            }catch (NumberFormatException exception){
                                Main.say("Je recontre une erreur dans le module " + module.getName() +" ! Veuillez vérifiée", 667);
                            }

                        }else if(answer.startsWith("answers add")){
                            module.addAnswer(Integer.parseInt(section), decodeAnswer(answer));
                        }
                    }
                }
                modules.add(module);
            }
        }catch (NullPointerException exception){
            Main.say("Je suis désolé je n'arrive pas à trouvé le dossier comportant les modules ! Veuillez vérifié.", 666);
        }

    }

    public HashMap<Object, Object> decodeVariable(String variableLine)
    throws NumberFormatException{
        List<String> resultName = new ArrayList<>();
        Object value = null;
        HashMap<Object, Object> result = new HashMap<>();
        List<String> args = Arrays.asList(variableLine.split(" "));
        HashMap<String, Integer> pos = this.getArgsPosition(args);
        StringBuilder values = new StringBuilder();
        for(int n = pos.get("to")+1; n < args.size(); n++){
            values.append(args.get(n)).append(" ");
        }
        for(int i=0;i<args.size(); i++){
            if(!args.get(i).equals("and") && i > pos.get("set") && i < pos.get("to")){
                resultName.add(args.get(i));
            }
            if(i > pos.get("to")){
                if(args.get(pos.get("to")+1).startsWith("[")){
                    value = Arrays.asList(values.toString().replace("[", "").replace("]", "").replace("\"", "").replace(" ", "").split(","));
                }else if (args.get(pos.get("to")+1).startsWith("(")){
                    String toDecode = values.toString().replace("(", "").replace(")", "");
                    if(toDecode.contains("+")){

                    }
                }else {
                    value = values.toString();
                    if(!value.toString().contains("\"")){
                        if(this.isOperation((String) value)){
                            Expression expression = new Expression((String)value);
                            value = expression.calculate();
                        }
                    }
                }
            }
        }

        for(Object name : resultName){
            result.put(name, value);
        }

        return result;
    }

    public boolean isOperation(String value){
        return value.contains("+")|| value.contains("-") || value.contains("*") || value.contains("/") ||value.contains("%");
    }

    public String decodeAnswer(String answerLine){
        return null;


        /*assert moduleTemp != null;
        List<String> answer = moduleTemp.getAnswers().get(answerId);
        for(String command : answer){
            if (command.startsWith("set")) {
                List<String> args = Arrays.asList(command.split(" "));
                String variableName = args.get(1);
                StringBuilder value = new StringBuilder();
                for(int i = 3; i < args.size(); i++){
                    value.append(args.get(i)).append(" ");
                }

                if(!value.toString().startsWith("(")){
                    variables.put(variableName, value.toString().replace("\"", ""));
                }else {

                }
                System.out.println(value.toString());
            }
        }*/
    }

    public HashMap<String, Integer> getArgsPosition(List<String> args){
        HashMap<String, Integer> pos = new HashMap<>();
        for(int i=0; i < args.size(); i++) {
            pos.put(args.get(i), i);
        }

        return pos;
    }

}
