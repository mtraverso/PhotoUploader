package ar.com.uploader;

import com.google.gdata.util.ServiceException;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matias on 30/4/16.
 */
public class FileFinder {

    private static final String FILE_TEXT_EXT = ".jpg";

    public static void main(String args[]) throws IOException, ServiceException, ParseException {
        BufferedReader reader = new BufferedReader(new FileReader("resources/folders.properties"));
        String line;
        List<String> files = new ArrayList<String>();
        while((line = reader.readLine()) != null){
            listFile(line, FILE_TEXT_EXT,files);

        }
        PhotoUploader uploader = new PhotoUploader(OAuth2Auth.auth());
        for(String s : files){
            System.out.println("Uploading --  "+s);
            uploader.upload(s);
        }
    }

    public static void listFile(String folder, String ext, List<String> result) {

        JpgExtFilter jpgFilter = new JpgExtFilter(ext);
        DirFilter dirFilter = new DirFilter();

        File dir = new File(folder);

        if (dir.isDirectory() == false) {
            //System.out.println("Directory does not exists : " + folder);
            return;
        }

        String[] dirs = dir.list(dirFilter);
        for(String dirToCheck: dirs){
            listFile(dir.getAbsolutePath() + "/" + dirToCheck, FILE_TEXT_EXT,result);
        }

        // list out all the file name and filter by the extension
        String[] images = dir.list(jpgFilter);


        if (images.length == 0) {
            //System.out.println("no files end with : " + ext);
            return;
        }


        for (String file : images) {
            String temp = new StringBuffer(folder).append(File.separator)
                    .append(file).toString();
            result.add(temp);
        }
    }

    public static class JpgExtFilter implements FilenameFilter {

        private String ext;

        public JpgExtFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(ext));
        }
    }

    public static class DirFilter implements FilenameFilter {

        private String ext;

        public DirFilter() {

        }

        public boolean accept(File dir, String name) {
            return new File(dir.getAbsolutePath()+"/"+name).isDirectory();
        }
    }
}

