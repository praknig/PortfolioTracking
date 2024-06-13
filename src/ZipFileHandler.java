import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.util.ArrayList;

public class ZipFileHandler implements Runnable {

    private String baseFolderPath;
    private long sleepTime;
    private String zippFileFolder;
    public ZipFileHandler(String baseFolderPath)
    {
        StringBuilder stringBuilder = new StringBuilder();
        this.baseFolderPath = baseFolderPath;
        stringBuilder.append(this.baseFolderPath);
        stringBuilder.append(File.separator);
        stringBuilder.append("zipped");
        stringBuilder.append(File.separator);

        this.zippFileFolder = stringBuilder.toString();
        File fl = new File(this.zippFileFolder);
        if(!fl.exists())
        {
            fl.mkdirs();
        }
        this.sleepTime = Constants.ZIP_FILE_DELAY;
    }
    public List<String> getListToZip()
    {
        File fl = new File(this.baseFolderPath);
        System.out.println("this.baseFolderPath--->"+this.baseFolderPath);
        if(!fl.exists() || !fl.isDirectory())
        {
            return null;
        }
        Stack<File> stckDirectory = new Stack<>();
        List<String> lstFileList = new ArrayList<>();
        do{
            //System.out.println(fl.getAbsolutePath());
            for(File subFiles:fl.listFiles())
            {
                if(subFiles.isDirectory())
                {
                    stckDirectory.add(subFiles);
                    continue;
                }
                if(subFiles.getName().endsWith("_tozip.txt"))
                {
                    lstFileList.add(subFiles.getAbsolutePath() );
                }
            }
            if(stckDirectory.isEmpty())
            {
                fl = null;
            }
            else
            {
                fl = stckDirectory.pop();

            }
        }while(fl!=null);
        return lstFileList;
    }
    public void run()
    {
        System.out.println("Zip File Thread started");
        while(true)
        {
            try{
                for(String fileAbsPath:getListToZip())
                {
                    System.out.println("zipping---->"+fileAbsPath);
                    zipFile(fileAbsPath);
                }
                Thread.sleep(this.sleepTime);
//                /break;
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
