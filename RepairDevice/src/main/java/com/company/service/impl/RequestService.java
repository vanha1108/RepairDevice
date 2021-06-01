package com.company.service.impl;

import com.company.configs.AccountUserDetail;
import com.company.constant.EnumRole;
import com.company.constant.EnumStatus;
import com.company.constant.HandleStatus;
import com.company.entities.Account;
import com.company.entities.Request;
import com.company.repository.IRequestRepository;
import com.company.service.IAccountService;
import com.company.service.IRequestService;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
public class RequestService implements IRequestService {
    @Autowired
    private IRequestRepository requestRepository;

    @Autowired
    private HandleStatus handleStatus;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private IAccountService accountService;

    @Override
    public Request addRequestFixDivice(AccountUserDetail accountUserDetail, Request requestBody) throws Exception {
       try {
           do {
               requestBody.setCode(UUID.randomUUID().toString());
               if(requestRepository.findRequestByCode(requestBody.getCode()) != null){
                   requestBody.setCode("");
               }
           }while (requestBody.getCode().equals(""));

           requestBody.setStatus(EnumStatus.WAIT_MANAGER.toString());
           requestBody.setCreatedDate(new Date());
           requestBody.setLastModifiedDate(new Date());
           requestBody.setCreatedBy(accountUserDetail.getAccountCode());
           requestBody.setModifiedBy(accountUserDetail.getAccountCode());
           requestRepository.saveRequest(requestBody);
           return requestBody;
       }catch(Exception a) {
           throw new Exception("Data is not execute valid before request");
       }

    }

    @Override
    public Request findRequestByCode(String code) {
        return requestRepository.findRequestByCode(code);
    }

    @Override
    public List<Request> findAllRequestNotHandle(AccountUserDetail accountUserDetail) throws Exception {
        try {
            String role =String.valueOf(accountUserDetail.getAuthorities());
            role = role.replace("[","").replace("]","");
            String status = handleStatus.getStatusBeforeHandle(role);

            List<Request> requestList;
            System.out.println("Initial");
            if(role.equals(EnumRole.MANAGER.toString())){
                requestList = requestRepository.findAllRequestNotAcceptOrWaiting(accountUserDetail.getDepartment().getId(),"",status);
                System.out.println("Get complete");
            }else if(role.equals(EnumRole.EMPLOYEE.toString())){
                requestList = requestRepository.findAllRequestNotAcceptOrWaiting(-1,accountUserDetail.getAccountCode(),status);
            }else {
                requestList = requestRepository.findAllRequestNotAcceptOrWaiting(-1,"",status);
            }
            return requestList;
        }catch(Exception a){
            throw new Exception(a.toString());
        }
    }

//    @Override
//    public List<Request> findAllRequestDoneByUserLogin(AccountUserDetail accountUserDetail) throws Exception {
//        try {
//            String role =String.valueOf(accountUserDetail.getAuthorities());
//            role = role.replace("[","").replace("]","");
//            String status = handleStatus.getStatusHandled(role);
//
//            List<Request> requestListDone;
//
//            if(role.equals(EnumRole.MANAGER.toString())){
//                requestListDone = requestRepository.findAllRequestNotAcceptOrWaiting(accountUserDetail.getDepartment().getId(),"",status);
//            }else if(role.equals(EnumRole.EMPLOYEE.toString())){
//                requestListDone = requestRepository.findAllRequestNotAcceptOrWaiting(-1,accountUserDetail.getAccountCode(),status);
//            }else {
//                requestListDone = requestRepository.findAllRequestNotAcceptOrWaiting(-1,"",status);
//            }
//            return requestListDone;
//        }catch(Exception a){
//            throw new Exception("Error login token");
//        }
//    }

    @Override
    public Request updateRequest(AccountUserDetail accountUserDetail, Request requestSource, Request requestUpdate) {
        requestSource.setSolution(requestUpdate.getSolution());
        requestSource.setReason(requestUpdate.getReason());
        requestSource.setLastModifiedDate(new Date());
        requestSource.setModifiedBy(accountUserDetail.getAccountCode());
        requestSource.setStatus(EnumStatus.WAIT_MANAGER.toString());
        requestRepository.updateRequest(requestSource);

        return requestSource;
    }

    @Override
    public void rejectRequest(AccountUserDetail accountUserDetail, Request request) {
        request.setStatus(EnumStatus.FAILED.toString());
        request.setLastModifiedDate(new Date());
        request.setModifiedBy(accountUserDetail.getAccountCode());
        requestRepository.updateRequest(request);
    }

    @Override
    public void deleteRequest(Request request) {
        requestRepository.deleteRequest(request);
        return;
    }

    @Override
    public void approveRequest(AccountUserDetail accountUserDetail, Request request) {
        request.setStatus(handleStatus.getStatusApproved(accountUserDetail.getRole()));
        request.setLastModifiedDate(new Date());
        request.setModifiedBy(accountUserDetail.getAccountCode());
        requestRepository.updateRequest(request);
    }

    @Override
    public void finishRequest(AccountUserDetail accountUserDetail, Request request) {
        request.setStatus(EnumStatus.FINISHED.toString());
        request.setLastModifiedDate(new Date());
        request.setModifiedBy(accountUserDetail.getAccountCode());
        requestRepository.updateRequest(request);
    }

    @Override
    public void exportPdf(Request request) throws IOException, DocumentException {
        Context context = new Context();
        context.setVariable("reason",request.getReason());
        context.setVariable("solution",request.getSolution());
        System.out.println(context.getVariable("reason"));
        System.out.println(context.getVariable("solution"));
        String processHtml = templateEngine.process("request",context);

        OutputStream outputStream = new FileOutputStream("request.pdf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(processHtml);
        renderer.layout();
        renderer.createPDF(outputStream,false);
        renderer.finishPDF();
        outputStream.close();
    }

}
