import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PortfolioBuilder implements Runnable {

    private StringBuilder foliosData;
    private String fileName;
    private int numWrites;
    private int writeLimit;
    private int start;
    private int end;
    private int precision;
    private int companies;
    private String workingFolder;
    private int folioSizeLimit;
    private int folioSize;

    public PortfolioBuilder(int start, int end) {
        this.start = start;
        this.end = end;
        this.folioSize = 0;
        this.numWrites = 0;
        this.fileName = "portfolios.txt";
        this.companies = Constants.COMPANIES;
        this.precision =  Constants.PERCISION;
        this.writeLimit = Constants.WRITE_FILE_LIMIT;
        this.folioSizeLimit = Constants.WRITE_LINE_LIMIT;
        this.foliosData = new StringBuilder();
    }

    public void run() {
        String thName = Thread.currentThread().getName();
        this.workingFolder = "portfolio_" + thName;
        File fl = new File(this.workingFolder);
        if (!fl.exists()) {
            fl.mkdir();
        }
        this.fileName = this.workingFolder + File.separator + this.fileName;
        runTheShow();
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
    private void printLastLine(String data)
    {
        int lastIndex = data.length()-1;
        int secondLastIndex = data.substring(0,lastIndex-1).lastIndexOf(System.lineSeparator());
        System.out.println("Thread=" + Thread.currentThread().getName() + "  " +data.substring(secondLastIndex,lastIndex));
    }
    private void renameFile(File fl)
    {

        StringBuilder newFileNameBuilder = new StringBuilder();
        newFileNameBuilder.append(fl.getParentFile().getAbsolutePath());
        newFileNameBuilder.append(File.pathSeparator);
        newFileNameBuilder.append("portfolio__");
        newFileNameBuilder.append(Thread.currentThread().getName());
        newFileNameBuilder.append("___");
        newFileNameBuilder.append(System.currentTimeMillis());
        newFileNameBuilder.append("_tozip.txt");
        File newFile = new File(newFileNameBuilder.toString());
        fl.renameTo(newFile);

    }
    private void writeToFile(int[] numArray) {

        for (int i = 0; i < this.companies; i++) {

            this.foliosData.append(numArray[i]).append("|");

        }
        this.foliosData.deleteCharAt(this.foliosData.length()-1);
        this.foliosData.append(System.lineSeparator());
        this.folioSize++;
        if (this.folioSize <= this.folioSizeLimit)
            return;

        String foliosDataLocal = this.foliosData.toString();
        this.foliosData.setLength(0);
        printLastLine(foliosDataLocal);
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
        long sum = 0;
        for (int i = 0; i < this.companies; i++) {
            sum += numArray[i];
            if (sum > limit)
                break;
        }
        if (sum == limit) {
            writeToFile(numArray);
        }
    }

    private void runTheShow() {

        System.out.println("Thread Started " + Thread.currentThread().getName() + " start " + this.start + " end " + this.end);
        int precision = this.precision;
        int companies = this.companies;
        int end = this.end;
        int lastIndex = companies - 1;
        int limit = (int) Math.pow(10, precision + 2);
        int[] companiesArray = loadInitialArray();
        while (companiesArray[lastIndex] < end) {
            companiesArray[0]++;
            if (companiesArray[0] > limit) {
                makeCarryAdjustments(companiesArray, limit);
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
        File portfolioFile = new File(this.fileName);
        if (portfolioFile.exists()) {
            try {
                FileReader fr = new FileReader(portfolioFile.getAbsolutePath());
                BufferedReader bufferedReader = new BufferedReader(fr);
                String line, prevLine = null;
                while (true) {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    prevLine = line;

                }
                if (prevLine == null || prevLine.length() == 0) {
                    return companiesArray;
                } else {
                    int index = 0;
                    String percentages[] = prevLine.split("\\|");
                    for (String percentage : percentages) {
                        percentage = percentage.trim();
                        if(percentage == null  || percentage.length() == 0)
                            continue;
                        companiesArray[index] = Integer.valueOf(percentage);
                        index++;
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return companiesArray;


    }


}