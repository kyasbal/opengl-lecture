#version 330

layout(location=0) in vec3 POSITION;

void main() {
    gl_Position = vec4(POSITION, 1.0);
}
