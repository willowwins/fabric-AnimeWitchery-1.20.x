in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D DiffuseDepthSampler;
uniform vec2 uTexelSize; // 1.0 / framebuffer size

float linearize(float depth)
{
	float n = 0.05;
	float f = 200.0;
	return (2.0 * n) / (f + n - depth * (f - n));
}

void main() {
	// Read center depth
	float center = linearize(texture(DiffuseDepthSampler, texCoord).r);

	// Neighbor offsets (4-neighborhood)
	float up    = linearize(texture(DiffuseDepthSampler, texCoord + vec2(0.0,  uTexelSize.y)).r);
	float down  = linearize(texture(DiffuseDepthSampler, texCoord + vec2(0.0, -uTexelSize.y)).r);
	float left  = linearize(texture(DiffuseDepthSampler, texCoord + vec2(-uTexelSize.x, 0.0)).r);
	float right = linearize(texture(DiffuseDepthSampler, texCoord + vec2( uTexelSize.x, 0.0)).r);

	// Simple edge strength = max neighbor difference
	float edgeStrength = max(
		max(abs(center - up), abs(center - down)),
		max(abs(center - left), abs(center - right))
	);

	// Threshold to avoid noise
	float threshold = 0.01; // tweak for sensitivity
	float edge = step(threshold, edgeStrength);

	// Purple color for edges
	vec3 edgeColor = vec3(0.6, 0.0, 0.8); // RGB purple

	fragColor = vec4(edgeColor * edge, 1.0);
}

