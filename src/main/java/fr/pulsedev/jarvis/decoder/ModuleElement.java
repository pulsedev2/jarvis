package fr.pulsedev.jarvis.decoder;

import java.util.List;

/**
 * This file is a part of jarvis, located on fr.pulsedev.jarvis.Decoder
 * Copyright (c) Niout - All rights reserved
 *
 * @author Niout
 * Created the 13/08/2020 at 16:56.
 */
public class ModuleElement {

    private final String name;
    private List<String> parents;
    private String type;
    private Object value;


    public ModuleElement(String name, String type, List<String> parents){
        this.name = name;
        this.parents = parents;
        this.type = type;
    }

    public ModuleElement(String name, String type){
        this(name, type,null);
    }

    public ModuleElement(String name){
        this(name, "Element");
    }


    public String getPath(){
        StringBuilder path = new StringBuilder();
        for(String parent : parents){
            path.append(parent).append(".");
        }

        return path.append(getName()).toString();
    }

    public Object getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public List<String> getParents() {
        return parents;
    }
    public void setType(String type){this.type = type;}
    public void setParents(List<String> parents) {
        this.parents = parents;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public String toString(){
        return "Module"+ getType() +"[path="+getPath()+";parents="+getParents() + ";value="+ getValue() + "]";
    }
}
