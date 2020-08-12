package fr.pulsedev.jarvis.stt.recognition;



import fr.pulsedev.jarvis.Main;
import fr.pulsedev.jarvis.items.ItemsType;
import fr.pulsedev.jarvis.modules.ErrorModule;
import fr.pulsedev.jarvis.modules.Module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 15:09.
 */
public class PhraseRecognition implements Runnable {

    Thread thread;
    Phrase phrase;
    HashMap<ItemsType, String> arguments = new HashMap<>();

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public PhraseRecognition(String phrase) {
        this.phrase = new Phrase(phrase.toLowerCase().trim());
    }

    @Override
    public void run() {
        assert thread != null;
        System.out.println("Phrase: " + phrase.getText());
        if(phrase.isACommand() || phrase.words.contains("jarvis")) {
            if(!Main.muted){
                findArguments();
                Module module = findRightModule();
                if(phrase.words.contains("chute")){
                    Main.muted = true;
                    Main.play("src\\main\\resources\\go_off.wav");
                    stopThread();
                    return;
                }
                if(module == null){
                    int answerIndex = new Random().nextInt(new ErrorModule(arguments).getAnswers().size());
                    Main.say(new ErrorModule(arguments).getAnswers().get(answerIndex), thread.getId());
                    stopThread();
                    return;
                }
                int answerIndex = new Random().nextInt(module.getAnswers().size());
                Main.say(module.getAnswers().get(answerIndex), thread.getId());
            }else {
                if(phrase.words.contains("reviens")){
                    Main.muted = false;
                    Main.play("src\\main\\resources\\go_on.wav");
                }
            }
        }
        stopThread();
    }

    public void findArguments(){
        for(String word : phrase.words){
            if(Main.cityHashMap.containsKey(word)){
                arguments.put(ItemsType.CITY, word);
            }
        }
    }

    public Module findRightModule(){
        for(Class<? extends Module> module : Main.modules){
            try {
                Constructor<? extends Module> constructor = module.getConstructor(HashMap.class);
                Module mod = constructor.newInstance(arguments);
                for(String keyword : mod.getKeywords()){
                    if(phrase.words.contains(keyword)){
                        return mod;
                    }
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void stopThread(){
        System.out.println("Stopping thread: " + thread.getId());
        if(thread.isAlive()){
            thread.interrupt();
        }
        else {
            System.out.println(thread.getId() + " is already stopped !");
        }
    }
}
