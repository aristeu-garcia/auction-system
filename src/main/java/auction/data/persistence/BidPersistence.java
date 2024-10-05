package auction.data.persistence;

import auction.config.DatabaseConnection;
import auction.data.models.Bid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidPersistence {

    public void create(Bid bid) {
        String sql = "INSERT INTO bids (user_id, auction_id, date, value) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, bid.getUserId());
            pstmt.setInt(2, bid.getAuctionId());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(bid.getDate()));
            pstmt.setDouble(4, bid.getValue());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create bid: " + e.getMessage(), e);
        }
    }
    public List<Bid> getBidsByAuctionId(int auctionId) {
        return getBidsByAuctionId(auctionId, "DESC");
    }

    public List<Bid> getBidsByAuctionId(int auctionId, String sortOrder) {
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc"))) {
            sortOrder = "DESC";
        }

        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT id, user_id, auction_id, date, value FROM bids WHERE auction_id = ? ORDER BY date " + sortOrder;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, auctionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid();
                    bid.setId(rs.getInt("id"));
                    bid.setUserId(rs.getInt("user_id"));
                    bid.setAuctionId(rs.getInt("auction_id"));
                    bid.setDate(rs.getTimestamp("date").toLocalDateTime());
                    bid.setValue(rs.getDouble("value"));
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving bids for auction ID " + auctionId + ": " + e.getMessage(), e);
        }

        return bids;
    }

    public List<Bid> getBids() {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT id, user_id, auction_id, date, value FROM bids";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid();
                    bid.setId(rs.getInt("id"));
                    bid.setUserId(rs.getInt("user_id"));
                    bid.setAuctionId(rs.getInt("auction_id"));
                    bid.setDate(rs.getTimestamp("date").toLocalDateTime());
                    bid.setValue(rs.getDouble("value"));
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving bids " + e.getMessage());
        }

        return bids;
    }
}
