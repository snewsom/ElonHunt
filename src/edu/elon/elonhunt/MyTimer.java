package edu.elon.elonhunt;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTimer extends TextView{
	
	private CountDownTimer timer = new CountDownTimer(30000,1000) {
		
		public void onTick(long millisUntilFinished) {
			setText("" + (millisUntilFinished / 1000));
		}
		
		public void onFinish() {
			setText("done!");
		}
	};

	public MyTimer(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		timer.start();
	}

}
