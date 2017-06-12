package com.vintagetechnologies.menschaergeredichnicht.view;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;

/**
 * Created by johannesholzl on 11.06.17.
 */

public class BoardViewOnClickListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (ActualGame.getInstance().getGameLogic().getPossibleToMove() != null && event.getAction() == MotionEvent.ACTION_UP) {

            BoardView bv = ActualGame.getInstance().getBoardView();

            float xx = event.getX();
            float yy = event.getY();

            for (GamePiece gp : ActualGame.getInstance().getGameLogic().getPossibleToMove()) {

                double x = xx - (2 * gp.getSpot().getX() + 1) * (bv.getSpotRadius() + bv.getAbstand());
                double y = yy - (2 * gp.getSpot().getY() + 1) * (bv.getSpotRadius() + bv.getAbstand());

                if (Math.sqrt(x * x + y * y) < 100) {
                    bv.setHighlightedGamePiece(gp);
                    bv.invalidate();
                }
            }

            Log.i("BoardView", event.getAction() + ": (" + event.getX() + " / " + event.getY() + " )");

        }
        return true;
    }
}
