package com.company.repository;

import com.company.entities.Request;

import java.io.Serializable;
import java.util.List;

public interface IRequestRepository extends Serializable {
    void saveRequest(Request request);
    void updateRequest(Request request);
    void deleteRequest(Request request);
    Request findRequestByCode(String code);

    List<Request> findAllRequestNotAcceptOrWaiting(int department_id,String code, String status);


    List<Request> findAllRequestFinished();

    List<Request> findAllRequestFixing(String code);

    List<Request> findAllRequestFaild(String code);
}
