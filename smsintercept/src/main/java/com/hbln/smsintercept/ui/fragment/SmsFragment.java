package com.hbln.smsintercept.ui.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.CloseUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hbln.smsintercept.R;
import com.hbln.smsintercept.base.BaseFragment;
import com.hbln.smsintercept.db.DbWrapper;
import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.db.bean.SmsBean;
import com.hbln.smsintercept.db.bean.SuccessSmsBean;
import com.hbln.smsintercept.db.dao.SuccessSmsBeanDao;
import com.hbln.smsintercept.event.NotifyAdapter;
import com.hbln.smsintercept.network.ApiResult;
import com.hbln.smsintercept.receiver.SmsObserver;
import com.hbln.smsintercept.ui.activity.HomeActivity;
import com.hbln.smsintercept.ui.adapter.RUAdapter;
import com.hbln.smsintercept.ui.adapter.RUViewHolder;
import com.hbln.smsintercept.ui.widget.DrawableCenterButton;
import com.hbln.smsintercept.ui.widget.SimpleCalendarDialogFragment;
import com.hbln.smsintercept.utils.TitleUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/7.
 */

public class SmsFragment extends BaseFragment implements View.OnClickListener, RUAdapter.OnItemClickListener {
    public List<SmsBean> mSmsList = new ArrayList<>();
    private View view;
    /** 查询短信 */
    private DrawableCenterButton mBtnSmsQuery;
    /** 批量上传 */
    private DrawableCenterButton mBtnSmsUpload;
    /** 设置号码接受短信90条\n上传成功70条\n上传失败10条 */
    private AppCompatTextView mTvSmsTotal;
    private RecyclerView mRvSms;
    private RUAdapter<SmsBean> mAdapter;

    @Override
    public void initView(View view) {
        TitleUtil.attach(view).setTitle("短信")
                .setRightDrawable(0, 0, R.drawable.icon_setting, 0)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mView) {
                        ((HomeActivity) getBaseActivity()).showFragment(2);
                    }
                });
        mBtnSmsQuery = (DrawableCenterButton) view.findViewById(R.id.btn_sms_query);
        mBtnSmsQuery.setOnClickListener(this);
        mBtnSmsUpload = (DrawableCenterButton) view.findViewById(R.id.btn_sms_upload);
        mBtnSmsUpload.setOnClickListener(this);
        mTvSmsTotal = (AppCompatTextView) view.findViewById(R.id.tv_sms_total);
        mRvSms = (RecyclerView) view.findViewById(R.id.rv_sms);
    }

    @Override
    public void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //列表
        mAdapter = new RUAdapter<SmsBean>(getContext(), mSmsList, R.layout.item_main) {
            @Override
            protected void onInflateData(RUViewHolder holder, SmsBean data, int position) {
                holder.setText(R.id.tv_item_main_moblie, data.getMobile());
                holder.setText(R.id.tv_item_main_content, "\t\t\t" + data.getContent());
                holder.setText(R.id.tv_item_main_date, TimeUtils.millis2String(data.getCreate_time()));
                TextView tvState = holder.getViewById(R.id.tv_item_main_state);
                if (data.getIsSuccess()) {
                    tvState.setText("已完成");
                    tvState.setTextColor(Color.GREEN);
                } else {
                    tvState.setText(data.getErrorMsg());
                    tvState.setTextColor(Color.RED);
                    SmsBean.requestPostSms(data, new Action1<SmsBean>() {
                        @Override
                        public void call(SmsBean mSmsBean) {
                        }
                    });
                }
            }
        };
        mAdapter.setOnItemClickListener(this);
        mRvSms.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvSms.setItemAnimator(new DefaultItemAnimator());
        mRvSms.setAdapter(mAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sms;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sms_query:
                mSmsList.clear();
                mAdapter.setData(mSmsList);
                SimpleCalendarDialogFragment simpleCalendarDialogFragment = new SimpleCalendarDialogFragment();
                simpleCalendarDialogFragment.setListener(new SimpleCalendarDialogFragment.OnSureListener() {
                    @Override
                    public void sureListener(long date) {
                        loadSmsData(date);
                        showLoading("导入数据中");
                    }
                });
                simpleCalendarDialogFragment.show(getBaseActivity().getSupportFragmentManager(), "simple-calendar");
                break;
            case R.id.btn_sms_upload:
                showLoading("上传数据中");
                requestPostSmsList();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, int itemType, final int position) {
        final SmsBean smsBean = mSmsList.get(position);
        if (smsBean.getIsSuccess()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("重新上传");
        builder.setMessage(smsBean.getMobile() + "：" + smsBean.getContent());
        builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmsBean.requestPostSms(smsBean, new Action1<SmsBean>() {
                    @Override
                    public void call(SmsBean mSmsBean) {

                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyAdapter(NotifyAdapter notifyAdapter) {
        hideLoading();
        if (!mSmsList.contains(notifyAdapter.mSmsBean)) {
            //数量
            long countAll = DbWrapper.getSession().getSmsBeanDao().count();
            long countSuccess = DbWrapper.getSession().getSuccessSmsBeanDao().count();
            mTvSmsTotal.setText("接受短信" + (countAll + countSuccess) + "条\n上传成功" + countSuccess + "条\n上传失败" + countAll + "条");
            //刷新列表
            mAdapter.addDataFirst(notifyAdapter.mSmsBean);
        }
    }

    /**
     * 查询数据
     *
     * @param selectorDate 选择时间
     */
    public void loadSmsData(final long selectorDate) {
        mSmsList = new ArrayList<>();
        Observable.from(DbWrapper.getSession().getMobileBeanDao().loadAll())
                .flatMap(new Func1<MobileBean, Observable<String>>() {
                    @Override
                    public Observable<String> call(MobileBean mobileBean) {
                        return Observable.just(mobileBean.getMobile());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
                        long minDate;
                        if (selectorDate <= 0) {
                            long day = System.currentTimeMillis() % TimeConstants.DAY + 8 * TimeConstants.HOUR;
                            minDate = System.currentTimeMillis() - day;
                        } else {
                            minDate = selectorDate;
                        }
                        String where = " address like '%" + mobile + "%' AND protocol = '0' AND type = 1 AND date >  "
                                + minDate;
                        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(SmsObserver.SMS_INBOX_URI), SmsObserver.PROJECTION,
                                where, null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
                        if (null == cursor) {
                            LogUtils.e(mobile + ":空数据");
                            return;
                        }
                        while (cursor.moveToNext()) {
                            String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                            LogUtils.e(date);
                            boolean saved = DbWrapper.getSession().getSuccessSmsBeanDao().count() > 0 &&
                                    DbWrapper.getSession().getSuccessSmsBeanDao().queryBuilder()
                                            .where(SuccessSmsBeanDao.Properties.Create_time.eq(date)).count() > 0;
                            mSmsList.add(new SmsBean(date, smsSender, smsBody, saved, saved ? "已完成" : "未上传"));
                        }
                        CloseUtils.closeIO(cursor);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String mobile) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        hideLoading();
                        throwable.printStackTrace();
                        ToastUtils.showShort("查询出错：" + throwable.getMessage());
                        mAdapter.setData(mSmsList);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        hideLoading();
                        Collections.sort(mSmsList, new Comparator<SmsBean>() {
                            @Override
                            public int compare(SmsBean o1, SmsBean o2) {
                                long i = o2.getCreate_time() - o1.getCreate_time();
                                return (int) i;
                            }
                        });
                        ToastUtils.showShort("查询到" + mSmsList.size() + "条短息");
                        mAdapter.setData(mSmsList);
                    }
                });
    }

    /**
     * 批量上传
     */
    public void requestPostSmsList() {
        Observable.from(mSmsList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Func1<SmsBean, Boolean>() {
                    @Override
                    public Boolean call(SmsBean smsBean) {
                        return !smsBean.getIsSuccess();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<SmsBean, Observable<ApiResult>>() {
                    @Override
                    public Observable<ApiResult> call(SmsBean smsBean) {
                        return SmsBean.getSmsBeanPostObservable(smsBean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiResult>() {
                    @Override
                    public void call(ApiResult apiResult) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showShort("上传出现错误:" + throwable.getMessage());
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        ToastUtils.showShort("完成上传");
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
}
