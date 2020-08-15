package fr.pulsedev.jarvis.decoder;

import java.io.*;
import java.util.*;

/**
 * This file is a part of jarvis, located on fr.pulsedev.jarvis.modules
 * Copyright (c) Niout - All rights reserved
 *
 * @author Niout
 * Created the 12/08/2020 at 23:51.
 */
public class ModuleReader {

    private final File fileName;
    private final List<Object> lines = new ArrayList<>();
    private final List<ModuleElement> moduleElements = new ArrayList<>();

    public ModuleReader(File fileName){
        this.fileName = fileName;
    }

    public void loadConfiguration(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String st = reader.readLine();
            TreeMap<Integer, String> indent = new TreeMap<>();
            ModuleElement moduleElement = null;
            while (st !=null){
                Object value = null;
                List<String> stringList = new ArrayList<>();
                if(!this.isList(st)){
                    moduleElement = new ModuleElement(this.getIdLine(st), "Section");
                    if(!this.isSection(st)){
                        moduleElement.setType("Value");
                        value = getValueIdLine(st);
                    }
                    indent.put(getIndent(st), moduleElement.getName());
                    TreeMap<Integer, String> indents = new TreeMap<>(indent);
                    for(Integer key : indents.keySet()){
                        if(key > getIndent(st)){
                            indent.remove(key);
                        }
                    }
                    List<String> parents = new ArrayList<>();
                    for(Map.Entry<Integer, String> entry : indent.entrySet()){
                        if(!entry.getValue().equals(moduleElement.getName())){
                            parents.add(entry.getValue());
                        }
                    }
                    moduleElement.setParents(parents);
                    moduleElement.setValue(value);
                    moduleElements.add(moduleElement);
                    st = reader.readLine();
                }else {
                    while(this.isList(st)){
                        stringList.add(getLineListContent(st));
                        st = reader.readLine();
                        if(st == null){
                            break;
                        }
                    }
                    value = stringList;
                    moduleElement.setValue(value);
                    moduleElement.setType("List");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLineListContent(String line){
        StringBuilder content = new StringBuilder();
        int tiretCount = 0;
        for(int i = 0; i < line.length(); i++){
            if (line.charAt(i) == '-') {
                tiretCount++;
            }
            if(tiretCount == 1){
                content.append(line.charAt(i));
            }
        }
        return content.substring(2);
    }

    public boolean isSection(String line){
        return line.charAt(line.length()-1) == ':';
    }

    public int getIndent(String line){
        for(int i =0; i < line.length();i++){
            if(line.charAt(i) != ' '){
                return i;
            }
        }
        return 0;
    }

    public String getIdLine(String line){
        StringBuilder id = new StringBuilder();
        for(int i = 0; i < line.length(); i++){
            if(line.charAt(i) == ':'){
                return id.toString().replace(" ", "");
            }
            id.append(line.charAt(i));
        }
        return id.toString();
    }

    public String getValueIdLine(String line){
        StringBuilder content = new StringBuilder();
        int guillementCount = 0;
        for (int i = 0; i < line.length(); i++){
            if(line.charAt(i) == ':'){
                guillementCount++;
            }
            if(guillementCount == 1){
                content.append(line.charAt(i));
            }
        }
        String result = content.substring(2);
        if(result.startsWith("\"")){
            result = result.substring(1);
        }if(result.endsWith("\"")){
            result = result.substring(0, result.length()-1);
        }

        return result;
    }

    public boolean isList(String line){
        for(int i = 0; i < line.length(); i++){
            if(line.charAt(i) == '-'){
                return true;
            }
        }
        return false;
    }

    public List<String> getConfigurationSection(String id){
        List<String> allModules = new ArrayList<>();
        for(ModuleElement module: moduleElements){
            if(!id.isEmpty()){
                if(module.getParents().size() == 1 && module.getParents().get(0).equals(id)){
                    allModules.add(module.getName());
                }
            }else {
                if(module.getParents().isEmpty()){
                    allModules.add(module.getName());
                }
            }
        }
    return allModules;
    }

    public Object get(String id) throws NullPointerException{
        for(ModuleElement module : moduleElements){
            if(module.getPath().equals(id)){
                if(module.getValue() == null && module.getType().equals("Section")){
                    return module.toString();
                }
                return module.getValue();
            }
        }
        throw new NullPointerException("Unknown id: " + id);
    }

    public String getString(String id){
        return (String) this.get(id);
    }

    public Integer getInt(String id){
        return Integer.parseInt((String) this.get(id));
    }

    public List<String> getStringList(String id){
        return (List<String>) this.get(id);
    }

    public Boolean getBool(String id){
        return Boolean.parseBoolean((String) this.get(id));
    }

}
