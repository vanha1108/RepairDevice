package com.company.service;

import com.company.configs.AccountUserDetail;
import com.company.entities.Request;

import java.io.Serializable;
import java.util.List;

public interface IRequestService extends Serializable{
    public Request addRequestFixDivice(AccountUserDetail accountUserDetail, Request requestBody) throws Exception;

    public Request findRequestByCode(String code);

    List<Request> findAllRequestByUserLogin(AccountUserDetail accountUserDetail) throws Exception;

    List<Request> findAllRequestDoneByUserLogin(AccountUserDetail accountUserDetail) throws Exception;

    public Request updateRequest(AccountUserDetail accountUserDetail, Request requestSource, Request requestUpdate);

    public void rejectRequest(AccountUserDetail accountUserDetail,Request request);

    public void deleteRequest(Request request);

    public void  approveRequest(AccountUserDetail accountUserDetail, Request request);

    public  void  finishRequest(AccountUserDetail accountUserDetail, Request request);
}
