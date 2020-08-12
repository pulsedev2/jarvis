package fr.pulsedev.jarvis;

import net.contentobjects.jnotify.JNotifyListener;

public class FileListener implements JNotifyListener {
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        System.out.println("File created: " + name);
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {

    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {

    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {

    }
}
