package business.services;

import auction.business.services.AuctionServices;
import auction.config.DatabaseConnection;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.persistence.AuctionPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceIntegrationTest {

    @InjectMocks
    private AuctionServices auctionServices;

    private AuctionPersistence auctionPersistence;

    @BeforeEach
    public void setUp() throws SQLException {
        auctionPersistence = new AuctionPersistence();
        auctionServices = new AuctionServices(auctionPersistence);
    }

    @AfterEach
    public void tearDown() throws SQLException{
        DatabaseConnection.getConnection().prepareStatement("delete from auctions").execute();
    }

    @Test
    public void testCreateAuction() {
        String auctionName = "Auction Test";
        double initValue = 100.0;

        Auction auction = auctionServices.create(auctionName, initValue);

        assertNotNull(auction);
        assertEquals(auctionName, auction.getName());
        assertEquals(initValue, auction.getInitValue());
        assertEquals(AuctionState.INACTIVE, auction.getState());

        List<Auction> auctionsList = auctionPersistence.getAuctionsList();
        Auction auctionResult = auctionsList.stream()
                .filter(item -> item.getCode().equals(auction.getCode()))
                .findFirst()
                .orElse(null);

        assertNotNull(auctionResult);
        assertNotNull(auctionResult.getId());
        assertTrue(auctionResult.getId() > 0);
        assertEquals(auction.getName(), auctionResult.getName());
        assertEquals(auction.getState(), auctionResult.getState());
        assertEquals(auction.getInitValue(), auctionResult.getInitValue());
        assertEquals(auction.getFinalValue(), auctionResult.getFinalValue());
    }

    @Test
    public void testFindAuctionByCode() {
        String auctionName = "Auction Test";
        double initValue = 100.0;

        Auction auction = auctionServices.create(auctionName, initValue);

        Optional<Auction> auctionResult = auctionPersistence.findByCode(auction.getCode());

        assertTrue(auctionResult.isPresent(), "Expected auction to be present");

        Auction foundAuction = auctionResult.get();

        assertNotNull(foundAuction.getId(), "Auction ID should not be null");
        assertTrue(foundAuction.getId() > 0, "Auction ID should be greater than zero");
        assertEquals(auction.getName(), foundAuction.getName(), "Auction names should match");
        assertEquals(auction.getState(), foundAuction.getState(), "Auction states should match");
        assertEquals(auction.getInitValue(), foundAuction.getInitValue(), "Initial values should match");
        assertEquals(auction.getFinalValue(), foundAuction.getFinalValue(), "Final values should match");
    }
}
