package hello.service;

import javax.inject.Inject;

public class OrderService {
    private UserService userService;

    @Inject
    public OrderService(UserService userService) {
        this.userService = userService;
    }

//    public void placeOrder(Integer id, String item) {
//        userService.getUserById(id);
//    }
}
