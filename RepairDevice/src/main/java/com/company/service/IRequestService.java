package com.company.service;

import com.company.configs.AccountUserDetail;
import com.company.entities.Department;
import com.company.entities.Request;
import com.lowagie.text.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface IRequestService extends Serializable{
    Request addRequestFixDivice(AccountUserDetail accountUserDetail, Request requestBody) throws Exception;

     Request findRequestByCode(String code);

    List<Request> findAllRequestNotHandle(AccountUserDetail accountUserDetail) throws Exception;

//    List<Request> findAllRequestDoneByUserLogin(AccountUserDetail accountUserDetail) throws Exception;

     Request updateRequest(AccountUserDetail accountUserDetail, Request requestSource, Request requestUpdate);

     void rejectRequest(AccountUserDetail accountUserDetail,Request request);

     void deleteRequest(Request request);

     void  approveRequest(AccountUserDetail accountUserDetail, Request request);

      void  finishRequest(AccountUserDetail accountUserDetail, Request request);

    void exportPdf(Request request) throws IOException, DocumentException;

    void assignRequestForDepartment(Department department, String request);

    void finishRequestByFixer(Request request1);

    List<Request> findRequestFinshed();

    List<Request> findRequestFixing(String code);
}
