package sap.ass01.solution.backend.hexagonal.adapters.presentation;

import java.util.Optional;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import sap.ass01.solution.backend.hexagonal.domain.Domain;
import sap.ass01.solution.backend.hexagonal.domain.exceptions.*;
import sap.ass01.solution.backend.hexagonal.domain.model.*;
import sap.ass01.solution.backend.hexagonal.domain.model.dto.*;

public class HTTPPresentationAdapter extends AbstractVerticle {

    private final Domain domain;

    public HTTPPresentationAdapter(Domain domain) {
        this.domain = domain;
    }

    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        registerEBikeHandlers(router);
        registerUserHandlers(router);
        registerRideHandlers(router);

        router.getRoutes().forEach(r -> r.failureHandler(ctx -> {
            var err = ctx.failure();
            var msg = Optional.ofNullable(err.getMessage()).orElse("Errore sconosciuto: " + err.toString());
            if (err instanceof OperationFailedException) {
                ctx.response().setStatusCode(500).end(msg);
            } else if (err instanceof NotFoundException) {
                ctx.response().setStatusCode(404).end(msg);
            } else if (err instanceof IllegalArgumentException) {
                ctx.response().setStatusCode(400).end(msg);
            } else {
                ctx.response().setStatusCode(500).end(msg);
            }
        }));

        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, res -> {
                    if (res.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(res.cause());
                    }
                });
    }

    private void registerEBikeHandlers(Router router) {
        String route = "/ebikes";
        router.get(route).respond(ctx -> {
            var bikes = domain.getEBikes();
            return Future.succeededFuture(new JsonArray(bikes.stream().toList()));
        });
        router.post(route).handler(BodyHandler.create()).respond(ctx -> {
            var json = Optional.ofNullable(ctx.body().asJsonObject()).orElseThrow(() -> {
                return new IllegalArgumentException("Expected a CreateEBikeDTO");
            });
            var dto = json.mapTo(CreateEBikeDTO.class);
            return Future.succeededFuture(domain.createEBike(dto));
        });
        router.get(route + "/:id").respond(ctx -> {
            var bikeId = new EBikeId(ctx.pathParam("id"));
            var bike = domain.getEBike(bikeId);
            return Future.succeededFuture(JsonObject.mapFrom(bike.orElseThrow(() -> new NotFoundException())));
        });
        router.delete(route + "/:id").respond(ctx -> {
            var bikeId = new EBikeId(ctx.pathParam("id"));
            domain.deleteEBike(bikeId);
            return Future.succeededFuture();
        });
        router.patch(route + "/:id").handler(BodyHandler.create()).respond(ctx -> {
            var bikeId = new EBikeId(ctx.pathParam("id"));
            var json = Optional.ofNullable(ctx.body().asJsonObject()).orElseThrow(() -> {
                return new IllegalArgumentException("Expected a UpdateEBikeDTO");
            });
            var dto = json.mapTo(UpdateEBikeDTO.class);
            var bike = domain.updateEBike(bikeId, dto);
            return Future.succeededFuture(JsonObject.mapFrom(bike));
        });
    }

    private void registerUserHandlers(Router router) {
        String route = "/users";
        router.get(route).respond(ctx -> {
            var users = domain.getUsers();
            return Future.succeededFuture(new JsonArray(users.stream().toList()));
        });
        // signup
        router.post(route).handler(BodyHandler.create()).respond(ctx -> {
            var json = Optional.ofNullable(ctx.body().asJsonObject()).orElseThrow(() -> {
                return new IllegalArgumentException("Expected a CreateUserDTO");
            });
            var dto = json.mapTo(CreateUserDTO.class);
            return Future.succeededFuture(domain.signup(dto));
        });
        router.get(route + "/login/:id").respond(ctx -> {
            var userId = new UserId(ctx.pathParam("id"));
            var user = domain.getUser(userId);
            return Future.succeededFuture(JsonObject.mapFrom(user.orElseThrow(() -> new NotFoundException())));
        });
        router.get(route + "/:id").respond(ctx -> {
            var userId = new UserId(ctx.pathParam("id"));
            var user = domain.getUser(userId);
            return Future.succeededFuture(JsonObject.mapFrom(user.orElseThrow(() -> new NotFoundException())));
        });
        router.patch(route + "/:id").handler(BodyHandler.create()).respond(ctx -> {
            var userId = new UserId(ctx.pathParam("id"));
            var json = Optional.ofNullable(ctx.body().asJsonObject()).orElseThrow(() -> {
                return new IllegalArgumentException();
            });
            var dto = json.mapTo(UpdateUserDTO.class);
            var user = domain.updateUser(userId, dto);
            return Future.succeededFuture(JsonObject.mapFrom(user));
        });
    }

    private void registerRideHandlers(Router router) {
        String route = "/rides";
        router.get(route).respond(ctx -> {
            var rides = domain.getRides();
            return Future.succeededFuture(new JsonArray(rides.stream().toList()));
        });
        router.post(route).handler(BodyHandler.create()).respond(ctx -> {
            var json = Optional.ofNullable(ctx.body().asJsonObject()).orElseThrow(() -> {
                return new IllegalArgumentException("Expected a StartRideDTO");
            });
            var dto = json.mapTo(StartRideDTO.class);
            try {
                return Future.succeededFuture(domain.startRide(dto));
            } catch (UserAlreadyOnRideException | EBikeAlreadyOnRideException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        });
        router.get(route + "/:id").respond(ctx -> {
            var rideId = new RideId(ctx.pathParam("id"));
            var ride = domain.getRide(rideId);
            return Future.succeededFuture(JsonObject.mapFrom(ride.orElseThrow(() -> new NotFoundException())));
        });

        // End ride
        router.patch(route + "/:id").respond(ctx -> {
            var rideId = new RideId(ctx.pathParam("id"));
            try {
                return Future.succeededFuture(JsonObject.mapFrom(domain.endRide(rideId)));
            } catch (RideAlreadyEndedException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        });
    }
}
