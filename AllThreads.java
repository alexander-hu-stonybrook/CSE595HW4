import java.util.*;

public class AllThreads {

    public static void main(String... args) {
        WordCounter reader = new WordCounter();
        int threadnum = reader.getThreads();
        int filenum = reader.getAllFiles().size();

        List<List<Integer>> filedist = new ArrayList<List<Integer>>();
        int ctr = 0;

        while (ctr < threadnum) {
            filedist.add(new ArrayList<Integer>());
            ctr++;
        }

        ctr = 0;

        while (ctr < filenum) {
            int placement = ctr % threadnum;
            filedist.get(placement).add(ctr);
            ctr++;
        }

        //System.out.println("Before calling threads");
        //System.out.println(filedist);

        List<Thread> multithread = new ArrayList<Thread>();
        for (List<Integer> fileinds : filedist) {
            Thread at = new Thread(new WordThread(reader,fileinds));
            at.start();
            multithread.add(at);
        }

        //System.out.println("After calling threads");


        for (Thread t : multithread) {
            //System.out.println("Inside join threads");
            try {
                //System.out.println("In Join Loop");
                t.join();
            }
            catch (InterruptedException e){
                System.out.println("An error occurred with AllThreads - a thread got interrupted during join");
                e.printStackTrace();
            }
        }

        //System.out.println("After joining threads");

        reader.alphaFinal();
        reader.printWords();

    }

}