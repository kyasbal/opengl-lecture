package opengl.lecture;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;


import java.io.IOException;

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
}
