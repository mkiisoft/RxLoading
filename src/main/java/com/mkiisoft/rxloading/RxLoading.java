package com.mkiisoft.rxloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;

public class RxLoading extends RelativeLayout {

    private static final int FADE_DURATION = 400;
    private static final int ANIMATION_DURATION = 150;

    private List<ImageView> listOfViews = new ArrayList<>();

    private AtomicBoolean untilBoolean = new AtomicBoolean(false);

    private Observable<ImageView> observable = null;

    private Completable initCompletable;

    private LinearLayout rootView;

    private Drawable drawable;

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    public RxLoading(Context context) {
        super(context);
        init();
    }

    public RxLoading(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RxLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init() {
        init(null);
    }

    private void init(AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.loading_view, this);

        this.setVisibility(GONE);

        rootView = view.findViewById(R.id.root_view);

        ImageView loadingOne = view.findViewById(R.id.loading_one);
        ImageView loadingTwo = view.findViewById(R.id.loading_two);
        ImageView loadingThree = view.findViewById(R.id.loading_three);

        listOfViews.addAll(Arrays.asList(loadingOne, loadingTwo, loadingThree));

        drawable = getContext().getDrawable(R.drawable.shape_loading);
        setImageDrawable(drawable);

        initCompletable = scaleAnimation().repeatUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return untilBoolean.get();
            }
        });

        if (attrs != null) {
            TypedArray attributes = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.RxLoading,
                    0,
                    0);

            try {
                int orientation = attributes.getInt(R.styleable.RxLoading_orientation, 0);
                rootView.setOrientation(orientation);

                String color = attributes.getString(R.styleable.RxLoading_color);
                if (!TextUtils.isEmpty(color) && color.startsWith("#") && drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY));
                    setImageDrawable(drawable);
                }
                float opacity = attributes.getFloat(R.styleable.RxLoading_opacity, 1);
                setOpacity(opacity);
            } finally {
                attributes.recycle();
            }
        }
    }

    private void setImageDrawable(Drawable drawable) {
        for (ImageView view : listOfViews) {
            view.setImageDrawable(drawable);
        }
    }

    public void setOrientation(Orientation orientation) {
        switch (orientation) {
            case VERTICAL:
                rootView.setOrientation(VERTICAL);
                break;
            case HORIZONTAL:
                rootView.setOrientation(HORIZONTAL);
                break;
            default:
                rootView.setOrientation(HORIZONTAL);
                break;
        }
    }

    public void setOrientation(int orientation) {
        if ((orientation >= 0 && orientation <= 1) && rootView != null) {
            rootView.setOrientation(orientation);
        }
    }

    public void setOpacity(float opacity) {
        rootView.setAlpha(opacity);
    }

    public void setColor(int color) {
        try {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColor(String color) {
        if (!TextUtils.isEmpty(color) && color.startsWith("#")) {
            try {
                drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY));
                setImageDrawable(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        this.setAlpha(0f);
        this.setVisibility(VISIBLE);
        this.animate()
                .alpha(1f)
                .setDuration(FADE_DURATION)
                .start();
        initCompletable.subscribe();
    }

    public void stop() {
        this.animate()
                .alpha(0f)
                .setDuration(FADE_DURATION)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        RxLoading.this.setAlpha(0f);
                        RxLoading.this.setVisibility(GONE);
                        untilBoolean.set(true);
                    }
                });

    }

    private Observable<ImageView> getObservable() {
        return observable == null ? Observable.fromIterable(listOfViews) : observable;
    }

    private Completable scaleAnimation() {
        return scaleInit().concatWith(scaleReverse());
    }

    private Completable scaleInit() {
        return getObservable().concatMapCompletable(new Function<ImageView, CompletableSource>() {
            @Override
            public CompletableSource apply(ImageView view) {
                return animateViewScale(view);
            }
        });
    }

    private Completable scaleReverse() {
        return getObservable().concatMapCompletable(new Function<ImageView, CompletableSource>() {
            @Override
            public CompletableSource apply(ImageView view) {
                return animateViewReverse(view);
            }
        });
    }

    private Completable animateViewScale(final ImageView view) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) {
                final ViewPropertyAnimator animation = view.animate();
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() {
                        animation.cancel();
                    }
                });
                animation.scaleX(1.2f).scaleY(1.2f).setDuration(ANIMATION_DURATION)
                        .setInterpolator(new LinearInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                emitter.onComplete();
                            }
                        }).start();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    private Completable animateViewReverse(final ImageView view) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) {
                final ViewPropertyAnimator animation = view.animate();
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() {
                        animation.cancel();
                    }
                });
                animation.scaleX(0.8f).scaleY(0.8f).setDuration(ANIMATION_DURATION)
                        .setInterpolator(new LinearInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                emitter.onComplete();
                            }
                        }).start();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}