package clarktribegames;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// <editor-fold defaultstate="collapsed" desc="credits">
/**
 * 
 * @author  Geoff Clark
 * @e-mail  info@clarktribegames.com
 * @game    Save The World
 * 
 */
// </editor-fold>

public class ChecksBalances {

    public void fileCheck(String ogPath, String newPath, boolean isitStream, 
            boolean hideItOrNo) 
            throws FileNotFoundException, IOException, Exception {
        File ogFile = new File(ogPath);
        File newFile = new File(newPath);
        boolean exists = newFile.exists();
        if (!exists) {
            if (!isitStream) {
                FileChannel source;
                FileChannel destination;
                source = new FileInputStream(ogFile).getChannel();
                destination = new FileOutputStream(newFile).getChannel();
                if (destination != null && source != null) {
                    destination.transferFrom(source, 0, source.size());
                }
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } else {
                String fullPath = ExportResource(ogPath,newPath);
            }
            if(hideItOrNo == true) {
                    hideFile(newFile);
                }
        }
    }
    
    private static String ExportResource(String source, String dest) throws 
            IOException {
           InputStream stream = null;
           OutputStream resStreamOut = null;
           try {
               stream = ChecksBalances.class.getResourceAsStream(source);
               if(stream == null) {
                   logFile("severe","Export Resource error.  Dir:  " + source);
               }
               int readBytes;
               byte[] buffer = new byte[4096];
               resStreamOut = new FileOutputStream(dest);
               while ((readBytes = stream.read(buffer)) > 0) {
                   resStreamOut.write(buffer, 0, readBytes);
               }
           } catch (IOException ex) {
               logFile("severe","ExportResource.  IOEx: " + ex.toString());
           } finally {
               stream.close();
               resStreamOut.close();
           }
           return dest;
    }
    
    public void newfileCheck(String filepath, boolean hideorNot, String text) 
            throws IOException {
        try{
            File filename = new File(filepath);
            boolean exists = filename.exists();
            if (exists == false) {
                filename.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new
                FileWriter(filename))) {
                    writer.flush();
                    writer.write(text);
                    writer.close();
                    if(hideorNot == true) {
                        hideFile(filename);
                    }
                }
            }
        } catch (IOException ex) {
            logFile("severe","NewFileCheck.  Ex: " + ex.toString());
        }
    }
    
    public void newdirCheck(String dirpath, boolean hideorNot) throws 
            IOException {
        try {
            File dirname = new File(dirpath);
            boolean exists = dirname.exists();
           if (exists == false) {
               Files.createDirectories(Paths.get(dirpath));
               if(hideorNot == true) {
                   hideFile(dirname);
               }
           }
        } catch (IOException ex) {
            logFile("severe","NewDirCheck.  Ex: " + ex.toString());
        }
    }
    
    public static void ifexistDelete(String filepath) throws IOException {
        try {
            Files.deleteIfExists(Paths.get(filepath));;
        } catch(IOException ex) {
            logFile("severe","Delete Existing.  IOEx: " + ex.toString());
        }
    }
    
    private static void hideFile(File hide) throws IOException {
        try {
          Process p = Runtime.getRuntime().exec("attrib +H " + hide.getPath());
          p.waitFor(); 
        } catch (IOException | InterruptedException e) {
            logFile("severe","HideFile Method.  Ex: " + e.toString());
        }
      }
    
    public boolean nameCheck(List<String> list,String name,int option) {
        if(option == 1) {
            return checkplayerExists(list, name);
        } else {
            return invalidcharCheck(name);
        }
    }

    private boolean invalidcharCheck(String s) {
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        if (b) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean checkplayerExists(List<String> list, String newname) {
        boolean retVal = false;
        for (String s : list) {
            if(s.contains(newname)) {
                retVal = true;
            }
        }
        return retVal;
    }    
    
//    public String getfromFile(String gffPath, boolean gffJustfirstline, boolean 
//            gffFirstcap) throws IOException {
//        String text = "";
//        System.out.println(new File(gffPath).exists());
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(gffPath));
//            text = br.readLine();
//            if(gffJustfirstline == false) {
//                StringBuilder sb = new StringBuilder();
//                text = br.readLine();
//                while(text != null) {
//                    System.out.println(sb);
//                    sb.append(text).append("\n");
//                    text = br.readLine();
//                }
//            } else {
//                br.close();
//            }
//        } catch(IOException ex) {
//            logFile("severe","1stLine Error.\nIOEx: " + ex.toString());
//        }
//        if(gffFirstcap == true) {
//            text = new Converters().capFirstLetter((text));
//        }
//        if(text == null || text.isEmpty() || text == "") {
//            text = "default";
//        }
//        return text;
//    }
    
    public String getLast(File filename) throws IOException {
        String last = new String();  
        try {
            InputStreamReader sr = new InputStreamReader(new 
                FileInputStream(filename));
            BufferedReader br = new BufferedReader(sr);
            while (br.ready()) {
                last = br.readLine();
            }
        } catch (IOException e) {
            logFile("severe","Last Line Method.  Ex: " + e.toString());
        }
        return last;
    }

//<editor-fold defaultstate="collapsed" desc="Log File Method">
    private static void logFile (String type, String loginfo) throws IOException {
        try {
            new LogWriter().writeLog(type,loginfo);
        } catch(IOException ioex) {
            logFile("severe","logFile cannot fine log file (infinite loop)!");
        }
    }
//</editor-fold>
}
