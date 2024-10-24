package sap.ass01.solution.frontend.admin;

import java.util.*;
import java.util.stream.*;
import java.util.function.Consumer;
import sap.ass01.solution.frontend.model.*;
import sap.ass01.solution.frontend.model.dto.*;
import sap.ass01.solution.frontend.utils.Result;

public class AdminControlPanelViewModel {

    private final HTTPAPIs api;
    private int requestsInExecution = 0;
    private Collection<EBike> bikes = new ArrayList<>();
    private Collection<User> users = new ArrayList<>();
    private Collection<Ride> rides = new ArrayList<>();
    private Map<String, ButtonPlugin> plugins = new HashMap<>();
    private Collection<AdminControlPanelViewModelListener> listeners = new HashSet<>();

    public AdminControlPanelViewModel(HTTPAPIs api) {
        this.api = api;
    }

    /* EBikes */

    public void fetchBikes(Consumer<Result<Iterable<EBike>, Throwable>> handler) {
        incRequestsInExecution();
        api.getEBikes(res -> {
            decRequestsInExecution();
            res.handle(bikes -> {
                this.bikes = StreamSupport.stream(bikes.spliterator(), true).collect(Collectors.toList());
                updateListeners();
                handler.accept(Result.success(bikes));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    public void createEBike(CreateEBikeDTO dto, Consumer<Result<EBike, Throwable>> handler) {
        incRequestsInExecution();
        api.createEBike(dto, res -> {
            decRequestsInExecution();
            handler.accept(res);
        });
    }

    /* Users */

    public void fetchUsers(Consumer<Result<Iterable<User>, Throwable>> handler) {
        incRequestsInExecution();
        api.getUsers(res -> {
            decRequestsInExecution();
            res.handle(users -> {
                this.users = StreamSupport.stream(users.spliterator(), true).collect(Collectors.toList());
                updateListeners();
                handler.accept(Result.success(users));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    /* Rides */

    public void fetchRides(Consumer<Result<Iterable<Ride>, Throwable>> handler) {
        incRequestsInExecution();
        api.getRides(res -> {
            decRequestsInExecution();
            res.handle(rides -> {
                this.rides = StreamSupport.stream(rides.spliterator(), true).collect(Collectors.toList());
                updateListeners();
                handler.accept(Result.success(rides));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    public void addPlugin(ButtonPlugin p) {
        if (!plugins.containsKey(p.pluginId())) {
            plugins.put(p.pluginId(), p);
        }
        updateListeners();
    }

    private void incRequestsInExecution() {
        requestsInExecution++;
        updateListeners();
    }

    private void decRequestsInExecution() {
        requestsInExecution--;
        updateListeners();
    }

    private void updateListeners() {
        listeners.forEach(l -> l.viewModelChanged());
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

    public int getRequestsInExecution() {
        return requestsInExecution;
    }

    public Iterable<ButtonPlugin> getPlugins() {
        return plugins.values();
    }

    public HTTPAPIs getApi() {
        return api;
    }

    public void addListener(AdminControlPanelViewModelListener l) {
        this.listeners.add(l);
    }

    public void removeListener(AdminControlPanelViewModelListener l) {
        this.listeners.remove(l);
    }
}
