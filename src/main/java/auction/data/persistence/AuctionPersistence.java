package auction.data.persistence;

import auction.config.DatabaseConnection;
import auction.data.models.Auction;
import auction.data.models.AuctionState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AuctionPersistence {
    private List<Auction> auctionsList;

    public AuctionPersistence() {
        this.auctionsList = new ArrayList<>();
    }

    public void create(Auction auction) {
        String sql = "INSERT INTO auctions (code, name, state, init_value, final_value, end_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, auction.getCode());
            pstmt.setString(2, auction.getName());
            pstmt.setString(3, auction.getState().name());
            pstmt.setDouble(4, auction.getInitValue());
            pstmt.setDouble(5, auction.getFinalValue());
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(auction.getEndDate()));

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


    public void setAuctionsList(List<Auction> auctionsList) {
        this.auctionsList = auctionsList;
    }

    @Override
    public String toString() {
        return "AuctionPersistence{" +
                "auctionsList=" + auctionsList +
                '}';
    }


}
