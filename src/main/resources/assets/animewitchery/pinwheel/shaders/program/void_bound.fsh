in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DiffuseSampler; // Original screen texture
uniform vec2 uTexelSize;
uniform float uTime;
uniform float uVoidPhase; // 0.0 to 1.0 - how much void effect to show
uniform vec3 uPlayerPos; // Player position in world space
uniform vec3 uCameraPos; // Camera position in world space

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
    float threshold = mix(0.002, 0.0001, linearDepth);
    
    // Smooth edge detection
    float edgeFactor = smoothstep(threshold, threshold * 3.0, edgeStrength);
    
    // Add animated noise overlay
    float noiseValue = noise(texCoord * 50.0 + uTime * 0.5);
    float animatedEdge = edgeFactor * (0.9 + 0.3 * noiseValue);
    
    // Boost nearby edges - closer objects get stronger void effects
    float nearbyBoost = 1.0 - linearDepth; // 1.0 for close, 0.0 for far
    animatedEdge *= (1.0 + nearbyBoost * 0.5); // 50% boost for nearby objects

    // Create pulsing void effect - slowed down for more subtle breathing
    float pulse = 0.5 + 0.5 * sin(uTime * 0.1);
    
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
    
    // Void particles - floating energy orbs that move around
    float particles = 0.0;
    
    // Screen-space particles (floating on the screen)
    for (int i = 0; i < 2; i++) {
        // Create different particle paths with unique timing - MUCH SLOWER
        vec2 particlePos = vec2(
            sin(uTime * 0.05 + float(i) * 2.39) * 0.4 + 0.5,
            cos(uTime * 0.03 + float(i) * 1.73) * 0.3 + 0.5
        );
        
        // Add some vertical drift for more natural movement - SLOWER
        particlePos.y += sin(uTime * 0.02 + float(i) * 3.14) * 0.1;
        
        // Calculate distance to particle
        float dist = length(texCoord - particlePos);
        
        // Create soft, glowing particles - MUCH BIGGER AND BRIGHTER
        float particle = smoothstep(0.02, 0.0, dist);
        
        // Add subtle pulsing to each particle
        float particlePulse = sin(uTime * 0.8 + float(i) * 2.0) * 0.3 + 0.7;
        particle *= particlePulse;
        
        // Accumulate screen particles
        particles += particle * 1.2;
    }
    
    // World-space particles (appear to be floating in 3D space)
    for (int i = 0; i < 3; i++) {
        // Create world-space particle positions
        vec2 worldParticlePos = vec2(
            sin(uTime * 0.03 + float(i) * 1.57) * 0.6 + 0.5,
            cos(uTime * 0.04 + float(i) * 2.86) * 0.4 + 0.5
        );
        
        // Add gentle world movement
        worldParticlePos.y += sin(uTime * 0.015 + float(i) * 3.14) * 0.15;
        
        // Calculate distance to world particle
        float worldDist = length(texCoord - worldParticlePos);
        
        // Create world particles - smaller and more focused
        float worldParticle = smoothstep(0.015, 0.0, worldDist);
        
        // Add pulsing to world particles
        float worldPulse = sin(uTime * 0.6 + float(i) * 1.5) * 0.4 + 0.6;
        worldParticle *= worldPulse;
        
        // Make world particles appear at specific depths - they fade based on world depth
        float targetDepth = 0.3 + float(i) * 0.2; // Different depths for each particle
        float depthFade = 1.0 - smoothstep(targetDepth - 0.1, targetDepth + 0.1, linearDepth);
        
        // World particles are more intense but depth-dependent
        particles += worldParticle * depthFade * 1.8;
    }
    
    // Add particles to the void effect - only visible during void phase
    finalColor += voidAccent * particles * uVoidPhase;
    

    
    // Sample the original screen
    vec3 originalScreen = texture(DiffuseSampler, texCoord).rgb;
    
    // Gradually fade out the normal Minecraft world as void phase increases
    // When voidPhase = 0: show full normal world
    // When voidPhase = 1: show only black background
    vec3 fadedWorld = mix(originalScreen, vec3(0.0, 0.0, 0.0), uVoidPhase);
    
    // Only show the void edges when void phase is active
    // This makes edges fade away along with the void phase
    vec3 finalOutput = mix(fadedWorld, finalColor, animatedEdge * uVoidPhase);
    
    // Add particles to the entire screen - not just edges
    finalOutput += voidAccent * particles * uVoidPhase;
    
    // Alpha controls the overall visibility - higher void phase = more visible effect
    float alpha = uVoidPhase;
    
    fragColor = vec4(finalOutput, alpha);
}