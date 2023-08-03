package com.sunbase.Assignment.service;

import com.sunbase.Assignment.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.authentication.url}")
    private String authenticationUrl;

    @Value("${api.customer.url}")
    private String customerUrl;

//    @Value("${api.login.id}")
//    private String loginId;
//
//    @Value("${api.password}")
//    private String password;

    public String authenticateUser(String loginId, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{ \"login_id\" : \"" + loginId + "\", \"password\" : \"" + password + "\" }";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(authenticationUrl, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().value() == 200) {
            return response.getBody();
        } else {
            throw new RuntimeException("Authentication failed.");
        }
    }

    public ResponseEntity<String> createNewCustomer(String bearerToken, Customer customer) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

//            String requestBody = "{ " +
////                "\"cmd\": \"create\"," +
//                    "\"first_name\": \"" + customer.getFirstName() + "\"," +
//                    "\"last_name\": \"" + customer.getLastName() + "\"," +
//                    "\"street\": \"" + customer.getStreet() + "\"," +
//                    "\"address\": \"" + customer.getAddress() + "\"," +
//                    "\"city\": \"" + customer.getCity() + "\"," +
//                    "\"state\": \"" + customer.getState() + "\"," +
//                    "\"email\": \"" + customer.getEmail() + "\"," +
//                    "\"phone\": \"" + customer.getPhone() + "\"" +
//                    "}";

            HttpEntity<Customer> entity = new HttpEntity<>(customer, headers);

            return restTemplate.exchange(customerUrl + "?cmd=create", HttpMethod.POST, entity, String.class);
        }
        catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //old method

//    public ResponseEntity<String> getCustomerList(String bearerToken) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(bearerToken);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        return restTemplate.exchange(customerUrl + "?cmd=get_customer_list", HttpMethod.GET, entity, String.class);
//    }


    public ResponseEntity<List<Customer>> getCustomerList(String bearerToken) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Customer[]> response = restTemplate.exchange(customerUrl + "?cmd=get_customer_list", HttpMethod.GET, entity, Customer[].class);

            System.out.println("this is res in service" + response);

            // Convert the array of customers to a list and return it in the response
            return ResponseEntity.ok(Arrays.asList(Objects.requireNonNull(response.getBody())));
        }
        catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<String> deleteCustomer(String bearerToken, String customerUuid) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

//            String requestBody = "{ \"cmd\": \"delete\", \"uuid\": \"" + customerUuid + "\" }";

            HttpEntity<String> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(customerUrl + "?cmd=delete&uuid=" + customerUuid, HttpMethod.POST, entity, String.class);
        }

        catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<String> updateCustomer(String bearerToken, Customer customer) {
        try {


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

//            String requestBody = "{ " +
////                "\"cmd\": \"update\"," +
////                "\"uuid\": \"" + customer.getUuid() + "\"," +
//                    "\"first_name\": \"" + customer.getFirstName() + "\"," +
//                    "\"last_name\": \"" + customer.getLastName() + "\"," +
//                    "\"street\": \"" + customer.getStreet() + "\"," +
//                    "\"address\": \"" + customer.getAddress() + "\"," +
//                    "\"city\": \"" + customer.getCity() + "\"," +
//                    "\"state\": \"" + customer.getState() + "\"," +
//                    "\"email\": \"" + customer.getEmail() + "\"," +
//                    "\"phone\": \"" + customer.getPhone() + "\"" +
//                    "}";

            HttpEntity<Customer> entity = new HttpEntity<>(customer, headers);

            return restTemplate.exchange(customerUrl + "?cmd=update&uuid=" + customer.getUuid(), HttpMethod.POST, entity, String.class);
        }
        catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //get customer by customer uuid
    public Customer getCustomerByUuid(String token, String uuid){
        ResponseEntity<List<Customer>> response = getCustomerList(token);

        List<Customer> customerList = response.getBody();
        Customer customer = null;

        if(customerList != null && !customerList.isEmpty())
            customer = customerList.stream().filter(e -> e.getUuid().equals(uuid)).toList().get(0);
        return customer;
    }
}

