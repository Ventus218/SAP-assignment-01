package sap.ass01.solution.frontend.admin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;
import sap.ass01.solution.frontend.model.*;
import sap.ass01.solution.frontend.model.dto.*;
import sap.ass01.solution.frontend.utils.Result;

public class AdminControlPanelViewModel {

    private final HTTPAPIs api;
    private Collection<EBike> bikes = new ArrayList<>();
    private Collection<User> users = new ArrayList<>();
    private Collection<Ride> rides = new ArrayList<>();

    public AdminControlPanelViewModel(HTTPAPIs api) {
        this.api = api;
    }

    /* EBikes */

    void fetchBikes(Consumer<Result<Iterable<EBike>, Throwable>> handler) {
        api.getEBikes(res -> {
            res.handle(bikes -> {
                this.bikes = StreamSupport.stream(bikes.spliterator(), true).collect(Collectors.toList());
                handler.accept(Result.success(bikes));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    void createEBike(CreateEBikeDTO dto, Consumer<Result<EBike, Throwable>> handler) {
        api.createEBike(dto, handler);
    }

    /* Users */

    void fetchUsers(Consumer<Result<Iterable<User>, Throwable>> handler) {
        api.getUsers(res -> {
            res.handle(users -> {
                this.users = StreamSupport.stream(users.spliterator(), true).collect(Collectors.toList());
                handler.accept(Result.success(users));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    /* Rides */

    void fetchRides(Consumer<Result<Iterable<Ride>, Throwable>> handler) {
        api.getRides(res -> {
            res.handle(rides -> {
                this.rides = StreamSupport.stream(rides.spliterator(), true).collect(Collectors.toList());
                handler.accept(Result.success(rides));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    public Iterable<EBike> getBikes() {
        return Collections.unmodifiableCollection(bikes);
    }

    public Iterable<User> getUsers() {
        return Collections.unmodifiableCollection(users);
    }

    public Iterable<Ride> getRides() {
        return Collections.unmodifiableCollection(rides);
    }
}
