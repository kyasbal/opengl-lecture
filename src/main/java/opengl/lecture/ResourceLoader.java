package opengl.lecture;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.jogamp.common.net.Uri;


import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
    public String readText(String path){

        var url = Resources.getResource(path);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        }catch(IOException io){
            System.err.println("Speficified resource was not found");
            return "";
        }
    }

    public InputStream getStream(String path){
        try {
            return Resources.getResource(path).openStream();
        }catch(Exception e){
            return null;
        }
    }
}
