import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.util.ArrayList;

public class ZipFileHandler implements Runnable {

    private String baseFolderPath;
    private long sleepTime = 1000*60*10;
    private String zippFileFolder;
    public ZipFileHandler(String baseFolderPath)
    {
        StringBuilder stringBuilder = new StringBuilder();
        this.baseFolderPath = baseFolderPath;
        stringBuilder.append(this.baseFolderPath);
        stringBuilder.append(File.pathSeparator);
        stringBuilder.append("zipped");
        stringBuilder.append(File.pathSeparator);
        this.zippFileFolder = stringBuilder.toString();
    }
    public List<String> getListToZip()
    {
        File fl = new File(this.baseFolderPath);
        if(!fl.exists() || !fl.isDirectory())
        {
            return null;
        }
        Stack<File> stckDirectory = new Stack<>();
        List<String> lstFileList = new ArrayList<>();
        do{
            for(File subFiles:fl.listFiles())
            {
                if(fl.isDirectory())
                {
                    stckDirectory.add(fl);
                    continue;
                }
                if(fl.getName().endsWith("_tozip.txt"))
                {
                    lstFileList.add(fl.getAbsolutePath() );
                }
                fl = stckDirectory.pop();
            }
        }while(!stckDirectory.isEmpty());
        return lstFileList;
    }
    public void run()
    {
        System.out.println("Zip File Thread started");
        while(true)
        {
            try{
                Thread.sleep(this.sleepTime);
                for(String fileAbsPath:getListToZip())
                {
                    zipFile(fileAbsPath);
                }

            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);

            }
        }

    }
    public  void zipFile(String fileAbsPath) {
        try {
            File fileToZip = new File(fileAbsPath);
            String zipFileName = this.zippFileFolder +  "portfolio_" + System.nanoTime() + ".zip";
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);

            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[4096];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.close();
            fis.close();
            fos.close();
            fileToZip.delete();

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }
}
