package auction.data.persistence;

import auction.config.DatabaseConnection;
import auction.data.models.Auction;
import auction.data.models.AuctionState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AuctionPersistence {
    private List<Auction> auctionsList;

    public AuctionPersistence() {
    }

    public void create(Auction auction) {
        String sql = "INSERT INTO auctions (code, name, state, init_value, final_value, end_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, auction.getCode());
            pstmt.setString(2, auction.getName());
            pstmt.setString(3, auction.getState().name());
            pstmt.setDouble(4, auction.getInitValue());
            pstmt.setDouble(5, auction.getFinalValue());
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(auction.getEndDate()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating auction failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    auction.setId((int) generatedId);
                } else {
                    throw new SQLException("Creating auction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create auction: " + e.getMessage(), e);
        }
    }


    public void updateStatus(String code, AuctionState auctionState) {
        String sql = "UPDATE auctions SET state = ? WHERE code = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, auctionState.toString());
            pstmt.setString(2, code);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create auction: " + e.getMessage(), e);
        }
    }


    public List<Auction> getAuctionsList() {
        List<Auction> auctionsList = new ArrayList<>();
        String sql = "SELECT id ,code, name, state, init_value, final_value, end_date FROM auctions";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Auction auction = new Auction(
                        rs.getString("code"),
                        rs.getString("name"),
                        AuctionState.valueOf(rs.getString("state")),
                        rs.getDouble("init_value"),
                        rs.getTimestamp("end_date").toLocalDateTime()
                );
                auction.setId(rs.getInt("id"));
                auction.setFinalValue(rs.getDouble("final_value"));
                auctionsList.add(auction);
            }
        } catch (SQLException e) {
            throw new RuntimeException("List error " + e.getMessage());
        }
        return auctionsList;
    }

    public Optional<Auction> findByCode(String code) {
        String sql = "SELECT id, code, name, state, init_value, final_value, end_date FROM auctions WHERE auctions.code = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, code);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    Auction auction = new Auction(
                            rs.getString("code"),
                            rs.getString("name"),
                            AuctionState.valueOf(rs.getString("state")),
                            rs.getDouble("init_value"),
                            rs.getTimestamp("end_date").toLocalDateTime()
                    );
                    auction.setFinalValue(rs.getDouble("final_value"));
                    auction.setId(rs.getInt("id"));
                    return Optional.of(auction);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find by code error: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "AuctionPersistence{" +
                "auctionsList=" + auctionsList +
                '}';
    }

}
