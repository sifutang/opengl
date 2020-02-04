
attribute vec4 aPosition;
attribute vec3 aColor;

uniform mat4 uProjectMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uModelMatrix;

varying vec3 vColor;

void main() {
    gl_Position = uProjectMatrix * uViewMatrix * uModelMatrix * aPosition;
    vColor = aColor;
}
