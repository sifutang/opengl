precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D yTexture;
uniform sampler2D uTexture;
uniform sampler2D vTexture;
uniform sampler2D uvTexture;

uniform int type;

const mat3 yuv2rgb = mat3(
1, 0, 1.2802,
1, -0.214821, -0.380589,
1, 2.127982, 0
);

void main() {
    float y, u, v;
    // We had put the Y values of each pixel to the R, G, B components by GL_LUMINANCE,
    //that's why we're pulling it from the R component, we could also use G or B
    y = texture2D(yTexture, vTexCoord).r;

    if (type == 1) {
        //We had put the U and V values of each pixel to the A and R,G,B components of the
        //texture respectively using GL_LUMINANCE_ALPHA. Since U,V bytes are interspread
        //in the texture
        u = texture2D(uvTexture, vTexCoord).a;
        v = texture2D(uvTexture, vTexCoord).r;
    } else {
        u = texture2D(uTexture, vTexCoord).r;
        v = texture2D(vTexture, vTexCoord).r;
    }

    y = 1.1643 * (y - 16.0 / 255.0);
    u = u - 128.0 / 255.0;
    v = v - 128.0 / 255.0;

    // yuv to rgb
    vec3 rgb = vec3(y, u, v) * yuv2rgb;

    gl_FragColor = vec4(rgb, 1.0);
}
