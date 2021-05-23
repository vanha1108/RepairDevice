package com.company.controller;

import com.company.configs.AccountUserDetail;
import com.company.constant.EnumRole;
import com.company.constant.EnumStatus;
import com.company.constant.HandleStatus;
import com.company.entities.Account;
import com.company.entities.Request;
import com.company.service.IAccountService;
import com.company.service.IRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    private IRequestService requestService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private HandleStatus handleStatus;

    @GetMapping("/not-handled")
    public ResponseEntity<List<Request>> getAllOwnRequest() throws Exception {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(requestService.findAllRequestByUserLogin(account),HttpStatus.OK);
    }

    @GetMapping("/handled")
    public ResponseEntity<List<Request>> getAllOwnRequestHandled() throws Exception {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(requestService.findAllRequestDoneByUserLogin(account),HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Request> addRequest(@RequestBody Request request) throws Exception {
        AccountUserDetail account = (AccountUserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Request requestAdded = requestService.addRequestFixDivice(account,request);
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
            if(accountLogin.getAccount().equals(EnumRole.MANAGER.toString())){
                if(requestSource.getStatus().equals(handleStatus.getStatusBeforeHandle(accountLogin.getRole()))){
                    Account account = accountService.findByCode(requestSource.getCreatedBy());
                    if(accountLogin.getDepartment().toString().equals(account.getDepartment().toString())){
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
                    if(accountLogin.getDepartment().toString().equals(account.getDepartment().toString())){
                        requestService.approveRequest(accountLogin,requestSource);
                        return new ResponseEntity("approved",HttpStatus.OK);
                    }
                    else {
                        return new ResponseEntity("Permission denied",HttpStatus.UNAUTHORIZED);
                    }
                }
                requestService.approveRequest(accountLogin,requestSource);
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
}
