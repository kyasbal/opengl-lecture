#version 400

layout(location=0) in vec3 POSITION;
layout(location=1) in vec2 TEXCOORD;
layout(location=2) in vec3 NORMAL;

uniform mat4 view;
uniform mat4 projection;

out vec3 normal;
out vec2 texcoord;
out vec3 position;

void main() {
    gl_Position = projection*view*vec4(POSITION, 1.0);
    normal = NORMAL;
    texcoord = TEXCOORD;
    position = POSITION;
}
