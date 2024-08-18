package business.services;

import auction.business.services.AuctionServices;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.models.Bid;
import auction.data.models.User;
import auction.data.persistence.AuctionPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceUnitTest {

    @Mock
    private AuctionPersistence auctionPersistence;

    @InjectMocks
    private AuctionServices auctionServices;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Test
    public void shouldCreateNewAuction() {
        Auction auction = auctionServices.create("PS5", 1000);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);
        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);
        assertEquals(1, auctionPersistence.getAuctionsList().size());
        assertEquals(auction.getCode(), auctionPersistence.getAuctionsList().get(0).getCode());
    }

    @Test
    public void shouldFindAuctionByCode() {
        List<Auction> listAuction = new ArrayList<>();
        Auction auction = auctionServices.create("PS5", 1000);
        Auction secondAuction = auctionServices.create("Carro", 2000);
        listAuction.add(auction);
        listAuction.add(secondAuction);
        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);
        Optional<Auction> auctionFound = auctionServices.findByCode(auction.getCode());

        assertTrue(auctionFound.isPresent());
        assertEquals(auction.getCode(), auctionFound.get().getCode());
    }
    @Test
    public void shouldOpenAuction() {
        List<Auction> listAuction = new ArrayList<>();
        Auction auction = auctionServices.create("PS5", 1000);
        Auction secondAuction = auctionServices.create("Carro", 2000);
        listAuction.add(auction);
        listAuction.add(secondAuction);
        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);
        Optional<Auction> auctionFound = auctionServices.findByCode(auction.getCode());

        assertTrue(auctionFound.isPresent());
        assertEquals(auction.getCode(), auctionFound.get().getCode());
    }


    @Test
    public void shouldFilterAuctionsByAllStates() {

        Auction openAuction = new Auction("OPEN123", "PS5", AuctionState.OPEN, 1000, LocalDateTime.now().plusHours(2));
        Auction inactiveAuction = new Auction("INACTIVE123", "Xbox", AuctionState.INACTIVE, 800, LocalDateTime.now().plusHours(3));
        Auction expiredAuction = new Auction("EXPIRED123", "Switch", AuctionState.EXPIRED, 700, LocalDateTime.now().minusHours(1));
        Auction endedAuction = new Auction("ENDED123", "Car", AuctionState.ENDED, 20000, LocalDateTime.now().plusDays(1));

        List<Auction> auctionList = new ArrayList<>();
        auctionList.add(openAuction);
        auctionList.add(inactiveAuction);
        auctionList.add(expiredAuction);
        auctionList.add(endedAuction);

        when(auctionPersistence.getAuctionsList()).thenReturn(auctionList);

        List<Auction> filteredOpenAuctions = auctionServices.filterByState(AuctionState.OPEN);
        List<Auction> filteredInactiveAuctions = auctionServices.filterByState(AuctionState.INACTIVE);
        List<Auction> filteredExpiredAuctions = auctionServices.filterByState(AuctionState.EXPIRED);
        List<Auction> filteredEndedAuctions = auctionServices.filterByState(AuctionState.ENDED);

        assertEquals(1, filteredOpenAuctions.size());
        assertEquals(openAuction, filteredOpenAuctions.get(0));

        assertEquals(1, filteredInactiveAuctions.size());
        assertEquals(inactiveAuction, filteredInactiveAuctions.get(0));

        assertEquals(1, filteredExpiredAuctions.size());
        assertEquals(expiredAuction, filteredExpiredAuctions.get(0));

        assertEquals(1, filteredEndedAuctions.size());
        assertEquals(endedAuction, filteredEndedAuctions.get(0));
    }

    @Test
    public void shouldCreateAuctionAsInactive() {
        Auction auction = auctionServices.create("PS5", 1000);
        assertEquals(AuctionState.INACTIVE, auction.getState(), "Auction should be INACTIVE upon creation");
    }
    @Test
    public void shouldAddValidBid() {
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        auctionServices.placeBid(auction.getCode(), 2000.0, user);
        Bid bid = auction.getBids().get(0);
        assertEquals(bid.getValue(), 2000.0);
        assertEquals(bid.getUser(), user);
    }

    @Test
    public void shouldAddManyValidBidsSameUser() {
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        auctionServices.placeBid(auction.getCode(), 2000.0, user);
        Bid bid = auction.getBids().get(0);
        assertEquals(bid.getValue(), 2000.0);
        assertEquals(bid.getUser(), user);

        User userTwo = new User("outro2", LocalDateTime.now(), "outro2@aristeu", "pass");
        auctionServices.placeBid(auction.getCode(), 2001.0, userTwo);
        Bid bidTwo = auction.getBids().get(1);
        assertEquals(bidTwo.getValue(), 2001.0);
        assertEquals(bidTwo.getUser(), userTwo);

        auctionServices.placeBid(auction.getCode(), 2003.0, user);
        Bid bidThree = auction.getBids().get(2);
        assertEquals(bidThree.getValue(), 2003.0);
        assertEquals(bidThree.getUser(), user);
    }
    @Test
    public void shouldThrowExceptionWhenTryingToGiveBidForNonExistentAuction() {
        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid("XYZ789", 2000.0, user));
        assertEquals(exception.getMessage(), "Auction not found");
    }

    @Test
    public void shouldThrowExceptionWhenTryingToGiveBidForAuctionEnded() {
        LocalDateTime expiredDate = LocalDateTime.now().minusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.ENDED, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid(auction.getCode(), 2000.0, user));
        assertEquals(exception.getMessage(), "Auction must be OPEN");
    }
    @Test
    public void shouldThrowExceptionWhenTryingToGiveBidForAuctionExpired() {
        LocalDateTime expiredDate = LocalDateTime.now().minusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.EXPIRED, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid(auction.getCode(), 2000.0, user));
        assertEquals(exception.getMessage(), "Auction must be OPEN");
    }
    @Test
    public void shouldThrowExceptionWhenTryingToGiveBidForAuctionInactive() {
        LocalDateTime expiredDate = LocalDateTime.now().minusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.INACTIVE, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid(auction.getCode(), 2000.0, user));
        assertEquals(exception.getMessage(), "Auction must be OPEN");
    }
    @Test
    public void shouldThrowExceptionWhenBidValueIsLessThanOrEqualToInitialValue() {
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User user = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid(auction.getCode(), 10.0, user));
        assertEquals(exception.getMessage(), "Value must be greater than the initial bid");
    }

    @Test
    public void shouldThrowExceptionWhenBidValueIsLessThanOrEqualToLastBid() {
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        User user = new User("Aristeu2", LocalDateTime.now(), "aristeu2@aristeu", "pass");

        Bid lastBid = new Bid(user, LocalDateTime.now(), 1500.0);
        auction.getBids().add(lastBid);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        User userTwo = new User("Aristeu", LocalDateTime.now(), "aristeu@aristeu", "pass");
        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid(auction.getCode(), 1200.0, userTwo));
        assertEquals(exception.getMessage(), "Value must be greater than the last bid");
    }

    @Test
    public void shouldThrowExceptionWhenUserIsSameAsLastBidder() {
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        User user = new User("Aristeu2", LocalDateTime.now(), "aristeu2@aristeu", "pass");

        Bid lastBid = new Bid(user, LocalDateTime.now(), 1500.0);
        auction.getBids().add(lastBid);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        Exception exception = assertThrows(RuntimeException.class, () -> auctionServices.placeBid(auction.getCode(), 1600.0, user));
        assertEquals(exception.getMessage(), "User must not be the same as the last user");
    }
    @Test
    public void shouldExpireAuctionBasedOnEndDate() {
        LocalDateTime expiredDate = LocalDateTime.now().minusDays(1);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        auction.checkExpiration();
        assertEquals(AuctionState.EXPIRED, auction.getState(), "Auction should be marked as EXPIRED after the end date has passed");
    }

    @Test
    public void shouldFinishExpiredAuction() {
        LocalDateTime expiredDate = LocalDateTime.now().minusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.EXPIRED, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);
        boolean finished = auctionServices.finish(auction.getCode());

        assertTrue(finished, "Auction should be finished successfully");
        assertEquals(AuctionState.ENDED, auction.getState(), "Auction should be marked as ENDED after being finished");
    }

    @Test
    public void shouldPrintMessageWhenAuctionIsFinished(){
        System.setOut(new PrintStream(outContent));

        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        User winner = new User("Aristeu2", LocalDateTime.now(), "aristeu2@aristeu", "pass");
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);
        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        auctionServices.placeBid(auction.getCode(), 20000.0,winner);

        auctionServices.finish(auction.getCode());

        System.setOut(originalOut);

        String expectedOutput = "Sending congratulations message to: aristeu2@aristeu";
        assertEquals(expectedOutput, outContent.toString().trim(), "Console output should match the expected message.");
    }


    @Test
    public void shouldFinishOpenAuction() {
        LocalDateTime expiredDate = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("ABC123", "PS5", AuctionState.OPEN, 1000, expiredDate);
        List<Auction> listAuction = new ArrayList<>();
        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);
        boolean finished = auctionServices.finish(auction.getCode());

        assertTrue(finished, "Auction should be finished successfully");
        assertEquals(AuctionState.ENDED, auction.getState(), "Auction should be marked as ENDED after being finished");
    }

    @Test
    public void shouldConsiderHighestBidderAsAuctionWinner() {
        User user1 = new User("UserOne", LocalDateTime.now(), "userone@example.com", "password");
        User user2 = new User("UserTwo", LocalDateTime.now(), "usertwo@example.com", "password");
        User user3 = new User("UserThree", LocalDateTime.now(), "userthree@example.com", "password");
        List<Auction> listAuction = new ArrayList<>();
        Auction auction = new Auction("Leilao001", "Item de Teste", AuctionState.OPEN, 100.0, LocalDateTime.now().plusHours(2));

        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        auctionServices.placeBid(auction.getCode(), 500.0,user1);
        auctionServices.placeBid(auction.getCode(), 600.0, user2);
        auctionServices.placeBid(auction.getCode(), 650.0,user1);
        auctionServices.placeBid(auction.getCode(), 750.0,user3);

        auctionServices.finish(auction.getCode());

        User winner = auction.getWinner();
        assertNotNull(winner);
    }

    @Test
    public void shouldListBidsInDescendingOrderForAuction() {
        User user1 = new User("UserOne", LocalDateTime.now(), "userone@example.com", "password");
        User user2 = new User("UserTwo", LocalDateTime.now(), "usertwo@example.com", "password");
        List<Auction> listAuction = new ArrayList<>();
        Auction auction = new Auction("Leilao001", "Item de Teste", AuctionState.OPEN, 100.0, LocalDateTime.now().plusHours(2));

        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        auctionServices.placeBid(auction.getCode(), 500.0,user1);
        auctionServices.placeBid(auction.getCode(), 600.0, user2);

        auctionServices.finish(auction.getCode());

        assertEquals(auctionServices.getOrderedBids(auction).get(0).getValue(), 600.0);
        assertEquals(auctionServices.getOrderedBids(auction).get(1).getValue(), 500.0);

    }

    @Test
    public void shouldListHighestAndLowestBidForAuction() {
        User user1 = new User("UserOne", LocalDateTime.now(), "userone@example.com", "password");
        User user2 = new User("UserTwo", LocalDateTime.now(), "usertwo@example.com", "password");
        List<Auction> listAuction = new ArrayList<>();
        Auction auction = new Auction("Leilao001", "Item de Teste", AuctionState.OPEN, 100.0, LocalDateTime.now().plusHours(2));

        listAuction.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(listAuction);

        auctionServices.placeBid(auction.getCode(), 500.0,user1);
        auctionServices.placeBid(auction.getCode(), 600.0, user2);
        List<Bid> bids = auctionServices.getHighestAndLowestBid(auction);

        assertEquals(bids.get(0).getValue(), 600.0);
        assertEquals(bids.get(1).getValue(), 500.0);

    }

}
