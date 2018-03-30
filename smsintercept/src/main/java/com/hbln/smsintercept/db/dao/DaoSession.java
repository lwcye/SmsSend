package com.hbln.smsintercept.db.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.hbln.smsintercept.db.bean.MobileBean;
import com.hbln.smsintercept.db.bean.SmsBean;
import com.hbln.smsintercept.db.bean.SuccessSmsBean;

import com.hbln.smsintercept.db.dao.MobileBeanDao;
import com.hbln.smsintercept.db.dao.SmsBeanDao;
import com.hbln.smsintercept.db.dao.SuccessSmsBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig mobileBeanDaoConfig;
    private final DaoConfig smsBeanDaoConfig;
    private final DaoConfig successSmsBeanDaoConfig;

    private final MobileBeanDao mobileBeanDao;
    private final SmsBeanDao smsBeanDao;
    private final SuccessSmsBeanDao successSmsBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        mobileBeanDaoConfig = daoConfigMap.get(MobileBeanDao.class).clone();
        mobileBeanDaoConfig.initIdentityScope(type);

        smsBeanDaoConfig = daoConfigMap.get(SmsBeanDao.class).clone();
        smsBeanDaoConfig.initIdentityScope(type);

        successSmsBeanDaoConfig = daoConfigMap.get(SuccessSmsBeanDao.class).clone();
        successSmsBeanDaoConfig.initIdentityScope(type);

        mobileBeanDao = new MobileBeanDao(mobileBeanDaoConfig, this);
        smsBeanDao = new SmsBeanDao(smsBeanDaoConfig, this);
        successSmsBeanDao = new SuccessSmsBeanDao(successSmsBeanDaoConfig, this);

        registerDao(MobileBean.class, mobileBeanDao);
        registerDao(SmsBean.class, smsBeanDao);
        registerDao(SuccessSmsBean.class, successSmsBeanDao);
    }
    
    public void clear() {
        mobileBeanDaoConfig.clearIdentityScope();
        smsBeanDaoConfig.clearIdentityScope();
        successSmsBeanDaoConfig.clearIdentityScope();
    }

    public MobileBeanDao getMobileBeanDao() {
        return mobileBeanDao;
    }

    public SmsBeanDao getSmsBeanDao() {
        return smsBeanDao;
    }

    public SuccessSmsBeanDao getSuccessSmsBeanDao() {
        return successSmsBeanDao;
    }

}