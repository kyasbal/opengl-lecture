package opengl.lecture;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.glsl.ShaderCode;

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
        var elements = IntBuffer.wrap(new int[]{0,1,2});
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
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        currentTime = (System.currentTimeMillis() % 100000000L) / 1000f;
        this.gl.glClearColor(0f,1f,0f,1f);
        this.gl.glClear(GL4.GL_COLOR_BUFFER_BIT);

        gl.glUseProgram(programID);
        gl.glUniform1f(gl.glGetUniformLocation(programID,"time"),currentTime);
        gl.glVertexAttribPointer(0,3,GL4.GL_FLOAT,false,32,0);
        gl.glVertexAttribPointer(1,2,GL4.GL_FLOAT,false,32,12);
        gl.glVertexAttribPointer(2,3,GL4.GL_FLOAT,false,32,20);
        gl.glDrawArrays(GL4.GL_TRIANGLES,0,polygonCount);
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
