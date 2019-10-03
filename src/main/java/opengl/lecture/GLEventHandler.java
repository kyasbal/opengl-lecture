package opengl.lecture;

import com.jme3.math.MathUtils;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.glsl.ShaderCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLEventHandler implements GLEventListener {

    private GL4 gl;

    private ResourceLoader resourceLoader = new ResourceLoader();

    private String vertexShaderCode;

    private String fragmentShaderCode;

    private int programID;

    private int vertexBufferId;

    private int elementBufferId;

    private float currentTime;

    private int polygonCount = 0;

    private Matrix4 projection = new Matrix4();

    private Matrix4 view = new Matrix4();

    private int textureIndex;

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();

        vertexShaderCode = resourceLoader.readText("./simple.vert");
        var vertexShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(vertexShader,1,new String[]{vertexShaderCode},new int[]{vertexShaderCode.length()},0);
        gl.glCompileShader(vertexShader);
        checkCompileStatus(vertexShader);

        fragmentShaderCode = resourceLoader.readText("./simple.frag");
        var fragmentShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragmentShader,1,new String[]{fragmentShaderCode},new int[]{fragmentShaderCode.length()},0);
        gl.glCompileShader(fragmentShader);
        checkCompileStatus(fragmentShader);

        programID = gl.glCreateProgram();
        gl.glAttachShader(programID,vertexShader);
        gl.glAttachShader(programID,fragmentShader);
        gl.glLinkProgram(programID);
        checkLinkStatus(programID);


        var objFile = resourceLoader.readText("Suzanne.obj");
        var objParser = new ObjParser();
        objParser.load(objFile);
        var objPos = objParser.getPolygonArray();
        var positions = FloatBuffer.wrap(objPos);
        var buffers = new int[]{0,0};
        gl.glGenBuffers(2,buffers,0);
        vertexBufferId = buffers[0];
        elementBufferId = buffers[1];
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER,vertexBufferId);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, 4 * objPos.length,positions,GL4.GL_STATIC_DRAW);
//        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER,elementBufferId);
//        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER,4*3,elements,GL4.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        polygonCount = objPos.length/8;

        projection.makePerspective(Mathf.PI/2f,1f,0.1f,100f);
        view.loadIdentity();
        view.translate(0f,0f,3f);
        view.invert();

        gl.glEnable(GL4.GL_DEPTH_TEST);

        var textures = new int[]{0};
        gl.glGenTextures(1,textures,0);
        textureIndex = textures[0];
        try {
            var image = ImageIO.read(resourceLoader.getStream("./debug.png"));
            int[] pixels = new int[image.getWidth()*image.getHeight()];
            image.getRGB(0,0,image.getWidth(),image.getHeight(),pixels,0,image.getWidth());
            byte[] bytePixels = new byte[image.getWidth()*image.getHeight()*4];
            for(int j=0,count=0; j<pixels.length; j++){
                bytePixels[count++] = (byte)((pixels[j] >> (8*2)) & 0xFF);
                bytePixels[count++] = (byte)((pixels[j] >> (8*1)) & 0xFF);
                bytePixels[count++] = (byte)((pixels[j] >> (8*0)) & 0xFF);
                bytePixels[count++] = (byte)128;
            }
            ByteBuffer textureBuffer = ByteBuffer.wrap(bytePixels);
            gl.glBindTexture(GL4.GL_TEXTURE_2D,textureIndex);
            gl.glTexImage2D(GL4.GL_TEXTURE_2D,0,GL4.GL_RGBA,image.getWidth(),image.getHeight(),0,GL4.GL_RGBA,GL4.GL_BYTE,textureBuffer);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D,GL4.GL_TEXTURE_MAG_FILTER,GL4.GL_NEAREST);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D,GL4.GL_TEXTURE_MIN_FILTER,GL4.GL_NEAREST);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D,GL4.GL_TEXTURE_WRAP_S,GL4.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D,GL4.GL_TEXTURE_WRAP_T,GL4.GL_CLAMP_TO_EDGE);
        }catch(Exception e){

        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        currentTime = (System.currentTimeMillis() % 100000000L) / 1000f;
        this.gl.glClearColor(0f,1f,0f,1f);
        this.gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(programID);
        gl.glUniform1f(gl.glGetUniformLocation(programID,"time"),currentTime);
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(programID,"projection"),1,false,projection.getMatrix(),0);
        gl.glUniformMatrix4fv(gl.glGetUniformLocation(programID,"view"),1,false,view.getMatrix(),0);
        gl.glVertexAttribPointer(0,3,GL4.GL_FLOAT,false,32,0);
        gl.glVertexAttribPointer(1,2,GL4.GL_FLOAT,false,32,12);
        gl.glVertexAttribPointer(2,3,GL4.GL_FLOAT,false,32,20);
        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D,textureIndex);
        gl.glUniform1f(gl.glGetUniformLocation(programID,"tex"),0);
        gl.glDrawArrays(GL4.GL_TRIANGLES,0,polygonCount);
        view.loadIdentity();
        view.translate(0f,0f,3f);
        view.invert();
        view.rotate(currentTime,0f,1f,0f);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    private void checkCompileStatus(int shaderId){
        var compileStatus = new int[]{0};
        gl.glGetShaderiv(shaderId,GL4.GL_COMPILE_STATUS,compileStatus,0);
        if(compileStatus[0] == GL4.GL_FALSE){
            var maxLogLength = 8192;
            var logArray = new byte[maxLogLength];
            var logLength = new int[]{0};
            gl.glGetShaderInfoLog(shaderId,maxLogLength,logLength,0,logArray,0);
            for(var i = 0; i < logLength[0]; i++){
                System.out.print((char)logArray[i]);
            }
        }
    }

    private void checkLinkStatus(int programId){
        var linkStatus = new int[]{0};
        gl.glGetProgramiv(programId,GL4.GL_LINK_STATUS,linkStatus,0);
        if(linkStatus[0] == GL4.GL_FALSE){
            var maxLogLength = 8192;
            var logArray = new byte[maxLogLength];
            var logLength = new int[]{0};
            gl.glGetProgramInfoLog(programId,maxLogLength,logLength,0,logArray,0);
            for(var i = 0; i < logLength[0]; i++){
                System.out.print((char)logArray[i]);
            }
        }
    }
}
