package opengl.lecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjParser {

    private Map<String, List<String>> lines = new HashMap<>();


    public void load(String objFile){
        for(var line: objFile.split(System.lineSeparator())){
            String header = "";
            if(line.charAt(0) == '#'){
                continue;
            }
            for(var i = 0; i < line.length();i++){
                if(line.charAt(i) == ' '){
                    if(lines.get(header) == null){
                        lines.put(header,new ArrayList<>());
                    }
                    lines.get(header).add(line.substring(i+1));
                    break;
                }else {
                    header += line.charAt(i);
                }
            }
        }
    }

    public float[] getPositions(){
        var vertexLines = lines.get("v");
        var result = new float[vertexLines.size() * 3];
        for(int i = 0; i < vertexLines.size();i++){
            var line = vertexLines.get(i);
            var components = line.split(" ");
            result[3*i+0] = Float.parseFloat(components[0]);
            result[3*i+1] = Float.parseFloat(components[1]);
            result[3*i+2] = Float.parseFloat(components[2]);
        }
        return result;
    }

    public float[] getNormals(){
        var vertexLines = lines.get("vn");
        var result = new float[vertexLines.size() * 3];
        for(int i = 0; i < vertexLines.size();i++){
            var line = vertexLines.get(i);
            var components = line.split(" ");
            result[3*i+0] = Float.parseFloat(components[0]);
            result[3*i+1] = Float.parseFloat(components[1]);
            result[3*i+2] = Float.parseFloat(components[2]);
        }
        return result;
    }

    public float[] getTexCoords(){
        var vertexLines = lines.get("vt");
        var result = new float[vertexLines.size() * 2];
        for(int i = 0; i < vertexLines.size();i++){
            var line = vertexLines.get(i);
            var components = line.split(" ");
            result[2*i+0] = Float.parseFloat(components[0]);
            result[2*i+1] = Float.parseFloat(components[1]);
        }
        return result;
    }

    public float[] getPolygonArray(){
        var faceLines = lines.get("f");
        var positions = getPositions();
        var normals = getNormals();
        var texcoords = getTexCoords();
        var result = new float[faceLines.size() * 8 * 3];
        for(int i = 0; i < faceLines.size();i++){
            var line = faceLines.get(i);
            var faces = line.split(" ");
            for(int j = 0; j < 3; j++){
                var index = i * 8 * 3 + j * 8;
                var face=faces[j];
                var components = face.split("/");
                var positionIndex = Integer.parseInt(components[0]) - 1;
                var texIndex = Integer.parseInt(components[1]) - 1;
                var normalIndex = Integer.parseInt(components[2]) - 1;
                result[index + 0] = positions[3*positionIndex + 0];
                result[index + 1] = positions[3*positionIndex + 1];
                result[index + 2] = positions[3*positionIndex + 2];
                result[index + 3] = texcoords[2*texIndex + 0];
                result[index + 4] = texcoords[2*texIndex + 1];
                result[index + 5] = normals[3*normalIndex + 0];
                result[index + 6] = normals[3*normalIndex + 1];
                result[index + 7] = normals[3*normalIndex + 2];
            }
        }
        return result;
    }
}
