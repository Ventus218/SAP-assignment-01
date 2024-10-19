package sap.ass01.solution.backend.layered.businesslogic;

import sap.ass01.solution.backend.layered.businesslogic.exceptions.FatalErrorException;
import sap.ass01.solution.backend.layered.businesslogic.exceptions.NotFoundException;
import sap.ass01.solution.backend.layered.businesslogic.model.*;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.UpdateEBikeDTO;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.UpdateUserDTO;

public class RidesSimulator extends Thread {

    private final BusinessLogic businessLogic;
    private final long tickMillis;

    public RidesSimulator(BusinessLogic businessLogic, long tickMillis) {
        this.businessLogic = businessLogic;
        this.tickMillis = tickMillis;
    }

    @Override
    public void run() {
        var lastTimeDecreasedCredit = System.currentTimeMillis();
        var lastTimeChangedDir = System.currentTimeMillis();
        while (true) {
            // Making the whole simulation step atomic
            businessLogic.startTransaction();

            var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
            var elapsedTimeSinceLastDecredit = System.currentTimeMillis() - lastTimeDecreasedCredit;

            for (Ride r : businessLogic.getRides().stream().filter(r -> r.endDate().isEmpty()).toList()) {
                var b = businessLogic.getEBike(r.ebikeId())
                        .orElseThrow(() -> new FatalErrorException("While simulating ride " + r.id().id()
                                + " looked like rided bike " + r.ebikeId().id() + " was not persisted"));

                var l = b.loc();
                var d = b.direction();
                var s = b.speed();
                if (s == 0) {
                    s = 1;
                }
                var battery = b.batteryLevel(); // TODO: now battery does not change

                l = l.sum(d.mul(s));
                if (l.x() > 200 || l.x() < -200) {
                    d = new V2d(-d.x(), d.y());
                    if (l.x() > 200) {
                        l = new P2d(200, l.y());
                    } else {
                        l = new P2d(-200, l.y());
                    }
                }
                if (l.y() > 200 || l.y() < -200) {
                    d = new V2d(d.x(), -d.y());
                    if (l.y() > 200) {
                        l = new P2d(l.x(), 200);
                    } else {
                        l = new P2d(l.x(), -200);
                    }
                }

                /* change dir randomly */

                if (elapsedTimeSinceLastChangeDir > 500) {
                    double angle = Math.random() * 60 - 30;
                    d = (d.rotate(angle));
                    elapsedTimeSinceLastChangeDir = System.currentTimeMillis();
                }

                /* compute new credit */

                var user = businessLogic.getUser(r.userId())
                        .orElseThrow(() -> new FatalErrorException("While simulating ride " + r.id().id()
                                + " looked like riding user " + r.userId().id() + " was not persisted"));
                var credit = user.credit();
                if (elapsedTimeSinceLastDecredit > 1000) {
                    credit -= 1;
                    lastTimeDecreasedCredit = System.currentTimeMillis();
                }

                /* persist changes */
                try {
                    businessLogic.updateEBike(b.id(), new UpdateEBikeDTO(l, d, s, battery));
                    businessLogic.updateUser(user.id(), new UpdateUserDTO(credit));
                } catch (NotFoundException e) {
                    throw new FatalErrorException(
                            "Unexpectedly could not find user or ebike for updating it with simulation data");
                }
            }
            businessLogic.endTransaction();
            try {
                Thread.sleep(tickMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
