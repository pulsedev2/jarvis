package fr.pulsedev.jarvis.stt.recognition;



import fr.pulsedev.jarvis.Main;
import fr.pulsedev.jarvis.items.ItemsType;
import fr.pulsedev.jarvis.modules.ErrorModule;
import fr.pulsedev.jarvis.modules.Module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * This file is a part of VoiceAssistant, located on fr.renardfute.jarvis
 * Copyright (c) Renard - All rights reserved
 *
 * @author Renard
 * Created the 26/07/2020 at 15:09.
 */
public class PhraseRecognition implements Runnable {

    private Thread thread;
    private final Phrase phrase;
    private final String txt;
    private final HashMap<ItemsType, String> arguments = new HashMap<>();
    private final HashMap<Module, Integer> occurence = new HashMap<>();

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public PhraseRecognition(String phrase) {
        this.phrase = new Phrase(phrase.toLowerCase().trim());
        this.txt = phrase;
    }

    @Override
    public void run() {
        assert thread != null;
        System.out.println("Phrase: " + phrase.getText());
        Main.echo("User: " + txt);
        if(phrase.isACommand() || phrase.words.contains("jarvis")) {
            if(!Main.muted){
                findArguments();
                if(phrase.words.contains("chute")){
                    Main.muted = true;
                    Main.play("src\\main\\resources\\go_off.wav");
                    stopThread();
                    return;
                }
                Module module = findRightModule();
                if(module == null){
                    int answerIndex = new Random().nextInt(new ErrorModule(arguments).getAnswers().size());
                    Main.say(new ErrorModule(arguments).getAnswers().get(answerIndex), thread.getId());
                    stopThread();
                    return;
                }
                int answerIndex = new Random().nextInt(module.getAnswers().size());
                Main.echo("Jarvis: " + module.getAnswers().get(answerIndex));
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
                        occurence.merge(mod, 1, Integer::sum);
                    }
                }



            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return (Module)sortByValue(occurence).keySet().toArray()[0];
    }

    public HashMap<Module, Integer> sortByValue(HashMap<Module, Integer> toSort){
        List<Map.Entry<Module, Integer>> list = new LinkedList<>(toSort.entrySet());

        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));
        HashMap<Module, Integer> temp = new LinkedHashMap<>();
        for(Map.Entry<Module, Integer> aa : list){
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
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
