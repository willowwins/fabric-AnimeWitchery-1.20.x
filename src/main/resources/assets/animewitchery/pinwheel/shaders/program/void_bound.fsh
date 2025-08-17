in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DiffuseSampler; // Original screen texture
uniform vec2 uTexelSize;
uniform float uTime;
uniform float uVoidPhase; // 0.0 to 1.0 - how much void effect to show

float linearize(float depth) {
    float n = 0.05;
    float f = 200.0;
    return (2.0 * n) / (f + n - depth * (f - n));
}

// Noise function for animated effects
float noise(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    // Sample depth at current pixel
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    float linearDepth = linearize(depth);
    
    // Use built-in derivatives for edge detection - much more efficient!
    float ddx = dFdx(linearDepth);
    float ddy = dFdy(linearDepth);
    
    // Compute edge strength from derivatives
    float edgeStrength = sqrt(ddx * ddx + ddy * ddy);
    
    // Adaptive threshold based on depth
    float threshold = mix(0.005, 0.0001, linearDepth);
    
    // Smooth edge detection
    float edgeFactor = smoothstep(threshold, threshold * 2.0, edgeStrength);
    
    // Add animated noise overlay
    float noiseValue = noise(texCoord * 50.0 + uTime * 0.5);
    float animatedEdge = edgeFactor * (0.8 + 0.2 * noiseValue);
    
    // Create pulsing void effect
    float pulse = 0.5 + 0.5 * sin(uTime * 2.0);
    
    // Enhanced void colors with multiple layers
    vec3 voidColor = vec3(0.6, 0.0, 0.8); // Base purple
    vec3 voidAccent = vec3(0.9, 0.2, 1.0); // Bright accent
    vec3 voidDark = vec3(0.2, 0.0, 0.4); // Dark shadow
    
    // Mix colors based on edge strength and animation
    vec3 finalColor = mix(voidDark, voidColor, animatedEdge);
    finalColor = mix(finalColor, voidAccent, animatedEdge * pulse * 0.5);
    
    // Add subtle glow effect
    float glow = smoothstep(0.0, 0.1, animatedEdge);
    finalColor += voidAccent * glow * 0.3;
    
    // Sample the original screen
    vec3 originalScreen = texture(DiffuseSampler, texCoord).rgb;
    
    // Gradually fade out the normal Minecraft world as void phase increases
    // When voidPhase = 0: show full normal world
    // When voidPhase = 1: show only black background
    vec3 fadedWorld = mix(originalScreen, vec3(0.0, 0.0, 0.0), uVoidPhase);
    
    // Only show the void edges when void phase is active
    // This makes edges fade away along with the void phase
    vec3 finalOutput = mix(fadedWorld, finalColor, animatedEdge * uVoidPhase);
    
    // Alpha controls the overall visibility - higher void phase = more visible effect
    float alpha = uVoidPhase;
    
    fragColor = vec4(finalOutput, alpha);
}