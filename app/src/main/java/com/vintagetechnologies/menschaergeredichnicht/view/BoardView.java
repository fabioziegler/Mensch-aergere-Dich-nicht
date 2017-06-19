package com.vintagetechnologies.menschaergeredichnicht.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.implementation.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.Colorful;
import com.vintagetechnologies.menschaergeredichnicht.structure.EndSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;
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
     * @param context
     */
    public BoardView(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the BoardView
     * board is not necessary, just easier to access.
     */
    private void init() {
        paint = new Paint();
        board = Board.get();

        GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
        paint.setStrokeWidth(2);

        try {
            if (gameSettings.getBoardDesign() == GameSettings.BoardDesign.CLASSIC) {
                theme = new Theme(getContext().getAssets().open("themes/classic.json"));
            } else if (gameSettings.getBoardDesign() == GameSettings.BoardDesign.VINTAGE) {
                theme = new Theme(getContext().getAssets().open("themes/vintage.json"));
            }

        } catch (IOException e) {
            Log.e("BoardView", "Fehler", e);
        }
    }

    public GamePiece getHighlightedGamePiece() {
        return highlightedGamePiece;
    }

    public void setHighlightedGamePiece(GamePiece highlightedGamePiece) {
        this.highlightedGamePiece = highlightedGamePiece;
    }

    /**
     * Overrieds Views onDraw method.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (!boardIsDrawn) {
            boardIsDrawn = true;
        }

        spotRadius = getWidth() / 40.0;
        abstand = (getWidth() - (22 * spotRadius)) / 22;

        drawBoard(canvas);

        drawGamePieces(canvas);
    }

    /**
     * Draws the GamePieces
     *
     * @param canvas
     */
    private void drawGamePieces(Canvas canvas) {
        for (Player p : ActualGame.getInstance().getGameLogic().getPlayers()) {

            paint.setColor(theme.getColor(p.getColor().toString()));

            paint.setStyle(Paint.Style.FILL);
            for (GamePiece gp : p.getPieces()) {

                Spot n = gp.getSpot();

                double x = (2 * n.getX() + 1) * (spotRadius + abstand);
                double y = (2 * n.getY() + 1) * (spotRadius + abstand);

                canvas.drawCircle((float) x, (float) y, (float) spotRadius / 2, paint);
            }


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(theme.getColor("BORDER_"+p.getColor().toString()));
            for (GamePiece gp : p.getPieces()) {
                Spot n = gp.getSpot();

                double x = (2 * n.getX() + 1) * (spotRadius + abstand);
                double y = (2 * n.getY() + 1) * (spotRadius + abstand);

                canvas.drawCircle((float) x, (float) y, (float) spotRadius / 2, paint);
            }

        }

        GameSettings gameSettings = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class);


        if (!ActualGame.getInstance().isLocalGame() && DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, GameLogic.class).isHost() && !ActualGame.getInstance().getGameLogic().getCurrentPlayer().getName().equals(gameSettings.getPlayerName())) {
            //network game && clients turn
        }else{
            drawSelected(canvas);
        }

    }

    private void drawSelected(Canvas canvas) {
        paint.setStrokeWidth(10);

        if (ActualGame.getInstance().getGameLogic().getPossibleToMove() != null) {
            for (GamePiece gp : ActualGame.getInstance().getGameLogic().getPossibleToMove()) {
                Spot s = gp.getSpot();
                Spot t = board.checkSpot(RealDice.get().getDiceNumber(), gp);


                double x = (2 * s.getX() + 1) * (spotRadius + abstand);
                double y = (2 * s.getY() + 1) * (spotRadius + abstand);

                double xx = (2 * t.getX() + 1) * (spotRadius + abstand);
                double yy = (2 * t.getY() + 1) * (spotRadius + abstand);

                paint.setColor(theme.getColor(gp.getPlayerColor().toString()));
                paint.setStyle(Paint.Style.FILL);

                canvas.drawCircle((float) x, (float) y, (float) spotRadius * 3 / 2, paint);

                paint.setStyle(Paint.Style.STROKE);

                canvas.drawCircle((float) xx, (float) yy, (float) spotRadius * 3 / 2, paint);

            }
        }


        if (highlightedGamePiece != null) {
            Spot s = highlightedGamePiece.getSpot();
            Spot t = board.checkSpot(RealDice.get().getDiceNumber(), highlightedGamePiece);

            double x = (2 * s.getX() + 1) * (spotRadius + abstand);
            double y = (2 * s.getY() + 1) * (spotRadius + abstand);

            double xx = (2 * t.getX() + 1) * (spotRadius + abstand);
            double yy = (2 * t.getY() + 1) * (spotRadius + abstand);


            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);

            canvas.drawCircle((float) x, (float) y, (float) spotRadius * 3 / 2, paint);
            canvas.drawLine((float) (x - spotRadius * 1.0607), (float) (y - spotRadius * 1.0607), (float) (x + spotRadius * 1.0607), (float) (y + spotRadius * 1.0607), paint);
            canvas.drawLine((float) (x - spotRadius * 1.0607), (float) (y + spotRadius * 1.0607), (float) (x + spotRadius * 1.0607), (float) (y - spotRadius * 1.0607), paint);


            // highlight destination:

            paint.setColor(theme.getColor(highlightedGamePiece.getPlayerColor().toString()));


            //DRAW ARROW

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine((float) (x), (float) (y), (float) (xx), (float) (yy), paint);

            double arrowHeadLength = spotRadius * 2;

            double multiplier = arrowHeadLength / Math.sqrt((xx - x) * (xx - x) + (yy - y) * (yy - y));

            double aPosX = (x - xx) * multiplier;
            double aPosY = (y - yy) * multiplier;


            canvas.drawLine((float) (x), (float) (y), (float) (xx + aPosX), (float) (yy + aPosY), paint); //arrow itself


            double ratio = 0.5;

            Path p = new Path();
            p.setFillType(Path.FillType.EVEN_ODD);

            p.moveTo((float) (xx + aPosX + aPosY * ratio), (float) (yy + aPosY - aPosX * ratio));
            p.lineTo((float) (xx + aPosX - aPosY * ratio), (float) (yy + aPosY + aPosX * ratio));
            p.lineTo((float) (xx), (float) (yy));
            p.close();

            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            canvas.drawPath(p, paint);

        }

        paint.setStrokeWidth(2);
    }


    /**
     * Draws the Board.
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {

        setBackgroundColor(theme.getColor("Backgroundcolor"));

        paint.reset();
        String backgroundImage;
        if ((backgroundImage = theme.getAttribute("BackgroundImage")) != null) {
            int id = getResources().getIdentifier(backgroundImage, "drawable", getContext().getPackageName());
            Bitmap vintageBackground = BitmapFactory.decodeResource(getResources(), id);
            canvas.drawBitmap(vintageBackground, 0, 0, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);


        double prevX = (2 * board.getBoard()[0].getX() + 1) * (spotRadius + abstand);
        double prevY = (2 * board.getBoard()[0].getY() + 1) * (spotRadius + abstand);


        RegularSpot s = (RegularSpot) board.getBoard()[0];
        RegularSpot n = s.getNextSpot();


        while (s != n) {


            n = n.getNextSpot();

            double x = (2 * n.getX() + 1) * (spotRadius + abstand);
            double y = (2 * n.getY() + 1) * (spotRadius + abstand);


            canvas.drawLine((float) prevX, (float) prevY, (float) x, (float) y, paint);
            prevX = x;
            prevY = y;

        }

        for (Spot spot : board.getBoard()) {
            double x = (2 * spot.getX() + 1) * (spotRadius + abstand);
            double y = (2 * spot.getY() + 1) * (spotRadius + abstand);


            if (spot instanceof RegularSpot) {
                paint.setColor(theme.getColor("SpotColor"));


                paintSetPlayerColor(spot);
            } else if (spot instanceof EndSpot || spot instanceof StartingSpot) {
                paint.setColor(theme.getColor(((Colorful) spot).getColor().toString()));
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) x, (float) y, (float) spotRadius, paint);


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawCircle((float) x, (float) y, (float) spotRadius, paint);
        }
    }

    private void paintSetPlayerColor(Spot spot) {
        for (PlayerColor colorP : PlayerColor.values()) {
            if (Board.getEntrance(colorP) == spot) {
                paint.setColor(theme.getColor("START_" + colorP.toString()));
            }
        }
    }

    public double getSpotRadius() {
        return spotRadius;
    }

    public double getAbstand() {
        return abstand;
    }
}
