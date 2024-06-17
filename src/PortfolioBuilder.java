import java.io.*;

import java.nio.file.Files;

import java.nio.file.StandardOpenOption;
import java.util.Random;

public class PortfolioBuilder implements Runnable {

    private StringBuilder foliosData;
    private String fileName;
    private short numWrites;
    private short writeLimit;
    private int start;
    private int end;
    private short precision;
    private short companies;
    private String workingFolder;
    private short folioSizeLimit;
    private short folioSize;
    private String configFile;
    private short relevantLength;

    public PortfolioBuilder(int start, int end) {
        this.start = start;
        this.end = end;
        this.folioSize = 0;
        this.numWrites = 0;
        this.fileName = "portfolios.txt";
        this.configFile = "portfolio_config.txt";
        this.companies = Constants.COMPANIES;
        this.precision = Constants.PERCISION;
        this.writeLimit = Constants.WRITE_FILE_LIMIT;
        this.folioSizeLimit = Constants.WRITE_LINE_LIMIT;
        this.foliosData = new StringBuilder();
        this.relevantLength = 0;
    }

    public void run() {
        String thName = Thread.currentThread().getName();
        this.workingFolder = "portfolio_" + thName;
        File fl = new File(this.workingFolder);
        if (!fl.exists()) {
            fl.mkdir();
        }

        this.fileName = this.workingFolder + File.separator + this.fileName;
        this.configFile = this.workingFolder + File.separator + this.configFile;
        fl = new File(this.configFile);
        try{
            if(!fl.exists())
            {
                fl.createNewFile();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        int nums[] = new int[this.companies];
        Random rand = new Random();
        for(int i = 0 ;i<this.companies;i++)
        {
            nums[i] = Math.abs(rand.nextInt())%10;
        }
        this.printArray(nums);
        System.out.println(buildString(nums));
        //runTheShow();
    }

    private void makeCarryAdjustments(int[] companyArray, int limit) {
        int size = companyArray.length-1;
        for (int i = 0; i < size; i++) {
            if (companyArray[i] > limit) {
                companyArray[i] = 0;
                companyArray[i + 1] += 1;
            } else {
                break;
            }
        }
    }

    private void printArray(int[] arrayToPrint) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nth=").append(Thread.currentThread().getName()).append(" Array Length = ").append(arrayToPrint.length).append("\n");

        for (int i = 0; i < arrayToPrint.length; i++) {
            stringBuilder.append(arrayToPrint[i]).append("|");

        }
        System.out.println(stringBuilder);

    }
    private String returnLastLine(String data)
    {
        int lastIndex = data.length()-1;
        int secondLastIndex = data.substring(0,lastIndex-1).lastIndexOf(System.lineSeparator());
        return data.substring(secondLastIndex,lastIndex);

    }
    private void updateConfigFile(String data)
    {
        try{
            data = data.trim();
            String configFile = this.configFile;
            File file = new File(configFile);
            FileWriter fw = new FileWriter(file.getAbsoluteFile(),false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
            fw.close();

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private void renameFile(File fl)
    {

        StringBuilder newFileNameBuilder = new StringBuilder();
        newFileNameBuilder.append(fl.getParentFile().getAbsolutePath());
        newFileNameBuilder.append(File.separator);
        newFileNameBuilder.append("portfolio__");
        newFileNameBuilder.append(Thread.currentThread().getName());
        newFileNameBuilder.append("___");
        newFileNameBuilder.append(System.currentTimeMillis());
        newFileNameBuilder.append("_tozip.txt");
        File newFile = new File(newFileNameBuilder.toString());
        fl.renameTo(newFile);

    }
    private StringBuilder buildString(int[] numArray)
    {
        StringBuilder strBuilder = new StringBuilder();
        short startIndex = 1;
        int lastIndex = numArray.length-1;
        short countSameElement = 0;
        int element=0;
        strBuilder.append(numArray[0]).append("|");
        for(short index = startIndex;index<lastIndex;index++)
        {
            if( (index+1)<lastIndex && numArray[index] == numArray[index+1] )
            {
                countSameElement++;
                element = numArray[index];

            }
            else{
                if(countSameElement>0)
                {
                    strBuilder.append("(").append( element).append( ",").append( countSameElement).append(")|");
                    countSameElement = 0;
                }
                strBuilder.append(numArray[index]).append("|");
            }
        }
        strBuilder.append(numArray[lastIndex]).append(System.lineSeparator());
        return strBuilder;


    }
    private void writeToFile(int[] numArray) {

        /*short countZeros=0;
        String s = "";
        for (int i = 0; i < this.companies; i++) {

            if(numArray[i] == 0)
            {
                countZeros++;
            }
            else {

                if(countZeros>0)
                {

                    this.foliosData.append("["+countZeros+"]").append("|");
                    s += "["+countZeros+"]"+"|";
                    countZeros= 0;
                }
                s += numArray[i] + "|";
                this.foliosData.append(numArray[i]).append("|");
            }


        }
        if(countZeros>0)
        {
            this.foliosData.append("{"+countZeros+"}").append("|");
            s += "{"+countZeros+"}|";

        }
        this.foliosData.deleteCharAt(this.foliosData.length()-1);
        //System.out.println("s---->"+s);
        this.foliosData.append(System.lineSeparator()); */
        this.foliosData.append(buildString(numArray));
        this.folioSize++;
        if (this.folioSize <= this.folioSizeLimit)
            return;

        String foliosDataLocal = this.foliosData.toString();
        this.foliosData.setLength(0);
        String lastLine = this.returnLastLine(foliosDataLocal);
        System.out.println("Thread=" + Thread.currentThread().getName() + " " +lastLine);
        updateConfigFile(lastLine);
        this.folioSize = 0;
        File fl = new File(this.fileName);
        try {
            if (!fl.exists()) {
                fl.createNewFile();
            }
            Files.writeString(fl.toPath(), foliosDataLocal, StandardOpenOption.APPEND);
            this.numWrites++;
            if (this.numWrites > this.writeLimit) {
                renameFile(fl);
                this.numWrites = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkForTotal(int[] numArray, int limit) {
        long sum = numArray[this.companies-1];
        for (int i = 0; i < this.companies && i<=this.relevantLength; i++) {
            sum += numArray[i];
            if (sum > limit)
                break;
        }
        if (sum == limit) {

            writeToFile(numArray);
        }
    }
    private void calculateRelevantLength(int companies[])
    {
        int lastIndex = this.companies-1;
        short relIndex = 0;
        for(short index = 0;index<lastIndex;index++)
        {
            if(companies[index]!= 0)
            {
                relIndex = index;
            }
        }
        this.relevantLength = relIndex;
    }
    private void runTheShow() {

        System.out.println("Thread Started " + Thread.currentThread().getName() + " start " + this.start + " end " + this.end);
        short precision = this.precision;
        int companies = this.companies;
        int end = this.end;
        int lastIndex = companies - 1;
        int limit = (int) Math.pow(10, precision + 2);
        int[] companiesArray = loadInitialArray();
        printArray(companiesArray);
        calculateRelevantLength(companiesArray);
        while (companiesArray[lastIndex] < end) {
            companiesArray[0]++;
            if (companiesArray[0] > limit) {
                makeCarryAdjustments(companiesArray, limit);
                calculateRelevantLength(companiesArray);
            }
            checkForTotal(companiesArray, limit);
        }
        System.out.println("Thread Ended " + Thread.currentThread().getName() + " start " + this.start + " end " + this.end);

    }

    private int[] loadInitialArray() {

        int start = this.start;
        int companies = this.companies;
        int companiesArray[] = new int[companies];
        companiesArray[companies-1] = start;
        File portfolioFile = new File(this.configFile);
        if (portfolioFile.exists()) {
            try {
                FileReader fr = new FileReader(portfolioFile.getAbsolutePath());
                BufferedReader bufferedReader = new BufferedReader(fr);
                String line = bufferedReader.readLine();
                bufferedReader.close();
                fr.close();
                if (line == null || line.length() == 0) {
                    return companiesArray;
                } else {
                    int index = 0;
                    String percentages[] = line.split("\\|");
                    for (String percentage : percentages) {
                        percentage = percentage.trim();
                        if(percentage == null || percentage.length() == 0)
                        {
                            companiesArray[index] = 0;
                        }
                        else {
                            companiesArray[index] = Integer.valueOf(percentage);
                        }
                        index++;
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else {
            for(int index = 0;index <companies;index++)
            {

                companiesArray[index] =0;
            }
            companiesArray[companies-1] = start;
        }
        return companiesArray;


    }


}