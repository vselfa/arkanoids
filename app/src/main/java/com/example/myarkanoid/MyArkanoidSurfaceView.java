package com.example.myarkanoid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.myarkanoid.Objectes.Bloc;

import java.util.ArrayList;

public class MyArkanoidSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // El thread
    MyArkanoidThread game2Thread = null;

    // Els bitmaps
    private Bitmap playButtonBitmap, paletaBitmap, backgroundBitmap, blocBitmap;
    // Els rectangles que contindran els bitmaps
    Rect rectBackground,  rectPlay, rectPause;
    int ampladaFons;  // Amplada background
    // El fons
    int ampleBackground, altBackground;
    // Les posicions
    int xBoto, yBoto, xNau, yNau;
    private float mLastTouchX, mLastTouchY;

    // The position of the ball
    private int x, y, xPosition, yPosition;
    // The vector speed of the ball
    private int xDirection = 0;     private int yDirection = 0;  // Joc parat
    private int xDirectionAux = 10;  private int yDirectionAux = 20;
    private static int radius = 20;

    // The palette
    private float xPaleta, yPaleta;
    private int  ample = 200; private int alt = 30;
    private float ultimaXPaleta,  ultimaYPaleta;

    // The painters
    Paint ball = new Paint();
    Paint blocPaint = new Paint();

    //  Els blocs
    private ArrayList<Bloc> blocs = new ArrayList<>();
    // Establim el número de blocs que volem
    // => ampladaBloc = maxAmplada/numBlocs
    int numBlocs = 5; int blocSize; int numFiles = 1;
    int numBloc; // Bloc a qui pega la bola
    int linia = 10; // Per marcar la separació entre blocs
    boolean dins = false; boolean inici = true;

    // El constructor
    public MyArkanoidSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public void fillBlocs(float maxAmplada ){
        // Reset blocs per a iniciar noves partides
        blocs.clear();
        // Creem els objecte bloc. Els rectangles no calen.
        for (int i = 0; i < numFiles; i++) {
            for (int j = 0; j < numBlocs; j++) {
                // Controlem que capiguen complets en pantalla
                if (( i * blocSize + blocSize) < maxAmplada )
                    blocs.add(new Bloc(j*blocSize,i*blocSize,blocSize));
            }
        }
    }

    public void newDraw(Canvas canvas) {
        // El fons
        canvas.drawBitmap(backgroundBitmap, null, rectBackground, null);
        // Els botons
        if (inici) canvas.drawBitmap(playButtonBitmap,null, rectPlay, null);
        // La pilota
        ball.setColor(Color.RED);
        canvas.drawCircle(x, y, radius, ball);
        // La paleta
        if (!inici)
            canvas.drawBitmap(paletaBitmap, (float) xPaleta, (float) yPaleta - 20 , null);
        // Els blocs
        // Log.d ("Blocs", "Pintant rectangle " ) ;
        // Repitem tinguent en compte els blocs ocults
        for (int i = 0; i < blocs.size(); i++){
            // Sols pintem els blocs visibles
            if (blocs.get(i).getVisible()) {
                Rect r = new Rect(i*blocSize,
                        (numFiles-1) * blocSize,
                        (i + 1) * blocSize-linia, blocSize);
                canvas.drawBitmap(blocBitmap, null, r, null);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Amplada fons
        ampladaFons = getWidth();
        // Valors inicials paleta.
        xPaleta = (int) (0.25 * getWidth());
        yPaleta = (int) ((float) 0.98 * getHeight());
        // Mida i color dels blocs
        blocSize = (int) ((getWidth() / numBlocs) * numFiles);
        blocPaint.setColor(Color.GREEN);
        // Generem els bloc en funció de l'amplada de la pantalla
        // i el numBlocs: Ho fem al clicar el Play
        // fillBlocs(getWidth() );
        // Pilota apareix per sota dels blocs.
        // Per evitar que es quede atrapada entre blocs
        y = blocSize + 2*radius;
        // I en una x al atzar
        x = (int) (Math.random()*getWidth());
        // Els bitmaps
        playButtonBitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.button_play);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fons);
        paletaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nau);
        blocBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bloc);
        // Els rectangles que contenen els Bitmaps
        rectBackground = new Rect (0,0, getWidth(), getHeight());
        // Les mides del fons de la pantalla
        ampleBackground = getWidth(); altBackground = getHeight();
        // Part de baix de la pantalla
        xBoto = getWidth()/ 4;         yBoto = getHeight() - getHeight()/10;
        // Els rectangles on dibuixar els botons
        rectPlay = new Rect(0, yBoto,  getWidth(), getHeight());
        rectPause = rectPlay ;
        // Starting the thread
        if (game2Thread!=null) return;
        game2Thread = new MyArkanoidThread(getHolder());
        game2Thread.start();
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,int format,int width,int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread ();
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // Remember this touch position for the move event
                ultimaXPaleta = ev.getX();
                ultimaYPaleta = ev.getY();
                // Control botons play/pause
                if (inici) {
                    // Bola parada fins que cliquem en el botó play
                    if (rectPlay.contains((int) ultimaXPaleta, (int) ultimaYPaleta)) {
                        // No mostrem el botó inici
                        inici = false;
                        // Iniciem el joc
                        xDirection = xDirectionAux;
                        yDirection = yDirectionAux;
                        // Generem els bloc en funció de l'amplada de la pantalla
                        // i el numBlocs
                        visibilitzarBlocs();
                        fillBlocs(getWidth());
                    }
                }
                else {

                }
                break;

            }
            case MotionEvent.ACTION_MOVE: {
                // Calculate the distance moved
                final float dx = ev.getX() - ultimaXPaleta;
                // Moviment paleta en horitzaontal i entre bordes
                if (xPaleta < 0 ) xPaleta = 0;
                if (xPaleta + ample > ampladaFons) xPaleta = ampladaFons - ample;
                xPaleta += dx;
                // Remember this touch position for the next move
                ultimaXPaleta = ev.getX();
                ultimaYPaleta = ev.getY();
                break;
            }
        }
        return true;
    } // Fi  onTouchEvent

    public void stopThread () {
        game2Thread.stop = true;
    }

    // The thread -----------------------------------------------
    private class MyArkanoidThread extends Thread {

        public boolean stop = false;
        private SurfaceHolder surfaceHolder;

        public MyArkanoidThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void run() { // The movement of the ball
            while (!stop) {
                // The movement of the ball
                x += xDirection;    y += yDirection;
                // The conditions: Bouncing on the left
                if (x < radius) {
                    x = radius;
                    xDirection = -xDirection; }
                // Bouncing on the right
                if (x > getWidth() - radius) {
                    x = getWidth() - radius;
                    xDirection = -xDirection;}
                // Bouncing on the top
                if (y < radius) {
                    y = radius;
                    yDirection = -yDirection;
                }
                // Xoc contra els blocs ------------------------
                if (!quedenBlocsVisibles()) {
                    inici = true;
                    xDirection = 0; yDirection = 0;
                }
                else {
                    if (y - radius < blocSize * numFiles) {
                        // A quin bloc li hem pegat?
                        // Divisió entera (floor) de x entre blocSize
                        numBloc = (int) Math.floor(x / blocSize);
                        // ------------------------------------------
                        // El bloc està visible?
                        if (blocs.get(numBloc).getVisible()) {
                            if (dins) { // Dins d'un bloc invisible
                                // Rebot lateral
                                xDirection = -xDirection;
                            } else { // Xoc des de baix
                                // El fem invisible i la pilota rebota
                                blocs.get(numBloc).setVisible(false);
                                y = radius + blocSize * numFiles;
                                yDirection = -yDirection;
                                dins = false;
                            }
                        } else {
                            // Bloc ocult => la pilota continua dins
                            dins = true;
                        }
                    } else dins = false;
                    // Fi xoc contra blocs ---------------------------
                    // Bouncing on the bottom
                    if (y > getHeight() - radius) {
                        y = getHeight() - radius;
                        yDirection = -yDirection;
                    }
                    // Bouncing on the palette
                    if (y + radius > yPaleta) {
                        if ((x + radius > xPaleta)
                                && (x - radius < xPaleta + ample)) {
                            yDirection = -yDirection;
                        }
                    }
                }
                Canvas c = null;
                try {  c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        newDraw (c);
                    }
                } finally {
                    if (c != null)
                        surfaceHolder.unlockCanvasAndPost(c);}
            }  // while
        } // run()
    }  // Bouncin

    public boolean quedenBlocsVisibles ()  {
        boolean aux = false;
        Log.d ("Blocs", "quedenBlocsVisibles inici: " + aux +  " Size: " + blocs.size()) ;
        for (int i = 0; i< blocs.size(); i++ ) {
            if (blocs.get(i).getVisible()) {
                aux = true;
                Log.d ("Blocs", "quedenBlocsVisibles i: " + i ) ;
                break;
            }
        }
        Log.d ("Blocs", "quedenBlocsVisibles final: " + aux) ;
        return aux;
    }

    public void visibilitzarBlocs () {
        // Per iniciar una altra partida
        for (int i = 0; i< blocs.size(); i++ ) {
            blocs.get(i).setVisible(true);
        }

    }


}
