package fr.pulsedev.jarvis.utils;

import fr.pulsedev.jarvis.Main;
import fr.pulsedev.jarvis.stt.recognition.PhraseRecognition;

import java.io.Console;
import java.util.Scanner;

public class ConsoleScanner implements Runnable {
    @Override
    public void run() {
        while (true){
            Scanner scanner = new Scanner(System.in);
            String in = scanner.nextLine();
            PhraseRecognition recognition = new PhraseRecognition(in);
            Thread recognitionThread = new Thread(recognition);
            recognition.setThread(recognitionThread);
            recognitionThread.start();
        }
    }
}
