package sap.ass01.solution.frontend.model;

import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.client.*;
import java.util.function.Consumer;
import io.vertx.core.json.JsonObject;
import sap.ass01.solution.frontend.model.dto.*;
import sap.ass01.solution.frontend.utils.Result;

public class HTTPAPIsImpl implements HTTPAPIs {

    private final WebClient client;
    private final String BIKES = "/ebikes";
    private final String USERS = "/users";
    private final String RIDES = "/rides";

    public HTTPAPIsImpl(String host, int port) {
        Vertx vertx = Vertx.vertx();
        var clientOptions = new WebClientOptions().setDefaultHost(host).setDefaultPort(port);
        this.client = WebClient.create(vertx, clientOptions);
    }

    private Expectation<HttpResponseHead> expectation() {
        return HttpResponseExpectation.SC_SUCCESS.wrappingFailure((resp, err) -> {
            HttpResponse<?> response = (HttpResponse<?>) resp;
            var msg = response.bodyAsString();
            return new Exception(msg, err);
        });
    }

    @Override
    public void getEBikes(Consumer<Result<Iterable<EBike>, Throwable>> handler) {
        client.get(BIKES)
                .send()
                .expecting(expectation())
                .onSuccess(buf -> {
                    var items = buf.bodyAsJsonArray().stream().map(o -> (JsonObject) o).map(o -> o.mapTo(EBike.class))
                            .toList();
                    handler.accept(Result.success(items));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void createEBike(CreateEBikeDTO dto, Consumer<Result<EBike, Throwable>> handler) {
        client.post(BIKES)
                .sendJsonObject(JsonObject.mapFrom(dto))
                .expecting(expectation())
                .onSuccess(buf -> {
                    var item = buf.bodyAsJsonObject().mapTo(EBike.class);
                    handler.accept(Result.success(item));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void deleteEBike(EBikeId id, Consumer<Result<Void, Throwable>> handler) {
        client.delete(BIKES + "/" + id.id())
                .send()
                .expecting(expectation())
                .onSuccess(buf -> handler.accept(Result.success(null)))
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void getUsers(Consumer<Result<Iterable<User>, Throwable>> handler) {
        client.get(USERS)
                .send()
                .expecting(expectation())
                .onSuccess(buf -> {
                    var items = buf.bodyAsJsonArray().stream().map(o -> (JsonObject) o).map(o -> o.mapTo(User.class))
                            .toList();
                    handler.accept(Result.success(items));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void signup(CreateUserDTO dto, Consumer<Result<User, Throwable>> handler) {
        client.post(USERS)
                .sendJsonObject(JsonObject.mapFrom(dto))
                .expecting(expectation())
                .onSuccess(buf -> {
                    var item = buf.bodyAsJsonObject().mapTo(User.class);
                    handler.accept(Result.success(item));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void login(UserId id, Consumer<Result<User, Throwable>> handler) {
        client.get(USERS + "/" + id.id())
                .send()
                .expecting(expectation())
                .onSuccess(buf -> {
                    var item = buf.bodyAsJsonObject().mapTo(User.class);
                    handler.accept(Result.success(item));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void getRides(Consumer<Result<Iterable<Ride>, Throwable>> handler) {
        client.get(RIDES)
                .send()
                .expecting(expectation())
                .onSuccess(buf -> {
                    var items = buf.bodyAsJsonArray().stream().map(o -> (JsonObject) o).map(o -> o.mapTo(Ride.class))
                            .toList();
                    handler.accept(Result.success(items));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void startRide(StartRideDTO dto, Consumer<Result<Ride, Throwable>> handler) {
        client.post(RIDES)
                .sendJsonObject(JsonObject.mapFrom(dto))
                .expecting(expectation())
                .onSuccess(buf -> {
                    var item = buf.bodyAsJsonObject().mapTo(Ride.class);
                    handler.accept(Result.success(item));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

    @Override
    public void endRide(RideId id, Consumer<Result<Ride, Throwable>> handler) {
        client.patch(RIDES + "/" + id.id())
                .send()
                .expecting(expectation())
                .onSuccess(buf -> {
                    var item = buf.bodyAsJsonObject().mapTo(Ride.class);
                    handler.accept(Result.success(item));
                })
                .onFailure(err -> handler.accept(Result.failure(err)));
    }

}
