package com.company.controller;

import com.company.configs.AccountUserDetail;
import com.company.configs.JwtTokenProvider;
import com.company.entities.Account;
import com.company.service.IAccountService;
import com.company.storage.UserStorage;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 3:38 PM 4/28/2021
 */
@RestController
public class AccountController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity loginAccount(@Valid @RequestBody Account account){
        // Xác thực từ username và password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        account.getUsername(),
                        account.getPassword()
                )
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((AccountUserDetail) authentication.getPrincipal());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer "+jwt);

        AccountUserDetail accountLogin = (AccountUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserStorage.getInstance().setUsers(accountLogin.getAccountCode(),accountLogin.getRole());

        return new ResponseEntity(accountLogin,httpHeaders,HttpStatus.OK);
    }

    @PostMapping("/log-out")
    public  ResponseEntity logOut(){
        AccountUserDetail accountLogin = (AccountUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserStorage.getInstance().removeUser(accountLogin.getAccountCode());
        return new ResponseEntity(HttpStatus.OK);
    }
    @GetMapping("/test-jwt")
    public  ResponseEntity testToken(@RequestHeader String Authorization){
       AccountUserDetail account = (AccountUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       System.out.println("test code:"+account.getAccountCode());
        return new ResponseEntity(""+Authorization,HttpStatus.OK);
    }

    @PostMapping(value = "/account")
    public ResponseEntity<?> insertAccount(@RequestBody Account account) {
        if(GenericValidator.isBlankOrNull(account.getCode().toString())) {
            return new ResponseEntity("Code cannot be null!", HttpStatus.NO_CONTENT);
        }
        Account acc = accountService.findByCode(account.getCode().toString());
        if (acc != null) {
            return new ResponseEntity("Code already exist!", HttpStatus.CONFLICT);
        }
        Account accCheck = accountService.findByUsername(account.getUsername());
        if (accCheck != null) {
            return new ResponseEntity("Username already exist!", HttpStatus.CONFLICT);
        }
        accountService.save(account);
        return new ResponseEntity(account, HttpStatus.OK);
    }

    @PutMapping( value = "/account/{code}")
    public ResponseEntity<?> updateAccount(@RequestBody Account account, @PathVariable("code") String code) {
        Account acc = accountService.findByCode(code.toString());
        if (acc == null) {
            return new ResponseEntity("Account not found!", HttpStatus.NOT_FOUND);
        }
        Account accCheck = accountService.findByUsername(account.getUsername());
        if(accCheck != null && !accCheck.getCode().equals(code)) {
            return  new ResponseEntity<>("Username already exist!", HttpStatus.CONFLICT);
        }
        account.setId(acc.getId());
        account.setCode(code);
        accountService.update(account);
        return new ResponseEntity(account, HttpStatus.OK);
    }

    @DeleteMapping("/account/{code}")
    public ResponseEntity<?> delete(@PathVariable("code") String code) {
        if(accountService.delete(code)) {
            return new ResponseEntity<>(code, HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account/{code}")
    public ResponseEntity<?> getAccountByCode(@PathVariable("code") String code) {
        Account account = accountService.findByCode(code);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAllAccount() {
        List<Account> accounts = accountService.findAll();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
}
