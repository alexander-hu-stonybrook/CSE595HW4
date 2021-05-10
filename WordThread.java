import java.lang.Thread;
import java.util.*;
import java.io.*;

public class WordThread extends Thread{

    private WordCounter base = new WordCounter();
    private List<Integer> fileinds = new ArrayList<Integer>();

    public WordThread(WordCounter base, List<Integer> fileinds) {
        this.base = base;
        this.fileinds = fileinds;
    }

    public void run() {
        try {
            //System.out.println("Running Thread");
            this.base.countWords(this.fileinds);
        }
        catch (Exception e) {
            System.out.println("An error occurred with countWords - probably one of the files doesn't exist");
            e.printStackTrace();
        }
    }
}