package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.affair.Affair;

public interface AffairStorePort {
    void upsert(Affair affair);
}