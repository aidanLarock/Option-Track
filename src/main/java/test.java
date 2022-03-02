
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Aidan Larock
 * 2020
 */
public class test {
    File Tickers = new File("tickers.txt");
    File Output = new File("output.txt");
    File mid = new File("hold.txt");
    
    public test() throws IOException{
        createFiles();
        daily();
        main main = new main();
    }
    
    private void createFiles() throws IOException{
        Tickers.createNewFile();
        Output.createNewFile();
        mid.createNewFile();
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        test t = new test();
    }

    private boolean checkDay() throws FileNotFoundException {
        boolean newDay = true;
        Date today = new Date();
            Scanner checkDate = new Scanner(Output);
            while(checkDate.hasNextLine()){
                String line = checkDate.nextLine();
                line = line.substring(0, 28);
                if(line.equals(today)){
                    System.out.println("YEEEEEEE");
                    newDay = false;
                }
            }
            checkDate.close();
        return newDay;
    }
    
    private void daily() throws FileNotFoundException, IOException{
        boolean run = checkDay();
        boolean inFile = false;
        Date today = new Date();
        if(run = true){
            Scanner tickers = new Scanner(Tickers);
            while(tickers.hasNextLine()){   
                String ticker = tickers.nextLine();
                String price = getData(ticker);
                Scanner output = new Scanner(Output);
                BufferedWriter closeMid= new BufferedWriter(new FileWriter(mid));
                closeMid.close();
                BufferedWriter writeMid= new BufferedWriter(new FileWriter(mid,true));
                while(output.hasNextLine()){
                    String line = output.nextLine();
                    if(line.equals(ticker)){
                        writeMid.write(ticker+"\n"+today+"\t"+"Price: "+price+"\n");
                        inFile = true;
                    }else{
                        writeMid.write(line+"\n");
                    } 
                }
                if(inFile == false){
                    writeMid.write(ticker+"\n"+today+"\t"+"Price: "+price+"\n");
                }
                output.close();
                writeMid.close();
                BufferedWriter closeOut = new BufferedWriter(new FileWriter(Output));
                closeOut.close();    
                Scanner middle = new Scanner(mid);
                BufferedWriter writeOut = new BufferedWriter(new FileWriter(Output,true));
                while(middle.hasNextLine()){
                    String line = middle.nextLine();
                    writeOut.write(line+"\n");
                }
                writeOut.close();
                middle.close();
            } 
        }
    }
      
    private String getStockInfo(String ticker) throws IOException{
        OkHttpClient client = new OkHttpClient();   
        int stop1 = ticker.indexOf('-');
        int stop2 = ticker.indexOf('-', stop1+1);
        String tick = ticker.substring(0,stop1);
        String date = ticker.substring(stop1+1,stop2);
        String code = ticker.substring(stop2+1,ticker.length());
       
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.clear();
        int y = Integer.parseInt(date.substring(0,4));
        int m = Integer.parseInt(date.substring(4,6));
        int d = Integer.parseInt(date.substring(6,8));
        calendar.set(y,m-1,d);
        long sse = calendar.getTimeInMillis() / 1000l;
  
        Request request = new Request.Builder()
            .url("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-options?symbol="+tick+"&date="+sse)
            .get()
            .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "ec41683bfdmsh4d21fadf6475153p1186d2jsn9735136dc573")
            .build();
        
        Response response = client.newCall(request).execute();
        String lines = response.body().string();
        int start = lines.indexOf(code);
        int end = lines.indexOf("bid", start);
        lines = lines.substring(start,end);
        int startLast = lines.indexOf("lastPrice");
        int endLast = lines.indexOf("fmt", startLast);
        lines = lines.substring(startLast+18, endLast-2);
        return lines;
    }
    
     private String getData(String url) throws IOException{
         OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
	.url("https://zenscrape-hassle-free-data-extraction-with-rotating-proxies.p.rapidapi.com/get?location=na&url="+url)
	.get()
	.addHeader("x-rapidapi-host", "zenscrape-hassle-free-data-extraction-with-rotating-proxies.p.rapidapi.com")
	.addHeader("x-rapidapi-key", "ec41683bfdmsh4d21fadf6475153p1186d2jsn9735136dc573")
	.build();

        Response response = client.newCall(request).execute();
        
        String lines = response.body().string();
        int start = lines.indexOf("PREV_CLOSE-value");
        int end = lines.indexOf("/td", start);
        lines = lines.substring(start, end);
        start = lines.indexOf("\"42\">");
        end = lines.indexOf("</span>",start);
        lines = lines.substring(start+5, end);
        return lines;
    }
}
