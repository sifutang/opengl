
attribute vec4 aPosition;
attribute vec2 aTextureCoord;

uniform mat4 uProjectMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;

varying vec2 vTextureCoord;

void main() {
    gl_Position = uProjectMatrix * uViewMatrix * uModelMatrix * aPosition;
    vTextureCoord = aTextureCoord;
}
