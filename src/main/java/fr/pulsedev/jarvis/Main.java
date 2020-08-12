package fr.pulsedev.jarvis;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.common.html.HtmlEscapers;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import fr.pulsedev.jarvis.items.City;
import fr.pulsedev.jarvis.modules.HourAskModule;
import fr.pulsedev.jarvis.modules.Module;
import fr.pulsedev.jarvis.modules.ThanksModule;
import fr.pulsedev.jarvis.modules.WeatherModule;
import fr.pulsedev.jarvis.modules.WelcomeModule;
import fr.pulsedev.jarvis.stt.InfiniteStreamRecognizeOptions;
import fr.pulsedev.jarvis.stt.recognition.PhraseRecognition;
import fr.pulsedev.jarvis.utils.MakeSound;
import fr.pulsedev.jarvis.utils.PropertiesValue;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;

public class Main {

    private static final int STREAMING_LIMIT = 290000; // ~5 minutes

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";

    // Creating shared object
    private static volatile BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue();
    private static TargetDataLine targetDataLine;
    private static final int BYTES_PER_BUFFER = 6400; // buffer size in bytes

    private static int restartCounter = 0;
    private static ArrayList<ByteString> audioInput = new ArrayList<>();
    private static ArrayList<ByteString> lastAudioInput = new ArrayList<>();
    private static int resultEndTimeInMS = 0;
    private static int isFinalEndTime = 0;
    private static int finalRequestEndTime = 0;
    private static boolean newStream = true;
    private static double bridgingOffset = 0;
    private static boolean lastTranscriptWasFinal = false;
    private static StreamController referenceToStreamController;
    private static ByteString tempByteString;
    public static List<Class<? extends Module>> modules = new ArrayList<>();
    public static Boolean muted = false;
    public static HashMap<String, City> cityHashMap = new HashMap<>();
    public static final String moduleFolder = "./modules";

    public static void main(String... args){
        initAllModule();
        initCityList();

        Main.play("src\\main\\resources\\go_on.wav");
        InfiniteStreamRecognizeOptions options = InfiniteStreamRecognizeOptions.fromFlags(args);
        if (options == null) {
            // Could not parse.
            System.out.println("Failed to parse options.");
            System.exit(1);
        }

        try {
            infiniteStreamingRecognize(options.langCode);
        } catch (Exception e) {
            System.out.println("Exception caught: " + e);
        }

        int mask = JNotify.FILE_CREATED + JNotify.FILE_MODIFIED;
        try {
            JNotify.addWatch(moduleFolder, mask, true, new FileListener());
        } catch (JNotifyException e) {
            e.printStackTrace();
        }
    }
    public static void initAllModule(){
        File modulesFolder = new File("src\\main\\java\\fr\\pulsedev\\jarvis\\modules");
        for(File module : Objects.requireNonNull(modulesFolder.listFiles())){
            try {
                Class<? extends Module> clazz = (Class<? extends Module>) Class.forName("fr.pulsedev.jarvis.modules." + module.getName().replace(".java", ""));
                if(!clazz.getName().equals(Module.class.getName()) && !clazz.getName().equals(Error.class.getName())){
                    modules.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(modules.toString());
    }

    public static void initCityList(){
        JSONParser jsonParser = new JSONParser();
        try(FileReader reader = new FileReader("src\\main\\resources\\city.list.json")) {
            Object obj = jsonParser.parse(reader);
            assert obj instanceof JSONArray;
            JSONArray array = (JSONArray) obj;
            for(Object object : array){
                if(object instanceof JSONObject){
                    JSONObject jsonObject = (JSONObject) object;
                    City city = new City((Long) jsonObject.get("id"), (String) jsonObject.get("name"), (String) jsonObject.get("state"), (String) jsonObject.get("country"));
                    cityHashMap.put( ((String) jsonObject.get("name")).toLowerCase(), city);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    public static String convertMillisToDate(double milliSeconds) {
        long millis = (long) milliSeconds;
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(2);
        return String.format(
                "%s:%s /",
                format.format(TimeUnit.MILLISECONDS.toMinutes(millis)),
                format.format(
                        TimeUnit.MILLISECONDS.toSeconds(millis)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
    }

    /** Performs infinite streaming speech recognition */
    public static void infiniteStreamingRecognize(String languageCode) throws Exception {

        // Microphone Input buffering
        class MicBuffer implements Runnable {

            @Override
            public void run() {
                System.out.println(YELLOW);
                System.out.println("Start speaking...Press Ctrl-C to stop");
                targetDataLine.start();
                byte[] data = new byte[BYTES_PER_BUFFER];
                while (targetDataLine.isOpen()) {
                    try {
                        int numBytesRead = targetDataLine.read(data, 0, data.length);
                        if ((numBytesRead <= 0) && (targetDataLine.isOpen())) {
                            continue;
                        }
                        sharedQueue.put(data.clone());
                    } catch (InterruptedException e) {
                        System.out.println("Microphone input buffering interrupted : " + e.getMessage());
                    }
                }
            }
        }

        // Creating microphone input buffer thread
        MicBuffer micrunnable = new MicBuffer();
        Thread micThread = new Thread(micrunnable);
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {
            ClientStream<StreamingRecognizeRequest> clientStream;
            responseObserver =
                    new ResponseObserver<StreamingRecognizeResponse>() {

                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {
                            referenceToStreamController = controller;
                        }

                        public void onResponse(StreamingRecognizeResponse response) {
                            responses.add(response);
                            StreamingRecognitionResult result = response.getResultsList().get(0);
                            Duration resultEndTime = result.getResultEndTime();
                            resultEndTimeInMS =
                                    (int)
                                            ((resultEndTime.getSeconds() * 1000) + (resultEndTime.getNanos() / 1000000));
                            double correctedTime =
                                    resultEndTimeInMS - bridgingOffset + (STREAMING_LIMIT * restartCounter);

                            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                            if (result.getIsFinal()) {
                                /*System.out.print(GREEN);
                                System.out.print("\033[2K\r");
                                System.out.printf(
                                        "%s: %s [confidence: %.2f]\n",
                                        convertMillisToDate(correctedTime),
                                        alternative.getTranscript(),
                                        alternative.getConfidence());*/
                                isFinalEndTime = resultEndTimeInMS;
                                lastTranscriptWasFinal = true;
                                PhraseRecognition recognition = new PhraseRecognition(alternative.getTranscript());
                                Thread recognitionThread = new Thread(recognition);
                                recognition.setThread(recognitionThread);
                                recognitionThread.start();
                            } else {
                                /*System.out.print(RED);
                                System.out.print("\033[2K\r");
                                System.out.printf(
                                        "%s: %s", convertMillisToDate(correctedTime), alternative.getTranscript());*/
                                lastTranscriptWasFinal = false;
                            }
                        }

                        public void onComplete() {}

                        public void onError(Throwable t) {}
                    };
            clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode(languageCode)
                            .setSampleRateHertz(22050)
                            .build();

            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder()
                            .setConfig(recognitionConfig)
                            .setInterimResults(true)
                            .build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);

            try {
                // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
                // bigEndian: false
                AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);
                DataLine.Info targetInfo =
                        new Info(
                                TargetDataLine.class,
                                audioFormat); // Set the system information to read from the microphone audio
                // stream

                if (!AudioSystem.isLineSupported(targetInfo)) {
                    System.out.println("Microphone not supported");
                    System.exit(0);
                }
                // Target data line captures the audio stream the microphone produces.
                targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                targetDataLine.open(audioFormat);
                micThread.start();

                long startTime = System.currentTimeMillis();

                while (true) {

                    long estimatedTime = System.currentTimeMillis() - startTime;

                    if (estimatedTime >= STREAMING_LIMIT) {

                        clientStream.closeSend();
                        referenceToStreamController.cancel(); // remove Observer

                        if (resultEndTimeInMS > 0) {
                            finalRequestEndTime = isFinalEndTime;
                        }
                        resultEndTimeInMS = 0;

                        lastAudioInput = null;
                        lastAudioInput = audioInput;
                        audioInput = new ArrayList<ByteString>();

                        restartCounter++;

                        if (!lastTranscriptWasFinal) {
                            System.out.print('\n');
                        }

                        newStream = true;

                        clientStream = client.streamingRecognizeCallable().splitCall(responseObserver);

                        request =
                                StreamingRecognizeRequest.newBuilder()
                                        .setStreamingConfig(streamingRecognitionConfig)
                                        .build();

                        System.out.println(YELLOW);
                        System.out.printf("%d: RESTARTING REQUEST\n", restartCounter * STREAMING_LIMIT);

                        startTime = System.currentTimeMillis();

                    } else {

                        if ((newStream) && (lastAudioInput.size() > 0)) {
                            // if this is the first audio from a new request
                            // calculate amount of unfinalized audio from last request
                            // resend the audio to the speech client before incoming audio
                            double chunkTime = STREAMING_LIMIT / lastAudioInput.size();
                            // ms length of each chunk in previous request audio arrayList
                            if (chunkTime != 0) {
                                if (bridgingOffset < 0) {
                                    // bridging Offset accounts for time of resent audio
                                    // calculated from last request
                                    bridgingOffset = 0;
                                }
                                if (bridgingOffset > finalRequestEndTime) {
                                    bridgingOffset = finalRequestEndTime;
                                }
                                int chunksFromMS =
                                        (int) Math.floor((finalRequestEndTime - bridgingOffset) / chunkTime);
                                // chunks from MS is number of chunks to resend
                                bridgingOffset =
                                        (int) Math.floor((lastAudioInput.size() - chunksFromMS) * chunkTime);
                                // set bridging offset for next request
                                for (int i = chunksFromMS; i < lastAudioInput.size(); i++) {
                                    request =
                                            StreamingRecognizeRequest.newBuilder()
                                                    .setAudioContent(lastAudioInput.get(i))
                                                    .build();
                                    clientStream.send(request);
                                }
                            }
                            newStream = false;
                        }

                        tempByteString = ByteString.copyFrom(sharedQueue.take());

                        request =
                                StreamingRecognizeRequest.newBuilder().setAudioContent(tempByteString).build();

                        audioInput.add(tempByteString);
                    }

                    clientStream.send(request);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static void play(String fileName){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public static String textToSsml(String text) {


        // Replace special characters with HTML Ampersand Character Codes
        // These codes prevent the API from confusing text with SSML tags
        // For example, '<' --> '&lt;' and '&' --> '&amp;'
        String escapedLines = HtmlEscapers.htmlEscaper().escape(text);

        // Convert plaintext to SSML
        // Tag SSML so that there is a 2 second pause between each address
        String expandedNewline = escapedLines.replaceAll("\\n", "\n<break time='2s'/>");
        String ssml = "<speak>" + expandedNewline + "</speak>";

        // Return the concatenated String of SSML
        return ssml;
    }

    /*public static Clip say(String phrase, long id){
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            System.out.println(textToSsml(phrase));
            SynthesisInput input = SynthesisInput.newBuilder().setSsml(textToSsml(phrase)).build();
            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("fr-FR")
                            .setName("fr-FR-Wavenet-D")
                            .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder()
                            .setAudioEncoding(AudioEncoding.LINEAR16)
                            .setPitch(-6.0)
                            .build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output" + id + ".wav")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output"+ id +".wav\"");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("output" + id +  ".wav").getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                return clip;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static void say(String phrase, long id){
        try {
            String ub = new PropertiesValue("apiKeys.properties").get("ibm-url");
            ub+="accept=audio/wav&";
            ub+="text=" + textToUrl(phrase);
            ub+="&voice=fr-FR_NicolasV3Voice";
            String path = new File(".\\output").getAbsolutePath().replace("\\.", "") + "\\";
            String command = "curl -X GET -u \"apikey:" + new PropertiesValue("apiKeys.properties").get("ibm") + "\" --output "+ path +"output"+ id + ".wav " + "\""+ ub +"\"";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            MakeSound makeSound = new MakeSound();
            makeSound.playSound(path + "output" + id + ".wav");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String textToUrl(String text){
        return text.replaceAll(" ", "%20").replaceAll("é", "%C3%A9").replaceAll("è", "%C3%A8").replaceAll("à", "%C3%A0");
    }
}
