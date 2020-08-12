package fr.pulsedev.jarvis.modules;

import fr.pulsedev.jarvis.Main;
import fr.pulsedev.jarvis.items.ItemsType;
import fr.pulsedev.jarvis.utils.PropertiesValue;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis.modules
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 20:14.
 */
public class WeatherModule extends Module {

    public List<String> keywords = Arrays.asList("météo", "temps");
    public List<String> answers = new ArrayList<>();

    public WeatherModule(HashMap<ItemsType, String> arguments){
        super(arguments);
        String jsonString = "";
        if(!arguments.containsKey(ItemsType.CITY)){
            jsonString = Main.executePost("http://api.openweathermap.org/data/2.5/weather?q=Paris&appid=" + new PropertiesValue("apiKeys.properties").get("weather") +"","");
        }
        else {
            jsonString = Main.executePost("http://api.openweathermap.org/data/2.5/weather?q="+ Main.cityHashMap.get(arguments.get(ItemsType.CITY)).getName() +"&appid="+ new PropertiesValue("apiKeys.properties").get("weather") +"","");
        }
        JSONObject obj = new JSONObject(jsonString);
        String weather = obj.getJSONArray("weather").getJSONObject(0).getString("main").toLowerCase();
        System.out.println(weather);
        if(weather.equals("clouds")){
            answers.add("Il fait actuellement nuageux a " + obj.getString("name") + " avec " + ((int)(obj.getJSONObject("main").getDouble("feels_like") - 273.15)) + " degré en ressenti.");
        }
        else if(weather.equals("clear")){
            answers.add("A " + obj.getString("name") + " le temps est clair et la température ressenti est de " + ((int)(obj.getJSONObject("main").getDouble("feels_like") - 273.15)) + " degré Celsius.");
        }
        else {
            answers.add("Monsieur le temps actuelle n'a pas été ajouter au réponsse");
        }
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
