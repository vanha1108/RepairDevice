package com.company.controller;

import com.company.configs.AccountUserDetail;
import com.company.constant.EnumRole;
import com.company.constant.EnumStatus;
import com.company.constant.HandleStatus;
import com.company.entities.Account;
import com.company.entities.Department;
import com.company.entities.Request;
import com.company.service.IAccountService;
import com.company.service.IDepartmentService;
import com.company.service.IRequestService;
import com.company.storage.UserStorage;
import com.lowagie.text.DocumentException;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    private IRequestService requestService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IDepartmentService departmentService;

    @Autowired
    private HandleStatus handleStatus;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/not-handled")
    public ResponseEntity<List<Request>> getAllOwnRequest() throws Exception {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(requestService.findAllRequestNotHandle(account),HttpStatus.OK);
    }

    @GetMapping("/handled")
    public ResponseEntity<List<Request>> getAllOwnRequestHandled() throws Exception {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(requestService.findAllRequestNotHandle(account),HttpStatus.OK);
    }

    @GetMapping("/export-pdf/{code}")
    public ResponseEntity exportRequestToPdf(@Valid @PathVariable("code") String code) throws IOException, DocumentException {
        Request requestSource = requestService.findRequestByCode(code);
        if(requestSource != null){
            requestService.exportPdf(requestSource);
        }
        return new ResponseEntity("done", HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Request> addRequest(@RequestBody Request request) throws Exception {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Request requestAdded = requestService.addRequestFixDivice(account,request);


        //send socket to client
       /* HashMap<String,String> users = UserStorage.getInstance().getUsers();
        users.forEach((key,value) ->{
            if(value.equals(EnumRole.MANAGER.toString())){
                Account accountManager = accountService.findByCode(key);
                if(accountManager != null){
                    if(accountManager.getDepartment().getId()==account.getDepartment().getId())
                    {
                        simpMessagingTemplate.convertAndSend("/data/request/"+key,"new request");
                    }
                }
            }
        });*/

        return new ResponseEntity<>(requestAdded, HttpStatus.CREATED);
    }

    @PutMapping("/{code}")
    public ResponseEntity<Request> updateRequest(@Valid @PathVariable("code") String code, @RequestBody Request request){
        Request requestSource = requestService.findRequestByCode(code);
        if(requestSource != null){
            AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if((requestSource.getCreatedBy().equals(account.getAccountCode())) &&((requestSource.getStatus().equals(EnumStatus.WAIT_MANAGER.toString())) || (requestSource.getStatus().equals(EnumStatus.FAILED.toString())))) {
                requestService.updateRequest(account,requestSource,request);
                return new ResponseEntity<>(requestSource,HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity("Not found",HttpStatus.NOT_FOUND);
    }
    @PutMapping("/reject/{code}")
    public ResponseEntity rejectRequest(@Valid @PathVariable("code") String code){
        Request requestSource = requestService.findRequestByCode(code);
        if(requestSource != null){
            AccountUserDetail accountLogin = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(accountLogin.getRole().equals(EnumRole.DIRECTOR.toString())){
                if(requestSource.getStatus().equals(handleStatus.getStatusBeforeHandle(accountLogin.getRole()))){
                    requestService.rejectRequest(accountLogin,requestSource);
                    return new ResponseEntity<>("rejected",HttpStatus.OK);
                }
            }
            if(accountLogin.getAccount().getRoles().equals(EnumRole.MANAGER.toString())){
                if(requestSource.getStatus().equals(handleStatus.getStatusBeforeHandle(accountLogin.getRole()))){
                    Account account = accountService.findByCode(requestSource.getCreatedBy());
                    if(accountLogin.getDepartment().getId() == account.getDepartment().getId()){
                        requestService.rejectRequest(accountLogin,requestSource);
                        return new ResponseEntity<>("rejected",HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity("Not found",HttpStatus.NOT_FOUND);
    }

    @PutMapping("/approve/{code}")
    public  ResponseEntity approveRequest(@Valid @PathVariable("code") String code){
        Request requestSource = requestService.findRequestByCode(code);
        if(requestSource != null){
            AccountUserDetail accountLogin = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(requestSource.getStatus().equals(handleStatus.getStatusBeforeHandle(accountLogin.getRole()))){
                if(accountLogin.getRole().equals(EnumRole.MANAGER.toString())){
                    Account account = accountService.findByCode(requestSource.getCreatedBy());
                    if(accountLogin.getDepartment().getId() == account.getDepartment().getId()){
                        requestService.approveRequest(accountLogin,requestSource);
                        return new ResponseEntity("approved",HttpStatus.OK);
                    }
                    else {
                        return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
                    }
                }
                requestService.approveRequest(accountLogin,requestSource);
                return new ResponseEntity("approved",HttpStatus.OK);
            }
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity("Not found",HttpStatus.NOT_FOUND);
    }

    @PutMapping("/finish-tchc/{code}")
    public ResponseEntity tchcFinishRequest(@Valid @PathVariable("code") String code){
        Request requestSource = requestService.findRequestByCode(code);
        if(requestSource != null){
            AccountUserDetail accountLogin = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(accountLogin.getRole().equals(EnumRole.TCHC.toString()) && requestSource.getStatus().equals(EnumStatus.FIXING.toString())){
                requestService.finishRequest(accountLogin,requestSource);
                return new ResponseEntity("finished",HttpStatus.OK);
            }
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity("Not found",HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/{code}")
    public ResponseEntity deleteRequestByCode(@PathVariable String code){
        Request requestSource = requestService.findRequestByCode(code);
        if(requestSource != null){
            AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if((requestSource.getCreatedBy().equals(account.getAccountCode())) &&((requestSource.getStatus().equals(EnumStatus.WAIT_MANAGER.toString())) || (requestSource.getStatus().equals(EnumStatus.FAILED.toString())))) {
                requestService.deleteRequest(requestSource);
                return new ResponseEntity("deleted",HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity("Not found",HttpStatus.NOT_FOUND);
    }

    //Tchc assgin request for Department to fix request
    //Params: department code, request code
    @PostMapping("/tchc/assign-request")
    public ResponseEntity assignRequestForDepartment(@RequestParam String department,@RequestParam String request){
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(EnumRole.TCHC.toString().equals(account.getRole())){
            Department department1 = departmentService.findByCode(department);
            Request request1 = requestService.findRequestByCode(request);
            if(department1 == null || department1.getType() != -1){
                return new ResponseEntity("Wrong code department", HttpStatus.BAD_REQUEST);
            }
            else if(request1==null){
                return new ResponseEntity("Wrong code request", HttpStatus.BAD_REQUEST);
            }
            else {
                requestService.assignRequestForDepartment(department1, request);
                return new ResponseEntity("success",HttpStatus.OK);
            }
        }else {
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
    }

    //Fixer complete fix request and finish request
    //Params: request code
    @PostMapping("/fixer-finish-request")
    public ResponseEntity fixerFinishReques(@RequestParam String request)
    {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(EnumRole.FIXER.toString().equals(account.getRole())){
            Request request1 = requestService.findRequestByCode(request);
            if(request1 == null){
                return new ResponseEntity("Wrong code request", HttpStatus.BAD_REQUEST);
            }
            else {
                requestService.finishRequestByFixer(request1);
                return new ResponseEntity("success", HttpStatus.OK);
            }
        }
        else {
            return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
        }
    }

    //Get all request finish
    @GetMapping("/get-all-request-finish")
    public ResponseEntity<List<Request>> getAllRequestFinish() {
        AccountUserDetail account = (AccountUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (EnumRole.TCHC.toString().equals(account.getRole())) {
            return new ResponseEntity<List<Request>> (requestService.findRequestFinshed(),HttpStatus.OK);
        } else {
            return new ResponseEntity("Permission denied", HttpStatus.UNAUTHORIZED);
        }
    }

    //Get all request need to fix
    //Role: fixer
    @GetMapping("/get-all-request-fixing")
    public ResponseEntity<List<Request>> getAllRequestFixing(){
        AccountUserDetail account = (AccountUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (EnumRole.FIXER.toString().equals(account.getRole())) {
            return new ResponseEntity<> (requestService.findRequestFixing(account.getDepartment().getCode()),HttpStatus.OK);
        } else {
            return new ResponseEntity("Permission denied", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/get-all-request-faild-by-employee")
    public ResponseEntity<List<Request>> getAllRequestFail(){
        AccountUserDetail account = (AccountUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (EnumRole.EMPLOYEE.toString().equals(account.getRole())) {
            return new ResponseEntity<> (requestService.findRequestFaild(account.getDepartment().getCode()),HttpStatus.OK);
        } else {
            return new ResponseEntity("Permission denied", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/resend/{code}")
    public ResponseEntity resendRequestToManager(@PathVariable("code") String code){
        Request request = requestService.findRequestByCode(code);
        if(EnumStatus.FAILED.toString().equals(request.getStatus())){
            return new ResponseEntity(requestService.resendRequestToManager(request),HttpStatus.OK);
        }
        else{
            return new ResponseEntity("This request not faild type", HttpStatus.BAD_REQUEST);
        }
    }
}
