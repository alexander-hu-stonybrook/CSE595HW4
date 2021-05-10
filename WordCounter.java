import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WordCounter{

    // The following are the ONLY variables we will modify for grading.
    // The rest of your code must run with no changes.
    public static final Path FOLDER_OF_TEXT_FILES  = Paths.get("./input"); // path to the folder where input text files are located
    public static final Path WORD_COUNT_TABLE_FILE = Paths.get("output.txt"); // path to the output plain-text (.txt) file
    public static final int  NUMBER_OF_THREADS     = 10;                // max. number of threads to spawn

    private List<List<Word>> wordsinfiles = new ArrayList<List<Word>>();
    private List<Word> allwords = new ArrayList<Word>();
    private int longest = 0;

    List<String> allfiles = new ArrayList<String>();

    public WordCounter() {
        File directory = new File(FOLDER_OF_TEXT_FILES.toString());
        for (File file : directory.listFiles()) {
            List<Word> onefile = new ArrayList<Word>();
            this.wordsinfiles.add(onefile);
            this.allfiles.add(file.getName());
        }
        Collections.sort(this.allfiles);
    }

    public int getThreads() {
        return this.NUMBER_OF_THREADS;
    }

    public List<List<Word>> getWordsinFiles() {
        return this.wordsinfiles;
    }

    public List<Word> getAllWords() {
        return this.allwords;
    }

    public List<String> getAllFiles() {
        return this.allfiles;
    }

    public void countWords(List<Integer> fileinds) throws Exception {
        for (int ind : fileinds) {
            File file = new File(this.FOLDER_OF_TEXT_FILES.toString() + "/" + this.allfiles.get(ind));
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                String[] filewords = st.split("[ |\\.|\\?|,|;|!|:]+");
                for (String w : filewords) {
                    String lw = w.toLowerCase();

                    //This section should not need multithreading because every thread works in its own array

                    int ctr = 0;
                    boolean found = false;
                    while (ctr < this.wordsinfiles.get(ind).size()) {
                        //System.out.println("Testing Loop 1");
                        Word subw = this.wordsinfiles.get(ind).get(ctr);
                        if (subw.getWord().equals(lw)) {
                            subw.setCount(subw.getCount() + 1);
                            found = true;
                            break;
                        }
                        ctr++;
                    }

                    if ((this.wordsinfiles.get(ind).size() == 0) || !(found)) {
                        Word nw = new Word(lw, 1);
                        this.wordsinfiles.get(ind).add(nw);
                    }

                    //This section needs to mind multithreading
                    //Let's try to remove this section and put it in a diff function
                    //System.out.println("Outside synchronized part");

                    synchronized (this) {
                        //System.out.println(lw);
                        ctr = 0;
                        found = false;
                        while (ctr < this.allwords.size()) {
                            Word subw = this.allwords.get(ctr);
                            if (subw.getWord().equals(lw)) {
                                subw.setCount(subw.getCount() + 1);
                                found = true;
                                break;
                            }
                            ctr++;
                        }

                        if ((this.allwords.size() == 0) || !(found)) {
                            Word nw = new Word(lw, 1);
                            this.allwords.add(nw);
                            if (lw.length() > this.longest) {
                                this.longest = lw.length();
                            }
                        }
                    }

                    //Close multithread sensitive section
                }
            }
        }
    }

    /*
    public void gatherFinal(List<Integer> fileinds) {
        for (int ind : fileinds) {
            int ctr = 0;
            while (ctr < this.wordsinfiles.get(ind).size()) {
                Word subw = this.wordsinfiles.get(ind).get(ctr);

                int ctr2 = 0;

                boolean found = false;
                while (ctr2 < this.allwords.size()) {
                    Word subw2 = this.allwords.get(ctr2);
                    if (subw2.getWord().equals(subw.getWord())) {
                        subw2.setCount(subw2.getCount() + subw.getCount());
                        found = true;
                        break;
                    }
                    ctr2++;
                }

                if ((this.allwords.size() == 0) || !(found)) {
                    Word nw = new Word(lw, 1);
                    this.allwords.add(nw);
                    if (lw.length() > this.longest) {
                        this.longest = lw.length();
                    }
                }

                ctr++;
            }
        }
    }
    */

    public void alphaFinal() {
        Collections.sort(this.allwords);
    }

    public void printWords() {
        try{
            FileWriter myWriter = new FileWriter(this.WORD_COUNT_TABLE_FILE.toString());

            List<Integer> fnamelen = new ArrayList<Integer>();
            String topspace = new String(new char[longest + 1]).replace("\0"," ");
            for (String fname : this.allfiles) {
                String fn = fname.substring(0, fname.lastIndexOf('.'));
                fnamelen.add(fn.length() + 4);
                topspace += fn + "    ";
            }
            topspace += "total\n";
            myWriter.write(topspace);
            //System.out.println(topspace);
            for (Word w : this.allwords) {
                String thisline = w.getWord();
                String firstspace = new String(new char[longest - w.getWord().length() + 1]).replace("\0"," ");
                thisline += firstspace;

                int it = 0;
                while(it < this.wordsinfiles.size()) {
                    boolean found = false;
                    int wctr = 0;
                    for (Word itw : wordsinfiles.get(it)) {
                        if (itw.getWord().equals(w.getWord())) {
                            wctr = itw.getCount();
                            found = true;
                            break;
                        }
                    }
                    String wctrstr = String.valueOf(wctr);
                    String deadspace = new String(new char[fnamelen.get(it) - wctrstr.length()]).replace("\0"," ");
                    thisline += wctrstr + deadspace;
                    it++;
                }
                thisline += String.valueOf(w.getCount()) + "\n";
                myWriter.write(thisline);
                //System.out.println(thisline);
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred. Probably the output file couldn't be found.");
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        String retstr = "";
        int ctr = 0;
        while (ctr < this.allfiles.size()) {
            retstr += this.allfiles.get(ctr) + ": ";
            retstr += this.wordsinfiles.get(ctr).toString();
            retstr += "\n";
            ctr++;
        }
        retstr += "Total: ";
        retstr += this.allwords.toString();

        return retstr;
    }

    class Word implements Comparable<Word>{
        private String text;
        private int count = 0;

        public Word(String newtext) {
            this.text = newtext;
        }

        public Word(String newtext, int newcount) {
            this.text = newtext;
            this.count = newcount;
        }

        public String getWord() {
            return this.text;
        }

        public int getCount() {
            return this.count;
        }

        public void setWord(String newtext) {
            this.text = newtext;
        }

        public void setCount(int newcount) {
            this.count = newcount;
        }

        @Override
        public String toString(){
            return "(" + this.text + ", " + String.valueOf(this.count) + ")";
        }

        @Override
        public int compareTo(Word w2) {
            return this.text.compareTo(w2.getWord());
        }
    }

    public static void main(String... args) throws Exception{
        // your implementation of how to run the WordCounter as a stand-alone multi-threaded program
        /*
        WordCounter testctr = new WordCounter();
        System.out.println(testctr.getAllFiles());
        ArrayList<Integer> testind = new ArrayList<Integer>(Arrays.asList(0,1,2));
        testctr.countWords(testind);
        testctr.alphaFinal();
        testctr.printWords();
        */
    }
}