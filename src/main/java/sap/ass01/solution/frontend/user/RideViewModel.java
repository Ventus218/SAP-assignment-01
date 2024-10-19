package sap.ass01.solution.frontend.user;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;
import sap.ass01.solution.frontend.model.*;
import sap.ass01.solution.frontend.model.dto.StartRideDTO;
import sap.ass01.solution.frontend.utils.Result;

public class RideViewModel {

    private final HTTPAPIs api;
    private final UserId userId;
    private List<EBike> availableBikes = new ArrayList<>();
    private Optional<Ride> currentRide = Optional.empty();
    private Optional<Integer> selectedBikeIndex = Optional.empty();

    public RideViewModel(HTTPAPIs api, UserId userId) {
        this.api = api;
        this.userId = userId;
    }

    void fetchAvailableBikes(Consumer<Result<Iterable<EBike>, Throwable>> handler) {
        api.getEBikes(res -> {
            res.handle(bikes -> {
                this.availableBikes = StreamSupport.stream(bikes.spliterator(), true)
                        .filter(b -> b.state() == EBikeState.AVAILABLE).collect(Collectors.toList());
                handler.accept(Result.success(availableBikes));
            }, err -> {
                handler.accept(Result.failure(err));
            });
        });
    }

    void startRide(Consumer<Result<Ride, Throwable>> handler) {
        selectedBikeIndex.ifPresentOrElse(index -> {
            var bike = availableBikes.get(index);
            api.startRide(new StartRideDTO(userId, bike.id()), res -> res.handle(ride -> {
                this.currentRide = Optional.of(ride);
                this.selectedBikeIndex = Optional.empty();
                handler.accept(Result.success(ride));
            }, err -> handler.accept(Result.failure(err))));
        }, () -> handler.accept(Result.failure(new IllegalStateException("No bike was selected"))));
    }

    void stopRide(Consumer<Result<Ride, Throwable>> handler) {
        if (!isRiding()) {
            handler.accept(Result.failure(new IllegalStateException("Not riding")));
        } else {
            api.endRide(currentRide.get().id(), res -> res.handle(ride -> {
                this.currentRide = Optional.empty();
                handler.accept(Result.success(ride));
            }, err -> handler.accept(Result.failure(err))));
        }
    }

    public List<EBike> getAvailableBikes() {
        return Collections.unmodifiableList(availableBikes);
    }

    public void setSelectBikeIndex(Optional<Integer> selectedIndex) {
        selectedBikeIndex = selectedIndex;
    }

    public Optional<Integer> getSelectBikeIndex() {
        return selectedBikeIndex;
    }

    public boolean isRiding() {
        return getCurrentRide().isPresent();
    }

    public Optional<Ride> getCurrentRide() {
        return currentRide;
    }

}
