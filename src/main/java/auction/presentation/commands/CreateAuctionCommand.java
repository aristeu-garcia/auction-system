package auction.presentation.commands;

import auction.business.services.AuctionServices;
import auction.data.models.Auction;
import auction.data.models.AuctionState;
import auction.presentation.interfaces.ICommand;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;

public class CreateAuctionCommand implements ICommand {
    private final AuctionServices auctionServices;

    public CreateAuctionCommand(AuctionServices auctionServices) {
        this.auctionServices = auctionServices;
    }
    @Override
    public void execute() {
        createAuction();
    }

    private void createAuction() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do leilão:");
        String name = scanner.nextLine();

        double initialPrice = 0.0;
        boolean validPrice = false;
        while (!validPrice) {
            System.out.println("Digite o preço inicial do leilão:");
            try {
                initialPrice = Double.parseDouble(scanner.nextLine());
                if (initialPrice <= 0.0) {
                    System.out.println("O preço inicial deve ser maior do que zero.");
                } else {
                    validPrice = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, insira um valor numérico válido para o preço inicial.");
            }
        }

        this.auctionServices.create(name, initialPrice);

        System.out.println("Leilão criado com sucesso!");
    }

}
