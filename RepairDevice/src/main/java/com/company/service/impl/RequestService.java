package com.company.service.impl;

import com.company.configs.AccountUserDetail;
import com.company.constant.EnumRole;
import com.company.constant.EnumStatus;
import com.company.constant.HandleStatus;
import com.company.entities.Account;
import com.company.entities.Department;
import com.company.entities.Request;
import com.company.repository.IRequestRepository;
import com.company.service.IAccountService;
import com.company.service.IRequestService;
import com.company.storage.UserStorage;
import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private IAccountService accountService;

    @Autowired
    private HandleStatus handleStatus;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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

           sendSocketMessage(requestBody,1,"new request",200);//goi gui tin nhan socket

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
            if(role.equals(EnumRole.MANAGER.toString())){
                requestList = requestRepository.findAllRequestNotAcceptOrWaiting(accountUserDetail.getDepartment().getId(),"",status);
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
    
    @Override
    public Request updateRequest(AccountUserDetail accountUserDetail, Request requestSource, Request requestUpdate) {
        requestSource.setSolution(requestUpdate.getSolution());
        requestSource.setReason(requestUpdate.getReason());
        requestSource.setLastModifiedDate(new Date());
        requestSource.setModifiedBy(accountUserDetail.getAccountCode());
        requestSource.setStatus(EnumStatus.WAIT_MANAGER.toString());
        requestRepository.updateRequest(requestSource);

        sendSocketMessage(requestSource,1,"update request",200);//goi gui tin nhan socket
        return requestSource;
    }

    @Override
    public void rejectRequest(AccountUserDetail accountUserDetail, Request request) {
        request.setStatus(EnumStatus.FAILED.toString());
        request.setLastModifiedDate(new Date());
        request.setModifiedBy(accountUserDetail.getAccountCode());
        requestRepository.updateRequest(request);

        sendSocketMessage(request,0,"request reject",200);//goi gui tin nhan socket
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

        sendSocketMessage(request,1,"new request",200);//goi gui tin nhan socket
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
        String processHtml = templateEngine.process("request",context);

        OutputStream outputStream = new FileOutputStream("request.pdf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(processHtml);
        renderer.layout();
        renderer.createPDF(outputStream,false);
        renderer.finishPDF();
        outputStream.close();
    }

    @Override
    public void assignRequestForDepartment(Department department, String request) {
        Account account = accountService.findUserByDepartmentId(department.getId());
        if(account != null){
            Request request1 = requestRepository.findRequestByCode(request);
            request1.setStatus(EnumStatus.FIXING.toString());
            requestRepository.updateRequest(request1);
            try{

            }catch (Exception ex){
                simpMessagingTemplate.convertAndSend("/data/request/"+account.getCode(),"new request assgin");
            }
        }
    }

    @Override
    public void finishRequestByFixer(Request request1) {
        request1.setStatus(EnumStatus.FINISHED.toString());
        requestRepository.updateRequest(request1);

        try{
            Account account = accountService.findAccountTCHC();
            simpMessagingTemplate.convertAndSend("/data/request/"+account.getCode(),"new request finish");
        }catch (Exception ex) {

        }
    }

    @Override
    public List<Request> findRequestFinshed() {
        return requestRepository.findAllRequestFinished();
    }

    //Gửi thông tin thay đổi data đến đích đến sắp tới
    //status: nếu =1 thì request approved, còn lại là reject
    private void sendSocketMessage(Request requestHandled,Integer status,String message, Integer codeID){
        if(status==1){ //gui tin nhan toi nguoi pending
            String role = handleStatus.getRoleByStatusRequest(requestHandled.getStatus());

            boolean flagSend = true;
            HashMap<String,String> users = UserStorage.getInstance().getUsers();

            for (Map.Entry<String, String> entry : users.entrySet()) {
                flagSend = true;
                if(entry.getValue().equals((role))){

                    //Kiem tra chung phong neu la manager
                    if(role.equals(EnumRole.MANAGER.toString())){
                        Account account = accountService.findByCode(entry.getKey());
                        Account accountCreateRequest = accountService.findByCode(requestHandled.getCreatedBy());

                        if(account.getDepartment().getId() != accountCreateRequest.getDepartment().getId()){
                            flagSend = false;
                        }
                    }

                    if(flagSend){
                        simpMessagingTemplate.convertAndSend("/data/request/"+entry.getKey(),message);
                    }
                }
            }
        }
        else {
            simpMessagingTemplate.convertAndSend("/data/request/"+requestHandled.getCreatedBy(),message);
        }

    }
}
