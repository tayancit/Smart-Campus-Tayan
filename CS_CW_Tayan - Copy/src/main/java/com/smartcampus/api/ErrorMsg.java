/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

/**
 *
 * @author tayan
 */
public class ErrorMsg{
    private int status;
    private String code;
    private String message;

    public ErrorMsg(){
    }
    public ErrorMsg(int status,String code,String message){
        this.status=status;
        this.code=code;
        this.message=message;
    }
    public int getStatus(){return status;}
    public void setStatus(int status){this.status = status;}

    public String getCode(){return code;}
    public void setCode(String code){this.code = code;}

    public String getMessage(){return message;}
    public void setMessage(String message){this.message = message;}
}