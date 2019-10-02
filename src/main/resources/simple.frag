#version 400

layout(location = 0)out vec4 color;

uniform float time;

in vec3 normal;
in vec2 texcoord;
in vec3 position;

void main() {
    color = vec4(abs(normal), 1.0);
}
