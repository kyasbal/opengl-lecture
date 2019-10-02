#version 400

layout(location = 0)out vec4 color;

uniform float time;

void main() {
    color = vec4(1.0, sin(time), 0.0, 1.0);
}
