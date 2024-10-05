package business.services;

import auction.business.services.BidServices;
import auction.business.services.UserService;
import auction.config.DatabaseConnection;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.models.Bid;
import auction.data.models.User;
import auction.data.persistence.AuctionPersistence;
import auction.data.persistence.BidPersistence;
import auction.data.persistence.UserPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BidServiceIntegrationTest {

    private BidServices bidServices;
    private UserService userService;
    private UserPersistence userPersistence;
    private BidPersistence bidPersistence;
    private AuctionPersistence auctionPersistence;

    @BeforeEach
    public void setUp() throws SQLException {
        this.userPersistence = new UserPersistence();
        this.bidPersistence = new BidPersistence();
        this.auctionPersistence = new AuctionPersistence();
        this.userService = new UserService(userPersistence);
        this.bidServices = new BidServices(bidPersistence, userPersistence, auctionPersistence);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        DatabaseConnection.getConnection().prepareStatement("DELETE FROM bids").execute();
        DatabaseConnection.getConnection().prepareStatement("DELETE FROM users").execute();
        DatabaseConnection.getConnection().prepareStatement("DELETE FROM auctions").execute();
    }

    @Test
    public void testPlaceBidSuccess() {
        User user = new User("Alice", LocalDateTime.now(), "alice@example.com", "securepassword");
        User userCreated = userService.create(user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate());

        Auction auction = new Auction("Auction001","nome", AuctionState.OPEN, 10.0, LocalDateTime.now().plusHours(2));

        auctionPersistence.create(auction);

        Double bidValue = 150.0;

        this.bidServices.placeBid(auction.getCode(), bidValue, userCreated);

        List<Bid> bids = bidPersistence.getBids();
        assertEquals(1, bids.size(), "There should be 1 bid for the auction");

        Bid placedBid = bids.get(0);

        assertEquals(userCreated.getId(), placedBid.getUserId(), "The bid should belong to the user");
        assertEquals(auction.getId(), placedBid.getAuctionId());
        assertEquals(bidValue, placedBid.getValue(), "The bid value should match");
    }

    @Test
    public void testListBidsForAuction() {
        User user = new User("Alice", LocalDateTime.now(), "alice@example.com", "securepassword");
        User userCreated = userService.create(user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate());

        assertNotNull(userCreated, "The user should have been created successfully");

        Auction auction = new Auction("Auction001", "nome", AuctionState.OPEN, 10.0, LocalDateTime.now().plusHours(2));
        auctionPersistence.create(auction);

        assertNotNull(auction.getId(), "The auction should have been created successfully");

        Double bidValue = 150.0;
        bidServices.placeBid(auction.getCode(), bidValue, userCreated);

        List<Bid> auctionBids = bidPersistence.getBidsByAuctionId(auction.getId());
        assertNotNull(auctionBids, "The bid list should not be null");
        assertEquals(1, auctionBids.size(), "There should be 1 bid for the auction");

        Bid listedBid = auctionBids.get(0);
        assertNotNull(listedBid, "The listed bid should not be null");
        assertEquals(userCreated.getId(), listedBid.getUserId(), "The bid should belong to the correct user");
        assertEquals(auction.getId(), listedBid.getAuctionId(), "The bid should be associated with the correct auction");
        assertEquals(bidValue, listedBid.getValue(), 0.001, "The bid value should match the expected value");
    }

    @Test
    public void testListBidsOrderedDesc() {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            User user = new User("User" + i, LocalDateTime.now().minusYears(20 + i), "user" + i + "@example.com", "securepassword" + i);
            User userCreated = userService.create(user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate());
            users.add(userCreated);

            assertNotNull(userCreated, "User" + i + " should have been created successfully");
            assertEquals("User" + i, userCreated.getName(), "User" + i + "'s name should match the input");
            assertEquals("user" + i + "@example.com", userCreated.getEmail(), "User" + i + "'s email should match the input");
        }


        Auction auction = new Auction("Auction001", "nome", AuctionState.OPEN, 10.0, LocalDateTime.now().plusHours(2));
        auctionPersistence.create(auction);

        for (int i = 0; i < users.size(); i++) {
            bidServices.placeBid(auction.getCode(), 100.0 + i * 10, users.get(i));
        }

        List<Bid> bids = bidServices.getOrderedDescBids(auction.getId());

        assertFalse(bids.isEmpty(), "The bid list should not be null");

        assertEquals(5, bids.size(), "There should be exactly 5 bids for the auction");

        for (int i = 0; i < bids.size() - 1; i++) {
            assertTrue(bids.get(i).getDate().isAfter(bids.get(i + 1).getDate()), "Bids should be ordered in descending order by date");
        }
    }
    @Test
    public void testListBidsOrderedAsc() {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            User user = new User("User" + i, LocalDateTime.now().minusYears(20 + i), "user" + i + "@example.com", "securepassword" + i);
            User userCreated = userService.create(user.getName(), user.getEmail(), user.getPassword(), user.getBirthdate());
            users.add(userCreated);

            assertNotNull(userCreated, "User" + i + " should have been created successfully");
            assertEquals("User" + i, userCreated.getName(), "User" + i + "'s name should match the input");
            assertEquals("user" + i + "@example.com", userCreated.getEmail(), "User" + i + "'s email should match the input");
        }


        Auction auction = new Auction("Auction001", "nome", AuctionState.OPEN, 10.0, LocalDateTime.now().plusHours(2));
        auctionPersistence.create(auction);

        for (int i = 0; i < users.size(); i++) {
            bidServices.placeBid(auction.getCode(), 100.0 + i * 10, users.get(i));
        }

        List<Bid> bids = bidServices.getOrderedAscBids(auction.getId());

        assertFalse(bids.isEmpty(), "The bid list should not be null");

        assertEquals(5, bids.size(), "There should be exactly 5 bids for the auction");

        for (int i = 0; i < bids.size() - 1; i++) {
            assertTrue(bids.get(i).getDate().isBefore(bids.get(i + 1).getDate()), "Bids should be ordered in ascending order by date");
        }
    }

}
