precision mediump float;


uniform sampler2D uTexture2D;
varying vec2 vTextureCoord;

void main() {
    gl_FragColor = texture2D(uTexture2D, vTextureCoord);
}
