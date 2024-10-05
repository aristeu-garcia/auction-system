package business.services;

import auction.business.services.AuctionServices;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.data.models.Bid;
import auction.data.models.User;
import auction.data.persistence.AuctionPersistence;
import auction.data.persistence.UserPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Mock
    private UserPersistence userPersistence;

    @InjectMocks
    private AuctionServices auctionServices;

    private Auction auction;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("User1", LocalDateTime.now(), "user1@test.com", "password");
        auction = new Auction("ABC123", "PS5", AuctionState.INACTIVE, 1000, LocalDateTime.now().plusHours(4));
    }

    @Test
    public void shouldCreateNewAuction() {
        doNothing().when(auctionPersistence).create(any(Auction.class));


        Auction createdAuction = auctionServices.create("PS5", 1000);

        assertNotNull(createdAuction);
        assertEquals("PS5", createdAuction.getName());
        assertEquals(AuctionState.INACTIVE, createdAuction.getState());
        verify(auctionPersistence, times(1)).create(any(Auction.class));
    }

    @Test
    public void shouldOpenAuction() {
        auctionServices.open(auction.getCode());
        verify(auctionPersistence, times(1)).updateStatus(auction.getCode(), AuctionState.OPEN);
    }

    @Test
    public void shouldFinishAuctionWithWinningBid() {
        Bid winningBid = new Bid(user.getId(), LocalDateTime.now(), 2000.0);
        auction.getBids().add(winningBid);

        when(auctionPersistence.findByCode(auction.getCode())).thenReturn(Optional.of(auction));
        when(userPersistence.findById(winningBid.getUserId())).thenReturn(Optional.of(user));

        boolean finished = auctionServices.finish(auction.getCode());

        assertTrue(finished);
        assertEquals(AuctionState.ENDED, auction.getState());
        assertEquals(user, auction.getWinner());
        verify(auctionPersistence, times(1)).findByCode(auction.getCode());
        verify(userPersistence, times(1)).findById(winningBid.getUserId());
    }

    @Test
    public void shouldFinishAuctionWithoutWinningBid() {
        when(auctionPersistence.findByCode(auction.getCode())).thenReturn(Optional.of(auction));

        boolean finished = auctionServices.finish(auction.getCode());

        assertTrue(finished);
        assertNull(auction.getWinner());
        assertEquals(AuctionState.ENDED, auction.getState());
        verify(auctionPersistence, times(1)).findByCode(auction.getCode());
    }

    @Test
    public void shouldThrowExceptionWhenAuctionNotFoundToFinish() {
        when(auctionPersistence.findByCode("XYZ789")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> auctionServices.finish("XYZ789"));
        assertEquals("Auction not found to finish", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenWinnerNotFound() {
        Bid winningBid = new Bid(user.getId(), LocalDateTime.now(), 2000.0);
        auction.getBids().add(winningBid);

        when(auctionPersistence.findByCode(auction.getCode())).thenReturn(Optional.of(auction));
        when(userPersistence.findById(winningBid.getUserId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> auctionServices.finish(auction.getCode()));
        assertEquals("Winner not found", exception.getMessage());
    }

    @Test
    public void shouldFilterAuctionsByState() {
        Auction openAuction = new Auction("OPEN123", "PS5", AuctionState.OPEN, 1000, LocalDateTime.now().plusHours(2));
        Auction inactiveAuction = new Auction("INACTIVE123", "Xbox", AuctionState.INACTIVE, 800, LocalDateTime.now().plusHours(3));

        List<Auction> auctionList = new ArrayList<>();
        auctionList.add(openAuction);
        auctionList.add(inactiveAuction);

        when(auctionPersistence.getAuctionsList()).thenReturn(auctionList);

        List<Auction> filteredAuctions = auctionServices.filterByState(AuctionState.OPEN);
        assertEquals(1, filteredAuctions.size());
        assertEquals(openAuction, filteredAuctions.get(0));
    }

    @Test
    public void shouldReturnAllAuctions() {
        List<Auction> auctionList = new ArrayList<>();
        auctionList.add(auction);

        when(auctionPersistence.getAuctionsList()).thenReturn(auctionList);

        List<Auction> allAuctions = auctionServices.getAll();
        assertEquals(1, allAuctions.size());
        assertEquals(auction, allAuctions.get(0));
    }

    @Test
    public void shouldReturnEmptyWhenNoAuctionsExist() {
        when(auctionPersistence.getAuctionsList()).thenReturn(new ArrayList<>());

        List<Auction> allAuctions = auctionServices.getAll();
        assertTrue(allAuctions.isEmpty());
    }

    @Test
    public void shouldFindAuctionByCode() {
        when(auctionPersistence.findByCode(auction.getCode())).thenReturn(Optional.of(auction));

        Optional<Auction> foundAuction = auctionServices.findByCode(auction.getCode());

        assertTrue(foundAuction.isPresent());
        assertEquals(auction, foundAuction.get());
    }

    @Test
    public void shouldReturnEmptyWhenAuctionNotFoundByCode() {
        when(auctionPersistence.findByCode("XYZ789")).thenReturn(Optional.empty());

        Optional<Auction> foundAuction = auctionServices.findByCode("XYZ789");

        assertTrue(foundAuction.isEmpty());
    }
}
