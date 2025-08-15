// void_depth.vsh — for veil:blit
layout(location = 0) in vec2 Position; // <-- vec2, not vec3
layout(location = 1) in vec2 UV0;      // <-- vec2

out vec2 vUV;

void main() {
    vUV = UV0;
    gl_Position = vec4(Position, 0.0, 1.0);
}
