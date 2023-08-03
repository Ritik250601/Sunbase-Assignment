package com.sunbase.Assignment.controller;


import com.sunbase.Assignment.service.CustomerService;
import com.sunbase.Assignment.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

//    @GetMapping("/getCustomerList")
//    @CrossOrigin
//    public ResponseEntity<String> getCustomerList(@RequestHeader("Authorization") String authorizationHeader) {
//        // Extract the bearer token from the Authorization header
//        System.out.println("----Get CustometrList get Called-----");
//        String bearerToken = authorizationHeader.replace("Bearer", "");
//
//        ResponseEntity<String> customerListResponse = customerService.getCustomerList(bearerToken);
//        return customerListResponse;
//    }

//    @PostMapping("/createCustomer")
//    public ResponseEntity<String> createCustomer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Customer customer) {
//        // Extract the bearer token from the Authorization header
//        String bearerToken = authorizationHeader.replace("Bearer ", "");
//
//        ResponseEntity<String> createCustomerResponse = customerService.createNewCustomer(bearerToken, customer.getFirstName(), customer.getLastName(), customer.getStreet(), customer.getAddress(), customer.getCity(), customer.getState(), customer.getEmail(), customer.getPhone());
//        return createCustomerResponse;
//    }

    @PostMapping("/deleteCustomer/{uuid}")
    public ResponseEntity<String> deleteCustomer(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String uuid) {
        // Extract the bearer token from the Authorization header
        String bearerToken = authorizationHeader.replace("Bearer ", "");

        ResponseEntity<String> deleteCustomerResponse = customerService.deleteCustomer(bearerToken, uuid);
        return deleteCustomerResponse;
    }

//    @PostMapping("/updateCustomer/{uuid}")
//    public ResponseEntity<String> updateCustomer(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String uuid, @RequestBody Customer customer) {
//        // Extract the bearer token from the Authorization header
//        String bearerToken = authorizationHeader.replace("Bearer ", "");
//
//        ResponseEntity<String> updateCustomerResponse = customerService.updateCustomer(bearerToken, uuid, customer.getFirstName(), customer.getLastName(), customer.getStreet(), customer.getAddress(), customer.getCity(), customer.getState(), customer.getEmail(), customer.getPhone());
//        return updateCustomerResponse;
//    }
}
