#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MainDepthSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // Just output the depth buffer directly to see if it's working
    float depth = texture(MainDepthSampler, texCoord).r;
    
    // Debug: Show depth as grayscale (white = far, black = near)
    fragColor = vec4(depth, depth, depth, 1.0);
}
