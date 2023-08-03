package com.sunbase.Assignment.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbase.Assignment.model.Customer;
import com.sunbase.Assignment.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Controller
public class PageController {


    @Autowired
    private CustomerService customerService;

    String loginRedirect="redirect:/login";


    @RequestMapping("/")
public String firstPage(){
    return "LoginPage";
}


//for login page
    @GetMapping("/login")
    public String loginPage(){
        return "LoginPage";
    }

    //for user login
    @PostMapping("/login")
    public String login(HttpServletRequest request, RedirectAttributes red){

    String loginId = request.getParameter("loginId");
    String password = request.getParameter("password");

if(loginId.trim().isEmpty() ||password.trim().isEmpty()){
    red.addFlashAttribute("errorMsg", "Fields Must Not be Empty");
    return loginRedirect;
}

try {
    String response = customerService.authenticateUser(loginId, password);

    // Create an ObjectMapper instance
    ObjectMapper objectMapper = new ObjectMapper();

    // Convert the string to a JSON
   JsonNode jsonNode = objectMapper.readTree(response);


   String token = jsonNode.get("access_token").textValue();
    //store token to session
    HttpSession session = request.getSession();
    session.setAttribute("access_token", token);

    return "redirect:/customer-list";

}


catch (HttpServerErrorException e){
    e.printStackTrace();
    red.addFlashAttribute("errorMsg", "Login Id or Password is invalid");
}
 catch (Exception e){
    e.printStackTrace();
    red.addFlashAttribute("errorMsg", "Something went wrong");
 }
    return loginRedirect;

    }


    @RequestMapping("/customer-list")
    public String customer(HttpServletRequest request, Model model){

        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("access_token");

        if(token == null){
            return loginRedirect;
        }
        //access token is present so called customerslist function
        ResponseEntity<List<Customer>> response = customerService.getCustomerList(token);
//        ResponseEntity<String> response = customerService.getCustomerList(token);
if(response != null) {
    List<Customer> customerList = response.getBody();

    model.addAttribute("customerList", customerList);
}
    return "CustomersPage";
    }



    @RequestMapping(value = "/update-customer-page/{uuid}", method = RequestMethod.GET)
    public String addUpdateCustomerPage(HttpServletRequest request, Model model, @PathVariable String uuid){
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("access_token");

        if(token == null){
            return loginRedirect;
        }

//        String uuid = request.getParameter("uuid");
        Customer customer;

        if(uuid != null && !uuid.isBlank()){
            customer = customerService.getCustomerByUuid(token, uuid);
            model.addAttribute("customer", customer);
        }

        return "CustomerAddUpdate";
    }


    @RequestMapping(value = "/create-customer-page", method = RequestMethod.GET)
    public String createCustomerPage(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("access_token");

        if(token == null){
            return loginRedirect;
        }

//        String uuid = request.getParameter("uuid");
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "CustomerAddUpdate";
    }


    @RequestMapping(value = "/add-update-customer", method = RequestMethod.POST)
    public String AddUpdateCustomer(HttpServletRequest request, Model model, RedirectAttributes red){
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("access_token");

        if(token == null){
            return loginRedirect;
        }

        String uuid = request.getParameter("uuid");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String street = request.getParameter("street");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        System.out.println("uuid "+ uuid + "firstName " + firstName + "lastName " + lastName + "phone " + phone );


        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setCity(city);
        customer.setState(state);
        customer.setStreet(street);
        customer.setAddress(address);
        customer.setEmail(email);
        customer.setPhone(phone);


        if(uuid != null && !uuid.isBlank()){
            //update scenerio
            customer.setUuid(uuid);
            ResponseEntity<String> response = customerService.updateCustomer(token, customer);
            if(response.getStatusCode().value() == 200){
                red.addFlashAttribute("successMsg", "Successfully Update");
            }
            else{
                red.addFlashAttribute("errorMsg", "Something went wrong");
            }
            return "redirect:/customer-list";



        }
        else{

            if(firstName.isBlank() || lastName.isBlank()) {
                red.addFlashAttribute("errorMsg", "First Name and LastName is Required To create Customer");
                return "redirect:/customer-list";
            }

            ResponseEntity<String> response = customerService.createNewCustomer(token, customer);
            if(response.getStatusCode().value() == 201){
                red.addFlashAttribute("successMsg", "Successfully Created");
            }
            else{
                red.addFlashAttribute("errorMsg", "Something went wrong");
            }
            return "redirect:/customer-list";

        }


    }

    @RequestMapping(value = "/delete-customer/{uuid}", method = RequestMethod.GET)
    public String deleteCustomer(HttpServletRequest request, Model model, @PathVariable String uuid, RedirectAttributes red){
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("access_token");

        if(token == null){
            return loginRedirect;
        }

//        String uuid = request.getParameter("uuid");
        Customer customer;

        if(uuid != null && !uuid.isBlank()){
            ResponseEntity<String> response = customerService.deleteCustomer(token, uuid);
            if(response.getStatusCode().value() == 200){
                red.addFlashAttribute("successMsg", "successfully deleted");
            }
            else{
                red.addFlashAttribute("errorMsg", "Something Went wrong");

            }
        }
        else{
            red.addFlashAttribute("errorMsg", "uuid is required to delete customer");
        }

        return "redirect:/customer-list";
    }




}
