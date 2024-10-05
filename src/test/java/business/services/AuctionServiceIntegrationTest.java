package business.services;

import auction.business.services.AuctionServices;
import auction.business.services.UserService;
import auction.config.DatabaseConnection;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.persistence.AuctionPersistence;
import auction.data.persistence.BidPersistence;
import auction.data.persistence.UserPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionServiceIntegrationTest {

    private AuctionServices auctionServices;

    private AuctionPersistence auctionPersistence;
    private UserService userService;
    private UserPersistence userPersistence;
    private BidPersistence bidPersistence;

    @BeforeEach
    public void setUp() throws SQLException {
        auctionPersistence = new AuctionPersistence();
        bidPersistence = new BidPersistence();
        auctionServices = new AuctionServices(auctionPersistence, bidPersistence, userPersistence);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        DatabaseConnection.getConnection().prepareStatement("delete from auctions").execute();
    }

    @Test
    public void testCreateAuction() {
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

    @Test
    public void testUpdateAuctionState() {
        String auctionName = "Auction Test";
        double initValue = 100.0;

        Auction auction = auctionServices.create(auctionName, initValue);

        this.auctionServices.open(auction.getCode());

        Optional<Auction> dbAuction = auctionPersistence.findByCode(auction.getCode());

        assertTrue(dbAuction.isPresent(), "Expected auction to be present");

        Auction foundAuction = dbAuction.get();

        assertNotNull(foundAuction.getId(), "Auction ID should not be null");
        assertTrue(foundAuction.getId() > 0, "Auction ID should be greater than zero");
        assertEquals(auction.getName(), foundAuction.getName(), "Auction names should match");
        assertEquals(foundAuction.getState(), AuctionState.OPEN, "Auction states should match");
        assertEquals(auction.getInitValue(), foundAuction.getInitValue(), "Initial values should match");
        assertEquals(auction.getFinalValue(), foundAuction.getFinalValue(), "Final values should match");
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

    @Test
    public void testFindAllAuction() {
        String auctionName = "Auction Test";
        double initValue = 100.0;

        Auction auction = auctionServices.create(auctionName, initValue);
        Auction secondAuction = auctionServices.create(auctionName + "2", initValue);

        List<Auction> auctionList = auctionServices.getAll();
        System.out.println(auctionList);

        assertEquals(2, auctionList.size());

        Auction firstAuction = auctionList.get(0);
        assertEquals(auction.getCode(), firstAuction.getCode());
        assertEquals("Auction Test", firstAuction.getName());
        assertEquals(AuctionState.INACTIVE, firstAuction.getState());
        assertEquals(100.0, firstAuction.getInitValue(), 0.001);
        assertEquals(0.0, firstAuction.getFinalValue(), 0.001);
        assertEquals(0, firstAuction.getBids().size());
        assertNotNull(firstAuction.getEndDate());

        Auction secondAuctionFromList = auctionList.get(1);
        assertEquals(secondAuction.getCode(), secondAuctionFromList.getCode());
        assertEquals("Auction Test2", secondAuctionFromList.getName());
        assertEquals(AuctionState.INACTIVE, secondAuctionFromList.getState());
        assertEquals(100.0, secondAuctionFromList.getInitValue(), 0.001);
        assertEquals(0.0, secondAuctionFromList.getFinalValue(), 0.001);
        assertEquals(0, secondAuctionFromList.getBids().size());
        assertNotNull(secondAuctionFromList.getEndDate());
    }


    @Test
    public void testFindByAuctionState() {
        String auctionName = "Auction Test";
        double initValue = 100.0;

        Auction auction = auctionServices.create(auctionName, initValue);
        Auction secondAuction = auctionServices.create(auctionName + "2", initValue);
        this.auctionServices.open(secondAuction.getCode());
        List<Auction> auctionList = auctionServices.filterByState(AuctionState.OPEN);

        assertEquals(1, auctionList.size());

        Auction auctionFound = auctionList.get(0);
        assertEquals(secondAuction.getCode(), auctionFound.getCode());
        assertEquals("Auction Test2", auctionFound.getName());
        assertEquals(AuctionState.OPEN, auctionFound.getState());
        assertEquals(100.0, auctionFound.getInitValue(), 0.001);
        assertEquals(0.0, auctionFound.getFinalValue(), 0.001);
        assertEquals(0, auctionFound.getBids().size());
        assertNotNull(auctionFound.getEndDate());
    }

}
