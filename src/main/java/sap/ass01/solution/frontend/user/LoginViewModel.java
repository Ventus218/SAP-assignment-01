package sap.ass01.solution.frontend.user;

import java.util.function.Consumer;

import sap.ass01.solution.frontend.model.*;
import sap.ass01.solution.frontend.model.dto.CreateUserDTO;
import sap.ass01.solution.frontend.utils.Result;

public class LoginViewModel {
    private final HTTPAPIs api;
    private String username = "";

    public LoginViewModel(HTTPAPIs api) {
        this.api = api;
    }

    void signup(Consumer<Result<User, Throwable>> handler) {
        api.signup(new CreateUserDTO(new UserId(username)), handler);
    }

    void login(Consumer<Result<User, Throwable>> handler) {
        api.login(new UserId(username), handler);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
