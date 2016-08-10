package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;


public class PuzzleBoardView extends View{
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {

            for(int i=0 ; i<NUM_SHUFFLE_STEPS;i++)
            {
                ArrayList<PuzzleBoard> randomBoard = puzzleBoard.neighbours();
                puzzleBoard = new PuzzleBoard(randomBoard.get(random.nextInt(randomBoard.size())));

            }

            this.invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        PriorityQueueComparator pqc=new PriorityQueueComparator();
        PriorityQueue<PuzzleBoard> p = new PriorityQueue<>(1,pqc);
        p.add(puzzleBoard);

        do
        {
            PuzzleBoard c = p.poll();
            if(c.resolved())
            {
                while(c.previousBoard!=null)
                {
                    animation.add(c);
                    c = c.previousBoard;
                }
                Collections.reverse(animation);
            }
            else
            {
                ArrayList<PuzzleBoard> list = c. neighbours();
                for(int i=0; i<list.size();i++)
                {
                    p.add(list.get(i));
                }
            }

        }
        while(!p.isEmpty());
        this.invalidate();
    }
}
class PriorityQueueComparator implements Comparator<PuzzleBoard>{
    public int compare(PuzzleBoard s1, PuzzleBoard s2) {
        if (s1.priority() < s2.priority()) {
            return 1;
        }
        if (s1.priority() > s2.priority()) {
            return -1;
        }
        return 0;
    }
}
