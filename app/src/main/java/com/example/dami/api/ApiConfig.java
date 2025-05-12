package com.example.dami.api;

public class ApiConfig {
    // Base URL for the API
    public static final String BASE_URL_Donation_History = "http://10.0.2.2:8081/";
    public static final String BASE_URL_Position_Tracking = "http://10.0.2.2:8082/";
    public static final String BASE_URL_Banque = "http://10.0.2.2:8083/";
    public static final String BASE_URL_Request_Post = "http://10.0.2.2:8084/";
    
    // For Android Emulator - make sure there's no trailing slash
    public static final String AUTH_BASE_URL = "http://10.0.2.2:8080";
    
    // For physical device
    // public static final String AUTH_BASE_URL = "http://192.168.1.X:8080/"; // Replace X with your computer's IP
} 