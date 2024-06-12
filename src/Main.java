import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int percision = 3;
        int limit = (int)Math.pow(10,2+percision);
        int numThreads = 4;
        int window = limit/numThreads;

        List<Thread> lstThreads = new ArrayList<>();
        ZipFileHandler zp = new ZipFileHandler("./");
        for(int index= 0; index<numThreads;index++)
        {
            int start = index *window;
            int end = (index+1)*window;
            PortfolioBuilder port = new PortfolioBuilder(start,end );
            Thread th = new Thread(port);
            th.setName("th_" + index);
            lstThreads.add(th);
            //th.start();
        }
        Thread th_zip = new Thread(zp, "zipping");
        //th_zip.setDaemon(true);
        th_zip.start();
        lstThreads.add(th_zip);
        try{
            for(Thread th:lstThreads)
            {
                //th.join();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }



    }
}