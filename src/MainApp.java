import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;

public class MainApp extends PApplet{

    public static void main(String[] args) {PApplet.main("MainApp");}

    int scl = 100;
    float r = 250;

    float[][] globe;
    PeasyCam cam;
    PShader myShader;
    PImage colorMap;
    PImage heightMap;

    public void settings() {
//        size(800,600, P3D);
        fullScreen(P3D,1);
    }

    public void setup() {
        cam = new PeasyCam(this, 600);
        myShader = loadShader("colorfrag.glsl", "colorvert.glsl");
        heightMap = loadImage("elevation-map.jpg");
        colorMap = loadImage("topo.jpg");
    }

    public void draw() {
        heightMap.loadPixels();
        background(0);
        rotateX(radians(90));
        rotateZ(radians(-frameCount/8f));
        drawSphere(scl, r);
//        xyz(r*1.2f);
    }

    private void drawSphere(float scl, float r) {
        myShader.set("t", radians(frameCount));
        myShader.set("resolution", width, height);

        shader(myShader);
        textureMode(NORMAL);
        noStroke();

        for (int x = 0; x <= scl; x++) {
            beginShape(TRIANGLE_STRIP);
            texture(colorMap);
            for (int y = 0; y <= scl; y++) {
                fill(map(y, 0, scl, 0, 255));
                float zScl = .2f;
                float elev = zScl*getHeightAt(x,y);
                float elev1 = zScl*getHeightAt(x+1,y);
                PVector v0 = getPointOnSphere(x, y, scl, scl, r+elev);
                PVector v1 = getPointOnSphere(x+1, y, scl, scl, r+elev1);
                float u = map(x, 0, scl, 0, 1);
                float u1 = map(x+1, 0, scl, 0, 1);
                float v = map(y, 0, scl, 0, 1);
                vertex(v0.x, v0.y, v0.z, u,v);
                vertex(v1.x, v1.y, v1.z, u1,v);
            }
            endShape();
        }
    }

    private float getHeightAt(int x, int y) {
        int targetX = round(map(x,0, scl, 0, heightMap.width));
        int targetY = round(map(y,0, scl, 0, heightMap.height));
        if(targetX < 0  ||targetX >= heightMap.width || targetY < 0 || targetY >= heightMap.height) {
            return 0;
        }
        return brightness(heightMap.pixels[targetX+targetY*heightMap.width]);
    }

    PVector getPointOnSphere(float x, float y, float xMax, float yMax, float r){
        float s = map(x, 0, xMax, 0, TWO_PI);
        float t = map(y, 0, yMax, 0, PI);
        float resultX = r * cos(s) * sin(t);
        float resultY = r * sin(s) * sin(t);
        float resultZ = r * cos(t);
        return new PVector(resultX, resultY, resultZ);
    }

    private void xyz(float size) {
        strokeWeight(3);
        textSize(60);
        stroke(255,0,0);
        fill(255,0,0);
        line(0,0,0,size, 0,0);
        text("x", size,0, 0);
        stroke(0,255,0);
        fill(0,255,0);
        line(0,0,0,0, size,0);
        text("y", 0, size,0);
        stroke(0,0,255);
        fill(0,0,255);
        line(0,0,0,0, 0,size);
        text("z", 0, 0,size);
    }
}
