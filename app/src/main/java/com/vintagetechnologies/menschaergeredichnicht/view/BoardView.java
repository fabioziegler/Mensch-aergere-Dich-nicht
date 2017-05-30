package com.vintagetechnologies.menschaergeredichnicht.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.Impl.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.Colorful;
import com.vintagetechnologies.menschaergeredichnicht.structure.EndSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.RegularSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.structure.StartingSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Theme;

import java.io.IOException;

/**
 * Created by johannesholzl on 04.04.17.
 */

public class BoardView extends View {

    private Paint paint;
    private Board board;
    private boolean boardIsDrawn = false;
    private double spotRadius;
    private double abstand;
    private GamePiece highlightedGamePiece;
    private Theme theme;

    /**
     * Initializes the BoardView
     * board is not necessary, just easier to access.
     */
    private void init(){
        paint = new Paint();
        board = Board.get();

        GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
        paint.setStrokeWidth(2);

        try {
            if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.CLASSIC){
                theme = new Theme(getContext().getAssets().open("themes/classic.json"));
            }else if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.VINTAGE){
                theme = new Theme(getContext().getAssets().open("themes/vintage.json"));
            }

        } catch (IOException e) {
			Log.e("BoardView", "Fehler", e);
        }
    }


    /**
     *
     * @param context
     */
    public BoardView(Context context) {
        super(context);
        init();



    }

    /**
     *
     * @param context
     * @param attrs
     */
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GamePiece getHighlightedGamePiece() {
        return highlightedGamePiece;
    }

    public void setHighlightedGamePiece(GamePiece highlightedGamePiece) {
        this.highlightedGamePiece = highlightedGamePiece;
    }

    /**
     * Overrieds Views onDraw method.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(!boardIsDrawn){
            boardIsDrawn = true;
        }

        System.out.println(canvas);

        spotRadius = getWidth()/40.0;
        abstand = (getWidth()-(22*spotRadius))/22;

        drawBoard(canvas);

        drawGamePieces(canvas);
    }

    /**
     * Draws the GamePieces
     * @param canvas
     */
    private void drawGamePieces(Canvas canvas){
        for(Player p : ActualGame.getInstance().getGameLogic().getPlayers()){

            paint.setColor(theme.getColor(p.getColor().toString()));
            /*switch (p.getColor()){
                case RED:{
                    paint.setColor(Color.RED);
                    break;}
                case GREEN:{
                    paint.setColor(Color.GREEN);
                    break;}
                case YELLOW:{
                    paint.setColor(Color.YELLOW);
                    break;}
                case BLUE:{
                    paint.setColor(Color.BLUE);
                    break;}
            }*/
            paint.setStyle(Paint.Style.FILL);
            for(GamePiece gp : p.getPieces()){

                Spot n = gp.getSpot();

                double x = (2*n.getX()+1)*(spotRadius+abstand);
                double y = (2*n.getY()+1)*(spotRadius+abstand);

                canvas.drawCircle((float)x, (float)y, (float)spotRadius/2, paint);
            }


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            for(GamePiece gp : p.getPieces()){
                Spot n = gp.getSpot();

                double x = (2*n.getX()+1)*(spotRadius+abstand);
                double y = (2*n.getY()+1)*(spotRadius+abstand);

                canvas.drawCircle((float)x, (float)y, (float)spotRadius/2, paint);
            }

        }

        paint.setStrokeWidth(10);

        if(ActualGame.getInstance().getGameLogic().getPossibleToMove() != null){
            for(GamePiece gp: ActualGame.getInstance().getGameLogic().getPossibleToMove()){
                Spot s = gp.getSpot();
                Spot t = board.checkSpot(RealDice.get().getDiceNumber(), gp);


                double x = (2*s.getX()+1)*(spotRadius+abstand);
                double y = (2*s.getY()+1)*(spotRadius+abstand);

                double xx = (2*t.getX()+1)*(spotRadius+abstand);
                double yy = (2*t.getY()+1)*(spotRadius+abstand);

                paint.setColor(theme.getColor((gp.getPlayerColor().toString())));
                paint.setStyle(Paint.Style.FILL);

                canvas.drawCircle((float)x, (float)y, (float)spotRadius*3/2, paint);

                paint.setStyle(Paint.Style.STROKE);

                canvas.drawCircle((float)xx, (float)yy, (float)spotRadius*3/2, paint);

            }
        }


        if(highlightedGamePiece != null){
            Spot s = highlightedGamePiece.getSpot();
            Spot t = board.checkSpot(RealDice.get().getDiceNumber(), highlightedGamePiece);

            double x = (2*s.getX()+1)*(spotRadius+abstand);
            double y = (2*s.getY()+1)*(spotRadius+abstand);

            double xx = (2*t.getX()+1)*(spotRadius+abstand);
            double yy = (2*t.getY()+1)*(spotRadius+abstand);





            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawCircle((float)x, (float)y, (float)spotRadius*3/2, paint);
            canvas.drawLine((float)(x-spotRadius*1.0607),  (float)(y-spotRadius*1.0607), (float)(x+spotRadius*1.0607), (float)(y+spotRadius*1.0607), paint);
            canvas.drawLine((float)(x-spotRadius*1.0607),  (float)(y+spotRadius*1.0607), (float)(x+spotRadius*1.0607), (float)(y-spotRadius*1.0607), paint);



            // highlight destination:

            paint.setColor(theme.getColor((highlightedGamePiece.getPlayerColor().toString())));




            //canvas.drawLine((float)(xx-spotRadius),  (float)(yy-spotRadius), (float)(xx+spotRadius), (float)(yy+spotRadius), paint);
            //canvas.drawLine((float)(xx-spotRadius),  (float)(yy+spotRadius), (float)(xx+spotRadius), (float)(yy-spotRadius), paint);



            //DRAW ARROW

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine((float)(x),  (float)(y), (float)(xx), (float)(yy), paint);

            double arrowHeadLength = spotRadius*2;

            double multiplier = arrowHeadLength/Math.sqrt((xx-x)*(xx-x)+(yy-y)*(yy-y));

            double aPosX = (x-xx)*multiplier;
            double aPosY = (y-yy)*multiplier;


            canvas.drawLine((float)(x),  (float)(y), (float)(xx+aPosX), (float)(yy+aPosY), paint); //arrow itself


            double ratio = 0.5;

            Path p = new Path();
            p.setFillType(Path.FillType.EVEN_ODD);

            p.moveTo((float)(xx+aPosX+aPosY*ratio), (float)(yy+aPosY-aPosX*ratio));
            p.lineTo((float)(xx+aPosX-aPosY*ratio), (float)(yy+aPosY+aPosX*ratio));
            p.lineTo((float)(xx),  (float)(yy));
            p.close();

            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            canvas.drawPath(p, paint);

            //canvas.drawLine((float)(xx+aPosX+aPosY), (float)(yy+aPosY-aPosX), (float)(xx+aPosX-aPosY), (float)(yy+aPosY+aPosX), paint); //head
            //canvas.drawLine((float)(xx),  (float)(yy), (float)(xx+aPosX-aPosY), (float)(yy+aPosY+aPosX), paint);
            //canvas.drawLine((float)(xx),  (float)(yy), (float)(xx+aPosX+aPosY), (float)(yy+aPosY-aPosX), paint);

        }

        paint.setStrokeWidth(2);


    }



    public void onClick(){
        System.out.println("CLICK");
    }




    /**
     * Draws the Board.
     * @param canvas
     */
    private void drawBoard(Canvas canvas){

        //GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
        /**
        String path = "vintage_holz.jpg";
        Drawable d = null;
        try {
            d = Drawable.createFromStream(ActualGame.getInstance().getGameactivity().getAssets().open(path), null);
        } catch(Exception e) {

        }
         */
        // if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.CLASSIC)

            setBackgroundColor(theme.getColor("Backgroundcolor"));
        //else
          //  setBackgroundColor();

        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        //canvas.drawCircle(getWidth()/4, getHeight()/4, getHeight()/4, paint);


        double prev_x = (2*board.getBoard()[0].getX()+1)*(spotRadius+abstand);
        double prev_y = (2*board.getBoard()[0].getY()+1)*(spotRadius+abstand);


        RegularSpot s = (RegularSpot)board.getBoard()[0];
        RegularSpot n = s.getNextSpot();


        while(s != n){


            n = n.getNextSpot();

            double x = (2*n.getX()+1)*(spotRadius+abstand);
            double y = (2*n.getY()+1)*(spotRadius+abstand);


            canvas.drawLine((float)prev_x, (float)prev_y, (float)x, (float)y, paint);
            prev_x = x;
            prev_y = y;

        }

        for(Spot spot : board.getBoard()){
            double x = (2*spot.getX()+1)*(spotRadius+abstand);
            double y = (2*spot.getY()+1)*(spotRadius+abstand);



            if(spot instanceof RegularSpot) {
                paint.setColor(theme.getColor("SpotColor"));
            }else if(spot instanceof EndSpot || spot instanceof  StartingSpot){
                paint.setColor(theme.getColor(((Colorful) spot).getColor().toString()));
                /*
                switch (((Colorful) spot).getColor()){
                    case RED:{
                        paint.setColor(Color.RED);
                        break;}
                    case GREEN:{
                        paint.setColor(Color.GREEN);
                        break;}
                    case YELLOW:{
                        paint.setColor(Color.YELLOW);
                        break;}
                    case BLUE:{
                        paint.setColor(Color.BLUE);
                        break;}
                }
                */
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float)x, (float)y, (float)spotRadius, paint);


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawCircle((float)x, (float)y, (float)spotRadius, paint);
        }
    }

    public double getSpotRadius() {
        return spotRadius;
    }

    public double getAbstand() {
        return abstand;
    }
}
