package com.ventus.ibs.gui;

/**
 * Created by ventus0905 on 04/12/2019
 */
public interface BaseFragment {

    /**
     * @return true if enable swipe to refresh
     */
//    boolean canSwipeToRefresh();

    /**
     * method to place data refresh
     */
//    void requestRefresh();

    /**
     * @return true if currently under refreshing
     */
//    boolean isRefreshing();

    /**
     * @return one of the possible icons inside HeaderIconType,
     * if nothing set return default
     * HeaderIconType.Hamburger
     */
    HeaderIconType getHeaderIconType();

    /**
     * invalidate the fragment
     */
    void invalidateUI();

    /**
     * @return tag for replacing fragment
     */
    String getTransactionTag();

    /**
     * @return false to continue normal back press, true to interrupt normal on back press
     */
    boolean onBackPressed();

    /**
     * @return corresponding header type
     */
    HeaderType getHeaderType();

}
