package auction.presentation.commands;

import auction.business.services.AuctionServices;
import auction.data.models.AuctionState;
import auction.data.persistence.AuctionPersistence;
import auction.presentation.commands.invoker.CommandInvoker;
import auction.presentation.interfaces.ICommand;


public class Main {
        public static void main(String[] args) {
            AuctionPersistence auctionPersistence = new AuctionPersistence();
            AuctionServices auctionServices = new AuctionServices(auctionPersistence);
            CommandInvoker invoker = new CommandInvoker();

            executeCommand(invoker, new CreateAuctionCommand(auctionServices));
            executeCommand(invoker, new FilterAuctionsCommand(auctionServices, AuctionState.INACTIVE));
            executeCommand(invoker, new OpenAuctionCommand(auctionServices));
            executeCommand(invoker, new FilterAuctionsCommand(auctionServices, AuctionState.OPEN));
        }

        private static void executeCommand(CommandInvoker invoker, ICommand command) {
            invoker.setCommand(command);
            invoker.invoke();
        }

    }