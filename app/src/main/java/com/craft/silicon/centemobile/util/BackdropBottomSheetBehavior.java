//package com.craft.silicon.centemobile.util;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.core.widget.NestedScrollView;
//
//import java.lang.ref.WeakReference;
//
//public class BackdropBottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
//
//
//    private WeakReference<BottomSheetBehaviorGoogleMapsLike> mBottomSheetBehaviorRef;
//    /**
//     * Following {@link #onDependentViewChanged}'s docs mCurrentChildY just save the child Y
//     * position.
//     */
//    private int mCurrentChildY;
//
//    public BackdropBottomSheetBehavior(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//        if (dependency instanceof NestedScrollView) {
//            try {
//                BottomSheetBehaviorGoogleMapsLike.from(dependency);
//                return true;
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        /**
//         * collapsedY and achorPointY are calculated every time looking for
//         * flexibility, in case that dependency's height, child's height or {@link BottomSheetBehaviorGoogleMapsLike#getPeekHeight()}'s
//         * value changes throught the time, I mean, you can have a {@link android.widget.ImageView}
//         * using images with different sizes and you don't want to resize them or so
//         */
//        if (mBottomSheetBehaviorRef == null || mBottomSheetBehaviorRef.get() == null)
//            getBottomSheetBehavior(parent);
//        /**
//         * mCollapsedY: Y position in where backdrop get hidden behind dependency.
//         * {@link BottomSheetBehaviorGoogleMapsLike#getPeekHeight()} and collapsedY are the same point on screen.
//         */
//        int collapsedY = dependency.getHeight() - mBottomSheetBehaviorRef.get().getPeekHeight();
//        /**
//         * achorPointY: with top being Y=0, achorPointY defines the point in Y where could
//         * happen 2 things:
//         * The backdrop should be moved behind dependency view (when {@link #mCurrentChildY} got
//         * positive values) or the dependency view overlaps the backdrop (when
//         * {@link #mCurrentChildY} got negative values)
//         */
//        int achorPointY = child.getHeight();
//        /**
//         * lastCurrentChildY: Just to know if we need to return true or false at the end of this
//         * method.
//         */
//        int lastCurrentChildY = mCurrentChildY;
//
//        if ((mCurrentChildY = (int) ((dependency.getY() - achorPointY) * collapsedY / (collapsedY - achorPointY))) <= 0)
//            child.setY(mCurrentChildY = 0);
//        else
//            child.setY(mCurrentChildY);
//        return (lastCurrentChildY == mCurrentChildY);
//    }
//
//    /**
//     * Look into the CoordiantorLayout for the {@link BottomSheetBehaviorGoogleMapsLike}
//     *
//     * @param coordinatorLayout with app:layout_behavior= {@link BottomSheetBehaviorGoogleMapsLike}
//     */
//    private void getBottomSheetBehavior(@NonNull CoordinatorLayout coordinatorLayout) {
//
//        for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
//            View child = coordinatorLayout.getChildAt(i);
//
//            if (child instanceof NestedScrollView) {
//
//                try {
//                    BottomSheetBehaviorGoogleMapsLike temp = BottomSheetBehaviorGoogleMapsLike.from(child);
//                    mBottomSheetBehaviorRef = new WeakReference<>(temp);
//                    break;
//                } catch (IllegalArgumentException e) {
//                }
//            }
//        }
//    }
//}