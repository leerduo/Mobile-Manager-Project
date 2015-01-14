package com.dystu.managerprov2.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dystu.managerprov2.AntiVirusActivity;
import com.dystu.managerprov2.CallSmsSaveActivity;
import com.dystu.managerprov2.CleanCacheActivity;
import com.dystu.managerprov2.R;
import com.sefford.circularprogressdrawable.CircularProgressDrawable;

public class Changyonggongneng extends Fragment implements OnClickListener {

	ImageView ivDrawable;

	private RelativeLayout qljs;
	private RelativeLayout hfll;
	private RelativeLayout srlj;
	private RelativeLayout txzs;
	private RelativeLayout zfbb;
	private RelativeLayout sjsd;

	CircularProgressDrawable drawable;

	Animator currentAnimation;

	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (currentAnimation != null) {
				currentAnimation.cancel();
			}
			switch (v.getId()) {
			case R.id.iv_drawable:
				currentAnimation = preparePulseAnimation();

				break;

			}
			currentAnimation.start();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ivDrawable = (ImageView) getView().findViewById(R.id.iv_drawable);

		qljs = (RelativeLayout) getView().findViewById(R.id.qljs);
		hfll = (RelativeLayout) getView().findViewById(R.id.hfll);
		srlj = (RelativeLayout) getView().findViewById(R.id.srlj);
		txzs = (RelativeLayout) getView().findViewById(R.id.txzs);
		zfbb = (RelativeLayout) getView().findViewById(R.id.zfbb);
		sjsd = (RelativeLayout) getView().findViewById(R.id.sjsd);

		qljs.setOnClickListener(this);
		hfll.setOnClickListener(this);
		srlj.setOnClickListener(this);
		txzs.setOnClickListener(this);
		zfbb.setOnClickListener(this);
		sjsd.setOnClickListener(this);

		drawable = new CircularProgressDrawable.Builder()
				.setRingWidth(
						getResources().getDimensionPixelSize(
								R.dimen.drawable_ring_size))
				.setOutlineColor(
						getResources().getColor(android.R.color.darker_gray))
				.setRingColor(
						getResources().getColor(
								android.R.color.holo_green_light))
				.setCenterColor(
						getResources().getColor(android.R.color.holo_blue_dark))
				.create();
		ivDrawable.setImageDrawable(drawable);
		hookUpListeners();
		
		//preparePulseAnimation();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_cygn, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		ivDrawable = (ImageView) getView().findViewById(R.id.iv_drawable);
		drawable = new CircularProgressDrawable.Builder()
				.setRingWidth(
						getResources().getDimensionPixelSize(
								R.dimen.drawable_ring_size))
				.setOutlineColor(
						getResources().getColor(android.R.color.darker_gray))
				.setRingColor(
						getResources().getColor(
								android.R.color.holo_green_light))
				.setCenterColor(
						getResources().getColor(android.R.color.holo_blue_dark))
				.create();
		ivDrawable.setImageDrawable(drawable);
		hookUpListeners();
	}

	private void hookUpListeners() {
		ivDrawable.setOnClickListener(listener);

	}

	/**
	 * This animation will make a pulse effect to the inner circle
	 *
	 * @return Animation
	 */
	public Animator preparePulseAnimation() {
		AnimatorSet animation = new AnimatorSet();

		Animator firstBounce = ObjectAnimator.ofFloat(drawable,
				CircularProgressDrawable.CIRCLE_SCALE_PROPERTY,
				drawable.getCircleScale(), 0.88f);
		firstBounce.setDuration(300);
		firstBounce.setInterpolator(new CycleInterpolator(1));
		Animator secondBounce = ObjectAnimator.ofFloat(drawable,
				CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0.75f, 0.83f);
		secondBounce.setDuration(300);
		secondBounce.setInterpolator(new CycleInterpolator(1));
		Animator thirdBounce = ObjectAnimator.ofFloat(drawable,
				CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0.75f, 0.80f);
		thirdBounce.setDuration(300);
		thirdBounce.setInterpolator(new CycleInterpolator(1));

		ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(drawable,
				CircularProgressDrawable.PROGRESS_PROPERTY, 0f, 1f);
		progressAnimation.setDuration(1000);
		progressAnimation
				.setInterpolator(new AccelerateDecelerateInterpolator());

		ObjectAnimator colorAnimator = ObjectAnimator.ofInt(drawable,
				CircularProgressDrawable.RING_COLOR_PROPERTY, getResources()
						.getColor(android.R.color.holo_red_dark),
				getResources().getColor(android.R.color.holo_green_light),
				getResources().getColor(android.R.color.white));
		colorAnimator.setEvaluator(new ArgbEvaluator());
		colorAnimator.setDuration(1000);

		animation.playTogether(progressAnimation, colorAnimator);

		animation.playSequentially(firstBounce, secondBounce, thirdBounce);
		return animation;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qljs:
			CleanCacheActivity.actionStart(getActivity());
			break;
		case R.id.hfll:
			
			Toast.makeText(getActivity(), "该功能尚未实现,下一版本实现", 0).show();

			break;
		case R.id.srlj:
			
			CallSmsSaveActivity.actionStart(getActivity());

			break;
		case R.id.txzs:

			break;
		case R.id.zfbb:

			break;
		case R.id.sjsd:
			
			AntiVirusActivity.actionStart(getActivity());

			break;

		default:
			break;
		}

	}

}
